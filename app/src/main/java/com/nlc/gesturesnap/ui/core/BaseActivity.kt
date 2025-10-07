package com.nlc.gesturesnap.ui.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nlc.gesturesnap.extension.hideNavigation

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.hideNavigation()
    }
}