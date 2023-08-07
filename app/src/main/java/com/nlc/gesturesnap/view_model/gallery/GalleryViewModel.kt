package com.nlc.gesturesnap.view_model.gallery

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.model.SelectablePhoto

class GalleryViewModel : ViewModel() {

    val photos = mutableStateListOf<SelectablePhoto>()

    val selectedItemsText = mutableStateOf("Select Items")
    val isSelectable = mutableStateOf(false)
    val selectedItemsCount = mutableStateOf(0)

    val shownPhoto = mutableStateOf(PhotoInfo("", Uri.EMPTY))

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

    fun setShownPhotoInfo(photoInfo: PhotoInfo){
        shownPhoto.value = photoInfo
    }
}