package com.aurel.notes.ui.tasks

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.PriorityHigh
import com.aurel.notes.core.designsystem.PriorityLow
import com.aurel.notes.core.designsystem.PriorityMedium
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.core.designsystem.components.AurelTagChip
import com.aurel.notes.domain.model.Task
import com.aurel.notes.domain.model.TaskPriority
import com.aurel.notes.domain.model.TaskRecurrence
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background)
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AurelTheme.spacing.screenHorizontal)
                .padding(top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TASKS & REMINDERS",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
                Text(
                    text = "Action Horizon",
                    style = AurelTheme.typography.displayMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }

            AurelTagChip(
                text = "+ Add Task",
                isSelected = true,
                onClick = { showCreateDialog = true }
            )
        }

        // Today's Focus Progress
        val totalToday = uiState.todayCompletedCount + uiState.todayPendingCount
        val progress = if (totalToday > 0) uiState.todayCompletedCount.toFloat() / totalToday else 0f

        AurelCard(
            modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.todayCompletedCount} of $totalToday completed today",
                        style = AurelTheme.typography.labelMedium,
                        color = AurelTheme.colors.secondaryMuted
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = AurelTheme.typography.labelMedium,
                        color = AurelTheme.colors.primaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AurelTheme.colors.primaryAccent,
                    trackColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.15f)
                )
            }
        }

        // Category Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskFilterTab.values().forEach { tab ->
                val label = when (tab) {
                    TaskFilterTab.TODAY -> "Today"
                    TaskFilterTab.UPCOMING -> "Upcoming"
                    TaskFilterTab.OVERDUE -> "Overdue"
                    TaskFilterTab.COMPLETED -> "Completed"
                }
                AurelTagChip(
                    text = label,
                    isSelected = uiState.currentTab == tab,
                    onClick = { viewModel.setTab(tab) }
                )
            }
        }

        // Tasks List
        if (uiState.tasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (uiState.currentTab) {
                            TaskFilterTab.TODAY -> "No tasks due today"
                            TaskFilterTab.UPCOMING -> "No upcoming tasks"
                            TaskFilterTab.OVERDUE -> "No overdue tasks"
                            TaskFilterTab.COMPLETED -> "No completed tasks yet"
                        },
                        style = AurelTheme.typography.titleMedium,
                        color = AurelTheme.colors.primaryInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+ Add Task' above to capture what matters.",
                        style = AurelTheme.typography.bodySmall,
                        color = AurelTheme.colors.secondaryMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tasks, key = { it.id }) { task ->
                    TaskCardItem(
                        task = task,
                        onToggle = { viewModel.toggleTaskCompleted(task) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTaskDialog(
            onCreateTask = { title, priority, dueTime, reminder, recurrence ->
                viewModel.createTask(title, priority, dueTime, reminder, recurrence)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun TaskCardItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.LOW -> PriorityLow
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AurelTheme.colors.cardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AurelTheme.colors.primaryAccent,
                    uncheckedColor = AurelTheme.colors.secondaryMuted
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = AurelTheme.typography.bodyMedium.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (task.isCompleted) AurelTheme.colors.secondaryMuted else AurelTheme.colors.primaryInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Priority Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = task.priority.name,
                            style = AurelTheme.typography.labelSmall,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Due date if set
                    if (task.dueDateTime != null) {
                        val dueStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(task.dueDateTime))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (task.reminderEnabled) {
                                Icon(
                                    Icons.Filled.Alarm,
                                    contentDescription = "Reminder",
                                    tint = AurelTheme.colors.primaryAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = dueStr,
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.secondaryMuted
                            )
                        }
                    }

                    // Recurrence
                    if (task.recurrence != TaskRecurrence.NONE) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Repeat, contentDescription = "Repeats", tint = AurelTheme.colors.secondaryMuted, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = task.recurrence.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.secondaryMuted
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Task",
                    tint = AurelTheme.colors.secondaryMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CreateTaskDialog(
    onCreateTask: (String, TaskPriority, Long?, Boolean, TaskRecurrence) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var hasDueDate by remember { mutableStateOf(true) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var selectedRecurrence by remember { mutableStateOf(TaskRecurrence.NONE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Action Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("What needs to be done?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Priority Selector
                Text("Priority", style = AurelTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskPriority.values().forEach { priority ->
                        AurelTagChip(
                            text = priority.name,
                            isSelected = selectedPriority == priority,
                            onClick = { selectedPriority = priority }
                        )
                    }
                }

                // Due Time & Reminder Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Exact Reminder Alarm", style = AurelTheme.typography.bodyMedium)
                    Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                }

                // Recurrence
                Text("Repeat", style = AurelTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskRecurrence.values().forEach { rec ->
                        AurelTagChip(
                            text = if (rec == TaskRecurrence.NONE) "Once" else rec.name.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = selectedRecurrence == rec,
                            onClick = { selectedRecurrence = rec }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueTime = if (hasDueDate) {
                            Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, 2)
                            }.timeInMillis
                        } else null
                        onCreateTask(title, selectedPriority, dueTime, reminderEnabled, selectedRecurrence)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
