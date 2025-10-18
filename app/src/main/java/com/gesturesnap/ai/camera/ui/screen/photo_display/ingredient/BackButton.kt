package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.ui.component.TouchableOpacityButton

@Composable
fun BoxScope.BackButton() {
    val context = LocalContext.current

    TouchableOpacityButton(
        modifier = Modifier.align(Alignment.CenterStart),
        onClick = {
            (context as? Activity)?.finish()
        }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .height(24.dp)
                .width(24.dp),
            colorFilter = ColorFilter.tint(colorResource(R.color.blue))
        )
    }
}