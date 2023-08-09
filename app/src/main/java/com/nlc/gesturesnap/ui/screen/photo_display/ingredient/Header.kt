package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.nlc.gesturesnap.R

@Composable
fun BoxScope.Header(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.gray_white))
            .drawBehind {
                val strokeWidth = 2f
                val y = size.height - strokeWidth / 2

                drawLine(
                    Color.LightGray,
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth
                )
            }
            .padding(15.dp, 5.dp)
            .align(Alignment.TopCenter),
    ){
        BackButton()
        PhotoDate()
    }
}