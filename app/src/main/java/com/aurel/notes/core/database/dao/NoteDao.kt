package com.aurel.notes.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aurel.notes.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun getActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND is_vault_locked = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun getActiveNonVaultNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND is_vault_locked = 1 ORDER BY updated_at DESC")
    fun getVaultNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 1 ORDER BY updated_at DESC")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 1 ORDER BY trashed_at DESC")
    fun getTrashedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND notebook_id = :notebookId ORDER BY is_pinned DESC, updated_at DESC")
    fun getNotesByNotebook(notebookId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteByIdFlow(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE uuid = :uuid LIMIT 1")
    suspend fun getNoteByUuid(uuid: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE LOWER(title) = LOWER(:title) AND is_trashed = 0 LIMIT 1")
    suspend fun getNoteByTitle(title: String): NoteEntity?

    @Query("""
        SELECT notes.* FROM notes
        JOIN notes_fts ON notes.id = notes_fts.rowid
        WHERE notes_fts MATCH :query AND notes.is_trashed = 0
        ORDER BY notes.is_pinned DESC, notes.updated_at DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_pinned = 1 AND is_trashed = 0 AND is_archived = 0 ORDER BY updated_at DESC LIMIT 5")
    fun getPinnedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 ORDER BY updated_at DESC LIMIT :limit")
    fun getRecentNotes(limit: Int = 10): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotesByIds(ids: List<Long>)

    @Query("UPDATE notes SET is_trashed = 1, trashed_at = :trashedAt, is_pinned = 0 WHERE id = :id")
    suspend fun moveToTrash(id: Long, trashedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET is_trashed = 1, trashed_at = :trashedAt, is_pinned = 0 WHERE id IN (:ids)")
    suspend fun moveMultipleToTrash(ids: List<Long>, trashedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET is_trashed = 0, trashed_at = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("UPDATE notes SET is_trashed = 0, trashed_at = NULL WHERE id IN (:ids)")
    suspend fun restoreMultipleFromTrash(ids: List<Long>)

    @Query("DELETE FROM notes WHERE is_trashed = 1 AND trashed_at IS NOT NULL AND trashed_at < :expirationTime")
    suspend fun purgeExpiredTrash(expirationTime: Long): Int

    @Query("DELETE FROM notes WHERE is_trashed = 1")
    suspend fun emptyTrash()

    @Query("UPDATE notes SET is_pinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("UPDATE notes SET is_archived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: Long, isArchived: Boolean)

    @Query("UPDATE notes SET is_vault_locked = :isLocked, iv_hex = :ivHex WHERE id = :id")
    suspend fun setVaultLocked(id: Long, isLocked: Boolean, ivHex: String?)

    // FTS sync queries
    @Query("INSERT OR REPLACE INTO notes_fts(rowid, title, content, tags) VALUES (:rowid, :title, :content, :tags)")
    suspend fun upsertFts(rowid: Long, title: String, content: String, tags: String)

    @Query("DELETE FROM notes_fts WHERE rowid = :rowid")
    suspend fun deleteFts(rowid: Long)

    @Transaction
    suspend fun insertNoteWithFts(note: NoteEntity): Long {
        val id = insertNote(note)
        upsertFts(id, note.title, note.content, note.tags.joinToString(" "))
        return id
    }

    @Transaction
    suspend fun updateNoteWithFts(note: NoteEntity) {
        updateNote(note)
        upsertFts(note.id, note.title, note.content, note.tags.joinToString(" "))
    }

    @Transaction
    suspend fun deleteNoteWithFts(note: NoteEntity) {
        deleteFts(note.id)
        deleteNote(note)
    }

    @Query("SELECT COUNT(*) FROM notes WHERE is_trashed = 0")
    fun getActiveNoteCount(): Flow<Int>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesForBackup(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)
}
