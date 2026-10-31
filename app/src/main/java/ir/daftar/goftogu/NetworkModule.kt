package ir.daftar.goftogu

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Log

/**
 * Network module for Retrofit + OkHttp configuration
 * مدول شبکه برای Retrofit و OkHttp
 * 
 * Base URL: https://recamender.darkube.ir/api/v1/
 */
object NetworkModule {
    
    private const val TAG = "NetworkModule"
    
    // Base URL pointing to Darkube backend
    // باید https باشد و با / ختم شود
    private const val BASE_URL = "https://recamender-backending.darkube.ir/api/v1/"
    
    // Lazy initialization of Retrofit instance
    private var retrofitInstance: Retrofit? = null
    
    /**
     * Get or create Retrofit instance
     */
    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: createRetrofit().also { retrofitInstance = it }
    }
    
    /**
     * Create Retrofit instance with OkHttp client
     */
    private fun createRetrofit(): Retrofit {
        val httpClient = createOkHttpClient()
        val gson = createGson()
        
        Log.d(TAG, "Retrofit created with BASE_URL: $BASE_URL")
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Create OkHttp client with logging interceptor
     * تمام درخواست‌ها و پاسخ‌ها در Logcat با تگ "OkHttp" نمایش داده می‌شوند
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            // نمایش تمام درخواست‌ها و پاسخ‌ها
            Log.d("OkHttp", message)
            // همچنین در logcat نمایش بدهد
            if (message.contains("POST") || message.contains("GET") || message.contains("Error")) {
                Log.i("API_DEBUG", message)
            }
        }.apply {
            // Level.BODY تمام جزئیات را نشان می‌دهد
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Create Gson instance for JSON serialization
     */
    private fun createGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    /**
     * Get API service instance
     */
    fun getApiService(): ApiService {
        return getRetrofit().create(ApiService::class.java)
    }
}
