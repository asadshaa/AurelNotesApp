package com.aurel.notes.data.repository

import com.aurel.notes.core.database.dao.NoteDao
import com.aurel.notes.data.mapper.toDomain
import com.aurel.notes.data.mapper.toEntity
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getActiveNotes(): Flow<List<Note>> =
        noteDao.getActiveNotes().map { list -> list.map { it.toDomain() } }

    override fun getActiveNonVaultNotes(): Flow<List<Note>> =
        noteDao.getActiveNonVaultNotes().map { list -> list.map { it.toDomain() } }

    override fun getVaultNotes(): Flow<List<Note>> =
        noteDao.getVaultNotes().map { list -> list.map { it.toDomain() } }

    override fun getArchivedNotes(): Flow<List<Note>> =
        noteDao.getArchivedNotes().map { list -> list.map { it.toDomain() } }

    override fun getTrashedNotes(): Flow<List<Note>> =
        noteDao.getTrashedNotes().map { list -> list.map { it.toDomain() } }

    override fun getNotesByNotebook(notebookId: Long): Flow<List<Note>> =
        noteDao.getNotesByNotebook(notebookId).map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomain()

    override fun getNoteByIdFlow(id: Long): Flow<Note?> =
        noteDao.getNoteByIdFlow(id).map { it?.toDomain() }

    override suspend fun getNoteByTitle(title: String): Note? =
        noteDao.getNoteByTitle(title)?.toDomain()

    override fun searchNotes(query: String): Flow<List<Note>> {
        val sanitized = query.trim().replace("\"", "\"\"")
        val ftsQuery = "*$sanitized*"
        return noteDao.searchNotes(ftsQuery).map { list -> list.map { it.toDomain() } }
    }

    override fun getPinnedNotes(): Flow<List<Note>> =
        noteDao.getPinnedNotes().map { list -> list.map { it.toDomain() } }

    override fun getRecentNotes(limit: Int): Flow<List<Note>> =
        noteDao.getRecentNotes(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun saveNote(note: Note): Long {
        val entity = note.toEntity().copy(updatedAt = System.currentTimeMillis())
        return noteDao.insertNoteWithFts(entity)
    }

    override suspend fun updateNote(note: Note) {
        val entity = note.toEntity().copy(updatedAt = System.currentTimeMillis())
        noteDao.updateNoteWithFts(entity)
    }

    override suspend fun moveToTrash(id: Long) =
        noteDao.moveToTrash(id)

    override suspend fun moveMultipleToTrash(ids: List<Long>) =
        noteDao.moveMultipleToTrash(ids)

    override suspend fun restoreFromTrash(id: Long) =
        noteDao.restoreFromTrash(id)

    override suspend fun deletePermanently(id: Long) =
        noteDao.deleteNoteById(id)

    override suspend fun emptyTrash() =
        noteDao.emptyTrash()

    override suspend fun setPinned(id: Long, isPinned: Boolean) =
        noteDao.setPinned(id, isPinned)

    override suspend fun setArchived(id: Long, isArchived: Boolean) =
        noteDao.setArchived(id, isArchived)

    override suspend fun setVaultLocked(id: Long, isLocked: Boolean, ivHex: String?) =
        noteDao.setVaultLocked(id, isLocked, ivHex)

    override fun getActiveNoteCount(): Flow<Int> =
        noteDao.getActiveNoteCount()
}
