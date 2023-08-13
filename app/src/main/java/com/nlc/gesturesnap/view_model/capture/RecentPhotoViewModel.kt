package com.nlc.gesturesnap.view_model.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.MediaHelper

class RecentPhotoViewModel : ViewModel() {
    private val _recentPhoto = MutableLiveData<Bitmap>()

    val recentPhoto : LiveData<Bitmap> = _recentPhoto

    fun updateRecentPhoto(context: Context){
        val photoPath = MediaHelper.getLatestPhotoPath(context)

        val bitmap = if(photoPath != null){
            BitmapFactory.decodeFile(photoPath)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.gallery_frame)
        }

        bitmap?.let {
            setRecentPhoto(bitmap)
        }
    }
    fun setRecentPhoto(bitmap: Bitmap){
        _recentPhoto.value = bitmap
    }
}