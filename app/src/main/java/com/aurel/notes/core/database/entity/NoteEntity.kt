package com.aurel.notes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = NotebookEntity::class,
            parentColumns = ["id"],
            childColumns = ["notebook_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["notebook_id"]),
        Index(value = ["is_pinned"]),
        Index(value = ["is_archived"]),
        Index(value = ["is_trashed"]),
        Index(value = ["is_vault_locked"]),
        Index(value = ["updated_at"])
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "uuid")
    val uuid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "plain_text_summary")
    val plainTextSummary: String = "",
    @ColumnInfo(name = "notebook_id")
    val notebookId: Long? = null,
    @ColumnInfo(name = "card_color_hex")
    val cardColorHex: String = "#FFFFFF",
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "is_trashed")
    val isTrashed: Boolean = false,
    @ColumnInfo(name = "trashed_at")
    val trashedAt: Long? = null,
    @ColumnInfo(name = "is_vault_locked")
    val isVaultLocked: Boolean = false,
    @ColumnInfo(name = "iv_hex")
    val ivHex: String? = null,
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    val body: String get() = content
}
