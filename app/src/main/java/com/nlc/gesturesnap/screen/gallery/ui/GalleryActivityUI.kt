package com.nlc.gesturesnap.screen.gallery.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.screen.gallery.ui.component.BackButton
import com.nlc.gesturesnap.screen.gallery.ui.component.BottomBar
import com.nlc.gesturesnap.screen.gallery.ui.component.ChoiceButton
import com.nlc.gesturesnap.screen.gallery.ui.component.PhotoDeletionDialog
import com.nlc.gesturesnap.screen.gallery.ui.component.PhotosList
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel

val bottomBarHeight = 50.dp

@Composable
fun ScreenContent(activityActions: GalleryActivity.Actions, galleryViewModel: GalleryViewModel = viewModel()){

    val bottomBarTranslationValue by animateDpAsState(
        targetValue = if(galleryViewModel.isSelectable.value) 0.dp else bottomBarHeight,
        label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                PhotosList(bottomBarTranslationValue)
                BottomBar(bottomBarTranslationValue)
            }
            OverlayBackground()
            Header(activityActions)
            PhotoDeletionDialog()
        }
    }
}

@Composable
fun OverlayBackground(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.black_700),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun BoxScope.Header(activityActions: GalleryActivity.Actions){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        BackButton(activityActions)
        ChoiceButton()
    }
}