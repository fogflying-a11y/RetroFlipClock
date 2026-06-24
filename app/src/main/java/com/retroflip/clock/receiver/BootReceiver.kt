package com.retroflip.clock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Application
import com.retroflip.clock.MainActivity
import com.retroflip.clock.data.PreferencesManager
import com.retroflip.clock.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // Check if auto-start is enabled (blocking read from DataStore)
        val autoStart = runBlocking {
            try {
                val prefsManager = PreferencesManager(
                    (context.applicationContext as Application).dataStore
                )
                prefsManager.settings.first().autoStart
            } catch (e: Exception) {
                false
            }
        }

        if (autoStart) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launchIntent)
        }
    }
}
