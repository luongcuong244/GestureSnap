package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.component.SquareIconButton
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun DeleteButton(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    SquareIconButton(
        icon = painterResource(R.drawable.ic_garbage_bin),
        iconColor = colorResource(R.color.blue),
    ) {

        val uri = photoDisplayViewModel.fragmentArgument.value.photo.uri

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            photoDisplayViewModel.photoDeleteListener?.deletePhotosWithApi30orLater(uri)
        } else {
            photoDisplayViewModel.setIsPhotoDeletionDialogVisible(true)
        }
    }
}