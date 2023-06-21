package com.nlc.gesturesnap.screen.capture.view_model

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.capture.model.GestureDetectOption
import com.nlc.gesturesnap.screen.capture.ui.enums.GestureCategory

class GestureDetectViewModel : ViewModel() {

    private var _currentHandGesture = MutableLiveData<GestureDetectOption>()
    val handGestureOptions = MutableLiveData<List<GestureDetectOption>>()

    val currentHandGesture : LiveData<GestureDetectOption> = _currentHandGesture

    fun createOptionList(context: Context){

        if(handGestureOptions.value?.isNotEmpty() == true)
            return

        val gestureDetectOptions = ArrayList<GestureDetectOption>()

        ContextCompat.getDrawable(context, R.drawable.ic_select_all)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.all_text), GestureCategory.ALl)
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
                GestureDetectOption(it, context.getString(R.string.closed_fist_text), GestureCategory.CLOSED_FIST)
            }?.let {
                gestureDetectOptions.add(
                    it
            )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_hand_palm_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.open_palm_text), GestureCategory.OPEN_PALM)
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_pointing_up_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.pointing_up_text), GestureCategory.POINTING_UP)
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_thumbs_up_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.thumb_up_text), GestureCategory.THUMB_UP)
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_thumb_down_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.thumb_down_text), GestureCategory.THUMB_DOWN)
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_victory_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.victory_text), GestureCategory.VICTORY)
            }?.let {
                gestureDetectOptions.add(
                    it
                )
            }

        ContextCompat.getDrawable(context, R.drawable.ic_hand_love_outline)
            ?.let {
                GestureDetectOption(it, context.getString(R.string.love_text), GestureCategory.LOVE)
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
}