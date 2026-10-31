package com.nutri.app.core.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.time.Duration

// ---------- OpenFoodFacts DTO (только нужные поля, лишнее игнорируется) ----------

@Serializable
data class OffSearchResponse(val products: List<OffProduct> = emptyList())

@Serializable
data class OffProduct(
    @SerialName("_id") val id: String? = null,
    @JsonNames("product_name", "product_name_ru") val name: String? = null,
    val nutriments: OffNutriments? = null,
    @SerialName("image_small_url") val imageUrl: String? = null
)

@Serializable
data class OffNutriments(
    @SerialName("proteins_100g") val protein: Float? = null,
    @SerialName("fat_100g") val fat: Float? = null,
    @SerialName("carbohydrates_100g") val carbs: Float? = null,
    @SerialName("energy-kcal_100g") val kcal: Float? = null
)

/** Retrofit-интерфейс публичного API OpenFoodFacts. */
interface OpenFoodFactsApi {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): OffSearchResponse
}

/**
 * Фабрика сети: OkHttp с дисковым кэшем 10 МБ.
 * Стратегия: в оффлайне любые GET читаются из кэша (FORCE_CACHE) — приложение работает без сети.
 */
object ApiFactory {

    fun create(cacheDir: File, isOnline: () -> Boolean = { true }): OpenFoodFactsApi {
        val offlineCacheInterceptor = okhttp3.Interceptor { chain ->
            val request = if (isOnline()) {
                chain.request()
            } else {
                chain.request().newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            }
            chain.proceed(request)
        }
        val cacheResponseInterceptor = okhttp3.Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .removeHeader("Pragma")
                .build()
        }

        val client = OkHttpClient.Builder()
            .cache(Cache(cacheDir, 10L * 1024 * 1024))
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(20))
            .addInterceptor(offlineCacheInterceptor)
            .addNetworkInterceptor(cacheResponseInterceptor)
            .build()

        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaTypeOrNull()!!))
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}
