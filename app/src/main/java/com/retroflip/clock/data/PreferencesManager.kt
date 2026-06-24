package com.retroflip.clock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_24_HOUR = booleanPreferencesKey("is_24_hour")
        val KEY_SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_SHOW_CALENDAR = booleanPreferencesKey("show_calendar")
        val KEY_SOUND_THEME = stringPreferencesKey("sound_theme")
        val KEY_BURN_IN_INTERVAL = intPreferencesKey("burn_in_interval")
        val KEY_BRIGHTNESS = floatPreferencesKey("screen_brightness")
        val KEY_AUTO_START = booleanPreferencesKey("auto_start")
    }

    data class AppSettings(
        val is24Hour: Boolean = true,
        val showSeconds: Boolean = false,
        val soundEnabled: Boolean = false,
        val showCalendar: Boolean = true,
        val soundTheme: String = "clock_ticking_down",
        val burnInInterval: Int = 10,
        val brightness: Float = -1f,  // -1 = system default
        val autoStart: Boolean = false
    )

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            is24Hour = prefs[KEY_24_HOUR] ?: true,
            showSeconds = prefs[KEY_SHOW_SECONDS] ?: false,
            soundEnabled = prefs[KEY_SOUND_ENABLED] ?: false,
            showCalendar = prefs[KEY_SHOW_CALENDAR] ?: true,
            soundTheme = prefs[KEY_SOUND_THEME] ?: "clock_ticking_down",
            burnInInterval = prefs[KEY_BURN_IN_INTERVAL] ?: 10,
            brightness = prefs[KEY_BRIGHTNESS] ?: -1f,
            autoStart = prefs[KEY_AUTO_START] ?: false
        )
    }

    suspend fun updateIs24Hour(value: Boolean) {
        dataStore.edit { it[KEY_24_HOUR] = value }
    }

    suspend fun updateShowSeconds(value: Boolean) {
        dataStore.edit { it[KEY_SHOW_SECONDS] = value }
    }

    suspend fun updateSoundEnabled(value: Boolean) {
        dataStore.edit { it[KEY_SOUND_ENABLED] = value }
    }

    suspend fun updateShowCalendar(value: Boolean) {
        dataStore.edit { it[KEY_SHOW_CALENDAR] = value }
    }

    suspend fun updateSoundTheme(value: String) {
        dataStore.edit { it[KEY_SOUND_THEME] = value }
    }

    suspend fun updateBurnInInterval(value: Int) {
        dataStore.edit { it[KEY_BURN_IN_INTERVAL] = value }
    }

    suspend fun updateBrightness(value: Float) {
        dataStore.edit { it[KEY_BRIGHTNESS] = value }
    }

    suspend fun updateAutoStart(value: Boolean) {
        dataStore.edit { it[KEY_AUTO_START] = value }
    }
}
