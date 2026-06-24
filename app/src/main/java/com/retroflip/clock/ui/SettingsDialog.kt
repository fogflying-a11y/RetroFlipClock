package com.retroflip.clock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retroflip.clock.data.PreferencesManager

@Composable
fun SettingsOverlay(
    visible: Boolean,
    settings: PreferencesManager.AppSettings,
    onDismiss: () -> Unit,
    onIs24HourChange: (Boolean) -> Unit,
    onShowSecondsChange: (Boolean) -> Unit,
    onSoundEnabledChange: (Boolean) -> Unit,
    onShowCalendarChange: (Boolean) -> Unit,
    onSoundThemeChange: (String) -> Unit,
    onBurnInIntervalChange: (Int) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onAutoStartChange: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(250)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                }
        ) {
            // Settings panel — bottom sheet style
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(200)
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(150))
            ) {
                SettingsPanel(
                    settings = settings,
                    onDismiss = onDismiss,
                    onIs24HourChange = onIs24HourChange,
                    onShowSecondsChange = onShowSecondsChange,
                    onSoundEnabledChange = onSoundEnabledChange,
                    onShowCalendarChange = onShowCalendarChange,
                    onSoundThemeChange = onSoundThemeChange,
                    onBurnInIntervalChange = onBurnInIntervalChange,
                    onBrightnessChange = onBrightnessChange,
                    onAutoStartChange = onAutoStartChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SettingsPanel(
    settings: PreferencesManager.AppSettings,
    onDismiss: () -> Unit,
    onIs24HourChange: (Boolean) -> Unit,
    onShowSecondsChange: (Boolean) -> Unit,
    onSoundEnabledChange: (Boolean) -> Unit,
    onShowCalendarChange: (Boolean) -> Unit,
    onSoundThemeChange: (String) -> Unit,
    onBurnInIntervalChange: (Int) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onAutoStartChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = Color(0xFFF5F5F0)
    val subTextColor = Color(0xFFA1A1AA)
    val panelBg = Color(0xCC1A1A1E)
    val cardBg = Color(0xFF2C2C2E)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(panelBg)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .pointerInput(Unit) {
                // Consume taps so they don't pass through to background
                detectTapGestures {}
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RetroFlip 设置",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "关闭 ✕",
                color = subTextColor,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Display mode ──
            SettingsSectionCard {
                SettingsToggleRow(
                    label = "24 小时制",
                    subLabel = if (settings.is24Hour) "当前: 24h" else "当前: 12h",
                    checked = settings.is24Hour,
                    onCheckedChange = onIs24HourChange
                )
                Divider()
                SettingsToggleRow(
                    label = "显示秒数",
                    subLabel = if (settings.showSeconds) "HH:MM:SS" else "HH:MM",
                    checked = settings.showSeconds,
                    onCheckedChange = onShowSecondsChange
                )
                Divider()
                SettingsToggleRow(
                    label = "万年历",
                    subLabel = if (settings.showCalendar) "已开启" else "已关闭",
                    checked = settings.showCalendar,
                    onCheckedChange = onShowCalendarChange
                )
            }

            // ── Sound ──
            SettingsSectionCard {
                SettingsToggleRow(
                    label = "翻页音效",
                    subLabel = if (settings.soundEnabled) "已开启" else "已关闭",
                    checked = settings.soundEnabled,
                    onCheckedChange = onSoundEnabledChange
                )

                if (settings.soundEnabled) {
                    Divider()
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text("音效主题", color = subTextColor, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))

                        val themes = listOf(
                            "clock_ticking_down" to "清脆翻页音",
                            "slow_cinematic_clock_ticking" to "电影感滴答 (循环)",
                            "clock_ticking_sfx" to "机械环境音 (循环)"
                        )

                        themes.forEach { (key, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSoundThemeChange(key) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.soundTheme == key,
                                    onClick = { onSoundThemeChange(key) },
                                    colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFF5F5F0),
                                        unselectedColor = subTextColor
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(label, color = textColor, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // ── Brightness ──
            SettingsSectionCard {
                Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("屏幕亮度", color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = if (settings.brightness < 0) "自动" else "${(settings.brightness * 100).toInt()}%",
                            color = subTextColor,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Slider(
                        value = if (settings.brightness < 0) 0.5f else settings.brightness,
                        onValueChange = onBrightnessChange,
                        valueRange = 0.1f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFF5F5F0),
                            activeTrackColor = Color(0xFFF5F5F0),
                            inactiveTrackColor = Color(0xFF3A3A3C)
                        )
                    )
                    Text(
                        text = "← 拖动调节亮度，松手后生效",
                        color = subTextColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // ── Burn-in protection ──
            SettingsSectionCard {
                Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        "防烧屏间隔",
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 5, 10, 20, 30).forEach { minutes ->
                            val selected = settings.burnInInterval == minutes
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) Color(0xFFF5F5F0) else cardBg)
                                    .clickable { onBurnInIntervalChange(minutes) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${minutes}m",
                                    fontSize = 12.sp,
                                    color = if (selected) Color.Black else textColor
                                )
                            }
                        }
                    }
                }
            }

            // ── Auto start ──
            SettingsSectionCard {
                SettingsToggleRow(
                    label = "开机自启",
                    subLabel = if (settings.autoStart) "已开启" else "已关闭",
                    checked = settings.autoStart,
                    onCheckedChange = onAutoStartChange
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2C2C2E))
            .padding(14.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    subLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFFF5F5F0),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subLabel,
                color = Color(0xFFA1A1AA),
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFF5F5F0),
                checkedTrackColor = Color(0xFF3A3A3C),
                uncheckedThumbColor = Color(0xFF8E8E93),
                uncheckedTrackColor = Color(0xFF2C2C2E)
            )
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color(0xFF3A3A3C))
    )
}
