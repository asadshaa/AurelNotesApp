package com.aurel.notes.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.datastore.AurelUserPreferences
import com.aurel.notes.data.repository.NotebookRepositoryImpl
import com.aurel.notes.data.repository.NoteRepositoryImpl
import com.aurel.notes.data.repository.TaskRepositoryImpl
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val greeting: String = "",
    val formattedDate: String = "",
    val dailyPrompt: String = "",
    val pinnedNotes: List<Note> = emptyList(),
    val recentNotes: List<Note> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val todayCompletedCount: Int = 0,
    val todayPendingCount: Int = 0,
    val notebooks: List<Notebook> = emptyList(),
    val streakDays: Int = 1,
    val isEmpty: Boolean = true,
    val userPrefs: AurelUserPreferences = AurelUserPreferences()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val noteRepo = NoteRepositoryImpl(app.database.noteDao())
    private val taskRepo = TaskRepositoryImpl(app.database.taskDao())
    private val notebookRepo = NotebookRepositoryImpl(app.database.notebookDao())
    private val prefsRepo = app.preferencesRepository

    private val dailyPrompts = listOf(
        "What is one idea that challenged your perspective today?",
        "What made you feel deeply curious recently?",
        "Capture the most valuable lesson you learned this week.",
        "What small victory deserves celebration right now?",
        "If you could simplify one thing today, what would it be?",
        "What are three things you are quietly grateful for today?",
        "What does true focus look like for you this afternoon?"
    )

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    private fun getDailyPrompt(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return dailyPrompts[dayOfYear % dailyPrompts.size]
    }

    val uiState: StateFlow<HomeUiState> = combine(
        noteRepo.getPinnedNotes(),
        noteRepo.getRecentNotes(10),
        taskRepo.getTodayTasks(),
        taskRepo.getTodayPendingTaskCount(),
        taskRepo.getTodayCompletedTaskCount(),
        notebookRepo.getAllNotebooks(),
        prefsRepo.userPreferencesFlow
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val pinned = args[0] as List<Note>
        @Suppress("UNCHECKED_CAST")
        val recent = args[1] as List<Note>
        @Suppress("UNCHECKED_CAST")
        val tasks = args[2] as List<Task>
        val pendingCount = args[3] as Int
        val completedCount = args[4] as Int
        @Suppress("UNCHECKED_CAST")
        val notebooks = args[5] as List<Notebook>
        val prefs = args[6] as AurelUserPreferences

        val dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
        val isEmpty = pinned.isEmpty() && recent.isEmpty() && tasks.isEmpty()

        HomeUiState(
            greeting = getGreeting(),
            formattedDate = dateStr,
            dailyPrompt = getDailyPrompt(),
            pinnedNotes = pinned,
            recentNotes = recent,
            todayTasks = tasks,
            todayCompletedCount = completedCount,
            todayPendingCount = pendingCount,
            notebooks = notebooks,
            streakDays = if (recent.isNotEmpty()) (recent.size % 7) + 1 else 1,
            isEmpty = isEmpty,
            userPrefs = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            greeting = getGreeting(),
            formattedDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()),
            dailyPrompt = getDailyPrompt()
        )
    )

    fun toggleTaskCompleted(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepo.setTaskCompleted(taskId, isCompleted)
        }
    }

    fun createSampleStarterNote() {
        viewModelScope.launch {
            val starterNote = Note(
                title = "Welcome to Aurel 🌿",
                content = """
                    # Welcome to Aurel
                    Aurel is your calm, local-first thinking space and personal knowledge base.

                    ### Core Powers:
                    - **Instant Quick Capture**: Capture fleeting thoughts before they fade.
                    - **Rich Typography**: Structured markdown with serene editorial spacing.
                    - **Knowledge Web**: Connect ideas naturally using `[[Wiki-style Links]]`.
                    - **Local Integrity**: 100% offline, private, and yours forever.

                    > "Simplicity is the prerequisite for reliability." — Edsger W. Dijkstra

                    Start by editing this note or tap **+** to begin a fresh sheet.
                """.trimIndent(),
                plainTextSummary = "Aurel is your calm, local-first thinking space and personal knowledge base.",
                cardColorHex = "#EAE5FF",
                isPinned = true,
                tags = listOf("welcome", "guide", "starter")
            )
            noteRepo.saveNote(starterNote)
        }
    }
}
