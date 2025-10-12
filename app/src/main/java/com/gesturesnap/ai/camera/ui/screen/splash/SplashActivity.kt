package com.gesturesnap.ai.camera.ui.screen.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.databinding.ActivitySplashBinding
import com.gesturesnap.ai.camera.ui.core.BaseActivity
import com.gesturesnap.ai.camera.ui.screen.home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySplashBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }

        lifecycleScope.launch {
            for (i in 1..25) {
                mBinding.progressBar.progress = i * 4
                mBinding.tvProgress.text = getString(R.string.loading) + " (${i * 4}%)..."
                delay(80)
            }
            startNextScreen()
        }
    }

    private fun startNextScreen() {
        showActivity(HomeActivity::class.java)
        finishAffinity()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}