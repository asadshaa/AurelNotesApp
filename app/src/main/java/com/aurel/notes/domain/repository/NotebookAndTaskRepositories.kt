package com.aurel.notes.domain.repository

import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {
    fun getAllNotebooks(): Flow<List<Notebook>>
    suspend fun getNotebookById(id: Long): Notebook?
    suspend fun createNotebook(notebook: Notebook): Long
    suspend fun updateNotebook(notebook: Notebook)
    suspend fun deleteNotebook(id: Long)
}

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTodayTasks(): Flow<List<Task>>
    fun getUpcomingTasks(): Flow<List<Task>>
    fun getOverdueTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksForNote(noteId: Long): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun saveTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean)
    suspend fun snoozeTask(id: Long, newDueDateTime: Long)
    fun getTodayPendingTaskCount(): Flow<Int>
    fun getTodayCompletedTaskCount(): Flow<Int>
}
