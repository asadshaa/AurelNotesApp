package com.aurel.notes.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aurel.notes.core.database.converter.AurelTypeConverters;
import com.aurel.notes.core.database.entity.NoteEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class NoteDao_Impl implements NoteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NoteEntity> __insertionAdapterOfNoteEntity;

  private final AurelTypeConverters __aurelTypeConverters = new AurelTypeConverters();

  private final EntityDeletionOrUpdateAdapter<NoteEntity> __deletionAdapterOfNoteEntity;

  private final EntityDeletionOrUpdateAdapter<NoteEntity> __updateAdapterOfNoteEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteNoteById;

  private final SharedSQLiteStatement __preparedStmtOfMoveToTrash;

  private final SharedSQLiteStatement __preparedStmtOfRestoreFromTrash;

  private final SharedSQLiteStatement __preparedStmtOfPurgeExpiredTrash;

  private final SharedSQLiteStatement __preparedStmtOfEmptyTrash;

  private final SharedSQLiteStatement __preparedStmtOfSetPinned;

  private final SharedSQLiteStatement __preparedStmtOfSetArchived;

  private final SharedSQLiteStatement __preparedStmtOfSetVaultLocked;

  private final SharedSQLiteStatement __preparedStmtOfUpsertFts;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFts;

  public NoteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNoteEntity = new EntityInsertionAdapter<NoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notes` (`id`,`uuid`,`title`,`content`,`plain_text_summary`,`notebook_id`,`card_color_hex`,`is_pinned`,`is_archived`,`is_trashed`,`trashed_at`,`is_vault_locked`,`iv_hex`,`tags`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUuid());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, entity.getPlainTextSummary());
        if (entity.getNotebookId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getNotebookId());
        }
        statement.bindString(7, entity.getCardColorHex());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isArchived() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.isTrashed() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getTrashedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getTrashedAt());
        }
        final int _tmp_3 = entity.isVaultLocked() ? 1 : 0;
        statement.bindLong(12, _tmp_3);
        if (entity.getIvHex() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getIvHex());
        }
        final String _tmp_4 = __aurelTypeConverters.fromStringList(entity.getTags());
        statement.bindString(14, _tmp_4);
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfNoteEntity = new EntityDeletionOrUpdateAdapter<NoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `notes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfNoteEntity = new EntityDeletionOrUpdateAdapter<NoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `notes` SET `id` = ?,`uuid` = ?,`title` = ?,`content` = ?,`plain_text_summary` = ?,`notebook_id` = ?,`card_color_hex` = ?,`is_pinned` = ?,`is_archived` = ?,`is_trashed` = ?,`trashed_at` = ?,`is_vault_locked` = ?,`iv_hex` = ?,`tags` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUuid());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, entity.getPlainTextSummary());
        if (entity.getNotebookId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getNotebookId());
        }
        statement.bindString(7, entity.getCardColorHex());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isArchived() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.isTrashed() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getTrashedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getTrashedAt());
        }
        final int _tmp_3 = entity.isVaultLocked() ? 1 : 0;
        statement.bindLong(12, _tmp_3);
        if (entity.getIvHex() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getIvHex());
        }
        final String _tmp_4 = __aurelTypeConverters.fromStringList(entity.getTags());
        statement.bindString(14, _tmp_4);
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteNoteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notes WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMoveToTrash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notes SET is_trashed = 1, trashed_at = ?, is_pinned = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestoreFromTrash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notes SET is_trashed = 0, trashed_at = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeExpiredTrash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notes WHERE is_trashed = 1 AND trashed_at IS NOT NULL AND trashed_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfEmptyTrash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notes WHERE is_trashed = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetPinned = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notes SET is_pinned = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetArchived = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notes SET is_archived = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetVaultLocked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notes SET is_vault_locked = ?, iv_hex = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpsertFts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "INSERT OR REPLACE INTO notes_fts(rowid, title, content, tags) VALUES (?, ?, ?, ?)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteFts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notes_fts WHERE rowid = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertNote(final NoteEntity note, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfNoteEntity.insertAndReturnId(note);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertNotes(final List<NoteEntity> notes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteEntity.insert(notes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteNote(final NoteEntity note, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNoteEntity.handle(note);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateNote(final NoteEntity note, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfNoteEntity.handle(note);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertNoteWithFts(final NoteEntity note,
      final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> NoteDao.DefaultImpls.insertNoteWithFts(NoteDao_Impl.this, note, __cont), $completion);
  }

  @Override
  public Object updateNoteWithFts(final NoteEntity note,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> NoteDao.DefaultImpls.updateNoteWithFts(NoteDao_Impl.this, note, __cont), $completion);
  }

  @Override
  public Object deleteNoteWithFts(final NoteEntity note,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> NoteDao.DefaultImpls.deleteNoteWithFts(NoteDao_Impl.this, note, __cont), $completion);
  }

  @Override
  public Object deleteNoteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteNoteById.acquire();
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
          __preparedStmtOfDeleteNoteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object moveToTrash(final long id, final long trashedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMoveToTrash.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, trashedAt);
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
          __preparedStmtOfMoveToTrash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreFromTrash(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestoreFromTrash.acquire();
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
          __preparedStmtOfRestoreFromTrash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object purgeExpiredTrash(final long expirationTime,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeExpiredTrash.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, expirationTime);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPurgeExpiredTrash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object emptyTrash(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEmptyTrash.acquire();
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
          __preparedStmtOfEmptyTrash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setPinned(final long id, final boolean isPinned,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetPinned.acquire();
        int _argIndex = 1;
        final int _tmp = isPinned ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfSetPinned.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setArchived(final long id, final boolean isArchived,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetArchived.acquire();
        int _argIndex = 1;
        final int _tmp = isArchived ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfSetArchived.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setVaultLocked(final long id, final boolean isLocked, final String ivHex,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetVaultLocked.acquire();
        int _argIndex = 1;
        final int _tmp = isLocked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (ivHex == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, ivHex);
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
          __preparedStmtOfSetVaultLocked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertFts(final long rowid, final String title, final String content,
      final String tags, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpsertFts.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, rowid);
        _argIndex = 2;
        _stmt.bindString(_argIndex, title);
        _argIndex = 3;
        _stmt.bindString(_argIndex, content);
        _argIndex = 4;
        _stmt.bindString(_argIndex, tags);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeInsert();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpsertFts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFts(final long rowid, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFts.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, rowid);
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
          __preparedStmtOfDeleteFts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NoteEntity>> getActiveNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_trashed = 0 AND is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getActiveNonVaultNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND is_vault_locked = 0 ORDER BY is_pinned DESC, updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getVaultNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND is_vault_locked = 1 ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getArchivedNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_trashed = 0 AND is_archived = 1 ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getTrashedNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_trashed = 1 ORDER BY trashed_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getNotesByNotebook(final long notebookId) {
    final String _sql = "SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 AND notebook_id = ? ORDER BY is_pinned DESC, updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, notebookId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getNoteById(final long id, final Continuation<? super NoteEntity> $completion) {
    final String _sql = "SELECT * FROM notes WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NoteEntity>() {
      @Override
      @Nullable
      public NoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final NoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<NoteEntity> getNoteByIdFlow(final long id) {
    final String _sql = "SELECT * FROM notes WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<NoteEntity>() {
      @Override
      @Nullable
      public NoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final NoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object getNoteByUuid(final String uuid,
      final Continuation<? super NoteEntity> $completion) {
    final String _sql = "SELECT * FROM notes WHERE uuid = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, uuid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NoteEntity>() {
      @Override
      @Nullable
      public NoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final NoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getNoteByTitle(final String title,
      final Continuation<? super NoteEntity> $completion) {
    final String _sql = "SELECT * FROM notes WHERE LOWER(title) = LOWER(?) AND is_trashed = 0 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, title);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NoteEntity>() {
      @Override
      @Nullable
      public NoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final NoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> searchNotes(final String query) {
    final String _sql = "\n"
            + "        SELECT notes.* FROM notes\n"
            + "        JOIN notes_fts ON notes.id = notes_fts.rowid\n"
            + "        WHERE notes_fts MATCH ? AND notes.is_trashed = 0\n"
            + "        ORDER BY notes.is_pinned DESC, notes.updated_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes",
        "notes_fts"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getPinnedNotes() {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes WHERE is_pinned = 1 AND is_trashed = 0 AND is_archived = 0 ORDER BY updated_at DESC LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<NoteEntity>> getRecentNotes(final int limit) {
    final String _sql = "SELECT * FROM notes WHERE is_trashed = 0 AND is_archived = 0 ORDER BY updated_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUuid = CursorUtil.getColumnIndexOrThrow(_cursor, "uuid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPlainTextSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "plain_text_summary");
          final int _cursorIndexOfNotebookId = CursorUtil.getColumnIndexOrThrow(_cursor, "notebook_id");
          final int _cursorIndexOfCardColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "card_color_hex");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "is_pinned");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsTrashed = CursorUtil.getColumnIndexOrThrow(_cursor, "is_trashed");
          final int _cursorIndexOfTrashedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "trashed_at");
          final int _cursorIndexOfIsVaultLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_vault_locked");
          final int _cursorIndexOfIvHex = CursorUtil.getColumnIndexOrThrow(_cursor, "iv_hex");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Integer> getActiveNoteCount() {
    final String _sql = "SELECT COUNT(*) FROM notes WHERE is_trashed = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<Integer>() {
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
  public Object getAllNotesForBackup(final Continuation<? super List<NoteEntity>> $completion) {
    final String _sql = "SELECT `notes`.`id` AS `id`, `notes`.`uuid` AS `uuid`, `notes`.`title` AS `title`, `notes`.`content` AS `content`, `notes`.`plain_text_summary` AS `plain_text_summary`, `notes`.`notebook_id` AS `notebook_id`, `notes`.`card_color_hex` AS `card_color_hex`, `notes`.`is_pinned` AS `is_pinned`, `notes`.`is_archived` AS `is_archived`, `notes`.`is_trashed` AS `is_trashed`, `notes`.`trashed_at` AS `trashed_at`, `notes`.`is_vault_locked` AS `is_vault_locked`, `notes`.`iv_hex` AS `iv_hex`, `notes`.`tags` AS `tags`, `notes`.`created_at` AS `created_at`, `notes`.`updated_at` AS `updated_at` FROM notes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfUuid = 1;
          final int _cursorIndexOfTitle = 2;
          final int _cursorIndexOfContent = 3;
          final int _cursorIndexOfPlainTextSummary = 4;
          final int _cursorIndexOfNotebookId = 5;
          final int _cursorIndexOfCardColorHex = 6;
          final int _cursorIndexOfIsPinned = 7;
          final int _cursorIndexOfIsArchived = 8;
          final int _cursorIndexOfIsTrashed = 9;
          final int _cursorIndexOfTrashedAt = 10;
          final int _cursorIndexOfIsVaultLocked = 11;
          final int _cursorIndexOfIvHex = 12;
          final int _cursorIndexOfTags = 13;
          final int _cursorIndexOfCreatedAt = 14;
          final int _cursorIndexOfUpdatedAt = 15;
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUuid;
            _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPlainTextSummary;
            _tmpPlainTextSummary = _cursor.getString(_cursorIndexOfPlainTextSummary);
            final Long _tmpNotebookId;
            if (_cursor.isNull(_cursorIndexOfNotebookId)) {
              _tmpNotebookId = null;
            } else {
              _tmpNotebookId = _cursor.getLong(_cursorIndexOfNotebookId);
            }
            final String _tmpCardColorHex;
            _tmpCardColorHex = _cursor.getString(_cursorIndexOfCardColorHex);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final boolean _tmpIsArchived;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_1 != 0;
            final boolean _tmpIsTrashed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTrashed);
            _tmpIsTrashed = _tmp_2 != 0;
            final Long _tmpTrashedAt;
            if (_cursor.isNull(_cursorIndexOfTrashedAt)) {
              _tmpTrashedAt = null;
            } else {
              _tmpTrashedAt = _cursor.getLong(_cursorIndexOfTrashedAt);
            }
            final boolean _tmpIsVaultLocked;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsVaultLocked);
            _tmpIsVaultLocked = _tmp_3 != 0;
            final String _tmpIvHex;
            if (_cursor.isNull(_cursorIndexOfIvHex)) {
              _tmpIvHex = null;
            } else {
              _tmpIvHex = _cursor.getString(_cursorIndexOfIvHex);
            }
            final List<String> _tmpTags;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTags);
            _tmpTags = __aurelTypeConverters.toStringList(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new NoteEntity(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpPlainTextSummary,_tmpNotebookId,_tmpCardColorHex,_tmpIsPinned,_tmpIsArchived,_tmpIsTrashed,_tmpTrashedAt,_tmpIsVaultLocked,_tmpIvHex,_tmpTags,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object deleteNotesByIds(final List<Long> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM notes WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object moveMultipleToTrash(final List<Long> ids, final long trashedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE notes SET is_trashed = 1, trashed_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", is_pinned = 0 WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, trashedAt);
        _argIndex = 2;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreMultipleFromTrash(final List<Long> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE notes SET is_trashed = 0, trashed_at = NULL WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
