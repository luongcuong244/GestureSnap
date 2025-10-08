package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun Background(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val isFragmentOpen = remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if(isFragmentOpen.value) 1f else 0f,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    LaunchedEffect(photoDisplayViewModel.isFragmentOpen.observeAsState(false).value){
        photoDisplayViewModel.isFragmentOpen.value?.let {
            isFragmentOpen.value = it
        }
    }

    val backgroundColor =
        if(photoDisplayViewModel.isOnlyDisplayPhoto.value)
            Color.Black
        else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        )
    }
}