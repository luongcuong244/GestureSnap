package com.nlc.gesturesnap.screen.capture.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecentPhotoViewModel : ViewModel() {
    private val _recentPhoto = MutableLiveData<Bitmap>()

    val recentPhoto : LiveData<Bitmap> = _recentPhoto

    fun setRecentPhoto(bitmap: Bitmap){
        _recentPhoto.value = bitmap
    }
}