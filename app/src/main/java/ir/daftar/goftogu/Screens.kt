package ir.daftar.goftogu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import java.net.HttpURLConnection
import java.net.URL
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import java.io.File
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import ir.daftar.goftogu.ui.theme.APP_THEMES
import ir.daftar.goftogu.ui.theme.AppColorTheme
import ir.daftar.goftogu.ui.theme.LocalAppTheme
import ir.daftar.goftogu.ui.theme.updateAppTheme

/**
 * رنگ اکشن سراسری برنامه؛ به‌صورت واکنشی از تم دوگانه انتخاب‌شده کاربر خوانده می‌شود
 * تا با تغییر پالت، همه دکمه‌ها و آیکون‌ها بدون ری‌استارت دوباره رندر شوند.
 */
private val Blue: Color
    @Composable get() = LocalAppTheme.current.buttonColor

/** رنگ کانتینر/کپسول پسی‌زمینه‌ای واکنشی از تم دوگانه انتخاب‌شده. */
private val Pill: Color
    @Composable get() = LocalAppTheme.current.containerColor

private val CheckGreen = Color(0xFF4FAE4E)
private val OutBubble = Color(0xFFEEFFDE)

/** رنگ کانتینر سازگار با حالت شبانه: در تم تیره رنگ تیره و در تم روشن رنگ پالت انتخاب‌شده. */
@Composable
private fun themeContainer(): Color =
    if (LocalDark.current) Color(0xFF203447) else LocalAppTheme.current.containerColor

/** رنگ کانتینر دیالوگ‌ها و باتم‌شیت‌ها بر اساس تم دوگانه انتخاب‌شده. */
@Composable
private fun themeDialogContainer(): Color =
    if (LocalDark.current) Color(0xFF1E293B) else LocalAppTheme.current.containerColor

@Composable
fun LandingScreen(onLogin: () -> Unit, onSignup: () -> Unit, onPricing: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(Color(0xFFE6E6FA)).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.brand), null, Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)))
            Text("مکالمات من", color = Color(0xFF222222), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 10.dp))
        }
        Text("دفتر مکالمه‌های تو", color = Blue, fontSize = 14.sp, modifier = Modifier.padding(top = 28.dp))
        Text("هر حرفی که با کسی زدی، همان‌جا بماند.", fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Text("برای آدم‌هایی که هر روز با آدم‌های زیادی حرف می‌زنند. مکالمه و یادداشت را جدا نگه دار.", color = Color(0xFF707579), modifier = Modifier.padding(top = 12.dp))
        Row(Modifier.padding(top = 18.dp)) {
            Button(onClick = onSignup) { Text("ساخت حساب") }
            TextButton(onClick = onPricing) { Text("دیدن پلن‌ها") }
        }
        TextButton(onClick = onLogin) { Text("ورود") }
        Column(Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(28.dp)).background(Color(0xFFD5E1EB)).padding(16.dp)) {
            Text("سارا", fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp))
            Text("تحویل پروژه برای پنجشنبه قطعی شد.", Modifier.padding(top = 12.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp))
            Text("قبل از جلسه مدارک را دوباره بخوان.", Modifier.padding(top = 8.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFFFF4C2)).padding(12.dp))
        }
        listOf(
            "یک نفر، یک دفتر" to "برای هر کسی یک صفحه جدا.",
            "دو رنگ، دو معنی" to "موضوع صحبت سفید است و یادداشت زرد.",
            "روی خود گوشی" to "بدون اتصال به لپ‌تاپ، داده‌ها همین‌جا می‌مانند.",
        ).forEach { (title, body) ->
            Column(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(body, color = Color(0xFF707579), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun PricingScreen(onStart: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        TextButton(onClick = onBack) { Text("بازگشت") }
        Text("قیمت‌ها", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("سقف روزانه هر دو نوع ثبت را می‌شمارد: موضوع صحبت و یادداشت.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
        PlanCard("رایگان", "۰ تومان", "پنج ثبت در روز", onStart)
        PlanCard("پایه", "۵۰۰٬۰۰۰ تومان در ماه", "صد ثبت در روز و دانلود صحبت‌ها", onStart)
        PlanCard("پیشرفته", "۱٬۰۰۰٬۰۰۰ تومان در ماه", "خلاصه هوشمند، به‌علاوه همه امکانات پایه", onStart)
    }
}

@Composable
private fun PlanCard(title: String, price: String, blurb: String, onStart: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFFF7F9FB)).padding(16.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(price, color = Blue, modifier = Modifier.padding(top = 4.dp))
        Text(blurb, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
        Button(onClick = onStart, Modifier.padding(top = 12.dp)) { Text("شروع") }
    }
}

private object PlayedOnce {
    var bottomBar = false
}

@Composable
private fun DropHeader(content: @Composable () -> Unit) {
    val dy = with(LocalDensity.current) { -16.dp.roundToPx() }
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    AnimatedVisibility(
        shown,
        Modifier.fillMaxWidth(),
        enter = fadeIn(tween(200, easing = FastOutSlowInEasing)) + slideInVertically(tween(200, easing = FastOutSlowInEasing)) { dy },
    ) { content() }
}

@Composable
private fun RiseRow(id: String, index: Int, arrived: MutableList<String>, content: @Composable () -> Unit) {
    val ready = id in arrived
    var shown by remember(id) { mutableStateOf(ready) }
    val dy = with(LocalDensity.current) { 12.dp.roundToPx() }
    LaunchedEffect(id) {
        if (shown) return@LaunchedEffect
        delay(index.coerceAtMost(10) * 40L)
        shown = true
        if (id !in arrived) arrived.add(id)
    }
    AnimatedVisibility(
        shown,
        Modifier.fillMaxWidth(),
        enter = fadeIn(tween(220, easing = FastOutSlowInEasing)) + slideInVertically(tween(220, easing = FastOutSlowInEasing)) { dy },
    ) { content() }
}

@Composable
fun BottomBar(route: String, avatar: String?, name: String, onSelect: (String) -> Unit) {
    val palette = LocalTg.current
    val idle = if (palette.dark) Color(0xFFE0E0E0) else Color(0xFF17212B)
    val accent = Blue
    val pill = themeContainer()
    val items = listOf(
        TabGlyph("chats", "چت‌ها", Icons.Outlined.ChatBubbleOutline),
        TabGlyph("contacts", "مخاطبین", Icons.Outlined.People),
        TabGlyph("profile", "پروفایل", Icons.Outlined.Person),
        TabGlyph("support", "ارتباط با ما", Icons.Filled.Headphones),
    )
    val dy = with(LocalDensity.current) { 20.dp.roundToPx() }
    val played = PlayedOnce.bottomBar
    var shown by remember { mutableStateOf(played) }
    LaunchedEffect(Unit) {
        shown = true
        PlayedOnce.bottomBar = true
    }
    AnimatedVisibility(
        shown,
        Modifier.fillMaxWidth(),
        enter = fadeIn(tween(250, easing = FastOutSlowInEasing)) + slideInVertically(tween(250, easing = FastOutSlowInEasing)) { dy },
    ) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(6.dp, RoundedCornerShape(50), ambientColor = Color.Black.copy(alpha = 0.06f), spotColor = Color.Black.copy(alpha = 0.08f))
                .clip(RoundedCornerShape(50))
                .background(palette.surface.copy(alpha = 0.95f))
                .border(1.dp, if (palette.dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f), RoundedCornerShape(50))
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val on = route == item.id
                val tint by animateColorAsState(if (on) accent else idle, tween(200, easing = FastOutSlowInEasing), label = "tab-tint")
                val capsule by animateColorAsState(if (on) pill else Color.Transparent, tween(200, easing = FastOutSlowInEasing), label = "tab-pill")
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(capsule)
                        .clickable { onSelect(item.id) }
                        .padding(horizontal = if (on) 8.dp else 4.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (item.id == "profile") {
                        PersonAvatar(name, avatar, size = 20.dp)
                    } else {
                        Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(20.dp))
                    }
                    Text(item.label, fontSize = 10.sp, fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium, color = tint, maxLines = 1)
                }
            }
        }
    }
    }
}

private data class TabGlyph(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

/** باز کردن یک لینک؛ در صورت تعیین پکیج، فقط همان اپ (مثلاً تلگرام) هدف قرار می‌گیرد. */
private fun startView(context: android.content.Context, uri: String, pkg: String? = null): Boolean {
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)).apply {
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        if (pkg != null) setPackage(pkg)
    }
    return runCatching {
        context.startActivity(intent)
        true
    }.getOrDefault(false)
}

/** هدایت مستقیم به چت/پروفایل آیدی؛ اگر اپ نصب نبود یا دیپ‌لینک کار نکرد، به وب و سپس کلیپ‌بورد برمی‌گردد. */
private fun openDeepLink(context: android.content.Context, targets: List<Pair<String, String?>>): Boolean =
    targets.any { (uri, pkg) -> startView(context, uri, pkg) }

/** نشانی صفحهٔ حمایت اختیاری روی سایت. */
private const val SUPPORT_DONATE_URL = "https://hamsohbat.darkube.ir/donate"

