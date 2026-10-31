package ir.daftar.goftogu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavBackStackEntry
import ir.daftar.goftogu.ui.theme.AppThemeState
import ir.daftar.goftogu.ui.theme.LocalAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Vazir = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

private fun TextStyle.withVazir() = copy(fontFamily = Vazir)

private fun vazirTypography(): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.withVazir(),
        displayMedium = base.displayMedium.withVazir(),
        displaySmall = base.displaySmall.withVazir(),
        headlineLarge = base.headlineLarge.withVazir(),
        headlineMedium = base.headlineMedium.withVazir(),
        headlineSmall = base.headlineSmall.withVazir(),
        titleLarge = base.titleLarge.withVazir(),
        titleMedium = base.titleMedium.withVazir(),
        titleSmall = base.titleSmall.withVazir(),
        bodyLarge = base.bodyLarge.withVazir(),
        bodyMedium = base.bodyMedium.withVazir(),
        bodySmall = base.bodySmall.withVazir(),
        labelLarge = base.labelLarge.withVazir(),
        labelMedium = base.labelMedium.withVazir(),
        labelSmall = base.labelSmall.withVazir(),
    )
}

val LocalDark = compositionLocalOf { false }
val LocalChatFont = compositionLocalOf { 16 }

class ChatFontController(initial: Int, private val prefs: android.content.SharedPreferences) {
    var size by mutableStateOf(initial.coerceIn(12, 22))
        private set

    fun set(next: Int) {
        size = next.coerceIn(12, 22)
        prefs.edit().putInt("chat_font_size", size).apply()
    }
}

data class TgPalette(
    val dark: Boolean,
    val bg: Color,
    val surface: Color,
    val incoming: Color,
    val outgoing: Color,
    val text: Color,
    val subtext: Color,
    val border: Color,
    val search: Color,
    val note: Color,
    val blue: Color = Color(0xFF043693),
)

fun tgLight() = TgPalette(
    dark = false,
    bg = Color(0xFFE6E6FA),
    surface = Color.White,
    incoming = Color.White,
    outgoing = Color(0xFFEFFDDE),
    text = Color(0xFF222222),
    subtext = Color(0xFF8E8E93),
    border = Color(0xFFE2E8F0),
    search = Color(0xFFF1F2F6),
    note = Color(0xFFFFF4C2),
)

fun tgDark() = TgPalette(
    dark = true,
    bg = Color(0xFF0E1621),
    surface = Color(0xFF17212B),
    incoming = Color(0xFF182533),
    outgoing = Color(0xFF2B5278),
    text = Color.White,
    subtext = Color(0xFF7F91A4),
    border = Color(0xFF243242),
    search = Color(0xFF242F3D),
    note = Color(0xFF5C4A1F),
)

val LocalTg = compositionLocalOf { tgLight() }

data class ChatThemePalette(
    val id: String,
    val name: String,
    val primary: Color,
    val outgoingBubble: Color,
    val accent: Color,
)

val chatPalettes: List<ChatThemePalette> = listOf(
    ChatThemePalette("classic", "آبی کلاسیک", Color(0xFF24A1DE), Color(0xFFEFF7FD), Color(0xFF0088CC)),
    ChatThemePalette("emerald", "زمردی نئون", Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF059669)),
    ChatThemePalette("ocean", "اقیانوسی ژرف", Color(0xFF0EA5E9), Color(0xFFF0F9FF), Color(0xFF0284C7)),
    ChatThemePalette("sunset", "غروب مخملی", Color(0xFFF97316), Color(0xFFFFF7ED), Color(0xFFEA580C)),
    ChatThemePalette("royalty", "یاقوت ارغوانی", Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF7C3AED)),
    ChatThemePalette("rose", "رز مدرن", Color(0xFFF43F5E), Color(0xFFFFF1F2), Color(0xFFE11D48)),
    ChatThemePalette("galaxy", "کهکشانی شب", Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF4F46E5)),
    ChatThemePalette("olive", "سبز زیتونی مینیمال", Color(0xFF84CC16), Color(0xFFF7FEE7), Color(0xFF65A30D)),
    ChatThemePalette("amber", "طلایی کهربایی", Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFFD97706)),
    ChatThemePalette("slate", "نوک‌مدادی لوکس", Color(0xFF64748B), Color(0xFFF8FAFC), Color(0xFF475569)),
)

fun chatPaletteById(id: String?): ChatThemePalette = chatPalettes.firstOrNull { it.id == id } ?: chatPalettes.first()

val LocalChatPalette = compositionLocalOf { chatPalettes.first() }

class ChatPaletteController(initial: String, private val prefs: android.content.SharedPreferences) {
    var id by mutableStateOf(initial)
        private set

    fun set(next: String) {
        id = next
        prefs.edit().putString("chat_palette", next).apply()
    }
}

