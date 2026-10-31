# Nutri — MVP конструктора питания (Kotlin + Jetpack Compose)

Приложение для составления рациона и отслеживания калорий и БЖУ. Оффлайн-first, Material 3, Clean Architecture.

## Структура

```
app/src/main/java/com/nutri/app/
├── core/
│   ├── domain/            # Чистый домен: без Android
│   │   ├── model/         # User, Goal, Dish, Ingredient, DiaryEntry, Recommendation, Macros
│   │   ├── usecase/       # GetRecommendations, ObserveDailyProgress, SaveGoal, LogDish
│   │   ├── repository/    # Интерфейсы репозиториев
│   │   ├── ai/            # Контракт AiRecommender (платный модуль)
│   │   └── util/          # MacroCalculator — чистые функции расчёта БЖУ/калорий
│   ├── data/
│   │   ├── local/         # Room: сущности, DAO, база
│   │   ├── remote/        # Retrofit (OpenFoodFacts) + OkHttp Cache
│   │   └── repository/    # Offline-first реализации
│   └── di/                # AppGraph — явный DI
├── feature/               # Экраны по фичам
│   ├── auth/              # Вход по email/телефону
│   ├── goals/             # Установка цели калорий и БЖУ
│   ├── builder/           # Конструктор: карточки блюд + сборка из ингредиентов
│   └── diary/             # Дневник, баланс, график по дням
└── ui/theme/              # Material 3 темы (светлая/тёмная)
```

## Поток данных

UI (Compose, StateFlow) → ViewModel → UseCase → Repository → DataSource (Room = истина для UI, Retrofit = обновление кэша). Ошибки и загрузка — поля `UiState`, отдельные от данных.

## Оффлайн

- OkHttp Cache 10 МБ: онлайн-ответы хранятся 60 секунд, оффлайн-запросы принудительно читают кэш с любым возрастом.
- Room: блюда/ингредиенты/дневник дублируются локально; UI всегда читает Room.
- В Room при первом запуске добавляется небольшой каталог блюд и ингредиентов, поэтому конструктор доступен без первого сетевого запроса.
- Дневник и цели работают полностью без сети.

## Запуск

1. Открыть проект в Android Studio. Gradle JVM фиксируется в `.idea/gradle.xml` (value="17"). Если Studio пишет "Incompatible Gradle JVM": File ▸ Settings ▸ Build Tools ▸ Gradle ▸ Gradle JVM → выбрать **jdk-17** (не встроенный JBR — в новых Studio он версии 25, которую Gradle 8.11.1 не поддерживает).
2. Сборка через wrapper:

**PowerShell** (в Windows — `gradlew.bat`, без `./`):
```powershell
.\gradlew.bat :app:assembleDebug
```

**CMD / Linux / macOS:**
```
gradlew.bat :app:assembleDebug      # Windows CMD
./gradlew :app:assembleDebug        # Linux/macOS
```

При первой сборке wrapper скачает Gradle 8.11.1 (кэшируется в `~/.gradle`).
- minSdk 26, targetSdk 34, compileSdk 35, Kotlin 2.0, Compose BOM 2024.09.
- Готовый APK: `app/build/outputs/apk/debug/app-debug.apk`

## Зависимости (build.gradle.kts)

Compose BOM + material3 (+window-size-class для планшетов), Navigation Compose, Room 2.6 + KSP, DataStore Preferences, Retrofit 2.11 + kotlinx-serialization converter, OkHttp 4.12, Coroutines.

## Платный AI-модуль (задел)

`core/domain/ai/AiRecommender` — интерфейс; сейчас зарегистрирована заглушка `NoAiRecommender`. Будущий модуль `:ai` реализует контракт (например, GPT-подбор рациона) и подменяет реализацию в `AppGraph` одной строкой. UI показывает upsell, когда `isAvailable == false`.

## Производительность

- `LazyColumn` с `key` — стабильные элементы, без лишних recomposition.
- Room Flow — UI обновляется точечно после INSERT.
- Агрегации графика считаются SQL-запросом (SUM/GROUP BY), а не в Kotlin.
- Тяжёлые операции — на `Dispatchers.IO` в репозиториях; домен синхронный и чистый.
