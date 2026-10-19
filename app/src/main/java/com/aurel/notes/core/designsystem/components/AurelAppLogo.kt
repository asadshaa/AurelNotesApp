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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aurel.notes.core.designsystem.AurelTheme

@Composable
fun AurelAppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    showText: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("notepad.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.85f
    )

    Row(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.28f))
                .background(AurelTheme.colors.primaryAccent.copy(alpha = 0.12f))
                .border(1.dp, AurelTheme.colors.primaryAccent.copy(alpha = 0.25f), RoundedCornerShape(size * 0.28f)),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(size * 0.95f)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Aurel",
                style = AurelTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AurelTheme.colors.primaryInk,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
