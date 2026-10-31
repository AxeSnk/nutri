package com.nutri.app.core.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GoalDao_Impl implements GoalDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<GoalEntity> __upsertionAdapterOfGoalEntity;

  public GoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfGoalEntity = new EntityUpsertionAdapter<GoalEntity>(new EntityInsertionAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `goals` (`userId`,`dailyKcal`,`dailyProtein`,`dailyFat`,`dailyCarbs`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindLong(1, entity.getUserId());
        statement.bindLong(2, entity.getDailyKcal());
        statement.bindLong(3, entity.getDailyProtein());
        statement.bindLong(4, entity.getDailyFat());
        statement.bindLong(5, entity.getDailyCarbs());
      }
    }, new EntityDeletionOrUpdateAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `goals` SET `userId` = ?,`dailyKcal` = ?,`dailyProtein` = ?,`dailyFat` = ?,`dailyCarbs` = ? WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindLong(1, entity.getUserId());
        statement.bindLong(2, entity.getDailyKcal());
        statement.bindLong(3, entity.getDailyProtein());
        statement.bindLong(4, entity.getDailyFat());
        statement.bindLong(5, entity.getDailyCarbs());
        statement.bindLong(6, entity.getUserId());
      }
    });
  }

  @Override
  public Object upsert(final GoalEntity goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfGoalEntity.upsert(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<GoalEntity> observeGoal() {
    final String _sql = "SELECT * FROM goals LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"goals"}, new Callable<GoalEntity>() {
      @Override
      @Nullable
      public GoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDailyKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyKcal");
          final int _cursorIndexOfDailyProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyProtein");
          final int _cursorIndexOfDailyFat = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyFat");
          final int _cursorIndexOfDailyCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCarbs");
          final GoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final int _tmpDailyKcal;
            _tmpDailyKcal = _cursor.getInt(_cursorIndexOfDailyKcal);
            final int _tmpDailyProtein;
            _tmpDailyProtein = _cursor.getInt(_cursorIndexOfDailyProtein);
            final int _tmpDailyFat;
            _tmpDailyFat = _cursor.getInt(_cursorIndexOfDailyFat);
            final int _tmpDailyCarbs;
            _tmpDailyCarbs = _cursor.getInt(_cursorIndexOfDailyCarbs);
            _result = new GoalEntity(_tmpUserId,_tmpDailyKcal,_tmpDailyProtein,_tmpDailyFat,_tmpDailyCarbs);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
