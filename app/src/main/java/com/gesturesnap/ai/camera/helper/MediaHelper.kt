package com.gesturesnap.ai.camera.helper

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.database.getIntOrNull
import com.gesturesnap.ai.camera.model.PhotoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.util.Date

object MediaHelper {

    fun createPhotoUri(context: Context, directory: String, fileName: String): Uri? {

        val images: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues()

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        if (directory.isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, directory)
        }

        return context.contentResolver.insert(images, contentValues)
    }

    fun savePhoto(context: Context, photoBitmap: Bitmap, uri: Uri?): Boolean {
        return try {
            val outputStream = uri?.let { it -> context.contentResolver.openOutputStream(it) }
            if (outputStream != null) {
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            outputStream?.flush()
            outputStream?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getLatestPhotoPath(context: Context): String? {
        if (!PermissionHelper.isReadExternalStoragePermissionGranted(context)) {
            return null
        }

        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )

        val cursor =
            context.contentResolver.query(uri, projection, null, null, null)
                ?: return null

        if (!cursor.moveToLast()) {
            return null
        }

        val filePath = cursor.getString(0)

        cursor.close()
        return filePath
    }

    suspend fun getAllPhotos(context: Context): List<PhotoInfo> = withContext(Dispatchers.IO) {
        if (!PermissionHelper.isReadExternalStoragePermissionGranted(context)) {
            return@withContext emptyList()
        }

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
            ?: return@withContext emptyList()

        val photos = mutableListOf<PhotoInfo>()

        val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
        val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
        val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

        var count = 0
        while (cursor.moveToNext()) {
            val photoPath = cursor.getString(pathIndex)
            val file = File(photoPath)
            if (!file.exists()) continue

            val photoUri = ContentUris.withAppendedId(uri, cursor.getLong(idIndex))
            val dateTaken = cursor.getLong(dateTakenIndex)
            val width = cursor.getIntOrNull(widthIndex) ?: 0
            val height = cursor.getIntOrNull(heightIndex) ?: 0

            photos.add(
                PhotoInfo(
                    path = photoPath,
                    uri = photoUri,
                    name = file.name,
                    size = file.length(),
                    dateTaken = Date(if (dateTaken > 0) dateTaken else file.lastModified()),
                    resolution = Size(width, height)
                )
            )

            count++
            if (count >= 2000) break // 2000 photos limited
        }

        cursor.close()
        photos
    }
}