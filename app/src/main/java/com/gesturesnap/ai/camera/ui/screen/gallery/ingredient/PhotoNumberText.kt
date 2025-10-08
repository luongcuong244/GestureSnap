package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.view_model.gallery.GalleryViewModel

@Composable
fun PhotoNumberText(galleryViewModel: GalleryViewModel = viewModel()){

    val size = galleryViewModel.photos.size

    val text = "$size ${
        if(size > 1){
            stringResource(R.string.photos)
        } else stringResource(R.string.photo)
    }"

    Text(
        text = text,
        modifier = Modifier
            .padding(top = 3.dp),
        fontFamily = FontFamily(Font(R.font.poppins_medium)),
        fontSize = AppConstant.TITLE_FONT_SIZE,
        color = colorResource(R.color.navi_blue)
    )
}