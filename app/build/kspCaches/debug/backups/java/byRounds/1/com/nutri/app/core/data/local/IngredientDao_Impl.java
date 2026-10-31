package com.nutri.app.core.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IngredientDao_Impl implements IngredientDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<IngredientEntity> __upsertionAdapterOfIngredientEntity;

  public IngredientDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfIngredientEntity = new EntityUpsertionAdapter<IngredientEntity>(new EntityInsertionAdapter<IngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `ingredients` (`id`,`name`,`proteinPer100`,`fatPer100`,`carbsPer100`,`unit`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getProteinPer100());
        statement.bindDouble(4, entity.getFatPer100());
        statement.bindDouble(5, entity.getCarbsPer100());
        statement.bindString(6, entity.getUnit());
      }
    }, new EntityDeletionOrUpdateAdapter<IngredientEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `ingredients` SET `id` = ?,`name` = ?,`proteinPer100` = ?,`fatPer100` = ?,`carbsPer100` = ?,`unit` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IngredientEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getProteinPer100());
        statement.bindDouble(4, entity.getFatPer100());
        statement.bindDouble(5, entity.getCarbsPer100());
        statement.bindString(6, entity.getUnit());
        statement.bindString(7, entity.getId());
      }
    });
  }

  @Override
  public Object upsertAll(final List<IngredientEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfIngredientEntity.upsert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object search(final String query,
      final Continuation<? super List<IngredientEntity>> $completion) {
    final String _sql = "SELECT * FROM ingredients WHERE name LIKE '%' || ? || '%' LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IngredientEntity>>() {
      @Override
      @NonNull
      public List<IngredientEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProteinPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPer100");
          final int _cursorIndexOfFatPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPer100");
          final int _cursorIndexOfCarbsPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPer100");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final List<IngredientEntity> _result = new ArrayList<IngredientEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IngredientEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final float _tmpProteinPer100;
            _tmpProteinPer100 = _cursor.getFloat(_cursorIndexOfProteinPer100);
            final float _tmpFatPer100;
            _tmpFatPer100 = _cursor.getFloat(_cursorIndexOfFatPer100);
            final float _tmpCarbsPer100;
            _tmpCarbsPer100 = _cursor.getFloat(_cursorIndexOfCarbsPer100);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            _item = new IngredientEntity(_tmpId,_tmpName,_tmpProteinPer100,_tmpFatPer100,_tmpCarbsPer100,_tmpUnit);
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