@Composable
fun SupportScreen(onBack: (() -> Unit)? = null, onStartChat: () -> Unit = {}) {
    val context = LocalContext.current
    val view = androidx.compose.ui.platform.LocalView.current
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val dark = LocalDark.current
    val page = if (dark) Color(0xFF0E1621) else Color(0xFFE6E6FA)
    val card = if (dark) Color(0xFF17212B) else Color.White
    val ink = if (dark) Color.White else Color(0xFF111827)
    val muted = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val line = if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
    val softBlue = themeContainer()
    var toast by remember { mutableStateOf("") }
    LaunchedEffect(toast) { if (toast.isNotEmpty()) { delay(1600); toast = "" } }
    fun tap(targets: List<Pair<String, String?>>, copy: String, message: String) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        val opened = openDeepLink(context, targets)
        if (!opened) {
            clipboard.setText(androidx.compose.ui.text.AnnotatedString(copy))
            toast = message
        }
    }
    val mailSubject = Uri.encode("پشتیبانی دفتر گفتگو")
    val mailPrimary = "mailto:recamenderapp@gmail.com?subject=$mailSubject"
    val telegramTargets = listOf(
        "tg://resolve?domain=127_27" to "org.telegram.messenger",
        "tg://resolve?domain=127_27" to null,
        "https://t.me/127_27" to null,
    )
    val baleTargets = listOf(
        "bale://resolve?domain=127_27" to null,
        "https://web.bale.ai/chat?uid=127_27" to null,
    )
    val mailTargets = listOf(
        "googlegmail:///co?to=recamenderapp@gmail.com&subject=$mailSubject" to "com.google.android.gm",
        mailPrimary to "com.google.android.gm",
        mailPrimary to null,
    )
    Box(Modifier.fillMaxSize().background(page)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (onBack != null) 4.dp else 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) }
                }
                Text(
                    "ارتباط با ما",
                    color = ink,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(card)
                        .border(1.dp, line, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier.size(88.dp).clip(CircleShape).background(softBlue),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Headphones, null, tint = Blue, modifier = Modifier.size(46.dp))
                    }
                    Text(
                        "پشتیبانی و ارتباط مستقیم",
                        color = ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    Text(
                        "همواره آماده پاسخگویی به سؤالات و راهنمایی شما هستیم.",
                        color = muted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp).padding(horizontal = 8.dp),
                    )
                    val press = remember { MutableInteractionSource() }
                    val down by press.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (down) 0.98f else 1f, label = "support-cta")
                    val ctaBg = if (down) Blue.copy(alpha = 0.85f) else Blue
                    Row(
                        Modifier
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(ctaBg)
                            .clickable(press, null) {
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                onStartChat()
                            }
                            .padding(vertical = 14.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.HeadsetMic, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(
                            "شروع گفتگو با پشتیبانی",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                Column(
                    Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(card)
                        .border(1.dp, line, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                ) {
                    Text(
                        "راه‌های ارتباطی دیگر",
                        color = muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
                    )
                    SupportRow(SupportBrand.Telegram, if (dark) Color(0xFF17212B) else Color(0xFFF4F6F8), "تلگرام", "@127_27", ink, muted) {
                        tap(telegramTargets, "@127_27", "آیدی تلگرام کپی شد: @127_27")
                    }
                    SupportRow(SupportBrand.Bale, if (dark) Color(0xFF17212B) else Color(0xFFF4F6F8), "بله", "@127_27", ink, muted) {
                        tap(baleTargets, "@127_27", "آیدی بله کپی شد: @127_27")
                    }
                    SupportRow(SupportBrand.Gmail, if (dark) Color(0xFF17212B) else Color(0xFFF4F6F8), "ایمیل پشتیبانی", "recamenderapp@gmail.com", ink, muted) {
                        tap(mailTargets, "recamenderapp@gmail.com", "ایمیل پشتیبانی کپی شد: recamenderapp@gmail.com")
                    }
                }
            }
        }
        if (toast.isNotEmpty()) {
            Text(
                toast,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xE617212B))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

private enum class SupportBrand { Telegram, Bale, Gmail }

/** لوگوی برند به‌صورت برداری (تلگرام، بله، جی‌میل) تا در هر اندازه‌ای شفاف بماند. */
@Composable
private fun BrandLogo(brand: SupportBrand, size: androidx.compose.ui.unit.Dp = 28.dp) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (brand) {
            SupportBrand.Telegram -> {
                drawCircle(Color(0xFF229ED9), radius = w / 2f)
                val plane = Path().apply {
                    moveTo(w * 0.17f, h * 0.51f)
                    lineTo(w * 0.84f, h * 0.22f)
                    lineTo(w * 0.66f, h * 0.80f)
                    lineTo(w * 0.49f, h * 0.59f)
                    close()
                }
                drawPath(plane, Color.White)
                val fold = Path().apply {
                    moveTo(w * 0.49f, h * 0.59f)
                    lineTo(w * 0.80f, h * 0.31f)
                    lineTo(w * 0.50f, h * 0.74f)
                    close()
                }
                drawPath(fold, Color(0xFFCFE8F9))
            }
            SupportBrand.Bale -> {
                drawCircle(Color(0xFF2EBD59), radius = w / 2f)
                drawRoundRect(
                    Color.White,
                    topLeft = Offset(w * 0.20f, h * 0.22f),
                    size = Size(w * 0.60f, h * 0.46f),
                    cornerRadius = CornerRadius(w * 0.15f, w * 0.15f),
                )
                val tail = Path().apply {
                    moveTo(w * 0.33f, h * 0.66f)
                    lineTo(w * 0.28f, h * 0.86f)
                    lineTo(w * 0.47f, h * 0.68f)
                    close()
                }
                drawPath(tail, Color.White)
                drawLine(Color(0xFF2EBD59), Offset(w * 0.34f, h * 0.46f), Offset(w * 0.45f, h * 0.57f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
                drawLine(Color(0xFF2EBD59), Offset(w * 0.45f, h * 0.57f), Offset(w * 0.68f, h * 0.36f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
            }
            SupportBrand.Gmail -> {
                drawRoundRect(Color.White, cornerRadius = CornerRadius(w * 0.24f, w * 0.24f))
                drawRoundRect(Color(0xFFE5E7EB), cornerRadius = CornerRadius(w * 0.24f, w * 0.24f), style = Stroke(width = w * 0.03f))
                drawRect(Color(0xFF4285F4), topLeft = Offset(w * 0.17f, h * 0.30f), size = Size(w * 0.13f, h * 0.40f))
                drawRect(Color(0xFF34A853), topLeft = Offset(w * 0.70f, h * 0.30f), size = Size(w * 0.13f, h * 0.40f))
                val fold = Path().apply {
                    moveTo(w * 0.17f, h * 0.70f)
                    lineTo(w * 0.40f, h * 0.52f)
                    lineTo(w * 0.40f, h * 0.70f)
                    close()
                }
                drawPath(fold, Color(0xFFFBBC04))
                drawLine(Color(0xFFEA4335), Offset(w * 0.23f, h * 0.31f), Offset(w * 0.50f, h * 0.54f), strokeWidth = w * 0.14f, cap = StrokeCap.Round)
                drawLine(Color(0xFFEA4335), Offset(w * 0.50f, h * 0.54f), Offset(w * 0.77f, h * 0.31f), strokeWidth = w * 0.14f, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun SupportRow(
    brand: SupportBrand,
    tile: Color,
    title: String,
    value: String,
    ink: Color,
    muted: Color,
    onClick: () -> Unit,
) {
    val press = remember { MutableInteractionSource() }
    val down by press.collectIsPressedAsState()
    val scale by animateFloatAsState(if (down) 0.98f else 1f, label = title)
    Row(
        Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(press, null, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(tile), contentAlignment = Alignment.Center) {
            BrandLogo(brand, 28.dp)
        }
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(title, color = ink, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(
                value,
                color = muted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
            )
        }
        Icon(Icons.Filled.ContentCopy, null, tint = muted, modifier = Modifier.size(16.dp))
    }
}

// ---------- چت درون‌برنامه‌ای پشتیبانی (کاربر ↔ ادمین) ----------

@Composable
private fun SupportBadge(size: androidx.compose.ui.unit.Dp) {
    Box(Modifier.size(size).clip(CircleShape).background(if (LocalDark.current) Color(0xFF203447) else LocalAppTheme.current.containerColor), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Headphones, null, tint = Blue, modifier = Modifier.size(size * 0.52f))
    }
}

@Composable
private fun supportClock(millis: Long): String =
    persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(millis)))

@Composable
private fun SupportBubble(message: SupportMessage, mine: Boolean) {
    val palette = LocalTg.current
    val chatTheme = LocalChatPalette.current
    val bubble = if (mine) (if (palette.dark) palette.outgoing else chatTheme.outgoingBubble) else (if (palette.dark) Color(0xFF1E293B) else Color.White)
    val ink = if (palette.dark && !mine) Color.White else palette.text
    val shape = if (mine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(
            Modifier
                .fillMaxWidth(0.80f)
                .shadow(1.dp, shape)
                .clip(shape)
                .background(bubble)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            if (message.kind == "voice" && message.path != null) {
                VoiceMessage(message.path)
            } else if (message.kind == "photo" && message.path != null) {
                MediaPreview(message.path, "photo")
            }
            if (message.body.isNotBlank()) {
                val chatFont = LocalChatFont.current
                Text(
                    message.body,
                    color = ink,
                    fontSize = chatFont.sp,
                    lineHeight = (chatFont * 1.5f).sp,
                    modifier = Modifier.padding(top = if (message.kind == "text") 0.dp else 4.dp),
                )
            }
            Text(
                supportClock(message.createdAt),
                fontSize = 10.sp,
                color = if (message.kind == "voice") Color.White.copy(alpha = 0.85f) else palette.subtext,
                modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun SupportComposer(
    body: String,
    onBody: (String) -> Unit,
    onSend: () -> Unit,
    onMic: () -> Unit,
    onAttach: () -> Unit,
) {
    val dark = LocalDark.current
    Surface(
        color = if (dark) Color(0xFF17212B) else Color.White,
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onAttach) { Icon(Icons.Filled.AttachFile, "پیوست", tint = Color(0xFF6B7280), modifier = Modifier.size(22.dp)) }
            Box(Modifier.weight(1f).padding(horizontal = 4.dp)) {
                if (body.isEmpty()) Text("پیام خود را بنویسید...", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                BasicTextField(
                    body,
                    onBody,
                    Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = if (dark) Color.White else Color(0xFF222222), fontSize = 14.sp),
                    cursorBrush = SolidColor(Blue),
                    maxLines = 4,
                )
            }
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Blue)
                    .clickable { if (body.isBlank()) onMic() else onSend() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (body.isBlank()) Icons.Filled.Mic else Icons.AutoMirrored.Filled.Send,
                    if (body.isBlank()) "ضبط ویس" else "ارسال",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/** اتاق گفتگوی پشتیبانی؛ برای کاربر با پشتیبانی و برای ادمین با همان کاربر گفتگو می‌کند. */
@Composable
fun SupportChatRoom(app: App, userId: String, asAdmin: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val dark = LocalDark.current
    val target = app.db.user(userId)
    val page = if (dark) Color(0xFF0E1621) else Color(0xFFE6E6FA)
    val ink = if (dark) Color.White else Color(0xFF111827)
    val muted = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val hairline = if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
    var messages by remember(userId) { mutableStateOf(app.db.supportMessages(userId)) }
    var body by remember { mutableStateOf("") }
    var recording by remember { mutableStateOf(false) }
    var voicePaused by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf("") }
    val voiceFile = remember { mutableStateOf<File?>(null) }
    val recorder = remember { mutableStateOf<android.media.MediaRecorder?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    fun reload() {
        messages = app.db.supportMessages(userId)
    }
    LaunchedEffect(userId) {
        if (asAdmin) app.db.markSupportReadByAdmin(userId) else app.db.markSupportReadByUser(userId)
    }
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
    LaunchedEffect(recording, voicePaused) {
        if (!recording) {
            seconds = 0
            return@LaunchedEffect
        }
        while (recording) {
            delay(1000)
            if (recording && !voicePaused) seconds += 1
        }
    }
    fun send(text: String, kind: String = "text", path: String? = null) {
        if (text.isBlank() && path == null) return
        app.db.sendSupport(userId, asAdmin, text.trim(), kind, path)
        reload()
    }
    fun beginVoice() {
        val file = File(context.filesDir, "support-${System.currentTimeMillis()}.m4a")
        val rec = if (android.os.Build.VERSION.SDK_INT >= 31) android.media.MediaRecorder(context) else @Suppress("DEPRECATION") android.media.MediaRecorder()
        try {
            rec.setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            rec.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            rec.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
            rec.setOutputFile(file.absolutePath)
            rec.prepare()
            rec.start()
            voiceFile.value = file
            recorder.value = rec
            voicePaused = false
            recording = true
        } catch (e: Exception) {
            runCatching { rec.release() }
            error = e.message ?: "ضبط ممکن نیست"
        }
    }
    fun finishVoice(save: Boolean) {
        runCatching { recorder.value?.stop() }
        runCatching { recorder.value?.release() }
        recorder.value = null
        recording = false
        voicePaused = false
        val file = voiceFile.value
        voiceFile.value = null
        if (save && file != null) send("", "voice", file.absolutePath) else file?.delete()
    }
    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) beginVoice() else error = "اجازه میکروفون داده نشد"
    }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val saved = withContext(Dispatchers.IO) { runCatching { copyChatFile(context, uri) }.getOrNull() }
            if (saved == null) {
                error = "پیوست ارسال نشد"
                return@launch
            }
            send(saved.second, "photo", saved.first)
        }
    }
    val title = if (asAdmin) target?.name ?: "کاربر" else "پشتیبانی دفتر گفتگو"
    val subtitle = if (asAdmin) {
        listOfNotNull(target?.phone, target?.username?.takeIf { it.isNotBlank() }?.let { "@$it" }).joinToString(" · ")
    } else {
        "معمولاً در سریع‌ترین زمان پاسخ می‌دهیم"
    }
    Column(Modifier.fillMaxSize().background(page)) {
        Row(
            Modifier.fillMaxWidth().background(if (dark) Color(0xFF17212B) else Color.White).padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) }
            if (asAdmin) PersonAvatar(title, target?.avatar, size = 40.dp) else SupportBadge(40.dp)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(title, color = ink, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text(subtitle, color = muted, fontSize = 11.sp, maxLines = 1)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(hairline))
        if (messages.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(28.dp)) {
                    SupportBadge(72.dp)
                    Text(
                        if (asAdmin) "هنوز پیامی از این کاربر نیامده" else "هر سؤالی داری همین‌جا بپرس",
                        color = ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                    Text(
                        if (asAdmin) "پاسخ تو مستقیم در اپ برای کاربر نمایش داده می‌شود." else "پیام‌ها مستقیم به تیم پشتیبانی می‌رسد و همین‌جا پاسخ می‌گیری.",
                        color = muted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
            ) {
                itemsIndexed(messages, key = { _, m -> m.id }) { index, message ->
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (index == 0 || dayKey(messages[index - 1].createdAt) != dayKey(message.createdAt)) {
                            DateSeparatorPill(message.createdAt)
                        }
                        SupportBubble(message, mine = message.fromAdmin == asAdmin)
                    }
                }
            }
        }
        if (error.isNotEmpty()) {
            Text(error, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
            LaunchedEffect(error) { delay(2400); error = "" }
        }
        if (recording) {
            Row(
                Modifier.fillMaxWidth().background(if (dark) Color(0xFF17212B) else Color.White).padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Delete, "لغو", tint = Color(0xFFFF3B30), modifier = Modifier.size(22.dp).clickable { finishVoice(false) })
                Row(Modifier.weight(1f).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF3B30)))
                    Text(persianDigits(String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)), color = ink, modifier = Modifier.padding(start = 8.dp))
                    Row(
                        Modifier.padding(start = 16.dp).clickable {
                            val rec = recorder.value ?: return@clickable
                            if (voicePaused) { runCatching { rec.resume() }; voicePaused = false } else { runCatching { rec.pause() }; voicePaused = true }
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(if (voicePaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, null, tint = muted, modifier = Modifier.size(18.dp))
                        Text(if (voicePaused) "ادامه" else "توقف", color = muted, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Icon(Icons.AutoMirrored.Filled.Send, "ارسال ویس", tint = Color(0xFF4ECC5F), modifier = Modifier.size(24.dp).clickable { finishVoice(true) })
            }
        } else {
            SupportComposer(
                body = body,
                onBody = { body = it },
                onSend = { send(body); body = "" },
                onMic = { micPermission.launch(android.Manifest.permission.RECORD_AUDIO) },
                onAttach = { photoPicker.launch("image/*") },
            )
        }
    }
}

/** صندوق پیام‌های پشتیبانی کاربران در داشبورد ادمین. */
@Composable
fun AdminSupportInbox(app: App, onOpen: (String) -> Unit) {
    var threads by remember { mutableStateOf(app.db.supportThreads()) }
    val card = Color(0xFF101A2D)
    val line = Color(0xFF1E2D4A)
    val accent = Blue
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("پیام‌های پشتیبانی کاربران", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("گفتگوهای آغازشده از داخل اپ", color = Color(0xFF8BA0B8), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        if (threads.isEmpty()) {
            Text("هنوز کاربری گفتگو را شروع نکرده است.", color = Color(0xFF8BA0B8), modifier = Modifier.padding(top = 24.dp))
        } else {
        LazyColumn(Modifier.fillMaxSize().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(threads, key = { it.userId }) { thread ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(card)
                        .border(1.dp, line, RoundedCornerShape(16.dp))
                        .clickable {
                            app.db.markSupportReadByAdmin(thread.userId)
                            threads = app.db.supportThreads()
                            onOpen(thread.userId)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PersonAvatar(thread.name, thread.avatar, size = 44.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(thread.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            if (thread.unread > 0) {
                                Box(Modifier.padding(start = 6.dp).size(18.dp).clip(CircleShape).background(accent), contentAlignment = Alignment.Center) {
                                    Text(persianDigits(thread.unread.toString()), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            listOfNotNull(thread.phone, thread.username?.takeIf { it.isNotBlank() }?.let { "@$it" }).joinToString(" · "),
                            color = Color(0xFF8BA0B8),
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                        Text(
                            when {
                                thread.lastKind == "voice" -> "🎤 پیام صوتی"
                                thread.lastKind == "photo" -> "🖼 تصویر"
                                else -> thread.lastBody.ifBlank { "—" }
                            },
                            color = Color(0xFFD6E2F0),
                            fontSize = 12.sp,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                    Text(
                        persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(thread.lastAt))),
                        color = Color(0xFF8BA0B8),
                        fontSize = 10.sp,
                    )
                }
            }
        }
        }
    }
}

private fun persianDigits(input: String): String {
    val persian = "۰۱۲۳۴۵۶۷۸۹"
    return input.map { ch -> if (ch in '0'..'9') persian[ch - '0'] else ch }.joinToString("")
}

private fun formatToman(amount: Int): String {
    val grouped = String.format(Locale.US, "%,d", amount).replace(',', '٬')
    return persianDigits(grouped)
}

@Composable
fun AuthScreen(app: App, onLoggedIn: (String) -> Unit, openSignup: () -> Unit, openForgot: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf<Pair<String, String>?>(null) }
    Box(Modifier.fillMaxSize()) {
    AuthCard(title = "ورود") {
        AuthField(phone, { phone = it }, "شماره موبایل", KeyboardType.Phone)
        AuthPassword(password, { password = it }, "رمز عبور")
        AuthButton("ورود") {
            val normalized = normalizePhone(phone)
            if (normalized == null) {
                alert = "شماره نادرست" to "شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود."
                return@AuthButton
            }
            try {
                val user = app.db.login(normalized, password)
                app.setSession(user.id)
                onLoggedIn(user.id)
            } catch (e: Exception) {
                alert = "ورود ناموفق" to (e.message ?: "خطا")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Text("ثبت‌نام", color = Color(0xFF0288D1), fontSize = 14.sp, modifier = Modifier.clickable(onClick = openSignup))
            Text("فراموشی رمز", color = Color(0xFF0288D1), fontSize = 14.sp, modifier = Modifier.clickable(onClick = openForgot))
            Text("ورود مهمان", color = Color(0xFF0288D1), fontSize = 14.sp, modifier = Modifier.clickable {
                try {
                    val id = app.db.signupGuest()
                    app.setSession(id)
                    onLoggedIn(id)
                } catch (e: Exception) {
                    alert = "مهمان" to (e.message ?: "خطا")
                }
            })
        }
    }
    alert?.let { (title, body) ->
        PrettyAlert(title, body, onConfirm = { alert = null })
    }
    }
}

@Composable
fun SignUpScreen(app: App, onReady: (String) -> Unit, openLogin: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf<Pair<String, String>?>(null) }
    val verified = code == OTP
    Box(Modifier.fillMaxSize()) {
    AuthCard(title = "ثبت‌نام") {
        when (step) {
            0 -> {
                AuthField(name, { name = it }, "نام", KeyboardType.Text)
                AuthField(phone, { phone = it }, "شماره موبایل", KeyboardType.Phone)
                AuthButton("ثبت‌نام") {
                    if (name.trim().length < 2) {
                        alert = "نام" to "نام را کامل‌تر بنویس."
                        return@AuthButton
                    }
                    if (normalizePhone(phone) == null) {
                        alert = "شماره نادرست" to "شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود."
                        return@AuthButton
                    }
                    step = 1
                    alert = "کد تأیید" to "کد آزمایشی ${persianDigits(OTP)} را در ۶ خانه وارد کن."
                }
            }
            1 -> {
                Text("کد پیامک را وارد کن", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                OtpBoxes(code) { code = it.filter { ch -> ch.isDigit() }.take(6) }
                if (verified) Text("کد تأیید شد", color = CheckGreen, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                AuthButton("مرحله بعد") {
                    if (!verified) {
                        alert = "کد نادرست" to "شش رقم کد را درست وارد کن."
                        return@AuthButton
                    }
                    step = 2
                }
            }
            else -> {
                AuthPassword(password, { password = it }, "رمز عبور")
                AuthPassword(confirm, { confirm = it }, "تکرار رمز عبور")
                AuthButton("ساخت حساب") {
                    if (password.length < 8) {
                        alert = "رمز کوتاه" to "رمز باید حداقل ۸ کاراکتر باشد."
                        return@AuthButton
                    }
                    if (password != confirm) {
                        alert = "رمز" to "رمز و تکرار آن یکی نیست."
                        return@AuthButton
                    }
                    val normalized = normalizePhone(phone) ?: return@AuthButton
                    try {
                        val id = app.db.signup(normalized, name, password)
                        app.setSession(id)
                        onReady(id)
                    } catch (e: Exception) {
                        alert = "ثبت‌نام" to (e.message ?: "خطا")
                    }
                }
            }
        }
        AuthLinks("ورود", openLogin, if (step > 0) "مرحله قبل" else "ورود", { if (step > 0) step -= 1 else openLogin() })
    }
    alert?.let { (title, body) ->
        PrettyAlert(title, body, onConfirm = { alert = null })
        }
    }
}

@Composable
fun ForgotScreen(app: App, openLogin: () -> Unit, openSignup: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf<Pair<String, String>?>(null) }
    val verified = code == OTP
    Box(Modifier.fillMaxSize()) {
    AuthCard(title = "بازیابی رمز") {
        when (step) {
            0 -> {
                AuthField(phone, { phone = it }, "شماره موبایل", KeyboardType.Phone)
                AuthButton("ادامه") {
                    if (normalizePhone(phone) == null) {
                        alert = "شماره نادرست" to "شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود."
                        return@AuthButton
                    }
                    step = 1
                    alert = "کد تأیید" to "کد آزمایشی ${persianDigits(OTP)} را در ۶ خانه وارد کن."
                }
            }
            1 -> {
                OtpBoxes(code) { code = it.filter { ch -> ch.isDigit() }.take(6) }
                if (verified) Text("کد تأیید شد", color = CheckGreen, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                AuthButton("مرحله بعد") {
                    if (!verified) alert = "کد نادرست" to "شش رقم کد را درست وارد کن." else step = 2
                }
            }
            else -> {
                AuthPassword(password, { password = it }, "رمز جدید")
                AuthPassword(confirm, { confirm = it }, "تکرار رمز")
                AuthButton("بازیابی رمز") {
                    if (password.length < 8 || password != confirm) {
                        alert = "رمز" to "رمز حداقل ۸ کاراکتر باشد و با تکرارش یکی باشد."
                        return@AuthButton
                    }
                    val normalized = normalizePhone(phone) ?: return@AuthButton
                    try {
                        app.db.reset(normalized, password)
                        alert = "انجام شد" to "رمز تازه ذخیره شد. حالا وارد شو."
                        step = 3
                    } catch (e: Exception) {
                        alert = "بازیابی" to (e.message ?: "خطا")
                    }
                }
            }
        }
        AuthLinks("ورود", openLogin, "ثبت‌نام", openSignup)
    }
    alert?.let { (title, body) ->
        PrettyAlert(title, body, onConfirm = {
            alert = null
            if (step == 3) openLogin()
        })
    }
    }
}

@Composable
private fun OtpBoxes(value: String, onChange: (String) -> Unit) {
    val ok = value.length == 6 && value == OTP
    val border = if (ok) CheckGreen else Color(0xFFE5E7EB)
    BasicTextField(
        value,
        onChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { index ->
                    val char = value.getOrNull(index)?.toString().orEmpty()
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, if (ok) CheckGreen else if (char.isNotEmpty()) Blue else border, RoundedCornerShape(12.dp))
                            .background(if (ok) Color(0xFFE8F5E9) else Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (char.isEmpty()) "" else persianDigits(char), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    )
}

@Composable
fun PrettyAlert(title: String, body: String, confirm: String = "باشه", onConfirm: () -> Unit, dismiss: String? = null, onDismiss: (() -> Unit)? = null) {
    LaunchedEffect(title, body) { delay(2800); onConfirm() }
    Box(Modifier.fillMaxSize().statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
        Row(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF17212B))
                .clickable { (onDismiss ?: onConfirm)() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                if (title.isNotBlank()) Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (body.isNotBlank()) Text(body, color = Color(0xFFD6DEE8), fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Text(confirm, color = Blue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun AuthCard(title: String, content: @Composable () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    Box(
        Modifier.fillMaxSize().background(Color(0xFFE6E6FA)),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(shown, enter = fadeIn(tween(280)) + scaleIn(tween(280), initialScale = 0.96f)) {
            Column(
                Modifier.padding(20.dp).fillMaxWidth().clip(RoundedCornerShape(32.dp)).shadow(18.dp, RoundedCornerShape(32.dp)).background(Color.White).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("مکالمات من", color = Color(0xFF0288D1), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                Text(title, fontSize = 28.sp, fontWeight = FontWeight.Black, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), textAlign = TextAlign.Start)
                content()
            }
        }
    }
}

@Composable
private fun AuthField(value: String, onChange: (String) -> Unit, placeholder: String, kind: KeyboardType) {
    BasicTextField(
        value,
        onChange,
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF111827), textAlign = TextAlign.Start),
        cursorBrush = SolidColor(Color(0xFF29B6F6)),
        keyboardOptions = KeyboardOptions(keyboardType = kind),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text(placeholder, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                inner()
            }
        },
    )
}

@Composable
private fun AuthPassword(value: String, onChange: (String) -> Unit, placeholder: String) {
    var visible by remember { mutableStateOf(false) }
    BasicTextField(
        value,
        onChange,
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF111827), textAlign = TextAlign.Right),
        cursorBrush = SolidColor(Color(0xFF29B6F6)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { visible = !visible }) {
                    Icon(if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "نمایش رمز", tint = Color(0xFF6B7280))
                }
                Box(Modifier.weight(1f).padding(vertical = 10.dp), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                    inner()
                }
            }
        },
    )
}

@Composable
private fun AuthCodeRow(value: String, onChange: (String) -> Unit, request: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "code")
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicTextField(
            value,
            onChange,
            Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
            cursorBrush = SolidColor(Color(0xFF29B6F6)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (value.isEmpty()) Text("کد پیامک", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    inner()
                }
            },
        )
        Box(
            Modifier.graphicsLayer { scaleX = scale; scaleY = scale }.clip(RoundedCornerShape(14.dp)).background(Color(0xFF29B6F6)).clickable(interaction, null, onClick = request).padding(horizontal = 16.dp, vertical = 14.dp),
        ) { Text("کد", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun AuthButton(label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "auth-btn")
    Box(
        Modifier.graphicsLayer { scaleX = scale; scaleY = scale }.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF29B6F6)).clickable(interaction, null, onClick = onClick).padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = Color.White, fontWeight = FontWeight.Bold) }
}

@Composable
private fun AuthLinks(right: String, onRight: () -> Unit, left: String, onLeft: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(right, color = Color(0xFF0288D1), fontSize = 14.sp, modifier = Modifier.clickable(onClick = onRight))
        Text(left, color = Color(0xFF0288D1), fontSize = 14.sp, modifier = Modifier.clickable(onClick = onLeft))
    }
}

@Composable
fun PhoneContactsScreen(onBack: () -> Unit, onPick: (String) -> Unit) {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }
    var contacts by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var query by remember { mutableStateOf("") }
    var askSync by remember { mutableStateOf(false) }
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) contacts = readPhoneContacts(context)
    }
    LaunchedEffect(granted) {
        if (!granted) {
            ask.launch(android.Manifest.permission.READ_CONTACTS)
            return@LaunchedEffect
        }
        contacts = readPhoneContacts(context)
        val prefs = context.getSharedPreferences("tg", 0)
        val last = prefs.getLong("last_contacts_sync_prompt_time", 0L)
        if (System.currentTimeMillis() - last >= 86_400_000L) {
            prefs.edit().putLong("last_contacts_sync_prompt_time", System.currentTimeMillis()).apply()
            askSync = true
        }
    }
    val shown = contacts.filter { query.isBlank() || it.first.contains(query, true) || it.second.contains(query) }
    val dark = LocalDark.current
    val page = if (dark) Color(0xFF0F172A) else Color(0xFFE6E6FA)
    val ink = if (dark) Color.White else Color(0xFF111827)
    val hairline = if (dark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.05f)
    val rowLine = if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
    val arrived = remember { mutableStateListOf<String>() }
    Column(Modifier.fillMaxSize().background(page)) {
        Column(
            Modifier.fillMaxWidth().zIndex(1f).background(page).drawBehind {
                val stroke = 0.5.dp.toPx()
                drawLine(hairline, Offset(0f, size.height - stroke / 2f), Offset(size.width, size.height - stroke / 2f), stroke)
            },
        ) {
            DropHeader {
                Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) }
                    Text("مخاطبین", color = Blue, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
                Row(
                    Modifier.padding(horizontal = 12.dp).padding(bottom = 10.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (dark) Color(0xFF242F3D) else Color(0xFFF1F2F6)).padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Search, null, tint = if (dark) Color(0xFFA0ACB6) else Color(0xFF707579), modifier = Modifier.size(18.dp))
                    BasicTextField(
                        query,
                        { query = it },
                        Modifier.padding(start = 8.dp).weight(1f),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = ink),
                        cursorBrush = SolidColor(Blue),
                        decorationBox = { inner ->
                            if (query.isEmpty()) Text("جستجوی مخاطب...", color = if (dark) Color(0xFF7F91A4) else Color(0xFF8E8E93), fontSize = 14.sp)
                            inner()
                        },
                    )
                }
                }
            }
        }
        if (granted) {
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(shown, key = { _, item -> item.second }) { index, (name, phone) ->
                    RiseRow(phone, index, arrived) {
                        Column(Modifier.fillMaxWidth().clickable { onPick(phone) }) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(52.dp).clip(CircleShape).background(Color.hsl((name.sumOf { it.code } % 360).toFloat(), 0.55f, 0.42f)), contentAlignment = Alignment.Center) {
                                    Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column(Modifier.padding(start = 12.dp)) {
                                    Text(name, color = ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text(phone, color = if (dark) Color(0xFFD1D5DB) else Color(0xFF6B7280), fontSize = 14.sp)
                                }
                            }
                            if (index < shown.lastIndex) {
                                Box(Modifier.padding(start = 76.dp, end = 16.dp).fillMaxWidth().height(0.5.dp).background(rowLine))
                            }
                        }
                    }
                }
            }
        }
    }
    if (askSync) {
        ContactsSyncDialog(
            dark = dark,
            onConfirm = {
                contacts = readPhoneContacts(context)
                askSync = false
            },
            onDismiss = { askSync = false },
        )
    }
}

