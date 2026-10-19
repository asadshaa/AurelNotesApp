package com.aurel.notes

import com.aurel.notes.domain.model.Note
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteDomainModelUnitTest {

    @Test
    fun testNote_isVaultLocked_whenVaultFlagIsTrue() {
        val note = Note(
            id = 1L,
            title = "Secret Passwords",
            content = "encrypted payload",
            isVaultLocked = true,
            isPinned = false,
            isArchived = false,
            isTrashed = false,
            cardColorHex = "#FFFFFF",
            notebookId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        assertTrue(note.isVaultLocked)
        assertFalse(note.isPinned)
    }

    @Test
    fun testNote_activeNote_notArchivedOrTrashed() {
        val note = Note(
            id = 2L,
            title = "My Thoughts",
            content = "Standard note body",
            isVaultLocked = false,
            isPinned = true,
            isArchived = false,
            isTrashed = false,
            cardColorHex = "#FFFFFF",
            notebookId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        assertTrue(note.isPinned)
        assertFalse(note.isArchived)
        assertFalse(note.isTrashed)
    }
}
