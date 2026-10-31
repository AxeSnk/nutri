package com.nutri.app.core.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class DishDao_Impl implements DishDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfDeleteStaleRemote;

  private final EntityUpsertionAdapter<DishEntity> __upsertionAdapterOfDishEntity;

  public DishDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfDeleteStaleRemote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dishes WHERE isFromRemote = 1 AND updatedAt < ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfDishEntity = new EntityUpsertionAdapter<DishEntity>(new EntityInsertionAdapter<DishEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `dishes` (`id`,`name`,`imageUrl`,`kcal`,`protein`,`fat`,`carbs`,`tags`,`cookMinutes`,`isFromRemote`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DishEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getImageUrl() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getImageUrl());
        }
        statement.bindLong(4, entity.getKcal());
        statement.bindDouble(5, entity.getProtein());
        statement.bindDouble(6, entity.getFat());
        statement.bindDouble(7, entity.getCarbs());
        statement.bindString(8, entity.getTags());
        statement.bindLong(9, entity.getCookMinutes());
        final int _tmp = entity.isFromRemote() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getUpdatedAt());
      }
    }, new EntityDeletionOrUpdateAdapter<DishEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `dishes` SET `id` = ?,`name` = ?,`imageUrl` = ?,`kcal` = ?,`protein` = ?,`fat` = ?,`carbs` = ?,`tags` = ?,`cookMinutes` = ?,`isFromRemote` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DishEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getImageUrl() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getImageUrl());
        }
        statement.bindLong(4, entity.getKcal());
        statement.bindDouble(5, entity.getProtein());
        statement.bindDouble(6, entity.getFat());
        statement.bindDouble(7, entity.getCarbs());
        statement.bindString(8, entity.getTags());
        statement.bindLong(9, entity.getCookMinutes());
        final int _tmp = entity.isFromRemote() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindString(12, entity.getId());
      }
    });
  }

  @Override
  public Object deleteStaleRemote(final long staleBefore,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteStaleRemote.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, staleBefore);
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
          __preparedStmtOfDeleteStaleRemote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<DishEntity> dishes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDishEntity.upsert(dishes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DishEntity>> observeAll() {
    final String _sql = "SELECT * FROM dishes ORDER BY kcal";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"dishes"}, new Callable<List<DishEntity>>() {
      @Override
      @NonNull
      public List<DishEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "kcal");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCookMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cookMinutes");
          final int _cursorIndexOfIsFromRemote = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromRemote");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DishEntity> _result = new ArrayList<DishEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DishEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final int _tmpKcal;
            _tmpKcal = _cursor.getInt(_cursorIndexOfKcal);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final int _tmpCookMinutes;
            _tmpCookMinutes = _cursor.getInt(_cursorIndexOfCookMinutes);
            final boolean _tmpIsFromRemote;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFromRemote);
            _tmpIsFromRemote = _tmp != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DishEntity(_tmpId,_tmpName,_tmpImageUrl,_tmpKcal,_tmpProtein,_tmpFat,_tmpCarbs,_tmpTags,_tmpCookMinutes,_tmpIsFromRemote,_tmpUpdatedAt);
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
  public Object getById(final String id, final Continuation<? super DishEntity> $completion) {
    final String _sql = "SELECT * FROM dishes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DishEntity>() {
      @Override
      @Nullable
      public DishEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "kcal");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfCookMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cookMinutes");
          final int _cursorIndexOfIsFromRemote = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromRemote");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DishEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final int _tmpKcal;
            _tmpKcal = _cursor.getInt(_cursorIndexOfKcal);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final int _tmpCookMinutes;
            _tmpCookMinutes = _cursor.getInt(_cursorIndexOfCookMinutes);
            final boolean _tmpIsFromRemote;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFromRemote);
            _tmpIsFromRemote = _tmp != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DishEntity(_tmpId,_tmpName,_tmpImageUrl,_tmpKcal,_tmpProtein,_tmpFat,_tmpCarbs,_tmpTags,_tmpCookMinutes,_tmpIsFromRemote,_tmpUpdatedAt);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM dishes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
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
