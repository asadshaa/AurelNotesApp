package com.aurel.notes

import com.aurel.notes.ui.journal.MoodStreakHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class MoodStreakHelperUnitTest {

    @Test
    fun testEmptyRecordedDays_returnsZero() {
        assertEquals(0, MoodStreakHelper.calculateStreak(emptyList()))
    }

    @Test
    fun testConsecutiveDays_calculatesCorrectStreak() {
        val oneDay = 24 * 60 * 60 * 1000L
        val now = 100 * oneDay
        val days = listOf(now, now - oneDay, now - 2 * oneDay)

        assertEquals(3, MoodStreakHelper.calculateStreak(days, now))
    }

    @Test
    fun testBrokenStreak_returnsZeroIfStale() {
        val oneDay = 24 * 60 * 60 * 1000L
        val now = 100 * oneDay
        val days = listOf(now - 3 * oneDay)

        assertEquals(0, MoodStreakHelper.calculateStreak(days, now))
    }
}
