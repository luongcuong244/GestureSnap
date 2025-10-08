package com.gesturesnap.ai.camera.ui.screen.gallery.ingredient

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.ui.screen.photo_display.PhotoDisplayFragment
import com.gesturesnap.ai.camera.view_model.gallery.GalleryViewModel

@Composable
fun PhotoDisplayFragmentView(
    fragmentManager: FragmentManager,
    galleryViewModel: GalleryViewModel = viewModel()
) {
    if(galleryViewModel.isPhotoDisplayFragmentViewVisible.value){
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                FrameLayout(context).apply {
                    id = ViewCompat.generateViewId()
                }
            },
            update = {
                if(galleryViewModel.fragmentArgument.value.photo.path.isNotEmpty()){
                    fragmentManager.beginTransaction()
                        .replace(it.id, PhotoDisplayFragment.newInstance(galleryViewModel.fragmentArgument.value))
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
    }
}