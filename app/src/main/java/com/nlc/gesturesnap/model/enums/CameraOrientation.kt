package com.nlc.gesturesnap.model.enums

import androidx.camera.core.CameraSelector

enum class CameraOrientation(val value : Int) {
    BACK(CameraSelector.LENS_FACING_BACK),
    FRONT(CameraSelector.LENS_FACING_FRONT)
}