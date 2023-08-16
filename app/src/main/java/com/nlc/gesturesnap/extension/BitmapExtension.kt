package com.nlc.gesturesnap.extension

import android.graphics.Bitmap
import android.graphics.Matrix


fun Bitmap.rotate(angle: Float) : Bitmap{

    if(angle == 0f){
        return this
    }

    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Bitmap.flipHorizontally() : Bitmap{
    val matrix = Matrix().apply {
        postScale(
            -1f, 1f, this@flipHorizontally.width.toFloat(), this@flipHorizontally.height.toFloat()
        )
    }
    return Bitmap.createBitmap(this, 0, 0,this.width, this.height, matrix, true)
}