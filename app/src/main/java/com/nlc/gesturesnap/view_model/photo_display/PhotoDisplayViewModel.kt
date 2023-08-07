package com.nlc.gesturesnap.view_model.photo_display

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.model.PhotoInfo

class PhotoDisplayViewModel : ViewModel() {
    val shownPhoto = mutableStateOf(PhotoInfo())

    fun setPhotoInfo(photoInfo: PhotoInfo){
        this.shownPhoto.value = photoInfo
    }
}