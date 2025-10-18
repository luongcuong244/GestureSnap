package com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.helper.Formatter
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel
import java.text.SimpleDateFormat
import java.util.Locale

data class ItemInfo(val title: String, val value: String)

@Composable
fun PhotoDetailDialog(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val backgroundColor = colorResource(R.color.black_700)
    val defaultSystemBarsColor = colorResource(R.color.gray_white)

    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit){

        systemUiController.setSystemBarsColor(backgroundColor)

        onDispose {
            systemUiController.setSystemBarsColor(defaultSystemBarsColor)
        }
    }

    val context = LocalContext.current

    val items = remember {
        mutableStateListOf<ItemInfo>()
    }

    LaunchedEffect(Unit){

        val photoInfo = photoDisplayViewModel.photoInfo.value

        val dateFormatter = SimpleDateFormat("yyyy/MM/dd  HH:mm:ss", Locale.getDefault())

        items.add(
            ItemInfo(
                title = context.getString(R.string.name),
                value = photoInfo.name
            )
        )
        items.add(
            ItemInfo(
                title = context.getString(R.string.time),
                value = dateFormatter.format(photoInfo.dateTaken)
            )
        )
        items.add(
            ItemInfo(
                title = context.getString(R.string.resolution),
                value = "${photoInfo.width} x ${photoInfo.height}"
            )
        )
        items.add(
            ItemInfo(
                title = context.getString(R.string.size),
                value = Formatter.formatFileSize(photoInfo.size)
            )
        )
        items.add(
            ItemInfo(
                title = context.getString(R.string.path),
                value = photoInfo.path
            )
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    photoDisplayViewModel.setPhotoDetailDialogVisible(false)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.black_700))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5))
                    .background(Color.White)
                    .padding(15.dp)
                    .height(300.dp)
                    .align(Alignment.Center)
                    .pointerInput(Unit){
                        detectTapGestures {  }
                    }
            ) {
                Text(
                    text = stringResource(R.string.details),
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    fontSize = AppConstant.TITLE_FONT_SIZE,
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f),
                ) {
                    items(items.size) {
                        Item(items[it])
                    }
                }
            }
        }
    }
}

@Composable
fun Item(item: ItemInfo){
    Column(
        modifier = Modifier
            .padding(bottom = 15.dp)
    ) {
        Text(
            text = item.title,
            fontFamily = FontFamily(Font(R.font.poppins_light)),
            fontSize = AppConstant.SMALL_FONT_SIZE,
            color = Color.Gray
        )
        Text(
            text = item.value,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontSize = AppConstant.SMALL_FONT_SIZE,
            color = Color.Black
        )
    }
}