package com.aurel.notes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent_note_id"]),
        Index(value = ["is_completed"]),
        Index(value = ["due_date_time"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "parent_note_id")
    val parentNoteId: Long? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "priority")
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    @ColumnInfo(name = "due_date_time")
    val dueDateTime: Long? = null,
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = false,
    @ColumnInfo(name = "recurrence")
    val recurrence: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
