package com.nlc.gesturesnap.screen.capture.ui.value

import androidx.camera.core.CameraSelector

enum class CameraOrientation(val value : Int) {
    BACK(CameraSelector.LENS_FACING_BACK),
    FRONT(CameraSelector.LENS_FACING_FRONT)
}