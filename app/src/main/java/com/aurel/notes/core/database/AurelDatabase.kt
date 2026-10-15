package com.aurel.notes.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aurel.notes.core.database.converter.AurelTypeConverters
import com.aurel.notes.core.database.dao.AttachmentDao
import com.aurel.notes.core.database.dao.CrossRefDao
import com.aurel.notes.core.database.dao.NoteDao
import com.aurel.notes.core.database.dao.NotebookDao
import com.aurel.notes.core.database.dao.TaskDao
import com.aurel.notes.core.database.entity.AttachmentEntity
import com.aurel.notes.core.database.entity.NoteCrossRefEntity
import com.aurel.notes.core.database.entity.NoteEntity
import com.aurel.notes.core.database.entity.NoteFtsEntity
import com.aurel.notes.core.database.entity.NotebookEntity
import com.aurel.notes.core.database.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NoteEntity::class,
        NoteFtsEntity::class,
        NotebookEntity::class,
        TaskEntity::class,
        AttachmentEntity::class,
        NoteCrossRefEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AurelTypeConverters::class)
abstract class AurelDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun notebookDao(): NotebookDao
    abstract fun taskDao(): TaskDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun crossRefDao(): CrossRefDao

    companion object {
        const val DATABASE_NAME = "aurel_notes.db"

        @Volatile
        private var INSTANCE: AurelDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AurelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AurelDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDefaultNotebooks(database.notebookDao())
                    }
                }
            }

            private suspend fun populateDefaultNotebooks(notebookDao: NotebookDao) {
                val defaults = listOf(
                    NotebookEntity(name = "Inbox", colorHex = "#7564D8", iconName = "inbox", isDefault = true),
                    NotebookEntity(name = "Personal", colorHex = "#E08D3C", iconName = "person", isDefault = true),
                    NotebookEntity(name = "Ideas", colorHex = "#439C5E", iconName = "lightbulb", isDefault = true),
                    NotebookEntity(name = "Work", colorHex = "#4A8FD9", iconName = "work", isDefault = true),
                    NotebookEntity(name = "Learning", colorHex = "#9C8DF6", iconName = "school", isDefault = true),
                    NotebookEntity(name = "Journal", colorHex = "#D94848", iconName = "edit_calendar", isDefault = true)
                )
                notebookDao.insertNotebooks(defaults)
            }
        }
    }
}
