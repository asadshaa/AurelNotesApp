package com.aurel.notes.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.backup.ZipBackupManager
import com.aurel.notes.core.datastore.AurelUserPreferences
import com.aurel.notes.core.designsystem.EditorFontPreference
import com.aurel.notes.core.designsystem.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

data class DatabaseStats(
    val activeNotesCount: Int = 0,
    val notebooksCount: Int = 0,
    val pendingTasksCount: Int = 0
)

data class SettingsUiState(
    val preferences: AurelUserPreferences = AurelUserPreferences(),
    val stats: DatabaseStats = DatabaseStats(),
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val message: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val prefsRepo = app.preferencesRepository
    private val database = app.database
    private val backupManager = ZipBackupManager(app, database)

    private val messageFlow = MutableStateFlow<String?>(null)
    private val backupInProgressFlow = MutableStateFlow(false)
    private val restoreInProgressFlow = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        prefsRepo.userPreferencesFlow,
        combine(
            database.noteDao().getActiveNoteCount(),
            database.notebookDao().getAllNotebooks(),
            database.taskDao().getPendingTaskCount()
        ) { notes, notebooks, tasks ->
            DatabaseStats(
                activeNotesCount = notes,
                notebooksCount = notebooks.size,
                pendingTasksCount = tasks
            )
        },
        combine(messageFlow, backupInProgressFlow, restoreInProgressFlow) { msg, backingUp, restoring ->
            Triple(msg, backingUp, restoring)
        }
    ) { prefs, stats, (msg, backingUp, restoring) ->
        SettingsUiState(
            preferences = prefs,
            stats = stats,
            isBackingUp = backingUp,
            isRestoring = restoring,
            message = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefsRepo.setThemeMode(mode) }
    }

    fun setEditorFont(font: EditorFontPreference) {
        viewModelScope.launch { prefsRepo.setEditorFont(font) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { prefsRepo.setFontScale(scale) }
    }

    fun setLineHeightMultiplier(mult: Float) {
        viewModelScope.launch { prefsRepo.setLineHeightMultiplier(mult) }
    }

    fun setReducedMotion(reduced: Boolean) {
        viewModelScope.launch { prefsRepo.setReducedMotion(reduced) }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setAppLockEnabled(enabled) }
    }

    fun setPrivacyModeFlagSecure(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setPrivacyModeFlagSecure(enabled) }
    }

    fun toggleHomeSection(section: String, enabled: Boolean) {
        viewModelScope.launch { prefsRepo.toggleHomeSection(section, enabled) }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            database.noteDao().emptyTrash()
            messageFlow.value = "Trash emptied successfully."
        }
    }

    fun createBackup(outputStream: OutputStream, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            backupInProgressFlow.value = true
            try {
                val stats = backupManager.createBackup(outputStream)
                val msg = "Backup created: ${stats.notesCount} notes, ${stats.notebooksCount} notebooks, ${stats.tasksCount} tasks."
                messageFlow.value = msg
                onComplete(true, msg)
            } catch (e: Exception) {
                val err = "Backup failed: ${e.localizedMessage ?: e.message}"
                messageFlow.value = err
                onComplete(false, err)
            } finally {
                backupInProgressFlow.value = false
            }
        }
    }

    fun restoreBackup(
        inputStream: InputStream,
        conflictResolution: ZipBackupManager.ConflictResolution,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            restoreInProgressFlow.value = true
            try {
                val result = backupManager.restoreBackup(inputStream, conflictResolution)
                if (result.isSuccess) {
                    val msg = "Restored ${result.notesRestored} notes, ${result.notebooksRestored} notebooks, ${result.tasksRestored} tasks."
                    messageFlow.value = msg
                    onComplete(true, msg)
                } else {
                    val err = result.errorMessage ?: "Restore failed."
                    messageFlow.value = err
                    onComplete(false, err)
                }
            } catch (e: Exception) {
                val err = "Restore error: ${e.localizedMessage ?: e.message}"
                messageFlow.value = err
                onComplete(false, err)
            } finally {
                restoreInProgressFlow.value = false
            }
        }
    }

    fun dismissMessage() {
        messageFlow.value = null
    }
}
