package com.aurel.notes.domain.repository

import com.aurel.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getActiveNotes(): Flow<List<Note>>
    fun getActiveNonVaultNotes(): Flow<List<Note>>
    fun getVaultNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    fun getNotesByNotebook(notebookId: Long): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    fun getNoteByIdFlow(id: Long): Flow<Note?>
    suspend fun getNoteByTitle(title: String): Note?
    fun searchNotes(query: String): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getRecentNotes(limit: Int = 10): Flow<List<Note>>
    suspend fun saveNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun moveToTrash(id: Long)
    suspend fun moveMultipleToTrash(ids: List<Long>)
    suspend fun restoreFromTrash(id: Long)
    suspend fun deletePermanently(id: Long)
    suspend fun emptyTrash()
    suspend fun setPinned(id: Long, isPinned: Boolean)
    suspend fun setArchived(id: Long, isArchived: Boolean)
    suspend fun setVaultLocked(id: Long, isLocked: Boolean, ivHex: String?)
    fun getActiveNoteCount(): Flow<Int>
}
