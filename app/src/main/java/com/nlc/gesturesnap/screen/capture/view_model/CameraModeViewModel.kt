package com.nlc.gesturesnap.screen.capture.view_model

import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.screen.capture.ui.value.CameraOrientation
import com.nlc.gesturesnap.screen.capture.ui.value.FlashOption

class CameraModeViewModel : ViewModel() {

    private val _cameraAspectRatio = MutableLiveData<Int>().apply {
        this.value = AspectRatio.RATIO_16_9
    }
    private val _flashOption = MutableLiveData<FlashOption>().apply {
        value = FlashOption.AUTO
    }
    private val _isOpenGrid = MutableLiveData<Boolean>().apply {
        value = false
    }
    private val _cameraOrientation = MutableLiveData<CameraOrientation>().apply {
        value = CameraOrientation.FRONT
    }

    private var shouldRefreshCamera = true

    val cameraAspectRatio: LiveData<Int> = _cameraAspectRatio
    val flashOption: LiveData<FlashOption> = _flashOption
    val isOpenGrid : LiveData<Boolean> = _isOpenGrid
    val cameraOrientation: LiveData<CameraOrientation> = _cameraOrientation

    fun switchAspectRatio(){
        if(!shouldRefreshCamera){
            return
        }
        shouldRefreshCamera = false

        if(_cameraAspectRatio.value == AspectRatio.RATIO_4_3){
            _cameraAspectRatio.value = AspectRatio.RATIO_16_9
        }else {
            _cameraAspectRatio.value = AspectRatio.RATIO_4_3
        }
    }

    fun switchFlashMode(option : FlashOption){
        if(!shouldRefreshCamera){
            return
        }
        shouldRefreshCamera = false

        val isSelecting = _flashOption.value?.equals(option) ?: false

        if(!isSelecting){
            _flashOption.value = option
        }
    }

    fun switchGridMode(){
        _isOpenGrid.value = !isOpenGrid.value!!
    }

    fun switchCameraOrientation(){
        if(!shouldRefreshCamera){
            return
        }
        shouldRefreshCamera = false

        val index = CameraOrientation.values().indexOf(cameraOrientation.value)
        val size = CameraOrientation.values().size

        _cameraOrientation.value = CameraOrientation.values()[(index + 1) % size]
    }

    fun setShouldRefreshCamera(value : Boolean){
        shouldRefreshCamera = value
    }
}