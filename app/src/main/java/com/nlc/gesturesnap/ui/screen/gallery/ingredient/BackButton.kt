package com.nlc.gesturesnap.ui.screen.gallery.ingredient

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
import com.nlc.gesturesnap.ui.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.ui.component.TouchableOpacityButton

@Composable
fun BackButton(activityActions: GalleryActivity.Actions){

    TouchableOpacityButton(
        onClick = {
            activityActions.popActivity()
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

//    TouchableOpacityButton(
//        onClick = {
//            activityActions.popActivity()
//        }
//    ) {
//        Row {
//            Image(
//                painter = painterResource(R.drawable.ic_arrow_left),
//                contentDescription = "Back",
//                modifier = Modifier
//                    .height(25.dp)
//                    .width(25.dp),
//                colorFilter = ColorFilter.tint(colorResource(R.color.blue))
//            )
//            Text(
//                text = stringResource(R.string.camera),
//                modifier = Modifier
//                    .padding(start = 5.dp, top = 1.dp),
//                fontFamily = FontFamily(Font(R.font.poppins_medium)),
//                fontSize = AppConstant.TITLE_FONT_SIZE,
//                color = colorResource(R.color.navi_blue)
//            )
//        }
//    }
}