package com.aurel.notes.data.repository

import com.aurel.notes.core.database.dao.NotebookDao
import com.aurel.notes.core.database.dao.TaskDao
import com.aurel.notes.data.mapper.toDomain
import com.aurel.notes.data.mapper.toEntity
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task
import com.aurel.notes.domain.repository.NotebookRepository
import com.aurel.notes.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class NotebookRepositoryImpl(
    private val notebookDao: NotebookDao
) : NotebookRepository {

    override fun getAllNotebooks(): Flow<List<Notebook>> =
        notebookDao.getNotebooksWithCounts().map { list -> list.map { it.toDomain() } }

    override suspend fun getNotebookById(id: Long): Notebook? =
        notebookDao.getNotebookById(id)?.toDomain()

    override suspend fun createNotebook(notebook: Notebook): Long =
        notebookDao.insertNotebook(notebook.toEntity())

    override suspend fun updateNotebook(notebook: Notebook) =
        notebookDao.updateNotebook(notebook.toEntity())

    override suspend fun deleteNotebook(id: Long) =
        notebookDao.deleteNotebookById(id)
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    private fun getTodayStartEndMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        return Pair(startOfDay, endOfDay)
    }

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    override fun getTodayTasks(): Flow<List<Task>> {
        val (startOfDay, endOfDay) = getTodayStartEndMillis()
        return taskDao.getTodayTasks(startOfDay, endOfDay).map { list -> list.map { it.toDomain() } }
    }

    override fun getUpcomingTasks(): Flow<List<Task>> {
        val (_, endOfDay) = getTodayStartEndMillis()
        return taskDao.getUpcomingTasks(endOfDay).map { list -> list.map { it.toDomain() } }
    }

    override fun getOverdueTasks(): Flow<List<Task>> {
        val now = System.currentTimeMillis()
        return taskDao.getOverdueTasks(now).map { list -> list.map { it.toDomain() } }
    }

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }

    override fun getTasksForNote(noteId: Long): Flow<List<Task>> =
        taskDao.getTasksForNote(noteId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun saveTask(task: Task): Long =
        taskDao.insertTask(task.toEntity())

    override suspend fun updateTask(task: Task) =
        taskDao.updateTask(task.toEntity())

    override suspend fun deleteTask(id: Long) =
        taskDao.deleteTaskById(id)

    override suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(id, isCompleted, completedAt)
    }

    override suspend fun snoozeTask(id: Long, newDueDateTime: Long) =
        taskDao.snoozeTask(id, newDueDateTime)

    override fun getTodayPendingTaskCount(): Flow<Int> {
        val (startOfDay, endOfDay) = getTodayStartEndMillis()
        return taskDao.getTodayPendingTaskCount(startOfDay, endOfDay)
    }

    override fun getTodayCompletedTaskCount(): Flow<Int> {
        val (startOfDay, endOfDay) = getTodayStartEndMillis()
        return taskDao.getTodayCompletedTaskCount(startOfDay, endOfDay)
    }
}
