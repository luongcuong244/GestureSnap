package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun BoxScope.ChoiceButton(galleryViewModel: GalleryViewModel = viewModel()){

    val buttonText = if(galleryViewModel.isSelectable.value)
        stringResource(R.string.cancel) else stringResource(R.string.select)

    Button(
        onClick = {
            val isSelectable = galleryViewModel.isSelectable.value

            if(isSelectable){
                galleryViewModel.unSelectAll()
            }

            galleryViewModel.setIsSelectable(!isSelectable)
        },
        contentPadding = PaddingValues(10.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.white_300)),
        modifier = Modifier
            .height(27.dp)
            .align(Alignment.CenterEnd)
    ) {
        Text(
            text = buttonText,
            modifier = Modifier
                .padding(top = 3.dp),
            fontFamily = FontFamily(Font(R.font.poppins_semibold)),
            fontSize = AppConstant.TEXT_BUTTON_FONT_SIZE,
            color = colorResource(R.color.white)
        )
    }
}