package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.ui.component.SquareIconButton
import com.gesturesnap.ai.camera.ui.screen.edit.EditPhotoActivity
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

@Composable
fun EditButton(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()) {
    val context = LocalContext.current

    SquareIconButton(
        icon = painterResource(R.drawable.ic_edit),
        iconColor = colorResource(R.color.blue),
    ) {
        val path = photoDisplayViewModel.photoInfo.value.path
        (context as? Activity)?.startActivity(Intent(context, EditPhotoActivity::class.java).apply {
            putExtra("photo_path", path)
        })
    }
}