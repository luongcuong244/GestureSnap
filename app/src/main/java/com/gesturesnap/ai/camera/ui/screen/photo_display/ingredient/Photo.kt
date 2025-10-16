package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel
import java.io.File

@Composable
fun BoxScope.Photo(
    screenSizePx: IntSize,
    photoDisplayViewModel: PhotoDisplayViewModel = viewModel()
) {
    val density = LocalDensity.current
    val screenWidthDp = with(density) { screenSizePx.width.toDp() }
    val screenHeightDp = with(density) { screenSizePx.height.toDp() }

    val screenRatio = remember {
        screenSizePx.width.toFloat() / screenSizePx.height
    }

    val photo = photoDisplayViewModel.photoInfo.value
    val imagePainter = rememberAsyncImagePainter(model = File(photo.path))

    val photoRatio = remember { mutableStateOf(1f) }
    val isFitWidth = remember { mutableStateOf(true) }

    // Đọc kích thước thật của ảnh để tính tỉ lệ
    LaunchedEffect(photo.path) {
        if (photo.path.isNotEmpty()) {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(photo.path, options)

            if (options.outWidth != 0 && options.outHeight != 0) {
                photoRatio.value = options.outWidth.toFloat() / options.outHeight
                isFitWidth.value = photoRatio.value > screenRatio
            }
        }
    }

    // Animation nhỏ khi load ảnh
    val size by animateDpAsState(
        targetValue = if (isFitWidth.value) screenWidthDp else screenHeightDp,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    val aspectRatio by animateFloatAsState(
        targetValue = photoRatio.value,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    // Hiển thị ảnh
    Image(
        painter = imagePainter,
        contentDescription = "Photo",
        modifier = Modifier
            .align(Alignment.Center)
            .then(
                if (isFitWidth.value)
                    Modifier.width(size)
                else
                    Modifier.height(size)
            )
            .aspectRatio(aspectRatio),
        contentScale = ContentScale.Fit
    )
}