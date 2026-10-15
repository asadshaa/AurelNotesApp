package com.aurel.notes.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aurel.notes.core.database.entity.NotebookEntity
import kotlinx.coroutines.flow.Flow

data class NotebookWithCount(
    val id: Long,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val isDefault: Boolean,
    val noteCount: Int
)

@Dao
interface NotebookDao {

    @Query("SELECT * FROM notebooks ORDER BY is_default DESC, name ASC")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>

    @Query("""
        SELECT notebooks.id AS id,
               notebooks.name AS name,
               notebooks.color_hex AS colorHex,
               notebooks.icon_name AS iconName,
               notebooks.is_default AS isDefault,
               COUNT(notes.id) AS noteCount
        FROM notebooks
        LEFT JOIN notes ON notes.notebook_id = notebooks.id AND notes.is_trashed = 0
        GROUP BY notebooks.id
        ORDER BY notebooks.is_default DESC, notebooks.name ASC
    """)
    fun getNotebooksWithCounts(): Flow<List<NotebookWithCount>>

    @Query("SELECT * FROM notebooks WHERE id = :id LIMIT 1")
    suspend fun getNotebookById(id: Long): NotebookEntity?

    @Query("SELECT * FROM notebooks WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getNotebookByName(name: String): NotebookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebooks(notebooks: List<NotebookEntity>)

    @Update
    suspend fun updateNotebook(notebook: NotebookEntity)

    @Delete
    suspend fun deleteNotebook(notebook: NotebookEntity)

    @Query("DELETE FROM notebooks WHERE id = :id AND is_default = 0")
    suspend fun deleteNotebookById(id: Long)

    @Query("SELECT * FROM notebooks")
    suspend fun getAllNotebooksForBackup(): List<NotebookEntity>
}
