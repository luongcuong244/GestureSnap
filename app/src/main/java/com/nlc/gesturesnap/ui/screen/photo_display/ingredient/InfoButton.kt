package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.component.SquareIconButton
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun InfoButton(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){
    SquareIconButton(
        icon = painterResource(R.drawable.ic_more_info),
        iconColor = colorResource(R.color.blue),
    ) {
        photoDisplayViewModel.setPhotoDetailDialogVisible(true)
    }
}