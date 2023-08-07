package com.nlc.gesturesnap.screen.gallery.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.screen.gallery.ui.bottomBarHeight
import com.nlc.gesturesnap.jetpack_compose.custom_widget.TouchableOpacityButton
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel

@Composable
fun BoxScope.BottomBar(activityActions: GalleryActivity.Actions, translationYValue: Dp){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .align(Alignment.BottomEnd)
            .offset(0.dp, translationYValue)
            .background(color = colorResource(R.color.gray_white)) // note the order of background method and padding method
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
        fontSize = 16.sp,
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
        Color.Blue else Color.Gray

    TouchableOpacityButton(
        onClick = {
            activityActions.deletePhotos(
                galleryViewModel.photos.filter {
                    it.isSelecting
                }.map {
                    it.uri
                }
            )
        },
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .width(50.dp)
            .height(50.dp),
        enable = isEnable,
        opacity = 0.5f
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_garbage_bin),
                colorFilter = ColorFilter.tint(iconColor),
                contentDescription = "Garbage Bin",
                modifier = Modifier
                    .height(27.dp)
                    .width(27.dp)
            )
        }
    }
}
