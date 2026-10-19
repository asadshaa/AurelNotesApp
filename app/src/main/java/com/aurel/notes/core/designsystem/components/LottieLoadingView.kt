package com.aurel.notes.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun LottieLoadingView(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    message: String? = null
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("loading.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(size)
        )

        if (!message.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = AurelTheme.typography.bodyMedium.copy(
                    color = AurelTheme.colors.secondaryMuted,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FullScreenLottieLoader(
    modifier: Modifier = Modifier,
    message: String = "Loading your thoughts..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieLoadingView(
            size = 140.dp,
            message = message
        )
    }
}
