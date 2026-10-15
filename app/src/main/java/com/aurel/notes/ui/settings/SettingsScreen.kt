package com.aurel.notes.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.backup.ZipBackupManager
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.EditorFontPreference
import com.aurel.notes.core.designsystem.ThemeMode
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.core.designsystem.components.AurelTagChip

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val prefs = uiState.preferences
    val context = LocalContext.current

    var showRestoreConflictDialog by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    // SAF Create Document for ZIP Backup
    val createBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    viewModel.createBackup(outputStream) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // SAF Open Document for ZIP Restore
    val openRestoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it
            showRestoreConflictDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AurelTheme.colors.primaryInk
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        style = AurelTheme.typography.headlineLarge,
                        color = AurelTheme.colors.primaryInk
                    )
                }
            }

            // Section 1: Appearance & Theme
            item {
                SettingsSectionHeader("Appearance & Theme", Icons.Filled.Palette)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Theme Mode",
                            style = AurelTheme.typography.titleMedium,
                            color = AurelTheme.colors.primaryInk
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AurelTagChip(
                                text = "System",
                                isSelected = prefs.themeMode == ThemeMode.SYSTEM,
                                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                            )
                            AurelTagChip(
                                text = "Light Warm",
                                isSelected = prefs.themeMode == ThemeMode.LIGHT,
                                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                            )
                            AurelTagChip(
                                text = "Dark",
                                isSelected = prefs.themeMode == ThemeMode.DARK,
                                onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                            )
                            AurelTagChip(
                                text = "AMOLED",
                                isSelected = prefs.themeMode == ThemeMode.AMOLED,
                                onClick = { viewModel.setThemeMode(ThemeMode.AMOLED) }
                            )
                        }
                    }
                }
            }

            // Section 2: Typography & Editorial Design
            item {
                SettingsSectionHeader("Typography & Editor", Icons.Filled.FontDownload)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Editor Typeface",
                            style = AurelTheme.typography.titleMedium,
                            color = AurelTheme.colors.primaryInk
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AurelTagChip(
                                text = "Editorial Serif",
                                isSelected = prefs.editorFont == EditorFontPreference.SERIF,
                                onClick = { viewModel.setEditorFont(EditorFontPreference.SERIF) }
                            )
                            AurelTagChip(
                                text = "Clean Sans",
                                isSelected = prefs.editorFont == EditorFontPreference.SANS,
                                onClick = { viewModel.setEditorFont(EditorFontPreference.SANS) }
                            )
                            AurelTagChip(
                                text = "Mono",
                                isSelected = prefs.editorFont == EditorFontPreference.MONO,
                                onClick = { viewModel.setEditorFont(EditorFontPreference.MONO) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Font Scale",
                                style = AurelTheme.typography.bodyMedium,
                                color = AurelTheme.colors.primaryInk
                            )
                            Text(
                                text = "%.2fx".format(prefs.fontScale),
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.primaryAccent
                            )
                        }
                        Slider(
                            value = prefs.fontScale,
                            onValueChange = { viewModel.setFontScale(it) },
                            valueRange = 0.85f..1.35f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = AurelTheme.colors.primaryAccent,
                                activeTrackColor = AurelTheme.colors.primaryAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Line Height Multiplier",
                                style = AurelTheme.typography.bodyMedium,
                                color = AurelTheme.colors.primaryInk
                            )
                            Text(
                                text = "%.1fx".format(prefs.lineHeightMultiplier),
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.primaryAccent
                            )
                        }
                        Slider(
                            value = prefs.lineHeightMultiplier,
                            onValueChange = { viewModel.setLineHeightMultiplier(it) },
                            valueRange = 1.2f..1.8f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = AurelTheme.colors.primaryAccent,
                                activeTrackColor = AurelTheme.colors.primaryAccent
                            )
                        )

                        // Live Typography Sample Preview
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AurelTheme.colors.background,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Preview: The quiet room, the soft warm paper, and thoughts finding their shape.",
                                modifier = Modifier.padding(12.dp),
                                style = AurelTheme.typography.bodyMedium.copy(
                                    fontSize = (15 * prefs.fontScale).sp,
                                    lineHeight = (22 * prefs.lineHeightMultiplier).sp,
                                    fontFamily = when (prefs.editorFont) {
                                        EditorFontPreference.SERIF -> FontFamily.Serif
                                        EditorFontPreference.MONO -> FontFamily.Monospace
                                        else -> FontFamily.SansSerif
                                    }
                                ),
                                color = AurelTheme.colors.primaryInk
                            )
                        }
                    }
                }
            }

            // Section 3: Signature Home Customization
            item {
                SettingsSectionHeader("Home Dashboard Sections", Icons.Filled.ViewCarousel)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsToggleRow("Pinned Note Spotlight", prefs.showPinnedSection) {
                            viewModel.toggleHomeSection("pinned", it)
                        }
                        SettingsToggleRow("Recent Notes Carousel", prefs.showRecentSection) {
                            viewModel.toggleHomeSection("recent", it)
                        }
                        SettingsToggleRow("Today's Tasks Focus", prefs.showTasksSection) {
                            viewModel.toggleHomeSection("tasks", it)
                        }
                        SettingsToggleRow("Activity & Reflection Streak", prefs.showStreakSection) {
                            viewModel.toggleHomeSection("streak", it)
                        }
                        SettingsToggleRow("Notebook Quick Chips", prefs.showNotebooksSection) {
                            viewModel.toggleHomeSection("notebooks", it)
                        }
                    }
                }
            }

            // Section 4: Security & Vault
            item {
                SettingsSectionHeader("Security & Privacy", Icons.Filled.Security)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsToggleRow(
                            title = "Biometric App Lock",
                            subtitle = "Require fingerprint or device PIN to access Aurel",
                            isChecked = prefs.appLockEnabled
                        ) {
                            viewModel.setAppLockEnabled(it)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsToggleRow(
                            title = "Privacy Mode (FLAG_SECURE)",
                            subtitle = "Blank out app contents in Android recent apps switcher",
                            isChecked = prefs.privacyModeFlagSecure
                        ) {
                            viewModel.setPrivacyModeFlagSecure(it)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsToggleRow(
                            title = "Reduced Motion",
                            subtitle = "Minimize UI transitions for faster perceived speed",
                            isChecked = prefs.reducedMotion
                        ) {
                            viewModel.setReducedMotion(it)
                        }
                    }
                }
            }

            // Section 5: Backup & Portability
            item {
                SettingsSectionHeader("Backup & Data Portability", Icons.Filled.Backup)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Full Portable Backup (.zip)",
                            style = AurelTheme.typography.titleMedium,
                            color = AurelTheme.colors.primaryInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Exports all notes, notebooks, tasks, and media attachments to an offline zip archive with schema verification.",
                            style = AurelTheme.typography.bodySmall,
                            color = AurelTheme.colors.secondaryMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val timestamp = System.currentTimeMillis()
                                    createBackupLauncher.launch("aurel_backup_$timestamp.zip")
                                },
                                enabled = !uiState.isBackingUp && !uiState.isRestoring,
                                colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (uiState.isBackingUp) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                } else {
                                    Icon(Icons.Filled.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export .ZIP")
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    openRestoreLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                                },
                                enabled = !uiState.isBackingUp && !uiState.isRestoring,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (uiState.isRestoring) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = AurelTheme.colors.primaryAccent)
                                } else {
                                    Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restore .ZIP")
                                }
                            }
                        }
                    }
                }
            }

            // Section 6: Data Management & Database Stats
            item {
                SettingsSectionHeader("Data Management & Storage", Icons.Filled.Tune)
                AurelCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox(label = "Active Notes", count = uiState.stats.activeNotesCount)
                            StatBox(label = "Notebooks", count = uiState.stats.notebooksCount)
                            StatBox(label = "Pending Tasks", count = uiState.stats.pendingTasksCount)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.HorizontalDivider(color = AurelTheme.colors.divider)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Empty Trash Bin",
                                    style = AurelTheme.typography.bodyMedium,
                                    color = AurelTheme.colors.primaryInk
                                )
                                Text(
                                    text = "Permanently purge deleted items immediately.",
                                    style = AurelTheme.typography.labelSmall,
                                    color = AurelTheme.colors.secondaryMuted
                                )
                            }
                            TextButton(
                                onClick = { showEmptyTrashDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD94848))
                            ) {
                                Text("Empty Now")
                            }
                        }
                    }
                }
            }

            // Section 7: About Aurel
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aurel",
                        style = AurelTheme.typography.headlineMedium,
                        color = AurelTheme.colors.primaryInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0 · Local-First Knowledge & Thinking Space",
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% Offline · Zero Telemetry · Your Data Is Exclusively Yours",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryAccent
                    )
                }
            }
        }
    }

    // Conflict Resolution Dialog on Restore
    if (showRestoreConflictDialog && pendingRestoreUri != null) {
        var selectedStrategy by remember { mutableStateOf(ZipBackupManager.ConflictResolution.KEEP_BOTH) }

        AlertDialog(
            onDismissRequest = {
                showRestoreConflictDialog = false
                pendingRestoreUri = null
            },
            title = { Text("Restore Backup — Conflict Resolution") },
            text = {
                Column {
                    Text(
                        text = "How would you like to handle existing notes and notebooks with matching names?",
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStrategy = ZipBackupManager.ConflictResolution.KEEP_BOTH },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStrategy == ZipBackupManager.ConflictResolution.KEEP_BOTH,
                            onClick = { selectedStrategy = ZipBackupManager.ConflictResolution.KEEP_BOTH },
                            colors = RadioButtonDefaults.colors(selectedColor = AurelTheme.colors.primaryAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Keep Both (Safe)", fontWeight = FontWeight.Bold, style = AurelTheme.typography.bodyMedium)
                            Text("Appends '(Restored)' to duplicate items to avoid overwriting.", style = AurelTheme.typography.labelSmall, color = AurelTheme.colors.secondaryMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStrategy = ZipBackupManager.ConflictResolution.REPLACE },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStrategy == ZipBackupManager.ConflictResolution.REPLACE,
                            onClick = { selectedStrategy = ZipBackupManager.ConflictResolution.REPLACE },
                            colors = RadioButtonDefaults.colors(selectedColor = AurelTheme.colors.primaryAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Replace Existing", fontWeight = FontWeight.Bold, style = AurelTheme.typography.bodyMedium)
                            Text("Overwrites matching IDs with the backup copy.", style = AurelTheme.typography.labelSmall, color = AurelTheme.colors.secondaryMuted)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uri = pendingRestoreUri
                        showRestoreConflictDialog = false
                        pendingRestoreUri = null
                        if (uri != null) {
                            try {
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    viewModel.restoreBackup(inputStream, selectedStrategy) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AurelTheme.colors.primaryAccent)
                ) {
                    Text("Start Restore")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRestoreConflictDialog = false
                        pendingRestoreUri = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Empty Trash Confirmation Dialog
    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = { Text("Empty Trash Bin?") },
            text = { Text("All notes currently in the trash will be permanently deleted. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emptyTrash()
                        showEmptyTrashDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD94848))
                ) {
                    Text("Permanently Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AurelTheme.colors.primaryAccent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = AurelTheme.typography.labelLarge,
            color = AurelTheme.colors.primaryAccent
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    isChecked: Boolean,
    subtitle: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = AurelTheme.typography.bodyMedium,
                color = AurelTheme.colors.primaryInk
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AurelTheme.colors.primaryAccent,
                uncheckedThumbColor = AurelTheme.colors.secondaryMuted,
                uncheckedTrackColor = AurelTheme.colors.divider
            )
        )
    }
}

@Composable
private fun StatBox(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = AurelTheme.typography.headlineMedium,
            color = AurelTheme.colors.primaryAccent
        )
        Text(
            text = label,
            style = AurelTheme.typography.labelSmall,
            color = AurelTheme.colors.secondaryMuted
        )
    }
}
