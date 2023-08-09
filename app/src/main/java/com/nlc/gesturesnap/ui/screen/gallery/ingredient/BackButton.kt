package com.nlc.gesturesnap.ui.screen.gallery.ingredient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.ui.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.ui.component.TouchableOpacityButton

@Composable
fun BackButton(activityActions: GalleryActivity.Actions){
    TouchableOpacityButton(
        onClick = {
            activityActions.popActivity()
        }
    ) {
        Row {
            Image(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Back",
                modifier = Modifier
                    .height(25.dp)
                    .width(25.dp)
            )
            Text(
                text = stringResource(R.string.camera),
                modifier = Modifier
                    .padding(start = 5.dp, top = 3.dp),
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}