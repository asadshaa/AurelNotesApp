package com.aurel.notes.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun AurelTagChip(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(AurelTheme.spacing.chipRadius)
    val bgColor = if (isSelected) {
        AurelTheme.colors.primaryAccent.copy(alpha = 0.15f)
    } else {
        if (AurelTheme.colors.isDark) Color(0xFF22202A) else Color(0xFFF1EEE8)
    }
    val textColor = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.primaryInk
    val borderColor = if (isSelected) AurelTheme.colors.primaryAccent else AurelTheme.colors.divider

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(bgColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = AurelTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}
