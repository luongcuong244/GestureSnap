package com.gesturesnap.ai.camera.listener

import android.net.Uri

interface PhotoDeleteListener {
    fun deletePhotosWithApi30orLater(photoUri: Uri)
    fun deletePhotoWithApi29(photoUri: Uri)
    fun deletePhotosWithApi28orOlder(photoUri: Uri)
}