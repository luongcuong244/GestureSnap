package com.gesturesnap.ai.camera.listener

import android.net.Uri

interface PhotoDeleteListener {
    fun deletePhotosWithApi30orLater(photoPath: String)
    fun deletePhotoWithApi29(photoPath: String)
    fun deletePhotosWithApi28orOlder(photoPath: String)
}