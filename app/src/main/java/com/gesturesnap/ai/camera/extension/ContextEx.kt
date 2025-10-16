package com.gesturesnap.ai.camera.extension

import android.content.Context
import android.widget.Toast


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}