package com.aurel.notes.domain.model

data class Note(
    val id: Long = 0,
    val uuid: String = "",
    val title: String = "",
    val content: String = "",
    val plainTextSummary: String = "",
    val notebookId: Long? = null,
    val cardColorHex: String = "#FFFFFF",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null,
    val isVaultLocked: Boolean = false,
    val ivHex: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
