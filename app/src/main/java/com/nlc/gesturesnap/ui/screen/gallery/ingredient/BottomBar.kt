package com.nlc.gesturesnap.ui.screen.gallery.ingredient

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.ui.component.SquareIconButton
import com.nlc.gesturesnap.ui.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.ui.screen.gallery.bottomBarHeight
import com.nlc.gesturesnap.view_model.gallery.GalleryViewModel

@Composable
fun BoxScope.BottomBar(activityActions: GalleryActivity.Actions, translationYValue: Dp){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .align(Alignment.BottomEnd)
            .offset(0.dp, translationYValue)
            .background(color = colorResource(R.color.gray_white)) // note the order of background method and padding method
            .drawBehind {
                val strokeWidth = 2f

                drawLine(
                    Color.LightGray,
                    Offset(0f, 0f),
                    Offset(size.width, 0f),
                    strokeWidth
                )
            }
            .padding(start = 15.dp, end = 5.dp),
    ) {
        SelectedItemsText()
        DeleteButton(activityActions)
    }
}

@Composable
fun BoxScope.SelectedItemsText(galleryViewModel: GalleryViewModel = viewModel()){
    Text(
        text = galleryViewModel.selectedItemsText.value,
        fontFamily = FontFamily(Font(R.font.poppins_semibold)),
        fontSize = AppConstant.REGULAR_FONT_SIZE,
        color = colorResource(R.color.navi_blue),
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(top = 3.dp)
    )
}

@Composable
fun BoxScope.DeleteButton(activityActions: GalleryActivity.Actions, galleryViewModel: GalleryViewModel = viewModel()){

    val isEnable = galleryViewModel.selectedItemsCount.value > 0

    val iconColor = if(isEnable)
        colorResource(R.color.blue) else Color.Gray

    SquareIconButton(
        modifier = Modifier
            .align(Alignment.CenterEnd),
        icon = painterResource(R.drawable.ic_garbage_bin),
        iconColor = iconColor,
        isEnable = isEnable
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activityActions.deletePhotosWithApi30orLater(
                galleryViewModel.photos.filter {
                    it.isSelecting
                }.map {
                    it.uri
                }
            )
        } else {
            galleryViewModel.setIsPhotoDeletionDialogVisible(true)
        }
    }
}
