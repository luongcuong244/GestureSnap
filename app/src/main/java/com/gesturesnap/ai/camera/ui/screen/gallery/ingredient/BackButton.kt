package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.ui.component.TouchableOpacityButton
import com.gesturesnap.ai.camera.ui.screen.gallery.GalleryActivity

@Composable
fun BackButton(activityActions: GalleryActivity.Actions) {

    TouchableOpacityButton(
        onClick = {
            activityActions.popActivity()
        }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .height(24.dp)
                .width(24.dp),
        )
    }
}