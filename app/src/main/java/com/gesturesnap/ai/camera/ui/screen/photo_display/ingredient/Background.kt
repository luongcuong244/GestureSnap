package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun Background(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()) {
    val backgroundColor =
        if (photoDisplayViewModel.isOnlyDisplayPhoto.value)
            Color.Black
        else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        )
    }
}