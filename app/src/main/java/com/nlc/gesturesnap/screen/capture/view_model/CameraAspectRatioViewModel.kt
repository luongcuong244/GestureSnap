package com.nlc.gesturesnap.screen.capture.view_model

import androidx.camera.core.AspectRatio
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.screen.capture.ui.value.TimerOption

class CameraAspectRatioViewModel : ViewModel() {
    private val _cameraAspectRatio = MutableLiveData<Int>().apply {
        this.value = AspectRatio.RATIO_4_3
    }

    val cameraAspectRatio: LiveData<Int> = _cameraAspectRatio

    fun switchAspectRatio(){
        if(_cameraAspectRatio.value == AspectRatio.RATIO_4_3){
            _cameraAspectRatio.value = AspectRatio.RATIO_16_9
        }else {
            _cameraAspectRatio.value = AspectRatio.RATIO_4_3
        }
    }
}