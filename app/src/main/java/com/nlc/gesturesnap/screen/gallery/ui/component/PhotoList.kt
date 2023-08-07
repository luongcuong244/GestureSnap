package com.nlc.gesturesnap.screen.gallery.ui.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.screen.gallery.GalleryActivity
import com.nlc.gesturesnap.screen.gallery.model.Photo
import com.nlc.gesturesnap.screen.gallery.ui.bottomBarHeight
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel
import java.io.File

@Composable
fun PhotosList(activityActions: GalleryActivity.Actions, offsetValue: Dp, galleryViewModel: GalleryViewModel = viewModel()){

    val listState = rememberLazyGridState()

    LaunchedEffect(Unit){
        listState.scrollToItem(galleryViewModel.photos.lastIndex)
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .offset(0.dp, offsetValue - bottomBarHeight),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(top = bottomBarHeight - offsetValue),
        state = listState
    ) {
        items(
            items = galleryViewModel.photos,
            key = { photo ->
                photo.path
            }
        ) {
            PhotoItem(activityActions, photo = it)
        }
    }
}

@Composable
fun PhotoItem(activityActions: GalleryActivity.Actions, photo: Photo, galleryViewModel: GalleryViewModel = viewModel()){

    val imageBitmap = rememberAsyncImagePainter(model = File(photo.path))

    val isSelectingState = remember { mutableStateOf(photo.isSelecting) }

    val sizePhotoItem = remember { mutableStateOf(IntSize.Zero) }
    val positionInRootPhotoItem = remember { mutableStateOf(Offset.Zero) }

    val onClick : () -> Unit = {
        if(galleryViewModel.isSelectable.value){
            val addedValue = if(photo.isSelecting) -1 else 1
            galleryViewModel.setSelectedItemsCount(galleryViewModel.selectedItemsCount.value + addedValue)

            photo.isSelecting = !photo.isSelecting
            isSelectingState.value = photo.isSelecting // refresh UI
        } else {
            galleryViewModel.setShownPhotoInfo(photo as PhotoInfo)
        }
    }

    LaunchedEffect(galleryViewModel.isSelectable.value){
        if(!galleryViewModel.isSelectable.value){
            isSelectingState.value = photo.isSelecting // refresh UI
        }
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = CutCornerShape(0),
        contentPadding = PaddingValues(),
        modifier = Modifier
            .background(Color.Gray)
            .aspectRatio(1f)
            .onGloballyPositioned {
                sizePhotoItem.value = it.size
                positionInRootPhotoItem.value = it.positionInRoot()
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Image(
                painter = imageBitmap,
                contentDescription = "Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if(isSelectingState.value){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.white_300))
                        .padding(7.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_selected),
                        contentDescription = "Selected",
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}