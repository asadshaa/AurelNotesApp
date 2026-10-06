package com.aurel.notes

import com.aurel.notes.core.util.DateFormatters
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormattersUnitTest {

    @Test
    fun testRelativeTime_justNow() {
        val now = 1000000L
        val timestamp = now - 30 * 1000
        assertEquals("Just now", DateFormatters.formatRelativeTime(timestamp, now))
    }

    @Test
    fun testRelativeTime_minutesAgo() {
        val now = 1000000L
        val timestamp = now - 5 * 60 * 1000
        assertEquals("5m ago", DateFormatters.formatRelativeTime(timestamp, now))
    }

    @Test
    fun testRelativeTime_hoursAgo() {
        val now = 10000000L
        val timestamp = now - 3 * 3600 * 1000
        assertEquals("3h ago", DateFormatters.formatRelativeTime(timestamp, now))
    }

    @Test
    fun testRelativeTime_yesterday() {
        val now = 100000000L
        val timestamp = now - 24 * 3600 * 1000
        assertEquals("Yesterday", DateFormatters.formatRelativeTime(timestamp, now))
    }
}
