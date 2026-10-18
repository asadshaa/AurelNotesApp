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
            body = "encrypted payload",
            isVault = true,
            isPinned = false,
            isArchived = false,
            isTrashed = false,
            colorHex = null,
            notebookId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        assertTrue(note.isVault)
        assertFalse(note.isPinned)
    }

    @Test
    fun testNote_activeNote_notArchivedOrTrashed() {
        val note = Note(
            id = 2L,
            title = "My Thoughts",
            body = "Standard note body",
            isVault = false,
            isPinned = true,
            isArchived = false,
            isTrashed = false,
            colorHex = null,
            notebookId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        assertTrue(note.isPinned)
        assertFalse(note.isArchived)
        assertFalse(note.isTrashed)
    }
}
