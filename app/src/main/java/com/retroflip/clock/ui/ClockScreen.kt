package com.retroflip.clock.ui

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.retroflip.clock.audio.SoundManager
import com.retroflip.clock.data.LunarCalendar
import com.retroflip.clock.data.PreferencesManager
import com.retroflip.clock.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

// ── Page background: near-black ──
private val pageBackgroundColor = Color(0xFF050505)

// ── Calendar panel palette (information card) ──
private val calCardBg = Color(0xFF0D0D0D)
private val calCardBorder = Color(0xFF1E1E1E)
private val calDateColor = Color(0xFFE0E0E0)      // Level 1: date
private val calWeekdayColor = Color(0xFF9A9A9A)    // Level 2: weekday
private val calLunarColor = Color(0xFF6E6E6E)      // Level 3: lunar
private val calLunarSubColor = Color(0xFF555555)   // Level 3: lunar sub
private val yiBadgeColor = Color(0xFF3D6B45)
private val yiTextColor = Color(0xFF7EC88A)
private val jiBadgeColor = Color(0xFF6B2D2D)
private val jiTextColor = Color(0xFFE07070)
private val calDividerColor = Color(0xFF1A1A1A)

@Composable
fun ClockScreen(
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val prefsManager = remember {
        PreferencesManager((context.applicationContext as Application).dataStore)
    }
    val settings by prefsManager.settings.collectAsState(
        initial = PreferencesManager.AppSettings()
    )

    val soundManager = remember { SoundManager(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var showSettings by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0) }
    var offsetY by remember { mutableStateOf(0) }

    // ── Screen brightness ──
    LaunchedEffect(settings.brightness) {
        activity?.window?.let { window ->
            val params = window.attributes
            params.screenBrightness = settings.brightness
            window.attributes = params
        }
    }

    // ── Sound lifecycle ──
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (settings.soundEnabled) {
                        soundManager.resumeLoop(settings.soundTheme, true)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> soundManager.stopAll()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            soundManager.release()
        }
    }

    LaunchedEffect(settings.soundEnabled, settings.soundTheme) {
        if (settings.soundEnabled) soundManager.resumeLoop(settings.soundTheme, true)
        else soundManager.stopAll()
    }

    // ── Burn-in protection ──
    LaunchedEffect(settings.burnInInterval) {
        while (true) {
            delay(settings.burnInInterval * 60_000L)
            offsetX = (-(4..4).random())
            offsetY = (-(4..4).random())
        }
    }

    // ── Time state ──
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var prevMinute by remember { mutableStateOf(-1) }
    var prevSecond by remember { mutableStateOf(-1) }

    LaunchedEffect(settings.showSeconds) {
        calendar = Calendar.getInstance()
        prevMinute = calendar.get(Calendar.MINUTE)
        prevSecond = calendar.get(Calendar.SECOND)

        while (true) {
            val now = Calendar.getInstance()
            val minute = now.get(Calendar.MINUTE)
            val second = now.get(Calendar.SECOND)

            if (minute != prevMinute) {
                calendar = now
                prevMinute = minute
                prevSecond = second
            }
            if (settings.showSeconds && second != prevSecond) {
                calendar = now
                prevSecond = second
            }
            if (!settings.showSeconds) prevSecond = -1

            delay(if (settings.showSeconds) 200L else 1000L)
        }
    }

    val is24Hour = settings.is24Hour
    val showSeconds = settings.showSeconds
    val soundEnabled = settings.soundEnabled
    val soundTheme = settings.soundTheme

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    val displayHour = if (is24Hour) hour else {
        when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
    }

    val hourStr = displayHour.toString().padStart(2, '0')
    val minuteStr = minute.toString().padStart(2, '0')
    val secondStr = second.toString().padStart(2, '0')

    // ─ Calendar data ──
    val lunarDate = LunarCalendar.toLunar(calendar)
    val solarStr = LunarCalendar.formatSolar(calendar)
    val lunarYearStr = "${LunarCalendar.getGanZhiYear(lunarDate.year)} 【${LunarCalendar.getZodiac(lunarDate.year)}】"
    val lunarDayStr = "${LunarCalendar.getLunarMonthName(lunarDate.month, lunarDate.isLeapMonth)} ${LunarCalendar.getLunarDayName(lunarDate.day)}"
    val (yi, ji) = LunarCalendar.getHuangli(calendar)

    // ── Root: wood background + double-tap for settings ──
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBackgroundColor)
            .offset { IntOffset(offsetX, offsetY) }
            .pointerInput(Unit) {
                var lastTap = 0L
                detectTapGestures {
                    val now = System.currentTimeMillis()
                    if (now - lastTap < 400) showSettings = true
                    lastTap = now
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─ Left: Calendar Panel (18% weight — minimal visual weight) ──
            CalendarDisplay(
                solarStr = solarStr,
                lunarYearStr = lunarYearStr,
                lunarDayStr = lunarDayStr,
                yi = yi,
                ji = ji,
                calendar = calendar,
                modifier = Modifier
                    .weight(0.18f)
                    .fillMaxHeight()
            )

            // ── Spacer ──
            Spacer(modifier = Modifier.width(24.dp))

            // ─ Right: Clock Area (82% weight — absolute visual center) ──
            Box(
                modifier = Modifier
                    .weight(0.82f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlipCard(
                        value = hourStr,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        fontWeight = FontWeight.Black,
                        onFlip = { if (soundEnabled) soundManager.playFlip(soundTheme) }
                    )

                    ColonSeparator()

                    FlipCard(
                        value = minuteStr,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        fontWeight = FontWeight.Black,
                        onFlip = { if (soundEnabled) soundManager.playFlip(soundTheme) }
                    )

                    if (showSeconds) {
                        ColonSeparator()

                        FlipCard(
                            value = secondStr,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            fontWeight = FontWeight.Black,
                            onFlip = { if (soundEnabled) soundManager.playFlip(soundTheme) }
                        )
                    }
                }
            }
        }

        // ── 1:1 indicator (bottom-right) ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .width(44.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.White.copy(alpha = 0.07f))
        ) {
            androidx.compose.material3.Text(
                text = "1:1",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.30f),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Settings overlay ──
        SettingsOverlay(
            visible = showSettings,
            settings = settings,
            onDismiss = { showSettings = false },
            onIs24HourChange = { scope.launch { prefsManager.updateIs24Hour(it) } },
            onShowSecondsChange = { scope.launch { prefsManager.updateShowSeconds(it) } },
            onSoundEnabledChange = { scope.launch { prefsManager.updateSoundEnabled(it) } },
            onShowCalendarChange = { scope.launch { prefsManager.updateShowCalendar(it) } },
            onSoundThemeChange = { scope.launch { prefsManager.updateSoundTheme(it) } },
            onBurnInIntervalChange = { scope.launch { prefsManager.updateBurnInInterval(it) } },
            onBrightnessChange = { scope.launch { prefsManager.updateBrightness(it) } },
            onAutoStartChange = { scope.launch { prefsManager.updateAutoStart(it) } }
        )
    }
}

