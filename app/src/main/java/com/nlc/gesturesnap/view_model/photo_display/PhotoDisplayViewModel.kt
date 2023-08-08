package com.nlc.gesturesnap.view_model.photo_display

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment

class PhotoDisplayViewModel : ViewModel() {

    val isFragmentOpen = mutableStateOf(false)

    val fragmentArgument = mutableStateOf(PhotoDisplayFragment.Argument())

    fun setFragmentArgument(argument: PhotoDisplayFragment.Argument){
        this.fragmentArgument.value = argument
    }

    fun setIsFragmentOpen(isOpen : Boolean){
        isFragmentOpen.value = isOpen
    }
}