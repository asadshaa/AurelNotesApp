package com.aurel.notes.core.markdown

object MarkdownTableHelper {
    fun generateTableSnippet(rows: Int = 3, cols: Int = 3): String {
        val safeRows = rows.coerceIn(1, 10)
        val safeCols = cols.coerceIn(1, 6)

        val header = (1..safeCols).joinToString(" | ", prefix = "| ", postfix = " |") { "Header $it" }
        val separator = (1..safeCols).joinToString(" | ", prefix = "| ", postfix = " |") { "---" }
        val bodyRows = (1..safeRows).map { r ->
            (1..safeCols).joinToString(" | ", prefix = "| ", postfix = " |") { c -> "Cell $r-$c" }
        }

        return (listOf(header, separator) + bodyRows).joinToString("\n")
    }

    fun isTableLine(line: String): Boolean {
        val trimmed = line.trim()
        return trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.contains("|")
    }
}
