package ir.daftar.goftogu

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API Service Interface - Retrofit endpoints for Darkube backend
 */
interface ApiService {
    
    /**
     * Send OTP code via SMS to phone number
     * POST /api/v1/auth/send-otp
     */
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): OtpResponse
    
    /**
     * Verify OTP code
     * POST /api/v1/auth/verify-otp
     */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): VerifyResponse
    
    /**
     * Sign up new user
     * POST /api/v1/auth/signup
     */
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse
    
    /**
     * Login existing user
     * POST /api/v1/auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