@Composable
private fun ContactsSyncDialog(dark: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val scale = remember { Animatable(0.95f) }
    val fade = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { fade.animateTo(1f, tween(150)) }
        scale.animateTo(1f, tween(150, easing = FastOutSlowInEasing))
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color(0x66000000)), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .fillMaxWidth(0.9f)
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = fade.value }
                    .shadow(24.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (dark) Color(0xFF17212B) else Color.White),
            ) {
                Box(Modifier.fillMaxWidth().background(Blue).padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Contacts, null, tint = Color.White, modifier = Modifier.size(42.dp))
                }
                Text("بروزرسانی مخاطبین؟", color = if (dark) Color.White else Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                Text(
                    "برنامه مخاطبین همگام‌نشده جدیدی را شناسایی کرده است. آیا مایلید اکنون آن‌ها را همگام کنید؟ اگر از دستگاه، سیم‌کارت و حساب کاربری خود استفاده می‌کنید، «تایید» را انتخاب نمایید.",
                    color = if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp),
                )
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 16.dp, top = 8.dp), horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End)) {
                    Text("تایید", color = Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onConfirm))
                    Text("انصراف", color = Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onDismiss))
                }
            }
        }
    }
}

private fun readPhoneContacts(context: android.content.Context): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    val cursor = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    ) ?: return emptyList()
    cursor.use {
        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (it.moveToNext()) {
            val name = it.getString(nameIndex).orEmpty()
            val phone = it.getString(phoneIndex).orEmpty()
            if (name.isNotBlank() && phone.isNotBlank()) list += name to phone
        }
    }
    return list.distinctBy { it.second }
}

@Composable
fun NewPersonScreen(app: App, userId: String, initialPhone: String = "", back: () -> Unit) {
    AddPersonSheet(app, userId, initialPhone, onClose = back, onSaved = back)
}

private fun readRecentChats(context: android.content.Context): List<String> {
    return context.getSharedPreferences("tg", 0).getString("recent_chats", "").orEmpty().split(",").filter { it.isNotBlank() }
}

private fun saveRecentChat(context: android.content.Context, id: String) {
    val next = listOf(id) + readRecentChats(context).filter { it != id }
    context.getSharedPreferences("tg", 0).edit().putString("recent_chats", next.take(12).joinToString(",")).apply()
}

@Composable
private fun ChatSearchOverlay(
    people: List<Person>,
    query: String,
    onQuery: (String) -> Unit,
    dark: Boolean,
    ink: Color,
    recent: List<String>,
    onClearRecent: () -> Unit,
    onOpen: (String) -> Unit,
    onClose: () -> Unit,
) {
    val focus = remember { FocusRequester() }
    var recentIds by remember { mutableStateOf(recent) }
    val page = if (dark) Color(0xFF17212B) else Color(0xFFE6E6FA)
    val results = people.filter { query.isBlank() || it.name.contains(query, true) || it.phone.contains(query) || it.lastBody.contains(query, true) }
    val recentPeople = recentIds.mapNotNull { id -> people.find { it.id == id } }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Column(Modifier.fillMaxSize().background(page).statusBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) }
                BasicTextField(
                    query,
                    onQuery,
                    Modifier.weight(1f).focusRequester(focus),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = ink),
                    cursorBrush = SolidColor(Blue),
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) Text("جستجو در گفتگوها...", color = if (dark) Color(0xFF8B9AAB) else Color(0xFF8E8E93))
                            inner()
                        }
                    },
                )
                if (query.isNotEmpty()) IconButton(onClick = { onQuery("") }) { Icon(Icons.Filled.Close, "پاک کردن", tint = ink) }
            }
            if (query.isBlank() && recentPeople.isNotEmpty()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("جستجوهای اخیر", color = ink, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("پاک کردن همه", color = Blue, fontSize = 13.sp, modifier = Modifier.clickable {
                        onClearRecent()
                        recentIds = emptyList()
                    })
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    recentPeople.forEach { person ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onOpen(person.id) }) {
                            PersonAvatar(person.name, person.avatar, size = 54.dp)
                            Text(person.name, color = ink, fontSize = 12.sp, maxLines = 1, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
            LazyColumn(Modifier.weight(1f)) {
                items(results, key = { it.id }) { person ->
                    Row(Modifier.fillMaxWidth().clickable { onOpen(person.id) }.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        PersonAvatar(person.name, person.avatar, size = 48.dp)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(person.name, color = ink, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(person.lastBody.ifBlank { person.phone }, color = if (dark) Color(0xFF8B9AAB) else Color(0xFF707579), fontSize = 13.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncTitle(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var online by remember { mutableStateOf(isDeviceOnline(context)) }
    androidx.compose.runtime.DisposableEffect(Unit) {
        val manager = context.getSystemService(android.net.ConnectivityManager::class.java)
        val callback = object : android.net.ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) { online = true }
            override fun onLost(network: android.net.Network) { online = false }
        }
        runCatching { manager.registerDefaultNetworkCallback(callback) }
        onDispose { runCatching { manager.unregisterNetworkCallback(callback) } }
    }
    AnimatedContent(online, modifier = modifier, transitionSpec = { fadeIn(tween(280)) togetherWith fadeOut(tween(180)) }, label = "sync") { connected ->
        Text(
            if (connected) "همگام با سرور" else "در حال اتصال...",
            color = Blue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun isDeviceOnline(context: android.content.Context): Boolean {
    return runCatching {
        val manager = context.getSystemService(android.net.ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(network) ?: return false
        caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }.getOrDefault(true)
}

@Composable
fun ChatsScreen(
    app: App,
    userId: String,
    contacts: Boolean,
    open: (String) -> Unit,
    onAdmin: () -> Unit = {},
    onNotices: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    onCreate: () -> Unit = {},
    onAi: () -> Unit = {},
    onPay: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    darkTheme: Boolean = false,
    draftPhone: String = "",
    openComposer: Boolean = false,
    onComposerHandled: () -> Unit = {},
) {
    val context = LocalContext.current
    var people by remember { mutableStateOf(app.db.people(userId)) }
    var creating by remember { mutableStateOf(false) }
    var groupMode by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("all") }
    var menu by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf<Pair<String, String>?>(null) }
    val account = app.db.user(userId)
    LaunchedEffect(openComposer, draftPhone) {
        if (openComposer) {
            draft = draftPhone
            creating = true
            onComposerHandled()
        }
    }
    val shown = people.filter {
        (query.isBlank() || it.name.contains(query) || it.phone.contains(query)) && when (filter) {
            "note" -> it.hasNote
            "direct" -> !it.isGroup
            "group" -> it.isGroup
            else -> true
        }
    }
    val palette = LocalTg.current
    val dark = palette.dark
    val listState = rememberLazyListState()
    val arrived = remember { mutableStateListOf<String>() }
    val scrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 4 } }
    val hairline = if (dark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val rowLine = if (dark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.06f)
    val headerInk = if (dark) Color.White else Color.Black
    Box(Modifier.fillMaxSize().background(palette.bg)) {
        Column(Modifier.fillMaxSize()) {
            DropHeader {
            Column(
                Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .shadow(if (scrolled) 3.dp else 0.dp, ambientColor = Color.Black.copy(alpha = 0.03f), spotColor = Color.Black.copy(alpha = 0.05f))
                    .background(palette.bg)
                    .drawBehind {
                        val stroke = 0.5.dp.toPx()
                        drawLine(hairline, Offset(0f, size.height - stroke / 2f), Offset(size.width, size.height - stroke / 2f), stroke)
                    },
            ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "منو", tint = headerInk, modifier = Modifier.size(22.dp)) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text(if (darkTheme) "حالت روز" else "حالت شب") }, leadingIcon = { Icon(Icons.Filled.DarkMode, null) }, onClick = { menu = false; onToggleTheme() })
                        DropdownMenuItem(text = { Text("گروه جدید") }, leadingIcon = { Icon(Icons.Filled.Person, null) }, onClick = {
                            menu = false
                            if (account?.plan == "guest" && people.any { !it.saved }) alert = "حالت مهمان" to "در حالت مهمان فقط یک نفر می‌توانید اضافه کنید."
                            else { draft = ""; groupMode = true; creating = true }
                        })
                        DropdownMenuItem(text = { Text("پیام‌های ذخیره‌شده") }, leadingIcon = { Icon(Icons.Filled.PushPin, null) }, onClick = {
                            menu = false
                            open(app.db.ensureSaved(userId))
                        })
                        DropdownMenuItem(text = { Text("اطلاعیه‌ها") }, onClick = { menu = false; onNotices() })
                        if (isAdmin(account?.phone)) DropdownMenuItem(text = { Text("ادمین") }, onClick = { menu = false; onAdmin() })
                    }
                }
                if (account != null) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(listOf(Blue.copy(alpha = 0.20f), Blue.copy(alpha = 0.12f))))
                            .clickable(onClick = onPay)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Wallet, null, tint = palette.blue, modifier = Modifier.size(16.dp))
                        Text("موجودی: ${formatToman(account.balance)} تومان", color = palette.text, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { searching = true }) { Icon(Icons.Filled.Search, "جستجو", tint = headerInk, modifier = Modifier.size(22.dp)) }
                if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = headerInk) }
                SyncTitle()
            }
            if (!contacts) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("all" to "همه", "direct" to "شخصی", "group" to "گروه", "note" to "یادداشت‌ها").forEach { (id, label) ->
                        val on = filter == id
                        Text(
                            label,
                            color = if (on) LocalAppTheme.current.onButtonColor else if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
                            fontSize = 12.sp,
                            fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (on) Blue else Color.Transparent)
                                .clickable { filter = id }
                                .padding(horizontal = if (on) 16.dp else 14.dp, vertical = 6.dp),
                        )
                    }
                }
            }
            }
            }
            LazyColumn(Modifier.weight(1f).padding(bottom = 88.dp), state = listState) {
                if (!contacts && account?.plan == "advanced") {
                    item(key = "ai-chat") {
                        RiseRow("ai", 0, arrived) {
                        Column(Modifier.fillMaxWidth().clickable(onClick = onAi)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.padding(horizontal = 16.dp, vertical = 10.dp).size(52.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.AutoAwesome, null, tint = Color.White)
                                }
                                Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
                                    Text("چت با هوش مصنوعی", color = if (dark) Color.White else Color(0xFF111827), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text("یکی از گفتگوها را انتخاب کن", color = Color(0xFF6B7280), fontSize = 14.sp)
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            if (shown.isNotEmpty()) {
                                Box(Modifier.padding(start = 76.dp, end = 16.dp).fillMaxWidth().height(0.5.dp).background(rowLine))
                            }
                        }
                        }
                    }
                }
                itemsIndexed(shown, key = { _, person -> person.id }) { index, person ->
                    RiseRow(person.id, index, arrived) {
                    Column(Modifier.fillMaxWidth().clickable { open(person.id) }) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            PersonAvatar(person.name, person.avatar, Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                            Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
                                Text(person.name, color = palette.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                                Text(
                                    if (contacts) person.phone.ifBlank { person.role.ifBlank { "بدون شماره" } } else person.lastBody.ifBlank { person.role.ifBlank { "بدون ثبت" } },
                                    color = palette.subtext,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                )
                            }
                            Text(listStamp(person.lastAt), color = palette.subtext, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp))
                        }
                        if (index < shown.lastIndex) {
                            Box(Modifier.padding(start = 76.dp, end = 16.dp).fillMaxWidth().height(0.5.dp).background(rowLine))
                        }
                    }
                    }
                }
            }
        }
        if (searching) {
            ChatSearchOverlay(
                people = people,
                query = query,
                onQuery = { query = it },
                dark = dark,
                ink = headerInk,
                recent = readRecentChats(context),
                onClearRecent = { context.getSharedPreferences("tg", 0).edit().remove("recent_chats").apply() },
                onOpen = { id ->
                    saveRecentChat(context, id)
                    searching = false
                    query = ""
                    open(id)
                },
                onClose = { searching = false; query = "" },
            )
        }
        val fabScale by animateFloatAsState(if (creating) 0.9f else 1f, tween(200), label = "fab")
        val fabPop = remember { Animatable(0.6f) }
        val fabFade = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(150)
            launch { fabFade.animateTo(1f, tween(180, easing = FastOutSlowInEasing)) }
            fabPop.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 350f))
        }
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .graphicsLayer {
                    val scale = fabScale * fabPop.value
                    scaleX = scale
                    scaleY = scale
                    alpha = fabFade.value
                }
                .size(56.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(if (creating) Blue.copy(alpha = 0.85f) else Blue)
                .border(if (creating) 2.dp else 0.dp, Color.White, CircleShape)
                .clickable {
                    if (account?.plan == "guest" && people.isNotEmpty()) {
                        alert = "حالت مهمان" to "در حالت مهمان فقط یک نفر می‌توانید اضافه کنید."
                    } else {
                        draft = ""
                        groupMode = false
                        creating = !creating
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(if (creating) Icons.Filled.Close else Icons.Filled.Edit, "شخص جدید", tint = Color.White, modifier = Modifier.size(24.dp))
        }
        AnimatedVisibility(
            visible = creating,
            enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(240)) { it } + fadeOut(tween(180)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            AddPersonSheet(app, userId, draft, groupMode, onClose = { creating = false; groupMode = false }, onSaved = {
                        people = app.db.people(userId)
                        creating = false
            }, onError = { alert = "شخص جدید" to it })
        }
        alert?.let { (title, body) ->
            PrettyAlert(title, body, onConfirm = { alert = null })
        }
        val banner = app.db.activeBanner()
        val freshNotices = remember { app.db.unreadNotices(userId) }
        var showBanner by remember { mutableStateOf(account != null && !account.bannerSeen && banner != null) }
        var showNotices by remember { mutableStateOf(!showBanner && freshNotices.isNotEmpty()) }
        if (showBanner && banner != null) {
            Box(Modifier.fillMaxSize().background(Color(0x730E1621)), contentAlignment = Alignment.Center) {
                Column(Modifier.padding(24.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).padding(18.dp)) {
                    val image = banner.image?.let { File(it) }?.takeIf { it.exists() }?.let { BitmapFactory.decodeFile(it.absolutePath) }
                    if (image != null) Image(image.asImageBitmap(), null, Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                    Text("بنر", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(banner.title, color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 6.dp))
                    Text(banner.body, color = Color(0xFF374151), modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
                    Button(onClick = {
                        app.db.markBannerSeen(userId)
                        showBanner = false
                        showNotices = freshNotices.isNotEmpty()
                    }) { Text("متوجه شدم") }
                }
            }
        }
        if (showNotices && freshNotices.isNotEmpty()) {
            Box(Modifier.fillMaxSize().background(Color(0x730E1621)), contentAlignment = Alignment.Center) {
                Column(Modifier.padding(24.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).padding(18.dp)) {
                    Text("اطلاعیه", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    freshNotices.forEach { notice ->
                        Text(notice.title, color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                        Text(notice.body, color = Color(0xFF374151), modifier = Modifier.padding(top = 4.dp))
                    }
                    Button(onClick = {
                        app.db.markNoticesRead(userId)
                        showNotices = false
                    }, Modifier.padding(top = 16.dp)) { Text("خواندم") }
                }
            }
        }
    }
}

@Composable
private fun PersonAvatar(name: String, path: String?, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 52.dp) {
    val file = path?.let { File(it) }?.takeIf { it.exists() }
    val bitmap = file?.let { remember(it.absolutePath) { BitmapFactory.decodeFile(it.absolutePath) } }
    Box(
        modifier.size(size).clip(CircleShape).background(Color.hsl((name.sumOf { it.code } % 360).toFloat(), 0.55f, 0.42f)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun AddPersonSheet(
    app: App,
    userId: String,
    initialPhone: String,
    groupMode: Boolean = false,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    onError: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val palette = LocalTg.current
    var name by remember { mutableStateOf("") }
    var phone by remember(initialPhone) { mutableStateOf(initialPhone) }
    var role by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<String?>(null) }
    var memberDraft by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOf<String>()) }
    var crop by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) { runCatching { decodeSampledBitmap(context, uri, 1024) }.getOrNull() }
            if (bitmap == null) onError("عکس خوانده نشد") else crop = bitmap
        }
    }
    val dark = palette.dark
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(if (dark) Color(0xF21C2733) else Color.White)
            .border(1.dp, if (dark) Color(0x80374151) else palette.border, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .padding(bottom = 72.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "بستن", tint = if (dark) Color(0xFF9CA3AF) else palette.subtext) }
            Spacer(Modifier.weight(1f))
            Text("بستن", color = if (dark) Color(0xFF9CA3AF) else palette.subtext, modifier = Modifier.clickable(onClick = onClose))
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, null, tint = Color.White) }
            Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.padding(horizontal = 8.dp))
            Box(Modifier.size(46.dp).clip(CircleShape).background(Color(0xFF7E57C2)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Person, null, tint = Color.White) }
        }
        Text(if (groupMode) "گروه جدید" else "افزودن شخص جدید", color = if (dark) Color.White else palette.text, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Box(Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally).clickable { picker.launch("image/*") }) {
            PersonAvatar(name.ifBlank { "؟" }, avatar, size = 72.dp)
            Box(Modifier.align(Alignment.BottomEnd).size(24.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        SheetField(name, { name = it }, if (groupMode) "نام گروه" else "نام", Icons.Outlined.Person, dark)
        if (!groupMode) {
            SheetField(phone, { phone = it }, "شماره موبایل", Icons.Filled.Phone, dark, KeyboardType.Phone)
            SheetField(role, { role = it }, "سمت", Icons.Filled.Star, dark)
        }
        SheetField(memberDraft, { memberDraft = it }, "نام عضو", Icons.Filled.Person, dark)
        Text("افزودن عضو", color = Blue, modifier = Modifier.padding(top = 6.dp).clickable {
            if (memberDraft.isNotBlank()) {
                members = members + memberDraft.trim()
                memberDraft = ""
            }
        })
        if (members.isNotEmpty()) Text(members.joinToString("، "), color = palette.subtext, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        val pressed = remember { MutableInteractionSource() }
        val down by pressed.collectIsPressedAsState()
        val scale by animateFloatAsState(if (down) 0.98f else 1f, label = "save-person")
        Box(
            Modifier
                .padding(top = 12.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(Blue, Blue.copy(alpha = 0.8f))))
                .clickable(pressed, null) {
                    if (name.isBlank()) {
                        onError("فقط نام اجباری است.")
                        return@clickable
                    }
                    if (phone.isNotBlank() && normalizePhone(phone) == null) {
                        onError("شماره موبایل درست نیست.")
                        return@clickable
                    }
                    try {
                        app.db.addPerson(userId, name, if (groupMode) "" else phone, role, avatar, if (groupMode) members else members)
                        if (!isDeviceOnline(context)) app.db.enqueue("person", name)
                        onSaved()
                    } catch (e: Exception) {
                        onError(e.message ?: "خطا")
                    }
                }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text(if (groupMode) "ساخت گروه" else "ثبت شخص جدید", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
    crop?.let { bitmap ->
        CropDialog(bitmap, onCancel = { crop = null }) { saved ->
            val dest = File(context.filesDir, "person-draft-${System.currentTimeMillis()}.jpg")
            dest.outputStream().use { saved.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            saved.recycle()
            avatar = dest.absolutePath
            crop = null
        }
    }
}

@Composable
private fun SheetField(value: String, onChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, dark: Boolean, kind: KeyboardType = KeyboardType.Text) {
    val ink = if (dark) Color.White else Color(0xFF111827)
    val field = if (dark) Color(0xFF101926) else Color(0xFFF4F7FB)
    val line = if (dark) Color(0x99374151) else Color(0xFFE5E7EB)
    Row(
        Modifier.padding(top = 8.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(field).border(1.dp, line, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Blue, modifier = Modifier.size(18.dp))
        BasicTextField(
            value,
            onChange,
            Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 10.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = ink, fontSize = 15.sp, textAlign = TextAlign.Start),
            cursorBrush = SolidColor(Blue),
            keyboardOptions = KeyboardOptions(keyboardType = kind),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(label, color = Color(0xFF9CA3AF))
                    inner()
                }
            },
        )
    }
}

private fun listStamp(ms: Long): String {
    if (ms <= 0L) return ""
    val now = java.util.Calendar.getInstance()
    val day = java.util.Calendar.getInstance().apply { timeInMillis = ms }
    val same = now.get(java.util.Calendar.YEAR) == day.get(java.util.Calendar.YEAR) && now.get(java.util.Calendar.DAY_OF_YEAR) == day.get(java.util.Calendar.DAY_OF_YEAR)
    if (same) return SimpleDateFormat("HH:mm", Locale.US).format(Date(ms))
    if (now.timeInMillis - ms < 7L * 24 * 60 * 60 * 1000) return SimpleDateFormat("EEE", Locale.US).format(Date(ms))
    return SimpleDateFormat("MM/dd", Locale.US).format(Date(ms))
}

private val media = listOf(
    "phone" to "📞 تماس تلفنی",
    "in_person" to "🤝 جلسه حضوری",
    "online" to "🌐 مکالمه آنلاین",
    "sms" to "💬 پیامک و چت",
)

private fun mediumLabel(channel: String) = media.firstOrNull { it.first == channel }?.second ?: "📞 تماس تلفنی"

private val categories = listOf(
    "یادداشت" to ("note" to "phone"),
    "گفتگوی حضوری" to ("talk" to "in_person"),
    "گفتگوی مجازی" to ("talk" to "online"),
    "گفتگوی تلفنی" to ("talk" to "phone"),
)

private fun categoryIndex(kind: String, channel: String): Int {
    val found = categories.indexOfFirst { it.second.first == kind && it.second.second == channel }
    if (found >= 0) return found
    return if (kind == "note") 0 else 3
}

private val jalaliMonths = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")

private fun jalaliOf(millis: Long): Triple<Int, Int, Int> {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = millis
    val gy = cal.get(java.util.Calendar.YEAR)
    val gm = cal.get(java.util.Calendar.MONTH) + 1
    val gd = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val gDays = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val gy2 = if (gm > 2) gy + 1 else gy
    var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDays[gm - 1]
    var jy = -1595 + (33 * (days / 12053))
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }
    return if (days < 186) Triple(jy, 1 + days / 31, 1 + days % 31) else Triple(jy, 7 + (days - 186) / 30, 1 + (days - 186) % 30)
}

private fun jalaliDayLabel(millis: Long): String {
    val (y, m, d) = jalaliOf(millis)
    return persianDigits("$d ${jalaliMonths[m - 1]} $y")
}

private fun jalaliShort(millis: Long): String {
    val (y, m, d) = jalaliOf(millis)
    return persianDigits(String.format(Locale.US, "%04d/%02d/%02d", y, m, d))
}

private fun dayKey(millis: Long): Long = millis / 86_400_000L

private fun buildXlsx(rows: List<List<String>>): ByteArray {
    fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    fun col(index: Int): String {
        var n = index
        var out = ""
        while (true) {
            out = ('A' + (n % 26)) + out
            n = n / 26 - 1
            if (n < 0) break
        }
        return out
    }
    val sheet = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>")
    rows.forEachIndexed { r, row ->
        sheet.append("<row r=\"${r + 1}\">")
        row.forEachIndexed { c, v -> sheet.append("<c r=\"${col(c)}${r + 1}\" t=\"inlineStr\"><is><t xml:space=\"preserve\">${esc(v)}</t></is></c>") }
        sheet.append("</row>")
    }
    sheet.append("</sheetData></worksheet>")
    val contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>"
    val rels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>"
    val workbook = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"گزارش\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>"
    val wbRels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>"
    val out = java.io.ByteArrayOutputStream()
    java.util.zip.ZipOutputStream(out).use { zip ->
        fun put(name: String, data: String) {
            zip.putNextEntry(java.util.zip.ZipEntry(name))
            zip.write(data.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
        put("[Content_Types].xml", contentTypes)
        put("_rels/.rels", rels)
        put("xl/workbook.xml", workbook)
        put("xl/_rels/workbook.xml.rels", wbRels)
        put("xl/worksheets/sheet1.xml", sheet.toString())
    }
    return out.toByteArray()
}

private fun reportRows(personName: String, list: List<Entry>): List<List<String>> {
    val header = listOf("تاریخ", "ساعت", "نوع", "مخاطب", "متن")
    return listOf(header) + list.map { entry ->
        listOf(
            jalaliShort(entry.occurredAt),
            persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(entry.occurredAt))),
            if (entry.kind == "note") "یادداشت" else mediumLabel(entry.channel),
            entry.memberName ?: personName,
            entry.body.ifBlank { if (entry.voicePath != null) "[ویس]" else if (entry.mediaPath != null) "[رسانه]" else "" },
        )
    }
}

private fun reportText(personName: String, list: List<Entry>): String = buildString {
    appendLine("گزارش گفتگو با $personName")
    appendLine("تعداد گزاره‌ها: ${persianDigits(list.size.toString())}")
    appendLine()
    list.forEach { entry ->
        val time = persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(entry.occurredAt)))
        val kind = if (entry.kind == "note") "یادداشت" else mediumLabel(entry.channel)
        appendLine("${jalaliShort(entry.occurredAt)} $time · $kind")
        appendLine(entry.body.ifBlank { if (entry.voicePath != null) "[ویس]" else if (entry.mediaPath != null) "[رسانه]" else "" })
        appendLine()
    }
}

