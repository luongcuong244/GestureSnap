package com.gesturesnap.ai.camera.ui.core

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gesturesnap.ai.camera.extension.hideNavigation

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.hideNavigation()
    }

    fun showActivity(activity: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }
}