package com.nlc.gesturesnap.view_model.gallery

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.model.SelectablePhoto
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment

class GalleryViewModel : ViewModel() {

    val photos = mutableStateListOf<SelectablePhoto>()

    val selectedItemsText = mutableStateOf("Select Items")
    val isSelectable = mutableStateOf(false)
    val selectedItemsCount = mutableStateOf(0)

    val isPhotoDeletionDialogVisible = mutableStateOf(false)

    val isPhotoDisplayFragmentViewVisible = mutableStateOf(false)
    val fragmentArgument = mutableStateOf(PhotoDisplayFragment.Argument())

    fun setPhotos(photos: List<SelectablePhoto>){
        this.photos.clear()
        this.photos.addAll(photos)
    }

    fun unSelectAll(){
        for(i in 0 until photos.size){
            if(photos[i].isSelecting){
                photos[i].isSelecting = false
            }
        }
    }

    fun setIsSelectable(value: Boolean){
        isSelectable.value = value
        setSelectedItemsCount(0)
    }

    fun setSelectedItemsCount(value : Int){
        selectedItemsCount.value = value
        if(value == 0){
            selectedItemsText.value = "Select Items"
            return
        }

        if(value == 1){
            selectedItemsText.value = "1 Photo Selected"
            return
        }

        selectedItemsText.value = "$value Photos Selected"
    }

    fun setIsPhotoDeletionDialogVisible(visible: Boolean){
        isPhotoDeletionDialogVisible.value = visible
    }

    fun setIsPhotoDisplayFragmentViewVisible(visible: Boolean){
        isPhotoDisplayFragmentViewVisible.value = visible
    }

    fun setFragmentArgument(argument: PhotoDisplayFragment.Argument){
        fragmentArgument.value = argument
    }
}