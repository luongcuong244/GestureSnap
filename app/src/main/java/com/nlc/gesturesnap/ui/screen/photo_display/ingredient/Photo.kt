package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel
import java.io.File

@Composable
fun BoxScope.Photo(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    val screenRatio = width.toFloat() / height

    val imageBitmap = rememberAsyncImagePainter(model = File(photoDisplayViewModel.shownPhoto.value.path))

    val photoRatio = remember { mutableStateOf(1f) }
    val isFitWidth = remember { mutableStateOf(true) }

    LaunchedEffect(photoDisplayViewModel.shownPhoto.value.path) {
        if(photoDisplayViewModel.shownPhoto.value.path.isNotEmpty()){
            val options = BitmapFactory.Options().apply {
                this.inJustDecodeBounds = true
            }

            BitmapFactory.decodeFile(photoDisplayViewModel.shownPhoto.value.path, options)

            if(options.outWidth != 0 && options.outHeight != 0){
                photoRatio.value = options.outWidth.toFloat() / options.outHeight
                isFitWidth.value = photoRatio.value > screenRatio
            }
        }
    }

    Image(
        painter = imageBitmap,
        contentDescription = "Photo",
        modifier = (if (isFitWidth.value) Modifier.fillMaxWidth() else Modifier.fillMaxHeight())
            .aspectRatio(photoRatio.value)
            .align(Alignment.Center),
        contentScale = ContentScale.Crop
    )
}