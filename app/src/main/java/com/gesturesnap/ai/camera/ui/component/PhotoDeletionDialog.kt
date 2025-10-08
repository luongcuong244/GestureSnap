package com.gesturesnap.ai.camera.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant

@Composable
fun PhotoDeletionDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit
){

    val backgroundColor = colorResource(R.color.black_500)
    val defaultSystemBarsColor = colorResource(R.color.gray_white)

    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit){

        systemUiController.setSystemBarsColor(backgroundColor)

        onDispose {
            systemUiController.setSystemBarsColor(defaultSystemBarsColor)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable {
                onCancel()
            }
    ){
        Column(
            modifier = Modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(5))
                .background(colorResource(R.color.white))
                .clickable { } // prevent clicking through the dialog
                .padding(15.dp)
        ) {
            Text(
                text = stringResource(R.string.photo_deletion_dialog_title),
                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                fontSize = AppConstant.TITLE_FONT_SIZE,
            )
            Divider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(0.dp, 5.dp)
            )
            Text(text = stringResource(R.string.photo_deletion_dialog_text_content))
            Divider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(0.dp, 15.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TouchableOpacityButton(
                    onClick = { onCancel() },
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(10))
                        .background(colorResource(R.color.gray_2))
                        .padding(15.dp, 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = AppConstant.TEXT_BUTTON_FONT_SIZE,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 3.dp),
                    )
                }

                TouchableOpacityButton(
                    onClick = {
                        onDelete()
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(10))
                        .background(colorResource(R.color.red))
                        .padding(15.dp, 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = AppConstant.TEXT_BUTTON_FONT_SIZE,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 3.dp),
                        color = colorResource(R.color.white)
                    )
                }
            }
        }
    }
}