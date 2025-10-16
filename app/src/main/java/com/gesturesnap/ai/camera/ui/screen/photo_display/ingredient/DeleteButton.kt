package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.ui.component.SquareIconButton
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun DeleteButton(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()) {

    SquareIconButton(
        icon = painterResource(R.drawable.ic_garbage_bin),
        iconColor = colorResource(R.color.blue),
    ) {
        photoDisplayViewModel.setIsPhotoDeletionDialogVisible(true)
    }
}