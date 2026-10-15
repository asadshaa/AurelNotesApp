package com.aurel.notes.ui.journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.data.repository.NotebookRepositoryImpl
import com.aurel.notes.data.repository.NoteRepositoryImpl
import com.aurel.notes.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class JournalMood(val emoji: String, val label: String) {
    CALM("🌿", "Calm"),
    INSPIRED("✨", "Inspired"),
    ENERGIZED("⚡", "Energized"),
    REFLECTIVE("📖", "Reflective"),
    OVERWHELMED("🌧️", "Overwhelmed")
}

data class JournalUiState(
    val selectedMood: JournalMood = JournalMood.CALM,
    val gratitude1: String = "",
    val gratitude2: String = "",
    val gratitude3: String = "",
    val reflectionPrompt: String = "",
    val reflectionBody: String = "",
    val cardColorHex: String = CardColorTokens.SAND,
    val isSaved: Boolean = false,
    val createdNoteId: Long? = null
)

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val noteRepo = NoteRepositoryImpl(app.database.noteDao())
    private val notebookRepo = NotebookRepositoryImpl(app.database.notebookDao())

    private val promptBank = listOf(
        "What was the most meaningful conversation you had today?",
        "Where did you feel most in your element or at peace?",
        "What is something you handled better today than you would have a year ago?",
        "What idea or question lingered in your mind throughout the day?",
        "If you could speak to yourself this morning, what calm reassurance would you offer?"
    )

    private val _uiState = MutableStateFlow(
        JournalUiState(
            reflectionPrompt = promptBank.first()
        )
    )
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    fun selectMood(mood: JournalMood) {
        _uiState.value = _uiState.value.copy(selectedMood = mood)
    }

    fun setGratitude1(text: String) {
        _uiState.value = _uiState.value.copy(gratitude1 = text)
    }

    fun setGratitude2(text: String) {
        _uiState.value = _uiState.value.copy(gratitude2 = text)
    }

    fun setGratitude3(text: String) {
        _uiState.value = _uiState.value.copy(gratitude3 = text)
    }

    fun setReflectionBody(text: String) {
        _uiState.value = _uiState.value.copy(reflectionBody = text)
    }

    fun setCardColor(hex: String) {
        _uiState.value = _uiState.value.copy(cardColorHex = hex)
    }

    fun shufflePrompt() {
        val current = _uiState.value.reflectionPrompt
        val next = promptBank.filter { it != current }.random()
        _uiState.value = _uiState.value.copy(reflectionPrompt = next)
    }

    fun saveJournal(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
            val title = "Daily Reflection — $dateStr"

            val journalNotebook = app.database.notebookDao().getNotebookByName("Journal")

            val content = buildString {
                appendLine("# $title")
                appendLine()
                appendLine("**Mood:** ${state.selectedMood.emoji} ${state.selectedMood.label}")
                appendLine()
                appendLine("### 🙏 Moments of Gratitude")
                if (state.gratitude1.isNotBlank()) appendLine("1. ${state.gratitude1}")
                if (state.gratitude2.isNotBlank()) appendLine("2. ${state.gratitude2}")
                if (state.gratitude3.isNotBlank()) appendLine("3. ${state.gratitude3}")
                if (state.gratitude1.isBlank() && state.gratitude2.isBlank() && state.gratitude3.isBlank()) {
                    appendLine("*(Quiet reflection)*")
                }
                appendLine()
                appendLine("### 💭 ${state.reflectionPrompt}")
                if (state.reflectionBody.isNotBlank()) {
                    appendLine(state.reflectionBody)
                } else {
                    appendLine("*(No notes added)*")
                }
            }

            val note = Note(
                title = title,
                content = content,
                plainTextSummary = "Mood: ${state.selectedMood.label} · ${state.reflectionBody.take(120)}",
                notebookId = journalNotebook?.id,
                cardColorHex = state.cardColorHex,
                tags = listOf("journal", state.selectedMood.name.lowercase()),
                updatedAt = System.currentTimeMillis()
            )

            val id = noteRepo.saveNote(note)
            _uiState.value = _uiState.value.copy(isSaved = true, createdNoteId = id)
            onSaved(id)
        }
    }
}
