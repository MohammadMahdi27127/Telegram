package ir.daftar.goftogu

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module for Retrofit + OkHttp configuration
 * Connects to Darkube FastAPI backend
 */
object NetworkModule {
    
    // Base URL pointing to Darkube backend
    private const val BASE_URL = "https://recamender.darkube.ir/api/v1/"
    
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
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Create OkHttp client with logging interceptor
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            android.util.Log.d("OkHttp", message)
        }.apply {
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

/**
 * API models for request/response
 */
data class OtpRequest(val phone: String)
data class OtpVerifyRequest(val phone: String, val code: String)
data class SignupRequest(val phone: String, val name: String, val password: String)
data class LoginRequest(val phone: String, val password: String)

data class OtpResponse(
    val message: String,
    val expires_in: Int? = null
)

data class VerifyResponse(
    val message: String,
    val status: String,
    val user_id: String? = null
)

data class AuthResponse(
    val message: String,
    val user_id: String? = null,
    val status: String
)

data class ErrorResponse(
    val detail: String? = null,
    val message: String? = null,
    val error: String? = null
)
