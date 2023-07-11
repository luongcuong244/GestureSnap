package com.nlc.gesturesnap.screen.capture.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GridViewModel : ViewModel() {
    private val _isOpenGrid = MutableLiveData<Boolean>().apply {
        value = false
    }

    val isOpenGrid : LiveData<Boolean> = _isOpenGrid

    fun switchValue(){
        _isOpenGrid.value = !isOpenGrid.value!!
    }
}