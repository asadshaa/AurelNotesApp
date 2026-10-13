package com.aurel.notes.ui.notes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.util.DateFormatters

@Composable
fun NoteCardHeader(
    title: String,
    isPinned: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.ifBlank { "Untitled Note" },
            style = AurelTheme.typography.titleMedium,
            color = AurelTheme.colors.primaryInk,
            modifier = Modifier.weight(1f)
        )
        if (isPinned) {
            Icon(
                imageVector = Icons.Filled.PushPin,
                contentDescription = "Pinned",
                tint = AurelTheme.colors.primaryAccent,
                modifier = Modifier.weight(0.15f, fill = false)
            )
        }
    }
}

@Composable
fun NoteCardFooter(
    updatedAt: Long,
    wordCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = DateFormatters.formatRelativeTime(updatedAt),
            style = AurelTheme.typography.labelSmall,
            color = AurelTheme.colors.secondaryMuted
        )
        Text(
            text = "$wordCount words",
            style = AurelTheme.typography.labelSmall,
            color = AurelTheme.colors.secondaryMuted
        )
    }
}
