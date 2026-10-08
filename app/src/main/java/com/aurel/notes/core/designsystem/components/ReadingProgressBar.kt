package com.aurel.notes.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun ReadingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "ReadingProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(AurelTheme.colors.divider)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = animatedProgress)
                .height(3.dp)
                .background(AurelTheme.colors.primaryAccent)
        )
    }
}
