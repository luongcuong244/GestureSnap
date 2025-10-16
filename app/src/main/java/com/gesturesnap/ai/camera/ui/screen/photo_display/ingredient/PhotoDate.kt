package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun BoxScope.PhotoDate(){
    Column(
        modifier = Modifier
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DateText()
        TimeText()
    }
}

@Composable
fun DateText(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val dateString = remember {
        val dateTaken = photoDisplayViewModel.photoInfo.value.dateTaken
        formatter.format(dateTaken)
    }

    Text(
        text = dateString,
        color = Color.Black,
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontSize = AppConstant.SMALL_FONT_SIZE,
    )
}

@Composable
fun TimeText(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val formatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    val timeString = remember {
        val dateTaken = photoDisplayViewModel.photoInfo.value.dateTaken
        formatter.format(dateTaken)
    }

    Text(
        text = timeString,
        color = Color.Black,
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontSize = AppConstant.TINY_FONT_SIZE,
    )
}