package com.nlc.gesturesnap.screen.capture.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.screen.capture.ui.value.FlashOption
import com.nlc.gesturesnap.screen.capture.ui.value.TimerOption

class FlashViewModel : ViewModel() {
    private val _flashOption = MutableLiveData<FlashOption>().apply {
        value = FlashOption.AUTO
    }

    val flashOption: LiveData<FlashOption> = _flashOption

    fun nextValue(){
        val index = FlashOption.values().indexOf(flashOption.value)
        val size = FlashOption.values().size
        _flashOption.value = FlashOption.values()[(index + 1) % size]
    }
}