package com.gesturesnap.ai.camera.ui.screen.photo_display

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.model.PhotoInfo
import com.gesturesnap.ai.camera.ui.component.PhotoDeletionDialog
import com.gesturesnap.ai.camera.ui.core.ActivityHavingDeleteMediaFeature
import com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient.BottomBar
import com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient.Header
import com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient.InteractiveView
import com.gesturesnap.ai.camera.ui.screen.photo_display.ingredient.PhotoDetailDialog
import com.gesturesnap.ai.camera.view_model.photo_display.PhotoDisplayViewModel

class PhotoDisplayActivity : ActivityHavingDeleteMediaFeature() {

    private lateinit var photoDisplayViewModel: PhotoDisplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val photoInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_PHOTO_INFO, PhotoInfo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_PHOTO_INFO)
            } ?: PhotoInfo()

        photoDisplayViewModel = ViewModelProvider(this)[PhotoDisplayViewModel::class.java]
        photoDisplayViewModel.setPhotoInfo(photoInfo)

        setContent {
            MaterialTheme {
                PhotoDisplayComposeScreen(photoDisplayViewModel)
            }
        }
    }

    companion object {
        private const val EXTRA_PHOTO_INFO = "photo_info"

        fun start(context: Context, photoInfo: PhotoInfo) {
            val intent = Intent(context, PhotoDisplayActivity::class.java)
            intent.putExtra(EXTRA_PHOTO_INFO, photoInfo)
            context.startActivity(intent)
        }
    }
}

@Composable
fun PhotoDisplayComposeScreen(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? PhotoDisplayActivity

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(Modifier.fillMaxSize()) {
            InteractiveView()
            ViewContainer(photoDisplayViewModel)

            if (photoDisplayViewModel.isPhotoDetailDialogVisible.value) {
                PhotoDetailDialog()
            }

            if (photoDisplayViewModel.isPhotoDeletionDialogVisible.value) {
                PhotoDeletionDialog(
                    onCancel = {
                        photoDisplayViewModel.setIsPhotoDeletionDialogVisible(false)
                    },
                    onDelete = {
                        photoDisplayViewModel.setIsPhotoDeletionDialogVisible(false)
                        val path = photoDisplayViewModel.photoInfo.value.path
                        activity?.deleteMedia(path) {
                            activity.setResult(Activity.RESULT_OK)
                            activity.finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ViewContainer(photoDisplayViewModel: PhotoDisplayViewModel) {
    if (!photoDisplayViewModel.isOnlyDisplayPhoto.value) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Header()
            BottomBar()
        }
    }
}