package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import android.os.Build
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.component.SquareIconButton

@Composable
fun DeleteButton(){

    SquareIconButton(
        icon = painterResource(R.drawable.ic_garbage_bin),
        iconColor = colorResource(R.color.blue),
    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            activityActions.deletePhotosWithApi30orLater(
//                galleryViewModel.photos.filter {
//                    it.isSelecting
//                }.map {
//                    it.uri
//                }
//            )
//        } else {
//            galleryViewModel.setIsPhotoDeletionDialogVisible(true)
//        }
        // onClick
    }
}