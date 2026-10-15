package com.aurel.notes.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aurel.notes.core.database.entity.NoteCrossRefEntity
import com.aurel.notes.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CrossRefDao {

    @Query("SELECT * FROM note_cross_refs WHERE source_note_id = :sourceNoteId")
    fun getOutgoingLinks(sourceNoteId: Long): Flow<List<NoteCrossRefEntity>>

    @Query("""
        SELECT notes.* FROM notes
        JOIN note_cross_refs ON notes.id = note_cross_refs.source_note_id
        WHERE note_cross_refs.target_note_id = :targetNoteId AND notes.is_trashed = 0
        ORDER BY notes.updated_at DESC
    """)
    fun getBacklinksForNote(targetNoteId: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(ref: NoteCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(refs: List<NoteCrossRefEntity>)

    @Query("DELETE FROM note_cross_refs WHERE source_note_id = :sourceNoteId")
    suspend fun deleteCrossRefsForSource(sourceNoteId: Long)

    @Transaction
    suspend fun syncCrossRefsForSource(sourceNoteId: Long, refs: List<NoteCrossRefEntity>) {
        deleteCrossRefsForSource(sourceNoteId)
        if (refs.isNotEmpty()) {
            insertCrossRefs(refs)
        }
    }

    @Query("SELECT * FROM note_cross_refs")
    suspend fun getAllCrossRefsForBackup(): List<NoteCrossRefEntity>
}
