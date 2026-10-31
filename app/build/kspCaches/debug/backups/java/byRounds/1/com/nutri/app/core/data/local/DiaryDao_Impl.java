package com.nutri.app.core.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
public final class DiaryDao_Impl implements DiaryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DiaryEntryEntity> __insertionAdapterOfDiaryEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public DiaryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDiaryEntryEntity = new EntityInsertionAdapter<DiaryEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `diary_entries` (`id`,`userId`,`date`,`mealType`,`dishId`,`dishName`,`grams`,`kcal`,`protein`,`fat`,`carbs`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiaryEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindString(4, entity.getMealType());
        statement.bindString(5, entity.getDishId());
        statement.bindString(6, entity.getDishName());
        statement.bindDouble(7, entity.getGrams());
        statement.bindDouble(8, entity.getKcal());
        statement.bindDouble(9, entity.getProtein());
        statement.bindDouble(10, entity.getFat());
        statement.bindDouble(11, entity.getCarbs());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diary_entries WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DiaryEntryEntity entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDiaryEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DiaryEntryEntity>> observeDay(final long userId, final String date) {
    final String _sql = "SELECT * FROM diary_entries WHERE userId = ? AND date = ? ORDER BY mealType";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DiaryEntryEntity>>() {
      @Override
      @NonNull
      public List<DiaryEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfDishId = CursorUtil.getColumnIndexOrThrow(_cursor, "dishId");
          final int _cursorIndexOfDishName = CursorUtil.getColumnIndexOrThrow(_cursor, "dishName");
          final int _cursorIndexOfGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "grams");
          final int _cursorIndexOfKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "kcal");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final List<DiaryEntryEntity> _result = new ArrayList<DiaryEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiaryEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpDishId;
            _tmpDishId = _cursor.getString(_cursorIndexOfDishId);
            final String _tmpDishName;
            _tmpDishName = _cursor.getString(_cursorIndexOfDishName);
            final float _tmpGrams;
            _tmpGrams = _cursor.getFloat(_cursorIndexOfGrams);
            final float _tmpKcal;
            _tmpKcal = _cursor.getFloat(_cursorIndexOfKcal);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            _item = new DiaryEntryEntity(_tmpId,_tmpUserId,_tmpDate,_tmpMealType,_tmpDishId,_tmpDishName,_tmpGrams,_tmpKcal,_tmpProtein,_tmpFat,_tmpCarbs);
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
  public Flow<List<DailyTotalRow>> observeDailyTotals(final long userId, final String fromDate) {
    final String _sql = "SELECT date, SUM(kcal) as total FROM diary_entries WHERE userId = ? AND date >= ? GROUP BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, fromDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DailyTotalRow>>() {
      @Override
      @NonNull
      public List<DailyTotalRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfTotal = 1;
          final List<DailyTotalRow> _result = new ArrayList<DailyTotalRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTotalRow _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpTotal;
            _tmpTotal = _cursor.getFloat(_cursorIndexOfTotal);
            _item = new DailyTotalRow(_tmpDate,_tmpTotal);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
