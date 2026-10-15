package com.aurel.notes.domain.model

data class NoteTemplate(
    val id: String,
    val name: String,
    val description: String,
    val defaultTitle: String,
    val initialContent: String,
    val suggestedTags: List<String>
)

object NoteTemplates {
    val all = listOf(
        NoteTemplate(
            id = "meeting_notes",
            name = "Meeting Notes",
            description = "Structured agenda, key discussion points, and action items",
            defaultTitle = "Meeting: ",
            initialContent = """
                # Meeting Notes
                **Date:** 
                **Attendees:** 

                ### 1. Agenda
                - Item 1
                - Item 2

                ### 2. Discussion & Insights
                > Key takeaways and notes during the session.

                ### 3. Action Items
                - [ ] Action item 1
                - [ ] Action item 2
            """.trimIndent(),
            suggestedTags = listOf("meeting", "work")
        ),
        NoteTemplate(
            id = "daily_reflection",
            name = "Daily Reflection",
            description = "Evening review of wins, lessons, and tomorrow's horizon",
            defaultTitle = "Daily Reflection — ",
            initialContent = """
                # Daily Reflection
                
                ### 🌿 What went well today?
                - 

                ### ⚡ What challenged me or taught me something?
                - 

                ### 🙏 Moments of Gratitude
                1. 
                2. 
                3. 

                ### 🎯 Tomorrow's Top 3 Focus Areas
                - [ ] Priority 1
                - [ ] Priority 2
                - [ ] Priority 3
            """.trimIndent(),
            suggestedTags = listOf("journal", "reflection")
        ),
        NoteTemplate(
            id = "project_outline",
            name = "Project Outline",
            description = "Mission, key milestones, open questions, and resources",
            defaultTitle = "Project: ",
            initialContent = """
                # Project Outline
                
                ### 🎯 Mission & Objectives
                What problem does this solve and what does success look like?

                ### 🗺️ Key Milestones
                - [ ] Phase 1: Research & Discovery
                - [ ] Phase 2: Core Architecture & Prototype
                - [ ] Phase 3: Polish & Verification

                ### ❓ Open Questions
                - 

                ### 🔗 Related Links & References
                - [[Architecture Blueprint]]
            """.trimIndent(),
            suggestedTags = listOf("project", "planning")
        ),
        NoteTemplate(
            id = "reading_log",
            name = "Reading Log",
            description = "Book or article notes, key quotes, and core takeaways",
            defaultTitle = "Notes on: ",
            initialContent = """
                # Reading Notes
                **Title:** 
                **Author:** 
                **Rating:** ⭐⭐⭐⭐⭐

                ### 💡 Core Thesis
                What is the main argument or idea?

                ### 📌 Favorite Quotes
                > "Quote 1"

                ### 🔑 Actionable Insights
                - [ ] Idea to implement in daily life
            """.trimIndent(),
            suggestedTags = listOf("reading", "learning", "books")
        )
    )
}
