package ir.daftar.goftogu.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorTheme(
    val id: String,
    val name: String,
    val containerColor: Color,      // رنگ هدرها، کارت‌ها، کانتینرها و کپسول‌های پس‌زمینه
    val buttonColor: Color,         // رنگ دکمه‌های اصلی، میکروفون، کپسول‌های فعال و نشانگرها
    val onContainerColor: Color = Color(0xFF1E293B), // رنگ متن و آیکون روی کانتینر
    val onButtonColor: Color = Color.White           // رنگ متن و آیکون روی دکمه
)

val APP_THEMES = listOf(
    // 1. آبی تلگرام کلاسیک (Telegram Classic)
    AppColorTheme(
        id = "classic_blue",
        name = "آبی کلاسیک",
        containerColor = Color(0xFFE7F3FB),
        buttonColor = Color(0xFF24A1DE)
    ),
    // 2. زمردی نعنایی (Emerald Mint)
    AppColorTheme(
        id = "emerald",
        name = "زمردی نعنایی",
        containerColor = Color(0xFFE6F4EA),
        buttonColor = Color(0xFF0F9D58)
    ),
    // 3. یاسی ملایم (Lavender Whisper)
    AppColorTheme(
        id = "lavender",
        name = "یاسی ملایم",
        containerColor = Color(0xFFEDE7F6),
        buttonColor = Color(0xFF7E57C2)
    ),
    // 4. کهربایی و عسلی (Warm Amber)
    AppColorTheme(
        id = "amber",
        name = "کهربایی و عسلی",
        containerColor = Color(0xFFFEF3C7),
        buttonColor = Color(0xFFD97706)
    ),
    // 5. اقیانوسی فیروزه‌ای (Deep Cyan)
    AppColorTheme(
        id = "cyan",
        name = "اقیانوسی فیروزه‌ای",
        containerColor = Color(0xFFE0F7FA),
        buttonColor = Color(0xFF0097A7)
    ),
    // 6. مرجانی هلویی (Nordic Coral)
    AppColorTheme(
        id = "coral",
        name = "مرجانی هلویی",
        containerColor = Color(0xFFFFEBE6),
        buttonColor = Color(0xFFE05638)
    ),
    // 7. رز سلطنتی (Royal Rose)
    AppColorTheme(
        id = "rose",
        name = "رز ملایم",
        containerColor = Color(0xFFFCE4EC),
        buttonColor = Color(0xFFD81B60)
    ),
    // 8. سبز زیتونی (Olive Sage)
    AppColorTheme(
        id = "olive",
        name = "سبز زیتونی",
        containerColor = Color(0xFFF1F8E9),
        buttonColor = Color(0xFF689F38)
    ),
    // 9. موکا و نسکافه (Warm Mocha)
    AppColorTheme(
        id = "mocha",
        name = "موکا و نسکافه",
        containerColor = Color(0xFFEFEBE9),
        buttonColor = Color(0xFF795548)
    ),
    // 10. نقره‌ای نوک‌مدادی (Slate Silver)
    AppColorTheme(
        id = "slate",
        name = "نقره‌ای نوک‌مدادی",
        containerColor = Color(0xFFECEFF1),
        buttonColor = Color(0xFF455A64)
    )
)

val LocalAppTheme = staticCompositionLocalOf { APP_THEMES.first() }

fun appThemeById(id: String?): AppColorTheme = APP_THEMES.firstOrNull { it.id == id } ?: APP_THEMES.first()

/**
 * وضعیت واکنشی سراسری تم دوگانه. با تغییر تم، تمام درخت Compose دوباره رندر می‌شود
 * و انتخاب کاربر در SharedPreferences با کلید saved_theme_id ذخیره می‌گردد.
 */
object AppThemeState {
    var current by mutableStateOf(APP_THEMES.first())
        private set

    private var prefs: android.content.SharedPreferences? = null

    fun init(preferences: android.content.SharedPreferences) {
        prefs = preferences
        current = appThemeById(preferences.getString("saved_theme_id", null))
    }

    fun update(theme: AppColorTheme) {
        current = theme
        prefs?.edit()?.putString("saved_theme_id", theme.id)?.apply()
    }
}

/** به‌روزرسانی سراسری تم رنگی برنامه + ذخیره روی دیسک. */
fun updateAppTheme(theme: AppColorTheme) = AppThemeState.update(theme)
