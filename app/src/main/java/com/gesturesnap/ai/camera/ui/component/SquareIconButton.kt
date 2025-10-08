package com.gesturesnap.ai.camera.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.gesturesnap.ai.camera.helper.AppConstant

@Composable
fun SquareIconButton(
    modifier: Modifier = Modifier,
    isEnable: Boolean = true,
    icon: Painter,
    buttonSize: Dp = AppConstant.ICON_BUTTON_SIZE,
    iconSize: Dp = AppConstant.ICON_SIZE,
    iconColor: Color,
    onClick: () -> Unit){

    TouchableOpacityButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .width(buttonSize)
            .height(buttonSize),
        enable = isEnable,
        opacity = 0.5f
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = icon,
                colorFilter = ColorFilter.tint(iconColor),
                contentDescription = "Button",
                modifier = Modifier
                    .height(iconSize)
                    .width(iconSize)
            )
        }
    }
}