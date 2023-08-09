package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun Background(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val isFragmentOpen = remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if(isFragmentOpen.value) 1f else 0f,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    LaunchedEffect(photoDisplayViewModel.isFragmentOpen.value){
        isFragmentOpen.value = photoDisplayViewModel.isFragmentOpen.value
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
        )
    }
}