private fun rangeStart(rangeIndex: Int): Long {
    val day = 86_400_000L
    return when (rangeIndex) {
        0 -> System.currentTimeMillis() - day
        1 -> System.currentTimeMillis() - 3 * day
        2 -> System.currentTimeMillis() - 7 * day
        3 -> System.currentTimeMillis() - 30 * day
        4 -> System.currentTimeMillis() - 90 * day
        else -> 0L
    }
}

private val rangeLabels = listOf("امروز", "سه روز گذشته", "هفته گذشته", "ماه گذشته", "سه ماه گذشته", "اختیاری")

@Composable
private fun RangeChips(selected: Int, dark: Boolean, onSelect: (Int) -> Unit) {
    val chipBg = if (dark) Color(0xFF1C2738) else Color(0xFFF1F2F6)
    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        rangeLabels.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEachIndexed { i, label ->
                    val index = rangeLabels.indexOf(label)
                    val on = selected == index
                    Text(
                        label,
                        color = if (on) Color.White else if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (on) Blue else chipBg).clickable { onSelect(index) }.padding(vertical = 8.dp),
                    )
                }
            }
        }
        val onAll = selected == 5
        Text(
            "همۀ گفتگوها",
            color = if (onAll) Color.White else if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp).clip(RoundedCornerShape(12.dp)).background(if (onAll) Blue else chipBg).clickable { onSelect(5) }.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun DateSeparatorPill(millis: Long) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 6.dp)) {
        Text(jalaliDayLabel(millis), fontSize = 11.sp, color = Color(0xFF4B5563), modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    title: String,
    avatar: String?,
    subtitle: String,
    searchOpen: Boolean,
    query: String,
    onQuery: (String) -> Unit,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onAi: () -> Unit,
    onInfo: () -> Unit,
    onProfile: () -> Unit,
    onEditIdentity: () -> Unit,
    onSummary: () -> Unit,
    onExport: () -> Unit,
    onBuildFile: () -> Unit,
    onSearch: () -> Unit,
    onAll: () -> Unit,
    onNotes: () -> Unit,
    onDeleteIdentity: () -> Unit,
) {
    val dark = LocalDark.current
    val ink = if (dark) Color.White else LocalAppTheme.current.onContainerColor
    val sub = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val action = if (dark) Color(0xFFD1D5DB) else LocalAppTheme.current.onContainerColor
    val chipBg = if (dark) Color(0xFF1C2738) else LocalAppTheme.current.containerColor
    var menu by remember { mutableStateOf(false) }
    val pill = if (dark) Color(0xFF17212B) else LocalAppTheme.current.containerColor
    Column(Modifier.fillMaxWidth().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = CircleShape, shadowElevation = 2.dp, color = pill) {
                Box(Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink, modifier = Modifier.size(20.dp))
                }
            }
            Surface(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp,
                color = pill,
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            ) {
                Row(
                    Modifier.clip(RoundedCornerShape(24.dp)).clickable(onClick = onProfile).padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PersonAvatar(title, avatar, size = 32.dp)
                    Column(Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ink, maxLines = 1)
                        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = sub, maxLines = 1)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Surface(shape = CircleShape, shadowElevation = 2.dp, color = pill) {
                        Box(Modifier.size(40.dp).clip(CircleShape).clickable { menu = true }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.MoreVert, "منو", tint = action, modifier = Modifier.size(20.dp))
                        }
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("مشخصات هویت") }, onClick = { menu = false; onInfo() })
                        DropdownMenuItem(text = { Text("ویرایش هویت") }, onClick = { menu = false; onEditIdentity() })
                        DropdownMenuItem(text = { Text("خلاصه‌سازی") }, onClick = { menu = false; onSummary() })
                        DropdownMenuItem(text = { Text("خروجی گزارش") }, onClick = { menu = false; onExport() })
                        DropdownMenuItem(text = { Text("ساخت پرونده از گزارش") }, onClick = { menu = false; onBuildFile() })
                        DropdownMenuItem(text = { Text("جستجو در گزاره‌ها") }, onClick = { menu = false; onSearch() })
                        DropdownMenuItem(text = { Text("همۀ گزاره‌ها") }, onClick = { menu = false; onAll() })
                        DropdownMenuItem(text = { Text("فقط یادداشت‌ها") }, onClick = { menu = false; onNotes() })
                        DropdownMenuItem(text = { Text("حذف هویت", color = Color.Red) }, onClick = { menu = false; onDeleteIdentity() })
                    }
                }
                Surface(shape = CircleShape, shadowElevation = 2.dp, color = pill) {
                    Box(Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onAi), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AutoAwesome, "هوش مصنوعی", tint = Color(0xFFF5B301), modifier = Modifier.size(20.dp))
                    }
                }
                Surface(shape = CircleShape, shadowElevation = 2.dp, color = pill) {
                    Box(Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onDownload), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Download, "دانلود", tint = action, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        AnimatedVisibility(visible = searchOpen, enter = fadeIn(tween(160)) + expandVertically(tween(160)), exit = fadeOut(tween(120)) + shrinkVertically(tween(140))) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    query,
                    onQuery,
                    Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("جستجو در گزاره‌ها", fontSize = 13.sp) },
                    shape = RoundedCornerShape(22.dp),
                )
                Text("همه", color = ink, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp).clip(RoundedCornerShape(12.dp)).background(chipBg).clickable { onAll() }.padding(horizontal = 10.dp, vertical = 8.dp))
                Text("یادداشت‌ها", color = ink, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp).clip(RoundedCornerShape(12.dp)).background(chipBg).clickable { onNotes() }.padding(horizontal = 10.dp, vertical = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentitySheet(
    name: String,
    avatar: String?,
    phone: String,
    role: String,
    entries: List<Entry>,
    onDismiss: () -> Unit,
    onSendMessage: () -> Unit,
    onEdit: () -> Unit,
    onReports: () -> Unit,
) {
    val dark = LocalDark.current
    val context = LocalContext.current
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    var toast by remember { mutableStateOf("") }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val card = if (dark) Color(0xFF1E293B) else Color(0xFFF7F8FC)
    val ink = if (dark) Color.White else Color(0xFF111827)
    val muted = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = themeDialogContainer()) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PersonAvatar(name, avatar, size = 96.dp)
            Text(name, color = ink, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 12.dp))
            Text(if (phone.isBlank()) "شماره ثبت نشده" else persianDigits(phone), color = muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
            Column(Modifier.fillMaxWidth().padding(top = 18.dp).clip(RoundedCornerShape(18.dp)).background(card)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("شماره موبایل", color = muted, fontSize = 11.sp)
                        Text(if (phone.isBlank()) "ثبت نشده" else persianDigits(phone), color = ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (phone.isNotBlank()) {
                        Box(
                            Modifier.size(38.dp).clip(CircleShape).background(Blue).clickable {
                                runCatching {
                                    context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                                }.onFailure { toast = "برنامه تلفن در دسترس نیست" }
                            },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Filled.Phone, "تماس", tint = Color.White, modifier = Modifier.size(18.dp)) }
                        Box(
                            Modifier.padding(start = 8.dp).size(38.dp).clip(CircleShape).background(if (dark) Color(0xFF334155) else Color(0xFFE9EDF5)).clickable {
                                clipboard.setText(androidx.compose.ui.text.AnnotatedString(phone))
                                toast = "شماره کپی شد"
                            },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Filled.ContentCopy, "کپی", tint = ink, modifier = Modifier.size(17.dp)) }
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)))
                Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text("توضیحات", color = muted, fontSize = 11.sp)
                    Text(role.ifBlank { "بدون توضیحات" }, color = ink, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 3.dp))
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)))
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("تعداد گزاره‌ها", color = muted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text(persianDigits(entries.size.toString()), color = ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IdentityAction("ارسال پیام", Icons.AutoMirrored.Filled.Send, true) { toast = "نویسنده پیام پایین صفحه فعال است"; onSendMessage() }
                IdentityAction("ویرایش مخاطب", Icons.Filled.Edit, false) { onEdit() }
                IdentityAction("مشاهده گزارش‌ها", Icons.Filled.Download, false) { onReports() }
            }
            if (toast.isNotEmpty()) {
                Text(toast, color = muted, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
                LaunchedEffect(toast) { delay(1800); toast = "" }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.IdentityAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (primary) Blue else if (LocalDark.current) Color(0xFF1E293B) else Color(0xFFF7F8FC))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, label, tint = if (primary) Color.White else if (LocalDark.current) Color.White else Color(0xFF334155), modifier = Modifier.size(18.dp))
        Text(
            label,
            color = if (primary) Color.White else if (LocalDark.current) Color.White else Color(0xFF334155),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun ChatInputBar(
    body: String,
    onBody: (String) -> Unit,
    dark: Boolean,
    group: Boolean,
    members: List<Member>,
    memberId: String?,
    onMember: (String?) -> Unit,
    category: Int,
    onCategory: (Int) -> Unit,
    pendingMedia: String?,
    pendingKind: String,
    onAttach: () -> Unit,
    onMic: () -> Unit,
    onSend: () -> Unit,
) {
    val ink = if (dark) Color.White else Color(0xFF222222)
    val sub = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val labels = listOf("یادداشت", "گفتگوی حضوری", "گفتگوی مجازی", "گفتگوی تلفنی")
    var picker by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().background(if (dark) Color(0xFF17212B) else Color.White).padding(horizontal = 8.dp).padding(top = 6.dp, bottom = 10.dp)) {
        if (group) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InputChip("کل گروه", memberId == null, dark) { onMember(null) }
                members.forEach { member -> InputChip(member.name, memberId == member.id, dark) { onMember(member.id) } }
            }
        }
        if (pendingMedia != null) {
            Row(Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (pendingKind == "video") Icons.Filled.Videocam else Icons.Filled.Image, null, tint = Blue, modifier = Modifier.size(16.dp))
                Text(if (pendingKind == "video") "فیلم آماده ارسال" else "عکس آماده ارسال", color = ink, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
            }
        }
        Box(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    tonalElevation = 3.dp,
                    color = if (dark) Color(0xFF1E2B38) else Color(0xFFF4F6F8),
                    modifier = Modifier.weight(1f),
                ) {
                    Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(38.dp).clip(CircleShape).border(1.dp, if (dark) Color(0xFF33475C) else Color(0xFFDDE3EA), CircleShape).clickable { picker = !picker },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Edit, labels[category], tint = Blue, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onAttach, modifier = Modifier.size(38.dp)) { Icon(Icons.Filled.AttachFile, "پیوست", tint = sub, modifier = Modifier.size(20.dp)) }
                        Box(Modifier.weight(1f).padding(horizontal = 4.dp)) {
                            if (body.isEmpty()) Text("نوشتن گزاره...", color = sub, fontSize = 14.sp)
                            BasicTextField(
                                body,
                                onBody,
                                textStyle = androidx.compose.ui.text.TextStyle(color = ink, fontSize = 14.sp),
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                Spacer(Modifier.width(6.dp))
                val sendNow = body.isNotBlank() || pendingMedia != null
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(Blue).clickable { if (sendNow) onSend() else onMic() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(if (sendNow) Icons.AutoMirrored.Filled.Send else Icons.Filled.Mic, if (sendNow) "ارسال" else "ضبط ویس", tint = LocalAppTheme.current.onButtonColor, modifier = Modifier.size(20.dp))
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = picker,
                enter = fadeIn(tween(160)) + scaleIn(tween(160), initialScale = 0.94f),
                exit = fadeOut(tween(110)),
                modifier = Modifier.align(Alignment.BottomStart).offset(y = (-58).dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (dark) Color(0xFF1E2B38) else Color.White,
                    shadowElevation = 8.dp,
                    tonalElevation = 6.dp,
                ) {
                    Column(Modifier.width(190.dp).padding(6.dp)) {
                        labels.forEachIndexed { index, label ->
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onCategory(index); picker = false }.padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(selected = category == index, onClick = { onCategory(index); picker = false })
                                Text(label, color = ink, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InputChip(label: String, selected: Boolean, dark: Boolean, onClick: () -> Unit) {
    Text(
        label,
        color = if (selected) LocalAppTheme.current.onButtonColor else if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
        fontSize = 12.sp,
        modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(if (selected) Blue else if (dark) Color(0xFF1C2738) else Color(0xFFF1F2F6)).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun FormatPill(label: String, selected: Boolean, dark: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        label,
        color = if (selected) LocalAppTheme.current.onButtonColor else if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(if (selected) Blue else if (dark) Color(0xFF1C2738) else Color(0xFFF1F2F6)).clickable { onClick() }.padding(vertical = 10.dp),
    )
}

@Composable
private fun AiSummaryDialog(
    personName: String,
    entries: List<Entry>,
    dark: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var range by remember { mutableIntStateOf(0) }
    val ink = if (dark) Color.White else Color(0xFF111827)
    val sub = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val text = if (dark) Color(0xFFC7D2DE) else Color(0xFF4B5563)
    val count = entries.count { it.occurredAt >= rangeStart(range) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = themeDialogContainer(),
        title = {
            Column {
                Text("خلاصه‌سازی هوش مصنوعی", fontWeight = FontWeight.Bold, color = ink)
                Text(personName, fontSize = 13.sp, color = sub, modifier = Modifier.padding(top = 2.dp))
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "خلاصه با مدل‌های رایگان Gemini، OpenRouter و Nemotron ساخته می‌شود. متن گزاره‌های انتخابی فقط برای پردازش به سرویس مدل ارسال می‌شود و جای دیگری ذخیره نمی‌گردد.",
                    fontSize = 13.sp,
                    color = text,
                )
                RangeChips(selected = range, dark = dark, onSelect = { range = it })
                Text("${persianDigits(count.toString())} گزاره · رایگان", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 10.dp))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(range) }, colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor), shape = RoundedCornerShape(14.dp)) {
                Text("ساخت خلاصۀ رایگان")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", color = sub) } },
    )
}

@Composable
private fun ExportReportDialog(
    personName: String,
    entries: List<Entry>,
    dark: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var range by remember { mutableIntStateOf(0) }
    var excel by remember { mutableStateOf(true) }
    val ink = if (dark) Color.White else Color(0xFF111827)
    val sub = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val filtered = entries.filter { it.occurredAt >= rangeStart(range) }.sortedBy { it.occurredAt }
    val xlsxLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) { runCatching { context.contentResolver.openOutputStream(uri)?.use { it.write(buildXlsx(reportRows(personName, filtered))) } } }
            }
        }
        onDismiss()
    }
    val txtLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) { runCatching { context.contentResolver.openOutputStream(uri)?.use { it.write(reportText(personName, filtered).toByteArray(Charsets.UTF_8)) } } }
            }
        }
        onDismiss()
    }
    val stamp = if (filtered.isEmpty()) "بدون گزاره" else "${jalaliShort(filtered.first().occurredAt)} تا ${jalaliShort(filtered.last().occurredAt)} · ${persianDigits(filtered.size.toString())} گزاره"
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = themeDialogContainer(),
        title = {
            Column {
                Text("دانلود گزارش", fontWeight = FontWeight.Bold, color = ink)
                Text(personName, fontSize = 13.sp, color = sub, modifier = Modifier.padding(top = 2.dp))
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                RangeChips(selected = range, dark = dark, onSelect = { range = it })
                Text(stamp, fontSize = 12.sp, color = sub, modifier = Modifier.padding(top = 10.dp))
                Text("قالب فایل", fontWeight = FontWeight.Bold, color = ink, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormatPill("Excel (.xlsx)", excel, dark, Modifier.weight(1f)) { excel = true }
                    FormatPill("TXT (.txt)", !excel, dark, Modifier.weight(1f)) { excel = false }
                }
                Text("محل ذخیره در مرحلۀ بعد انتخاب می‌شود.", fontSize = 11.sp, color = sub, modifier = Modifier.padding(top = 10.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = { if (excel) xlsxLauncher.launch("$personName.xlsx") else txtLauncher.launch("$personName.txt") },
                colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor),
                shape = RoundedCornerShape(14.dp),
            ) { Text("دانلود") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", color = sub) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadScreen(app: App, userId: String, personId: String, back: () -> Unit) {
    val user = app.db.user(userId) ?: return
    var person by remember(personId) { mutableStateOf(app.db.people(userId).find { it.id == personId }) }
    val personTitle = person?.name ?: app.db.personName(personId)
    var entries by remember { mutableStateOf(app.db.entries(personId)) }
    var hidden by remember { mutableStateOf(setOf<String>()) }
    var fresh by remember { mutableStateOf(setOf<String>()) }
    var body by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var recording by remember { mutableStateOf(false) }
    var voicePaused by remember { mutableStateOf(false) }
    var recordSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(recording, voicePaused) {
        if (!recording) {
            recordSeconds = 0
            return@LaunchedEffect
        }
        while (recording) {
            kotlinx.coroutines.delay(1000)
            if (recording && !voicePaused) recordSeconds += 1
        }
    }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current
    val voiceFile = remember { mutableStateOf<File?>(null) }
    val voiceRecorder = remember { mutableStateOf<android.media.MediaRecorder?>(null) }
    fun beginVoice() {
        val file = File(context.filesDir, "voice-${System.currentTimeMillis()}.m4a")
        val rec = if (android.os.Build.VERSION.SDK_INT >= 31) android.media.MediaRecorder(context) else @Suppress("DEPRECATION") android.media.MediaRecorder()
        try {
            rec.setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            rec.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            rec.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
            rec.setOutputFile(file.absolutePath)
            rec.prepare()
            rec.start()
            voiceFile.value = file
            voiceRecorder.value = rec
            voicePaused = false
            recording = true
        } catch (e: Exception) {
            runCatching { rec.release() }
            error = e.message ?: "ضبط ممکن نیست"
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) beginVoice() else error = "اجازه میکروفون داده نشد"
    }
    fun stopVoice(app: App, user: User, personId: String, save: (String) -> Unit) {
        runCatching { voiceRecorder.value?.stop() }
        runCatching { voiceRecorder.value?.release() }
        voiceRecorder.value = null
        recording = false
        voicePaused = false
        voiceFile.value?.absolutePath?.let(save)
    }
    var kind by remember { mutableStateOf("talk") }
    var channel by remember { mutableStateOf("phone") }
    var upgrade by remember { mutableStateOf(false) }
    var pendingMedia by remember { mutableStateOf<String?>(null) }
    var pendingKind by remember { mutableStateOf("photo") }
    var compressing by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf(false) }
    var profileOpen by remember { mutableStateOf(false) }
    var aiOpen by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(person?.name ?: "") }
    var editPhone by remember { mutableStateOf(person?.phone ?: "") }
    var editRole by remember { mutableStateOf(person?.role ?: "") }
    var infoError by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var members by remember(personId) { mutableStateOf(app.db.members(personId)) }
    var memberId by remember { mutableStateOf<String?>(null) }
    var photoCrop by remember { mutableStateOf<Bitmap?>(null) }
    var toast by remember { mutableStateOf("") }
    LaunchedEffect(toast) {
        if (toast.isNotEmpty()) {
            delay(1800)
            toast = ""
        }
    }
    var attachMenu by remember { mutableStateOf(false) }
    var cameraShot by remember { mutableStateOf<File?>(null) }
    var newMember by remember { mutableStateOf("") }
    var searchOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var notesOnly by remember { mutableStateOf(false) }
    var exportOpen by remember { mutableStateOf(false) }
    var summaryOpen by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { photoCrop = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } }.onFailure { error = it.message ?: "عکس خوانده نشد" }
    }
    val cameraCapture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val file = cameraShot
        if (ok && file != null) photoCrop = BitmapFactory.decodeFile(file.absolutePath)
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) { error = "اجازه دوربین داده نشد"; return@rememberLauncherForActivityResult }
        val file = File(context.cacheDir, "shot-${System.currentTimeMillis()}.jpg")
        cameraShot = file
        val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".files", file)
        cameraCapture.launch(uri)
    }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val saved = withContext(Dispatchers.IO) { runCatching { copyChatFile(context, uri) }.getOrNull() }
            if (saved == null) {
                toast = "حجم فایل نباید بیشتر از ۲۰ مگابایت باشد."
                return@launch
            }
            try {
                app.db.addEntry(user, personId, saved.second, "file", channel, System.currentTimeMillis(), mediaPath = saved.first, memberId = memberId)
                if (!isDeviceOnline(context)) app.db.enqueue("entry", saved.second)
                entries = app.db.entries(personId)
                attachMenu = false
            } catch (e: Exception) {
                if ((e.message ?: "").let { it.contains("سقف") || it.contains("مهمان") }) upgrade = true else error = e.message ?: "خطا"
            }
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        compressing = true
        scope.launch {
            runCatching { pendingMedia = prepareVideo(context, uri); pendingKind = "video" }.onFailure { error = it.message ?: "فیلم ارسال نشد" }
            compressing = false
        }
    }
    val personPhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val path = savePersonAvatar(context, uri, personId)
            app.db.setPersonAvatar(personId, path)
            person = app.db.people(userId).find { it.id == personId }
        }.onFailure { infoError = it.message ?: "عکس ذخیره نشد" }
    }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Box(Modifier.fillMaxSize()) {
        ChatBackdrop(user)
        Column(Modifier.fillMaxSize()) {
        ChatTopBar(
            title = personTitle,
            avatar = person?.avatar,
            subtitle = "گزارش هویت",
            searchOpen = searchOpen,
            query = query,
            onQuery = { query = it },
            onBack = back,
            onDownload = { exportOpen = true },
            onAi = { aiOpen = true },
            onInfo = { info = true },
            onProfile = { profileOpen = true },
            onEditIdentity = { info = true },
            onSummary = { summaryOpen = true },
            onExport = { exportOpen = true },
            onBuildFile = {
                if (entries.isEmpty()) toast = "گزاره‌ای برای ساخت پرونده نیست" else shareTextFile(context, "$personTitle-پرونده.txt", reportText(personTitle, entries))
            },
            onSearch = { searchOpen = !searchOpen; if (!searchOpen) query = "" },
            onAll = { notesOnly = false; query = "" },
            onNotes = { notesOnly = true },
            onDeleteIdentity = { confirmDelete = true },
        )
        if (summary.isNotEmpty()) {
            Text(summary, Modifier.fillMaxWidth().background(Color.White).padding(12.dp), color = Color(0xFF334155))
        }
        val shown = entries.filter { (!notesOnly || it.kind == "note") && (query.isBlank() || it.body.contains(query.trim(), true)) }
        LazyColumn(Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(shown, key = { _, e -> e.id }) { index, entry ->
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (index == 0 || dayKey(shown[index - 1].occurredAt) != dayKey(entry.occurredAt)) DateSeparatorPill(entry.occurredAt)
                    val visible = entry.id !in hidden
                    AnimatedVisibility(
                        visible = visible,
                        enter = if (entry.id in fresh) fadeIn(tween(280)) + expandVertically(tween(280)) else fadeIn(tween(1)),
                        exit = shrinkVertically(tween(260)) + fadeOut(tween(220)),
                    ) {
                        JournalBubble(entry, personTitle, onPin = {
                            app.db.setEntryPinned(entry.id, !entry.pinned)
                            entries = app.db.entries(personId)
                        }, onDelete = {
                            hidden = hidden + entry.id
                            scope.launch {
                                delay(260)
                                app.db.deleteEntry(entry.id)
                                entries = entries.filter { it.id != entry.id }
                                hidden = hidden - entry.id
                            }
                        }, onEdit = {
                            editingId = entry.id
                            body = entry.body
                            kind = if (entry.kind == "note") "note" else "talk"
                        })
                    }
                }
            }
        }
        if (compressing) {
            Text("در حال کم‌حجم کردن فیلم...", color = Blue, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
        if (recording) {
            val bar = if (LocalDark.current) Color(0xFF17212B) else Color.White
            val ink = if (LocalDark.current) Color.White else Color(0xFF222222)
            Row(Modifier.fillMaxWidth().height(72.dp).background(bar).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Send, "ارسال ویس", tint = Color(0xFF4ECC5F), modifier = Modifier.size(22.dp).clickable {
                    if (voicePaused) runCatching { voiceRecorder.value?.resume() }
                    stopVoice(app, user, personId) { path ->
                        runCatching {
                            app.db.addEntry(user, personId, "", "voice", channel, System.currentTimeMillis(), path, memberId = memberId)
                            entries = app.db.entries(personId)
                        }.onFailure { error = it.message ?: "خطا" }
                    }
                })
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF3B30)))
                        Text(persianDigits(String.format(Locale.US, "%02d:%02d", recordSeconds / 60, recordSeconds % 60)), color = ink, modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(Modifier.clickable {
                        val rec = voiceRecorder.value ?: return@clickable
                        if (voicePaused) { runCatching { rec.resume() }; voicePaused = false } else { runCatching { rec.pause() }; voicePaused = true }
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (voicePaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, null, tint = if (LocalDark.current) Color(0xFFD1D5DB) else Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                        Text(if (voicePaused) "ادامه" else "توقف", color = if (LocalDark.current) Color(0xFFD1D5DB) else Color(0xFF6B7280), fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Icon(Icons.Filled.Delete, "لغو", tint = Color(0xFFFF3B30), modifier = Modifier.size(22.dp).clickable {
                    runCatching { voiceRecorder.value?.stop() }
                    runCatching { voiceRecorder.value?.release() }
                    voiceRecorder.value = null
                    voiceFile.value?.delete()
                    recording = false
                    voicePaused = false
                })
            }
        } else {
            ChatInputBar(
                body = body,
                onBody = { body = it },
                dark = LocalDark.current,
                group = person?.isGroup == true,
                members = members,
                memberId = memberId,
                onMember = { memberId = it },
                category = categoryIndex(kind, channel),
                onCategory = { index ->
                    val pick = categories[index].second
                    kind = pick.first
                    channel = pick.second
                },
                pendingMedia = pendingMedia,
                pendingKind = pendingKind,
                onAttach = { attachMenu = true },
                onMic = { permission.launch(android.Manifest.permission.RECORD_AUDIO) },
                onSend = {
                    error = ""
                    try {
                        if (editingId != null && pendingMedia == null) {
                            app.db.updateEntry(editingId!!, body, kind, channel, System.currentTimeMillis())
                            editingId = null
                        } else {
                            val sendKind = if (pendingMedia != null) pendingKind else kind
                            val caption = body.ifBlank { if (pendingKind == "video") "فیلم" else if (pendingMedia != null) "عکس" else "" }
                            app.db.addEntry(user, personId, caption, sendKind, channel, System.currentTimeMillis(), mediaPath = pendingMedia, memberId = memberId)
                            if (!isDeviceOnline(context)) app.db.enqueue("entry", caption)
                            pendingMedia = null
                        }
                        body = ""
                        entries = app.db.entries(personId)
                        upgrade = false
                    } catch (e: Exception) {
                        if ((e.message ?: "").let { it.contains("سقف") || it.contains("مهمان") }) upgrade = true else error = e.message ?: "خطا"
                    }
                },
            )
        }
        }
        if (upgrade) {
            PrettyAlert(
                "سقف پلن",
                if (user.plan == "guest") "در حالت مهمان فقط ۵ پیام آزمایشی می‌توانید بفرستید." else "سقف پلن شما به اتمام رسیده، لطفا پلن خود را تغییر دهید",
                onConfirm = { upgrade = false },
            )
        }
        if (error.isNotEmpty()) {
            PrettyAlert("توجه", error, onConfirm = { error = "" })
        }
        if (profileOpen) {
            IdentitySheet(
                name = editName.ifBlank { personTitle },
                avatar = person?.avatar,
                phone = person?.phone.orEmpty(),
                role = person?.role.orEmpty(),
                entries = entries,
                onDismiss = { profileOpen = false },
                onSendMessage = { profileOpen = false },
                onEdit = { profileOpen = false; info = true },
                onReports = { profileOpen = false; summaryOpen = true },
            )
        }
        if (info) {
            ModalBottomSheet(onDismissRequest = { info = false }, sheetState = sheet, containerColor = themeDialogContainer()) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).padding(bottom = 28.dp)) {
                    Text("اطلاعات کاربر", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    OutlinedTextField(newMember, { newMember = it }, Modifier.fillMaxWidth(), label = { Text("عضو جدید") })
                    TextButton(onClick = {
                        if (newMember.isBlank()) return@TextButton
                        app.db.addMember(personId, newMember)
                        members = app.db.members(personId)
                        person = app.db.people(userId).find { it.id == personId }
                        newMember = ""
                    }) { Text("افزودن به گروه") }
                    Box(Modifier.padding(vertical = 8.dp).clickable { personPhoto.launch("image/*") }) {
                        PersonAvatar(editName.ifBlank { personTitle }, person?.avatar, size = 72.dp)
                    }
                    OutlinedTextField(editName, { editName = it }, Modifier.fillMaxWidth(), label = { Text("نام") })
                    OutlinedTextField(editRole, { editRole = it }, Modifier.fillMaxWidth(), label = { Text("سمت") })
                    OutlinedTextField(editPhone, { editPhone = it }, Modifier.fillMaxWidth(), label = { Text("شماره موبایل") })
                    if (infoError.isNotEmpty()) Text(infoError, color = Color.Red)
                    Button(onClick = {
                        try {
                            app.db.updatePerson(personId, editName, editPhone, editRole)
                            person = app.db.people(userId).find { it.id == personId }
                            infoError = ""
                            info = false
                        } catch (e: Exception) { infoError = e.message ?: "خطا" }
                    }, Modifier.padding(top = 8.dp)) { Text("ذخیره") }
                    InfoLine("رسانه مشترک", entries.size.toString())
                }
            }
        }
        photoCrop?.let { bitmap ->
            PhotoSendPreview(bitmap, onCancel = { photoCrop = null }) { saved ->
                try {
                    val dest = File(context.filesDir, "photo-${System.currentTimeMillis()}.jpg")
                    dest.outputStream().use { saved.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                    if (saved !== bitmap) saved.recycle()
                    app.db.addEntry(user, personId, "عکس", "photo", channel, System.currentTimeMillis(), mediaPath = dest.absolutePath, memberId = memberId)
                    if (!isDeviceOnline(context)) app.db.enqueue("entry", "عکس")
                    entries = app.db.entries(personId)
                    photoCrop = null
                } catch (e: Exception) {
                    if ((e.message ?: "").let { it.contains("سقف") || it.contains("مهمان") }) upgrade = true else error = e.message ?: "خطا"
                }
            }
        }
        if (attachMenu) {
            AttachSheet(
                dark = LocalDark.current,
                onDismiss = { attachMenu = false },
                onCamera = { attachMenu = false; cameraPermission.launch(android.Manifest.permission.CAMERA) },
                onGallery = { attachMenu = false; imagePicker.launch("image/*") },
                onFile = { filePicker.launch(arrayOf("*/*")) },
                onVideo = { attachMenu = false; videoPicker.launch("video/*") },
            )
        }
        if (toast.isNotEmpty()) {
            Text(toast, color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xCC2B2B2F)).padding(horizontal = 16.dp, vertical = 10.dp))
        }
        if (aiOpen) {
            AiContextSheet(
                app = app,
                userId = userId,
                personId = personId,
                personName = personTitle,
                onClose = { aiOpen = false },
            )
        }
        if (summaryOpen) {
            AiSummaryDialog(
                personName = personTitle,
                entries = entries,
                dark = LocalDark.current,
                onDismiss = { summaryOpen = false },
                onConfirm = { summaryOpen = false; aiOpen = true },
            )
        }
        if (exportOpen) {
            ExportReportDialog(
                personName = personTitle,
                entries = entries,
                dark = LocalDark.current,
                onDismiss = { exportOpen = false },
            )
        }
        if (confirmDelete) {
            val dark = LocalDark.current
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = themeDialogContainer(),
                title = { Text("حذف هویت", fontWeight = FontWeight.Bold, color = if (dark) Color.White else Color(0xFF111827)) },
                text = { Text("همۀ گزاره‌ها و اطلاعات «$personTitle» حذف می‌شود. این عمل بازگشت‌پذیر نیست.", color = if (dark) Color(0xFFC7D2DE) else Color(0xFF4B5563)) },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                        app.db.deletePerson(personId)
                        back()
                    }) { Text("حذف", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("انصراف", color = if (dark) Color(0xFF9CA3AF) else Color(0xFF6B7280)) } },
            )
        }
    }
}

