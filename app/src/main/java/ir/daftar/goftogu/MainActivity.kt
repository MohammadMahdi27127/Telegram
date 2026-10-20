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

class ThemeController(initial: Boolean, private val prefs: android.content.SharedPreferences) {
    var dark by mutableStateOf(initial)
        private set
    fun toggle() {
        dark = !dark
        prefs.edit().putBoolean("dark", dark).apply()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App
        setContent {
            val context = LocalContext.current
            val theme = remember {
                val prefs = context.getSharedPreferences("tg", 0)
                ThemeController(prefs.getBoolean("dark", false), prefs)
            }
            val scheme = if (theme.dark) {
                darkColorScheme(primary = Color(0xFF24A1DE), background = Color(0xFF0F172A), surface = Color(0xFF1E293B))
            } else {
                lightColorScheme(primary = Color(0xFF24A1DE), background = Color.White)
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalDark provides theme.dark) {
                MaterialTheme(colorScheme = scheme, typography = vazirTypography()) {
                    Surface(Modifier.fillMaxSize(), color = scheme.background) {
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
    val showBar = userId != null && route in setOf("chats", "contacts", "settings", "profile", "notices")
    val user = userId?.let { app.db.user(it) }
    val chatBadge = userId?.let { id -> app.db.people(id).count { it.hasNote } } ?: 0
    Scaffold(
        containerColor = if (theme.dark) Color(0xFF0F172A) else Color.White,
        bottomBar = {
            if (showBar && user != null) {
                BottomBar(route, user.name, user.avatar, isAdmin(user.phone)) { nav.navigate(it) { launchSingleTop = true } }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            NavHost(nav, startDestination = if (userId == null) "home" else "chats") {
                composable("home") {
                    LandingScreen(
                        onLogin = { nav.navigate("login") },
                        onSignup = { nav.navigate("signup") },
                        onPricing = { nav.navigate("pricing") },
                    )
                }
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
                            nav.navigate("chats") { popUpTo("home") { inclusive = true } }
                        },
                        { nav.navigate("signup") { popUpTo("login") { inclusive = true } } },
                        { nav.navigate("forgot") { popUpTo("login") { inclusive = true } } },
                    )
                }
                composable(
                    "signup",
                    enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 6 } },
                    exitTransition = { fadeOut(tween(160)) },
                ) {
                    SignUpScreen(
                        app,
                        { nav.navigate("login") { popUpTo("signup") { inclusive = true } } },
                        { nav.navigate("forgot") { popUpTo("signup") { inclusive = true } } },
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
                    ChatsScreen(app, userId.orEmpty(), false, { nav.navigate("chat/$it") }, { nav.navigate("admin") }, { nav.navigate("notices") }, onCreate = { nav.navigate("new-person?phone=") })
                }
                composable("contacts") {
                    PhoneContactsScreen(
                        { nav.navigate("chats") { launchSingleTop = true } },
                        { phone -> nav.navigate("new-person?phone=${android.net.Uri.encode(phone)}") },
                    )
                }
                composable("chat/{id}", listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                    ThreadScreen(app, userId.orEmpty(), entry.arguments?.getString("id").orEmpty()) { nav.popBackStack() }
                }
                composable("notices") { NoticesScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                composable("settings") {
                    ProfileScreen(app, userId.orEmpty(), theme, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("pay") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("home") { popUpTo(0) }
                    })
                }
                composable("profile") {
                    ProfileScreen(app, userId.orEmpty(), theme, { nav.navigate("chats") { launchSingleTop = true } }, { nav.navigate("pay") }, { nav.navigate("pricing") }, { nav.navigate("admin") }, {
                        app.setSession(null)
                        userId = null
                        nav.navigate("home") { popUpTo(0) }
                    })
                }
                composable("pay") { PayScreen(app, userId.orEmpty()) { nav.popBackStack() } }
                composable("admin") {
                    AdminScreen(app, { nav.navigate("chats") { launchSingleTop = true } }) {
                        app.setSession(null)
                        userId = null
                        nav.navigate("home") { popUpTo(0) }
                    }
                }
            }
        }
    }
}
