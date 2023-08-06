package com.nlc.gesturesnap.screen.gallery.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.screen.gallery.model.Photo

class GalleryViewModel : ViewModel() {

    val photos = mutableStateListOf<Photo>()
    val selectedItemsText = mutableStateOf("Select Items")
    val isSelectable = mutableStateOf(false)
    val selectedItemsCount = mutableStateOf(0)

    val isPhotoDeletionDialogVisible = mutableStateOf(false)

    fun setPhotos(photos: List<Photo>){
        this.photos.clear()
        this.photos.addAll(photos)
    }

    fun setIsSelectable(value: Boolean){

        if(!value){
            for(i in 0 until photos.size){
                photos[i].isSelecting = false
            }
        }

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
}