package com.nlc.gesturesnap.ui.screen.gallery

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
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.helper.PermissionHelper
import com.nlc.gesturesnap.listener.PhotoDeleteListener
import com.nlc.gesturesnap.model.SelectablePhoto
import com.nlc.gesturesnap.ui.component.PhotoDeletionDialog
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.BottomBar
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.Header
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotoDisplayFragmentView
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotosList
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment
import com.nlc.gesturesnap.view_model.gallery.GalleryViewModel
import com.nlc.gesturesnap.view_model.shared.PhotoDisplayFragmentStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class GalleryActivity : AppCompatActivity(), PhotoDeleteListener {

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
                    uri = it.uri,
                    name = it.name,
                    size = it.size,
                    dateTaken = it.dateTaken,
                    resolution = it.resolution
                )
            }

            galleryViewModel.setPhotos(allPhotos)
        }

        val photoDisplayFragmentStateViewModel =
            ViewModelProvider(this@GalleryActivity)[PhotoDisplayFragmentStateViewModel::class.java]

        photoDisplayFragmentStateViewModel.photoDisplayFragmentState.observe(this) {
            if (it == PhotoDisplayFragmentStateViewModel.State.PREPARE_CLOSE) {
                galleryViewModel.setFragmentArgument(PhotoDisplayFragment.Argument())
            }

            if (it == PhotoDisplayFragmentStateViewModel.State.CLOSED) {
                galleryViewModel.setIsPhotoDisplayFragmentViewVisible(false)
            }
        }

        setContent {
            MaterialTheme {
                GalleryActivityComposeScreen(actions, supportFragmentManager)
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
        fun deletePhotosWithApi30orLater(photoUris: List<Uri>) {
            val intentSender =
                MediaStore.createDeleteRequest(contentResolver, photoUris).intentSender
            intentSender.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }

        fun deletePhotoWithApi29(photoUri: Uri) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                return
            }

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

        fun deletePhotosWithApi28orOlder(photoUris: List<Uri>) {

            CoroutineScope(Dispatchers.IO).launch {

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
                        contentResolver.delete(it, null, null)
                    }
                    updateAfterDeletingPhotosSuccessfully()
                }
            }
        }
    }

    private fun updateAfterDeletingPhotosSuccessfully() {

        closePhotoDisplayFragment()

        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        galleryViewModel.photos.removeIf {
            !File(it.path).exists()
        }

        galleryViewModel.setIsPhotoDeletionDialogVisible(false)
        galleryViewModel.setIsSelectable(false)
    }

    private fun closePhotoDisplayFragment() {
        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments

        fragments.forEach {
            if (it is PhotoDisplayFragment) {
                it.closeFragment()
                galleryViewModel.setFragmentArgument(PhotoDisplayFragment.Argument())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun deletePhotosWithApi30orLater(photoUri: Uri) {
        actions.deletePhotosWithApi30orLater(listOf(photoUri))
    }

    override fun deletePhotoWithApi29(photoUri: Uri) {
        actions.deletePhotoWithApi29(photoUri)
    }

    override fun deletePhotosWithApi28orOlder(photoUri: Uri) {
        actions.deletePhotosWithApi28orOlder(listOf(photoUri))
    }
}

@Composable
fun GalleryActivityComposeScreen(
    activityActions: GalleryActivity.Actions,
    fragmentManager: FragmentManager,
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
                    PhotosList(bottomBarTranslationValue)
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
                                    it.uri
                                }
                            )
                        }
                    }
                )
            }

            PhotoDisplayFragmentView(fragmentManager)
        }
    }
}