package ir.daftar.goftogu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
    val blue: Color = Color(0xFF24A1DE),
)

fun tgLight() = TgPalette(
    dark = false,
    bg = Color(0xFFEFEFF4),
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
        setContent {
            val context = LocalContext.current
            val theme = remember {
                val prefs = context.getSharedPreferences("tg", 0)
                val stored = prefs.getString("theme", null) ?: if (prefs.getBoolean("dark", false)) "dark" else "system"
                ThemeController(stored, prefs)
            }
            val dark = when (theme.mode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            val palette = if (dark) tgDark() else tgLight()
            val background by animateColorAsState(palette.bg, tween(200), label = "bg")
            val scheme = if (dark) {
                darkColorScheme(primary = palette.blue, background = palette.bg, surface = palette.surface, onBackground = palette.text, onSurface = palette.text)
            } else {
                lightColorScheme(primary = palette.blue, background = palette.bg, surface = palette.surface, onBackground = palette.text, onSurface = palette.text)
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalDark provides dark, LocalTg provides palette) {
                MaterialTheme(colorScheme = scheme, typography = vazirTypography()) {
                    Surface(Modifier.fillMaxSize(), color = background) {
                        Root(app, theme)
                    }
                }
            }
        }
    }
}

@Composable
fun Root(app: App, theme: ThemeController) {
    val nav = rememberNavController()
    var userId by remember { mutableStateOf(app.sessionId()) }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: ""
    val showBar = userId != null && route in setOf("chats", "contacts", "profile")
    val user = userId?.let { app.db.user(it) }
    var draftPhone by remember { mutableStateOf("") }
    var openComposer by remember { mutableStateOf(false) }
    val palette = LocalTg.current
    val chatBadge = userId?.let { id -> app.db.people(id).sumOf { it.unread } } ?: 0
    Scaffold(
        containerColor = palette.bg,
        bottomBar = {
            if (showBar && user != null) {
                BottomBar(route, chatBadge) { nav.navigate(it) { launchSingleTop = true } }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            NavHost(nav, startDestination = if (userId == null) "login" else "chats") {
                composable("pricing") {
                    PricingScreen(onStart = { nav.navigate("signup") }, onBack = { nav.popBackStack() })
                }
                composable(
                    "login",
                    enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 6 } },
                    exitTransition = { fadeOut(tween(160)) },
                ) {
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
                composable(
                    "signup",
                    enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 6 } },
                    exitTransition = { fadeOut(tween(160)) },
                ) {
                    SignUpScreen(
                        app,
                        { id ->
                            userId = id
                            nav.navigate("chats") { popUpTo(0) }
                        },
                        { nav.navigate("login") { popUpTo("signup") { inclusive = true } } },
                    )
                }
                composable(
                    "forgot",
                    enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 6 } },
                    exitTransition = { fadeOut(tween(160)) },
                ) {
                    ForgotScreen(
                        app,
                        { nav.navigate("login") { popUpTo("forgot") { inclusive = true } } },
                        { nav.navigate("signup") { popUpTo("forgot") { inclusive = true } } },
                    )
                }
                composable(
                    "new-person?phone={phone}",
                    listOf(navArgument("phone") { type = NavType.StringType; defaultValue = "" }),
                ) { entry ->
                    NewPersonScreen(app, userId.orEmpty(), entry.arguments?.getString("phone").orEmpty()) { nav.popBackStack() }
                }
                composable("chats") {
                    ChatsScreen(
                        app,
                        userId.orEmpty(),
                        false,
                        { nav.navigate("chat/$it") },
                        { nav.navigate("admin") },
                        { nav.navigate("notices") },
                        onAi = { nav.navigate("ai") },
                        onPay = { nav.navigate("pay") },
                        draftPhone = draftPhone,
                        openComposer = openComposer,
                        onComposerHandled = { openComposer = false; draftPhone = "" },
                    )
                }
                composable("contacts") {
                    PhoneContactsScreen(
                        { nav.navigate("chats") { launchSingleTop = true } },
                        { phone ->
                            draftPhone = phone
                            openComposer = true
                            nav.navigate("chats") { launchSingleTop = true }
                        },
                    )
                }
                composable("ai") {
                    AiChatScreen(app, userId.orEmpty()) { nav.popBackStack() }
                }
                composable("chat/{id}", listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    ThreadScreen(app, userId.orEmpty(), entry.arguments?.getString("id").orEmpty()) { nav.popBackStack() }
                }
                composable("notices") { NoticesScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                composable("settings") {
                    ProfileScreen(app, userId.orEmpty(), theme, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("pay") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    })
                }
                composable("profile") {
                    ProfileScreen(app, userId.orEmpty(), theme, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("pay") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    })
                }
                composable("pay") { PayScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                composable("admin") {
                    AdminScreen(app, { nav.navigate("chats") { launchSingleTop = true } }) {
                        app.setSession(null)
                        userId = null
                        nav.navigate("login") { popUpTo(0) }
                    }
                }
            }
        }
    }
}