@Composable
private fun CalendarDisplay(
    solarStr: String,
    lunarYearStr: String,
    lunarDayStr: String,
    yi: String,
    ji: String,
    calendar: Calendar,
    modifier: Modifier = Modifier
) {
    // ── Unified type scale on 4dp baseline grid ──
    val sansFont = FontFamily.SansSerif
    val weekdayNames = arrayOf("日", "一", "二", "三", "四", "五", "六")
    val weekday = "星期${weekdayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]}"

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(calCardBg)
            .drawBehind {
                // Card border
                drawRect(
                    color = calCardBorder,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Level 1: Date (primary) — compact format, allow wrap ──
        androidx.compose.material3.Text(
            text = solarStr,
            fontSize = 16.sp,
            fontFamily = sansFont,
            fontWeight = FontWeight.Bold,
            color = calDateColor,
            maxLines = 2
        )

        // ── Level 2: Weekday ──
        androidx.compose.material3.Text(
            text = weekday,
            fontSize = 14.sp,
            fontFamily = sansFont,
            fontWeight = FontWeight.Medium,
            color = calWeekdayColor,
            maxLines = 1
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(calDividerColor)
        )

        // ── Level 3: Lunar calendar ──
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            androidx.compose.material3.Text(
                text = lunarYearStr,
                fontSize = 12.sp,
                fontFamily = sansFont,
                color = calLunarColor,
                maxLines = 1
            )
            androidx.compose.material3.Text(
                text = lunarDayStr,
                fontSize = 13.sp,
                fontFamily = sansFont,
                fontWeight = FontWeight.Medium,
                color = calLunarSubColor,
                maxLines = 1
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(calDividerColor)
        )

        // ── Level 4: Yi/Ji ──
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            YiJiRow(label = "宜", text = yi, badgeBg = yiBadgeColor, textColor = yiTextColor)
            YiJiRow(label = "忌", text = ji, badgeBg = jiBadgeColor, textColor = jiTextColor)
        }
    }
}

@Composable
private fun YiJiRow(
    label: String,
    text: String,
    badgeBg: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = label,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
            )
        }
        androidx.compose.material3.Text(
            text = text,
            fontSize = 11.sp,
            fontFamily = FontFamily.SansSerif,
            color = textColor,
            maxLines = 2
        )
    }
}
