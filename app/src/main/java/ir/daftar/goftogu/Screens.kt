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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AttachFile
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
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.shadow
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
import java.io.File
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
            Text("دفتر گفتگو", color = Color(0xFF222222), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 10.dp))
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

@Composable
fun BottomBar(route: String, chatBadge: Int, onSelect: (String) -> Unit) {
    val palette = LocalTg.current
    val items = listOf(
        Triple("chats", "چت‌ها", Icons.Outlined.ChatBubbleOutline),
        Triple("contacts", "مخاطبین", Icons.Outlined.Person),
        Triple("profile", "پروفایل", Icons.Outlined.AccountCircle),
    )
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(10.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(28.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { (id, label, icon) ->
                val on = route == id
                Column(
                    Modifier.weight(1f).clickable { onSelect(id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Box(
                            Modifier
                                .height(32.dp)
                                .width(if (on) 64.dp else 46.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (on) Color(0x3324A1DE) else Color.Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(icon, null, tint = if (on) palette.blue else palette.subtext, modifier = Modifier.size(22.dp))
                        }
                        if (id == "chats" && chatBadge > 0) {
                            Box(Modifier.padding(top = 2.dp).size(16.dp).clip(CircleShape).background(palette.blue), contentAlignment = Alignment.Center) {
                                Text(if (chatBadge > 9) "۹" else persianDigits(chatBadge.toString()), color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                    Text(label, fontSize = 11.sp, color = if (on) palette.blue else palette.subtext)
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
        AuthLinks("ثبت‌نام", openSignup, "فراموشی رمز", openForgot)
        Text(
            "ورود مهمان",
            color = Color(0xFF0288D1),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clickable {
                try {
                    val id = app.db.signupGuest()
                    app.setSession(id)
                    onLoggedIn(id)
                } catch (e: Exception) {
                    alert = "مهمان" to (e.message ?: "خطا")
                }
            },
        )
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
    Box(Modifier.fillMaxSize().background(Color(0x730E1621)).clickable { (onDismiss ?: onConfirm)() }, contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable { }
                .padding(16.dp),
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            Text(body, color = Color(0xFF6B7280), fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp).fillMaxWidth(), textAlign = TextAlign.Start)
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(confirm, color = Blue, fontWeight = FontWeight.Medium, modifier = Modifier.clickable(onClick = onConfirm))
                if (dismiss != null && onDismiss != null) {
                    Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFFEE2E2)).clickable(onClick = onDismiss).padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(dismiss, color = Color(0xFFF87171))
                    }
                }
            }
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
                Text("دفتر گفتگو", color = Color(0xFF0288D1), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
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
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
        cursorBrush = SolidColor(Color(0xFF29B6F6)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).padding(vertical = 10.dp), contentAlignment = Alignment.CenterEnd) {
                    if (value.isEmpty()) Text(placeholder, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    inner()
                }
                IconButton(onClick = { visible = !visible }) {
                    Icon(if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "نمایش رمز", tint = Color(0xFF6B7280))
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
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) contacts = readPhoneContacts(context)
    }
    LaunchedEffect(granted) {
        if (granted) contacts = readPhoneContacts(context) else ask.launch(android.Manifest.permission.READ_CONTACTS)
    }
    val dark = LocalDark.current
    Column(Modifier.fillMaxSize().background(if (dark) Color(0xFF0F172A) else Color.White)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت") }
            Text("مخاطبین", color = Blue, fontSize = 17.sp, fontWeight = FontWeight.Medium)
        }
        if (granted) {
            LazyColumn {
                items(contacts) { (name, phone) ->
                    Row(Modifier.fillMaxWidth().clickable { onPick(phone) }.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(52.dp).clip(CircleShape).background(Color.hsl((name.sumOf { it.code } % 360).toFloat(), 0.55f, 0.42f)), contentAlignment = Alignment.Center) {
                            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(phone, color = Color(0xFF6B7280), fontSize = 14.sp)
                        }
                    }
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
    draftPhone: String = "",
    openComposer: Boolean = false,
    onComposerHandled: () -> Unit = {},
) {
    val context = LocalContext.current
    var people by remember { mutableStateOf(app.db.people(userId)) }
    var creating by remember { mutableStateOf(false) }
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
    Box(Modifier.fillMaxSize().background(palette.bg)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "منو", tint = palette.blue) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        listOf("all" to "همه", "note" to "یادداشت", "today" to "امروز", "phone" to "تلفنی").forEach { (id, label) ->
                            DropdownMenuItem(text = { Text(if (filter == id) "$label  ✓" else label) }, onClick = { filter = id; menu = false })
                        }
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
                Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(palette.search).padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Search, null, tint = palette.subtext, modifier = Modifier.size(20.dp))
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
            LazyColumn(Modifier.weight(1f).padding(bottom = 88.dp)) {
                if (!contacts && account?.plan == "advanced") {
                    item(key = "ai-chat") {
                        Row(Modifier.fillMaxWidth().clickable(onClick = onAi), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.padding(horizontal = 16.dp, vertical = 10.dp).size(52.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.AutoAwesome, null, tint = Color.White)
                            }
                            Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
                                Text("چت با هوش مصنوعی", color = if (dark) Color.White else Color(0xFF111827), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text("یکی از گفتگوها را انتخاب کن", color = Color(0xFF6B7280), fontSize = 14.sp)
                                Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(1.dp).background(Color(0xFFF3F4F6)))
                            }
                        }
                    }
                }
                items(shown, key = { it.id }) { person ->
                    Row(Modifier.fillMaxWidth().clickable { open(person.id) }, verticalAlignment = Alignment.CenterVertically) {
                        PersonAvatar(person.name, person.avatar, Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                        Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
                            Text(person.name, color = palette.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                            Text(
                                if (contacts) person.phone.ifBlank { person.role.ifBlank { "بدون شماره" } } else person.lastBody.ifBlank { person.role.ifBlank { "بدون ثبت" } },
                                color = palette.subtext,
                                fontSize = 14.sp,
                                maxLines = 1,
                            )
                            Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(1.dp).background(palette.border))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(listStamp(person.lastAt), color = palette.subtext, fontSize = 12.sp)
                            if (person.unread > 0) {
                                Box(Modifier.padding(top = 4.dp).size(20.dp).clip(CircleShape).background(palette.blue), contentAlignment = Alignment.Center) {
                                    Text(persianDigits(person.unread.coerceAtMost(9).toString()), color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        val fabScale by animateFloatAsState(if (creating) 0.9f else 1f, tween(200), label = "fab")
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .graphicsLayer { scaleX = fabScale; scaleY = fabScale }
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
                        creating = !creating
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(if (creating) Icons.Filled.Close else Icons.Filled.Edit, "شخص جدید", tint = Color.White, modifier = Modifier.size(26.dp))
        }
        AnimatedVisibility(
            visible = creating,
            enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(240)) { it } + fadeOut(tween(180)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            AddPersonSheet(app, userId, draft, onClose = { creating = false }, onSaved = {
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
                    Text(banner.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 6.dp))
                    Text(banner.body, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
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
                        Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                        Text(notice.body, modifier = Modifier.padding(top = 4.dp))
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
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) runCatching { avatar = savePersonAvatar(context, uri, "draft-${System.currentTimeMillis()}") }.onFailure { onError(it.message ?: "عکس ذخیره نشد") }
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
        Text("افزودن شخص جدید", color = if (dark) Color.White else palette.text, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Box(Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally).clickable { picker.launch("image/*") }) {
            PersonAvatar(name.ifBlank { "؟" }, avatar, size = 72.dp)
            Box(Modifier.align(Alignment.BottomEnd).size(24.dp).clip(CircleShape).background(Blue), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        SheetField(name, { name = it }, "نام", Icons.Outlined.Person, dark)
        SheetField(phone, { phone = it }, "شماره موبایل", Icons.Filled.Phone, dark, KeyboardType.Phone)
        SheetField(role, { role = it }, "سمت", Icons.Filled.Star, dark)
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
                        app.db.addPerson(userId, name, phone, role, avatar)
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
                Text("ثبت شخص جدید", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
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
    var recordSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(recording) {
        if (!recording) return@LaunchedEffect
        recordSeconds = 0
        while (recording) {
            kotlinx.coroutines.delay(1000)
            recordSeconds += 1
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
    val scope = rememberCoroutineScope()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching { pendingMedia = prepareImage(context, uri); pendingKind = "photo" }.onFailure { error = it.message ?: "عکس ارسال نشد" }
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
            IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = if (LocalDark.current) Color.White else Color(0xFF222222)) }
            if (user.plan == "advanced") IconButton(onClick = { aiOpen = true }) { Icon(Icons.Filled.AutoAwesome, "هوش مصنوعی", tint = Blue) }
            Row(Modifier.weight(1f).clickable { info = true }, verticalAlignment = Alignment.CenterVertically) {
                PersonAvatar(personTitle, person?.avatar, size = 44.dp)
                Column(Modifier.padding(start = 10.dp)) {
                    Text(personTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1)
                    SyncTitle()
                }
            }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, "بیشتر", tint = Color(0xFF222222)) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
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
                    JournalBubble(entry, onPin = {
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
            Row(Modifier.fillMaxWidth().background(Color(0xFF1C1C1E)).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Delete, "حذف", tint = Color(0xFFE53935), modifier = Modifier.clickable {
                    runCatching { voiceRecorder.value?.stop() }
                    runCatching { voiceRecorder.value?.release() }
                    voiceRecorder.value = null
                    voiceFile.value?.delete()
                    recording = false
                })
                Text("توقف", color = Color.White, modifier = Modifier.weight(1f).clickable {
                    stopVoice(app, user, personId) { path ->
                        runCatching { app.db.addEntry(user, personId, "", "voice", channel, System.currentTimeMillis(), path); entries = app.db.entries(personId) }
                    }
                }, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(String.format(Locale.US, "%d:%02d", recordSeconds / 60, recordSeconds % 60), color = Color.White)
                Icon(Icons.AutoMirrored.Filled.Send, "ارسال", tint = Color(0xFF39D353), modifier = Modifier.padding(start = 12.dp).clickable {
                    stopVoice(app, user, personId) { path ->
                        runCatching { app.db.addEntry(user, personId, "", "voice", channel, System.currentTimeMillis(), path); entries = app.db.entries(personId) }
                    }
                })
            }
        } else Column(Modifier.fillMaxWidth().background(if (LocalDark.current) Color(0xFF17212B) else Color.White).padding(horizontal = 8.dp, vertical = 8.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("talk" to "مکالمه", "note" to "یادداشت").forEach { (id, label) ->
                    val on = kind == id
                    Text(label, color = if (on) Color.White else LocalTg.current.text, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(if (on) if (id == "note") Color(0xFFF9A825) else Blue else LocalTg.current.search).clickable { kind = id }.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                media.forEach { (id, label) ->
                    val on = channel == id
                    Text(label, color = if (on) Color.White else LocalTg.current.text, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(if (on) Blue else LocalTg.current.search).clickable { channel = id }.padding(horizontal = 10.dp, vertical = 6.dp))
                }
            }
            if (pendingMedia != null) {
                Text(if (pendingKind == "video") "فیلم آماده ارسال است" else "عکس آماده ارسال است", color = Blue, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { imagePicker.launch("image/*") }) { Icon(Icons.Filled.Image, "عکس", tint = Blue) }
            IconButton(onClick = { videoPicker.launch("video/*") }) { Icon(Icons.Filled.Videocam, "فیلم", tint = Blue) }
            BasicTextField(
                body,
                { body = it },
                Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = Color(0xFF222222)),
                cursorBrush = SolidColor(Blue),
                decorationBox = { inner ->
                    if (body.isEmpty()) Text("پیام", color = Color(0xFF9CA3AF))
                    inner()
                },
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(if (recording) Color(0xFFE53935) else Blue).clickable {
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
                            app.db.addEntry(user, personId, caption, sendKind, channel, System.currentTimeMillis(), mediaPath = pendingMedia)
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
                    if (empty) Icon(Icons.Outlined.Mic, "صدا", tint = Color.White)
                    else Icon(Icons.AutoMirrored.Filled.Send, "ارسال", tint = Color.White)
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
    var editing by remember { mutableStateOf<AiMessage?>(null) }
    var busy by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch { runCatching { image = prepareImage(context, uri) }.onFailure { toast = it.message ?: "عکس خوانده نشد" } }
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
    var editing by remember { mutableStateOf<AiMessage?>(null) }
    var busy by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }
    val sheetBg = if (palette.dark) Color(0xF217212B) else Color(0xF2FFFFFF)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch { runCatching { image = prepareImage(context, uri) }.onFailure { toast = it.message ?: "عکس خوانده نشد" } }
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
    when {
        file != null -> {
            val bitmap = remember(file.absolutePath) { BitmapFactory.decodeFile(file.absolutePath) }
            if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else GalaxyBackdrop()
        }
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
private fun JournalBubble(entry: Entry, onPin: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val palette = LocalTg.current
    val note = entry.kind == "note"
    val bubble = when {
        entry.kind == "voice" -> Color(0xFF1F8F4E)
        note -> palette.note
        else -> palette.outgoing
    }
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
            if (entry.kind == "voice" && entry.voicePath != null) {
                VoiceMessage(entry.voicePath)
            } else {
                entry.mediaPath?.let { path -> MediaPreview(path, entry.kind) }
                if (entry.kind != "photo" && entry.kind != "video") {
                    Text(if (note) "یادداشت" else mediumLabel(entry.channel), color = if (note) Color(0xFF8D6E00) else Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                if (entry.body.isNotBlank()) Text(entry.body, color = ink, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
            Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.occurredAt)), fontSize = 11.sp, color = if (entry.kind == "voice") Color.White.copy(alpha = 0.8f) else palette.subtext, modifier = Modifier.align(Alignment.End).padding(top = 2.dp))
        }
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
private fun MediaPreview(path: String, kind: String) {
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
        if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
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
                Text(notice.title, fontWeight = FontWeight.Bold)
                Text(notice.body, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

private val wallpapers = listOf(
    "کوهستان" to "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=600&q=80",
    "کهکشان" to "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=600&q=80",
    "جنگل" to "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=600&q=80",
    "دریاچه" to "https://images.unsplash.com/photo-1439066615861-d1af74d74000?auto=format&fit=crop&w=600&q=80",
    "کویر" to "https://images.unsplash.com/photo-1509316785289-025f5b846b35?auto=format&fit=crop&w=600&q=80",
    "شفق" to "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=600&q=80",
    "اقیانوس" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80",
    "غروب" to "https://images.unsplash.com/photo-1495616811223-4d98c6e9c869?auto=format&fit=crop&w=600&q=80",
    "پاییز" to "https://images.unsplash.com/photo-1507783548227-544c3f8e3332?auto=format&fit=crop&w=600&q=80",
    "شب پرستاره" to "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?auto=format&fit=crop&w=600&q=80",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(app: App, userId: String, theme: ThemeController, onBack: () -> Unit, pay: () -> Unit, pricing: () -> Unit, onAdmin: () -> Unit, logout: () -> Unit) {
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
        Text("موجودی: ${current.balance} تومان", modifier = Modifier.padding(top = 6.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(horizontal = 12.dp, vertical = 6.dp))
        if (message.isNotEmpty()) Text(message, color = Blue, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        Column(Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(28.dp)).background(card)) {
            if (isAdmin(current.phone)) SettingRow("ادمین", "داشبورد", Icons.Filled.AdminPanelSettings, Color(0xFFE3F2FD), Blue, onClick = onAdmin)
            SettingRow("نام", current.name, Icons.Filled.Person, Color(0xFFE3F2FD), Blue) { name = current.name; editName = true }
            SettingRow("عکس پروفایل", "انتخاب و برش", Icons.Filled.CameraAlt, Color(0xFFE1F5FE), Color(0xFF0288D1)) { avatarPicker.launch("image/*") }
            SettingRow("پس‌زمینه چت", "گالری طبیعت", Icons.Filled.Image, Color(0xFFE0F7FA), Color(0xFF00ACC1)) { wallpapersOpen = true }
            SettingRow("کیف پول", "${current.balance} تومان", Icons.Filled.Wallet, Color(0xFFE8F5E9), Color(0xFF2E7D32), onClick = pay)
            SettingRow("رمز عبور", "", Icons.Filled.Lock, Color(0xFFE3F2FD), Color(0xFF1565C0)) { editPassword = true }
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(Icons.Filled.DarkMode, Color(0xFFFFF8E1), Color(0xFFF9A825))
                    Text("ظاهر برنامه", color = ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                }
                Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("light" to "روشن", "dark" to "تاریک", "system" to "سیستم").forEach { (id, label) ->
                        val on = theme.mode == id
                        Text(
                            label,
                            color = if (on) Color.White else ink,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(if (on) Blue else Color.Transparent).border(1.dp, if (on) Blue else Color(0xFFD0D7DE), RoundedCornerShape(16.dp)).clickable { theme.set(id) }.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }
            SettingRow("خروج", "", Icons.Filled.Logout, Color(0xFFFFEBEE), Color(0xFFE53935), danger = true, onClick = logout)
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
            app.db.setAvatar(userId, dest.absolutePath)
            user = app.db.user(userId)
            crop = null
            message = "عکس ذخیره شد"
        }
    }
    if (wallpapersOpen) {
        val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { wallpapersOpen = false }, sheetState = sheet) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text("انتخاب پس‌زمینه گفتگو", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                val uploaded = app.db.wallpapers()
                uploaded.groupBy { it.category }.forEach { (category, rows) ->
                    Text(category, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                    LazyVerticalGrid(GridCells.Fixed(2), Modifier.padding(top = 8.dp).heightIn(max = 220.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(rows, key = { it.id }) { item ->
                            val bitmap = remember(item.path) { BitmapFactory.decodeFile(item.path) }
                            Box(Modifier.height(96.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF10243A)).clickable {
                                app.db.setChatStyle(userId, item.path, null)
                                user = app.db.user(userId)
                                wallpapersOpen = false
                            }) {
                                if (bitmap != null) Image(bitmap.asImageBitmap(), item.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                Text(item.title, color = Color.White, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp))
                            }
                        }
                    }
                }
                Text("طبیعت", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                LazyVerticalGrid(GridCells.Fixed(2), Modifier.padding(top = 8.dp).heightIn(max = 420.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(wallpapers) { index, item ->
                        val selected = current.chatBg?.endsWith("wallpaper-$index.jpg") == true
                        var thumb by remember(item.second) { mutableStateOf<Bitmap?>(null) }
                        LaunchedEffect(item.second) {
                            thumb = withContext(Dispatchers.IO) {
                                runCatching { BitmapFactory.decodeStream(URL(item.second).openStream()) }.getOrNull()
                            }
                        }
                        Box(Modifier.height(96.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF10243A)).clickable {
                            scope.launch {
                                val dest = withContext(Dispatchers.IO) { downloadWallpaper(item.second, File(context.filesDir, "wallpaper-$index.jpg")) }
                                app.db.setChatStyle(userId, dest.absolutePath, null)
                                user = app.db.user(userId)
                                wallpapersOpen = false
                            }
                        }) {
                            if (thumb != null) Image(thumb!!.asImageBitmap(), item.first, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            Text(item.first, color = Color.White, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp))
                            if (selected) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
                        }
                    }
                }
                AuthButton("آپلود تصویر دلخواه") { wallpaperPicker.launch("image/*") }
                TextButton(onClick = {
                    app.db.setChatStyle(userId, null, null)
                    user = app.db.user(userId)
                    wallpapersOpen = false
                }) { Text("پس‌زمینه کهکشانی") }
            }
        }
    }
}

private fun downloadWallpaper(url: String, dest: File): File {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.connectTimeout = 15000
    conn.inputStream.use { input -> dest.outputStream().use { input.copyTo(it) } }
    return dest
}

@Composable
private fun SettingRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tile: Color, tint: Color, danger: Boolean = false, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        IconTile(icon, tile, tint)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (danger) Color(0xFFE53935) else if (LocalDark.current) Color.White else Color(0xFF111827))
            if (subtitle.isNotEmpty()) Text(subtitle, color = Color(0xFF707579), fontSize = 13.sp)
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
private fun CropDialog(bitmap: Bitmap, onCancel: () -> Unit, onSave: (Bitmap) -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var view by remember { mutableStateOf(1) }
    var rotation by remember { mutableStateOf(0f) }
    Box(Modifier.fillMaxSize().background(Color(0xCC0E1621)), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(20.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("برش عکس", fontWeight = FontWeight.Bold)
            Box(
                Modifier.padding(top = 12.dp).size(240.dp).clip(CircleShape).background(Color.Black).onSizeChanged { view = it.width }.pointerInput(bitmap) {
                    detectDragGestures { _, drag -> offset += drag }
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
            Slider(scale, { scale = it }, valueRange = 1f..3f)
            Row {
                TextButton(onClick = { rotation = (rotation + 90f) % 360f }) { Text("چرخش") }
                TextButton(onClick = onCancel) { Text("بستن") }
                TextButton(onClick = { onSave(cropBitmap(bitmap, scale, offset, view, rotation)) }) { Text("ذخیره") }
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
        if (message.isNotEmpty()) Text(message, color = Blue, modifier = Modifier.padding(top = 8.dp))
        if (error.isNotEmpty()) Text(error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
    }
}

private fun planTitle(plan: String) = when (plan) {
    "basic" -> "پایه"
    "advanced" -> "پیشرفته"
    else -> "رایگان"
}
