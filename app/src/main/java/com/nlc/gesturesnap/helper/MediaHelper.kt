package com.nlc.gesturesnap.helper

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.nlc.gesturesnap.model.PhotoInfo
import java.io.File
import java.util.Date

object MediaHelper {

    fun createPhotoUri(context: Context, directory: String, fileName: String) : Uri? {

        val images: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues()

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        if(directory.isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, directory)
        }

        return context.contentResolver.insert(images, contentValues)
    }

    fun savePhoto(context: Context, photoBitmap: Bitmap, uri: Uri?): Boolean {
        return try {
            val outputStream = uri?.let { it -> context.contentResolver.openOutputStream(it) }
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.flush()
            outputStream?.close()
            true
        } catch (e: java.lang.Exception){
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

    fun getAllPhotos(context: Context) : List<PhotoInfo>{

        if (!PermissionHelper.isReadExternalStoragePermissionGranted(context)) {
            return emptyList()
        }

        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val cursor =
            context.contentResolver.query(uri, projection, null, null, null)
                ?: return emptyList()

        val photos = mutableListOf<PhotoInfo>()

        while (cursor.moveToNext()) {

            val imageId = cursor.getLong(0)
            val photoUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageId
            )

            val photoPath = cursor.getString(1)

            val file = File(photoPath)

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoPath, options)

            var photoResolution = Size(0, 0)

            if(options.outHeight > 0 && options.outWidth > 0){
                photoResolution = Size(options.outWidth, options.outHeight)
            }

            val dateTaken = cursor.getLong(2)

            photos.add(
                PhotoInfo(
                    path = photoPath,
                    uri = photoUri,
                    name = file.name,
                    size = file.length(),
                    dateTaken = Date(if(dateTaken > 0) dateTaken else file.lastModified()),
                    resolution = photoResolution
                )
            )

            Log.d("DAGDAGGD","DATA: ${if(dateTaken > 0) dateTaken else file.lastModified()}" )
        }
        cursor.close()

        return photos
    }
}