class ThemeController(initial: String, private val prefs: android.content.SharedPreferences) {
    var mode by mutableStateOf(initial)
        private set

    fun set(next: String) {
        mode = next
        prefs.edit().putString("theme", next).apply()
    }
}

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        val locale = java.util.Locale("fa", "IR")
        java.util.Locale.setDefault(locale)
        val config = android.content.res.Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App

        // راه‌اندازی همگام‌سازی پس‌زمینه با سرور هنگام اتصال به اینترنت
        val syncConstraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        val syncRequest = androidx.work.PeriodicWorkRequestBuilder<SyncWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(syncConstraints)
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataSyncWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        setContent {
            val context = LocalContext.current
            val theme = remember {
                val prefs = context.getSharedPreferences("tg", 0)
                val stored = prefs.getString("theme", null) ?: if (prefs.getBoolean("dark", false)) "dark" else "system"
                ThemeController(stored, prefs)
            }
            val chatFont = remember {
                val prefs = context.getSharedPreferences("tg", 0)
                ChatFontController(prefs.getInt("chat_font_size", 16), prefs)
            }
            val chatPalette = remember {
                val prefs = context.getSharedPreferences("tg", 0)
                ChatPaletteController(prefs.getString("chat_palette", null) ?: "classic", prefs)
            }
            remember {
                AppThemeState.init(context.getSharedPreferences("tg", 0))
                true
            }
            val activeAppTheme = AppThemeState.current
            val dark = when (theme.mode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            val palette = (if (dark) tgDark() else tgLight()).copy(blue = activeAppTheme.buttonColor)
            val background by animateColorAsState(palette.bg, tween(200), label = "bg")
            val scheme = if (dark) {
                darkColorScheme(
                    primary = activeAppTheme.buttonColor,
                    onPrimary = activeAppTheme.onButtonColor,
                    background = palette.bg,
                    surface = palette.surface,
                    onBackground = palette.text,
                    onSurface = palette.text
                )
            } else {
                lightColorScheme(
                    primary = activeAppTheme.buttonColor,
                    onPrimary = activeAppTheme.onButtonColor,
                    background = palette.bg,
                    surface = palette.surface,
                    onBackground = palette.text,
                    onSurface = palette.text
                )
            }
            CompositionLocalProvider(
                LocalAppTheme provides activeAppTheme,
                LocalLayoutDirection provides LayoutDirection.Rtl,
                LocalDark provides dark,
                LocalTg provides palette,
                LocalChatFont provides chatFont.size,
                LocalChatPalette provides chatPaletteById(chatPalette.id)
            ) {
                MaterialTheme(colorScheme = scheme, typography = vazirTypography()) {
                    Surface(Modifier.fillMaxSize(), color = background) {
                        Root(app, theme, chatFont, chatPalette)
                    }
                }
            }
        }
    }
}
@Composable
fun Root(app: App, theme: ThemeController, chatFont: ChatFontController, chatPalette: ChatPaletteController) {
    val nav = rememberNavController()
    var userId by remember { mutableStateOf(app.sessionId()) }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: ""
    val showBar = userId != null && route in setOf("chats", "teams", "contacts", "profile", "support")

    var currentUser by remember { mutableStateOf<User?>(null) }

LaunchedEffect(userId) {
    val uid = userId
    currentUser = if (uid != null) {
        withContext(Dispatchers.IO) {
            app.db.user(uid)
        }
    } else {
        null
    }
}

    val context = LocalContext.current
    val currentRoute by rememberUpdatedState(route)
    var seenSupportReply by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val uid = userId ?: return@LaunchedEffect
        seenSupportReply = app.db.supportMessages(uid).lastOrNull { it.fromAdmin }?.id
    }

    var draftPhone by remember { mutableStateOf("") }
    var openComposer by remember { mutableStateOf(false) }
    val palette = LocalTg.current
    val shift = with(LocalDensity.current) { 24.dp.roundToPx() }

    fun NavGraphBuilder.screen(
        route: String,
        arguments: List<androidx.navigation.NamedNavArgument> = emptyList(),
        content: @Composable (NavBackStackEntry) -> Unit,
    ) {
        val motion = tween<androidx.compose.ui.unit.IntOffset>(220, easing = FastOutSlowInEasing)
        val fade = tween<Float>(220, easing = FastOutSlowInEasing)
        composable(
            route,
            arguments,
            enterTransition = { fadeIn(fade) + slideInHorizontally(motion) { shift } + scaleIn(fade, initialScale = 0.98f) },
            exitTransition = { fadeOut(fade) + slideOutHorizontally(motion) { -shift } },
            popEnterTransition = { fadeIn(fade) + slideInHorizontally(motion) { -shift } + scaleIn(fade, initialScale = 0.98f) },
            popExitTransition = { fadeOut(fade) + slideOutHorizontally(motion) { shift } },
        ) { entry -> content(entry) }
    }

    Scaffold(
        containerColor = palette.bg,
        bottomBar = {
    if (showBar) {
        val u = currentUser
        BottomBar(
            route = route,
            avatar = u?.avatar,
            name = u?.name ?: "",
            hasStar = (u?.plan == "advanced" || u?.plan == "basic")
        ) { targetRoute ->
            nav.navigate(targetRoute) {
                popUpTo("chats") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
},
    ) { padding ->
        Box(Modifier.padding(padding)) {
            NavHost(nav, startDestination = if (userId == null) "login" else "chats") {
                screen("pricing") {
                    PricingScreen(
                        onPay = { plan ->
                            if (userId == null) nav.navigate("signup")
                            else nav.navigate("pay")
                        },
                        onBack = { nav.popBackStack() }
                    )
                }
                screen("login") {
                    AuthScreen(
                        app,
                        { id ->
                            userId = id
                            nav.navigate("chats") { popUpTo(0) }
                        },
                        { nav.navigate("signup") },
                        { nav.navigate("forgot") },
                    )
                }
                screen("signup") {
                    SignUpScreen(
                        app,
                        { id ->
                            userId = id
                            nav.navigate("chats") { popUpTo(0) }
                        },
                        { nav.navigate("login") { popUpTo("signup") { inclusive = true } } },
                    )
                }
                screen("forgot") {
                    ForgotScreen(
                        app,
                        { nav.navigate("login") { popUpTo("forgot") { inclusive = true } } },
                        { nav.navigate("signup") { popUpTo("forgot") { inclusive = true } } },
                    )
                }
                screen(
                    "new-person?phone={phone}",
                    listOf(navArgument("phone") { type = NavType.StringType; defaultValue = "" }),
                ) { entry ->
                    NewPersonScreen(app, userId.orEmpty(), entry.arguments?.getString("phone").orEmpty()) { nav.popBackStack() }
                }
                screen("chats") {
                    ChatsScreen(
                        app,
                        userId.orEmpty(),
                        false,
                        { nav.navigate("chat/$it") },
                        { nav.navigate("admin") },
                        { nav.navigate("notices") },
                        onAi = { nav.navigate("ai") },
                        onPay = { nav.navigate("pay") },
                        onToggleTheme = { theme.set(if (palette.dark) "light" else "dark") },
                        darkTheme = palette.dark,
                        draftPhone = draftPhone,
                        openComposer = openComposer,
                        onComposerHandled = { openComposer = false; draftPhone = "" },
                    )
                }
                screen("teams") {
                    TeamsScreen(
                        app,
                        userId.orEmpty(),
                        onOpenTeam = { nav.navigate("chat/$it") },
                        onUpgrade = { nav.navigate("pricing") },
                        onBack = { nav.popBackStack() }
                    )
                }
                screen("contacts") {
                    PhoneContactsScreen(
                        { nav.navigate("chats") { launchSingleTop = true } },
                        { phone ->
                            draftPhone = phone
                            openComposer = true
                            nav.navigate("chats") { launchSingleTop = true }
                        },
                    )
                }
                screen("ai") {
                    AiChatScreen(app, userId.orEmpty()) { nav.popBackStack() }
                }
                screen("chat/{id}", listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    ThreadScreen(app, userId.orEmpty(), entry.arguments?.getString("id").orEmpty(), { nav.popBackStack() }, { nav.navigate("pricing") })
                }
                screen("notices") { NoticesScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                screen("settings") {
                    ProfileScreen(app, userId.orEmpty(), theme, chatFont, chatPalette, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("payment-history") }, { nav.navigate("export-history") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    })
                }
                screen("profile") {
                    ProfileScreen(app, userId.orEmpty(), theme, chatFont, chatPalette, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("payment-history") }, { nav.navigate("export-history") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    })
                }
                screen("payment-history") { PaymentHistoryScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                screen("export-history") { ExportHistoryScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                screen("support") { SupportScreen(onStartChat = { nav.navigate("support-chat") }) }
                screen("support-chat") { SupportChatRoom(app, userId.orEmpty(), false) { nav.popBackStack() } }
                screen("pay") { PayScreen(app, userId.orEmpty(), { nav.popBackStack() }, null) }
                screen("admin") {
                    AdminScreen(app, { nav.navigate("chats") { launchSingleTop = true } }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    }, onSupport = { nav.navigate("admin-support/$it") })
                }
                screen("admin-support/{id}", listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    SupportChatRoom(app, entry.arguments?.getString("id").orEmpty(), true) { nav.popBackStack() }
                }
            }
        }
    }
}
