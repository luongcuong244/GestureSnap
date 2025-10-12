package com.gesturesnap.ai.camera.ui.screen.home

import android.annotation.SuppressLint
import android.os.Bundle
import com.gesturesnap.ai.camera.databinding.ActivityHomeBinding
import com.gesturesnap.ai.camera.ui.core.BaseActivity
import com.gesturesnap.ai.camera.ui.screen.capture.CaptureActivity

@SuppressLint("CustomSplashScreen")
class HomeActivity : BaseActivity() {

    private lateinit var mBinding: ActivityHomeBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            llCapture.setOnClickListener {
                showActivity(CaptureActivity::class.java)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finishAffinity()
    }
}