package com.retroflip.clock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.retroflip.clock.ui.ClockScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val config = LocalConfiguration.current
            val isLandscape = config.screenWidthDp > config.screenHeightDp

            ClockScreen(
                isLandscape = isLandscape,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
