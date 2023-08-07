package com.nlc.gesturesnap.screen.photo_display.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.photo_display.ui.component.Photo

@Composable
fun PhotoDisplayComposeScreen(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Background()
            Photo()
            Box(
                Modifier.fillMaxSize()
            ) {

            }
        }
    }
}

@Composable
fun Background(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.white))
    ){

    }
}

@Preview
@Composable
fun Preview(){
    MaterialTheme {
        PhotoDisplayComposeScreen()
    }
}