package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.ui.screen.gallery.GalleryActivity

@Composable
fun Header(activityActions: GalleryActivity.Actions){

    val hideChoiceButton = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppConstant.APP_BAR_HEIGHT)
            .background(color = colorResource(R.color.color_181A19))
            .pointerInput(Unit) {}
            .drawBehind {
                val strokeWidth = 2f
                drawLine(
                    Color.DarkGray,
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    strokeWidth
                )
            }
            .padding(16.dp, 5.dp)
            .zIndex(1f)
    ){
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(activityActions)
        }
        PhotoNumberText()
        if(!hideChoiceButton){
            ChoiceButton()
        }
    }
}