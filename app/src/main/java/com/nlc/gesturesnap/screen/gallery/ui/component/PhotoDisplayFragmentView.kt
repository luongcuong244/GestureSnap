package com.nlc.gesturesnap.screen.gallery.ui.component

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel
import com.nlc.gesturesnap.screen.photo_display.PhotoDisplayFragment

@Composable
fun PhotoDisplayFragmentView(
    fragmentManager: FragmentManager,
    galleryViewModel: GalleryViewModel = viewModel()
) {
    if(galleryViewModel.shownPhotoInfo.value.path.isNotEmpty()){
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                FrameLayout(context).apply {
                    id = ViewCompat.generateViewId()
                }
            },
            update = {
                fragmentManager.beginTransaction()
                    .replace(it.id, PhotoDisplayFragment.newInstance(galleryViewModel.shownPhotoInfo.value))
                    .commit()
            }
        )
    }
}