@Composable
private fun AttachSheet(dark: Boolean, onDismiss: () -> Unit, onCamera: () -> Unit, onGallery: () -> Unit, onFile: () -> Unit, onVideo: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Color(0x80000000)).clickable(remember { MutableInteractionSource() }, null, onClick = onDismiss))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(if (dark) Color(0xFF17212B) else Color.White).border(1.dp, if (dark) Color(0xFF243242) else Color(0xFFE5E7EB), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).padding(horizontal = 20.dp).padding(top = 12.dp, bottom = 28.dp),
        ) {
            Box(Modifier.align(Alignment.CenterHorizontally).width(36.dp).height(4.dp).clip(CircleShape).background(if (dark) Color(0xFF4B5563) else Color(0xFFD1D5DB)))
            Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                AttachAction("دوربین", Color(0xCC374151), Icons.Filled.CameraAlt, onCamera)
                AttachAction("نگارخانه", Color(0xFFE53935), Icons.Filled.Image, onGallery)
                AttachAction("فایل", Color(0xFFFB8C00), Icons.Filled.Description, onFile)
                AttachAction("ویدیو", Color(0xFF0088CC), Icons.Filled.Videocam, onVideo)
            }
        }
    }
}

@Composable
private fun AttachAction(label: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val press = remember { MutableInteractionSource() }
    val down by press.collectIsPressedAsState()
    val scale by animateFloatAsState(if (down) 0.95f else 1f, tween(120), label = label)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(press, null, onClick = onClick)) {
        Box(Modifier.graphicsLayer { scaleX = scale; scaleY = scale }.size(56.dp).shadow(4.dp, CircleShape).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Text(label, color = if (LocalDark.current) Color(0xFFD1D5DB) else Color(0xFF374151), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun FileMessage(name: String, path: String, ink: Color) {
    val context = LocalContext.current
    val size = remember(path) { readableSize(File(path).length()) }
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
        val file = File(path)
        if (!file.exists()) return@clickable
        val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".files", file)
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).setDataAndType(uri, "*/*").addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        runCatching { context.startActivity(android.content.Intent.createChooser(intent, name)) }
    }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFFB8C00)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Description, null, tint = Color.White)
        }
        Column(Modifier.padding(start = 10.dp)) {
            Text(name.ifBlank { "فایل" }, color = ink, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(size, color = ink.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

private fun readableSize(bytes: Long): String {
    val text = when {
        bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f مگابایت", bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> String.format(Locale.US, "%.0f کیلوبایت", bytes / 1024.0)
        else -> "$bytes بایت"
    }
    return persianDigits(text)
}

private fun copyChatFile(context: android.content.Context, uri: Uri): Pair<String, String>? {
    val limit = 20L * 1024 * 1024
    val reported = context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME, android.provider.OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use "" to -1L
        val name = cursor.getString(0) ?: "file"
        val size = if (cursor.isNull(1)) -1L else cursor.getLong(1)
        name to size
    } ?: ("" to -1L)
    if (reported.second > limit) return null
    val safe = reported.first.ifBlank { "file" }.replace(Regex("[\\\\/:*?\"<>|]"), "_")
    val dest = File(context.filesDir, "doc-${System.currentTimeMillis()}-$safe")
    context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } } ?: return null
    if (dest.length() > limit) {
        dest.delete()
        return null
    }
    return dest.absolutePath to reported.first.ifBlank { safe }
}

@Composable
private fun MenuAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(label, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

private fun voiceLength(path: String): String {
    return runCatching {
        val meta = android.media.MediaMetadataRetriever()
        meta.setDataSource(path)
        val ms = meta.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        meta.release()
        String.format(Locale.US, "%d:%02d", ms / 1000 / 60, (ms / 1000) % 60)
    }.getOrDefault("0:00")
}

private fun shareTextFile(context: android.content.Context, name: String, text: String) {
    val file = java.io.File(context.cacheDir, name)
    file.writeText(text)
    val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".files", file)
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_STREAM, uri)
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(android.content.Intent.createChooser(intent, "دانلود خلاصه"))
}

private val aiPrompts = listOf("خلاصه مکالمه", "لحن صحبت چطور بود؟", "کارهای بعدی چیست؟")

