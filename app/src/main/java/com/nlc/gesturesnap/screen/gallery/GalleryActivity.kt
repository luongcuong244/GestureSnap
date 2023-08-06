package com.nlc.gesturesnap.screen.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.screen.gallery.model.Photo
import com.nlc.gesturesnap.screen.gallery.ui.ScreenContent
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val allPhotos = MediaHelper.getAllPhotos(this).map {
            Photo(it.path, it.uri)
        }

        val galleryViewModel =
            ViewModelProvider(this)[GalleryViewModel::class.java]

        galleryViewModel.setPhotos(allPhotos)

        val actions = Actions()

        setContent {
            MaterialTheme {
                ScreenContent(actions)
            }
        }
    }

    inner class Actions {

        fun popActivity(){
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}