package ir.daftar.goftogu

import android.icu.util.Calendar
import android.icu.util.ULocale
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Navy = Color(0xFF0B111E)
private val Card = Color(0xFF101A2D)
private val Line = Color(0xFF1E2D4A)
private val Sky = Color(0xFF0098EA)
private val Amber = Color(0xFFF5A623)

private enum class AdminTab(val title: String) {
    Finance("پایش مالی و هزینه دلاری"),
    Payments("تراکنش‌ها و فیش‌های واریزی"),
    Models("اتصال و مدل‌های هوش مصنوعی"),
    Banners("بنرهای سیستمی"),
    Users("کاربران و گزارش مصرف"),
    Security("امنیت و اطلاعیه‌ها"),
}

@Composable
fun AdminScreen(app: App, onChat: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("tg", 0) }
    var tab by remember { mutableStateOf(AdminTab.Models) }
    var menu by remember { mutableStateOf(false) }
    var rate by remember { mutableStateOf(prefs.getString("usd-irr", "165670") ?: "165670") }
    var rateNote by remember { mutableStateOf(prefs.getString("usd-note", "نرخ ذخیره‌شده") ?: "نرخ ذخیره‌شده") }
    Box(Modifier.fillMaxSize().background(Navy)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onChat) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = Color.White) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                    Text("  سرور فعال چت", color = Color(0xFF4ADE80), fontSize = 13.sp)
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Sky).clickable(onClick = onChat), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.RocketLaunch, "گفتگو", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.size(8.dp))
                Row(
                    Modifier.clip(RoundedCornerShape(16.dp)).background(Card).border(1.dp, Line, RoundedCornerShape(16.dp)).clickable { menu = !menu }.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Menu, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(tab.title, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
            AnimatedContent(tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "admin") { current ->
                when (current) {
                    AdminTab.Finance -> FinanceView(rate, rateNote, { rate = it }, {
                        prefs.edit().putString("usd-irr", rate).putString("usd-note", rateNote).apply()
                    }) { note, value ->
                        rate = value
                        rateNote = note
                        prefs.edit().putString("usd-irr", value).putString("usd-note", note).apply()
                    }
                    AdminTab.Payments -> PaymentsView(app)
                    AdminTab.Models -> ModelsView(app)
                    AdminTab.Banners -> BannersView(app)
                    AdminTab.Users -> UsersView(app)
                    AdminTab.Security -> SecurityView(app)
                }
            }
        }
        if (menu) {
            Box(Modifier.fillMaxSize().background(Color(0x99000000)).clickable { menu = false }) {
                Column(Modifier.padding(top = 64.dp, start = 16.dp, end = 16.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF0E1726)).border(1.dp, Line, RoundedCornerShape(20.dp)).padding(8.dp)) {
                    AdminTab.entries.forEach { item ->
                        val on = item == tab
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(14.dp)).background(if (on) Color(0xFF132033) else Color.Transparent).border(1.dp, if (on) Sky else Color.Transparent, RoundedCornerShape(14.dp)).clickable { tab = item; menu = false }.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (item == AdminTab.Payments) {
                                Box(Modifier.size(22.dp).clip(CircleShape).background(Amber), contentAlignment = Alignment.Center) {
                                    Text("0", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.size(8.dp))
                            }
                            Text(item.title, color = Color.White, modifier = Modifier.weight(1f))
                        }
                    }
                    Text("خروج", color = Sky, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onLogout).padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DarkField(value: String, onChange: (String) -> Unit, placeholder: String, mask: Boolean = false, number: Boolean = false) {
    BasicTextField(
        value,
        onChange,
        Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(16.dp)).background(Card).border(1.dp, Line, RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
        singleLine = true,
        visualTransformation = if (mask) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
        cursorBrush = SolidColor(Sky),
        keyboardOptions = KeyboardOptions(keyboardType = if (number) KeyboardType.Number else KeyboardType.Text),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.CenterEnd) {
                if (value.isEmpty()) Text(placeholder, color = Color(0xFF8BA0B8), fontSize = 14.sp)
                inner()
            }
        },
    )
}

