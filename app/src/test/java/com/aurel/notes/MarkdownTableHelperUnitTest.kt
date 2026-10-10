package com.aurel.notes

import com.aurel.notes.core.markdown.MarkdownTableHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownTableHelperUnitTest {

    @Test
    fun testGenerateTableSnippet_createsCorrectDimensions() {
        val snippet = MarkdownTableHelper.generateTableSnippet(rows = 2, cols = 3)
        val lines = snippet.lines()
        // Header + Separator + 2 body rows = 4 lines
        assertEquals(4, lines.size)
        assertTrue(lines[0].contains("Header 1"))
        assertTrue(lines[1].contains("---"))
    }

    @Test
    fun testIsTableLine_recognizesValidMarkdownTableSyntax() {
        assertTrue(MarkdownTableHelper.isTableLine("| A | B | C |"))
        assertTrue(MarkdownTableHelper.isTableLine("| --- | --- |"))
    }
}
