package ir.daftar.goftogu

/**
 * ثابت‌های سراسری برنامه
 */
object AppConstants {
    // شماره ادمین سیستم
    const val ADMIN_PHONE = "09052893115"
    
    // پلن‌های مجاز برای Teams
    val TEAM_ALLOWED_PLANS = listOf("plus", "smart", "advanced")
    
    // پلن‌های مجاز برای AI Chat نامحدود
    val AI_UNLIMITED_PLANS = listOf("plus", "smart", "advanced")
    
    // ID خاص برای دستیار عمومی AI
    const val AI_GENERAL_ASSISTANT_ID = "ai-general"
    const val AI_GENERAL_ASSISTANT_NAME = "دستیار هوش مصنوعی"
}
