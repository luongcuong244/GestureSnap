package com.nlc.gesturesnap.ui.screen.photo_display.ingredient

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.component.SquareIconButton

@Composable
fun InfoButton(){
    SquareIconButton(
        icon = painterResource(R.drawable.ic_more_info),
        iconColor = colorResource(R.color.blue),
    ) {

    }
}