package com.gesturesnap.ai.camera.view_model.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.Formatter
import com.gesturesnap.ai.camera.helper.MediaHelper


class RecentPhotoViewModel : ViewModel() {
    private val _recentPhoto = MutableLiveData<Bitmap>()

    val recentPhoto : LiveData<Bitmap> = _recentPhoto

    fun updateRecentPhoto(context: Context){
        val photoPath = MediaHelper.getLatestPhotoPath(context)

        val bitmap = if(photoPath != null){
            BitmapFactory.decodeFile(photoPath)
        } else {
            val d: Drawable? = ContextCompat.getDrawable(context, R.drawable.gallery_frame)
            if (d != null) {
                Formatter.drawableToBitmap(d)
            } else {
                null
            }
        }

        bitmap?.let {
            setRecentPhoto(bitmap)
        }
    }
    fun setRecentPhoto(bitmap: Bitmap){
        _recentPhoto.value = bitmap
    }
}