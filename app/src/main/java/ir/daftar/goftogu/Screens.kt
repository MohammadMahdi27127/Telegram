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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
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

private val Blue = Color(0xFF24A1DE)
private val Pill = Color(0xFFE3F2FD)
private val NavBlue = Color(0xFF1976D2)
private val CheckGreen = Color(0xFF4FAE4E)
private val OutBubble = Color(0xFFEEFFDE)

@Composable
fun LandingScreen(onLogin: () -> Unit, onSignup: () -> Unit, onPricing: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(Color.White).padding(20.dp)) {
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
fun BottomBar(route: String, onSelect: (String) -> Unit) {
    val palette = LocalTg.current
    val idle = if (palette.dark) Color(0xFFA0ACB6) else Color(0xFF707579)
    val accent = Color(0xFF24A1DE)
    val pill = if (palette.dark) Color(0xFF203447) else Color(0xFFE7F3FB)
    val items = listOf(
        TabGlyph("chats", "چت‌ها", Icons.Outlined.ChatBubbleOutline),
        TabGlyph("contacts", "مخاطبین", Icons.Outlined.People),
        TabGlyph("profile", "پروفایل", Icons.Outlined.Person),
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
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black.copy(alpha = 0.04f), spotColor = Color.Black.copy(alpha = 0.06f))
                .clip(RoundedCornerShape(28.dp))
                .background(palette.surface.copy(alpha = 0.95f))
                .border(0.5.dp, if (palette.dark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.05f), RoundedCornerShape(28.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val on = route == item.id
                val tint by animateColorAsState(if (on) accent else idle, tween(200, easing = FastOutSlowInEasing), label = "tab-tint")
                val capsule by animateColorAsState(if (on) pill else Color.Transparent, tween(200, easing = FastOutSlowInEasing), label = "tab-pill")
                val capsuleWidth by animateDpAsState(if (on) 48.dp else 24.dp, tween(200, easing = FastOutSlowInEasing), label = "tab-width")
                Column(
                    Modifier.weight(1f).clickable { onSelect(item.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .height(24.dp)
                            .width(capsuleWidth)
                            .clip(RoundedCornerShape(50))
                            .background(capsule),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(18.dp))
                    }
                    Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = tint)
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
            Text(confirm, color = Color(0xFF24A1DE), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun AuthCard(title: String, content: @Composable () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE8F1F8), Color(0xFFD7E7F2)))),
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
    val page = if (dark) Color(0xFF0F172A) else Color.White
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
                Box(Modifier.fillMaxWidth().background(Color(0xFF24A1DE)).padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
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
    val day = remember { java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0) }.timeInMillis }
    val shown = people.filter {
        (query.isBlank() || it.name.contains(query) || it.phone.contains(query)) && when (filter) {
            "note" -> it.hasNote
            "today" -> it.lastAt >= day
            "phone" -> it.lastChannel == "phone"
            else -> true
        }
    }
    val palette = LocalTg.current
    val dark = palette.dark
    val listState = rememberLazyListState()
    val arrived = remember { mutableStateListOf<String>() }
    val scrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 4 } }
    val hairline = if (dark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.05f)
    val rowLine = if (dark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
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
                    IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "منو", tint = if (palette.dark) Color(0xFFA0ACB6) else Color(0xFF707579), modifier = Modifier.size(22.dp)) }
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
                            .background(Brush.horizontalGradient(listOf(Color(0x3324A1DE), Color(0x221E88E5))))
                            .clickable(onClick = onPay)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Wallet, null, tint = palette.blue, modifier = Modifier.size(16.dp))
                        Text("موجودی: ${formatToman(account.balance)} تومان", color = palette.text, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
                    }
                }
                Spacer(Modifier.weight(1f))
                if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت") }
                SyncTitle()
            }
            Row(
                Modifier.padding(horizontal = 12.dp).padding(top = 4.dp, bottom = 10.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(palette.search).padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Search, null, tint = if (palette.dark) Color(0xFFA0ACB6) else Color(0xFF707579), modifier = Modifier.size(18.dp))
                BasicTextField(
                    query,
                    { query = it },
                    Modifier.padding(start = 8.dp).weight(1f),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = palette.text, textAlign = TextAlign.Start),
                    cursorBrush = SolidColor(palette.blue),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text(if (contacts) "جستجو در مخاطبین" else "جستجو در گفتگوها", color = palette.subtext, fontSize = 15.sp)
                        inner()
                    },
                )
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
                .background(if (creating) Color(0xFF1E88E5) else Blue)
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
            Box(Modifier.size(46.dp).clip(CircleShape).background(Color(0xFF24A1DE)), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, null, tint = Color.White) }
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
                .background(Brush.horizontalGradient(listOf(Color(0xFF24A1DE), Color(0xFF1E88E5))))
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
    var menu by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf(false) }
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
        Row(Modifier.fillMaxWidth().background(if (LocalDark.current) Color(0xFF0F172A) else Color.White).padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            val headerIdle = if (LocalDark.current) Color(0xFFA0ACB6) else Color(0xFF707579)
            IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = headerIdle, modifier = Modifier.size(24.dp)) }
            if (user.plan == "advanced") IconButton(onClick = { aiOpen = true }) { Icon(Icons.Filled.AutoAwesome, "هوش مصنوعی", tint = if (LocalDark.current) Color(0xFF5288C1) else Blue, modifier = Modifier.size(20.dp)) }
            Row(Modifier.weight(1f).clickable { info = true }, verticalAlignment = Alignment.CenterVertically) {
                PersonAvatar(personTitle, person?.avatar, size = 44.dp)
                Column(Modifier.padding(start = 10.dp)) {
                    Text(personTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                    SyncTitle()
                }
            }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "بیشتر", tint = if (LocalDark.current) Color(0xFFA0ACB6) else Color(0xFF707579), modifier = Modifier.size(22.dp)) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("افزودن عضو") }, onClick = { menu = false; info = true })
                    DropdownMenuItem(text = { Text("خلاصه") }, onClick = {
                        menu = false
                        val text = entries.joinToString("\n") { "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.occurredAt))} ${it.body}" }.ifBlank { "متنی نیست." }
                        shareTextFile(context, "$personTitle.txt", text)
                    })
                    DropdownMenuItem(text = { Text("پاک کردن تاریخچه") }, onClick = {
                        menu = false
                        val ids = entries.map { it.id }.toSet()
                        hidden = ids
                        scope.launch {
                            delay(280)
                            app.db.clearEntries(personId)
                            entries = emptyList()
                            hidden = emptySet()
                        }
                    })
                }
            }
        }
        if (summary.isNotEmpty()) {
            Text(summary, Modifier.fillMaxWidth().background(Color.White).padding(12.dp), color = Color(0xFF334155))
        }
        LazyColumn(Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries, key = { it.id }) { entry ->
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
            val darkBar = LocalDark.current
            val bar = if (darkBar) Color(0xFF17212B) else Color.White
            val idleChip = if (darkBar) Color(0xFF1C2738) else Color(0xCCF3F4F6)
            val idleInk = if (darkBar) Color(0xFFD1D5DB) else Color(0xFF4B5563)
            val field = if (darkBar) Color(0xFF0E1621) else Color(0xFFF1F2F6)
            val fieldInk = if (darkBar) Color.White else Color(0xFF222222)
            val group = person?.isGroup == true
            val recipient = members.find { it.id == memberId }?.name ?: "کل گروه"
            var memberMenu by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth().background(bar).padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (group) {
                    Box {
                        Row(
                            Modifier.clip(CircleShape).background(if (darkBar) Color(0xFF202B36) else Color(0xFFF3F4F6)).clickable { memberMenu = true }.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Outlined.Person, null, tint = idleInk, modifier = Modifier.size(15.dp))
                            Text(recipient, color = if (darkBar) Color(0xFFE5E7EB) else Color(0xFF1F2937), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 6.dp), maxLines = 1)
                            Icon(Icons.Filled.KeyboardArrowDown, null, tint = idleInk, modifier = Modifier.size(13.dp))
                        }
                        DropdownMenu(expanded = memberMenu, onDismissRequest = { memberMenu = false }, modifier = Modifier.background(if (darkBar) Color(0xFF17212B) else Color.White)) {
                            DropdownMenuItem(
                                text = { Text("کل گروه", color = fieldInk, fontWeight = if (memberId == null) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { memberId = null; memberMenu = false },
                                trailingIcon = { if (memberId == null) Icon(Icons.Filled.Check, null, tint = Blue) },
                            )
                            members.forEach { member ->
                                DropdownMenuItem(
                                    text = { Text(member.name, color = fieldInk) },
                                    leadingIcon = {
                                        Box(Modifier.size(22.dp).clip(CircleShape).background(Color.hsl((member.name.sumOf { it.code } % 360).toFloat(), 0.55f, 0.42f)), contentAlignment = Alignment.Center) {
                                            Text(member.name.take(1), color = Color.White, fontSize = 11.sp)
                                        }
                                    },
                                    onClick = { memberId = member.id; memberMenu = false },
                                )
                            }
                        }
                    }
                }
                listOf("talk" to "مکالمه", "note" to "یادداشت").forEach { (id, label) ->
                    val on = kind == id
                    Text(label, color = if (on) Color.White else idleInk, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clip(CircleShape).background(if (on) Blue else idleChip).clickable { kind = id }.padding(horizontal = 12.dp, vertical = 4.dp))
                }
                media.forEach { (id, label) ->
                    val on = channel == id
                    Text(label, color = if (on) Color.White else idleInk, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clip(CircleShape).background(if (on) Blue else idleChip).clickable { channel = id }.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
            if (pendingMedia != null) {
                Text(if (pendingKind == "video") "فیلم آماده ارسال است" else "عکس آماده ارسال است", color = Blue, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                IconButton(onClick = { attachMenu = true }, modifier = Modifier.size(36.dp)) { Icon(Icons.Filled.AttachFile, "پیوست", tint = if (darkBar) Color(0xFFA0ACB6) else Color(0xFF707579), modifier = Modifier.size(20.dp)) }
            }
            BasicTextField(
                body,
                { body = it },
                Modifier.weight(1f).heightIn(min = 40.dp, max = 100.dp).clip(RoundedCornerShape(20.dp)).background(field).padding(horizontal = 14.dp, vertical = 8.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = fieldInk),
                cursorBrush = SolidColor(Blue),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (body.isEmpty()) Text("پیام", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        inner()
                    }
                },
            )
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(if (recording) Color(0xFFE53935) else Blue).clickable {
                    if (body.isBlank() && pendingMedia == null) {
                        if (recording) stopVoice(app, user, personId) { path ->
                            try {
                                app.db.addEntry(user, personId, "ویس", "voice", channel, System.currentTimeMillis(), path)
                                entries = app.db.entries(personId)
                            } catch (e: Exception) {
                                if ((e.message ?: "").let { it.contains("سقف") || it.contains("مهمان") }) upgrade = true else error = e.message ?: "خطا"
                            }
                        } else permission.launch(android.Manifest.permission.RECORD_AUDIO)
                        return@clickable
                    }
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
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(body.isBlank() && pendingMedia == null, transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) }, label = "send") { empty ->
                    if (empty) Icon(Icons.Outlined.Mic, "صدا", tint = Color.White, modifier = Modifier.size(19.dp))
                    else Icon(Icons.AutoMirrored.Filled.Send, "ارسال", tint = Color.White, modifier = Modifier.size(19.dp))
                }
            }
            }
        }
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
        if (info) {
            ModalBottomSheet(onDismissRequest = { info = false }, sheetState = sheet) {
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
                Box(Modifier.size(42.dp).clip(CircleShape).border(2.dp, Brush.linearGradient(listOf(Color(0xFF24A1DE), Color(0xFF7C4DFF))), CircleShape).padding(2.dp)) {
                    Box(Modifier.fillMaxSize().clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF24A1DE), Color(0xFF5C6BC0)))), contentAlignment = Alignment.Center) {
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
            Box(Modifier.size(46.dp).clip(CircleShape).background(if (busy) palette.subtext else Blue).clickable(enabled = !busy, onClick = onSend), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Send, "ارسال", tint = Color.White)
            }
        }
    }
}

