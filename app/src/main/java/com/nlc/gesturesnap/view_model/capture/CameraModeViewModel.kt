package com.nlc.gesturesnap.view_model.capture

import android.app.Application
import androidx.camera.core.AspectRatio
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.helper.LocalStorageHelper
import com.nlc.gesturesnap.model.enums.CameraOrientation
import com.nlc.gesturesnap.model.enums.FlashOption

class CameraModeViewModel(application: Application) : AndroidViewModel(application) {

    private val _cameraAspectRatio = MutableLiveData<Int>()
    private val _flashOption = MutableLiveData<FlashOption>()
    private val _isOpenGrid = MutableLiveData<Boolean>()
    private val _cameraOrientation = MutableLiveData<CameraOrientation>()

    private var shouldRefreshCamera = true

    val cameraAspectRatio: LiveData<Int> = _cameraAspectRatio
    val flashOption: LiveData<FlashOption> = _flashOption
    val isOpenGrid : LiveData<Boolean> = _isOpenGrid
    val cameraOrientation: LiveData<CameraOrientation> = _cameraOrientation

    fun switchAndSaveAspectRatio(){
        if(!shouldRefreshCamera){
            return
        }
        shouldRefreshCamera = false

        if(_cameraAspectRatio.value == AspectRatio.RATIO_4_3){
            setAndSaveAspectRatio(AspectRatio.RATIO_16_9)
        }else {
            setAndSaveAspectRatio(AspectRatio.RATIO_4_3)
        }
    }

    fun setAndSaveAspectRatio(newValue: Int){

        _cameraAspectRatio.value = newValue

        LocalStorageHelper.writeData(
            getApplication(),
            AppConstant.ASPECT_RATIO_MODE_VALUE_KEY,
            newValue
        )
    }

    fun setAndSaveFlashMode(option : FlashOption){
        val isSelecting = _flashOption.value?.equals(option) ?: false

        if(!isSelecting){
            _flashOption.value = option
        }

        LocalStorageHelper.writeData(
            getApplication(),
            AppConstant.FLASH_MODE_INDEX_KEY,
            FlashOption.values().indexOf(option)
        )
    }

    fun switchAndSaveGridMode(){
        val newValue = !(isOpenGrid.value ?: false)
        setAndSaveGridMode(newValue)
    }

    fun setAndSaveGridMode(newValue: Boolean){

        _isOpenGrid.value = newValue

        LocalStorageHelper.writeData(
            getApplication(),
            AppConstant.GRID_MODE_VALUE_KEY,
            newValue
        )
    }

    fun switchAndSaveCameraOrientation(){
        if(!shouldRefreshCamera){
            return
        }
        shouldRefreshCamera = false

        val index = CameraOrientation.values().indexOf(cameraOrientation.value)
        val size = CameraOrientation.values().size

        val newIndex = (index + 1) % size

        setAndSaveCameraOrientation(CameraOrientation.values()[newIndex])
    }

    fun setAndSaveCameraOrientation(newValue: CameraOrientation){

        _cameraOrientation.value = newValue

        LocalStorageHelper.writeData(
            getApplication(),
            AppConstant.CAMERA_ORIENTATION_INDEX_KEY,
            CameraOrientation.values().indexOf(newValue)
        )
    }

    fun setShouldRefreshCamera(value : Boolean){
        shouldRefreshCamera = value
    }
}