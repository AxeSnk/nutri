# MVP: правила R8. Библиотеки из dependencies имеют consumer-rules; специфичного пока не требуется.
-keep class com.nutri.app.core.data.remote.** { *; }        # kotlinx-serialization DTO
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
