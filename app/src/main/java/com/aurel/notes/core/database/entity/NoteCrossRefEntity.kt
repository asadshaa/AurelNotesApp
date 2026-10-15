package com.aurel.notes.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "note_cross_refs",
    primaryKeys = ["source_note_id", "target_note_id"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_note_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["target_note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["source_note_id"]),
        Index(value = ["target_note_id"])
    ]
)
data class NoteCrossRefEntity(
    @ColumnInfo(name = "source_note_id")
    val sourceNoteId: Long,
    @ColumnInfo(name = "target_note_id")
    val targetNoteId: Long,
    @ColumnInfo(name = "link_text")
    val linkText: String
)
