package com.gesturesnap.ai.camera.extension

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Window.hideNavigation() {
    if (setFullScreenWallpaper()) return

    decorView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        val activityRoot = decorView
        activityRoot.getWindowVisibleDisplayFrame(rect)
        if (setFullScreenWallpaper()) return@addOnGlobalLayoutListener
    }
}

fun Window.hideStatusBar(){
    val flags: Int = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    decorView.systemUiVisibility = flags
    val decorView: View = decorView
    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            decorView.systemUiVisibility = flags
        }
    }
}

private fun Window.setFullScreenWallpaper(): Boolean {
    val windowInsetsController: WindowInsetsControllerCompat? =
        if (Build.VERSION.SDK_INT >= 30) {
            ViewCompat.getWindowInsetsController(decorView)
        } else {
            WindowInsetsControllerCompat(this, decorView)
        }

    if (windowInsetsController == null) {
        return true
    }
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
    )
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    windowInsetsController.hide(WindowInsetsCompat.Type.systemGestures())
    return false
}