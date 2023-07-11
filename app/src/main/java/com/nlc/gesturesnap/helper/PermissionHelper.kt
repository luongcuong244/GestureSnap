package com.nlc.gesturesnap.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object PermissionHelper {

    fun isExternalStoragePermissionGranted(context: Context) : Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            return context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        }
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionGranted(context: Context) : Boolean{
        return context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
}