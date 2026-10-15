package com.aurel.notes.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.scheduler.ExactAlarmScheduler
import com.aurel.notes.data.repository.TaskRepositoryImpl
import com.aurel.notes.domain.model.Task
import com.aurel.notes.domain.model.TaskPriority
import com.aurel.notes.domain.model.TaskRecurrence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilterTab {
    TODAY, UPCOMING, OVERDUE, COMPLETED
}

data class TasksUiState(
    val currentTab: TaskFilterTab = TaskFilterTab.TODAY,
    val tasks: List<Task> = emptyList(),
    val todayPendingCount: Int = 0,
    val todayCompletedCount: Int = 0,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val taskRepo = TaskRepositoryImpl(app.database.taskDao())
    private val alarmScheduler = ExactAlarmScheduler(application)

    private val currentTabFlow = MutableStateFlow(TaskFilterTab.TODAY)

    val uiState: StateFlow<TasksUiState> = currentTabFlow.flatMapLatest { tab ->
        val flow = when (tab) {
            TaskFilterTab.TODAY -> taskRepo.getTodayTasks()
            TaskFilterTab.UPCOMING -> taskRepo.getUpcomingTasks()
            TaskFilterTab.OVERDUE -> taskRepo.getOverdueTasks()
            TaskFilterTab.COMPLETED -> taskRepo.getCompletedTasks()
        }

        kotlinx.coroutines.flow.combine(
            flow,
            taskRepo.getTodayPendingTaskCount(),
            taskRepo.getTodayCompletedTaskCount()
        ) { tasks, pending, completed ->
            TasksUiState(
                currentTab = tab,
                tasks = tasks,
                todayPendingCount = pending,
                todayCompletedCount = completed,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState(isLoading = true)
    )

    fun setTab(tab: TaskFilterTab) {
        currentTabFlow.value = tab
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            taskRepo.setTaskCompleted(task.id, newCompleted)

            if (newCompleted) {
                alarmScheduler.cancelTaskAlarm(task.id)

                // If recurrence is set, spawn next recurrence
                if (task.recurrence != TaskRecurrence.NONE && task.dueDateTime != null) {
                    val nextDue = calculateNextRecurrence(task.dueDateTime, task.recurrence)
                    val nextTask = task.copy(
                        id = 0,
                        isCompleted = false,
                        completedAt = null,
                        dueDateTime = nextDue,
                        createdAt = System.currentTimeMillis()
                    )
                    val newId = taskRepo.saveTask(nextTask)
                    if (nextTask.reminderEnabled) {
                        alarmScheduler.scheduleTaskAlarm(newId, nextTask.title, nextDue)
                    }
                }
            } else if (task.reminderEnabled && task.dueDateTime != null && task.dueDateTime > System.currentTimeMillis()) {
                alarmScheduler.scheduleTaskAlarm(task.id, task.title, task.dueDateTime)
            }
        }
    }

    fun createTask(
        title: String,
        priority: TaskPriority,
        dueDateTime: Long?,
        reminderEnabled: Boolean,
        recurrence: TaskRecurrence
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                priority = priority,
                dueDateTime = dueDateTime,
                reminderEnabled = reminderEnabled,
                recurrence = recurrence
            )
            val id = taskRepo.saveTask(task)
            if (reminderEnabled && dueDateTime != null && dueDateTime > System.currentTimeMillis()) {
                alarmScheduler.scheduleTaskAlarm(id, task.title, dueDateTime)
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            alarmScheduler.cancelTaskAlarm(taskId)
            taskRepo.deleteTask(taskId)
        }
    }

    private fun calculateNextRecurrence(currentMillis: Long, recurrence: TaskRecurrence): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = currentMillis }
        when (recurrence) {
            TaskRecurrence.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            TaskRecurrence.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            TaskRecurrence.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            TaskRecurrence.NONE -> {}
        }
        return calendar.timeInMillis
    }
}
