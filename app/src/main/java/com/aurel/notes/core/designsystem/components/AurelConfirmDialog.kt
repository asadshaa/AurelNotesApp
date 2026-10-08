package com.aurel.notes.core.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun AurelConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = AurelTheme.typography.titleMedium, color = AurelTheme.colors.primaryInk) },
        text = { Text(message, style = AurelTheme.typography.bodyMedium, color = AurelTheme.colors.secondaryMuted) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) AurelTheme.colors.primaryAccent else AurelTheme.colors.primaryAccent
                )
            ) {
                Text(confirmText, style = AurelTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, style = AurelTheme.typography.labelLarge, color = AurelTheme.colors.secondaryMuted)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = AurelTheme.colors.cardSurface
    )
}