@Composable
fun AiChatScreen(app: App, userId: String, back: () -> Unit) {
    val palette = LocalTg.current
    var people by remember { mutableStateOf(app.db.people(userId)) }
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var personId by remember { mutableStateOf<String?>(null) }
    val person = people.find { it.id == personId }
    Column(Modifier.fillMaxSize().background(palette.bg)) {
        if (person == null) {
            Row(Modifier.fillMaxWidth().background(palette.surface).padding(horizontal = 4.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = palette.text) }
                Text("چت با هوش مصنوعی", color = palette.text, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = { searching = !searching }) { Icon(Icons.Filled.Search, "جستجو", tint = palette.blue) }
            }
            if (searching) {
                BasicTextField(
                    query,
                    { query = it },
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(palette.search).padding(horizontal = 14.dp, vertical = 10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(color = palette.text, fontSize = 15.sp, textAlign = TextAlign.Start),
                    cursorBrush = SolidColor(palette.blue),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("جستجو در گفتگوها", color = palette.subtext)
                        inner()
                    },
                )
            }
            LazyColumn(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(people.filter { query.isBlank() || it.name.contains(query) || it.lastBody.contains(query) }, key = { it.id }) { item ->
                    Row(
                        Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(palette.surface).border(1.dp, palette.border, RoundedCornerShape(16.dp)).clickable { personId = item.id }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box {
                            PersonAvatar(item.name, item.avatar, size = 48.dp)
                            Box(Modifier.align(Alignment.BottomStart).size(12.dp).clip(CircleShape).background(Color(0xFF4ECC5F)).border(2.dp, palette.surface, CircleShape))
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                            Text(item.name, color = palette.text, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(item.lastBody.ifBlank { item.role.ifBlank { "بدون ثبت" } }, color = palette.subtext, fontSize = 12.sp, maxLines = 1)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(listStamp(item.lastAt), color = palette.subtext, fontSize = 11.sp)
                            Text("✨", modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        } else {
            AiThread(app, userId, person, palette, onBack = { personId = null; people = app.db.people(userId) })
        }
    }
}

@Composable
private fun AiThread(app: App, userId: String, person: Person, palette: TgPalette, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val focus = remember { FocusRequester() }
    var turns by remember(person.id) { mutableStateOf(app.db.aiMessages(userId, person.id)) }
    var input by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var editing by remember { mutableStateOf<AiMessage?>(null) }
    var busy by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { preview = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } }.onFailure { toast = it.message ?: "عکس خوانده نشد" }
    }
    LaunchedEffect(toast) { if (toast.isNotEmpty()) { delay(1600); toast = "" } }
    LaunchedEffect(editing?.id) { if (editing != null) runCatching { focus.requestFocus() } }
    Box(Modifier.fillMaxSize().background(palette.bg)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().background(palette.surface).padding(horizontal = 4.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = palette.text) }
                Box(Modifier.size(42.dp).clip(CircleShape).border(2.dp, Brush.linearGradient(listOf(Blue, Color(0xFF7C4DFF))), CircleShape).padding(2.dp)) {
                    Box(Modifier.fillMaxSize().clip(CircleShape).background(Brush.linearGradient(listOf(Blue, Color(0xFF5C6BC0)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text("دستیار هوش مصنوعی", color = palette.text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("گفتگو با: ${person.name}", color = palette.blue, fontSize = 12.sp)
                    Text("پاسخ‌گوی هوشمند", color = Color(0xFF4ECC5F), fontSize = 11.sp)
                }
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "بیشتر", tint = palette.text) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("پاک کردن تاریخچه") }, onClick = {
                            menu = false
                            app.db.clearAi(userId, person.id)
                            turns = emptyList()
                        })
                        DropdownMenuItem(text = { Text("خروجی خلاصه") }, onClick = {
                            menu = false
                            val text = turns.joinToString("\n") { "${if (it.role == "user") "شما" else "دستیار"}: ${it.body}" }.ifBlank { "متنی نیست." }
                            shareTextFile(context, "ai-${person.name}.txt", text)
                        })
                    }
                }
            }
            LazyColumn(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(turns, key = { it.id }) { turn ->
                    AiBubble(turn, palette, onCopy = {
                        clipboard.setText(androidx.compose.ui.text.AnnotatedString(turn.body))
                        toast = "متن پیام کپی شد"
                    }, onEdit = {
                        editing = turn
                        input = turn.body
                        image = turn.image
                    })
                }
            }
            if (busy) Text("در حال فکر کردن...", color = palette.subtext, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            AiComposer(input, { input = it }, image, { image = null }, busy, palette, focus, { picker.launch("image/*") }) {
                val asked = input.trim()
                if (asked.isBlank() && image == null) return@AiComposer
                val edit = editing
                val picture = image
                if (edit != null) {
                    app.db.updateAiMessage(edit.id, asked, picture)
                    app.db.deleteAiAfter(userId, person.id, edit.createdAt)
                } else {
                    app.db.addAiMessage(userId, person.id, "user", asked, picture)
                }
                input = ""
                image = null
                editing = null
                turns = app.db.aiMessages(userId, person.id)
                val history = turns
                busy = true
                scope.launch {
                    val reply = withContext(Dispatchers.IO) { runCatching { askAboutChat(app, userId, person.id, history) }.getOrElse { it.message ?: "خطا" } }
                    app.db.addAiMessage(userId, person.id, "assistant", reply, null)
                    turns = app.db.aiMessages(userId, person.id)
                    busy = false
                }
            }
        }
        if (toast.isNotEmpty()) {
            Text(toast, color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 84.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xE617212B)).padding(horizontal = 14.dp, vertical = 8.dp))
        }
        preview?.let { bitmap ->
            PhotoSendPreview(bitmap, onCancel = { preview = null }) { saved ->
                val dest = File(context.filesDir, "ai-${System.currentTimeMillis()}.jpg")
                dest.outputStream().use { saved.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                if (saved !== bitmap) saved.recycle()
                image = dest.absolutePath
                preview = null
            }
        }
    }
}

@Composable
private fun AiContextSheet(app: App, userId: String, personId: String, personName: String, onClose: () -> Unit) {
    val palette = LocalTg.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val focus = remember { FocusRequester() }
    var turns by remember { mutableStateOf(app.db.aiMessages(userId, personId)) }
    var input by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<String?>(null) }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var editing by remember { mutableStateOf<AiMessage?>(null) }
    var busy by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }
    val sheetBg = if (palette.dark) Color(0xF217212B) else Color(0xF2FFFFFF)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { preview = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } }.onFailure { toast = it.message ?: "عکس خوانده نشد" }
    }
    LaunchedEffect(toast) { if (toast.isNotEmpty()) { delay(1400); toast = "" } }
    Box(Modifier.fillMaxSize().background(Color(0x66000000)), contentAlignment = Alignment.BottomCenter) {
        Column(Modifier.fillMaxWidth().heightIn(max = 560.dp).clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(sheetBg).border(1.dp, palette.border, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
            Box(Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally).width(42.dp).height(4.dp).clip(RoundedCornerShape(4.dp)).background(palette.subtext))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, null, tint = palette.blue)
                Text("تحلیل گفتگو با هوش مصنوعی", color = palette.text, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "بستن", tint = palette.subtext) }
            }
            Text(personName, color = palette.subtext, fontSize = 12.sp)
            Row(Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                aiPrompts.forEach { prompt ->
                    Text(prompt, color = palette.blue, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(palette.search).clickable { input = prompt }.padding(horizontal = 10.dp, vertical = 6.dp))
                }
            }
            LazyColumn(Modifier.padding(top = 8.dp).heightIn(max = 240.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(turns, key = { it.id }) { turn ->
                    AiBubble(turn, palette, onCopy = {
                        clipboard.setText(androidx.compose.ui.text.AnnotatedString(turn.body))
                        toast = "متن پیام کپی شد"
                    }, onEdit = { editing = turn; input = turn.body; image = turn.image })
                }
            }
            if (busy) Text("در حال فکر کردن...", color = palette.subtext, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            if (toast.isNotEmpty()) Text(toast, color = palette.blue, fontSize = 12.sp)
            AiComposer(input, { input = it }, image, { image = null }, busy, palette, focus, { picker.launch("image/*") }) {
                val asked = input.trim()
                if (asked.isBlank() && image == null) return@AiComposer
                val edit = editing
                val picture = image
                if (edit != null) {
                    app.db.updateAiMessage(edit.id, asked, picture)
                    app.db.deleteAiAfter(userId, personId, edit.createdAt)
                } else app.db.addAiMessage(userId, personId, "user", asked, picture)
                input = ""; image = null; editing = null
                turns = app.db.aiMessages(userId, personId)
                val history = turns
                busy = true
                scope.launch {
                    val reply = withContext(Dispatchers.IO) { runCatching { askAboutChat(app, userId, personId, history) }.getOrElse { it.message ?: "خطا" } }
                    app.db.addAiMessage(userId, personId, "assistant", reply, null)
                    turns = app.db.aiMessages(userId, personId)
                    busy = false
                }
            }
        }
    }
    preview?.let { bitmap ->
        PhotoSendPreview(bitmap, onCancel = { preview = null }) { saved ->
            val dest = File(context.filesDir, "ai-${System.currentTimeMillis()}.jpg")
            dest.outputStream().use { saved.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            if (saved !== bitmap) saved.recycle()
            image = dest.absolutePath
            preview = null
        }
    }
}

@Composable
private fun AiComposer(
    input: String,
    onInput: (String) -> Unit,
    image: String?,
    onClearImage: () -> Unit,
    busy: Boolean,
    palette: TgPalette,
    focus: FocusRequester,
    onPick: () -> Unit,
    onSend: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        if (image != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val bitmap = remember(image) { BitmapFactory.decodeFile(image) }
                if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.size(54.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                IconButton(onClick = onClearImage) { Icon(Icons.Filled.Close, "حذف عکس", tint = palette.subtext) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPick) { Icon(Icons.Filled.AttachFile, "پیوست", tint = palette.blue) }
            BasicTextField(
                input,
                onInput,
                Modifier.weight(1f).focusRequester(focus).clip(RoundedCornerShape(22.dp)).background(palette.search).padding(horizontal = 14.dp, vertical = 12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = palette.text, fontSize = 15.sp, textAlign = TextAlign.Start),
                cursorBrush = SolidColor(palette.blue),
                decorationBox = { inner ->
                    if (input.isEmpty()) Text("پیام به دستیار", color = palette.subtext)
                    inner()
                },
            )
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(46.dp).clip(CircleShape).background(if (busy) palette.subtext else LocalChatPalette.current.primary).clickable(enabled = !busy, onClick = onSend), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Send, "ارسال", tint = Color.White)
            }
        }
    }
}

@Composable
private fun AiBubble(turn: AiMessage, palette: TgPalette, onCopy: () -> Unit, onEdit: () -> Unit) {
    val mine = turn.role == "user"
    val bubble = if (mine) (if (palette.dark) palette.outgoing else LocalChatPalette.current.outgoingBubble) else palette.incoming
    val ink = if (palette.dark) Color.White else Color(0xFF222222)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.Start else Arrangement.End) {
        Column(Modifier.fillMaxWidth(0.86f).shadow(1.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(bubble).border(if (mine) 0.dp else 1.dp, palette.border, RoundedCornerShape(16.dp)).padding(10.dp)) {
            turn.image?.let { path ->
                val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            }
            if (turn.body.isNotBlank()) MarkdownText(turn.body, ink)
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(turn.createdAt)), color = palette.subtext, fontSize = 11.sp)
                if (mine) Icon(Icons.Filled.DoneAll, null, tint = palette.blue, modifier = Modifier.padding(start = 4.dp).size(14.dp))
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.ContentCopy, "کپی", tint = palette.subtext, modifier = Modifier.size(16.dp).clickable(onClick = onCopy))
                if (mine) Icon(Icons.Filled.Edit, "ویرایش", tint = palette.subtext, modifier = Modifier.padding(start = 10.dp).size(16.dp).clickable(onClick = onEdit))
            }
        }
    }
}

@Composable
private fun MarkdownText(source: String, color: Color) {
    val blocks = remember(source) { source.split("```") }
    Column {
        blocks.forEachIndexed { index, block ->
            if (index % 2 == 1) {
                Text(block.trim(), color = color, fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0x14000000)).padding(8.dp))
            } else {
                block.lines().forEach { line ->
                    val trimmed = line.trim()
                    val shown = when {
                        trimmed.startsWith("- ") -> "• ${trimmed.drop(2)}"
                        trimmed.startsWith("* ") -> "• ${trimmed.drop(2)}"
                        else -> line
                    }
                    if (shown.isNotBlank()) Text(inlineMarkdown(shown), color = color, fontSize = 15.sp, modifier = Modifier.padding(vertical = 1.dp))
                }
            }
        }
    }
}

private fun inlineMarkdown(line: String) = buildAnnotatedString {
    var rest = line
    while (rest.isNotEmpty()) {
        val bold = rest.indexOf("**")
        val code = rest.indexOf("`")
        val next = listOf(bold, code).filter { it >= 0 }.minOrNull()
        if (next == null) { append(rest); break }
        append(rest.substring(0, next))
        if (next == bold) {
            val end = rest.indexOf("**", next + 2)
            if (end < 0) { append(rest.substring(next)); break }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(rest.substring(next + 2, end)) }
            rest = rest.substring(end + 2)
        } else {
            val end = rest.indexOf("`", next + 1)
            if (end < 0) { append(rest.substring(next)); break }
            withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x22000000))) { append(rest.substring(next + 1, end)) }
            rest = rest.substring(end + 1)
        }
    }
}

private fun askAboutChat(app: App, userId: String, personId: String, turns: List<AiMessage>): String {
    val person = app.db.people(userId).find { it.id == personId } ?: error("گفتگو پیدا نشد")
    val transcript = app.db.entries(personId).takeLast(80).joinToString("\n") { entry ->
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(entry.occurredAt))
        val kind = if (entry.kind == "note") "یادداشت" else "صحبت"
        val text = entry.body.ifBlank { if (entry.voicePath != null) "[ویس]" else "" }
        "$time $kind: $text"
    }.ifBlank { "هنوز متنی ثبت نشده." }.take(12000)
    val system = "تو دستیار دفتر گفتگو هستی و فقط فارسی جواب می‌دهی. از عربی استفاده نکن. کاربر درباره گفتگوی ثبت‌شده با ${person.name} سؤال می‌پرسد. فقط از متن زیر جواب بده و اگر چیزی در آن نیست بگو در این گفتگو نیست.\n\n$transcript"
    val messages = listOf(Triple("system", system, null as String?)) + turns.map { Triple(it.role, it.body, it.image) }
    return askModel(app, messages)
}

private fun askModel(app: App, messages: List<Triple<String, String, String?>>): String {
    val model = app.db.activeModel() ?: error("مدل را در ادمین تنظیم کن")
    if (model.apiKey.isBlank() || messages.none { it.second.isNotBlank() || it.third != null }) error("مدل را در ادمین تنظیم کن")
    val conn = URL(model.endpoint.trimEnd('/') + "/chat/completions").openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Authorization", "Bearer ${model.apiKey}")
    conn.setRequestProperty("Content-Type", "application/json")
    if (model.endpoint.contains("openrouter.ai")) {
        conn.setRequestProperty("HTTP-Referer", "https://github.com/MohammadMahdi27127/Telegram")
        conn.setRequestProperty("X-Title", "Daftar Goftogu")
    }
    conn.doOutput = true
    conn.connectTimeout = 20000
    conn.readTimeout = 60000
    val messageArray = org.json.JSONArray()
    messages.forEach { (role, content, image) ->
        val item = org.json.JSONObject().put("role", role)
        val file = image?.let { File(it) }?.takeIf { it.exists() }
        if (file == null) item.put("content", content)
        else {
            val bytes = android.util.Base64.encodeToString(file.readBytes(), android.util.Base64.NO_WRAP)
            val parts = org.json.JSONArray()
            parts.put(org.json.JSONObject().put("type", "text").put("text", content.ifBlank { "این تصویر را در کنار گفتگو ببین." }))
            parts.put(org.json.JSONObject().put("type", "image_url").put("image_url", org.json.JSONObject().put("url", "data:image/jpeg;base64,$bytes")))
            item.put("content", parts)
        }
        messageArray.put(item)
    }
    val payload = org.json.JSONObject().put("model", model.modelId).put("messages", messageArray).toString()
    conn.outputStream.use { it.write(payload.toByteArray()) }
    val raw = (if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream)?.bufferedReader()?.readText().orEmpty()
    if (conn.responseCode !in 200..299) error(raw.ifBlank { "پاسخ مدل نرسید" })
    return org.json.JSONObject(raw).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        Text(label, color = Color(0xFF6B7280), fontSize = 12.sp)
        Text(value, fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun ChatBackdrop(user: User) {
    val color = user.chatColor?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    val file = user.chatBg?.let { File(it) }?.takeIf { it.exists() }
    val bitmap = file?.let { remember(it.absolutePath, it.length()) { decodeWallpaper(it) } }
    when {
        bitmap != null -> Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        color != null -> Box(Modifier.fillMaxSize().background(color))
        LocalDark.current -> GalaxyBackdrop()
        else -> Box(Modifier.fillMaxSize().background(Color(0xFFE6E6FA)))
    }
}

@Composable
private fun GalaxyBackdrop() {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Brush.verticalGradient(listOf(Color(0xFF070B1A), Color(0xFF1B1464), Color(0xFF311B92), Color(0xFF0B1026))))
        val random = Random(12)
        repeat(90) {
            drawCircle(
                Color.White.copy(alpha = 0.25f + random.nextFloat() * 0.6f),
                radius = 0.6f + random.nextFloat() * 1.8f,
                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height),
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun JournalBubble(entry: Entry, title: String, onPin: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    var viewer by remember { mutableStateOf(false) }
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val palette = LocalTg.current
    val note = entry.kind == "note"
    val chatTheme = LocalChatPalette.current
    val bubble = when {
        entry.kind == "voice" -> Color(0xFF1F8F4E)
        note -> if (palette.dark) Color(0xFF3B3524) else Color(0xFFFFF7D6)
        palette.dark -> palette.outgoing
        else -> chatTheme.outgoingBubble
    }
    val chatFont = LocalChatFont.current
    val bodySize = chatFont.sp
    val bodyLine = (chatFont * 1.5f).sp
    val ink = if (entry.kind == "voice") Color.White else if (palette.dark && !note) Color.White else palette.text
    val shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(open, enter = fadeIn(tween(160)) + scaleIn(tween(160), initialScale = 0.9f), exit = fadeOut(tween(120))) {
            Column(Modifier.padding(horizontal = 6.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF2B2B2F)).padding(vertical = 4.dp)) {
                MenuAction(Icons.Filled.PushPin, "سنجاق") { open = false; onPin() }
                MenuAction(Icons.Filled.Edit, "ویرایش") { open = false; onEdit() }
                MenuAction(Icons.Filled.ContentCopy, "کپی") {
                    clipboard.setText(androidx.compose.ui.text.AnnotatedString(entry.body))
                    open = false
                }
                MenuAction(Icons.Filled.Delete, "حذف") { open = false; onDelete() }
            }
        }
    Column(
        Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth(0.78f)
                .shadow(1.dp, shape)
                .clip(shape)
                .background(bubble)
                .then(
                    if (note) {
                        Modifier.drawBehind {
                            drawRoundRect(
                                color = Color(0xFFD4A373),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx()),
                            )
                        }
                    } else {
                        Modifier
                    },
                )
                .combinedClickable(onClick = {}, onLongClick = { open = !open })
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (entry.pinned) Text("سنجاق", color = Blue, fontSize = 11.sp)
            entry.memberName?.let { Text(it, color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            if (entry.kind == "voice" && entry.voicePath != null) {
                VoiceMessage(entry.voicePath)
            } else if (entry.kind == "file" && entry.mediaPath != null) {
                FileMessage(entry.body, entry.mediaPath, ink)
            } else if (entry.kind == "photo" && entry.mediaPath != null) {
                Box {
                    MediaPreview(entry.mediaPath, entry.kind) { viewer = true }
                Text(
                        persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(entry.occurredAt))),
                        color = Color.White,
                    fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x99000000)).padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                if (entry.body.isNotBlank() && entry.body != "عکس") Text(entry.body, color = ink, fontSize = bodySize, lineHeight = bodyLine, modifier = Modifier.padding(top = 4.dp))
            } else {
                entry.mediaPath?.let { path -> MediaPreview(path, entry.kind) }
                if (entry.kind != "photo" && entry.kind != "video") {
                    Text(
                        if (note) "یادداشت من" else mediumLabel(entry.channel),
                        color = if (note) Color(0xFF8D6E00) else Blue,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    )
                }
                if (entry.body.isNotBlank() && entry.body != "عکس") Text(entry.body, color = ink, fontSize = bodySize, lineHeight = bodyLine, modifier = Modifier.padding(top = 2.dp))
            }
            if (entry.kind != "photo") Text(persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(entry.occurredAt))), fontSize = 11.sp, color = if (entry.kind == "voice") Color.White.copy(alpha = 0.8f) else palette.subtext, modifier = Modifier.align(Alignment.End).padding(top = 2.dp))
        }
    }
    if (viewer && entry.mediaPath != null) {
        PhotoViewer(entry.mediaPath, title, photoWhen(entry.occurredAt)) { viewer = false }
    }
}

