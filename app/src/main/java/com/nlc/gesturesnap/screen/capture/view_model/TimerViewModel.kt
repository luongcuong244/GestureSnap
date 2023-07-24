package com.nlc.gesturesnap.screen.capture.view_model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.screen.capture.ui.value.TimerOption

class TimerViewModel : ViewModel() {

    private var _countDownTimer : CountDownTimer? = null
    private val _timerValue = MutableLiveData<Int>().apply {
        value = 0
    }

    private val _timerOption = MutableLiveData<TimerOption>().apply {
        value = TimerOption.OFF
    }

    // if the data of the _photoSavingTrigger changes, the device will take a photo
    private val _photoSavingTrigger = MutableLiveData<Boolean>()

    val timerOption: LiveData<TimerOption> = _timerOption
    val timerValue: LiveData<Int> = _timerValue
    val photoSavingTrigger = _photoSavingTrigger

    fun setTimerOption(option: TimerOption){
        val isSelecting = _timerOption.value?.equals(option) ?: false
        if (!isSelecting){
            _timerOption.value = option
        }
    }

    fun startTimer(){

        // plus one for a delay
        val timeForCountDown : Int = (_timerOption.value ?: TimerOption.OFF).value + 1

        _timerValue.value = timeForCountDown

        _countDownTimer = object : CountDownTimer((timeForCountDown * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerValue.value = _timerValue.value!! - 1
            }

            override fun onFinish() {
                // take a photo
                _photoSavingTrigger.value = !(_photoSavingTrigger.value ?: false)
                _timerValue.value = 0
                _countDownTimer?.cancel()
            }
        }

        _countDownTimer?.start()
    }
}