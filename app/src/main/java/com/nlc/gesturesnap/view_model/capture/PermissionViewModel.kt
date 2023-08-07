package com.nlc.gesturesnap.view_model.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {

    private val _isCameraPermissionGranted = MutableLiveData<Boolean>()
    private val _isStoragePermissionGranted = MutableLiveData<Boolean>()

    private val _isCameraPermissionDialogShowing = MutableLiveData<Boolean>()
    private val _isStoragePermissionDialogShowing = MutableLiveData<Boolean>()

    private val _isCameraPermissionTipDialogShowing = MutableLiveData<Boolean>()
    private val _isStoragePermissionTipDialogShowing = MutableLiveData<Boolean>()

    val isCameraPermissionGranted : LiveData<Boolean> = _isCameraPermissionGranted
    val isStoragePermissionGranted : LiveData<Boolean> = _isStoragePermissionGranted

    val isCameraPermissionDialogShowing : LiveData<Boolean> = _isCameraPermissionDialogShowing
    val isStoragePermissionDialogShowing : LiveData<Boolean> = _isStoragePermissionDialogShowing

    val isCameraPermissionTipDialogShowing : LiveData<Boolean> = _isCameraPermissionTipDialogShowing
    val isStoragePermissionTipDialogShowing : LiveData<Boolean> = _isStoragePermissionTipDialogShowing

    fun setCameraPermissionGranted(value: Boolean){
        if(_isCameraPermissionGranted.value == value){
            return
        }
        _isCameraPermissionGranted.value = value
    }

    fun setStoragePermissionGranted(value: Boolean){
        if(_isStoragePermissionGranted.value == value){
            return
        }
        _isStoragePermissionGranted.value = value
    }

    fun setCameraPermissionDialogShowing(value : Boolean){
        _isCameraPermissionDialogShowing.value = value
    }

    fun setStoragePermissionDialogShowing(value : Boolean){
        _isStoragePermissionDialogShowing.value = value
    }

    fun setCameraPermissionTipDialogShowing(value : Boolean){
        _isCameraPermissionTipDialogShowing.value = value
    }

    fun setStoragePermissionTipDialogShowing(value : Boolean){
        _isStoragePermissionTipDialogShowing.value = value
    }
}