@Composable
private fun VoiceMessage(path: String) {
    var player by remember(path) { mutableStateOf<android.media.MediaPlayer?>(null) }
    var playing by remember(path) { mutableStateOf(false) }
    var progress by remember(path) { mutableStateOf(0f) }
    DisposableEffect(path) {
        onDispose {
            player?.release()
            player = null
        }
    }
    LaunchedEffect(playing, player) {
        while (playing) {
            val current = player
            if (current != null && current.duration > 0) progress = current.currentPosition / current.duration.toFloat()
            delay(80)
        }
    }
    val bars = listOf(8, 14, 6, 16, 10, 18, 7, 13, 9, 15, 6, 12, 17, 8, 11)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        val current = player
        if (playing && current != null) {
            current.pause()
            playing = false
            return@clickable
        }
        if (current != null) {
            current.start()
            playing = true
            return@clickable
        }
        runCatching {
            android.media.MediaPlayer().apply {
                setDataSource(path)
                setOnCompletionListener {
                    playing = false
                    progress = 1f
                    seekTo(0)
                }
                prepare()
                start()
                player = this
                playing = true
            }
        }
    }) {
        Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF39D353)), contentAlignment = Alignment.Center) {
            Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (playing) "توقف" else "پخش", tint = Color.White)
        }
        Row(Modifier.padding(start = 8.dp).weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            bars.forEachIndexed { index, bar ->
                val played = progress >= index / bars.size.toFloat()
                Box(Modifier.width(3.dp).height(bar.dp).clip(RoundedCornerShape(2.dp)).background(if (played) Color.White else Color.White.copy(alpha = 0.35f)))
            }
        }
        Text(voiceLength(path), color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun PhotoViewer(path: String, title: String, time: String, onClose: () -> Unit) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var menu by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }
    val fade = remember { Animatable(0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(200)) }
    LaunchedEffect(toast) {
        if (toast.isNotEmpty()) {
            delay(1600)
            toast = ""
        }
    }
    fun saved() {
        toast = if (saveImageToGallery(context, path)) "تصویر ذخیره شد" else "ذخیره نشد"
        menu = false
    }
    val storage = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) saved() else toast = "اجازه ذخیره داده نشد"
    }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black).graphicsLayer { alpha = fade.value }) {
            Box(
                Modifier.fillMaxSize().clickable(remember { MutableInteractionSource() }, null) {
                    if (scale <= 1.05f) onClose()
                },
                contentAlignment = Alignment.Center,
            ) {
                val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                if (bitmap != null) {
                    Image(
                        bitmap.asImageBitmap(),
                        null,
                        Modifier.fillMaxWidth().graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }.pointerInput(path) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val next = (scale * zoom).coerceIn(1f, 5f)
                                if (next <= 1.02f && kotlin.math.abs(pan.y) > kotlin.math.abs(pan.x)) {
                                    scale = 1f
                                    offset = Offset(0f, offset.y + pan.y)
                                    if (offset.y > 220f) onClose()
                                } else {
                                    scale = next
                                    offset = if (next <= 1.02f) Offset.Zero else offset + pan
                                }
                            }
                        }.pointerInput(path) {
                            detectTapGestures(onDoubleTap = {
                                if (scale > 1.05f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else scale = 2.5f
                            })
                        },
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().background(Color(0x99000000)).padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = Color.White) }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Text(time, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Box {
                    IconButton(onClick = { menu = !menu }) { Icon(Icons.Filled.MoreVert, "بیشتر", tint = Color.White) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, modifier = Modifier.background(Color(0xFF1C1C1E))) {
                        DropdownMenuItem(
                            text = { Text("ذخیره در گالری", color = Color.White) },
                            leadingIcon = { Icon(Icons.Filled.Download, null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                if (android.os.Build.VERSION.SDK_INT >= 29 || context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) saved()
                                else storage.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("اشتراک‌گذاری", color = Color.White) },
                            leadingIcon = { Icon(Icons.Filled.Share, null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menu = false
                                shareImage(context, path)
                            },
                        )
                    }
                }
            }
            if (toast.isNotEmpty()) {
                Text(
                    toast,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 36.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xCC2B2B2F)).padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

private fun photoWhen(at: Long): String {
    val cal = java.util.Calendar.getInstance()
    val day = cal.get(java.util.Calendar.DAY_OF_YEAR)
    val year = cal.get(java.util.Calendar.YEAR)
    cal.timeInMillis = at
    val clock = persianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(at)))
    val today = cal.get(java.util.Calendar.DAY_OF_YEAR) == day && cal.get(java.util.Calendar.YEAR) == year
    return if (today) "امروز ساعت $clock" else persianDigits(SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date(at)))
}

private fun saveImageToGallery(context: android.content.Context, path: String): Boolean {
    val file = File(path)
    if (!file.exists()) return false
    return runCatching {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "goftogu-${System.currentTimeMillis()}.jpg")
                put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/Goftogu")
                put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
            context.contentResolver.openOutputStream(uri)?.use { out -> file.inputStream().use { it.copyTo(out) } } ?: return false
            values.clear()
            values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
        } else {
            val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
            if (!dir.exists()) dir.mkdirs()
            val dest = File(dir, "goftogu-${System.currentTimeMillis()}.jpg")
            file.copyTo(dest, overwrite = true)
            android.media.MediaScannerConnection.scanFile(context, arrayOf(dest.absolutePath), arrayOf("image/jpeg"), null)
        }
        true
    }.getOrDefault(false)
}

private fun shareImage(context: android.content.Context, path: String) {
    val file = File(path)
    if (!file.exists()) return
    val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".files", file)
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(android.content.Intent.EXTRA_STREAM, uri)
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(android.content.Intent.createChooser(intent, "اشتراک‌گذاری"))
}

@Composable
private fun MediaPreview(path: String, kind: String, onPhoto: () -> Unit = {}) {
    val context = LocalContext.current
    val file = File(path)
    if (!file.exists()) return
    if (kind == "video" || path.endsWith(".mp4")) {
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0x22000000)).clickable {
            val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".files", file)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).setDataAndType(uri, "video/mp4").addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            runCatching { context.startActivity(intent) }
        }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PlayArrow, null, tint = Blue)
            Text("پخش فیلم", color = Blue, modifier = Modifier.padding(start = 6.dp))
        }
    } else {
        val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
        if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).clickable { onPhoto() }, contentScale = ContentScale.Crop)
    }
}

@Composable
fun NoticesScreen(app: App, userId: String, onBack: () -> Unit) {
    val items = remember { app.db.notices(userId).also { app.db.markNoticesRead(userId) } }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت") }
            Text("اطلاعیه‌ها", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        items(items) { notice ->
            Column(Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(20.dp)).background(if (notice.read) Color(0xFFF7F7F8) else Pill).padding(16.dp)) {
                Text(notice.title, color = Color(0xFF111827), fontWeight = FontWeight.Bold)
                Text(notice.body, color = Color(0xFF374151), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

private data class PresetWallpaper(val id: String, val name: String, val url: String)

private fun commonsImage(file: String): String {
    val name = java.net.URLEncoder.encode(file, "UTF-8").replace("+", "%20")
    return "https://commons.wikimedia.org/wiki/Special:FilePath/$name?width=800"
}

private val presetWallpapers = listOf(
    PresetWallpaper("galaxy", "کهکشان", commonsImage("NGC_4414_(NASA-med).jpg")),
    PresetWallpaper("mountain", "کوهستان", commonsImage("Matterhorn_from_Domhütte_-_2.jpg")),
    PresetWallpaper("lake", "دریاچه", commonsImage("Moraine_Lake_17092005.jpg")),
    PresetWallpaper("forest", "جنگل", commonsImage("Black_Forest.jpg")),
    PresetWallpaper("aurora", "شفق قطبی", commonsImage("Aurora_borealis.jpg")),
    PresetWallpaper("desert", "کویر", commonsImage("Sahara_Desert.jpg")),
    PresetWallpaper("sunset", "غروب", commonsImage("Sunset_over_the_sea.jpg")),
    PresetWallpaper("ocean", "اقیانوس", commonsImage("Ocean.jpg")),
    PresetWallpaper("starry-night", "شب پرستاره", commonsImage("Milky_Way_Night_Sky_Black_Rock_Desert_Nevada.jpg")),
    PresetWallpaper("autumn", "پاییز", commonsImage("Autumn_landscape.jpg")),
)

private suspend fun cacheWallpaper(context: android.content.Context, item: PresetWallpaper): File = kotlinx.coroutines.withContext(Dispatchers.IO) {
    val dest = File(context.filesDir, "preset-wiki-${item.id}.jpg")
    if (decodeWallpaper(dest) != null) return@withContext dest
    if (dest.exists()) dest.delete()
    val conn = URL(item.url).openConnection() as HttpURLConnection
    conn.instanceFollowRedirects = true
    conn.connectTimeout = 15000
    conn.readTimeout = 20000
    conn.setRequestProperty("User-Agent", "DaftarGoftogu/2.0 (Android wallpaper)")
    conn.setRequestProperty("Accept", "image/jpeg,image/*,*/*")
    if (conn.responseCode !in 200..299) {
        conn.disconnect()
        error("عکس ${item.name} دانلود نشد")
    }
    conn.inputStream.use { input -> dest.outputStream().use { input.copyTo(it) } }
    conn.disconnect()
    if (decodeWallpaper(dest) == null) {
        dest.delete()
        error("عکس ${item.name} خوانده نشد")
    }
    dest
}

private fun decodeSampledBitmap(context: android.content.Context, uri: Uri, maxEdge: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    var sample = 1
    while (bounds.outWidth / sample > maxEdge || bounds.outHeight / sample > maxEdge) sample *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    return context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
}

private fun decodeWallpaper(file: File): Bitmap? {
    if (!file.exists() || file.length() < 1000) return null
    val probe = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, probe)
    if (probe.outWidth <= 0 || probe.outHeight <= 0) return null
    val sample = BitmapFactory.Options().apply {
        inSampleSize = maxOf(1, maxOf(probe.outWidth, probe.outHeight) / 1200)
    }
    return BitmapFactory.decodeFile(file.absolutePath, sample)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(app: App, userId: String, theme: ThemeController, chatFont: ChatFontController, chatPalette: ChatPaletteController, onBack: () -> Unit, pay: () -> Unit, pricing: () -> Unit, onAdmin: () -> Unit, logout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf(app.db.user(userId)) }
    var editProfile by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.name ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var editPassword by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var nextPassword by remember { mutableStateOf("") }
    var wallpapersOpen by remember { mutableStateOf(false) }
    var crop by remember { mutableStateOf<Bitmap?>(null) }
    var message by remember { mutableStateOf("") }
    var chatSettingsOpen by remember { mutableStateOf(false) }
    var walletOpen by remember { mutableStateOf(false) }
    var plansOpen by remember { mutableStateOf(false) }
    var billingTab by remember { mutableIntStateOf(0) }
    var donation by remember { mutableStateOf(false) }
    val wallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val dest = File(context.filesDir, "chat-bg-$userId.jpg")
        context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } }
        app.db.setChatStyle(userId, dest.absolutePath, null)
        user = app.db.user(userId)
        wallpapersOpen = false
    }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.openInputStream(uri)?.use { crop = BitmapFactory.decodeStream(it) }
    }
    val current = user ?: return
    val dark = LocalDark.current
    val page = if (dark) Color(0xFF0F172A) else Color(0xFFE6E6FA)
    val card = if (dark) Color(0xFF1E293B) else Color.White
    val ink = if (dark) Color.White else Color(0xFF111827)
    val chatTheme = LocalChatPalette.current
    val paid = current.plan == "basic" || current.plan == "advanced"
    val daysLeft = current.planExpiresAt?.let { ((it - System.currentTimeMillis()) / 86_400_000L).coerceAtLeast(0) } ?: 0
    val pulse = rememberInfiniteTransition(label = "cam")
    val camScale by pulse.animateFloat(0.92f, 1.08f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "cam-scale")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(page), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth()) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) } }
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(24.dp)).background(card).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    val avatarFile = current.avatar?.let { File(it) }?.takeIf { it.exists() }
                    Box(Modifier.size(80.dp).clip(CircleShape).background(Blue).clickable { avatarPicker.launch("image/*") }, contentAlignment = Alignment.Center) {
                        if (avatarFile != null) {
                            val bitmap = remember(avatarFile.absolutePath) { BitmapFactory.decodeFile(avatarFile.absolutePath) }
                            if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            else Text(current.name.take(1), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        } else Text(current.name.take(1), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        Modifier.graphicsLayer { scaleX = camScale; scaleY = camScale }.size(26.dp).clip(CircleShape).background(Blue).border(2.dp, card, CircleShape).clickable { avatarPicker.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.CameraAlt, "تغییر عکس", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(current.name, color = ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(current.phone, color = Color(0xFF707579), fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    Text("@" + (current.username ?: "—"), color = chatTheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
                }
                IconButton(onClick = { name = current.name; username = current.username ?: ""; editProfile = true }) { Icon(Icons.Filled.Edit, "ویرایش پروفایل", tint = Blue) }
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusCapsule(Modifier.weight(1f), Icons.Filled.Wallet, Color(0xFF10B981), "موجودی", "${formatToman(current.balance)} تومان", themeContainer(), dark)
            StatusCapsule(Modifier.weight(1f), Icons.Filled.Star, Blue, "پلن فعلی", planDisplay(current.plan), themeContainer(), dark)
        }
        if (message.isNotEmpty()) Text(message, color = chatTheme.primary, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        Column(Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(20.dp)).background(card)) {
            if (isAdmin(current.phone)) {
                SettingRow("ادمین", "داشبورد", Icons.Filled.AdminPanelSettings, Color(0xFFE3F2FD), Blue, onClick = onAdmin)
                RowDivider(dark)
            }
            SettingRow("تنظیمات گفتگو", "پس‌زمینه، اندازه قلم و ترکیب رنگ", Icons.Filled.Chat, Color(0xFFE0F7FA), Color(0xFF00ACC1)) { chatSettingsOpen = !chatSettingsOpen }
            AnimatedVisibility(chatSettingsOpen) { ChatSettingsSection(dark, ink, chatFont, chatPalette) { wallpapersOpen = true } }
            RowDivider(dark)
            SettingRow("کیف پول", "${formatToman(current.balance)} تومان", Icons.Filled.Wallet, Color(0xFFE8F5E9), Color(0xFF2E7D32)) { if (paid) walletOpen = true else plansOpen = true }
            RowDivider(dark)
            SettingRow("رمز عبور", "", Icons.Filled.Lock, Color(0xFFE3F2FD), Color(0xFF1565C0)) { editPassword = true }
            RowDivider(dark)
            SettingRow("حمایت مالی", "حمایت از توسعه اپلیکیشن", Icons.Filled.Favorite, Color(0xFFFFE4E6), Color(0xFFF43F5E)) {
                // با اتصال به اینترنت، صفحهٔ حمایت روی سایت باز می‌شود؛ در حالت آفلاین پیام راهنما نمایش داده می‌شود.
                if (!isDeviceOnline(context) || !startView(context, SUPPORT_DONATE_URL)) donation = true
            }
            RowDivider(dark)
            var themeMenu by remember { mutableStateOf(false) }
            val themeLabel = when (theme.mode) {
                "light" -> "روشن"
                "dark" -> "تاریک"
                else -> "همگام با سیستم"
            }
            Box(Modifier.fillMaxWidth()) {
                SettingRow("ظاهر برنامه", themeLabel, Icons.Filled.DarkMode, Color(0xFFFFF8E1), Color(0xFFF9A825)) { themeMenu = true }
                DropdownMenu(expanded = themeMenu, onDismissRequest = { themeMenu = false }, modifier = Modifier.background(card)) {
                    listOf("light" to "روشن", "dark" to "تاریک", "system" to "همگام با سیستم").forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label, color = ink, fontWeight = if (theme.mode == id) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                theme.set(id)
                                themeMenu = false
                            },
                            trailingIcon = {
                                if (theme.mode == id) Icon(Icons.Filled.Check, null, tint = Blue)
                            },
                        )
                    }
                }
            }
            RowDivider(dark)
            SettingRow("خروج از حساب", "", Icons.Filled.Logout, Color(0xFFFFEBEE), Color(0xFFEF4444), danger = true, onClick = logout)
        }
        Text("نسخه: V1", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 16.dp, bottom = 24.dp))
    }
    if (editProfile) {
        AlertDialog(
            onDismissRequest = { editProfile = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = themeDialogContainer(),
            title = { Text("ویرایش پروفایل") },
            text = {
                Column {
                    OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("نام نمایشی") })
                    OutlinedTextField(username, { username = it }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("نام کاربری (بدون @)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        app.db.rename(userId, name)
                        app.db.setUsername(userId, username)
                        user = app.db.user(userId)
                        message = "پروفایل ذخیره شد"
                        editProfile = false
                    } catch (e: Exception) { message = e.message ?: "خطا" }
                }) { Text("ذخیره") }
            },
            dismissButton = { TextButton(onClick = { editProfile = false }) { Text("بستن") } },
        )
    }
    if (donation) PrettyAlert("حمایت مالی", "از حمایت شما سپاسگزاریم. برای هماهنگی حمایت با پشتیبانی در ارتباط باشید.", onConfirm = { donation = false })
    if (editPassword) {
        AlertDialog(
            onDismissRequest = { editPassword = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = themeDialogContainer(),
            title = { Text("رمز عبور") },
            text = {
                Column {
                    OutlinedTextField(currentPassword, { currentPassword = it }, Modifier.fillMaxWidth(), label = { Text("رمز فعلی") }, visualTransformation = PasswordVisualTransformation())
                    OutlinedTextField(nextPassword, { nextPassword = it }, Modifier.fillMaxWidth(), label = { Text("رمز جدید") }, visualTransformation = PasswordVisualTransformation())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        app.db.login(current.phone, currentPassword)
                        app.db.reset(current.phone, nextPassword)
                        currentPassword = ""
                        nextPassword = ""
                        message = "رمز عوض شد"
                        editPassword = false
                    } catch (e: Exception) { message = e.message ?: "خطا" }
                }) { Text("تغییر") }
            },
            dismissButton = { TextButton(onClick = { editPassword = false }) { Text("بستن") } },
        )
    }
    crop?.let { bitmap ->
        CropDialog(bitmap, onCancel = { crop = null }) { saved ->
            val dest = File(context.filesDir, "avatar-$userId.jpg")
            dest.outputStream().use { saved.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            saved.recycle()
            app.db.setAvatar(userId, dest.absolutePath)
            user = app.db.user(userId)
            crop = null
            message = "عکس ذخیره شد"
        }
    }
    if (wallpapersOpen) {
        val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { wallpapersOpen = false }, sheetState = sheet, containerColor = themeDialogContainer()) {
            val darkSheet = LocalDark.current
            Column(Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                Text("انتخاب پس‌زمینه گفتگو", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("پیشنهادی", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                presetWallpapers.chunked(2).forEach { pair ->
                    Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { item ->
                            var thumb by remember(item.id) { mutableStateOf<Bitmap?>(null) }
                            var status by remember(item.id) { mutableStateOf("در حال دریافت") }
                            var attempt by remember(item.id) { mutableIntStateOf(0) }
                            LaunchedEffect(item.id, attempt) {
                                status = "در حال دریافت"
                                val loaded = runCatching { decodeWallpaper(cacheWallpaper(context, item)) }
                                thumb = loaded.getOrNull()
                                status = loaded.exceptionOrNull()?.message ?: if (thumb == null) "دانلود نشد" else ""
                            }
                            val selected = current.chatBg?.contains("preset-wiki-${item.id}.jpg") == true
                            val press = remember { MutableInteractionSource() }
                            val down by press.collectIsPressedAsState()
                            val cardScale by animateFloatAsState(if (down) 0.95f else 1f, tween(150), label = "wall")
                            val line = if (selected) Blue else if (darkSheet) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(3f / 4f)
                                    .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
                                    .shadow(2.dp, RoundedCornerShape(16.dp))
                                    .border(if (selected) 3.dp else 1.dp, line, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (darkSheet) Color(0xFF334155) else Color(0xFFE5E7EB))
                                    .clickable(press, null) {
                                        if (thumb != null) {
                                            scope.launch {
                                                val file = runCatching { cacheWallpaper(context, item) }.getOrNull() ?: return@launch
                                                if (decodeWallpaper(file) == null) return@launch
                                                app.db.setChatStyle(userId, file.absolutePath, null)
                                                user = app.db.user(userId)
                                                wallpapersOpen = false
                                            }
                                        } else if (thumb == null && status != "در حال دریافت") attempt += 1
                                    },
                            ) {
                                if (thumb != null) Image(thumb!!.asImageBitmap(), item.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                else Text(if (status == "در حال دریافت") status else "دوباره: $status", color = if (darkSheet) Color.White else Color(0xFF111827), fontSize = 11.sp, modifier = Modifier.align(Alignment.Center).padding(8.dp), textAlign = TextAlign.Center)
                                Text(item.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x73000000)).padding(horizontal = 10.dp, vertical = 4.dp))
                                if (selected) {
                                    Box(Modifier.align(Alignment.TopEnd).padding(10.dp).size(22.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                TextButton(onClick = {
                    app.db.setChatStyle(userId, null, null)
                    user = app.db.user(userId)
                    wallpapersOpen = false
                }) { Text("پس‌زمینه کهکشانی") }
            }
            Column(
                Modifier.fillMaxWidth().background(if (darkSheet) Color(0xCC17212B) else Color(0xCCFFFFFF)).border(1.dp, if (darkSheet) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)).padding(16.dp),
            ) {
                val uploadPress = remember { MutableInteractionSource() }
                val uploadDown by uploadPress.collectIsPressedAsState()
                val uploadScale by animateFloatAsState(if (uploadDown) 0.98f else 1f, tween(150), label = "upload-wall")
                Row(
                    Modifier
                        .graphicsLayer { scaleX = uploadScale; scaleY = uploadScale }
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Blue)
                        .clickable(uploadPress, null) { wallpaperPicker.launch("image/*") }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Image, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("آپلود تصویر دلخواه", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
                }
            }
            }
        }
    }
    if (walletOpen) {
        val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { walletOpen = false }, sheetState = sheet, containerColor = themeDialogContainer()) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("کیف پول", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ink, modifier = Modifier.weight(1f))
                    IconButton(onClick = { user = app.db.user(userId) }) { Icon(Icons.Filled.Refresh, "به‌روزرسانی", tint = Blue) }
                }
                Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusCapsule(Modifier.weight(1f), Icons.Filled.Star, Blue, "پلن فعلی", planDisplay(current.plan), card, dark)
                    StatusCapsule(Modifier.weight(1f), Icons.Filled.Schedule, Blue, "مدت باقی‌مانده", "${persianDigits(daysLeft.toString())} روز", card, dark)
                }
                Button(
                    onClick = { walletOpen = false; pay() },
                    Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor),
                ) { Text("پرداخت‌ها و گردش حساب") }
            }
        }
    }
    if (plansOpen) {
        val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { plansOpen = false }, sheetState = sheet, containerColor = themeDialogContainer()) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
                Text("خرید اشتراک", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ink)
                Text("پلن مناسب خود را انتخاب کنید.", color = Color(0xFF6B7280), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                Row(Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if (dark) Color(0xFF243242) else Color(0xFFF1F2F6)).padding(4.dp)) {
                    listOf("ماهانه", "فصلی").forEachIndexed { i, label ->
                        val on = billingTab == i
                        Text(
                            label,
                            color = if (on) Color.White else if (dark) Color(0xFFD1D5DB) else Color(0xFF707579),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(if (on) Blue else Color.Transparent).clickable { billingTab = i }.padding(vertical = 8.dp),
                        )
                    }
                }
                val months = if (billingTab == 1) 3 else 1
                PlanRow("رایگان", "۰ تومان", listOf("۵ ثبت در روز", "مکالمه و یادداشت", "بدون هزینه"), false, dark, months) { plansOpen = false; pay() }
                PlanRow("پلاس", "${formatToman(500_000 * months)} تومان", listOf("۱۰۰ ثبت در روز", "دانلود صحبت‌ها", "پشتیبانی سریع"), true, dark, months) { plansOpen = false; pay() }
                PlanRow("هوشمند", "${formatToman(1_000_000 * months)} تومان", listOf("همه امکانات پلاس", "خلاصه هوشمند با هوش مصنوعی", "سقف نامحدود ثبت"), false, dark, months) { plansOpen = false; pay() }
                Text("پس از واریز و ثبت رسید، مدیر پرداخت را بررسی و اشتراک را فعال می‌کند.", color = Color(0xFF6B7280), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
                TextButton(onClick = { plansOpen = false; pricing() }, modifier = Modifier.fillMaxWidth()) { Text("مشاهده صفحه قیمت‌ها", color = Blue) }
            }
        }
    }
}

