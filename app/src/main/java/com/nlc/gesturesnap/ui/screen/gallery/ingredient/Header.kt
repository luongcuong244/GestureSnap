package com.nlc.gesturesnap.ui.screen.gallery.ingredient

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.screen.gallery.GalleryActivity

@Composable
fun Header(activityActions: GalleryActivity.Actions){

    val hideChoiceButton = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = colorResource(R.color.gray_white))
            .pointerInput(Unit) {}
            .padding(15.dp, 5.dp)
            .zIndex(1f)
    ){
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(activityActions)
            Divider(
                color = colorResource(R.color.black_300),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(15.dp, 5.dp)
                    .width(0.75.dp)
            )
            PhotoNumberText()
        }
        if(!hideChoiceButton){
            ChoiceButton()
        }
    }
}