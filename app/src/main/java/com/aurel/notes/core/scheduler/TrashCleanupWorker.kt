package com.aurel.notes.core.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aurel.notes.core.database.AurelDatabase

class TrashCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AurelDatabase.getInstance(applicationContext)
            // 30 days in milliseconds
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            db.noteDao().purgeExpiredTrash(thirtyDaysAgo)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "aurel_trash_cleanup_work"
    }
}
