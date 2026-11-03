package ir.daftar.goftogu

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("sync/ai-models")
    suspend fun syncAiModels(
        @Body payload: SyncAiModelsRequest
    ): Response<SyncResponse>

    @POST("sync/support-messages")
    suspend fun syncSupportMessages(
        @Body payload: SyncSupportMessagesRequest
    ): Response<SyncResponse>

    @POST("sync/banners")
    suspend fun syncBanners(
        @Body payload: SyncBannersRequest
    ): Response<SyncResponse>

    @POST("sync/announcements")
    suspend fun syncAnnouncements(
        @Body payload: SyncAnnouncementsRequest
    ): Response<SyncResponse>

    @POST("sync/export-logs")
    suspend fun syncExportLogs(
        @Body payload: SyncExportLogsRequest
    ): Response<SyncResponse>

    @POST("users/check")
    suspend fun checkUserExists(
        @Body request: UserCheckRequest
    ): Response<UserCheckResponse>

    @POST("teams")
    suspend fun createTeam(
        @Body request: CreateTeamRequest
    ): Response<TeamDto>

    @GET("teams/user/{user_id}")
    suspend fun getUserTeams(
        @Path("user_id") userId: String
    ): Response<List<TeamDto>>

    @POST("sync/tokens")
    suspend fun syncTokens(
        @Body payload: TokenUsageSyncPayload
    ): Response<SyncResponse>

    @GET("admin/users-stats")
    suspend fun getAdminStats(
        @Query("query") query: String = "",
        @Query("sort_by") sortBy: String = "highest_usage",
        @Query("plan") plan: String = "all"
    ): Response<AdminStatsResponse>

    @GET("users/{user_id}/profile")
    suspend fun getUserProfile(
        @Path("user_id") userId: String
    ): Response<UserProfileDto>

    @PUT("users/{user_id}/username")
    suspend fun updateUsername(
        @Path("user_id") userId: String,
        @Body req: UpdateUsernameReq
    ): Response<Map<String, String>>

    @GET("admin/ai/history")
    suspend fun getAdminAiHistory(
        @Query("admin_id") adminId: String
    ): Response<List<AdminChatMessageDto>>

    @POST("admin/ai/chat")
    suspend fun sendAdminAiMessage(
        @Body req: AdminAiChatRequest
    ): Response<AdminAiChatResponse>
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

// --- مدل‌های مدل‌های هوش مصنوعی ثبت‌شده ادمین ---
data class SyncAiModelItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("endpoint") val endpoint: String,
    @SerializedName("model_id") val modelId: String,
    @SerializedName("api_key") val apiKey: String,
    @SerializedName("input_cost") val inputCost: Float,
    @SerializedName("output_cost") val outputCost: Float,
    @SerializedName("active") val active: Boolean
)

data class SyncAiModelsRequest(
    @SerializedName("ai_models") val aiModels: List<SyncAiModelItem>
)

// --- مدل‌های پیام‌های پشتیبانی ---
data class SyncSupportMessageItem(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("from_admin") val fromAdmin: Boolean,
    @SerializedName("body") val body: String,
    @SerializedName("kind") val kind: String,
    @SerializedName("path") val path: String? = null,
    @SerializedName("read_admin") val readAdmin: Boolean = false,
    @SerializedName("read_user") val readUser: Boolean = false,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncSupportMessagesRequest(
    @SerializedName("support_messages") val supportMessages: List<SyncSupportMessageItem>
)

// --- مدل‌های بنرها ---
data class SyncBannerItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("image") val image: String? = null,
    @SerializedName("active") val active: Boolean,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncBannersRequest(
    @SerializedName("banners") val banners: List<SyncBannerItem>
)

// --- مدل‌های اطلاعیه‌ها ---
data class SyncAnnouncementItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncAnnouncementsRequest(
    @SerializedName("announcements") val announcements: List<SyncAnnouncementItem>
)

// --- مدل‌های لاگ‌های خروجی ---
data class SyncExportLogItem(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("file_type") val fileType: String,
    @SerializedName("target_name") val targetName: String,
    @SerializedName("records_count") val recordsCount: Int
)

data class SyncExportLogsRequest(
    @SerializedName("export_logs") val exportLogs: List<SyncExportLogItem>
)

// --- مدل‌های تیم‌ها و گفتگوهای گروهی ---
data class UserCheckRequest(
    @SerializedName("phone") val phone: String
)

data class UserCheckResponse(
    @SerializedName("exists") val exists: Boolean,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("name") val name: String? = null
)

data class TeamMemberDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("role") val role: String,
    @SerializedName("joined_at") val joinedAt: Long
)

data class TeamDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("creator_id") val creatorId: String,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("member_count") val memberCount: Int,
    @SerializedName("members") val members: List<TeamMemberDto>
)

data class CreateTeamRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("creator_id") val creatorId: String,
    @SerializedName("member_user_ids") val memberUserIds: List<String>
)


// --- مدل‌های توکن‌ها ---
data class TokenUsageSyncItem(
    @SerializedName("user_id") val userId: String,
    @SerializedName("tokens") val tokens: Int,
    @SerializedName("occurred_at") val occurredAt: Long
)

data class TokenUsageSyncPayload(
    @SerializedName("usages") val usages: List<TokenUsageSyncItem>
)

data class UserStatsResponse(
    @SerializedName("id") val id: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("plan") val plan: String,
    @SerializedName("tokens_used") val tokensUsed: Long,
    @SerializedName("created_at") val createdAt: Long
)

data class AdminStatsResponse(
    @SerializedName("total_tokens_used") val totalTokensUsed: Long,
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("users") val users: List<UserStatsResponse>
)


// --- مدل‌های پروفایل کاربر ---
data class UserProfileDto(
    @SerializedName("user_id") val userId: String,
    val phone: String,
    val name: String,
    val username: String? = null,
    val plan: String,
    val balance: Int,
    @SerializedName("plan_expires_at") val planExpiresAt: Long?,
    val suspended: Boolean
)


data class UpdateUsernameReq(
    val username: String
)


// --- مدل‌های چت هوشمند ادمین ---
data class AdminChatMessageDto(
    val id: String,
    val role: String,
    val content: String,
    @SerializedName("created_at") val createdAt: Long
)

data class AdminAiChatRequest(
    @SerializedName("admin_id") val adminId: String,
    val message: String
)

data class AdminAiChatResponse(
    val reply: String
)


// --- مدل‌های Teams ---
data class AddTeamMemberRequest(
    @SerializedName("user_id") val userId: String,
    val role: String = "member"
)

data class UpdateTeamRequest(
    val name: String? = null,
    val description: String? = null
)

data class SendTeamMessageRequest(
    @SerializedName("sender_id") val senderId: String,
    val content: String
)

data class TeamMessageDto(
    val id: String,
    @SerializedName("team_id") val teamId: String,
    @SerializedName("sender_id") val senderId: String,
    val content: String,
    @SerializedName("created_at") val createdAt: Long
)

// --- Endpoints Teams ---

