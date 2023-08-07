package com.nlc.gesturesnap.model.enums

import androidx.camera.core.ImageCapture
import com.nlc.gesturesnap.R

enum class FlashOption(val icon : Int, val value : Int, val text: String) {
    OFF(R.drawable.ic_flash_off, ImageCapture.FLASH_MODE_OFF, "Off"),
    ON(R.drawable.ic_flash_on, ImageCapture.FLASH_MODE_ON, "On"),
    AUTO(R.drawable.ic_flash_auto, ImageCapture.FLASH_MODE_AUTO, "Auto");
}