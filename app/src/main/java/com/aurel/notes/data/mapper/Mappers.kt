package com.aurel.notes.data.mapper

import com.aurel.notes.core.database.dao.NotebookWithCount
import com.aurel.notes.core.database.entity.AttachmentEntity
import com.aurel.notes.core.database.entity.NoteEntity
import com.aurel.notes.core.database.entity.NotebookEntity
import com.aurel.notes.core.database.entity.TaskEntity
import com.aurel.notes.domain.model.Attachment
import com.aurel.notes.domain.model.AttachmentType
import com.aurel.notes.domain.model.Note
import com.aurel.notes.domain.model.Notebook
import com.aurel.notes.domain.model.Task
import com.aurel.notes.domain.model.TaskPriority
import com.aurel.notes.domain.model.TaskRecurrence

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    uuid = uuid,
    title = title,
    content = content,
    plainTextSummary = plainTextSummary,
    notebookId = notebookId,
    cardColorHex = cardColorHex,
    isPinned = isPinned,
    isArchived = isArchived,
    isTrashed = isTrashed,
    trashedAt = trashedAt,
    isVaultLocked = isVaultLocked,
    ivHex = ivHex,
    tags = tags,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    uuid = if (uuid.isBlank()) java.util.UUID.randomUUID().toString() else uuid,
    title = title,
    content = content,
    plainTextSummary = plainTextSummary,
    notebookId = notebookId,
    cardColorHex = cardColorHex,
    isPinned = isPinned,
    isArchived = isArchived,
    isTrashed = isTrashed,
    trashedAt = trashedAt,
    isVaultLocked = isVaultLocked,
    ivHex = ivHex,
    tags = tags,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun NotebookEntity.toDomain(noteCount: Int = 0): Notebook = Notebook(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    isDefault = isDefault,
    noteCount = noteCount,
    createdAt = createdAt
)

fun NotebookWithCount.toDomain(): Notebook = Notebook(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    isDefault = isDefault,
    noteCount = noteCount
)

fun Notebook.toEntity(): NotebookEntity = NotebookEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    isDefault = isDefault,
    createdAt = createdAt
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    parentNoteId = parentNoteId,
    title = title,
    isCompleted = isCompleted,
    priority = try { TaskPriority.valueOf(priority) } catch (_: Exception) { TaskPriority.MEDIUM },
    dueDateTime = dueDateTime,
    reminderEnabled = reminderEnabled,
    recurrence = try { TaskRecurrence.valueOf(recurrence) } catch (_: Exception) { TaskRecurrence.NONE },
    completedAt = completedAt,
    createdAt = createdAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    parentNoteId = parentNoteId,
    title = title,
    isCompleted = isCompleted,
    priority = priority.name,
    dueDateTime = dueDateTime,
    reminderEnabled = reminderEnabled,
    recurrence = recurrence.name,
    completedAt = completedAt,
    createdAt = createdAt
)

fun AttachmentEntity.toDomain(): Attachment = Attachment(
    id = id,
    noteId = noteId,
    type = try { AttachmentType.valueOf(type) } catch (_: Exception) { AttachmentType.FILE },
    filePath = filePath,
    fileName = fileName,
    fileSizeBytes = fileSizeBytes,
    durationMs = durationMs,
    createdAt = createdAt
)

fun Attachment.toEntity(): AttachmentEntity = AttachmentEntity(
    id = id,
    noteId = noteId,
    type = type.name,
    filePath = filePath,
    fileName = fileName,
    fileSizeBytes = fileSizeBytes,
    durationMs = durationMs,
    createdAt = createdAt
)
