package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.ui.component.SquareIconButton
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun InfoButton(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){
    SquareIconButton(
        icon = painterResource(R.drawable.ic_more_info),
        iconColor = colorResource(R.color.blue),
    ) {
        photoDisplayViewModel.setPhotoDetailDialogVisible(true)
    }
}