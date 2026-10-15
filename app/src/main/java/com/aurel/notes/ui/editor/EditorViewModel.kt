package com.aurel.notes.ui.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.database.entity.AttachmentEntity
import com.aurel.notes.core.database.entity.NoteCrossRefEntity
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.core.markdown.FormattingType
import com.aurel.notes.core.markdown.MarkdownParser
import com.aurel.notes.data.mapper.toDomain
import com.aurel.notes.data.repository.NotebookRepositoryImpl
import com.aurel.notes.data.repository.NoteRepositoryImpl
import com.aurel.notes.domain.model.Attachment
import com.aurel.notes.domain.model.AttachmentType
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.NoteTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.ArrayDeque

enum class SaveStatus {
    SAVED, SAVING, IDLE
}

data class EditorTextSnapshot(
    val title: String,
    val content: String
)

data class EditorUiState(
    val noteId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val cardColorHex: String = CardColorTokens.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isVaultLocked: Boolean = false,
    val selectedNotebookId: Long? = null,
    val tags: List<String> = emptyList(),
    val notebooks: List<Notebook> = emptyList(),
    val backlinks: List<Note> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val saveStatus: SaveStatus = SaveStatus.IDLE,
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isReadingMode: Boolean = false,
    val isFocusMode: Boolean = false,
    val focusTimerSeconds: Int = 25 * 60,
    val isTimerRunning: Boolean = false
)

class EditorViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val noteRepo = NoteRepositoryImpl(app.database.noteDao())
    private val notebookRepo = NotebookRepositoryImpl(app.database.notebookDao())
    private val crossRefDao = app.database.crossRefDao()
    private val attachmentDao = app.database.attachmentDao()

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var activeNoteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L
    private var autosaveJob: Job? = null
    private var timerJob: Job? = null

    // Undo / Redo history stacks
    private val undoStack = ArrayDeque<EditorTextSnapshot>()
    private val redoStack = ArrayDeque<EditorTextSnapshot>()

    init {
        loadNotebooks()
        if (activeNoteId > 0) {
            loadExistingNote(activeNoteId)
            observeBacklinks(activeNoteId)
            observeAttachments(activeNoteId)
        }
    }

    private fun loadNotebooks() {
        viewModelScope.launch {
            notebookRepo.getAllNotebooks().collect { list ->
                _uiState.value = _uiState.value.copy(notebooks = list)
            }
        }
    }

    private fun observeBacklinks(id: Long) {
        viewModelScope.launch {
            crossRefDao.getBacklinksForNote(id).collect { noteEntities ->
                _uiState.value = _uiState.value.copy(backlinks = noteEntities.map { it.toDomain() })
            }
        }
    }

    private fun observeAttachments(id: Long) {
        viewModelScope.launch {
            attachmentDao.getAttachmentsForNote(id).collect { entities ->
                _uiState.value = _uiState.value.copy(attachments = entities.map { it.toDomain() })
            }
        }
    }

    private fun loadExistingNote(id: Long) {
        viewModelScope.launch {
            val note = noteRepo.getNoteById(id)
            if (note != null) {
                _uiState.value = _uiState.value.copy(
                    noteId = note.id,
                    title = note.title,
                    content = note.content,
                    cardColorHex = note.cardColorHex,
                    isPinned = note.isPinned,
                    isArchived = note.isArchived,
                    isVaultLocked = note.isVaultLocked,
                    selectedNotebookId = note.notebookId,
                    tags = note.tags,
                    wordCount = calculateWordCount(note.content),
                    charCount = note.content.length,
                    saveStatus = SaveStatus.SAVED
                )
                undoStack.clear()
                redoStack.clear()
                pushUndoSnapshot(note.title, note.content)
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        val oldSnapshot = EditorTextSnapshot(_uiState.value.title, _uiState.value.content)
        pushUndoSnapshot(oldSnapshot.title, oldSnapshot.content)

        _uiState.value = _uiState.value.copy(
            title = newTitle,
            saveStatus = SaveStatus.SAVING
        )
        triggerAutosave()
    }

    fun onContentChange(newContent: String) {
        val oldSnapshot = EditorTextSnapshot(_uiState.value.title, _uiState.value.content)
        pushUndoSnapshot(oldSnapshot.title, oldSnapshot.content)

        _uiState.value = _uiState.value.copy(
            content = newContent,
            wordCount = calculateWordCount(newContent),
            charCount = newContent.length,
            saveStatus = SaveStatus.SAVING
        )
        triggerAutosave()
    }

    fun applyFormatting(type: FormattingType) {
        val (formatted, _) = MarkdownParser.applyFormatting(
            currentText = _uiState.value.content,
            selectionStart = _uiState.value.content.length,
            selectionEnd = _uiState.value.content.length,
            formatType = type
        )
        onContentChange(formatted)
    }

    fun applyTemplate(template: NoteTemplate) {
        val currentTitle = _uiState.value.title
        val newTitle = if (currentTitle.isBlank()) template.defaultTitle else currentTitle
        val currentContent = _uiState.value.content
        val newContent = if (currentContent.isBlank()) template.initialContent else "$currentContent\n\n${template.initialContent}"
        val mergedTags = (_uiState.value.tags + template.suggestedTags).distinct()

        _uiState.value = _uiState.value.copy(
            title = newTitle,
            content = newContent,
            tags = mergedTags,
            wordCount = calculateWordCount(newContent),
            charCount = newContent.length,
            saveStatus = SaveStatus.SAVING
        )
        triggerAutosave()
    }

    private fun pushUndoSnapshot(title: String, content: String) {
        if (undoStack.isEmpty() || undoStack.peek() != EditorTextSnapshot(title, content)) {
            undoStack.push(EditorTextSnapshot(title, content))
            if (undoStack.size > 50) undoStack.removeLast()
            redoStack.clear()
            _uiState.value = _uiState.value.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = false
            )
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val current = EditorTextSnapshot(_uiState.value.title, _uiState.value.content)
            redoStack.push(current)
            val previous = undoStack.pop()
            _uiState.value = _uiState.value.copy(
                title = previous.title,
                content = previous.content,
                wordCount = calculateWordCount(previous.content),
                charCount = previous.content.length,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true,
                saveStatus = SaveStatus.SAVING
            )
            triggerAutosave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val current = EditorTextSnapshot(_uiState.value.title, _uiState.value.content)
            undoStack.push(current)
            val next = redoStack.pop()
            _uiState.value = _uiState.value.copy(
                title = next.title,
                content = next.content,
                wordCount = calculateWordCount(next.content),
                charCount = next.content.length,
                canUndo = true,
                canRedo = redoStack.isNotEmpty(),
                saveStatus = SaveStatus.SAVING
            )
            triggerAutosave()
        }
    }

    fun setCardColor(hex: String) {
        _uiState.value = _uiState.value.copy(cardColorHex = hex, saveStatus = SaveStatus.SAVING)
        triggerAutosave()
    }

    fun togglePin() {
        _uiState.value = _uiState.value.copy(isPinned = !_uiState.value.isPinned, saveStatus = SaveStatus.SAVING)
        triggerAutosave()
    }

    fun toggleArchive() {
        _uiState.value = _uiState.value.copy(isArchived = !_uiState.value.isArchived, saveStatus = SaveStatus.SAVING)
        triggerAutosave()
    }

    fun toggleVaultLock() {
        _uiState.value = _uiState.value.copy(
            isVaultLocked = !_uiState.value.isVaultLocked,
            saveStatus = SaveStatus.SAVING
        )
        triggerAutosave()
    }

    fun getCurrentNoteEntity(): com.aurel.notes.core.database.entity.NoteEntity {
        val state = _uiState.value
        return com.aurel.notes.core.database.entity.NoteEntity(
            id = activeNoteId,
            title = state.title,
            content = state.content,
            plainTextSummary = extractPlainSummary(state.content),
            notebookId = state.selectedNotebookId,
            cardColorHex = state.cardColorHex,
            isPinned = state.isPinned,
            isArchived = state.isArchived,
            isVaultLocked = state.isVaultLocked,
            tags = state.tags,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun toggleReadingMode() {
        _uiState.value = _uiState.value.copy(isReadingMode = !_uiState.value.isReadingMode)
    }

    fun toggleFocusMode() {
        val newFocus = !_uiState.value.isFocusMode
        _uiState.value = _uiState.value.copy(isFocusMode = newFocus)
        if (newFocus && !_uiState.value.isTimerRunning) {
            startFocusTimer(25)
        }
    }

    fun startFocusTimer(minutes: Int) {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            focusTimerSeconds = minutes * 60,
            isTimerRunning = true
        )
        timerJob = viewModelScope.launch {
            while (_uiState.value.focusTimerSeconds > 0 && _uiState.value.isTimerRunning) {
                delay(1000)
                val remaining = _uiState.value.focusTimerSeconds - 1
                _uiState.value = _uiState.value.copy(
                    focusTimerSeconds = remaining,
                    isTimerRunning = remaining > 0
                )
            }
        }
    }

    fun pauseFocusTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    fun resetFocusTimer(minutes: Int = 25) {
        pauseFocusTimer()
        _uiState.value = _uiState.value.copy(focusTimerSeconds = minutes * 60)
    }

    fun setNotebook(notebookId: Long?) {
        _uiState.value = _uiState.value.copy(selectedNotebookId = notebookId, saveStatus = SaveStatus.SAVING)
        triggerAutosave()
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim().lowercase().removePrefix("#")
        if (trimmed.isNotBlank() && !_uiState.value.tags.contains(trimmed)) {
            val newTags = _uiState.value.tags + trimmed
            _uiState.value = _uiState.value.copy(tags = newTags, saveStatus = SaveStatus.SAVING)
            triggerAutosave()
        }
    }

    fun removeTag(tag: String) {
        val newTags = _uiState.value.tags.filter { it != tag }
        _uiState.value = _uiState.value.copy(tags = newTags, saveStatus = SaveStatus.SAVING)
        triggerAutosave()
    }

    fun addAttachment(
        type: AttachmentType,
        filePath: String,
        fileName: String,
        fileSizeBytes: Long = 0L,
        durationMs: Long? = null
    ) {
        viewModelScope.launch {
            // Ensure note is saved first
            if (activeNoteId == 0L) {
                saveToDatabase()
            }
            if (activeNoteId > 0L) {
                val entity = AttachmentEntity(
                    noteId = activeNoteId,
                    type = type.name,
                    filePath = filePath,
                    fileName = fileName,
                    fileSizeBytes = fileSizeBytes,
                    durationMs = durationMs
                )
                attachmentDao.insertAttachment(entity)
            }
        }
    }

    fun deleteAttachment(attachmentId: Long) {
        viewModelScope.launch {
            attachmentDao.deleteAttachmentById(attachmentId)
        }
    }

    private fun triggerAutosave() {
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(350) // 350ms debounce
            saveToDatabase()
        }
    }

    private suspend fun saveToDatabase() {
        val state = _uiState.value
        if (state.title.isBlank() && state.content.isBlank()) {
            _uiState.value = state.copy(saveStatus = SaveStatus.IDLE)
            return
        }

        val plainSummary = extractPlainSummary(state.content)

        val note = Note(
            id = activeNoteId,
            title = state.title,
            content = state.content,
            plainTextSummary = plainSummary,
            notebookId = state.selectedNotebookId,
            cardColorHex = state.cardColorHex,
            isPinned = state.isPinned,
            isArchived = state.isArchived,
            isVaultLocked = state.isVaultLocked,
            tags = state.tags,
            updatedAt = System.currentTimeMillis()
        )

        val newId = noteRepo.saveNote(note)
        if (activeNoteId == 0L) {
            activeNoteId = newId
            savedStateHandle["noteId"] = newId
            _uiState.value = _uiState.value.copy(noteId = newId)
            observeBacklinks(newId)
            observeAttachments(newId)
        }

        // Process [[Wiki Links]]
        val links = MarkdownParser.extractWikiLinks(state.content)
        val crossRefs = mutableListOf<NoteCrossRefEntity>()
        for (linkText in links) {
            val targetNote = noteRepo.getNoteByTitle(linkText)
            if (targetNote != null && targetNote.id != activeNoteId) {
                crossRefs.add(
                    NoteCrossRefEntity(
                        sourceNoteId = activeNoteId,
                        targetNoteId = targetNote.id,
                        linkText = linkText
                    )
                )
            }
        }
        crossRefDao.syncCrossRefsForSource(activeNoteId, crossRefs)

        _uiState.value = _uiState.value.copy(saveStatus = SaveStatus.SAVED)
    }

    fun deleteNote(onDeleted: () -> Unit) {
        viewModelScope.launch {
            if (activeNoteId > 0) {
                noteRepo.moveToTrash(activeNoteId)
            }
            onDeleted()
        }
    }

    private fun calculateWordCount(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split("\\s+".toRegex()).size
    }

    private fun extractPlainSummary(markdown: String): String {
        return markdown
            .replace("#+".toRegex(), "")
            .replace("\\*\\*".toRegex(), "")
            .replace("\\*".toRegex(), "")
            .replace("\\[\\[.*?\\]\\]".toRegex()) { it.value.removeSurrounding("[[", "]]") }
            .replace("- \\[ \\]".toRegex(), "")
            .replace("- \\[x\\]".toRegex(), "")
            .trim()
            .take(200)
    }
}
