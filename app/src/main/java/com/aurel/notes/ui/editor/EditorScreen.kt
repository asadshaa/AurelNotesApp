package com.aurel.notes.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.core.designsystem.components.AurelTagChip
import com.aurel.notes.domain.model.Attachment
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.NoteTemplate
import com.aurel.notes.domain.model.NoteTemplates

@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLinkedNote: (Long) -> Unit = {},
    viewModel: EditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showColorDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }

    val exportMdLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/markdown")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(com.aurel.notes.core.export.NoteExporter.toMarkdown(viewModel.getCurrentNoteEntity()).toByteArray())
            }
        }
    }

    val exportTxtLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(com.aurel.notes.core.export.NoteExporter.toPlainText(viewModel.getCurrentNoteEntity()).toByteArray())
            }
        }
    }

    val exportHtmlLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/html")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(com.aurel.notes.core.export.NoteExporter.toHtml(viewModel.getCurrentNoteEntity()).toByteArray())
            }
        }
    }

    val exportPdfLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                com.aurel.notes.core.export.NoteExporter.exportToPdf(viewModel.getCurrentNoteEntity(), stream)
            }
        }
    }
    var newTagInput by remember { mutableStateOf("") }
    var showNotebookMenu by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }

    val resolvedBackground = CardColorTokens.resolveCardColor(uiState.cardColorHex, AurelTheme.colors.isDark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(resolvedBackground)
            .imePadding()
    ) {
        // Top Toolbar
        EditorTopBar(
            saveStatus = uiState.saveStatus,
            canUndo = uiState.canUndo,
            canRedo = uiState.canRedo,
            isPinned = uiState.isPinned,
            onBackClick = onNavigateBack,
            onUndoClick = { viewModel.undo() },
            onRedoClick = { viewModel.redo() },
            onPinClick = { viewModel.togglePin() },
            onPaletteClick = { showColorDialog = true },
            onMoreClick = { showMoreMenu = true }
        )

        // Focus Mode Countdown Bar
        if (uiState.isFocusMode) {
            FocusTimerBanner(
                secondsRemaining = uiState.focusTimerSeconds,
                isRunning = uiState.isTimerRunning,
                onTogglePlay = {
                    if (uiState.isTimerRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer(25)
                },
                onReset = { viewModel.resetFocusTimer(25) }
            )
        }

        // More Menu Dropdown
        DropdownMenu(
            expanded = showMoreMenu,
            onDismissRequest = { showMoreMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (uiState.isVaultLocked) "Remove from Vault" else "Lock in Private Vault") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = if (uiState.isVaultLocked) AurelTheme.colors.primaryAccent else androidx.compose.material3.LocalContentColor.current
                    )
                },
                onClick = {
                    viewModel.toggleVaultLock()
                    showMoreMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (uiState.isArchived) "Unarchive Note" else "Archive Note") },
                leadingIcon = { Icon(Icons.Filled.Archive, contentDescription = null) },
                onClick = {
                    viewModel.toggleArchive()
                    showMoreMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (uiState.isReadingMode) "Exit Reading Mode" else "Reading Mode") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                onClick = {
                    viewModel.toggleReadingMode()
                    showMoreMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (uiState.isFocusMode) "Exit Focus Mode" else "Focus Mode & Timer") },
                leadingIcon = { Icon(Icons.Filled.Timer, contentDescription = null) },
                onClick = {
                    viewModel.toggleFocusMode()
                    showMoreMenu = false
                }
            )
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            DropdownMenuItem(
                text = { Text("Export as Markdown (.md)") },
                leadingIcon = { Icon(Icons.Filled.FileDownload, contentDescription = null) },
                onClick = {
                    showMoreMenu = false
                    val filename = "${uiState.title.ifBlank { "note" }.take(30).replace("[^a-zA-Z0-9_-]".toRegex(), "_")}.md"
                    exportMdLauncher.launch(filename)
                }
            )
            DropdownMenuItem(
                text = { Text("Export as Plain Text (.txt)") },
                leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) },
                onClick = {
                    showMoreMenu = false
                    val filename = "${uiState.title.ifBlank { "note" }.take(30).replace("[^a-zA-Z0-9_-]".toRegex(), "_")}.txt"
                    exportTxtLauncher.launch(filename)
                }
            )
            DropdownMenuItem(
                text = { Text("Export as HTML (.html)") },
                leadingIcon = { Icon(Icons.Filled.Code, contentDescription = null) },
                onClick = {
                    showMoreMenu = false
                    val filename = "${uiState.title.ifBlank { "note" }.take(30).replace("[^a-zA-Z0-9_-]".toRegex(), "_")}.html"
                    exportHtmlLauncher.launch(filename)
                }
            )
            DropdownMenuItem(
                text = { Text("Export as PDF (.pdf)") },
                leadingIcon = { Icon(Icons.Filled.PictureAsPdf, contentDescription = null) },
                onClick = {
                    showMoreMenu = false
                    val filename = "${uiState.title.ifBlank { "note" }.take(30).replace("[^a-zA-Z0-9_-]".toRegex(), "_")}.pdf"
                    exportPdfLauncher.launch(filename)
                }
            )
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            DropdownMenuItem(
                text = { Text("Move to Trash", color = Color(0xFFD94848)) },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = Color(0xFFD94848)) },
                onClick = {
                    showMoreMenu = false
                    viewModel.deleteNote(onDeleted = onNavigateBack)
                }
            )
        }

        // Color Picker Dialog
        if (showColorDialog) {
            ColorPickerDialog(
                selectedColor = uiState.cardColorHex,
                onColorSelected = {
                    viewModel.setCardColor(it)
                    showColorDialog = false
                },
                onDismiss = { showColorDialog = false }
            )
        }

        // Template Picker Dialog
        if (showTemplateDialog) {
            TemplatePickerDialog(
                onSelectTemplate = { template ->
                    viewModel.applyTemplate(template)
                    showTemplateDialog = false
                },
                onDismiss = { showTemplateDialog = false }
            )
        }

        // Add Tag Dialog
        if (showTagDialog) {
            AlertDialog(
                onDismissRequest = { showTagDialog = false },
                title = { Text("Add Tag") },
                text = {
                    OutlinedTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it },
                        placeholder = { Text("e.g. ideas, work, travel") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addTag(newTagInput)
                        newTagInput = ""
                        showTagDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTagDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Tags & Notebook Strip (hidden in Reading Mode)
        if (!uiState.isReadingMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentNotebook = uiState.notebooks.firstOrNull { it.id == uiState.selectedNotebookId }
                AurelTagChip(
                    text = currentNotebook?.name ?: "No Notebook",
                    leadingIcon = {
                        Icon(Icons.Filled.Book, contentDescription = null, modifier = Modifier.size(14.dp), tint = AurelTheme.colors.primaryAccent)
                    },
                    onClick = { showNotebookMenu = true }
                )

                DropdownMenu(
                    expanded = showNotebookMenu,
                    onDismissRequest = { showNotebookMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            viewModel.setNotebook(null)
                            showNotebookMenu = false
                        }
                    )
                    uiState.notebooks.forEach { notebook ->
                        DropdownMenuItem(
                            text = { Text(notebook.name) },
                            onClick = {
                                viewModel.setNotebook(notebook.id)
                                showNotebookMenu = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(uiState.tags, key = { it }) { tag ->
                        AurelTagChip(
                            text = "#$tag",
                            onClick = { viewModel.removeTag(tag) }
                        )
                    }
                    item {
                        AurelTagChip(
                            text = "+ Tag",
                            onClick = { showTagDialog = true }
                        )
                    }
                }
            }
        }

        // Main Scrollable Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = AurelTheme.spacing.screenHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Title input
            BasicTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                enabled = !uiState.isReadingMode,
                modifier = Modifier.fillMaxWidth(),
                textStyle = AurelTheme.typography.displayMedium.copy(color = AurelTheme.colors.primaryInk),
                cursorBrush = SolidColor(AurelTheme.colors.primaryAccent),
                decorationBox = { innerTextField ->
                    if (uiState.title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = AurelTheme.typography.displayMedium,
                            color = AurelTheme.colors.secondaryMuted.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Body / Content input
            BasicTextField(
                value = uiState.content,
                onValueChange = { viewModel.onContentChange(it) },
                enabled = !uiState.isReadingMode,
                modifier = Modifier.fillMaxWidth(),
                textStyle = AurelTheme.typography.bodyLarge.copy(color = AurelTheme.colors.primaryInk),
                cursorBrush = SolidColor(AurelTheme.colors.primaryAccent),
                decorationBox = { innerTextField ->
                    if (uiState.content.isEmpty()) {
                        Text(
                            text = "Write your thoughts... Supports # headings, - [ ] tasks, and [[backlinks]]",
                            style = AurelTheme.typography.bodyLarge,
                            color = AurelTheme.colors.secondaryMuted.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )

            // Attachments Preview Section
            if (uiState.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "ATTACHMENTS (${uiState.attachments.size})",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                AttachmentsGrid(
                    attachments = uiState.attachments,
                    onDeleteAttachment = { viewModel.deleteAttachment(it) }
                )
            }

            // Bi-Directional Backlinks Section
            if (uiState.backlinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(28.dp))
                BacklinksSection(
                    backlinks = uiState.backlinks,
                    onBacklinkClick = onNavigateToLinkedNote
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Formatting Toolbar (above keyboard, hidden in reading mode)
        if (!uiState.isReadingMode) {
            FormattingToolbar(
                onApplyFormat = { viewModel.applyFormatting(it) },
                onOpenTemplates = { showTemplateDialog = true },
                onAttachClick = {
                    // Quick placeholder attachment for testing vertical slice
                    viewModel.addAttachment(
                        type = com.aurel.notes.domain.model.AttachmentType.FILE,
                        filePath = "attachments/doc_${System.currentTimeMillis()}.txt",
                        fileName = "Reference Document.txt",
                        fileSizeBytes = 1024L
                    )
                }
            )
        }

        // Bottom Word Counter Bar
        Surface(
            modifier = Modifier.fillMaxWidth().height(38.dp),
            color = resolvedBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.wordCount} words · ${uiState.charCount} characters",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )

                if (uiState.isReadingMode) {
                    Text(
                        text = "Reading Mode",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusTimerBanner(
    secondsRemaining: Int,
    isRunning: Boolean,
    onTogglePlay: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        color = AurelTheme.colors.primaryAccent.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.primaryAccent.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Timer, contentDescription = null, tint = AurelTheme.colors.primaryAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Focus Timer: $timeFormatted",
                    style = AurelTheme.typography.titleMedium,
                    color = AurelTheme.colors.primaryAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = onTogglePlay, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Toggle Timer",
                        tint = AurelTheme.colors.primaryAccent
                    )
                }
                IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Replay,
                        contentDescription = "Reset Timer",
                        tint = AurelTheme.colors.primaryAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun BacklinksSection(
    backlinks: List<Note>,
    onBacklinkClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Link, contentDescription = null, tint = AurelTheme.colors.primaryAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LINKED REFERENCES (${backlinks.size})",
                style = AurelTheme.typography.labelSmall,
                color = AurelTheme.colors.primaryAccent,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        backlinks.forEach { note ->
            AurelCard(
                modifier = Modifier.padding(vertical = 4.dp),
                colorHex = note.cardColorHex,
                onClick = { onBacklinkClick(note.id) }
            ) {
                Column {
                    Text(
                        text = if (note.title.isNotBlank()) note.title else "Untitled Note",
                        style = AurelTheme.typography.titleMedium,
                        color = AurelTheme.colors.primaryInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (note.plainTextSummary.isNotBlank()) note.plainTextSummary else note.content.take(100),
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentsGrid(
    attachments: List<Attachment>,
    onDeleteAttachment: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        attachments.forEach { attachment ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = AurelTheme.colors.cardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Attachment,
                            contentDescription = null,
                            tint = AurelTheme.colors.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = attachment.fileName,
                                style = AurelTheme.typography.labelMedium,
                                color = AurelTheme.colors.primaryInk
                            )
                            Text(
                                text = "${attachment.type.name} · ${attachment.fileSizeBytes} B",
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.secondaryMuted
                            )
                        }
                    }

                    IconButton(onClick = { onDeleteAttachment(attachment.id) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Delete", tint = Color(0xFFD94848), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplatePickerDialog(
    onSelectTemplate: (NoteTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a Note Template") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(NoteTemplates.all, key = { it.name }) { template ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTemplate(template) }
                            .border(1.dp, AurelTheme.colors.divider, RoundedCornerShape(12.dp)),
                        color = AurelTheme.colors.cardSurface
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = template.name,
                                style = AurelTheme.typography.titleMedium,
                                color = AurelTheme.colors.primaryInk,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = template.description,
                                style = AurelTheme.typography.bodySmall,
                                color = AurelTheme.colors.secondaryMuted
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditorTopBar(
    saveStatus: SaveStatus,
    canUndo: Boolean,
    canRedo: Boolean,
    isPinned: Boolean,
    onBackClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onPinClick: () -> Unit,
    onPaletteClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AurelTheme.colors.primaryInk)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (saveStatus) {
                    SaveStatus.SAVING -> AurelTheme.colors.primaryAccent.copy(alpha = 0.12f)
                    SaveStatus.SAVED -> Color(0xFF439C5E).copy(alpha = 0.12f)
                    SaveStatus.IDLE -> Color.Transparent
                }
            ) {
                Text(
                    text = when (saveStatus) {
                        SaveStatus.SAVING -> "Saving…"
                        SaveStatus.SAVED -> "Saved"
                        SaveStatus.IDLE -> ""
                    },
                    style = AurelTheme.typography.labelSmall,
                    color = when (saveStatus) {
                        SaveStatus.SAVING -> AurelTheme.colors.primaryAccent
                        SaveStatus.SAVED -> Color(0xFF439C5E)
                        SaveStatus.IDLE -> Color.Transparent
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onUndoClick, enabled = canUndo) {
                Icon(
                    Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) AurelTheme.colors.primaryInk else AurelTheme.colors.secondaryMuted.copy(alpha = 0.3f)
                )
            }
            IconButton(onClick = onRedoClick, enabled = canRedo) {
                Icon(
                    Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) AurelTheme.colors.primaryInk else AurelTheme.colors.secondaryMuted.copy(alpha = 0.3f)
                )
            }
            IconButton(onClick = onPinClick) {
                Icon(
                    imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin Note",
                    tint = if (isPinned) AurelTheme.colors.primaryAccent else AurelTheme.colors.primaryInk
                )
            }
            IconButton(onClick = onPaletteClick) {
                Icon(Icons.Filled.Palette, contentDescription = "Change Color", tint = AurelTheme.colors.primaryInk)
            }
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = AurelTheme.colors.primaryInk)
            }
        }
    }
}

@Composable
private fun ColorPickerDialog(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note Pastel Color") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardColorTokens.all.forEach { colorHex ->
                    val isSelected = selectedColor == colorHex
                    val displayColor = CardColorTokens.resolveCardColor(colorHex, AurelTheme.colors.isDark)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(displayColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.divider,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = AurelTheme.colors.primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
