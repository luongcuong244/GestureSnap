package com.nlc.gesturesnap.ui.screen.splash_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nlc.gesturesnap.ads.MyApplication
import com.nlc.gesturesnap.ui.screen.capture.CaptureActivity
import com.nlc.gesturesnap.view_model.splash.AdsLoadingViewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel : AdsLoadingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        val application = application as? MyApplication

        application?.loadAd(
            this@SplashActivity,
            onAdLoaded = {
                viewModel.setIsLoading(false)
                navigateToCaptureScreen()
            },
            onAdFailedToLoad = {
                viewModel.setIsLoading(false)
                navigateToCaptureScreen()
            }
        )

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    private fun navigateToCaptureScreen(){
        val intent = Intent(this, CaptureActivity :: class.java)
        startActivity(intent)

        finish()
    }
}