package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.component.TouchableOpacityButton

@Composable
fun BoxScope.BackButton(){
    TouchableOpacityButton(
        modifier = Modifier.align(Alignment.CenterStart),
        onClick = {

        }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .height(25.dp)
                .width(25.dp),
            colorFilter = ColorFilter.tint(colorResource(R.color.blue))
        )
    }
}