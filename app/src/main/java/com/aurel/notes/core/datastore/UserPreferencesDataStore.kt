package com.aurel.notes.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aurel.notes.core.designsystem.EditorFontPreference
import com.aurel.notes.core.designsystem.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aurel_user_prefs")

data class AurelUserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val editorFont: EditorFontPreference = EditorFontPreference.SERIF,
    val fontScale: Float = 1.0f,
    val lineHeightMultiplier: Float = 1.4f,
    val reducedMotion: Boolean = false,
    val appLockEnabled: Boolean = false,
    val privacyModeFlagSecure: Boolean = false,
    val showPinnedSection: Boolean = true,
    val showRecentSection: Boolean = true,
    val showTasksSection: Boolean = true,
    val showStreakSection: Boolean = true,
    val showNotebooksSection: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val EDITOR_FONT = stringPreferencesKey("editor_font")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val LINE_HEIGHT_MULTIPLIER = floatPreferencesKey("line_height_multiplier")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val PRIVACY_FLAG_SECURE = booleanPreferencesKey("privacy_flag_secure")
        val SHOW_PINNED = booleanPreferencesKey("show_pinned")
        val SHOW_RECENT = booleanPreferencesKey("show_recent")
        val SHOW_TASKS = booleanPreferencesKey("show_tasks")
        val SHOW_STREAK = booleanPreferencesKey("show_streak")
        val SHOW_NOTEBOOKS = booleanPreferencesKey("show_notebooks")
    }

    val userPreferencesFlow: Flow<AurelUserPreferences> = context.dataStore.data.map { prefs ->
        AurelUserPreferences(
            themeMode = try {
                ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            },
            editorFont = try {
                EditorFontPreference.valueOf(prefs[Keys.EDITOR_FONT] ?: EditorFontPreference.SERIF.name)
            } catch (_: Exception) {
                EditorFontPreference.SERIF
            },
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            lineHeightMultiplier = prefs[Keys.LINE_HEIGHT_MULTIPLIER] ?: 1.4f,
            reducedMotion = prefs[Keys.REDUCED_MOTION] ?: false,
            appLockEnabled = prefs[Keys.APP_LOCK_ENABLED] ?: false,
            privacyModeFlagSecure = prefs[Keys.PRIVACY_FLAG_SECURE] ?: false,
            showPinnedSection = prefs[Keys.SHOW_PINNED] ?: true,
            showRecentSection = prefs[Keys.SHOW_RECENT] ?: true,
            showTasksSection = prefs[Keys.SHOW_TASKS] ?: true,
            showStreakSection = prefs[Keys.SHOW_STREAK] ?: true,
            showNotebooksSection = prefs[Keys.SHOW_NOTEBOOKS] ?: true
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setEditorFont(font: EditorFontPreference) {
        context.dataStore.edit { it[Keys.EDITOR_FONT] = font.name }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.35f) }
    }

    suspend fun setLineHeightMultiplier(multiplier: Float) {
        context.dataStore.edit { it[Keys.LINE_HEIGHT_MULTIPLIER] = multiplier.coerceIn(1.2f, 1.8f) }
    }

    suspend fun setReducedMotion(reduced: Boolean) {
        context.dataStore.edit { it[Keys.REDUCED_MOTION] = reduced }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.APP_LOCK_ENABLED] = enabled }
    }

    suspend fun setPrivacyModeFlagSecure(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PRIVACY_FLAG_SECURE] = enabled }
    }

    suspend fun toggleHomeSection(section: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            when (section) {
                "pinned" -> prefs[Keys.SHOW_PINNED] = enabled
                "recent" -> prefs[Keys.SHOW_RECENT] = enabled
                "tasks" -> prefs[Keys.SHOW_TASKS] = enabled
                "streak" -> prefs[Keys.SHOW_STREAK] = enabled
                "notebooks" -> prefs[Keys.SHOW_NOTEBOOKS] = enabled
            }
        }
    }
}
