package com.nlc.gesturesnap.screen.photo_display.view_model

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.model.PhotoInfo

class PhotoDisplayViewModel : ViewModel() {
    val photoInfo = mutableStateOf(PhotoInfo("", Uri.EMPTY))

    fun setPhotoInfo(photoInfo: PhotoInfo){
        this.photoInfo.value = photoInfo
    }
}