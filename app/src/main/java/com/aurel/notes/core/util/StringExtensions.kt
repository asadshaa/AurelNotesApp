package com.aurel.notes.core.util

fun String.calculateWordCount(): Int {
    if (this.isBlank()) return 0
    return this.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
}

fun String.calculateReadingTimeMinutes(wpm: Int = 200): Int {
    val words = calculateWordCount()
    if (words == 0) return 1
    val mins = (words + wpm - 1) / wpm
    return mins.coerceAtLeast(1)
}

fun String.toReadingTimeString(): String {
    val mins = calculateReadingTimeMinutes()
    val words = calculateWordCount()
    return "$words words • $mins min read"
}

fun String.slugify(): String {
    return this.lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .trim('-')
}
