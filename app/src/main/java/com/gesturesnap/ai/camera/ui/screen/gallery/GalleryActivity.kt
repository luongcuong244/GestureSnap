package com.gesturesnap.ai.camera.ui.screen.gallery

import android.Manifest
import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ConditionVariable
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.helper.AppConstant
import com.gesturesnap.ai.camera.helper.MediaHelper
import com.gesturesnap.ai.camera.helper.PermissionHelper
import com.gesturesnap.ai.camera.listener.PhotoDeleteListener
import com.gesturesnap.ai.camera.model.PhotoInfo
import com.gesturesnap.ai.camera.model.SelectablePhoto
import com.gesturesnap.ai.camera.ui.component.PhotoDeletionDialog
import com.gesturesnap.ai.camera.ui.core.ActivityHavingDeleteMediaFeature
import com.gesturesnap.ai.camera.ui.core.BaseActivity
import com.gesturesnap.ai.camera.ui.screen.gallery.ingredient.BottomBar
import com.gesturesnap.ai.camera.ui.screen.gallery.ingredient.Header
import com.gesturesnap.ai.camera.ui.screen.gallery.ingredient.PhotosList
import com.gesturesnap.ai.camera.ui.screen.photo_display.PhotoDisplayActivity
import com.gesturesnap.ai.camera.view_model.gallery.GalleryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class GalleryActivity : BaseActivity(), PhotoDeleteListener {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var requestExternalPermissionLauncher: ActivityResultLauncher<String>

    private val condVarWaitState = ConditionVariable()

    private val actions = Actions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {
            val allPhotos = MediaHelper.getAllPhotos(this@GalleryActivity).map {
                SelectablePhoto(
                    path = it.path,
                    name = it.name,
                    size = it.size,
                    dateTaken = it.dateTaken,
                    width = it.width,
                    height = it.height
                )
            }

            galleryViewModel.setPhotos(allPhotos)
        }

        setContent {
            MaterialTheme {
                GalleryActivityComposeScreen(actions)
            }
        }

        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    updateAfterDeletingPhotosSuccessfully()
                }
            }

        requestExternalPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                condVarWaitState.open()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        actions.popActivity()
    }

    inner class Actions {

        fun popActivity() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun deletePhotosWithApi30orLater(photoPaths: List<String>) {
            val photoUris = photoPaths.map {
                MediaHelper.getUriFromPath(this@GalleryActivity, it)
            }
            val intentSender =
                MediaStore.createDeleteRequest(contentResolver, photoUris).intentSender
            intentSender.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }

        fun deletePhotoWithApi29(photoPath: String) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                return
            }
            val photoUri = MediaHelper.getUriFromPath(this@GalleryActivity, photoPath) ?: return
            try {
                contentResolver.delete(photoUri, null, null)
                updateAfterDeletingPhotosSuccessfully()
            } catch (e: SecurityException) {
                val recoverableSecurityException = e as? RecoverableSecurityException
                val intentSender =
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender

                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }

        fun deletePhotosWithApi28orOlder(photoPaths: List<String>) {

            CoroutineScope(Dispatchers.IO).launch {
                val photoUris = photoPaths.map {
                    MediaHelper.getUriFromPath(this@GalleryActivity, it)
                }

                var hasWriteExternalPermission = true

                if (!PermissionHelper.isWriteExternalStoragePermissionGranted(this@GalleryActivity)) {
                    requestExternalPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    condVarWaitState.close()
                    hasWriteExternalPermission =
                        condVarWaitState.block(1000) // stop and wait until the permission is granted
                }

                if (hasWriteExternalPermission) {
                    photoUris.forEach {
                        it?.let { url -> contentResolver.delete(url, null, null) }
                    }
                    updateAfterDeletingPhotosSuccessfully()
                }
            }
        }

        fun goToDisplayScreen(photoInfo: PhotoInfo) {
            PhotoDisplayActivity.start(this@GalleryActivity, photoInfo)
        }
    }

    private fun updateAfterDeletingPhotosSuccessfully() {
        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        galleryViewModel.photos.removeIf {
            !File(it.path).exists()
        }

        galleryViewModel.setIsPhotoDeletionDialogVisible(false)
        galleryViewModel.setIsSelectable(false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun deletePhotosWithApi30orLater(photoPath: String) {
        actions.deletePhotosWithApi30orLater(listOf(photoPath))
    }

    override fun deletePhotoWithApi29(photoPath: String) {
        actions.deletePhotoWithApi29(photoPath)
    }

    override fun deletePhotosWithApi28orOlder(photoPath: String) {
        actions.deletePhotosWithApi28orOlder(listOf(photoPath))
    }
}

@Composable
fun GalleryActivityComposeScreen(
    activityActions: GalleryActivity.Actions,
    galleryViewModel: GalleryViewModel = viewModel()
) {

    val bottomBarTranslationValue by animateDpAsState(
        targetValue = if (galleryViewModel.isSelectable.value) 0.dp else AppConstant.BOTTOM_BAR_HEIGHT,
        label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Header(activityActions)
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (galleryViewModel.photos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.no_photos),
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        PhotosList(activityActions, bottomBarTranslationValue)
                    }
                    BottomBar(activityActions, bottomBarTranslationValue)
                }
            }

            if (galleryViewModel.isPhotoDeletionDialogVisible.value) {
                PhotoDeletionDialog(
                    onCancel = {
                        galleryViewModel.setIsPhotoDeletionDialogVisible(false)
                    },
                    onDelete = {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            activityActions.deletePhotosWithApi28orOlder(
                                galleryViewModel.photos.filter {
                                    it.isSelecting
                                }.map {
                                    it.path
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}