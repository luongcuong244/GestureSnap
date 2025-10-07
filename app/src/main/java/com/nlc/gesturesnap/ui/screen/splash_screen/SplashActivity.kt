package com.nlc.gesturesnap.ui.screen.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.nlc.gesturesnap.ui.core.BaseActivity
import com.nlc.gesturesnap.ui.screen.capture.CaptureActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }

        lifecycleScope.launch {
            delay(2000)
            navigateToCaptureScreen()
        }
    }

    private fun navigateToCaptureScreen() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivity(intent)
        finish()
    }
}