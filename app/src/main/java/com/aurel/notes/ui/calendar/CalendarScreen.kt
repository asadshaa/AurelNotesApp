package com.aurel.notes.ui.calendar

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.components.AurelCard
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToEditor: (Long) -> Unit,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formattedSelectedDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(uiState.selectedDateMillis))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal)
                    .padding(top = 24.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "TIME & AGENDA",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
                Text(
                    text = "Offline Calendar",
                    style = AurelTheme.typography.displayMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }
        }

        // Month Navigation Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                color = AurelTheme.colors.cardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Month title & arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.prevMonth() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Month", tint = AurelTheme.colors.primaryInk)
                        }

                        Text(
                            text = uiState.monthTitle,
                            style = AurelTheme.typography.titleLarge,
                            color = AurelTheme.colors.primaryInk,
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = AurelTheme.colors.primaryInk)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Day of week labels
                    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        dayLabels.forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                style = AurelTheme.typography.labelSmall,
                                color = AurelTheme.colors.secondaryMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 42 days grid (6 rows x 7 cols)
                    val chunked = uiState.days.chunked(7)
                    chunked.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            week.forEach { day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                day.isSelected -> AurelTheme.colors.primaryAccent
                                                day.isToday -> AurelTheme.colors.primaryAccent.copy(alpha = 0.15f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { viewModel.selectDate(day.dateMillis) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = day.dayNumber.toString(),
                                            style = AurelTheme.typography.labelMedium,
                                            color = when {
                                                day.isSelected -> Color.White
                                                day.isToday -> AurelTheme.colors.primaryAccent
                                                !day.isCurrentMonth -> AurelTheme.colors.secondaryMuted.copy(alpha = 0.35f)
                                                else -> AurelTheme.colors.primaryInk
                                            },
                                            fontWeight = if (day.isToday || day.isSelected) FontWeight.Bold else FontWeight.Normal
                                        )

                                        // Activity indicators (dots)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (day.hasNotes) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (day.isSelected) Color.White else AurelTheme.colors.primaryAccent)
                                                )
                                            }
                                            if (day.hasTasks) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (day.isSelected) Color.White else Color(0xFFE08D3C))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Agenda Section Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AurelTheme.spacing.screenHorizontal)
                    .padding(top = 18.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "AGENDA FOR",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted
                )
                Text(
                    text = formattedSelectedDate,
                    style = AurelTheme.typography.headlineMedium,
                    color = AurelTheme.colors.primaryInk
                )
            }
        }

        // Empty state for agenda
        if (uiState.notesOnSelectedDate.isEmpty() && uiState.tasksOnSelectedDate.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = AurelTheme.colors.cardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No notes or tasks on this day",
                            style = AurelTheme.typography.titleMedium,
                            color = AurelTheme.colors.primaryInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A calm day to reflect or start something new.",
                            style = AurelTheme.typography.bodySmall,
                            color = AurelTheme.colors.secondaryMuted
                        )
                    }
                }
            }
        }

        // Tasks on selected date
        if (uiState.tasksOnSelectedDate.isNotEmpty()) {
            item {
                Text(
                    text = "TASKS DUE (${uiState.tasksOnSelectedDate.size})",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted,
                    modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp)
                )
            }
            items(uiState.tasksOnSelectedDate, key = { "task_${it.id}" }) { task ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AurelTheme.colors.cardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AurelTheme.colors.divider)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTask(task.id, it) },
                            colors = CheckboxDefaults.colors(checkedColor = AurelTheme.colors.primaryAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            style = AurelTheme.typography.bodyMedium.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (task.isCompleted) AurelTheme.colors.secondaryMuted else AurelTheme.colors.primaryInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Notes on selected date
        if (uiState.notesOnSelectedDate.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NOTES & JOURNALS (${uiState.notesOnSelectedDate.size})",
                    style = AurelTheme.typography.labelSmall,
                    color = AurelTheme.colors.secondaryMuted,
                    modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 6.dp)
                )
            }
            items(uiState.notesOnSelectedDate, key = { "note_${it.id}" }) { note ->
                AurelCard(
                    modifier = Modifier.padding(horizontal = AurelTheme.spacing.screenHorizontal, vertical = 4.dp),
                    colorHex = note.cardColorHex,
                    onClick = { onNavigateToEditor(note.id) }
                ) {
                    Column {
                        Text(
                            text = if (note.title.isNotBlank()) note.title else "Untitled",
                            style = AurelTheme.typography.titleMedium,
                            color = AurelTheme.colors.primaryInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (note.plainTextSummary.isNotBlank()) note.plainTextSummary else note.content.take(120),
                            style = AurelTheme.typography.bodySmall,
                            color = AurelTheme.colors.secondaryMuted,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
