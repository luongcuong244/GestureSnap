package com.nlc.gesturesnap.screen.capture.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaptureViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Capture Fragment"
    }
    val text: LiveData<String> = _text
}