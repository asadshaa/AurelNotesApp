package com.aurel.notes.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.CardColorTokens

@Composable
fun AurelCard(
    modifier: Modifier = Modifier,
    colorHex: String? = null,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(AurelTheme.spacing.cardRadius),
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = AurelTheme.colors.isDark
    val resolvedBg = backgroundColor ?: CardColorTokens.resolveCardColor(colorHex, isDark)
    val borderColor = if (isDark) AurelTheme.colors.divider.copy(alpha = 0.5f) else AurelTheme.colors.divider

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = resolvedBg,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(AurelTheme.spacing.cardPadding),
            content = content
        )
    }
}
