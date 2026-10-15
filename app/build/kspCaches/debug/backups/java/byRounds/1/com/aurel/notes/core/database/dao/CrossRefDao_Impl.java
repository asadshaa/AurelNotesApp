package com.aurel.notes.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aurel.notes.core.database.converter.AurelTypeConverters;
import com.aurel.notes.core.database.entity.NoteCrossRefEntity;
import com.aurel.notes.core.database.entity.NoteEntity;
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
public final class CrossRefDao_Impl implements CrossRefDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NoteCrossRefEntity> __insertionAdapterOfNoteCrossRefEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCrossRefsForSource;

  private final AurelTypeConverters __aurelTypeConverters = new AurelTypeConverters();

  public CrossRefDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNoteCrossRefEntity = new EntityInsertionAdapter<NoteCrossRefEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `note_cross_refs` (`source_note_id`,`target_note_id`,`link_text`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteCrossRefEntity entity) {
        statement.bindLong(1, entity.getSourceNoteId());
        statement.bindLong(2, entity.getTargetNoteId());
        statement.bindString(3, entity.getLinkText());
      }
    };
    this.__preparedStmtOfDeleteCrossRefsForSource = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM note_cross_refs WHERE source_note_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCrossRef(final NoteCrossRefEntity ref,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteCrossRefEntity.insert(ref);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCrossRefs(final List<NoteCrossRefEntity> refs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteCrossRefEntity.insert(refs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object syncCrossRefsForSource(final long sourceNoteId, final List<NoteCrossRefEntity> refs,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> CrossRefDao.DefaultImpls.syncCrossRefsForSource(CrossRefDao_Impl.this, sourceNoteId, refs, __cont), $completion);
  }

  @Override
  public Object deleteCrossRefsForSource(final long sourceNoteId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCrossRefsForSource.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, sourceNoteId);
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
          __preparedStmtOfDeleteCrossRefsForSource.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NoteCrossRefEntity>> getOutgoingLinks(final long sourceNoteId) {
    final String _sql = "SELECT * FROM note_cross_refs WHERE source_note_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sourceNoteId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"note_cross_refs"}, new Callable<List<NoteCrossRefEntity>>() {
      @Override
      @NonNull
      public List<NoteCrossRefEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSourceNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_note_id");
          final int _cursorIndexOfTargetNoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "target_note_id");
          final int _cursorIndexOfLinkText = CursorUtil.getColumnIndexOrThrow(_cursor, "link_text");
          final List<NoteCrossRefEntity> _result = new ArrayList<NoteCrossRefEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteCrossRefEntity _item;
            final long _tmpSourceNoteId;
            _tmpSourceNoteId = _cursor.getLong(_cursorIndexOfSourceNoteId);
            final long _tmpTargetNoteId;
            _tmpTargetNoteId = _cursor.getLong(_cursorIndexOfTargetNoteId);
            final String _tmpLinkText;
            _tmpLinkText = _cursor.getString(_cursorIndexOfLinkText);
            _item = new NoteCrossRefEntity(_tmpSourceNoteId,_tmpTargetNoteId,_tmpLinkText);
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
  public Flow<List<NoteEntity>> getBacklinksForNote(final long targetNoteId) {
    final String _sql = "\n"
            + "        SELECT notes.* FROM notes\n"
            + "        JOIN note_cross_refs ON notes.id = note_cross_refs.source_note_id\n"
            + "        WHERE note_cross_refs.target_note_id = ? AND notes.is_trashed = 0\n"
            + "        ORDER BY notes.updated_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, targetNoteId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes",
        "note_cross_refs"}, new Callable<List<NoteEntity>>() {
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
  public Object getAllCrossRefsForBackup(
      final Continuation<? super List<NoteCrossRefEntity>> $completion) {
    final String _sql = "SELECT `note_cross_refs`.`source_note_id` AS `source_note_id`, `note_cross_refs`.`target_note_id` AS `target_note_id`, `note_cross_refs`.`link_text` AS `link_text` FROM note_cross_refs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NoteCrossRefEntity>>() {
      @Override
      @NonNull
      public List<NoteCrossRefEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSourceNoteId = 0;
          final int _cursorIndexOfTargetNoteId = 1;
          final int _cursorIndexOfLinkText = 2;
          final List<NoteCrossRefEntity> _result = new ArrayList<NoteCrossRefEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteCrossRefEntity _item;
            final long _tmpSourceNoteId;
            _tmpSourceNoteId = _cursor.getLong(_cursorIndexOfSourceNoteId);
            final long _tmpTargetNoteId;
            _tmpTargetNoteId = _cursor.getLong(_cursorIndexOfTargetNoteId);
            final String _tmpLinkText;
            _tmpLinkText = _cursor.getString(_cursorIndexOfLinkText);
            _item = new NoteCrossRefEntity(_tmpSourceNoteId,_tmpTargetNoteId,_tmpLinkText);
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
