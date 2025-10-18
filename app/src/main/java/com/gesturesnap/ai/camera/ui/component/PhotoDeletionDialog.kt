package com.gesturesnap.ai.camera.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PhotoDeletionDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = colorResource(R.color.black_700)
    val defaultSystemBarsColor = colorResource(R.color.gray_white)
    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(backgroundColor)
        onDispose { systemUiController.setSystemBarsColor(defaultSystemBarsColor) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onCancel() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .background(colorResource(R.color.white))
                .clickable(enabled = false) { } // chặn click xuyên qua
                .padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // căn giữa text và nút
            verticalArrangement = Arrangement.Center
        ) {
            // Tiêu đề
            Text(
                text = stringResource(R.string.photo_deletion_dialog_title),
                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                fontSize = AppConstant.TITLE_FONT_SIZE,
                color = colorResource(R.color.navi_blue),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Nội dung mô tả
            Text(
                text = stringResource(R.string.photo_deletion_dialog_text_content),
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontSize = AppConstant.SMALL_FONT_SIZE,
                color = colorResource(R.color.black),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally),
            )

            // Hàng nút
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                // Nút Cancel
                TouchableOpacityButton(
                    onClick = { onCancel() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorResource(R.color.gray_2))
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = AppConstant.TEXT_BUTTON_FONT_SIZE,
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(R.color.black)
                    )
                }

                // Nút Delete
                TouchableOpacityButton(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorResource(R.color.red))
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = AppConstant.TEXT_BUTTON_FONT_SIZE,
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(R.color.white)
                    )
                }
            }
        }
    }
}