package com.aurel.notes.ui.notebooks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aurel.notes.AurelApplication
import com.aurel.notes.core.database.dao.NotebookWithCount
import com.aurel.notes.core.database.entity.NotebookEntity
import com.aurel.notes.core.designsystem.CardColorTokens
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotebooksViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AurelApplication
    private val notebookDao = app.database.notebookDao()

    val notebooksWithCount: StateFlow<List<NotebookWithCount>> = notebookDao.getNotebooksWithCounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createNotebook(name: String, colorHex: String, iconName: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val notebook = NotebookEntity(
                name = name.trim(),
                colorHex = colorHex,
                iconName = iconName,
                isDefault = false,
                createdAt = System.currentTimeMillis()
            )
            notebookDao.insertNotebook(notebook)
        }
    }

    fun deleteNotebook(id: Long) {
        viewModelScope.launch {
            notebookDao.deleteNotebookById(id)
        }
    }
}
