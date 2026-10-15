package com.aurel.notes.core.markdown

object MarkdownParser {

    private val WIKI_LINK_REGEX = "\\[\\[([^\\]]+)\\]\\]".toRegex()
    private val CHECKLIST_REGEX = "^\\s*-\\s*\\[([ xX])\\]\\s*(.*)$".toRegex()

    fun extractWikiLinks(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        return WIKI_LINK_REGEX.findAll(content)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }

    data class ExtractedChecklistItem(
        val isChecked: Boolean,
        val text: String,
        val fullLine: String
    )

    fun extractChecklistItems(content: String): List<ExtractedChecklistItem> {
        if (content.isBlank()) return emptyList()
        val items = mutableListOf<ExtractedChecklistItem>()
        content.lines().forEach { line ->
            val match = CHECKLIST_REGEX.find(line)
            if (match != null) {
                val isChecked = match.groupValues[1].trim().lowercase() == "x"
                val text = match.groupValues[2].trim()
                items.add(ExtractedChecklistItem(isChecked, text, line))
            }
        }
        return items
    }

    fun applyFormatting(
        currentText: String,
        selectionStart: Int,
        selectionEnd: Int,
        formatType: FormattingType
    ): Pair<String, Int> {
        val start = selectionStart.coerceIn(0, currentText.length)
        val end = selectionEnd.coerceIn(start, currentText.length)
        val selectedText = currentText.substring(start, end)

        val (prefix, suffix) = when (formatType) {
            FormattingType.BOLD -> Pair("**", "**")
            FormattingType.ITALIC -> Pair("*", "*")
            FormattingType.STRIKETHROUGH -> Pair("~~", "~~")
            FormattingType.INLINE_CODE -> Pair("`", "`")
            FormattingType.H1 -> Pair("# ", "")
            FormattingType.H2 -> Pair("## ", "")
            FormattingType.H3 -> Pair("### ", "")
            FormattingType.QUOTE -> Pair("> ", "")
            FormattingType.BULLET_LIST -> Pair("- ", "")
            FormattingType.NUMBERED_LIST -> Pair("1. ", "")
            FormattingType.CHECKLIST -> Pair("- [ ] ", "")
            FormattingType.CODE_BLOCK -> Pair("```\n", "\n```")
            FormattingType.DIVIDER -> Pair("\n---\n", "")
            FormattingType.WIKI_LINK -> Pair("[[", "]]")
        }

        val replacement = if (formatType.isLinePrefix) {
            val beforeSelection = currentText.substring(0, start)
            val afterSelection = currentText.substring(start)
            val needsLeadingNewline = beforeSelection.isNotEmpty() && !beforeSelection.endsWith("\n")
            val lead = if (needsLeadingNewline) "\n" else ""
            val newText = beforeSelection + lead + prefix + selectedText + afterSelection
            return Pair(newText, (beforeSelection + lead + prefix + selectedText).length)
        } else {
            prefix + (if (selectedText.isEmpty()) "text" else selectedText) + suffix
        }

        val newText = currentText.substring(0, start) + replacement + currentText.substring(end)
        val newCursor = start + replacement.length
        return Pair(newText, newCursor)
    }
}

enum class FormattingType(val isLinePrefix: Boolean = false) {
    BOLD,
    ITALIC,
    STRIKETHROUGH,
    INLINE_CODE,
    H1(isLinePrefix = true),
    H2(isLinePrefix = true),
    H3(isLinePrefix = true),
    QUOTE(isLinePrefix = true),
    BULLET_LIST(isLinePrefix = true),
    NUMBERED_LIST(isLinePrefix = true),
    CHECKLIST(isLinePrefix = true),
    CODE_BLOCK(isLinePrefix = true),
    DIVIDER(isLinePrefix = true),
    WIKI_LINK
}
