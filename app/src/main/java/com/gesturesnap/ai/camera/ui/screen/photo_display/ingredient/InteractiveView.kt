package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

enum class InteractedObject {
    InteractiveView,
    ImageView
}

@Composable
fun InteractiveView(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val screenSizePx = remember {
        mutableStateOf(IntSize.Zero)
    }

    val photoPositionX = remember {
        mutableStateOf(0f)
    }

    val photoPositionY = remember {
        mutableStateOf(0f)
    }

    val photoWidth = remember {
        mutableStateOf(1)
    }

    val photoHeight = remember {
        mutableStateOf(1)
    }

    val shouldRunAnimation = remember {
        mutableStateOf(false)
    }

    val zoomValue = remember { mutableStateOf(1f) }

    val animatedZoomValue = animateFloatAsState(
        targetValue = zoomValue.value,
        animationSpec = tween(
            durationMillis = AppConstant.ANIMATION_DURATION_MILLIS
        ), label = ""
    )

    val zoomOffset = remember { mutableStateOf(Offset.Zero) }

    val animatedZoomOffset = animateOffsetAsState(
        targetValue = zoomOffset.value,
        animationSpec = tween(
            durationMillis = AppConstant.ANIMATION_DURATION_MILLIS
        ), label = ""
    )

    val systemUiController = rememberSystemUiController()

    val onTap = remember {
        { _: Offset, _: InteractedObject ->

            val isOnlyDisplayPhoto = !photoDisplayViewModel.isOnlyDisplayPhoto.value

            systemUiController.isSystemBarsVisible = !isOnlyDisplayPhoto

            photoDisplayViewModel.setIsOnlyDisplayPhoto(isOnlyDisplayPhoto)
        }
    }

    val onDoubleTap = remember {
        { offset: Offset, interactedObject: InteractedObject ->
            when(interactedObject){
                InteractedObject.InteractiveView -> {
                    onTap(offset, interactedObject)
                }
                InteractedObject.ImageView -> {
                    shouldRunAnimation.value = true

                    val isNormalSize = zoomValue.value == 1f

                    zoomValue.value = if(isNormalSize) AppConstant.MAX_PHOTO_SCALE else 1f

                    if(isNormalSize){
                        // calculate offset
                    } else {
                        zoomOffset.value = Offset.Zero
                    }
                }
            }
        }
    }

    val calculateAddedOffset = remember {
        { positionChange : Offset ->

            val canMoveToRight = photoPositionX.value < 0
            val canMoveToLeft =
                photoPositionX.value + photoWidth.value.times(zoomValue.value) > screenSizePx.value.width
            val canMoveToBottom = photoPositionY.value < 0
            val canMoveToTop =
                photoPositionY.value + photoHeight.value.times(zoomValue.value) > screenSizePx.value.height

            var addedOffset = Offset(0f, 0f)

            // moving to right
            if (positionChange.x > 0 && canMoveToRight) {
                addedOffset = addedOffset.plus(Offset(positionChange.x, 0f))

                // distance between the left edge of the screen and the left edge of the photo after drag
                val distance = photoPositionX.value + positionChange.x

                // if the left edge of the photo insides the screen
                if(distance > 0){
                    addedOffset = addedOffset.minus(Offset(distance, 0f))
                }
            }

            // moving to left
            if (positionChange.x < 0 && canMoveToLeft) {
                addedOffset = addedOffset.plus(Offset(positionChange.x, 0f))

                // distance between the left edge of the screen and the right edge of the photo after drag
                val distance = photoPositionX.value + photoWidth.value.times(zoomValue.value) + positionChange.x

                // if the right edge of the photo insides the screen
                if(distance < screenSizePx.value.width){
                    addedOffset = addedOffset.plus(Offset(screenSizePx.value.width - distance, 0f))
                }
            }

            // moving to bottom
            if (positionChange.y > 0 && canMoveToBottom) {
                addedOffset = addedOffset.plus(Offset(0f, positionChange.y))

                // distance between the top edge of the screen and the top edge of the photo after drag
                val distance = photoPositionY.value + addedOffset.y

                // if the top edge of the photo insides the screen
                if(distance > 0){
                    addedOffset = addedOffset.minus(Offset(0f, distance))
                }
            }

            // moving to top
            if (positionChange.y < 0 && canMoveToTop) {
                addedOffset = addedOffset.plus(Offset(0f, positionChange.y))

                // distance between the top edge of the screen and the bottom edge of the photo after drag
                val distance = photoPositionY.value + photoHeight.value.times(zoomValue.value) + positionChange.y

                // if the bottom edge of the photo insides the screen
                if(distance < screenSizePx.value.height){
                    addedOffset = addedOffset.plus(Offset(0f, screenSizePx.value.height - distance))
                }
            }

            addedOffset
        }
    }

    val onDrag = { positionChange : Offset ->

        shouldRunAnimation.value = false

        val addedOffset = calculateAddedOffset(positionChange)
        zoomOffset.value =
            zoomOffset.value.plus(addedOffset)
    }

    val onGesture = { _: Offset, pan: Offset, zoom: Float, _: Float ->
        shouldRunAnimation.value = false

        val informalZoomValue = zoomValue.value * zoom

        zoomValue.value = when {
            informalZoomValue < AppConstant.MIN_PHOTO_SCALE -> AppConstant.MIN_PHOTO_SCALE
            informalZoomValue > AppConstant.MAX_PHOTO_SCALE -> AppConstant.MAX_PHOTO_SCALE
            else -> informalZoomValue
        }

        onDrag(pan)
    }

    LaunchedEffect(photoDisplayViewModel.isFragmentOpen.observeAsState(false).value){
        photoDisplayViewModel.isFragmentOpen.value?.let {
            if(!it){
                shouldRunAnimation.value = true
                zoomOffset.value = Offset.Zero
                zoomValue.value = 1f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenSizePx.value = it.size
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTap(it, InteractedObject.InteractiveView)
                    },
                    onDoubleTap = {
                        onDoubleTap(it, InteractedObject.InteractiveView)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = onGesture
                )
            }
    ) {
        Background()
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onTap(it, InteractedObject.ImageView)
                        },
                        onDoubleTap = {
                            onDoubleTap(it, InteractedObject.ImageView)
                        }
                    )
                }
                .graphicsLayer {
                    scaleX =
                        if (shouldRunAnimation.value) animatedZoomValue.value else zoomValue.value
                    scaleY =
                        if (shouldRunAnimation.value) animatedZoomValue.value else zoomValue.value
                    translationX =
                        if (shouldRunAnimation.value) animatedZoomOffset.value.x else zoomOffset.value.x
                    translationY =
                        if (shouldRunAnimation.value) animatedZoomOffset.value.y else zoomOffset.value.y
                }
                .onGloballyPositioned {
                    photoPositionX.value = it.positionInParent().x
                    photoPositionY.value = it.positionInParent().y
                    photoWidth.value = it.size.width
                    photoHeight.value = it.size.height
                }
        ) {
            if(screenSizePx.value != IntSize.Zero){
                Photo(screenSizePx.value)
            }
        }
    }
}