package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.model.SelectablePhoto
import com.gesturesnap.ai.camera.ui.screen.gallery.GalleryActivity
import com.gesturesnap.ai.camera.view_model.gallery.GalleryViewModel
import java.io.File

@Composable
fun PhotosList(
    activityActions: GalleryActivity.Actions,
    offsetValue: Dp,
    galleryViewModel: GalleryViewModel = viewModel()
) {

    val gridState = rememberLazyGridState()
    val lastPhotoCount = remember { mutableIntStateOf(0) }

    LaunchedEffect(galleryViewModel.photos.size) {
        val newCount = galleryViewModel.photos.size
        // Only scroll when adding photos
        if (newCount > lastPhotoCount.intValue && newCount > 0) {
            gridState.scrollToItem(newCount - 1)
        }
        lastPhotoCount.intValue = newCount
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
                activityActions,
                gridState,
                index,
                photo = galleryViewModel.photos[index]
            )
        }
    }
}

@Composable
fun PhotoItem(
    activityActions: GalleryActivity.Actions,
    gridState: LazyGridState,
    index: Int,
    photo: SelectablePhoto,
    galleryViewModel: GalleryViewModel = viewModel()
) {

    val density = LocalDensity.current

    val isSelectingState = remember { mutableStateOf(photo.isSelecting) }
    val sizePhotoItem = remember { mutableStateOf(DpSize.Zero) }
    val positionInRootPhotoItem = remember { mutableStateOf(Offset.Zero) }

    val onClick: () -> Unit = {
        if (galleryViewModel.isSelectable.value) {
            val addedValue = if (photo.isSelecting) -1 else 1
            galleryViewModel.setSelectedItemsCount(galleryViewModel.selectedItemsCount.value + addedValue)

            photo.isSelecting = !photo.isSelecting
            isSelectingState.value = photo.isSelecting // refresh UI
        } else {
            activityActions.goToDisplayScreen(photo)
        }
    }

    LaunchedEffect(galleryViewModel.isSelectable.value) {
        if (!galleryViewModel.isSelectable.value) {
            isSelectingState.value = photo.isSelecting // refresh UI
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
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
                }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(photo.path))
                        .memoryCacheKey(photo.path)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                if (isSelectingState.value) {
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

fun isFullyVisibleItem(listState: LazyGridState, index: Int): Boolean {
    val layoutInfo = listState.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo

    if (visibleItemsInfo.isEmpty()) return false

    val item = visibleItemsInfo.find {
        it.index == index
    } ?: return false

    // item is on top of the Lazy Vertical Grid
    if (item.offset.y < 0) {
        return false
    }

    val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

    // item is on bottom of the Lazy Vertical Grid
    if (item.offset.y + item.size.height > viewportHeight) {
        return false
    }

    return true
}