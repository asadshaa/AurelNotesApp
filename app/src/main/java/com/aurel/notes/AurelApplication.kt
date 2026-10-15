package com.aurel.notes

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aurel.notes.core.database.AurelDatabase
import com.aurel.notes.core.datastore.UserPreferencesRepository
import com.aurel.notes.core.scheduler.TrashCleanupWorker
import java.util.concurrent.TimeUnit

class AurelApplication : Application() {

    lateinit var database: AurelDatabase
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AurelDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)

        schedulePeriodicTrashCleanup()
    }

    private fun schedulePeriodicTrashCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<TrashCleanupWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            TrashCleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }

    companion object {
        lateinit var instance: AurelApplication
            private set
    }
}
