package com.aurel.notes.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aurel.notes.core.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY is_completed ASC, due_date_time ASC, created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time >= :startOfDay AND due_date_time <= :endOfDay ORDER BY due_date_time ASC")
    fun getTodayTasks(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time > :endOfDay ORDER BY due_date_time ASC")
    fun getUpcomingTasks(endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time < :now ORDER BY due_date_time ASC")
    fun getOverdueTasks(now: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC LIMIT 50")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE parent_note_id = :noteId ORDER BY is_completed ASC, id ASC")
    fun getTasksForNote(noteId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND reminder_enabled = 1 AND due_date_time >= :now")
    suspend fun getUpcomingReminderTasks(now: Long): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks WHERE parent_note_id = :noteId")
    suspend fun deleteTasksByParentNote(noteId: Long)

    @Query("UPDATE tasks SET is_completed = :isCompleted, completed_at = :completedAt WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET due_date_time = :newDueDateTime WHERE id = :id")
    suspend fun snoozeTask(id: Long, newDueDateTime: Long)

    @Query("SELECT COUNT(*) FROM tasks WHERE is_completed = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time >= :startOfDay AND due_date_time <= :endOfDay")
    fun getTodayPendingTaskCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE is_completed = 1 AND due_date_time IS NOT NULL AND due_date_time >= :startOfDay AND due_date_time <= :endOfDay")
    fun getTodayCompletedTaskCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksForBackup(): List<TaskEntity>
}
