package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel
import java.io.File
import kotlin.math.roundToInt

@Composable
fun BoxScope.Photo(screenSizePx: IntSize, photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    // for showing the photo

    val density = LocalDensity.current

    val screenWidthDp = with(density){
        screenSizePx.width.toDp()
    }
    val screenHeightDp = with(density){
        screenSizePx.height.toDp()
    }

    val screenRatio = remember {
        screenSizePx.width / screenSizePx.height
    }

    val imageBitmap = rememberAsyncImagePainter(model = File(photoDisplayViewModel.fragmentArgument.value.photo.path))

    val photoRatio = remember { mutableStateOf(1f) }
    val isFitWidth = remember { mutableStateOf(true) }

    LaunchedEffect(photoDisplayViewModel.fragmentArgument.value.photo.path) {

        val photo = photoDisplayViewModel.fragmentArgument.value.photo

        if(photo.path.isNotEmpty()){
            val options = BitmapFactory.Options().apply {
                this.inJustDecodeBounds = true
            }

            BitmapFactory.decodeFile(photo.path, options)

            if(options.outWidth != 0 && options.outHeight != 0){
                photoRatio.value = options.outWidth.toFloat() / options.outHeight
                isFitWidth.value = photoRatio.value > screenRatio
            }
        }
    }

    val expectedOffset = remember {
        calculateOffset(screenSizePx, density, photoDisplayViewModel)
    }

    // for performing animation

    val isFragmentOpen = remember { mutableStateOf(false) }

    val aspectRatio by animateFloatAsState(
        targetValue = if(isFragmentOpen.value)
            photoRatio.value
        else
            photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.width / photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.height,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    val size by animateDpAsState(
        targetValue = if(isFitWidth.value)
            if(isFragmentOpen.value) screenWidthDp else photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.width
        else
            if(isFragmentOpen.value) screenHeightDp else photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.height,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    val offset by animateIntOffsetAsState(
        targetValue =
            if(isFragmentOpen.value)
                IntOffset.Zero
            else
                expectedOffset,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = ""
    )

    LaunchedEffect(photoDisplayViewModel.isFragmentOpen.value){
        isFragmentOpen.value = photoDisplayViewModel.isFragmentOpen.value
    }

    Image(
        painter = imageBitmap,
        contentDescription = "Photo",
        modifier = (if (isFitWidth.value) Modifier.width(size) else Modifier.height(size))
            .aspectRatio(aspectRatio)
            .align(Alignment.Center)
            .offset {
                offset
            },
        contentScale = ContentScale.FillBounds
    )
}

// because the Image is always in the center of the screen, so we can easily calculate the offset
fun calculateOffset(screenSizePx: IntSize, density: Density, photoDisplayViewModel: PhotoDisplayViewModel): IntOffset {

    val screenCenterPosition = Offset(
        (screenSizePx.width / 2).toFloat(),
        (screenSizePx.height / 2).toFloat()
    )

    val photoWidthPx = with(density) {
        photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.width.value.dp.toPx()
    }

    val photoHeightPx = with(density) {
        photoDisplayViewModel.fragmentArgument.value.initialPhotoSize.height.value.dp.toPx()
    }

    // the position of the Image
    val currentPhotoPosition = IntOffset(
        (screenCenterPosition.x - photoWidthPx / 2).roundToInt(),
        (screenCenterPosition.y - photoHeightPx / 2).roundToInt()
    )

    val initialPhotoPosition = IntOffset(
        photoDisplayViewModel.fragmentArgument.value.initialPhotoPosition.x.roundToInt(),
        photoDisplayViewModel.fragmentArgument.value.initialPhotoPosition.y.roundToInt()
    )

    return initialPhotoPosition.minus(currentPhotoPosition)
}