package com.nlc.gesturesnap.view_model.photo_display

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.listener.PhotoDeleteListener
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment

class PhotoDisplayViewModel : ViewModel() {

    var photoDeleteListener : PhotoDeleteListener? = null

    val isPhotoDeletionDialogVisible = mutableStateOf(false)

    val isFragmentOpen = MutableLiveData(false)

    val fragmentArgument = mutableStateOf(PhotoDisplayFragment.Argument())

    val isOnlyDisplayPhoto = mutableStateOf(false)

    val isPhotoDetailDialogVisible = mutableStateOf(false)

    fun setFragmentArgument(argument: PhotoDisplayFragment.Argument){
        this.fragmentArgument.value = argument
    }

    fun setIsFragmentOpen(isOpen : Boolean){
        isFragmentOpen.value = isOpen
    }

    fun setIsOnlyDisplayPhoto(value: Boolean){
        isOnlyDisplayPhoto.value = value
    }

    fun setPhotoDetailDialogVisible(value: Boolean){
        isPhotoDetailDialogVisible.value = value
    }

    fun setIsPhotoDeletionDialogVisible(visible: Boolean){
        isPhotoDeletionDialogVisible.value = visible
    }
}