@Composable
private fun SkyButton(label: String, color: Color = Sky, text: Color = Color.White, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "btn")
    Box(
        Modifier.graphicsLayer { scaleX = scale; scaleY = scale }.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(16.dp)).background(color).clickable(interaction, null, onClick = onClick).padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = text, fontWeight = FontWeight.Bold) }
}

@Composable
private fun ModelsView(app: App) {
    var models by remember { mutableStateOf(app.db.models()) }
    var provider by remember { mutableStateOf("openai") }
    var name by remember { mutableStateOf("") }
    var endpoint by remember { mutableStateOf("https://api.openai.com/v1") }
    var modelId by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("0") }
    var output by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf("") }
    fun selectProvider(id: String) {
        provider = id
        if (id == "openrouter") {
            endpoint = "https://openrouter.ai/api/v1"
            modelId = "google/gemma-4-31b-it:free"
            if (name.isBlank()) name = "Gemma 4 31B"
            if (key.isBlank()) key = BuildConfig.OPENROUTER_KEY
            input = "0"
            output = "0"
        } else {
            endpoint = "https://api.openai.com/v1"
            if (modelId == "google/gemma-4-31b-it:free") modelId = ""
        }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("مدل‌ها", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("openai" to "OpenAI", "openrouter" to "OpenRouter").forEach { (id, label) ->
                val selected = provider == id
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if (selected) Sky else Card).border(1.dp, if (selected) Sky else Line, RoundedCornerShape(14.dp)).clickable { selectProvider(id) }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(label, color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
        DarkField(name, { name = it }, "نام نمایشی")
        DarkField(endpoint, { endpoint = it }, "https://api.openai.com/v1")
        DarkField(modelId, { modelId = it }, "شناسه مدل")
        DarkField(key, { key = it }, "کلید API", mask = true)
        DarkField(input, { input = it }, "0", number = true)
        DarkField(output, { output = it }, "0", number = true)
        if (error.isNotEmpty()) Text(error, color = Color(0xFFF87171), modifier = Modifier.padding(top = 8.dp))
        SkyButton("افزودن مدل") {
            try {
                app.db.addModel(name, endpoint, modelId, key, input.toDoubleOrNull() ?: 0.0, output.toDoubleOrNull() ?: 0.0)
                models = app.db.models()
                name = ""; modelId = ""; key = ""
                error = ""
            } catch (e: Exception) { error = e.message ?: "خطا" }
        }
        models.forEach { model ->
            Column(Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(16.dp)).background(Card).border(1.dp, if (model.active) Sky else Line, RoundedCornerShape(16.dp)).padding(14.dp)) {
                Text(model.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(model.modelId, color = Color(0xFF8BA0B8), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                if (model.active) Text("فعال", color = Sky, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!model.active) {
                        Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Sky).clickable {
                            app.db.activateModel(model.id)
                            models = app.db.models()
                        }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text("استفاده شود", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF3F1D24)).clickable {
                        app.db.deleteModel(model.id)
                        models = app.db.models()
                    }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text("حذف", color = Color(0xFFF87171), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceView(rate: String, note: String, onRate: (String) -> Unit, save: () -> Unit, fetched: (String, String) -> Unit) {
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    val title = remember {
        val cal = Calendar.getInstance(ULocale("@calendar=persian"))
        val months = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
        "مالی ${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.End) {
        Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("درآمد تأییدشده: ۰ تومان / ۰ دلار", color = Color(0xFFD6E2F0), modifier = Modifier.padding(top = 16.dp))
        Text("هزینه هوش مصنوعی که باید پرداخت شود: ۰ دلار / ۰ تومان", color = Color(0xFFD6E2F0), modifier = Modifier.padding(top = 6.dp))
        Text("نرخ هر دلار $rate تومان از tgju.org · $note", color = Color(0xFF8BA0B8), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
        DarkField(rate, onRate, "165670", number = true)
        SkyButton("ثبت نرخ", onClick = save)
        Text(if (busy) "در حال دریافت..." else "گرفتن نرخ روز", color = Sky, modifier = Modifier.padding(top = 12.dp).clickable(enabled = !busy) {
            busy = true
            scope.launch {
                val result = withContext(Dispatchers.IO) { runCatching { fetchTgjuToman() }.getOrNull() }
                if (result != null) {
                    val clock = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    fetched("در امروز $clock", result.toLong().toString())
                }
                busy = false
            }
        })
    }
}

private fun adminPlan(plan: String) = when (plan) {
    "basic" -> "پایه"
    "advanced" -> "پیشرفته"
    else -> "رایگان"
}

private fun fetchTgjuToman(): Double {
    val conn = URL("https://www.tgju.org/profile/price_dollar_rl").openConnection() as HttpURLConnection
    conn.connectTimeout = 15000
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    val body = conn.inputStream.bufferedReader().readText()
    val rial = Regex("""data-col="info\.last_trade\.PDrCotVal">([0-9,]+)""")
        .find(body)?.groupValues?.get(1)?.replace(",", "")?.toDouble()
        ?: error("نرخ پیدا نشد")
    return rial / 10.0
}

@Composable
private fun PaymentsView(app: App) {
    var filter by remember { mutableStateOf("همه") }
    var rows by remember { mutableStateOf(app.db.slips()) }
    val shown = rows.filter {
        when (filter) {
            "در انتظار" -> it.status == "pending"
            "تأیید شده" -> it.status == "approved"
            "رد شده" -> it.status == "rejected"
            else -> true
        }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("پرداخت‌ها", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("همه", "در انتظار", "تأیید شده", "رد شده").forEach { label ->
                Text(label, color = if (filter == label) Color.Black else Color.White, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (filter == label) Sky else Card).clickable { filter = label }.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
        if (shown.isEmpty()) Text("فیشی برای این وضعیت نیست.", color = Color(0xFF8BA0B8), modifier = Modifier.padding(top = 24.dp).align(Alignment.End))
        shown.forEach { slip ->
            Column(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(16.dp)).background(Card).padding(12.dp)) {
                Text("${slip.userName} · ${slip.amount} تومان", color = Color.White, modifier = Modifier.align(Alignment.End))
                Text(slip.refCode ?: "رسید تصویری", color = Color(0xFF8BA0B8), modifier = Modifier.align(Alignment.End))
                if (slip.status == "pending") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("تأیید", color = Sky, modifier = Modifier.clickable {
                        app.db.reviewSlip(slip.id, "approved")
                        rows = app.db.slips()
                    })
                    Text("رد", color = Color(0xFFF87171), modifier = Modifier.clickable {
                        app.db.reviewSlip(slip.id, "rejected")
                        rows = app.db.slips()
                    })
                }
            }
        }
    }
}

@Composable
private fun BannersView(app: App) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(app.db.activeBanner()?.title ?: "") }
    var body by remember { mutableStateOf(app.db.activeBanner()?.body ?: "") }
        var file by remember { mutableStateOf("No file chosen") }
        var bannerPath by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val dest = File(context.filesDir, "admin-banner.jpg")
        context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } }
        file = dest.name
        bannerPath = dest.absolutePath
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Card).border(1.dp, Line, RoundedCornerShape(24.dp)).padding(16.dp)) {
            Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("کمپین فعال سراسری", color = Color.Black, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Amber).padding(horizontal = 8.dp, vertical = 4.dp))
                Text("LIVE TAB", color = Color.White, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFF15803D)).padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Text(title.ifBlank { "هنوز بنری ساخته نشده" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp).align(Alignment.End))
            Text(body.ifBlank { "عنوان و متن بنر اولین ورود کاربر را همین‌جا بنویس." }, color = Color(0xFFD6E2F0), modifier = Modifier.padding(top = 6.dp).align(Alignment.End))
            Text("🎁", fontSize = 28.sp, modifier = Modifier.padding(top = 8.dp).clip(RoundedCornerShape(16.dp)).background(Amber).padding(10.dp))
        }
        DarkField(title, { title = it }, "عنوان بنر")
        BasicTextField(
            body,
            { body = it },
            Modifier.fillMaxWidth().padding(top = 8.dp).height(96.dp).clip(RoundedCornerShape(16.dp)).background(Card).border(1.dp, Line, RoundedCornerShape(16.dp)).padding(16.dp),
            textStyle = TextStyle(color = Color.White),
            cursorBrush = SolidColor(Sky),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.TopEnd) {
                    if (body.isEmpty()) Text("متن بنر", color = Color(0xFF8BA0B8))
                    inner()
                }
            },
        )
        Text(file, color = Color.White, modifier = Modifier.padding(top = 8.dp).align(Alignment.End).clickable { picker.launch("image/*") })
        if (message.isNotEmpty()) Text(message, color = Sky, modifier = Modifier.padding(top = 8.dp))
        Text("پس‌زمینه برای کاربران", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp).align(Alignment.End))
        var wallTitle by remember { mutableStateOf("") }
        var wallCategory by remember { mutableStateOf("") }
        var wallFile by remember { mutableStateOf<String?>(null) }
        val wallPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) return@rememberLauncherForActivityResult
            val dest = File(context.filesDir, "admin-wall-${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input -> dest.outputStream().use { input.copyTo(it) } }
            wallFile = dest.absolutePath
        }
        DarkField(wallTitle, { wallTitle = it }, "عنوان عکس")
        DarkField(wallCategory, { wallCategory = it }, "دسته")
        Text(if (wallFile == null) "انتخاب عکس" else "عکس انتخاب شد", color = Sky, modifier = Modifier.padding(top = 8.dp).clickable { wallPicker.launch("image/*") })
        SkyButton("ذخیره پس‌زمینه") {
            try {
                if (wallFile == null) error("عکس را انتخاب کن")
                else {
                    app.db.addWallpaper(wallTitle, wallCategory, wallFile!!)
                    message = "پس‌زمینه برای کاربران ذخیره شد"
                    wallTitle = ""; wallCategory = ""; wallFile = null
                }
            } catch (e: Exception) { message = e.message ?: "خطا" }
        }
        SkyButton("ذخیره بنر", Amber, Color.Black) {
            try {
                app.db.saveBanner(title, body, bannerPath)
                message = "بنر ذخیره شد"
            } catch (e: Exception) { message = e.message ?: "خطا" }
        }
    }
}

