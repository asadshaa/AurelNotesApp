package com.aurel.notes.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.data.repository.NoteRepositoryImpl
import com.aurel.notes.data.repository.TaskRepositoryImpl
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DayItem(
    val dayNumber: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val hasNotes: Boolean = false,
    val hasTasks: Boolean = false
)

data class CalendarUiState(
    val selectedYear: Int = 2026,
    val selectedMonth: Int = Calendar.OCTOBER,
    val monthTitle: String = "",
    val days: List<DayItem> = emptyList(),
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val notesOnSelectedDate: List<Note> = emptyList(),
    val tasksOnSelectedDate: List<Task> = emptyList()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val noteRepo = NoteRepositoryImpl(app.database.noteDao())
    private val taskRepo = TaskRepositoryImpl(app.database.taskDao())

    private val calendarInstance = Calendar.getInstance()
    private val selectedYearFlow = MutableStateFlow(calendarInstance.get(Calendar.YEAR))
    private val selectedMonthFlow = MutableStateFlow(calendarInstance.get(Calendar.MONTH))
    private val selectedDateMillisFlow = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<CalendarUiState> = combine(
        selectedYearFlow,
        selectedMonthFlow,
        selectedDateMillisFlow,
        noteRepo.getActiveNotes(),
        taskRepo.getAllTasks()
    ) { year, month, selectedDate, allNotes, allTasks ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val monthTitle = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(cal.time)

        // Generate grid days (42 days: 6 weeks)
        cal.firstDayOfWeek = Calendar.MONDAY
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val offset = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

        cal.add(Calendar.DAY_OF_MONTH, -offset)

        val days = mutableListOf<DayItem>()
        val todayCal = Calendar.getInstance()
        val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }

        for (i in 0 until 42) {
            val dYear = cal.get(Calendar.YEAR)
            val dMonth = cal.get(Calendar.MONTH)
            val dDay = cal.get(Calendar.DAY_OF_MONTH)
            val dMillis = cal.timeInMillis

            val isCurrentMonth = dMonth == month
            val isToday = dYear == todayCal.get(Calendar.YEAR) && dMonth == todayCal.get(Calendar.MONTH) && dDay == todayCal.get(Calendar.DAY_OF_MONTH)
            val isSelected = dYear == selectedCal.get(Calendar.YEAR) && dMonth == selectedCal.get(Calendar.MONTH) && dDay == selectedCal.get(Calendar.DAY_OF_MONTH)

            // Check if any notes or tasks match this day
            val hasNotes = allNotes.any { note -> isSameDay(note.updatedAt, dMillis) }
            val hasTasks = allTasks.any { task -> task.dueDateTime != null && isSameDay(task.dueDateTime, dMillis) }

            days.add(
                DayItem(
                    dayNumber = dDay,
                    dateMillis = dMillis,
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday,
                    isSelected = isSelected,
                    hasNotes = hasNotes,
                    hasTasks = hasTasks
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        val notesForSelected = allNotes.filter { isSameDay(it.updatedAt, selectedDate) }
        val tasksForSelected = allTasks.filter { it.dueDateTime != null && isSameDay(it.dueDateTime, selectedDate) }

        CalendarUiState(
            selectedYear = year,
            selectedMonth = month,
            monthTitle = monthTitle,
            days = days,
            selectedDateMillis = selectedDate,
            notesOnSelectedDate = notesForSelected,
            tasksOnSelectedDate = tasksForSelected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    fun nextMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYearFlow.value)
            set(Calendar.MONTH, selectedMonthFlow.value)
            add(Calendar.MONTH, 1)
        }
        selectedYearFlow.value = cal.get(Calendar.YEAR)
        selectedMonthFlow.value = cal.get(Calendar.MONTH)
    }

    fun prevMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYearFlow.value)
            set(Calendar.MONTH, selectedMonthFlow.value)
            add(Calendar.MONTH, -1)
        }
        selectedYearFlow.value = cal.get(Calendar.YEAR)
        selectedMonthFlow.value = cal.get(Calendar.MONTH)
    }

    fun selectDate(millis: Long) {
        selectedDateMillisFlow.value = millis
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepo.setTaskCompleted(taskId, isCompleted)
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val c1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val c2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}
