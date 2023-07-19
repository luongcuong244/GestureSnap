package com.nlc.gesturesnap.screen.capture.ui.value

import androidx.camera.core.ImageCapture
import com.nlc.gesturesnap.R

enum class FlashOption(val icon : Int, val value : Int) {
    OFF(R.drawable.ic_flash_off, ImageCapture.FLASH_MODE_OFF),
    ON(R.drawable.ic_flash_on, ImageCapture.FLASH_MODE_ON),
    AUTO(R.drawable.ic_flash_auto, ImageCapture.FLASH_MODE_AUTO);
}