@Composable
private fun UsersView(app: App) {
    var users by remember { mutableStateOf(app.db.users()) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("کاربران و گزارش مصرف", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }
        items(users, key = { it.id }) { user ->
            val quota = app.db.quota(user)
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(16.dp)).background(Card).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (user.suspended) "رفع" else "تعلیق", color = Sky, modifier = Modifier.clickable {
                    app.db.setSuspended(user.id, !user.suspended)
                    users = app.db.users()
                })
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${user.phone} · ${adminPlan(user.plan)} · ${quota.first}/${quota.second}", color = Color(0xFF8BA0B8), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SecurityView(app: App) {
    var items by remember { mutableStateOf(app.db.announcements()) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("اطلاعیه‌ها", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
        Column(Modifier.padding(top = 12.dp).clip(RoundedCornerShape(20.dp)).background(Card).border(1.dp, Line, RoundedCornerShape(20.dp)).padding(12.dp)) {
            DarkField(title, { title = it }, "عنوان")
            BasicTextField(
                body,
                { body = it },
                Modifier.fillMaxWidth().padding(top = 8.dp).height(90.dp).clip(RoundedCornerShape(16.dp)).background(Navy).border(1.dp, Line, RoundedCornerShape(16.dp)).padding(16.dp),
                textStyle = TextStyle(color = Color.White),
                cursorBrush = SolidColor(Sky),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.TopEnd) {
                        if (body.isEmpty()) Text("متن", color = Color(0xFF8BA0B8))
                        inner()
                    }
                },
            )
            if (error.isNotEmpty()) Text(error, color = Color(0xFFF87171), modifier = Modifier.padding(top = 8.dp))
            SkyButton("انتشار") {
                try {
                    app.db.publishAnnouncement(title, body)
                    items = app.db.announcements()
                    title = ""; body = ""; error = ""
                } catch (e: Exception) { error = e.message ?: "خطا" }
            }
        }
        items.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(16.dp)).background(Card).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("حذف", color = Sky, modifier = Modifier.clickable {
                    app.db.deleteAnnouncement(item.id)
                    items = app.db.announcements()
                })
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("کاربر", color = Color(0xFF8BA0B8), fontSize = 12.sp)
                    Text(item.title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(item.body, color = Color(0xFFD6E2F0), fontSize = 13.sp)
                }
            }
        }
    }
}
