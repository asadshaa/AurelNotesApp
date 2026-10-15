package com.aurel.notes.core.scheduler

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aurel.notes.core.database.AurelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(AlarmReceiver.EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(AlarmReceiver.EXTRA_TASK_TITLE) ?: ""
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (taskId <= 0) return

        // Dismiss the active notification
        notificationManager.cancel(taskId.toInt())

        val db = AurelDatabase.getInstance(context)
        val scope = CoroutineScope(Dispatchers.IO)

        when (intent.action) {
            AlarmReceiver.ACTION_COMPLETE -> {
                scope.launch {
                    db.taskDao().setTaskCompleted(
                        id = taskId,
                        isCompleted = true,
                        completedAt = System.currentTimeMillis()
                    )
                }
            }
            AlarmReceiver.ACTION_SNOOZE_15 -> {
                val newDueTime = System.currentTimeMillis() + (15 * 60 * 1000)
                scheduleExactAlarm(context, taskId, taskTitle, newDueTime)
                scope.launch {
                    db.taskDao().snoozeTask(taskId, newDueTime)
                }
            }
            AlarmReceiver.ACTION_SNOOZE_60 -> {
                val newDueTime = System.currentTimeMillis() + (60 * 60 * 1000)
                scheduleExactAlarm(context, taskId, taskTitle, newDueTime)
                scope.launch {
                    db.taskDao().snoozeTask(taskId, newDueTime)
                }
            }
        }
    }

    private fun scheduleExactAlarm(context: Context, taskId: Long, title: String, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
            putExtra(AlarmReceiver.EXTRA_TASK_TITLE, title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // Contextual permission not granted
        }
    }
}
