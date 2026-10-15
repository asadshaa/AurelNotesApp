package com.aurel.notes.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.FtsTableInfo;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.aurel.notes.core.database.dao.AttachmentDao;
import com.aurel.notes.core.database.dao.AttachmentDao_Impl;
import com.aurel.notes.core.database.dao.CrossRefDao;
import com.aurel.notes.core.database.dao.CrossRefDao_Impl;
import com.aurel.notes.core.database.dao.NoteDao;
import com.aurel.notes.core.database.dao.NoteDao_Impl;
import com.aurel.notes.core.database.dao.NotebookDao;
import com.aurel.notes.core.database.dao.NotebookDao_Impl;
import com.aurel.notes.core.database.dao.TaskDao;
import com.aurel.notes.core.database.dao.TaskDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AurelDatabase_Impl extends AurelDatabase {
  private volatile NoteDao _noteDao;

  private volatile NotebookDao _notebookDao;

  private volatile TaskDao _taskDao;

  private volatile AttachmentDao _attachmentDao;

  private volatile CrossRefDao _crossRefDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `uuid` TEXT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `plain_text_summary` TEXT NOT NULL, `notebook_id` INTEGER, `card_color_hex` TEXT NOT NULL, `is_pinned` INTEGER NOT NULL, `is_archived` INTEGER NOT NULL, `is_trashed` INTEGER NOT NULL, `trashed_at` INTEGER, `is_vault_locked` INTEGER NOT NULL, `iv_hex` TEXT, `tags` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, FOREIGN KEY(`notebook_id`) REFERENCES `notebooks`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_notebook_id` ON `notes` (`notebook_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_pinned` ON `notes` (`is_pinned`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_archived` ON `notes` (`is_archived`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_trashed` ON `notes` (`is_trashed`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_vault_locked` ON `notes` (`is_vault_locked`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_updated_at` ON `notes` (`updated_at`)");
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `notes_fts` USING FTS4(`title` TEXT NOT NULL, `content` TEXT NOT NULL, `tags` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notebooks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `is_default` INTEGER NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `parent_note_id` INTEGER, `title` TEXT NOT NULL, `is_completed` INTEGER NOT NULL, `priority` TEXT NOT NULL, `due_date_time` INTEGER, `reminder_enabled` INTEGER NOT NULL, `recurrence` TEXT NOT NULL, `completed_at` INTEGER, `created_at` INTEGER NOT NULL, FOREIGN KEY(`parent_note_id`) REFERENCES `notes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_parent_note_id` ON `tasks` (`parent_note_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_is_completed` ON `tasks` (`is_completed`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_due_date_time` ON `tasks` (`due_date_time`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `attachments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `note_id` INTEGER NOT NULL, `type` TEXT NOT NULL, `file_path` TEXT NOT NULL, `file_name` TEXT NOT NULL, `file_size_bytes` INTEGER NOT NULL, `duration_ms` INTEGER, `created_at` INTEGER NOT NULL, FOREIGN KEY(`note_id`) REFERENCES `notes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_attachments_note_id` ON `attachments` (`note_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `note_cross_refs` (`source_note_id` INTEGER NOT NULL, `target_note_id` INTEGER NOT NULL, `link_text` TEXT NOT NULL, PRIMARY KEY(`source_note_id`, `target_note_id`), FOREIGN KEY(`source_note_id`) REFERENCES `notes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`target_note_id`) REFERENCES `notes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_note_cross_refs_source_note_id` ON `note_cross_refs` (`source_note_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_note_cross_refs_target_note_id` ON `note_cross_refs` (`target_note_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4bbc970662e684c78060ce4f383b1666')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `notes`");
        db.execSQL("DROP TABLE IF EXISTS `notes_fts`");
        db.execSQL("DROP TABLE IF EXISTS `notebooks`");
        db.execSQL("DROP TABLE IF EXISTS `tasks`");
        db.execSQL("DROP TABLE IF EXISTS `attachments`");
        db.execSQL("DROP TABLE IF EXISTS `note_cross_refs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsNotes = new HashMap<String, TableInfo.Column>(16);
        _columnsNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("uuid", new TableInfo.Column("uuid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("plain_text_summary", new TableInfo.Column("plain_text_summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("notebook_id", new TableInfo.Column("notebook_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("card_color_hex", new TableInfo.Column("card_color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_pinned", new TableInfo.Column("is_pinned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_trashed", new TableInfo.Column("is_trashed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("trashed_at", new TableInfo.Column("trashed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_vault_locked", new TableInfo.Column("is_vault_locked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("iv_hex", new TableInfo.Column("iv_hex", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysNotes.add(new TableInfo.ForeignKey("notebooks", "SET NULL", "NO ACTION", Arrays.asList("notebook_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesNotes = new HashSet<TableInfo.Index>(6);
        _indicesNotes.add(new TableInfo.Index("index_notes_notebook_id", false, Arrays.asList("notebook_id"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_is_pinned", false, Arrays.asList("is_pinned"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_is_archived", false, Arrays.asList("is_archived"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_is_trashed", false, Arrays.asList("is_trashed"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_is_vault_locked", false, Arrays.asList("is_vault_locked"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_updated_at", false, Arrays.asList("updated_at"), Arrays.asList("ASC")));
        final TableInfo _infoNotes = new TableInfo("notes", _columnsNotes, _foreignKeysNotes, _indicesNotes);
        final TableInfo _existingNotes = TableInfo.read(db, "notes");
        if (!_infoNotes.equals(_existingNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "notes(com.aurel.notes.core.database.entity.NoteEntity).\n"
                  + " Expected:\n" + _infoNotes + "\n"
                  + " Found:\n" + _existingNotes);
        }
        final HashSet<String> _columnsNotesFts = new HashSet<String>(4);
        _columnsNotesFts.add("title");
        _columnsNotesFts.add("content");
        _columnsNotesFts.add("tags");
        final FtsTableInfo _infoNotesFts = new FtsTableInfo("notes_fts", _columnsNotesFts, "CREATE VIRTUAL TABLE IF NOT EXISTS `notes_fts` USING FTS4(`title` TEXT NOT NULL, `content` TEXT NOT NULL, `tags` TEXT NOT NULL)");
        final FtsTableInfo _existingNotesFts = FtsTableInfo.read(db, "notes_fts");
        if (!_infoNotesFts.equals(_existingNotesFts)) {
          return new RoomOpenHelper.ValidationResult(false, "notes_fts(com.aurel.notes.core.database.entity.NoteFtsEntity).\n"
                  + " Expected:\n" + _infoNotesFts + "\n"
                  + " Found:\n" + _existingNotesFts);
        }
        final HashMap<String, TableInfo.Column> _columnsNotebooks = new HashMap<String, TableInfo.Column>(6);
        _columnsNotebooks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotebooks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotebooks.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotebooks.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotebooks.put("is_default", new TableInfo.Column("is_default", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotebooks.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotebooks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotebooks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotebooks = new TableInfo("notebooks", _columnsNotebooks, _foreignKeysNotebooks, _indicesNotebooks);
        final TableInfo _existingNotebooks = TableInfo.read(db, "notebooks");
        if (!_infoNotebooks.equals(_existingNotebooks)) {
          return new RoomOpenHelper.ValidationResult(false, "notebooks(com.aurel.notes.core.database.entity.NotebookEntity).\n"
                  + " Expected:\n" + _infoNotebooks + "\n"
                  + " Found:\n" + _existingNotebooks);
        }
        final HashMap<String, TableInfo.Column> _columnsTasks = new HashMap<String, TableInfo.Column>(10);
        _columnsTasks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("parent_note_id", new TableInfo.Column("parent_note_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("is_completed", new TableInfo.Column("is_completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("priority", new TableInfo.Column("priority", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("due_date_time", new TableInfo.Column("due_date_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("reminder_enabled", new TableInfo.Column("reminder_enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("recurrence", new TableInfo.Column("recurrence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("completed_at", new TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTasks = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTasks.add(new TableInfo.ForeignKey("notes", "CASCADE", "NO ACTION", Arrays.asList("parent_note_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTasks = new HashSet<TableInfo.Index>(3);
        _indicesTasks.add(new TableInfo.Index("index_tasks_parent_note_id", false, Arrays.asList("parent_note_id"), Arrays.asList("ASC")));
        _indicesTasks.add(new TableInfo.Index("index_tasks_is_completed", false, Arrays.asList("is_completed"), Arrays.asList("ASC")));
        _indicesTasks.add(new TableInfo.Index("index_tasks_due_date_time", false, Arrays.asList("due_date_time"), Arrays.asList("ASC")));
        final TableInfo _infoTasks = new TableInfo("tasks", _columnsTasks, _foreignKeysTasks, _indicesTasks);
        final TableInfo _existingTasks = TableInfo.read(db, "tasks");
        if (!_infoTasks.equals(_existingTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "tasks(com.aurel.notes.core.database.entity.TaskEntity).\n"
                  + " Expected:\n" + _infoTasks + "\n"
                  + " Found:\n" + _existingTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsAttachments = new HashMap<String, TableInfo.Column>(8);
        _columnsAttachments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("note_id", new TableInfo.Column("note_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("file_path", new TableInfo.Column("file_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("file_name", new TableInfo.Column("file_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("file_size_bytes", new TableInfo.Column("file_size_bytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("duration_ms", new TableInfo.Column("duration_ms", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttachments.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAttachments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAttachments.add(new TableInfo.ForeignKey("notes", "CASCADE", "NO ACTION", Arrays.asList("note_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAttachments = new HashSet<TableInfo.Index>(1);
        _indicesAttachments.add(new TableInfo.Index("index_attachments_note_id", false, Arrays.asList("note_id"), Arrays.asList("ASC")));
        final TableInfo _infoAttachments = new TableInfo("attachments", _columnsAttachments, _foreignKeysAttachments, _indicesAttachments);
        final TableInfo _existingAttachments = TableInfo.read(db, "attachments");
        if (!_infoAttachments.equals(_existingAttachments)) {
          return new RoomOpenHelper.ValidationResult(false, "attachments(com.aurel.notes.core.database.entity.AttachmentEntity).\n"
                  + " Expected:\n" + _infoAttachments + "\n"
                  + " Found:\n" + _existingAttachments);
        }
        final HashMap<String, TableInfo.Column> _columnsNoteCrossRefs = new HashMap<String, TableInfo.Column>(3);
        _columnsNoteCrossRefs.put("source_note_id", new TableInfo.Column("source_note_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNoteCrossRefs.put("target_note_id", new TableInfo.Column("target_note_id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNoteCrossRefs.put("link_text", new TableInfo.Column("link_text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNoteCrossRefs = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysNoteCrossRefs.add(new TableInfo.ForeignKey("notes", "CASCADE", "NO ACTION", Arrays.asList("source_note_id"), Arrays.asList("id")));
        _foreignKeysNoteCrossRefs.add(new TableInfo.ForeignKey("notes", "CASCADE", "NO ACTION", Arrays.asList("target_note_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesNoteCrossRefs = new HashSet<TableInfo.Index>(2);
        _indicesNoteCrossRefs.add(new TableInfo.Index("index_note_cross_refs_source_note_id", false, Arrays.asList("source_note_id"), Arrays.asList("ASC")));
        _indicesNoteCrossRefs.add(new TableInfo.Index("index_note_cross_refs_target_note_id", false, Arrays.asList("target_note_id"), Arrays.asList("ASC")));
        final TableInfo _infoNoteCrossRefs = new TableInfo("note_cross_refs", _columnsNoteCrossRefs, _foreignKeysNoteCrossRefs, _indicesNoteCrossRefs);
        final TableInfo _existingNoteCrossRefs = TableInfo.read(db, "note_cross_refs");
        if (!_infoNoteCrossRefs.equals(_existingNoteCrossRefs)) {
          return new RoomOpenHelper.ValidationResult(false, "note_cross_refs(com.aurel.notes.core.database.entity.NoteCrossRefEntity).\n"
                  + " Expected:\n" + _infoNoteCrossRefs + "\n"
                  + " Found:\n" + _existingNoteCrossRefs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4bbc970662e684c78060ce4f383b1666", "fac9e767a2e85ccd4a1ad8b7dc37f832");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(1);
    _shadowTablesMap.put("notes_fts", "notes_fts_content");
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "notes","notes_fts","notebooks","tasks","attachments","note_cross_refs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `notes`");
      _db.execSQL("DELETE FROM `notes_fts`");
      _db.execSQL("DELETE FROM `notebooks`");
      _db.execSQL("DELETE FROM `tasks`");
      _db.execSQL("DELETE FROM `attachments`");
      _db.execSQL("DELETE FROM `note_cross_refs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(NoteDao.class, NoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotebookDao.class, NotebookDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TaskDao.class, TaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AttachmentDao.class, AttachmentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CrossRefDao.class, CrossRefDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public NoteDao noteDao() {
    if (_noteDao != null) {
      return _noteDao;
    } else {
      synchronized(this) {
        if(_noteDao == null) {
          _noteDao = new NoteDao_Impl(this);
        }
        return _noteDao;
      }
    }
  }

  @Override
  public NotebookDao notebookDao() {
    if (_notebookDao != null) {
      return _notebookDao;
    } else {
      synchronized(this) {
        if(_notebookDao == null) {
          _notebookDao = new NotebookDao_Impl(this);
        }
        return _notebookDao;
      }
    }
  }

  @Override
  public TaskDao taskDao() {
    if (_taskDao != null) {
      return _taskDao;
    } else {
      synchronized(this) {
        if(_taskDao == null) {
          _taskDao = new TaskDao_Impl(this);
        }
        return _taskDao;
      }
    }
  }

  @Override
  public AttachmentDao attachmentDao() {
    if (_attachmentDao != null) {
      return _attachmentDao;
    } else {
      synchronized(this) {
        if(_attachmentDao == null) {
          _attachmentDao = new AttachmentDao_Impl(this);
        }
        return _attachmentDao;
      }
    }
  }

  @Override
  public CrossRefDao crossRefDao() {
    if (_crossRefDao != null) {
      return _crossRefDao;
    } else {
      synchronized(this) {
        if(_crossRefDao == null) {
          _crossRefDao = new CrossRefDao_Impl(this);
        }
        return _crossRefDao;
      }
    }
  }
}
