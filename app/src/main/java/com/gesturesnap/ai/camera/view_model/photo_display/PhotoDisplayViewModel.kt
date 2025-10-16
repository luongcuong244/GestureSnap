package com.gesturesnap.ai.camera.view_model.photo_display

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gesturesnap.ai.camera.listener.PhotoDeleteListener
import com.gesturesnap.ai.camera.model.PhotoInfo

class PhotoDisplayViewModel : ViewModel() {

    var photoDeleteListener: PhotoDeleteListener? = null

    var photoInfo = mutableStateOf(PhotoInfo())

    val isPhotoDeletionDialogVisible = mutableStateOf(false)

    val isOnlyDisplayPhoto = mutableStateOf(false)

    val isPhotoDetailDialogVisible = mutableStateOf(false)

    fun setPhotoInfo(photoInfo: PhotoInfo) {
        this.photoInfo.value = photoInfo
    }

    fun setIsOnlyDisplayPhoto(value: Boolean) {
        isOnlyDisplayPhoto.value = value
    }

    fun setPhotoDetailDialogVisible(value: Boolean) {
        isPhotoDetailDialogVisible.value = value
    }

    fun setIsPhotoDeletionDialogVisible(visible: Boolean) {
        isPhotoDeletionDialogVisible.value = visible
    }
}