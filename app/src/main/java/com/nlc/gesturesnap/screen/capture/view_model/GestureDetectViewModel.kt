package com.nlc.gesturesnap.screen.capture.view_model

import android.content.Context
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.capture.model.GestureDetectOption
import com.nlc.gesturesnap.screen.capture.ui.value.GestureCategory

class GestureDetectViewModel : ViewModel() {

    val HAND_DETECTING_TIME_IN_MILLI = 3000L

    private var _countDownTimer : CountDownTimer? = null
    private var _isDetecting : Boolean = false

    private val _currentHandGesture = MutableLiveData<GestureDetectOption>()
    private val _isDrawHand = MutableLiveData<Boolean>().apply {
        value = false
    }
    private val _handGestureProgress = MutableLiveData<Int>().apply {
        value = 0
    }

    // if the data of the _timerTrigger changes, the self-timer will run
    private val _timerTrigger = MutableLiveData<Boolean>()

    val handGestureOptions = MutableLiveData<List<GestureDetectOption>>()
    val currentHandGesture : LiveData<GestureDetectOption> = _currentHandGesture
    val isDrawHand : LiveData<Boolean> = _isDrawHand
    val handGestureProgress: LiveData<Int> = _handGestureProgress
    val timerTrigger : LiveData<Boolean> = _timerTrigger

    fun createOptionList(context: Context){

        if(handGestureOptions.value?.isNotEmpty() == true)
            return

        val gestureDetectOptions = ArrayList<GestureDetectOption>()

        ContextCompat.getDrawable(context, R.drawable.ic_select_all)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.all_text), GestureCategory.ALl, false, ContextCompat.getDrawable(context, R.drawable.ic_select_all_white))
            }?.let {
                gestureDetectOptions.add(
                    it
            )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_not_interested)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.none_text), GestureCategory.NONE)
            }?.let {
                gestureDetectOptions.add(
                    it
            )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_fist_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.closed_fist_text), GestureCategory.CLOSED_FIST, false, ContextCompat.getDrawable(context, R.drawable.ic_fist_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
            )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_hand_palm_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.open_palm_text), GestureCategory.OPEN_PALM, false, ContextCompat.getDrawable(context, R.drawable.ic_hand_palm_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_pointing_up_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.pointing_up_text), GestureCategory.POINTING_UP, false, ContextCompat.getDrawable(context, R.drawable.ic_pointing_up_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_thumbs_up_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.thumb_up_text), GestureCategory.THUMB_UP, false, ContextCompat.getDrawable(context, R.drawable.ic_thumbs_up_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_thumb_down_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.thumb_down_text), GestureCategory.THUMB_DOWN, false, ContextCompat.getDrawable(context, R.drawable.ic_thumb_down_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_victory_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.victory_text), GestureCategory.VICTORY, false, ContextCompat.getDrawable(context, R.drawable.ic_victory_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_hand_love_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.love_text), GestureCategory.LOVE, false, ContextCompat.getDrawable(context, R.drawable.ic_hand_love_fill))
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        handGestureOptions.value = gestureDetectOptions
    }

    fun setCurrentHandGesture(index: Int){
        _currentHandGesture.value = handGestureOptions.value?.get(index)
    }

    fun switchIsDrawHandValue(){
        _isDrawHand.value = !isDrawHand.value!!
    }

    fun startTimer(){
        _countDownTimer = object : CountDownTimer(HAND_DETECTING_TIME_IN_MILLI, 100) {
            override fun onTick(millisUntilFinished: Long) {
                _handGestureProgress.value = (100 * ( 1 - millisUntilFinished / HAND_DETECTING_TIME_IN_MILLI.toDouble())).toInt()
            }

            override fun onFinish() {
                // activate the self-timer
                _timerTrigger.value = !(timerTrigger.value ?: false)
                cancelTimer()
            }
        }

        _countDownTimer?.start()
    }

    fun cancelTimer(){
        _handGestureProgress.value = 0
        _countDownTimer?.cancel()
    }

    fun setIsDetecting(value : Boolean){
        _isDetecting = value
    }

    fun isDetecting() : Boolean{
        return _isDetecting
    }
}