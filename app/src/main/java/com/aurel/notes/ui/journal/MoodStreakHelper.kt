package com.aurel.notes.ui.journal

object MoodStreakHelper {
    fun calculateStreak(recordedDays: List<Long>, now: Long = System.currentTimeMillis()): Int {
        if (recordedDays.isEmpty()) return 0
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val sortedDays = recordedDays.map { it / oneDayMillis }.distinct().sortedDescending()
        val currentDay = now / oneDayMillis

        if (sortedDays.first() < currentDay - 1) {
            return 0
        }

        var streak = 0
        var expected = if (sortedDays.first() == currentDay) currentDay else currentDay - 1

        for (day in sortedDays) {
            if (day == expected) {
                streak++
                expected--
            } else if (day < expected) {
                break
            }
        }
        return streak
    }
}
