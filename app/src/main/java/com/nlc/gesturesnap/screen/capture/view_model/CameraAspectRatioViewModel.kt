package com.nlc.gesturesnap.screen.capture.view_model

import androidx.camera.core.AspectRatio
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraAspectRatioViewModel : ViewModel() {
    private val _cameraAspectRatio = MutableLiveData<Int>().apply {
        this.value = AspectRatio.RATIO_16_9
    }

    val cameraAspectRatio: LiveData<Int> = _cameraAspectRatio

    fun setCameraAspectRatio(aspectRatio: Int){
        _cameraAspectRatio.value = aspectRatio
    }
}