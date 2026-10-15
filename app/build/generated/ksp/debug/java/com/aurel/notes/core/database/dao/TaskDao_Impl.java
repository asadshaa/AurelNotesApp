package com.aurel.notes.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aurel.notes.core.database.entity.TaskEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskEntity> __insertionAdapterOfTaskEntity;

  private final EntityDeletionOrUpdateAdapter<TaskEntity> __deletionAdapterOfTaskEntity;

  private final EntityDeletionOrUpdateAdapter<TaskEntity> __updateAdapterOfTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTaskById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTasksByParentNote;

  private final SharedSQLiteStatement __preparedStmtOfSetTaskCompleted;

  private final SharedSQLiteStatement __preparedStmtOfSnoozeTask;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskEntity = new EntityInsertionAdapter<TaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tasks` (`id`,`parent_note_id`,`title`,`is_completed`,`priority`,`due_date_time`,`reminder_enabled`,`recurrence`,`completed_at`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getParentNoteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getParentNoteId());
        }
        statement.bindString(3, entity.getTitle());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindString(5, entity.getPriority());
        if (entity.getDueDateTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDueDateTime());
        }
        final int _tmp_1 = entity.getReminderEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindString(8, entity.getRecurrence());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCompletedAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfTaskEntity = new EntityDeletionOrUpdateAdapter<TaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTaskEntity = new EntityDeletionOrUpdateAdapter<TaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`parent_note_id` = ?,`title` = ?,`is_completed` = ?,`priority` = ?,`due_date_time` = ?,`reminder_enabled` = ?,`recurrence` = ?,`completed_at` = ?,`created_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getParentNoteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getParentNoteId());
        }
        statement.bindString(3, entity.getTitle());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindString(5, entity.getPriority());
        if (entity.getDueDateTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDueDateTime());
        }
        final int _tmp_1 = entity.getReminderEnabled() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindString(8, entity.getRecurrence());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCompletedAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTaskById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTasksByParentNote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks WHERE parent_note_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetTaskCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tasks SET is_completed = ?, completed_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSnoozeTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tasks SET due_date_time = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTask(final TaskEntity task, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTaskEntity.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTasks(final List<TaskEntity> tasks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTaskEntity.insert(tasks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTask(final TaskEntity task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTaskEntity.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTask(final TaskEntity task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTaskEntity.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTaskById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTaskById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteTaskById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTasksByParentNote(final long noteId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTasksByParentNote.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, noteId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteTasksByParentNote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setTaskCompleted(final long id, final boolean isCompleted, final Long completedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetTaskCompleted.acquire();
        int _argIndex = 1;
        final int _tmp = isCompleted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (completedAt == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, completedAt);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetTaskCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object snoozeTask(final long id, final long newDueDateTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSnoozeTask.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, newDueDateTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSnoozeTask.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TaskEntity>> getAllTasks() {
    final String _sql = "SELECT `tasks`.`id` AS `id`, `tasks`.`parent_note_id` AS `parent_note_id`, `tasks`.`title` AS `title`, `tasks`.`is_completed` AS `is_completed`, `tasks`.`priority` AS `priority`, `tasks`.`due_date_time` AS `due_date_time`, `tasks`.`reminder_enabled` AS `reminder_enabled`, `tasks`.`recurrence` AS `recurrence`, `tasks`.`completed_at` AS `completed_at`, `tasks`.`created_at` AS `created_at` FROM tasks ORDER BY is_completed ASC, due_date_time ASC, created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfParentNoteId = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfIsCompleted = 3;
          final int _cursorIndexOfPriority = 4;
          final int _cursorIndexOfDueDateTime = 5;
          final int _cursorIndexOfReminderEnabled = 6;
          final int _cursorIndexOfRecurrence = 7;
          final int _cursorIndexOfCompletedAt = 8;
          final int _cursorIndexOfCreatedAt = 9;
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskEntity>> getTodayTasks(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time >= ? AND due_date_time <= ? ORDER BY due_date_time ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskEntity>> getUpcomingTasks(final long endOfDay) {
    final String _sql = "SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time > ? ORDER BY due_date_time ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskEntity>> getOverdueTasks(final long now) {
    final String _sql = "SELECT * FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time < ? ORDER BY due_date_time ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskEntity>> getCompletedTasks() {
    final String _sql = "SELECT `tasks`.`id` AS `id`, `tasks`.`parent_note_id` AS `parent_note_id`, `tasks`.`title` AS `title`, `tasks`.`is_completed` AS `is_completed`, `tasks`.`priority` AS `priority`, `tasks`.`due_date_time` AS `due_date_time`, `tasks`.`reminder_enabled` AS `reminder_enabled`, `tasks`.`recurrence` AS `recurrence`, `tasks`.`completed_at` AS `completed_at`, `tasks`.`created_at` AS `created_at` FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfParentNoteId = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfIsCompleted = 3;
          final int _cursorIndexOfPriority = 4;
          final int _cursorIndexOfDueDateTime = 5;
          final int _cursorIndexOfReminderEnabled = 6;
          final int _cursorIndexOfRecurrence = 7;
          final int _cursorIndexOfCompletedAt = 8;
          final int _cursorIndexOfCreatedAt = 9;
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskEntity>> getTasksForNote(final long noteId) {
    final String _sql = "SELECT * FROM tasks WHERE parent_note_id = ? ORDER BY is_completed ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, noteId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTaskById(final long id, final Continuation<? super TaskEntity> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TaskEntity>() {
      @Override
      @Nullable
      public TaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final TaskEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUpcomingReminderTasks(final long now,
      final Continuation<? super List<TaskEntity>> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE is_completed = 0 AND reminder_enabled = 1 AND due_date_time >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfParentNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_note_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDueDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_time");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_enabled");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> getPendingTaskCount() {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE is_completed = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTodayPendingTaskCount(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE is_completed = 0 AND due_date_time IS NOT NULL AND due_date_time >= ? AND due_date_time <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTodayCompletedTaskCount(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE is_completed = 1 AND due_date_time IS NOT NULL AND due_date_time >= ? AND due_date_time <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllTasksForBackup(final Continuation<? super List<TaskEntity>> $completion) {
    final String _sql = "SELECT `tasks`.`id` AS `id`, `tasks`.`parent_note_id` AS `parent_note_id`, `tasks`.`title` AS `title`, `tasks`.`is_completed` AS `is_completed`, `tasks`.`priority` AS `priority`, `tasks`.`due_date_time` AS `due_date_time`, `tasks`.`reminder_enabled` AS `reminder_enabled`, `tasks`.`recurrence` AS `recurrence`, `tasks`.`completed_at` AS `completed_at`, `tasks`.`created_at` AS `created_at` FROM tasks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TaskEntity>>() {
      @Override
      @NonNull
      public List<TaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfParentNoteId = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfIsCompleted = 3;
          final int _cursorIndexOfPriority = 4;
          final int _cursorIndexOfDueDateTime = 5;
          final int _cursorIndexOfReminderEnabled = 6;
          final int _cursorIndexOfRecurrence = 7;
          final int _cursorIndexOfCompletedAt = 8;
          final int _cursorIndexOfCreatedAt = 9;
          final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpParentNoteId;
            if (_cursor.isNull(_cursorIndexOfParentNoteId)) {
              _tmpParentNoteId = null;
            } else {
              _tmpParentNoteId = _cursor.getLong(_cursorIndexOfParentNoteId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpDueDateTime;
            if (_cursor.isNull(_cursorIndexOfDueDateTime)) {
              _tmpDueDateTime = null;
            } else {
              _tmpDueDateTime = _cursor.getLong(_cursorIndexOfDueDateTime);
            }
            final boolean _tmpReminderEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_1 != 0;
            final String _tmpRecurrence;
            _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TaskEntity(_tmpId,_tmpParentNoteId,_tmpTitle,_tmpIsCompleted,_tmpPriority,_tmpDueDateTime,_tmpReminderEnabled,_tmpRecurrence,_tmpCompletedAt,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
