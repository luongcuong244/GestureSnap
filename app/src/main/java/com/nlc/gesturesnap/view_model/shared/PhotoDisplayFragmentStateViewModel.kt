package com.nlc.gesturesnap.view_model.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoDisplayFragmentStateViewModel : ViewModel() {
    enum class State {
        PREPARE_CLOSE,
        CLOSED
    }

    private val _photoDisplayFragmentState = MutableLiveData<State>()

    val photoDisplayFragmentState: LiveData<State> = _photoDisplayFragmentState

    fun setState(state: State){
        _photoDisplayFragmentState.value = state
    }
}