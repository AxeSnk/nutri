package com.nutri.app.core.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
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
public final class NutriDatabase_Impl extends NutriDatabase {
  private volatile DishDao _dishDao;

  private volatile IngredientDao _ingredientDao;

  private volatile DiaryDao _diaryDao;

  private volatile UserDao _userDao;

  private volatile GoalDao _goalDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `dishes` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `imageUrl` TEXT, `kcal` INTEGER NOT NULL, `protein` REAL NOT NULL, `fat` REAL NOT NULL, `carbs` REAL NOT NULL, `tags` TEXT NOT NULL, `cookMinutes` INTEGER NOT NULL, `isFromRemote` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ingredients` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `proteinPer100` REAL NOT NULL, `fatPer100` REAL NOT NULL, `carbsPer100` REAL NOT NULL, `unit` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `diary_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `mealType` TEXT NOT NULL, `dishId` TEXT NOT NULL, `dishName` TEXT NOT NULL, `grams` REAL NOT NULL, `kcal` REAL NOT NULL, `protein` REAL NOT NULL, `fat` REAL NOT NULL, `carbs` REAL NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diary_entries_userId_date` ON `diary_entries` (`userId`, `date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER NOT NULL, `email` TEXT, `phone` TEXT, `displayName` TEXT NOT NULL, `isPremium` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`userId` INTEGER NOT NULL, `dailyKcal` INTEGER NOT NULL, `dailyProtein` INTEGER NOT NULL, `dailyFat` INTEGER NOT NULL, `dailyCarbs` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '91fdb11ade4acb0b4701a6d96dd852e8')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `dishes`");
        db.execSQL("DROP TABLE IF EXISTS `ingredients`");
        db.execSQL("DROP TABLE IF EXISTS `diary_entries`");
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `goals`");
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
        final HashMap<String, TableInfo.Column> _columnsDishes = new HashMap<String, TableInfo.Column>(11);
        _columnsDishes.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("kcal", new TableInfo.Column("kcal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("cookMinutes", new TableInfo.Column("cookMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("isFromRemote", new TableInfo.Column("isFromRemote", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDishes.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDishes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDishes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDishes = new TableInfo("dishes", _columnsDishes, _foreignKeysDishes, _indicesDishes);
        final TableInfo _existingDishes = TableInfo.read(db, "dishes");
        if (!_infoDishes.equals(_existingDishes)) {
          return new RoomOpenHelper.ValidationResult(false, "dishes(com.nutri.app.core.data.local.DishEntity).\n"
                  + " Expected:\n" + _infoDishes + "\n"
                  + " Found:\n" + _existingDishes);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredients = new HashMap<String, TableInfo.Column>(6);
        _columnsIngredients.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("proteinPer100", new TableInfo.Column("proteinPer100", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("fatPer100", new TableInfo.Column("fatPer100", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("carbsPer100", new TableInfo.Column("carbsPer100", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredients = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIngredients = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIngredients = new TableInfo("ingredients", _columnsIngredients, _foreignKeysIngredients, _indicesIngredients);
        final TableInfo _existingIngredients = TableInfo.read(db, "ingredients");
        if (!_infoIngredients.equals(_existingIngredients)) {
          return new RoomOpenHelper.ValidationResult(false, "ingredients(com.nutri.app.core.data.local.IngredientEntity).\n"
                  + " Expected:\n" + _infoIngredients + "\n"
                  + " Found:\n" + _existingIngredients);
        }
        final HashMap<String, TableInfo.Column> _columnsDiaryEntries = new HashMap<String, TableInfo.Column>(11);
        _columnsDiaryEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("dishId", new TableInfo.Column("dishId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("dishName", new TableInfo.Column("dishName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("grams", new TableInfo.Column("grams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("kcal", new TableInfo.Column("kcal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiaryEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiaryEntries = new HashSet<TableInfo.Index>(1);
        _indicesDiaryEntries.add(new TableInfo.Index("index_diary_entries_userId_date", false, Arrays.asList("userId", "date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoDiaryEntries = new TableInfo("diary_entries", _columnsDiaryEntries, _foreignKeysDiaryEntries, _indicesDiaryEntries);
        final TableInfo _existingDiaryEntries = TableInfo.read(db, "diary_entries");
        if (!_infoDiaryEntries.equals(_existingDiaryEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "diary_entries(com.nutri.app.core.data.local.DiaryEntryEntity).\n"
                  + " Expected:\n" + _infoDiaryEntries + "\n"
                  + " Found:\n" + _existingDiaryEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(5);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phone", new TableInfo.Column("phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("isPremium", new TableInfo.Column("isPremium", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.nutri.app.core.data.local.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsGoals = new HashMap<String, TableInfo.Column>(5);
        _columnsGoals.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("dailyKcal", new TableInfo.Column("dailyKcal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("dailyProtein", new TableInfo.Column("dailyProtein", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("dailyFat", new TableInfo.Column("dailyFat", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("dailyCarbs", new TableInfo.Column("dailyCarbs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGoals = new TableInfo("goals", _columnsGoals, _foreignKeysGoals, _indicesGoals);
        final TableInfo _existingGoals = TableInfo.read(db, "goals");
        if (!_infoGoals.equals(_existingGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "goals(com.nutri.app.core.data.local.GoalEntity).\n"
                  + " Expected:\n" + _infoGoals + "\n"
                  + " Found:\n" + _existingGoals);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "91fdb11ade4acb0b4701a6d96dd852e8", "1d90de5aae2a5d27e05876006136ea22");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "dishes","ingredients","diary_entries","users","goals");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `dishes`");
      _db.execSQL("DELETE FROM `ingredients`");
      _db.execSQL("DELETE FROM `diary_entries`");
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `goals`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(DishDao.class, DishDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IngredientDao.class, IngredientDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiaryDao.class, DiaryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoalDao.class, GoalDao_Impl.getRequiredConverters());
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
  public DishDao dishDao() {
    if (_dishDao != null) {
      return _dishDao;
    } else {
      synchronized(this) {
        if(_dishDao == null) {
          _dishDao = new DishDao_Impl(this);
        }
        return _dishDao;
      }
    }
  }

  @Override
  public IngredientDao ingredientDao() {
    if (_ingredientDao != null) {
      return _ingredientDao;
    } else {
      synchronized(this) {
        if(_ingredientDao == null) {
          _ingredientDao = new IngredientDao_Impl(this);
        }
        return _ingredientDao;
      }
    }
  }

  @Override
  public DiaryDao diaryDao() {
    if (_diaryDao != null) {
      return _diaryDao;
    } else {
      synchronized(this) {
        if(_diaryDao == null) {
          _diaryDao = new DiaryDao_Impl(this);
        }
        return _diaryDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public GoalDao goalDao() {
    if (_goalDao != null) {
      return _goalDao;
    } else {
      synchronized(this) {
        if(_goalDao == null) {
          _goalDao = new GoalDao_Impl(this);
        }
        return _goalDao;
      }
    }
  }
}
