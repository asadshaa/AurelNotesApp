package com.aurel.notes.core.designsystem

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AurelSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val screenHorizontal: Dp = 20.dp,
    val cardPadding: Dp = 16.dp,
    val cardRadius: Dp = 18.dp,
    val chipRadius: Dp = 12.dp
)

val LocalAurelSpacing = staticCompositionLocalOf { AurelSpacing() }

@Immutable
data class AurelMotion(
    val briskDuration: Int = 180,
    val normalDuration: Int = 240,
    val reducedMotion: Boolean = false
) {
    fun <T> briskTween() = tween<T>(
        durationMillis = if (reducedMotion) 0 else briskDuration,
        easing = FastOutSlowInEasing
    )
}

val LocalAurelMotion = staticCompositionLocalOf { AurelMotion() }

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED
}

@Composable
fun AurelTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    fontScale: Float = 1.0f,
    lineHeightMultiplier: Float = 1.4f,
    editorFont: EditorFontPreference = EditorFontPreference.SERIF,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
    val isAmoled = themeMode == ThemeMode.AMOLED

    val colors = when {
        isAmoled -> AmoledAurelColors
        isDark -> DarkAurelColors
        else -> LightAurelColors
    }

    val typography = createAurelTypography(
        fontScale = fontScale,
        lineHeightMultiplier = lineHeightMultiplier,
        editorFont = editorFont
    )
    val spacing = AurelSpacing()
    val motion = AurelMotion(reducedMotion = reducedMotion)

    // Bridge into Material3 theme for foundational components
    val m3ColorScheme = if (isDark) {
        darkColorScheme(
            primary = colors.primaryAccent,
            background = colors.background,
            surface = colors.surface,
            onPrimary = colors.surface,
            onBackground = colors.primaryInk,
            onSurface = colors.primaryInk,
            outline = colors.divider
        )
    } else {
        lightColorScheme(
            primary = colors.primaryAccent,
            background = colors.background,
            surface = colors.surface,
            onPrimary = CardSurfaceLight,
            onBackground = colors.primaryInk,
            onSurface = colors.primaryInk,
            outline = colors.divider
        )
    }

    CompositionLocalProvider(
        LocalAurelColors provides colors,
        LocalAurelTypography provides typography,
        LocalAurelSpacing provides spacing,
        LocalAurelMotion provides motion
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            shapes = MaterialTheme.shapes.copy(
                small = RoundedCornerShape(10.dp),
                medium = RoundedCornerShape(16.dp),
                large = RoundedCornerShape(22.dp)
            ),
            content = content
        )
    }
}

object AurelTheme {
    val colors: AurelColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAurelColors.current

    val typography: AurelTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAurelTypography.current

    val spacing: AurelSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAurelSpacing.current

    val motion: AurelMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalAurelMotion.current
}
