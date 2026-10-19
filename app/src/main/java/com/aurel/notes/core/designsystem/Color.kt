package com.aurel.notes.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light (Warm Editorial Paper)
val WarmBackgroundLight = Color(0xFFF8F6F1)
val CardSurfaceLight = Color(0xFFFFFFFF)
val PrimaryInkLight = Color(0xFF25232B)
val SecondaryMutedLight = Color(0xFF777481)
val PrimaryAccentLight = Color(0xFF7564D8)
val DividerLight = Color(0xFFE9E5E0)

// Dark Editorial
val BackgroundDark = Color(0xFF141318)
val CardSurfaceDark = Color(0xFF1E1C24)
val PrimaryInkDark = Color(0xFFEDE9F5)
val SecondaryMutedDark = Color(0xFFA29DB1)
val PrimaryAccentDark = Color(0xFF9C8DF6)
val DividerDark = Color(0xFF2E2B38)

// True AMOLED
val BackgroundAmoled = Color(0xFF000000)
val CardSurfaceAmoled = Color(0xFF121116)
val DividerAmoled = Color(0xFF22202A)

// Card Pastel Tokens (Light & Desaturated Dark counterparts)
val PastelLavender = Color(0xFFEAE5FF)
val PastelSage = Color(0xFFE1EDDF)
val PastelPeach = Color(0xFFF9E4D7)
val PastelSand = Color(0xFFF5EBE1)
val PastelMistBlue = Color(0xFFE3EFF8)
val PastelPaper = Color(0xFFFFFFFF)

// Desaturated Dark Pastels for high contrast
val DarkPastelLavender = Color(0xFF2B263C)
val DarkPastelSage = Color(0xFF243026)
val DarkPastelPeach = Color(0xFF3B2720)
val DarkPastelSand = Color(0xFF362E27)
val DarkPastelMistBlue = Color(0xFF202B38)
val DarkPastelPaper = Color(0xFF1E1C24)

// Functional Colors
val PriorityHigh = Color(0xFFD94848)
val PriorityMedium = Color(0xFFE08D3C)
val PriorityLow = Color(0xFF4A9E68)
val SuccessGreen = Color(0xFF439C5E)

@Immutable
data class AurelColors(
    val background: Color,
    val surface: Color,
    val cardSurface: Color,
    val primaryInk: Color,
    val secondaryMuted: Color,
    val primaryAccent: Color,
    val divider: Color,
    val isDark: Boolean,
    val isAmoled: Boolean = false,
    // Pastel Card palette
    val pastelLavender: Color = PastelLavender,
    val pastelSage: Color = PastelSage,
    val pastelPeach: Color = PastelPeach,
    val pastelSand: Color = PastelSand,
    val pastelMistBlue: Color = PastelMistBlue,
    val pastelDefault: Color = PastelPaper
)

val LightAurelColors = AurelColors(
    background = WarmBackgroundLight,
    surface = CardSurfaceLight,
    cardSurface = CardSurfaceLight,
    primaryInk = PrimaryInkLight,
    secondaryMuted = SecondaryMutedLight,
    primaryAccent = PrimaryAccentLight,
    divider = DividerLight,
    isDark = false,
    pastelLavender = PastelLavender,
    pastelSage = PastelSage,
    pastelPeach = PastelPeach,
    pastelSand = PastelSand,
    pastelMistBlue = PastelMistBlue,
    pastelDefault = PastelPaper
)

val DarkAurelColors = AurelColors(
    background = BackgroundDark,
    surface = CardSurfaceDark,
    cardSurface = CardSurfaceDark,
    primaryInk = PrimaryInkDark,
    secondaryMuted = SecondaryMutedDark,
    primaryAccent = PrimaryAccentDark,
    divider = DividerDark,
    isDark = true,
    pastelLavender = DarkPastelLavender,
    pastelSage = DarkPastelSage,
    pastelPeach = DarkPastelPeach,
    pastelSand = DarkPastelSand,
    pastelMistBlue = DarkPastelMistBlue,
    pastelDefault = DarkPastelPaper
)

val AmoledAurelColors = AurelColors(
    background = BackgroundAmoled,
    surface = CardSurfaceAmoled,
    cardSurface = CardSurfaceAmoled,
    primaryInk = PrimaryInkDark,
    secondaryMuted = SecondaryMutedDark,
    primaryAccent = PrimaryAccentDark,
    divider = DividerAmoled,
    isDark = true,
    isAmoled = true,
    pastelLavender = DarkPastelLavender,
    pastelSage = DarkPastelSage,
    pastelPeach = DarkPastelPeach,
    pastelSand = DarkPastelSand,
    pastelMistBlue = DarkPastelMistBlue,
    pastelDefault = CardSurfaceAmoled
)

val LocalAurelColors = staticCompositionLocalOf { LightAurelColors }

object CardColorTokens {
    const val LAVENDER = "#EAE5FF"
    const val SAGE = "#E1EDDF"
    const val PEACH = "#F9E4D7"
    const val SAND = "#F5EBE1"
    const val MIST_BLUE = "#E3EFF8"
    const val DEFAULT = "#FFFFFF"

    val all = listOf(DEFAULT, LAVENDER, SAGE, PEACH, SAND, MIST_BLUE)

    fun resolveCardColor(colorHex: String?, isDark: Boolean): Color {
        return when (colorHex) {
            LAVENDER -> if (isDark) DarkPastelLavender else PastelLavender
            SAGE -> if (isDark) DarkPastelSage else PastelSage
            PEACH -> if (isDark) DarkPastelPeach else PastelPeach
            SAND -> if (isDark) DarkPastelSand else PastelSand
            MIST_BLUE -> if (isDark) DarkPastelMistBlue else PastelMistBlue
            else -> if (isDark) CardSurfaceDark else CardSurfaceLight
        }
    }
}
