package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.screen.gallery.bottomBarHeight

@Composable
fun BoxScope.BottomBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .align(Alignment.BottomEnd)
            .background(color = colorResource(R.color.gray_white))
            .pointerInput(Unit) {}
            .drawBehind {
                val strokeWidth = 2f

                drawLine(
                    Color.LightGray,
                    Offset(0f, 0f),
                    Offset(size.width, 0f),
                    strokeWidth
                )
            }
            .padding(30.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoButton()
        DeleteButton()
    }
}