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
import com.aurel.notes.core.database.entity.AttachmentEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class AttachmentDao_Impl implements AttachmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AttachmentEntity> __insertionAdapterOfAttachmentEntity;

  private final EntityDeletionOrUpdateAdapter<AttachmentEntity> __deletionAdapterOfAttachmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAttachmentById;

  public AttachmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAttachmentEntity = new EntityInsertionAdapter<AttachmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `attachments` (`id`,`note_id`,`type`,`file_path`,`file_name`,`file_size_bytes`,`duration_ms`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttachmentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getNoteId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getFilePath());
        statement.bindString(5, entity.getFileName());
        statement.bindLong(6, entity.getFileSizeBytes());
        if (entity.getDurationMs() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDurationMs());
        }
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfAttachmentEntity = new EntityDeletionOrUpdateAdapter<AttachmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `attachments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttachmentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAttachmentById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM attachments WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAttachment(final AttachmentEntity attachment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAttachmentEntity.insertAndReturnId(attachment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAttachments(final List<AttachmentEntity> attachments,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAttachmentEntity.insert(attachments);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAttachment(final AttachmentEntity attachment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAttachmentEntity.handle(attachment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAttachmentById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAttachmentById.acquire();
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
          __preparedStmtOfDeleteAttachmentById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AttachmentEntity>> getAttachmentsForNote(final long noteId) {
    final String _sql = "SELECT * FROM attachments WHERE note_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, noteId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"attachments"}, new Callable<List<AttachmentEntity>>() {
      @Override
      @NonNull
      public List<AttachmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "note_id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<AttachmentEntity> _result = new ArrayList<AttachmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttachmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpNoteId;
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final Long _tmpDurationMs;
            if (_cursor.isNull(_cursorIndexOfDurationMs)) {
              _tmpDurationMs = null;
            } else {
              _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AttachmentEntity(_tmpId,_tmpNoteId,_tmpType,_tmpFilePath,_tmpFileName,_tmpFileSizeBytes,_tmpDurationMs,_tmpCreatedAt);
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
  public Object getAttachmentById(final long id,
      final Continuation<? super AttachmentEntity> $completion) {
    final String _sql = "SELECT * FROM attachments WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AttachmentEntity>() {
      @Override
      @Nullable
      public AttachmentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "note_id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_ms");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final AttachmentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpNoteId;
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final Long _tmpDurationMs;
            if (_cursor.isNull(_cursorIndexOfDurationMs)) {
              _tmpDurationMs = null;
            } else {
              _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new AttachmentEntity(_tmpId,_tmpNoteId,_tmpType,_tmpFilePath,_tmpFileName,_tmpFileSizeBytes,_tmpDurationMs,_tmpCreatedAt);
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
  public Object getAllAttachmentsForBackup(
      final Continuation<? super List<AttachmentEntity>> $completion) {
    final String _sql = "SELECT `attachments`.`id` AS `id`, `attachments`.`note_id` AS `note_id`, `attachments`.`type` AS `type`, `attachments`.`file_path` AS `file_path`, `attachments`.`file_name` AS `file_name`, `attachments`.`file_size_bytes` AS `file_size_bytes`, `attachments`.`duration_ms` AS `duration_ms`, `attachments`.`created_at` AS `created_at` FROM attachments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AttachmentEntity>>() {
      @Override
      @NonNull
      public List<AttachmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfNoteId = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfFilePath = 3;
          final int _cursorIndexOfFileName = 4;
          final int _cursorIndexOfFileSizeBytes = 5;
          final int _cursorIndexOfDurationMs = 6;
          final int _cursorIndexOfCreatedAt = 7;
          final List<AttachmentEntity> _result = new ArrayList<AttachmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttachmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpNoteId;
            _tmpNoteId = _cursor.getLong(_cursorIndexOfNoteId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final Long _tmpDurationMs;
            if (_cursor.isNull(_cursorIndexOfDurationMs)) {
              _tmpDurationMs = null;
            } else {
              _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AttachmentEntity(_tmpId,_tmpNoteId,_tmpType,_tmpFilePath,_tmpFileName,_tmpFileSizeBytes,_tmpDurationMs,_tmpCreatedAt);
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
