package com.nutri.app.core.data.local

import androidx.room.*

/** Room-сущность блюда (кэш карточек). */
@Entity(tableName = "dishes")
data class DishEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val kcal: Int,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    /** Теги через запятую — для MVP достаточно, при росте данных вынесем в таблицу. */
    val tags: String,
    val cookMinutes: Int,
    val isFromRemote: Boolean,
    /** Время последнего обновления — для стратегии инвалидации кэша. */
    val updatedAt: Long
) {
    companion object {
        fun tagsToSet(raw: String) = raw.split(',').filter { it.isNotBlank() }.toSet()
        fun setToTags(tags: Set<String>) = tags.joinToString(",")
    }
}

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val proteinPer100: Float,
    val fatPer100: Float,
    val carbsPer100: Float,
    val unit: String
)

@Entity(
    tableName = "diary_entries",
    indices = [Index(value = ["userId", "date"])]  // быстрый выбор дня без full scan
)
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val date: String,
    val mealType: String,
    val dishId: String,
    val dishName: String,
    val grams: Float,
    val kcal: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val email: String?,
    val phone: String?,
    val displayName: String,
    val isPremium: Boolean
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val userId: Long,
    val dailyKcal: Int,
    val dailyProtein: Int,
    val dailyFat: Int,
    val dailyCarbs: Int
)

// ---------- DAO ----------

@Dao
interface DishDao {
    @Query("SELECT * FROM dishes ORDER BY kcal")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<DishEntity>>

    @Query("SELECT * FROM dishes WHERE id = :id")
    suspend fun getById(id: String): DishEntity?

    @Upsert
    suspend fun upsertAll(dishes: List<DishEntity>)

    @Query("DELETE FROM dishes WHERE isFromRemote = 1 AND updatedAt < :staleBefore")
    suspend fun deleteStaleRemote(staleBefore: Long)

    @Query("SELECT COUNT(*) FROM dishes")
    suspend fun count(): Int
}

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%' LIMIT 20")
    suspend fun search(query: String): List<IngredientEntity>

    @Upsert
    suspend fun upsertAll(items: List<IngredientEntity>)
}

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE userId = :userId AND date = :date ORDER BY mealType")
    fun observeDay(userId: Long, date: String): kotlinx.coroutines.flow.Flow<List<DiaryEntryEntity>>

    /** Сумма калорий по дням для графика прогресса. Считается SQL-агрегатом — быстро даже на больших объёмах. */
    @Query("SELECT date, SUM(kcal) as total FROM diary_entries WHERE userId = :userId AND date >= :fromDate GROUP BY date")
    fun observeDailyTotals(userId: Long, fromDate: String): kotlinx.coroutines.flow.Flow<List<DailyTotalRow>>

    @Insert
    suspend fun insert(entry: DiaryEntryEntity): Long

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun delete(id: Long)
}

data class DailyTotalRow(val date: String, val total: Float)

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): kotlinx.coroutines.flow.Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clear()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals LIMIT 1")
    fun observeGoal(): kotlinx.coroutines.flow.Flow<GoalEntity?>

    @Upsert
    suspend fun upsert(goal: GoalEntity)
}

/** Единая база. exportSchema = false для MVP, включим при миграциях. */
@Database(
    entities = [DishEntity::class, IngredientEntity::class, DiaryEntryEntity::class, UserEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NutriDatabase : RoomDatabase() {
    abstract fun dishDao(): DishDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun diaryDao(): DiaryDao
    abstract fun userDao(): UserDao
    abstract fun goalDao(): GoalDao

    companion object {
        fun build(context: android.content.Context): NutriDatabase =
            androidx.room.Room.databaseBuilder(context, NutriDatabase::class.java, "nutri.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL(
                            """INSERT OR IGNORE INTO dishes
                                (id, name, imageUrl, kcal, protein, fat, carbs, tags, cookMinutes, isFromRemote, updatedAt)
                                VALUES
                                ('local_oatmeal', 'Овсяная каша с ягодами', NULL, 186, 8, 6, 25, 'vegetarian', 10, 0, 0),
                                ('local_chicken_rice', 'Курица с рисом', NULL, 165, 18, 3, 17, 'gluten_free', 25, 0, 0),
                                ('local_greek_salad', 'Греческий салат', NULL, 140, 5, 11, 6, 'vegetarian,gluten_free', 15, 0, 0),
                                ('local_lentil_soup', 'Чечевичный суп', NULL, 116, 7, 2, 18, 'vegetarian,vegan,gluten_free', 30, 0, 0)"""
                        )
                        db.execSQL(
                            """INSERT OR IGNORE INTO ingredients
                                (id, name, proteinPer100, fatPer100, carbsPer100, unit)
                                VALUES
                                ('local_oats', 'Овсяные хлопья', 13.2, 6.5, 68.7, 'GRAM'),
                                ('local_chicken', 'Куриная грудка', 23.1, 1.9, 0, 'GRAM'),
                                ('local_rice', 'Рис варёный', 2.7, 0.3, 28, 'GRAM'),
                                ('local_tomato', 'Помидор', 0.9, 0.2, 3.9, 'GRAM'),
                                ('local_lentils', 'Чечевица варёная', 9, 0.4, 20, 'GRAM')"""
                        )
                    }
                })
                .fallbackToDestructiveMigration() // MVP; при релизе заменим на миграции
                .build()
    }
}
