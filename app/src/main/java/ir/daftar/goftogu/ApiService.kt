package ir.daftar.goftogu

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API Service Interface - Retrofit endpoints for Darkube backend
 * Base URL: https://recamender.darkube.ir/api/v1/
 */
interface ApiService {
    
    /**
     * ارسال کد OTP به شماره تلفن
     * POST /auth/send-otp
     * 
     * Request: { "phone": "09123456789" }
     * Response: { "message": "کد تایید ارسال شد", "expires_in": 300 }
     */
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): OtpResponse
    
    /**
     * تایید کد OTP
     * POST /auth/verify-otp
     * 
     * Request: { "phone": "09123456789", "code": "123456" }
     * Response: { "message": "احراز هویت با موفقیت انجام شد", "status": "success" }
     */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): VerifyResponse
    
    /**
     * ورود کاربر
     * POST /auth/login
     * 
     * Request: { "phone": "09123456789", "password": "رمز" }
     * Response: { "message": "ورود موفق", "user_id": "uuid", "phone": "09123456789", "name": "نام", "plan": "free", "status": "success" }
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    /**
     * ثبت نام کاربر جدید
     * POST /auth/signup
     * 
     * Request: { "phone": "09123456789", "name": "محمد", "password": "رمز" }
     * Response: { "message": "ثبت نام موفق", "user_id": "uuid", "phone": "09123456789", "name": "محمد", "status": "success" }
     */
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse
}

// Request Models
data class OtpRequest(val phone: String)

data class OtpVerifyRequest(val phone: String, val code: String)

data class LoginRequest(val phone: String, val password: String)

data class SignupRequest(val phone: String, val name: String, val password: String)

// Response Models
data class OtpResponse(
    val message: String,
    val expires_in: Int? = null
)

data class VerifyResponse(
    val message: String,
    val status: String,
    val user_id: String? = null
)

data class LoginResponse(
    val message: String,
    val status: String,
    val user_id: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val plan: String? = null
)

data class SignupResponse(
    val message: String,
    val status: String,
    val user_id: String? = null,
    val phone: String? = null,
    val name: String? = null
)

data class ErrorResponse(
    val detail: String? = null,
    val message: String? = null,
    val error: String? = null
)
