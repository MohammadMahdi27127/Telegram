package ir.daftar.goftogu

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): OtpResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): VerifyResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("sync/entries")
    suspend fun syncEntries(
        @Body payload: SyncPayloadRequest
    ): Response<SyncResponse>

    @POST("sync/people")
    suspend fun syncPeople(
        @Body payload: SyncPeopleRequest
    ): Response<SyncResponse>

    @POST("sync/slips")
    suspend fun syncSlips(
        @Body payload: SyncSlipRequest
    ): Response<SyncResponse>

    @POST("sync/ai-messages")
    suspend fun syncAiMessages(
        @Body payload: SyncAiMessagesRequest
    ): Response<SyncResponse>
}

// --- مدل‌های احراز هویت ---
data class OtpRequest(
    @SerializedName("phone") val phone: String
)

data class OtpVerifyRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("code") val code: String
)

data class LoginRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("code") val code: String
)

data class OtpResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("expires_in") val expiresIn: Int? = null
)

data class VerifyResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user_id") val userId: String? = null
)

data class LoginResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("plan") val plan: String? = null
)

data class SignupResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("name") val name: String? = null
)

data class ErrorResponse(
    @SerializedName("detail") val detail: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)

// --- مدل‌های همگام‌سازی عمومی و پیام‌ها ---
data class SyncResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("saved_count") val savedCount: Int? = null
)

data class SyncEntryItem(
    @SerializedName("id") val id: String,
    @SerializedName("person_id") val personId: String,
    @SerializedName("body") val body: String,
    @SerializedName("kind") val kind: String,
    @SerializedName("channel") val channel: String,
    @SerializedName("occurred_at") val occurredAt: Long,
    @SerializedName("voice_path") val voicePath: String? = null,
    @SerializedName("media_path") val mediaPath: String? = null,
    @SerializedName("target_recipient_id") val targetRecipientId: String? = null
)

data class SyncPayloadRequest(
    @SerializedName("entries") val entries: List<SyncEntryItem>
)

// --- مدل‌های مخاطبین و گروه‌ها ---
data class SyncPersonItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("pinned") val pinned: Boolean = false,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("is_group") val isGroup: Boolean = false,
    @SerializedName("saved") val saved: Boolean = false,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncPeopleRequest(
    @SerializedName("people") val people: List<SyncPersonItem>
)

// --- مدل‌های فیش‌های پرداخت ---
data class SyncSlipItem(
    @SerializedName("id") val id: String,
    @SerializedName("kind") val kind: String,
    @SerializedName("plan") val plan: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("receipt") val receipt: String? = null,
    @SerializedName("ref_code") val refCode: String? = null,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncSlipRequest(
    @SerializedName("slips") val slips: List<SyncSlipItem>
)

// --- مدل‌های چت هوش مصنوعی ---
data class SyncAiMessageItem(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("person_id") val personId: String? = null,
    @SerializedName("role") val role: String,
    @SerializedName("body") val body: String,
    @SerializedName("image_path") val imagePath: String? = null,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncAiMessagesRequest(
    @SerializedName("messages") val messages: List<SyncAiMessageItem>
)