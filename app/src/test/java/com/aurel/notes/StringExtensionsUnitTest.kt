package com.aurel.notes

import com.aurel.notes.core.util.calculateReadingTimeMinutes
import com.aurel.notes.core.util.calculateWordCount
import com.aurel.notes.core.util.slugify
import com.aurel.notes.core.util.toReadingTimeString
import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsUnitTest {

    @Test
    fun testWordCount_withNormalText() {
        val text = "The quick brown fox jumps over the lazy dog."
        assertEquals(9, text.calculateWordCount())
    }

    @Test
    fun testWordCount_withBlankString() {
        assertEquals(0, "".calculateWordCount())
        assertEquals(0, "   ".calculateWordCount())
    }

    @Test
    fun testReadingTime_smallText_returnsAtLeastOneMinute() {
        val text = "Short note content."
        assertEquals(1, text.calculateReadingTimeMinutes())
    }

    @Test
    fun testSlugify_createsCleanUrlFriendlySlug() {
        val title = "My First Thinking Note! #2026"
        assertEquals("my-first-thinking-note-2026", title.slugify())
    }

    @Test
    fun testReadingTimeString_formatsCorrectly() {
        val text = "Aurel is a local first notes application"
        assertEquals("7 words • 1 min read", text.toReadingTimeString())
    }
}
