package com.nlc.gesturesnap.ui.screen.gallery.ingredient

import android.os.ConditionVariable
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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.model.SelectablePhoto
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment
import com.nlc.gesturesnap.view_model.gallery.GalleryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PhotosList(offsetValue: Dp, galleryViewModel: GalleryViewModel = viewModel()){

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit){
        if(galleryViewModel.photos.size > 0){
            gridState.scrollToItem(galleryViewModel.photos.lastIndex)
        }
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .offset(0.dp, offsetValue - AppConstant.BOTTOM_BAR_HEIGHT),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(top = AppConstant.BOTTOM_BAR_HEIGHT - offsetValue),
        state = gridState
    ) {
        items(galleryViewModel.photos.size) { index ->
            PhotoItem(
                gridState,
                index,
                photo = galleryViewModel.photos[index]
            )
        }
    }
}

@Composable
fun PhotoItem(gridState: LazyGridState, index: Int, photo: SelectablePhoto, galleryViewModel: GalleryViewModel = viewModel()){

    val density = LocalDensity.current

    val imageBitmap = rememberAsyncImagePainter(model = File(photo.path))

    val isSelectingState = remember { mutableStateOf(photo.isSelecting) }

    val sizePhotoItem = remember { mutableStateOf(DpSize.Zero) }
    val positionInRootPhotoItem = remember { mutableStateOf(Offset.Zero) }

    val conditionVariable = remember {
        ConditionVariable()
    }

    val onClick : () -> Unit = {
        if(galleryViewModel.isSelectable.value){
            val addedValue = if(photo.isSelecting) -1 else 1
            galleryViewModel.setSelectedItemsCount(galleryViewModel.selectedItemsCount.value + addedValue)

            photo.isSelecting = !photo.isSelecting
            isSelectingState.value = photo.isSelecting // refresh UI
        } else {

            val isFullyVisibleItem = isFullyVisibleItem(gridState, index)

            if(!isFullyVisibleItem){
                CoroutineScope(Dispatchers.IO).launch {
                    gridState.scrollToItem(index)
                }
                conditionVariable.close()
                conditionVariable.block(1000L)
            }

            galleryViewModel.setFragmentArgument(
                PhotoDisplayFragment.Argument(
                    sizePhotoItem.value,
                    positionInRootPhotoItem.value,
                    photo as PhotoInfo
                )
            )

            galleryViewModel.setIsPhotoDisplayFragmentViewVisible(true)
        }
    }

    LaunchedEffect(galleryViewModel.isSelectable.value){
        if(!galleryViewModel.isSelectable.value){
            isSelectingState.value = photo.isSelecting // refresh UI
        }
    }

    val alpha = remember {
        mutableStateOf(1f)
    }

    LaunchedEffect(galleryViewModel.fragmentArgument.value){

        val path = galleryViewModel.fragmentArgument.value.photo.path

        if(path == photo.path){
            delay(100) // for fragment initialization
            alpha.value = 0f
        } else {
            alpha.value = 1f
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .alpha(alpha.value)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            shape = CutCornerShape(0),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .onGloballyPositioned {
                    val widthPx = it.size.width
                    val heightPx = it.size.height

                    val viewWidthDp = with(density) {
                        widthPx.toDp()
                    }

                    val viewHeightDp = with(density) {
                        heightPx.toDp()
                    }

                    sizePhotoItem.value = DpSize(viewWidthDp, viewHeightDp)
                    positionInRootPhotoItem.value = it.positionInRoot()

                    conditionVariable.open()
                }
        ) {
            Box(modifier = Modifier.fillMaxSize()){
                Image(
                    painter = imageBitmap,
                    contentDescription = "Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
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
}

fun isFullyVisibleItem(listState: LazyGridState, index: Int) : Boolean{
    val layoutInfo = listState.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo

    if(visibleItemsInfo.isEmpty()) return false

    val item = visibleItemsInfo.find {
        it.index == index
    } ?: return false

    // item is on top of the Lazy Vertical Grid
    if(item.offset.y < 0){
        return false
    }

    val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

    // item is on bottom of the Lazy Vertical Grid
    if (item.offset.y + item.size.height > viewportHeight) {
        return false
    }

    return true
}