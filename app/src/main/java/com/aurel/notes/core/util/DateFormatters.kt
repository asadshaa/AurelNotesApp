package com.aurel.notes.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatters {
    private val fullDateTime = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    private val shortDate = SimpleDateFormat("MMM d", Locale.getDefault())

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTime.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortDate.format(Date(timestamp))
    }

    fun formatRelativeTime(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        val diff = (now - timestamp).coerceAtLeast(0L)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            else -> formatShortDate(timestamp)
        }
    }
}
