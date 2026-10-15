package com.aurel.notes.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.core.designsystem.components.AurelTagChip
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aurel.notes.ui.voice.VoiceRecordDialog

@Composable
fun HomeScreen(
    onNavigateToEditor: (Long) -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToNotebooks: () -> Unit,
    onNavigateToCanvas: (Long) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showVoiceDialog by remember { mutableStateOf(false) }

    if (showVoiceDialog) {
        VoiceRecordDialog(
            onAttachVoice = { file, duration ->
                // Launches editor for the new note
                onNavigateToEditor(0L)
            },
            onDismiss = { showVoiceDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Contextual Header & Greeting
        item {
            HomeHeaderSection(
                greeting = uiState.greeting,
                date = uiState.formattedDate,
                dailyPrompt = uiState.dailyPrompt,
                streakDays = uiState.streakDays,
                showStreak = uiState.userPrefs.showStreakSection
            )
        }

        // 2. Quick Capture Bar
        item {
            QuickCaptureBar(
                onTextClick = { onNavigateToEditor(0L) },
                onChecklistClick = { onNavigateToEditor(0L) },
                onVoiceClick = { showVoiceDialog = true },
                onDrawingClick = { onNavigateToCanvas(0L) }
            )
        }

        // 3. Zero-State Starter Card (if database empty)
        if (uiState.isEmpty) {
            item {
                ZeroStateOnboardingCard(
                    onCreateStarterClick = { viewModel.createSampleStarterNote() },
                    onStartFreshClick = { onNavigateToEditor(0L) }
                )
            }
        }

        // 4. Pinned / Featured Spotlight
        if (uiState.userPrefs.showPinnedSection && uiState.pinnedNotes.isNotEmpty()) {
            item {
                SectionHeader(title = "Featured Spotlight", actionText = "See all", onActionClick = onNavigateToNotes)
                FeaturedNoteCard(
                    note = uiState.pinnedNotes.first(),
                    onClick = { onNavigateToEditor(uiState.pinnedNotes.first().id) }
                )
            }
        }

        // 5. Today's Tasks & Progress Bar
        if (uiState.userPrefs.showTasksSection && (uiState.todayTasks.isNotEmpty() || uiState.todayPendingCount > 0)) {
            item {
                SectionHeader(title = "Today's Focus", actionText = "Manage", onActionClick = onNavigateToTasks)
                TodayTasksSection(
                    tasks = uiState.todayTasks.take(4),
                    completedCount = uiState.todayCompletedCount,
                    totalCount = uiState.todayCompletedCount + uiState.todayPendingCount,
                    onToggleTask = { taskId, isCompleted -> viewModel.toggleTaskCompleted(taskId, isCompleted) }
                )
            }
        }

        // 6. Recent Notes Carousel
        if (uiState.userPrefs.showRecentSection && uiState.recentNotes.isNotEmpty()) {
            item {
                SectionHeader(title = "Recent Notes", actionText = "View all", onActionClick = onNavigateToNotes)
                RecentNotesCarousel(
                    notes = uiState.recentNotes,
                    onNoteClick = onNavigateToEditor
                )
            }
        }

        // 7. Favorite Notebooks Quick-Jump
        if (uiState.userPrefs.showNotebooksSection && uiState.notebooks.isNotEmpty()) {
            item {
                SectionHeader(title = "Collections", actionText = "Explore", onActionClick = onNavigateToNotebooks)
                NotebooksHorizontalRow(
                    notebooks = uiState.notebooks,
                    onNotebookClick = { onNavigateToNotes() }
                )
            }
        }
    }
}

@Composable
private fun HomeHeaderSection(
    greeting: String,
    date: String,
    dailyPrompt: String,
    streakDays: Int,
    showStreak: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal)
            .padding(top = 28.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = date.uppercase(),
                    style = AurelTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = AurelTheme.colors.secondaryMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = greeting,
                    style = AurelTheme.typography.displayMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }

            if (showStreak) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AurelTheme.colors.primaryAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.primaryAccent.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = "Streak",
                            tint = AurelTheme.colors.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streakDays d",
                            style = AurelTheme.typography.labelMedium,
                            color = AurelTheme.colors.primaryAccent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Daily Prompt Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (AurelTheme.colors.isDark) Color(0xFF1E1A29) else Color(0xFFF1EEFF),
            border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.primaryAccent.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = AurelTheme.colors.primaryAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Daily Reflection Prompt",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryAccent
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\"$dailyPrompt\"",
                        style = AurelTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = AurelTheme.colors.primaryInk
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickCaptureBar(
    onTextClick: () -> Unit,
    onChecklistClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onDrawingClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        color = AurelTheme.colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickCaptureItem(icon = Icons.Filled.Edit, label = "Note", onClick = onTextClick)
            QuickCaptureItem(icon = Icons.Filled.CheckBox, label = "Task", onClick = onChecklistClick)
            QuickCaptureItem(icon = Icons.Filled.Mic, label = "Voice", onClick = onVoiceClick)
            QuickCaptureItem(icon = Icons.Filled.Brush, label = "Sketch", onClick = onDrawingClick)
        }
    }
}

