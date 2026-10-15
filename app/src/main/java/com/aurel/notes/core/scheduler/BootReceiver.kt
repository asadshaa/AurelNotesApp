package com.aurel.notes.core.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aurel.notes.core.database.AurelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val db = AurelDatabase.getInstance(context)
            val scope = CoroutineScope(Dispatchers.IO)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            scope.launch {
                val now = System.currentTimeMillis()
                val upcomingTasks = db.taskDao().getUpcomingReminderTasks(now)

                for (task in upcomingTasks) {
                    val dueTime = task.dueDateTime ?: continue
                    val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
                        putExtra(AlarmReceiver.EXTRA_TASK_TITLE, task.title)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        task.id.toInt(),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueTime, pendingIntent)
                        } else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, dueTime, pendingIntent)
                        }
                    } catch (_: SecurityException) {
                        // In case SCHEDULE_EXACT_ALARM permission is revoked
                    }
                }
            }
        }
    }
}
