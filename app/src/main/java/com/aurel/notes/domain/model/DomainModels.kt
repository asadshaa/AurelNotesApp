package com.aurel.notes.domain.model

data class Notebook(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#7564D8",
    val iconName: String = "book",
    val isDefault: Boolean = false,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class Task(
    val id: Long = 0,
    val parentNoteId: Long? = null,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDateTime: Long? = null,
    val reminderEnabled: Boolean = false,
    val recurrence: TaskRecurrence = TaskRecurrence.NONE,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

enum class TaskRecurrence {
    NONE, DAILY, WEEKLY, MONTHLY
}

data class Attachment(
    val id: Long = 0,
    val noteId: Long,
    val type: AttachmentType,
    val filePath: String,
    val fileName: String,
    val fileSizeBytes: Long = 0L,
    val durationMs: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AttachmentType {
    IMAGE, AUDIO, DRAWING, FILE
}
