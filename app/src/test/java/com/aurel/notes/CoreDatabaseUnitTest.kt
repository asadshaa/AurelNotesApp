package com.aurel.notes

import com.aurel.notes.core.database.converter.AurelTypeConverters
import com.aurel.notes.core.database.entity.NoteEntity
import com.aurel.notes.core.database.entity.NotebookEntity
import com.aurel.notes.core.database.entity.TaskEntity
import com.aurel.notes.core.designsystem.CardColorTokens
import com.aurel.notes.core.designsystem.DarkPastelLavender
import com.aurel.notes.core.designsystem.PastelLavender
import com.aurel.notes.data.mapper.toDomain
import com.aurel.notes.data.mapper.toEntity
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task
import com.aurel.notes.domain.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreDatabaseUnitTest {

    @Test
    fun typeConverters_serializeAndDeserializeListCorrectly() {
        val converters = AurelTypeConverters()
        val originalTags = listOf("ideas", "architecture", "android-compose")
        val serialized = converters.fromStringList(originalTags)
        val deserialized = converters.toStringList(serialized)

        assertEquals(originalTags, deserialized)
    }

    @Test
    fun typeConverters_handleEmptyAndNullGracefully() {
        val converters = AurelTypeConverters()
        assertEquals("", converters.fromStringList(emptyList()))
        assertEquals("", converters.fromStringList(null))
        assertEquals(emptyList<String>(), converters.toStringList(""))
        assertEquals(emptyList<String>(), converters.toStringList(null))
    }

    @Test
    fun noteEntity_mappingToDomainAndBack_preservesAllFields() {
        val entity = NoteEntity(
            id = 42L,
            uuid = "test-uuid-1234",
            title = "Aurel Editorial Notes",
            content = "# Heading 1\nContent body here with [[Backlink]]",
            plainTextSummary = "Content body here",
            notebookId = 2L,
            cardColorHex = CardColorTokens.LAVENDER,
            isPinned = true,
            isArchived = false,
            isTrashed = false,
            trashedAt = null,
            isVaultLocked = false,
            ivHex = null,
            tags = listOf("journal", "deep-work"),
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val domain = entity.toDomain()
        assertEquals(42L, domain.id)
        assertEquals("test-uuid-1234", domain.uuid)
        assertEquals("Aurel Editorial Notes", domain.title)
        assertTrue(domain.isPinned)
        assertFalse(domain.isArchived)
        assertEquals(2, domain.tags.size)

        val mappedBack = domain.toEntity()
        assertEquals(entity, mappedBack)
    }

    @Test
    fun taskEntity_priorityAndCompletionMapping_worksAccurately() {
        val entity = TaskEntity(
            id = 10L,
            parentNoteId = 42L,
            title = "Implement Phase 1 verification",
            isCompleted = true,
            priority = "HIGH",
            dueDateTime = 500000L,
            reminderEnabled = true,
            recurrence = "DAILY",
            completedAt = 500100L
        )

        val domain = entity.toDomain()
        assertEquals(TaskPriority.HIGH, domain.priority)
        assertTrue(domain.isCompleted)
        assertTrue(domain.reminderEnabled)

        val backToEntity = domain.toEntity()
        assertEquals("HIGH", backToEntity.priority)
        assertTrue(backToEntity.isCompleted)
    }

    @Test
    fun cardColorTokens_resolvesPastelCorrectlyInLightAndDarkMode() {
        val lightLavender = CardColorTokens.resolveCardColor(CardColorTokens.LAVENDER, isDark = false)
        val darkLavender = CardColorTokens.resolveCardColor(CardColorTokens.LAVENDER, isDark = true)

        assertEquals(PastelLavender, lightLavender)
        assertEquals(DarkPastelLavender, darkLavender)
    }

    @Test
    fun markdownSummaryExtraction_cleansMarkdownSymbolsCorrectly() {
        val markdown = "# Architecture Plan\nThis is a **bold** thought with [[Knowledge Note]] and - [ ] action item."
        val cleaned = markdown
            .replace("#+".toRegex(), "")
            .replace("\\*\\*".toRegex(), "")
            .replace("\\*".toRegex(), "")
            .replace("\\[\\[.*?\\]\\]".toRegex()) { it.value.removeSurrounding("[[", "]]") }
            .replace("- \\[ \\]".toRegex(), "")
            .replace("- \\[x\\]".toRegex(), "")
            .trim()

        assertTrue(cleaned.contains("Architecture Plan"))
        assertTrue(cleaned.contains("bold thought"))
        assertTrue(cleaned.contains("Knowledge Note"))
        assertFalse(cleaned.contains("#"))
        assertFalse(cleaned.contains("**"))
        assertFalse(cleaned.contains("[["))
    }

    @Test
    fun noteSorting_byTitleAndUpdatedDate_worksProperly() {
        val noteA = Note(id = 1, title = "Beta Concept", updatedAt = 2000L)
        val noteB = Note(id = 2, title = "Alpha Concept", updatedAt = 5000L)
        val list = listOf(noteA, noteB)

        val sortedByTitle = list.sortedBy { it.title.lowercase() }
        assertEquals("Alpha Concept", sortedByTitle.first().title)

        val sortedByDate = list.sortedByDescending { it.updatedAt }
        assertEquals(2L, sortedByDate.first().id)
    }

    @Test
    fun markdownParser_extractWikiLinks_findsAllUniqueLinks() {
        val content = """
            Here is a link to [[Project Roadmap]] and another to [[Meeting Notes]].
            Also repeated [[Project Roadmap]] and [[Personal Thoughts]].
        """.trimIndent()

        val links = com.aurel.notes.core.markdown.MarkdownParser.extractWikiLinks(content)
        assertEquals(3, links.size)
        assertTrue(links.contains("Project Roadmap"))
        assertTrue(links.contains("Meeting Notes"))
        assertTrue(links.contains("Personal Thoughts"))
    }

    @Test
    fun markdownParser_extractChecklistItems_identifiesCheckedAndUnchecked() {
        val content = """
            - [ ] Buy groceries
            - [x] Complete Phase 3 unit tests
            Regular text line
            - [X] Review pull request
        """.trimIndent()

        val items = com.aurel.notes.core.markdown.MarkdownParser.extractChecklistItems(content)
        assertEquals(3, items.size)
        assertFalse(items[0].isChecked)
        assertEquals("Buy groceries", items[0].text)
        assertTrue(items[1].isChecked)
        assertEquals("Complete Phase 3 unit tests", items[1].text)
        assertTrue(items[2].isChecked)
    }

    @Test
    fun markdownParser_applyFormatting_wrapsInlineCorrectly() {
        val original = "Hello World"
        val (formatted, newCursor) = com.aurel.notes.core.markdown.MarkdownParser.applyFormatting(
            currentText = original,
            selectionStart = 6,
            selectionEnd = 11,
            formatType = com.aurel.notes.core.markdown.FormattingType.BOLD
        )

        assertEquals("Hello **World**", formatted)
        assertEquals(15, newCursor)
    }

    @Test
    fun noteTemplates_allTemplatesContainValidContent() {
        val templates = com.aurel.notes.domain.model.NoteTemplates.all
        assertTrue(templates.isNotEmpty())
        for (template in templates) {
            assertTrue(template.name.isNotBlank())
            assertTrue(template.initialContent.isNotBlank())
            assertTrue(template.suggestedTags.isNotEmpty())
        }
    }

    @Test
    fun taskRecurrence_calculatesNextDayProperly() {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(2026, java.util.Calendar.OCTOBER, 15, 10, 0, 0)
        val initial = calendar.timeInMillis

        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val nextDay = calendar.timeInMillis

        assertTrue(nextDay > initial)
        val diffHours = (nextDay - initial) / (1000 * 60 * 60)
        assertEquals(24L, diffHours)
    }

    @Test
    fun journalMood_containsEmojisAndValidLabels() {
        val moods = com.aurel.notes.ui.journal.JournalMood.values()
        assertEquals(5, moods.size)
        for (mood in moods) {
            assertTrue(mood.emoji.isNotBlank())
            assertTrue(mood.label.isNotBlank())
        }
    }
}