@Composable
private fun AiBubble(turn: AiMessage, palette: TgPalette, onCopy: () -> Unit, onEdit: () -> Unit) {
    val mine = turn.role == "user"
    val bubble = if (mine) palette.outgoing else palette.incoming
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
        else -> GalaxyBackdrop()
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
    val bubble = when {
        entry.kind == "voice" -> Color(0xFF1F8F4E)
        note -> palette.note
        else -> palette.outgoing
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
                .fillMaxWidth(0.78f)
                .shadow(1.dp, shape)
                .clip(shape)
                .background(bubble)
                .combinedClickable(onClick = {}, onLongClick = { open = !open })
                .padding(horizontal = 10.dp, vertical = 6.dp),
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
                    Text(if (note) "یادداشت" else mediumLabel(entry.channel), color = if (note) Color(0xFF8D6E00) else Blue, fontSize = bodySize, lineHeight = bodyLine, fontWeight = FontWeight.Bold)
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
fun ProfileScreen(app: App, userId: String, theme: ThemeController, chatFont: ChatFontController, onBack: () -> Unit, pay: () -> Unit, pricing: () -> Unit, onAdmin: () -> Unit, logout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf(app.db.user(userId)) }
    var editName by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.name ?: "") }
    var editPassword by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var nextPassword by remember { mutableStateOf("") }
    var wallpapersOpen by remember { mutableStateOf(false) }
    var crop by remember { mutableStateOf<Bitmap?>(null) }
    var message by remember { mutableStateOf("") }
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
    val page = if (dark) Color(0xFF0F172A) else Color(0xFFF4F7FB)
    val card = if (dark) Color(0xFF1E293B) else Color.White
    val ink = if (dark) Color.White else Color(0xFF111827)
    val pulse = rememberInfiniteTransition(label = "cam")
    val camScale by pulse.animateFloat(0.92f, 1.08f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "cam-scale")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(page), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth()) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = ink) } }
        Box(Modifier.padding(top = 8.dp), contentAlignment = Alignment.BottomEnd) {
            val avatarFile = current.avatar?.let { File(it) }?.takeIf { it.exists() }
            Box(Modifier.size(104.dp).clip(CircleShape).background(Blue).clickable { avatarPicker.launch("image/*") }, contentAlignment = Alignment.Center) {
                if (avatarFile != null) {
                    val bitmap = remember(avatarFile.absolutePath) { BitmapFactory.decodeFile(avatarFile.absolutePath) }
                    if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Text(current.name.take(1), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                } else Text(current.name.take(1), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
            Box(Modifier.graphicsLayer { scaleX = camScale; scaleY = camScale }.size(30.dp).clip(CircleShape).background(Blue).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.CameraAlt, "تغییر عکس", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Text(current.name, color = ink, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text(current.phone, color = Color(0xFF707579))
        Text(planTitle(current.plan), color = Blue, modifier = Modifier.padding(top = 4.dp))
        val balanceInk = if (dark) Color.White else Color(0xFF111827)
        Row(
            Modifier.padding(top = 8.dp).clip(RoundedCornerShape(20.dp)).background(if (dark) Color(0xFF243042) else Color.White).border(1.dp, if (dark) Color(0xFF3A4A5C) else Color(0xFFE5E7EB), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Wallet, null, tint = Blue, modifier = Modifier.size(16.dp))
            Text("موجودی: ${formatToman(current.balance)} تومان", color = balanceInk, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.padding(start = 6.dp))
        }
        if (message.isNotEmpty()) Text(message, color = Blue, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        Column(Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(28.dp)).background(card)) {
            if (isAdmin(current.phone)) SettingRow("ادمین", "داشبورد", Icons.Filled.AdminPanelSettings, Color(0xFFE3F2FD), Blue, onClick = onAdmin)
            SettingRow("نام", current.name, Icons.Filled.Person, Color(0xFFE3F2FD), Blue) { name = current.name; editName = true }
            SettingRow("عکس پروفایل", "انتخاب و برش", Icons.Filled.CameraAlt, Color(0xFFE1F5FE), Color(0xFF0288D1)) { avatarPicker.launch("image/*") }
            SettingRow("پس‌زمینه چت", "گالری طبیعت", Icons.Filled.Image, Color(0xFFE0F7FA), Color(0xFF00ACC1)) { wallpapersOpen = true }
            SettingRow("کیف پول", "${formatToman(current.balance)} تومان", Icons.Filled.Wallet, Color(0xFFE8F5E9), Color(0xFF2E7D32), onClick = pay)
            SettingRow("رمز عبور", "", Icons.Filled.Lock, Color(0xFFE3F2FD), Color(0xFF1565C0)) { editPassword = true }
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
            SettingRow("خروج", "", Icons.Filled.Logout, Color(0xFFFFEBEE), Color(0xFFE53935), danger = true, onClick = logout)
        }
        Text("تنظیمات گفتگو", color = ink, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 18.dp).fillMaxWidth().padding(horizontal = 20.dp))
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(28.dp)).background(card).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(Icons.Filled.FormatSize, Color(0xFFE3F2FD), Blue)
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text("اندازه قلم پیام‌ها", color = ink, fontWeight = FontWeight.Bold)
                    Text("${persianDigits(chatFont.size.toString())} pt", color = if (dark) Color(0xFFD1D5DB) else Color(0xFF7F91A4), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            val bubble = if (dark) Color(0xFF2B5278) else Color(0xFFEFFDDE)
            Text(
                "این یک پیام آزمایشی برای مشاهده اندازه قلم است.",
                color = if (dark) Color.White else Color(0xFF111827),
                fontSize = chatFont.size.sp,
                lineHeight = (chatFont.size * 1.5f).sp,
                modifier = Modifier.padding(top = 12.dp).clip(RoundedCornerShape(16.dp)).background(bubble).padding(horizontal = 12.dp, vertical = 8.dp),
            )
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("A", color = ink, fontSize = 12.sp)
                Slider(
                    chatFont.size.toFloat(),
                    { chatFont.set(it.roundToInt()) },
                    Modifier.weight(1f).padding(horizontal = 8.dp),
                    valueRange = 12f..22f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = Blue, activeTrackColor = Blue, inactiveTrackColor = if (dark) Color(0xFF334155) else Color(0xFFE5E7EB)),
                )
                Text("A", color = ink, fontSize = 22.sp)
            }
        }
        Text("دیدن قیمت‌ها", color = Blue, modifier = Modifier.clickable(onClick = pricing).padding(bottom = 24.dp))
    }
    if (editName) {
        AlertDialog(
            onDismissRequest = { editName = false },
            title = { Text("نام") },
            text = { OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        app.db.rename(userId, name)
                        user = app.db.user(userId)
                        message = "نام ذخیره شد"
                        editName = false
                    } catch (e: Exception) { message = e.message ?: "خطا" }
                }) { Text("ذخیره") }
            },
            dismissButton = { TextButton(onClick = { editName = false }) { Text("بستن") } },
        )
    }
    if (editPassword) {
        AlertDialog(
            onDismissRequest = { editPassword = false },
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
        ModalBottomSheet(onDismissRequest = { wallpapersOpen = false }, sheetState = sheet) {
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
                Button(onClick = { onSend(exportSendPhoto(bitmap, rotation, cropping, aspect, scale, offset, view)) }, shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
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
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
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
    val page = if (dark) Color(0xFF0F172A) else Color(0xFFF4F7FB)
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
            Box(Modifier.padding(top = 16.dp).fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Color(0xFF0098EA)).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
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
                Text(label, color = if (on) Color.White else ink, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) Color(0xFF0098EA) else card).clickable {
                    if (id == "basic" || id == "advanced") { plan = id; amount = if (id == "advanced") "1000000" else "500000" }
                    else { plan = null; amount = id }
                }.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
        OutlinedTextField(amount, { amount = it.filter { ch -> ch.isDigit() }; plan = null }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("مبلغ دلخواه به تومان") })
        OutlinedTextField(deposit, { deposit = it }, Modifier.fillMaxWidth(), label = { Text("شناسه واریز / کد پیگیری") })
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(20.dp)).clickable { picker.launch("image/*") }.padding(18.dp), contentAlignment = Alignment.Center) {
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

private fun planTitle(plan: String) = when (plan) {
    "basic" -> "پایه"
    "advanced" -> "پیشرفته"
    else -> "رایگان"
}
