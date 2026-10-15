package com.aurel.notes.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.data.repository.NotebookRepositoryImpl
import com.aurel.notes.data.repository.NoteRepositoryImpl
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NotesTab {
    ALL, PINNED, VAULT, ARCHIVED, TRASH
}

enum class NotesSortOrder {
    UPDATED_DESC, CREATED_DESC, TITLE_ASC
}

data class NotesUiState(
    val currentTab: NotesTab = NotesTab.ALL,
    val searchQuery: String = "",
    val sortOrder: NotesSortOrder = NotesSortOrder.UPDATED_DESC,
    val selectedNotebookId: Long? = null,
    val selectedTag: String? = null,
    val isGridLayout: Boolean = true,
    val isSelectionMode: Boolean = false,
    val selectedNoteIds: Set<Long> = emptySet(),
    val notes: List<Note> = emptyList(),
    val notebooks: List<Notebook> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val isLoading: Boolean = false
)

private data class FilterParams(
    val tab: NotesTab,
    val query: String,
    val sort: NotesSortOrder,
    val notebookId: Long?,
    val tag: String?,
    val isGrid: Boolean,
    val selectedIds: Set<Long>
)

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val noteRepo = NoteRepositoryImpl(app.database.noteDao())
    private val notebookRepo = NotebookRepositoryImpl(app.database.notebookDao())

    private val currentTabFlow = MutableStateFlow(NotesTab.ALL)
    private val searchQueryFlow = MutableStateFlow("")
    private val sortOrderFlow = MutableStateFlow(NotesSortOrder.UPDATED_DESC)
    private val selectedNotebookIdFlow = MutableStateFlow<Long?>(null)
    private val selectedTagFlow = MutableStateFlow<String?>(null)
    private val isGridLayoutFlow = MutableStateFlow(true)
    private val selectedNoteIdsFlow = MutableStateFlow<Set<Long>>(emptySet())

    private val filterParamsFlow = combine(
        combine(currentTabFlow, searchQueryFlow, sortOrderFlow) { tab, q, sort ->
            Triple(tab, q, sort)
        },
        combine(selectedNotebookIdFlow, selectedTagFlow, isGridLayoutFlow) { nb, tag, grid ->
            Triple(nb, tag, grid)
        },
        selectedNoteIdsFlow
    ) { (tab, query, sort), (notebookId, tag, isGrid), selectedIds ->
        FilterParams(
            tab = tab,
            query = query,
            sort = sort,
            notebookId = notebookId,
            tag = tag,
            isGrid = isGrid,
            selectedIds = selectedIds
        )
    }

    val uiState: StateFlow<NotesUiState> = filterParamsFlow.flatMapLatest { params ->
        val baseNotesFlow = when {
            params.query.isNotBlank() -> noteRepo.searchNotes(params.query)
            params.tab == NotesTab.ARCHIVED -> noteRepo.getArchivedNotes()
            params.tab == NotesTab.TRASH -> noteRepo.getTrashedNotes()
            params.tab == NotesTab.VAULT -> noteRepo.getVaultNotes()
            params.notebookId != null -> noteRepo.getNotesByNotebook(params.notebookId)
            else -> noteRepo.getActiveNonVaultNotes()
        }

        combine(
            baseNotesFlow,
            notebookRepo.getAllNotebooks()
        ) { notes, notebooks ->
            // Filter by tab if in ALL/PINNED
            var filteredNotes = when (params.tab) {
                NotesTab.PINNED -> notes.filter { it.isPinned }
                else -> notes
            }

            // Filter by Tag
            if (params.tag != null) {
                filteredNotes = filteredNotes.filter { it.tags.contains(params.tag) }
            }

            // Sort
            val sortedNotes = when (params.sort) {
                NotesSortOrder.UPDATED_DESC -> filteredNotes.sortedByDescending { it.updatedAt }
                NotesSortOrder.CREATED_DESC -> filteredNotes.sortedByDescending { it.createdAt }
                NotesSortOrder.TITLE_ASC -> filteredNotes.sortedBy { it.title.lowercase() }
            }

            // Extract all unique tags
            val allTags = notes.flatMap { it.tags }.distinct().sorted()

            NotesUiState(
                currentTab = params.tab,
                searchQuery = params.query,
                sortOrder = params.sort,
                selectedNotebookId = params.notebookId,
                selectedTag = params.tag,
                isGridLayout = params.isGrid,
                isSelectionMode = params.selectedIds.isNotEmpty(),
                selectedNoteIds = params.selectedIds,
                notes = sortedNotes,
                notebooks = notebooks,
                availableTags = allTags,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState(isLoading = true)
    )

    fun setTab(tab: NotesTab) {
        currentTabFlow.value = tab
        selectedNoteIdsFlow.value = emptySet()
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun setSortOrder(order: NotesSortOrder) {
        sortOrderFlow.value = order
    }

    fun selectNotebook(notebookId: Long?) {
        selectedNotebookIdFlow.value = notebookId
    }

    fun selectTag(tag: String?) {
        selectedTagFlow.value = if (selectedTagFlow.value == tag) null else tag
    }

    fun toggleLayout() {
        isGridLayoutFlow.value = !isGridLayoutFlow.value
    }

    fun toggleNoteSelection(noteId: Long) {
        val current = selectedNoteIdsFlow.value.toMutableSet()
        if (current.contains(noteId)) {
            current.remove(noteId)
        } else {
            current.add(noteId)
        }
        selectedNoteIdsFlow.value = current
    }

    fun clearSelection() {
        selectedNoteIdsFlow.value = emptySet()
    }

    fun batchMoveToTrash() {
        val ids = selectedNoteIdsFlow.value.toList()
        viewModelScope.launch {
            noteRepo.moveMultipleToTrash(ids)
            clearSelection()
        }
    }

    fun batchRestore() {
        val ids = selectedNoteIdsFlow.value.toList()
        viewModelScope.launch {
            for (id in ids) {
                noteRepo.restoreFromTrash(id)
            }
            clearSelection()
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            noteRepo.emptyTrash()
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            noteRepo.setPinned(note.id, !note.isPinned)
        }
    }

    fun toggleArchive(note: Note) {
        viewModelScope.launch {
            noteRepo.setArchived(note.id, !note.isArchived)
        }
    }

    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            noteRepo.moveToTrash(noteId)
        }
    }

    fun restoreFromTrash(noteId: Long) {
        viewModelScope.launch {
            noteRepo.restoreFromTrash(noteId)
        }
    }

    fun deletePermanently(noteId: Long) {
        viewModelScope.launch {
            noteRepo.deletePermanently(noteId)
        }
    }
}
