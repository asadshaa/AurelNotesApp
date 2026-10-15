package com.aurel.notes.core.backup

import android.content.Context
import androidx.room.withTransaction
import com.aurel.notes.core.database.AurelDatabase
import com.aurel.notes.core.database.entity.AttachmentEntity
import com.aurel.notes.core.database.entity.NoteEntity
import com.aurel.notes.core.database.entity.NotebookEntity
import com.aurel.notes.core.database.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Portable Backup and Restore engine for Aurel.
 * Packages versioned JSON database manifest and internal attachments into a compressed .zip archive.
 * Supports schema validation and conflict resolution (KEEP_BOTH vs REPLACE).
 */
class ZipBackupManager(
    private val context: Context,
    private val database: AurelDatabase
) {
    enum class ConflictResolution {
        KEEP_BOTH,
        REPLACE
    }

    data class BackupStats(
        val notesCount: Int,
        val notebooksCount: Int,
        val tasksCount: Int,
        val attachmentsCount: Int,
        val backupSizeBytes: Long
    )

    data class RestoreResult(
        val isSuccess: Boolean,
        val notesRestored: Int = 0,
        val notebooksRestored: Int = 0,
        val tasksRestored: Int = 0,
        val attachmentsRestored: Int = 0,
        val errorMessage: String? = null
    )

    companion object {
        const val MANIFEST_ENTRY_NAME = "aurel_backup_v1.json"
        const val CURRENT_SCHEMA_VERSION = 1
        const val APP_IDENTIFIER = "Aurel"
    }

    /**
     * Creates a full portable ZIP backup and writes it to the given OutputStream.
     */
    suspend fun createBackup(outputStream: OutputStream): BackupStats = withContext(Dispatchers.IO) {
        val noteDao = database.noteDao()
        val notebookDao = database.notebookDao()
        val taskDao = database.taskDao()
        val attachmentDao = database.attachmentDao()

        val allNotebooks = notebookDao.getAllNotebooksForBackup()
        val allNotes = noteDao.getAllNotesForBackup()
        val allTasks = taskDao.getAllTasksForBackup()
        val allAttachments = attachmentDao.getAllAttachmentsForBackup()

        // Build versioned JSON manifest
        val manifestJson = JSONObject().apply {
            put("version", CURRENT_SCHEMA_VERSION)
            put("app", APP_IDENTIFIER)
            put("created_at", System.currentTimeMillis())

            // Notebooks
            val notebooksArray = JSONArray()
            for (nb in allNotebooks) {
                notebooksArray.put(JSONObject().apply {
                    put("id", nb.id)
                    put("name", nb.name)
                    put("color_hex", nb.colorHex)
                    put("icon_name", nb.iconName)
                    put("is_default", nb.isDefault)
                    put("created_at", nb.createdAt)
                })
            }
            put("notebooks", notebooksArray)

            // Notes
            val notesArray = JSONArray()
            for (n in allNotes) {
                notesArray.put(JSONObject().apply {
                    put("id", n.id)
                    put("uuid", n.uuid)
                    put("title", n.title)
                    put("content", n.content)
                    put("plain_text_summary", n.plainTextSummary)
                    put("notebook_id", n.notebookId ?: JSONObject.NULL)
                    put("card_color_hex", n.cardColorHex)
                    put("is_pinned", n.isPinned)
                    put("is_archived", n.isArchived)
                    put("is_trashed", n.isTrashed)
                    put("trashed_at", n.trashedAt ?: JSONObject.NULL)
                    put("is_vault_locked", n.isVaultLocked)
                    put("iv_hex", n.ivHex ?: JSONObject.NULL)
                    put("tags", JSONArray(n.tags))
                    put("created_at", n.createdAt)
                    put("updated_at", n.updatedAt)
                })
            }
            put("notes", notesArray)

            // Tasks
            val tasksArray = JSONArray()
            for (t in allTasks) {
                tasksArray.put(JSONObject().apply {
                    put("id", t.id)
                    put("parent_note_id", t.parentNoteId ?: JSONObject.NULL)
                    put("title", t.title)
                    put("is_completed", t.isCompleted)
                    put("priority", t.priority)
                    put("due_date_time", t.dueDateTime ?: JSONObject.NULL)
                    put("reminder_enabled", t.reminderEnabled)
                    put("recurrence", t.recurrence)
                    put("completed_at", t.completedAt ?: JSONObject.NULL)
                    put("created_at", t.createdAt)
                })
            }
            put("tasks", tasksArray)

            // Attachments
            val attachmentsArray = JSONArray()
            for (a in allAttachments) {
                attachmentsArray.put(JSONObject().apply {
                    put("id", a.id)
                    put("note_id", a.noteId)
                    put("type", a.type)
                    put("file_path", a.filePath)
                    put("file_name", a.fileName)
                    put("file_size_bytes", a.fileSizeBytes)
                    put("duration_ms", a.durationMs ?: JSONObject.NULL)
                    put("created_at", a.createdAt)
                })
            }
            put("attachments", attachmentsArray)
        }

        val manifestBytes = manifestJson.toString(2).toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256").digest(manifestBytes)
        val checksumHex = digest.joinToString("") { "%02x".format(it) }
        manifestJson.put("checksum", checksumHex)

        val updatedManifestBytes = manifestJson.toString(2).toByteArray(Charsets.UTF_8)

        var totalBytesWritten = 0L
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
            // Write manifest entry
            val manifestEntry = ZipEntry(MANIFEST_ENTRY_NAME)
            zipOut.putNextEntry(manifestEntry)
            zipOut.write(updatedManifestBytes)
            zipOut.closeEntry()
            totalBytesWritten += updatedManifestBytes.size

            // Write internal attachments if they exist
            val attachmentsDir = File(context.filesDir, "attachments")
            if (attachmentsDir.exists()) {
                val files = attachmentsDir.listFiles() ?: emptyArray()
                val buffer = ByteArray(8192)
                for (file in files) {
                    if (file.isFile) {
                        val entry = ZipEntry("attachments/${file.name}")
                        zipOut.putNextEntry(entry)
                        FileInputStream(file).use { fis ->
                            var read: Int
                            while (fis.read(buffer).also { read = it } != -1) {
                                zipOut.write(buffer, 0, read)
                                totalBytesWritten += read
                            }
                        }
                        zipOut.closeEntry()
                    }
                }
            }
        }

        BackupStats(
            notesCount = allNotes.size,
            notebooksCount = allNotebooks.size,
            tasksCount = allTasks.size,
            attachmentsCount = allAttachments.size,
            backupSizeBytes = totalBytesWritten
        )
    }

    /**
     * Restores backup from an InputStream with explicit schema validation and conflict resolution.
     */
    suspend fun restoreBackup(
        inputStream: InputStream,
        conflictResolution: ConflictResolution
    ): RestoreResult = withContext(Dispatchers.IO) {
        val attachmentsDir = File(context.filesDir, "attachments").apply { mkdirs() }
        var manifestContent: String? = null

        try {
            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                val buffer = ByteArray(8192)

                while (entry != null) {
                    if (!entry.isDirectory) {
                        if (entry.name == MANIFEST_ENTRY_NAME || entry.name.endsWith(".json")) {
                            manifestContent = zipIn.bufferedReader(Charsets.UTF_8).readText()
                        } else if (entry.name.startsWith("attachments/")) {
                            val fileName = entry.name.removePrefix("attachments/")
                            if (fileName.isNotBlank()) {
                                val destFile = File(attachmentsDir, fileName)
                                FileOutputStream(destFile).use { fos ->
                                    var read: Int
                                    while (zipIn.read(buffer).also { read = it } != -1) {
                                        fos.write(buffer, 0, read)
                                    }
                                }
                            }
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (manifestContent == null) {
                return@withContext RestoreResult(
                    isSuccess = false,
                    errorMessage = "Missing manifest in backup archive ($MANIFEST_ENTRY_NAME)"
                )
            }

            val manifest = JSONObject(manifestContent!!)
            val version = manifest.optInt("version", -1)
            val app = manifest.optString("app", "")

            if (version > CURRENT_SCHEMA_VERSION) {
                return@withContext RestoreResult(
                    isSuccess = false,
                    errorMessage = "Unsupported backup version ($version). Please update Aurel."
                )
            }

            val noteDao = database.noteDao()
            val notebookDao = database.notebookDao()
            val taskDao = database.taskDao()
            val attachmentDao = database.attachmentDao()

            var notesCount = 0
            var notebooksCount = 0
            var tasksCount = 0
            var attachmentsCount = 0

            database.withTransaction {
                val notebookIdMap = mutableMapOf<Long, Long>()
                val noteIdMap = mutableMapOf<Long, Long>()

                // 1. Restore Notebooks
                val notebooksArray = manifest.optJSONArray("notebooks") ?: JSONArray()
                for (i in 0 until notebooksArray.length()) {
                    val obj = notebooksArray.getJSONObject(i)
                    val oldId = obj.getLong("id")
                    val originalName = obj.getString("name")
                    val existing = notebookDao.getNotebookByName(originalName)

                    val nameToUse = if (conflictResolution == ConflictResolution.KEEP_BOTH && existing != null) {
                        "$originalName (Restored)"
                    } else {
                        originalName
                    }

                    val nbEntity = NotebookEntity(
                        id = if (conflictResolution == ConflictResolution.REPLACE && existing != null) existing.id else 0L,
                        name = nameToUse,
                        colorHex = obj.optString("color_hex", "#7564D8"),
                        iconName = obj.optString("icon_name", "book"),
                        isDefault = obj.optBoolean("is_default", false),
                        createdAt = obj.optLong("created_at", System.currentTimeMillis())
                    )
                    val newId = notebookDao.insertNotebook(nbEntity)
                    notebookIdMap[oldId] = newId
                    notebooksCount++
                }

                // 2. Restore Notes
                val notesArray = manifest.optJSONArray("notes") ?: JSONArray()
                for (i in 0 until notesArray.length()) {
                    val obj = notesArray.getJSONObject(i)
                    val oldId = obj.getLong("id")
                    val rawNotebookId = if (obj.isNull("notebook_id")) null else obj.getLong("notebook_id")
                    val remappedNotebookId = rawNotebookId?.let { notebookIdMap[it] ?: it }

                    val rawTags = obj.optJSONArray("tags") ?: JSONArray()
                    val tagsList = mutableListOf<String>()
                    for (t in 0 until rawTags.length()) {
                        tagsList.add(rawTags.getString(t))
                    }

                    val existingNote = noteDao.getNoteById(oldId)
                    val title = obj.optString("title", "")
                    val finalTitle = if (conflictResolution == ConflictResolution.KEEP_BOTH && existingNote != null) {
                        "$title (Restored)"
                    } else {
                        title
                    }

                    val noteEntity = NoteEntity(
                        id = if (conflictResolution == ConflictResolution.REPLACE && existingNote != null) oldId else 0L,
                        uuid = if (conflictResolution == ConflictResolution.KEEP_BOTH) java.util.UUID.randomUUID().toString() else obj.optString("uuid"),
                        title = finalTitle,
                        content = obj.optString("content", ""),
                        plainTextSummary = obj.optString("plain_text_summary", ""),
                        notebookId = remappedNotebookId,
                        cardColorHex = obj.optString("card_color_hex", "#FFFFFF"),
                        isPinned = obj.optBoolean("is_pinned", false),
                        isArchived = obj.optBoolean("is_archived", false),
                        isTrashed = obj.optBoolean("is_trashed", false),
                        trashedAt = if (obj.isNull("trashed_at")) null else obj.getLong("trashed_at"),
                        isVaultLocked = obj.optBoolean("is_vault_locked", false),
                        ivHex = if (obj.isNull("iv_hex")) null else obj.getString("iv_hex"),
                        tags = tagsList,
                        createdAt = obj.optLong("created_at", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updated_at", System.currentTimeMillis())
                    )

                    val newNoteId = noteDao.insertNoteWithFts(noteEntity)
                    noteIdMap[oldId] = newNoteId
                    notesCount++
                }

                // 3. Restore Tasks
                val tasksArray = manifest.optJSONArray("tasks") ?: JSONArray()
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    val rawParentNoteId = if (obj.isNull("parent_note_id")) null else obj.getLong("parent_note_id")
                    val remappedParentId = rawParentNoteId?.let { noteIdMap[it] ?: it }

                    val taskEntity = TaskEntity(
                        id = if (conflictResolution == ConflictResolution.REPLACE) obj.optLong("id", 0L) else 0L,
                        parentNoteId = remappedParentId,
                        title = obj.getString("title"),
                        isCompleted = obj.optBoolean("is_completed", false),
                        priority = obj.optString("priority", "MEDIUM"),
                        dueDateTime = if (obj.isNull("due_date_time")) null else obj.getLong("due_date_time"),
                        reminderEnabled = obj.optBoolean("reminder_enabled", false),
                        recurrence = obj.optString("recurrence", "NONE"),
                        completedAt = if (obj.isNull("completed_at")) null else obj.getLong("completed_at"),
                        createdAt = obj.optLong("created_at", System.currentTimeMillis())
                    )
                    taskDao.insertTask(taskEntity)
                    tasksCount++
                }

                // 4. Restore Attachments
                val attachmentsArray = manifest.optJSONArray("attachments") ?: JSONArray()
                for (i in 0 until attachmentsArray.length()) {
                    val obj = attachmentsArray.getJSONObject(i)
                    val rawNoteId = obj.getLong("note_id")
                    val remappedNoteId = noteIdMap[rawNoteId] ?: rawNoteId

                    val attachmentEntity = AttachmentEntity(
                        id = if (conflictResolution == ConflictResolution.REPLACE) obj.optLong("id", 0L) else 0L,
                        noteId = remappedNoteId,
                        type = obj.getString("type"),
                        filePath = obj.getString("file_path"),
                        fileName = obj.getString("file_name"),
                        fileSizeBytes = obj.optLong("file_size_bytes", 0L),
                        durationMs = if (obj.isNull("duration_ms")) null else obj.getLong("duration_ms"),
                        createdAt = obj.optLong("created_at", System.currentTimeMillis())
                    )
                    attachmentDao.insertAttachment(attachmentEntity)
                    attachmentsCount++
                }
            }

            RestoreResult(
                isSuccess = true,
                notesRestored = notesCount,
                notebooksRestored = notebooksCount,
                tasksRestored = tasksCount,
                attachmentsRestored = attachmentsCount
            )
        } catch (e: Exception) {
            RestoreResult(
                isSuccess = false,
                errorMessage = "Restore failed: ${e.localizedMessage ?: e.message}"
            )
        }
    }
}