@Composable
private fun QuickCaptureItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AurelTheme.colors.primaryAccent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AurelTheme.colors.primaryAccent,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = AurelTheme.typography.labelSmall,
            color = AurelTheme.colors.primaryInk
        )
    }
}

@Composable
private fun ZeroStateOnboardingCard(
    onCreateStarterClick: () -> Unit,
    onStartFreshClick: () -> Unit
) {
    AurelCard(
        modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 12.dp),
        colorHex = "#EAE5FF"
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Text(
                text = "Welcome to Aurel 🌿",
                style = AurelTheme.typography.headlineLarge,
                color = AurelTheme.colors.primaryInk
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your quiet, local-first thinking space. Connect your thoughts with backlinks, write without distractions, and keep your tasks clear.",
                style = AurelTheme.typography.bodyMedium,
                color = AurelTheme.colors.secondaryMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AurelTagChip(
                    text = "Load Starter Guide",
                    isSelected = true,
                    leadingIcon = {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = AurelTheme.colors.primaryAccent)
                    },
                    onClick = onCreateStarterClick
                )
                AurelTagChip(
                    text = "New Blank Sheet",
                    isSelected = false,
                    leadingIcon = {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = AurelTheme.colors.primaryInk)
                    },
                    onClick = onStartFreshClick
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AurelTheme.spacing.screenHorizontal)
            .padding(top = 22.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AurelTheme.typography.titleLarge,
            color = AurelTheme.colors.primaryInk
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = AurelTheme.typography.labelMedium,
                color = AurelTheme.colors.primaryAccent,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
private fun FeaturedNoteCard(
    note: Note,
    onClick: () -> Unit
) {
    AurelCard(
        modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal),
        colorHex = note.cardColorHex,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = AurelTheme.colors.primaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PINNED NOTE",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.primaryAccent
                    )
                }

                if (note.tags.isNotEmpty()) {
                    Text(
                        text = "#${note.tags.first()}",
                        style = AurelTheme.typography.labelSmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (note.title.isNotBlank()) note.title else "Untitled Note",
                style = AurelTheme.typography.headlineLarge,
                color = AurelTheme.colors.primaryInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (note.plainTextSummary.isNotBlank()) note.plainTextSummary else note.content.take(160),
                style = AurelTheme.typography.bodyMedium,
                color = AurelTheme.colors.secondaryMuted,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TodayTasksSection(
    tasks: List<Task>,
    completedCount: Int,
    totalCount: Int,
    onToggleTask: (Long, Boolean) -> Unit
) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "task_progress")

    AurelCard(
        modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedCount of $totalCount completed",
                    style = AurelTheme.typography.labelMedium,
                    color = AurelTheme.colors.secondaryMuted
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = AurelTheme.typography.labelMedium,
                    color = AurelTheme.colors.primaryAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AurelTheme.colors.primaryAccent,
                trackColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.15f)
            )

            if (tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { isChecked -> onToggleTask(task.id, isChecked) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AurelTheme.colors.primaryAccent,
                                uncheckedColor = AurelTheme.colors.secondaryMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = task.title,
                            style = AurelTheme.typography.bodyMedium,
                            color = if (task.isCompleted) AurelTheme.colors.secondaryMuted else AurelTheme.colors.primaryInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentNotesCarousel(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            AurelCard(
                modifier = Modifier.width(220.dp),
                colorHex = note.cardColorHex,
                onClick = { onNoteClick(note.id) }
            ) {
                Column(modifier = Modifier.height(120.dp)) {
                    Text(
                        text = if (note.title.isNotBlank()) note.title else "Untitled",
                        style = AurelTheme.typography.titleMedium,
                        color = AurelTheme.colors.primaryInk,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (note.plainTextSummary.isNotBlank()) note.plainTextSummary else note.content.take(120),
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (note.tags.isNotEmpty()) {
                        Text(
                            text = "#${note.tags.first()}",
                            style = AurelTheme.typography.labelSmall,
                            color = AurelTheme.colors.primaryAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotebooksHorizontalRow(
    notebooks: List<Notebook>,
    onNotebookClick: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notebooks, key = { it.id }) { notebook ->
            AurelTagChip(
                text = "${notebook.name} (${notebook.noteCount})",
                isSelected = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AurelTheme.colors.primaryAccent
                    )
                },
                onClick = { onNotebookClick(notebook.id) }
            )
        }
    }
}
