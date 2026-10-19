package com.aurel.notes.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.aurel.notes.core.designsystem.AurelTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class VividParticle(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val baseRadius: Float,
    val speed: Float,
    val horizontalAmplitude: Float,
    val phase: Float,
    val baseAlpha: Float,
    val colorIndex: Int
)

@Composable
fun FloatingParticlesBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 45,
    reducedMotion: Boolean = false,
    primaryColor: Color = AurelTheme.colors.primaryAccent,
    secondaryColor: Color = AurelTheme.colors.cardSurface
) {
    // Vibrant harmonious palette for floating particles
    val isDark = AurelTheme.colors.isDark
    val particleColors = remember(isDark, primaryColor) {
        if (isDark) {
            listOf(
                primaryColor,                        // Accent Violet/Indigo
                Color(0xFF38BDF8),                   // Vivid Cyan / Sky
                Color(0xFFA78BFA),                   // Glowing Purple
                Color(0xFFF472B6),                   // Soft Neon Pink
                Color(0xFFFBBF24)                    // Warm Gold
            )
        } else {
            listOf(
                primaryColor,                        // Editorial Indigo
                Color(0xFF0284C7),                   // Deep Sky Blue
                Color(0xFF7C3AED),                   // Electric Purple
                Color(0xFFE11D48),                   // Warm Coral
                Color(0xFFD97706)                    // Amber
            )
        }
    }

    val particles = remember(particleCount) {
        val rand = Random(1337)
        List(particleCount) { index ->
            VividParticle(
                initialXRatio = rand.nextFloat(),
                initialYRatio = rand.nextFloat(),
                baseRadius = rand.nextFloat() * 4.5f + 2.0f,     // 2.0dp to 6.5dp - prominent dots
                speed = rand.nextFloat() * 0.65f + 0.25f,         // lively drifting speed
                horizontalAmplitude = rand.nextFloat() * 32f + 12f,
                phase = rand.nextFloat() * 6.28f,
                baseAlpha = rand.nextFloat() * 0.45f + 0.30f,    // 0.30 to 0.75 - clearly visible!
                colorIndex = index % 5
            )
        }
    }

    if (reducedMotion) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            particles.forEach { p ->
                val x = p.initialXRatio * width
                val y = p.initialYRatio * height
                val color = particleColors[p.colorIndex]
                drawCircle(
                    color = color.copy(alpha = p.baseAlpha * 0.4f),
                    radius = p.baseRadius,
                    center = Offset(x, y)
                )
            }
        }
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vividParticlesTransition")
    val timeProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timeProgression"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return@Canvas

        particles.forEach { p ->
            // Continuous upward drift with wrap-around
            val rawY = (p.initialYRatio * height - (timeProgression * p.speed * 25f)) % height
            val currentY = if (rawY < 0f) rawY + height else rawY

            // Sinusoidal floating wave on X-axis
            val timeOffset = (timeProgression * 0.08f) + p.phase
            val currentX = (p.initialXRatio * width + sin(timeOffset) * p.horizontalAmplitude).let { x ->
                ((x % width) + width) % width
            }

            // Pulsing luminescence and subtle sizing
            val pulse = (sin(timeOffset * 1.2f) * 0.15f).toFloat()
            val finalAlpha = (p.baseAlpha + pulse).coerceIn(0.18f, 0.85f)
            val currentRadius = p.baseRadius + (cos(timeOffset * 0.9f) * 0.8f).toFloat()

            val dotColor = particleColors[p.colorIndex]

            // Outer glowing aura for prominent particles
            drawCircle(
                color = dotColor.copy(alpha = finalAlpha * 0.35f),
                radius = currentRadius * 2.5f,
                center = Offset(currentX, currentY)
            )

            // Inner solid luminous core
            drawCircle(
                color = dotColor.copy(alpha = finalAlpha),
                radius = currentRadius,
                center = Offset(currentX, currentY)
            )
        }
    }
}
