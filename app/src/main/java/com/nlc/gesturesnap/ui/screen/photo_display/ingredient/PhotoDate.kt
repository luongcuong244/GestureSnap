package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant

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
fun DateText(){
    Text(
        text = "24 July 2020",
        color = Color.Black,
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontSize = AppConstant.SMALL_FONT_SIZE,
    )
}

@Composable
fun TimeText(){
    Text(
        text = "09:45",
        color = Color.Black,
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontSize = AppConstant.TINY_FONT_SIZE,
    )
}