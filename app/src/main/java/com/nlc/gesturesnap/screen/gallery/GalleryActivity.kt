package com.nlc.gesturesnap.screen.gallery

import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.screen.gallery.model.Photo
import com.nlc.gesturesnap.screen.gallery.ui.ScreenContent
import com.nlc.gesturesnap.screen.gallery.view_model.GalleryViewModel

class GalleryActivity : ComponentActivity() {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
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

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it.resultCode == RESULT_OK) {
                actions.updateAfterDeletingPhotosSuccessfully()
            }
        }
    }

    inner class Actions {

        fun popActivity(){
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        fun deletePhotos(photoUris: List<Uri>) {
            try {
                photoUris.forEach {
                    contentResolver.delete(it, null, null)
                }
                updateAfterDeletingPhotosSuccessfully()
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, photoUris).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }

        fun updateAfterDeletingPhotosSuccessfully(){
            val galleryViewModel =
                ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

            galleryViewModel.photos.removeIf {
                it.isSelecting
            }

            galleryViewModel.setIsSelectable(false)
        }
    }
}