@Composable
private fun RowDivider(dark: Boolean) {
    Box(Modifier.fillMaxWidth().padding(start = 64.dp).height(1.dp).background(if (dark) Color(0xFF243242) else Color(0xFFE5E7EB)))
}

@Composable
private fun StatusCapsule(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, label: String, value: String, card: Color, dark: Boolean) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(card).border(1.dp, if (dark) Color(0xFF243242) else Color(0xFFE5E7EB), RoundedCornerShape(16.dp)).padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(30.dp).clip(CircleShape).background(tint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Text(label, color = if (dark) Color(0xFF9CA3AF) else Color(0xFF707579), fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
        }
        Text(value, color = if (dark) Color.White else Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ChatSettingsSection(dark: Boolean, ink: Color, chatFont: ChatFontController, chatPalette: ChatPaletteController, onWallpaper: () -> Unit) {
    val chatTheme = LocalChatPalette.current
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (dark) Color(0xFF243242) else Color(0xFFF4F7FB)).clickable(onClick = onWallpaper).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Image, null, tint = chatTheme.primary, modifier = Modifier.size(18.dp))
            Text("پس‌زمینه چت", color = ink, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp).weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color(0xFFB0B8C1), modifier = Modifier.size(18.dp))
        }
        Text("اندازه قلم پیام‌ها", color = ink, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 14.dp))
        Text(
            "این یک پیام آزمایشی برای مشاهده اندازه قلم است.",
            color = if (dark) Color.White else Color(0xFF111827),
            fontSize = chatFont.size.sp,
            lineHeight = (chatFont.size * 1.5f).sp,
            modifier = Modifier.padding(top = 8.dp).clip(RoundedCornerShape(16.dp)).background(if (dark) chatTheme.accent.copy(alpha = 0.4f) else chatTheme.outgoingBubble).padding(horizontal = 12.dp, vertical = 8.dp),
        )
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("A", color = ink, fontSize = 12.sp)
            Slider(
                chatFont.size.toFloat(),
                { chatFont.set(it.roundToInt()) },
                Modifier.weight(1f).padding(horizontal = 8.dp),
                valueRange = 12f..22f,
                steps = 9,
                colors = SliderDefaults.colors(thumbColor = chatTheme.primary, activeTrackColor = chatTheme.primary, inactiveTrackColor = if (dark) Color(0xFF334155) else Color(0xFFE5E7EB)),
            )
            Text("A", color = ink, fontSize = 22.sp)
        }
        Text("ترکیب رنگ", color = ink, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 14.dp))
        ColorPaletteSettingsView(dark)
    }
}

/** انتخاب‌گر پالت دوگانه (کانتینر + اکشن)؛ تغییر بلافاصله در کل برنامه اعمال و ذخیره می‌شود. */
@Composable
private fun ColorPaletteSettingsView(dark: Boolean) {
    val active = LocalAppTheme.current
    val rows = (APP_THEMES.size + 1) / 2
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().height((rows * 104).dp).padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = false,
    ) {
        items(APP_THEMES) { t ->
            val on = t.id == active.id
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (dark) Color(0xFF1E293B) else Color.White)
                    .border(
                        BorderStroke(if (on) 2.dp else 1.dp, if (on) t.buttonColor else if (dark) Color(0xFF243242) else Color(0xFFE5E7EB)),
                        RoundedCornerShape(16.dp),
                    )
                    .clickable { updateAppTheme(t) }
                    .padding(10.dp),
            ) {
                Box(
                    Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(12.dp)).background(t.containerColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.size(24.dp).clip(CircleShape).background(t.buttonColor))
                    if (on) {
                        Box(
                            Modifier.align(Alignment.TopEnd).padding(5.dp).size(18.dp).clip(CircleShape).background(t.buttonColor),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Filled.Check, null, tint = t.onButtonColor, modifier = Modifier.size(12.dp)) }
                    }
                }
                Text(
                    t.name,
                    color = if (dark) Color(0xFFE5E7EB) else Color(0xFF1E293B),
                    fontSize = 11.sp,
                    fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun PlanRow(title: String, price: String, features: List<String>, highlight: Boolean, dark: Boolean, months: Int, onBuy: () -> Unit) {
    val cardBg = if (highlight) (if (dark) Color(0xFF14324A) else Color(0xFFEFF7FD)) else (if (dark) Color(0xFF1E293B) else Color(0xFFF7F9FB))
    val line = if (highlight) Blue else if (dark) Color(0xFF243242) else Color(0xFFE5E7EB)
    Column(Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(20.dp)).background(cardBg).border(if (highlight) 2.dp else 1.dp, line, RoundedCornerShape(20.dp)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (dark) Color.White else Color(0xFF111827))
            if (highlight) {
                Box(Modifier.padding(start = 8.dp).clip(CircleShape).background(Blue).padding(5.dp)) { Icon(Icons.Filled.Star, null, tint = Color.White, modifier = Modifier.size(14.dp)) }
            }
            Spacer(Modifier.weight(1f))
            Text(price, color = Blue, fontWeight = FontWeight.Bold)
        }
        Text(if (months > 1) "$months ماهه" else "ماهانه", color = Color(0xFF6B7280), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        features.forEach { f ->
            Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                Text(f, color = if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563), fontSize = 13.sp, modifier = Modifier.padding(start = 6.dp))
            }
        }
        Button(
            onClick = onBuy,
            Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor),
        ) { Text("خرید اشتراک") }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tile: Color, tint: Color, danger: Boolean = false, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        IconTile(icon, tile, tint)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (danger) Color(0xFFE53935) else if (LocalDark.current) Color.White else Color(0xFF111827))
            if (subtitle.isNotEmpty()) Text(subtitle, color = if (LocalDark.current) Color(0xFFE5E7EB) else Color(0xFF7F91A4), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color(0xFFB0B8C1))
    }
}

@Composable
private fun IconTile(icon: androidx.compose.ui.graphics.vector.ImageVector, tile: Color, tint: Color) {
    Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(tile), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun PhotoSendPreview(bitmap: Bitmap, onCancel: () -> Unit, onSend: (Bitmap) -> Unit) {
    var rotation by remember { mutableStateOf(0f) }
    var cropping by remember { mutableStateOf(false) }
    var aspect by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var view by remember { mutableStateOf(1) }
    val frameRatio = when (aspect) {
        1f -> 1f
        4f / 3f -> 4f / 3f
        16f / 9f -> 16f / 9f
        else -> if (rotation % 180f == 0f) bitmap.width / bitmap.height.toFloat() else bitmap.height / bitmap.width.toFloat()
    }
    Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Column(Modifier.fillMaxSize().background(Color(0xE6000000)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCancel) { Text("انصراف", color = Color.White) }
                Text("پیش‌نمایش تصویر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                TextButton(onClick = { rotation = 0f; cropping = false; scale = 1f; offset = Offset.Zero; aspect = 0f }) { Text("بازنشانی", color = Color.White) }
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                if (!cropping) {
                    Image(
                        bitmap.asImageBitmap(),
                        null,
                        Modifier.fillMaxWidth().heightIn(max = 520.dp).graphicsLayer { rotationZ = rotation },
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Box(
                        Modifier.fillMaxWidth(0.92f).aspectRatio(frameRatio.coerceIn(0.4f, 2.2f)).clip(RoundedCornerShape(12.dp)).border(2.dp, Color.White, RoundedCornerShape(12.dp)).background(Color.Black).onSizeChanged { view = it.width }.pointerInput(bitmap) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 4f)
                                offset += pan
                            }
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize().graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = rotation
                        }, contentScale = ContentScale.Crop)
                    }
                }
            }
            if (cropping) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0f to "آزاد", 1f to "۱:۱", 4f / 3f to "۴:۳", 16f / 9f to "۱۶:۹").forEach { (value, label) ->
                        val on = aspect == value
                        Text(label, color = if (on) Color.White else Color(0xFFD1D5DB), fontSize = 12.sp, modifier = Modifier.clip(CircleShape).background(if (on) Blue else Color(0xFF243042)).clickable { aspect = value }.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { rotation = (rotation + 90f) % 360f }) {
                    Icon(Icons.AutoMirrored.Filled.RotateRight, null, tint = Color.White)
                    Text("چرخش", color = Color.White)
                }
                TextButton(onClick = { cropping = !cropping }) {
                    Icon(Icons.Filled.Crop, null, tint = if (cropping) Blue else Color.White)
                    Text(if (cropping) "برش روشن" else "برش تصویر", color = if (cropping) Blue else Color.White)
                }
                Button(onClick = { onSend(exportSendPhoto(bitmap, rotation, cropping, aspect, scale, offset, view)) }, shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor)) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                    Text("ارسال", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

private fun exportSendPhoto(src: Bitmap, rotation: Float, cropping: Boolean, aspect: Float, scale: Float, offset: Offset, view: Int): Bitmap {
    val turn = android.graphics.Matrix().apply { postRotate(rotation) }
    val rotated = if (rotation % 360f == 0f) src else Bitmap.createBitmap(src, 0, 0, src.width, src.height, turn, true)
    if (!cropping) return rotated
    val ratio = if (aspect <= 0f) rotated.width / rotated.height.toFloat() else aspect
    val outW = 1080
    val outH = (outW / ratio).toInt().coerceAtLeast(1)
    val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    val base = outW / view.coerceAtLeast(1).toFloat()
    val fit = maxOf(view / rotated.width.toFloat(), (view / ratio) / rotated.height.toFloat()) * scale
    val draw = android.graphics.Matrix()
    draw.postScale(fit * base, fit * base)
    draw.postTranslate((outW - rotated.width * fit * base) / 2f + offset.x * base, (outH - rotated.height * fit * base) / 2f + offset.y * base)
    canvas.drawBitmap(rotated, draw, android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG))
    if (rotated !== src) rotated.recycle()
    return out
}

@Composable
private fun CropDialog(bitmap: Bitmap, onCancel: () -> Unit, onSave: (Bitmap) -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var view by remember { mutableStateOf(1) }
    var rotation by remember { mutableStateOf(0f) }
    Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Column(Modifier.fillMaxSize().background(Color(0xD9000000)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCancel) { Text("انصراف", color = Color.White) }
                Text("تنظیم تصویر پروفایل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(Modifier.width(64.dp))
            }
            Box(
                Modifier.padding(top = 24.dp).size(280.dp).align(Alignment.CenterHorizontally).clip(CircleShape).background(Color.Black).onSizeChanged { view = it.width }.pointerInput(bitmap) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 3f)
                        offset += pan
                    }
                },
                contentAlignment = Alignment.Center,
            ) {
                Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize().graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                    rotationZ = rotation
                }, contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { rotation = (rotation + 90f) % 360f }) {
                    Icon(Icons.AutoMirrored.Filled.RotateRight, null, tint = Color.White)
                    Text("چرخش", color = Color.White)
                }
                Text("بزرگ‌نمایی", color = Color.White, fontSize = 13.sp)
            }
            Slider(scale, { scale = it }, valueRange = 1f..3f, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { onSave(cropBitmap(bitmap, scale, offset, view, rotation)) },
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = LocalAppTheme.current.onButtonColor),
            ) {
                Icon(Icons.Filled.Check, null, tint = Color.White)
                Text("تایید و برش", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

private fun cropBitmap(src: Bitmap, scale: Float, offset: Offset, view: Int, rotation: Float): Bitmap {
    val size = 512
    val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    val path = android.graphics.Path().apply { addCircle(size / 2f, size / 2f, size / 2f, android.graphics.Path.Direction.CW) }
    canvas.clipPath(path)
    val base = size / view.coerceAtLeast(1).toFloat()
    val matrix = android.graphics.Matrix()
    val fit = maxOf(view / src.width.toFloat(), view / src.height.toFloat()) * scale
    matrix.postRotate(rotation, src.width / 2f, src.height / 2f)
    matrix.postScale(fit * base, fit * base)
    matrix.postTranslate((size - src.width * fit * base) / 2f + offset.x * base, (size - src.height * fit * base) / 2f + offset.y * base)
    canvas.drawBitmap(src, matrix, android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG))
    return out
}

@Composable
fun PayScreen(app: App, userId: String, back: () -> Unit) {
    val context = LocalContext.current
    val dark = LocalDark.current
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    var user by remember { mutableStateOf(app.db.user(userId)) }
    var amount by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf<String?>(null) }
    var deposit by remember { mutableStateOf("") }
    var receipt by remember { mutableStateOf<String?>(null) }
    var copied by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val page = if (dark) Color(0xFF0F172A) else Color(0xFFE6E6FA)
    val card = if (dark) Color(0xFF1E293B) else Color.White
    val ink = if (dark) Color.White else Color(0xFF111827)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val dest = File(context.filesDir, "receipt-$userId-${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } }
        receipt = dest.absolutePath
    }
    val current = user ?: return
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(page).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) }
            Text("افزایش موجودی و پرداخت", color = ink, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.Wallet, null, tint = Blue)
        }
        Text("موجودی فعلی: ${current.balance} تومان", color = ink, modifier = Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(card).padding(16.dp))
        val cardNumber = "6219861465646171"
        Column(Modifier.padding(top = 14.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(24.dp))) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("بانک سامان", color = Color(0xFF0B3A82), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Saman Bank", color = Color(0xFF6B7280), fontSize = 11.sp)
                }
                Canvas(Modifier.size(42.dp, 32.dp)) {
                    drawRoundRect(Color(0xFFD4AF37), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                    drawRoundRect(Color(0xFFB8860B), topLeft = Offset(8f, 8f), size = androidx.compose.ui.geometry.Size(size.width - 16f, 6f))
                }
            }
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(persianDigits("6219 - 8614 - 6564 - 6171"), color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f).clickable {
                    clipboard.setText(androidx.compose.ui.text.AnnotatedString(cardNumber))
                    copied = true
                })
                Icon(if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy, "کپی", tint = Blue, modifier = Modifier.clickable {
                    clipboard.setText(androidx.compose.ui.text.AnnotatedString(cardNumber))
                    copied = true
                })
            }
            Box(Modifier.padding(top = 16.dp).fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Blue).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Text("محمد مهدی آبرودی", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (copied) {
            Text("شماره کارت کپی شد", color = Blue, modifier = Modifier.padding(top = 8.dp))
        }
        Text("لطفاً پس از واریز، عکس رسید یا شناسه پیگیری را در فرم زیر ثبت کنید.", color = Color(0xFF6B7280), modifier = Modifier.padding(top = 8.dp))
        Text("انتخاب مبلغ یا پلن", color = ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("basic" to "پلن پایه", "advanced" to "پلن پیشرفته", "50000" to "۵۰,۰۰۰", "100000" to "۱۰۰,۰۰۰").forEach { (id, label) ->
                val on = plan == id || amount == id
                Text(label, color = if (on) Color.White else ink, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) Blue else card).clickable {
                    if (id == "basic" || id == "advanced") { plan = id; amount = if (id == "advanced") "1000000" else "500000" }
                    else { plan = null; amount = id }
                }.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
        OutlinedTextField(amount, { amount = it.filter { ch -> ch.isDigit() }; plan = null }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("مبلغ دلخواه به تومان") })
        OutlinedTextField(deposit, { deposit = it }, Modifier.fillMaxWidth(), label = { Text("شناسه واریز / کد پیگیری") })
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).border(1.dp, Blue, RoundedCornerShape(20.dp)).clickable { picker.launch("image/*") }.padding(18.dp), contentAlignment = Alignment.Center) {
            if (receipt != null) {
                val preview = BitmapFactory.decodeFile(receipt)
                if (preview != null) Image(preview.asImageBitmap(), null, Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            } else Text("عکس رسید واریز", color = Blue)
        }
        Button(onClick = {
            try {
                app.db.submitSlip(userId, "topup", null, amount.toIntOrNull() ?: 0, receipt, deposit.ifBlank { null })
                message = "فیش برای شارژ فرستاده شد."
                error = ""
            } catch (e: Exception) { error = e.message ?: "خطا" }
        }, Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(16.dp)) { Text("ارسال فیش و شارژ حساب") }
        OutlinedButton(onClick = {
            val chosen = plan ?: "basic"
            val price = if (chosen == "advanced") 1_000_000 else 500_000
            if (current.balance < price) { error = "اول کیف پول را به اندازه پلن شارژ کن"; return@OutlinedButton }
            try {
                message = "پلن ${app.db.buyPlan(current, chosen)} از موجودی فعال شد"
                error = ""
                user = app.db.user(userId)
            } catch (e: Exception) { error = e.message ?: "خطا" }
        }, Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(16.dp)) { Text("خرید مستقیم از موجودی فعلی") }
    }
    val note = message.ifBlank { error }
    if (note.isNotEmpty()) {
        PrettyAlert(if (error.isNotEmpty()) "توجه" else "اعلان", note, onConfirm = { message = ""; error = "" })
    }
}

private fun planDisplay(plan: String) = when (plan) {
    "basic" -> "پلاس"
    "advanced" -> "هوشمند"
    "guest" -> "مهمان"
    else -> "رایگان"
}

private fun planTitle(plan: String) = when (plan) {
    "basic" -> "پایه"
    "advanced" -> "پیشرفته"
    else -> "رایگان"
}
