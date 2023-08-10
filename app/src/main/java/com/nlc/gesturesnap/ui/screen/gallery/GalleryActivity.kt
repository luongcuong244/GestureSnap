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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.helper.PermissionHelper
import com.nlc.gesturesnap.model.SelectablePhoto
import com.nlc.gesturesnap.ui.component.PhotoDeletionDialog
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.BackButton
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.BottomBar
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.ChoiceButton
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotoDisplayFragmentView
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotosList
import com.nlc.gesturesnap.ui.screen.photo_display.PhotoDisplayFragment
import com.nlc.gesturesnap.view_model.gallery.GalleryViewModel
import com.nlc.gesturesnap.view_model.shared.PhotoDisplayFragmentStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity() {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var requestExternalPermissionLauncher: ActivityResultLauncher<String>

    private val condVarWaitState = ConditionVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val allPhotos = MediaHelper.getAllPhotos(this).map {
            SelectablePhoto(it.path, it.uri)
        }

        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        galleryViewModel.setPhotos(allPhotos)

        val photoDisplayFragmentStateViewModel =
            ViewModelProvider(this@GalleryActivity)[PhotoDisplayFragmentStateViewModel::class.java]

        photoDisplayFragmentStateViewModel.photoDisplayFragmentState.observe(this) {
            if(it == PhotoDisplayFragmentStateViewModel.State.PREPARE_CLOSE){
                galleryViewModel.setFragmentArgument(PhotoDisplayFragment.Argument())
            }

            if(it == PhotoDisplayFragmentStateViewModel.State.CLOSED){
                galleryViewModel.setIsPhotoDisplayFragmentViewVisible(false)
            }
        }

        val actions = Actions()

        setContent {
            MaterialTheme {
                GalleryActivityComposeScreen(actions, supportFragmentManager)
            }
        }

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it.resultCode == RESULT_OK) {
                actions.updateAfterDeletingPhotosSuccessfully()
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

    inner class Actions {

        fun popActivity(){
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun deletePhotosWithApi30orLater(photoUris: List<Uri>){
            val intentSender = MediaStore.createDeleteRequest(contentResolver, photoUris).intentSender
            intentSender.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }

        fun deletePhotoWithApi29(photoUri: Uri){
            if(Build.VERSION.SDK_INT != Build.VERSION_CODES.Q){
                return
            }

            try {
                contentResolver.delete(photoUri, null, null)
                updateAfterDeletingPhotosSuccessfully()
            } catch (e: SecurityException) {
                val recoverableSecurityException = e as? RecoverableSecurityException
                val intentSender = recoverableSecurityException?.userAction?.actionIntent?.intentSender

                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }

        fun deletePhotosWithApi28orOlder(photoUris: List<Uri>){

            CoroutineScope(Dispatchers.IO).launch {

                var hasWriteExternalPermission = true

                if(!PermissionHelper.isWriteExternalStoragePermissionGranted(this@GalleryActivity)){
                    requestExternalPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    condVarWaitState.close()
                    hasWriteExternalPermission = condVarWaitState.block(1000) // stop and wait until the permission is granted
                }

                if(hasWriteExternalPermission) {
                    photoUris.forEach {
                        contentResolver.delete(it, null, null)
                    }
                    updateAfterDeletingPhotosSuccessfully()
                }
            }
        }

        fun updateAfterDeletingPhotosSuccessfully(){
            val galleryViewModel =
                ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

            galleryViewModel.photos.removeIf {
                it.isSelecting
            }

            galleryViewModel.setIsPhotoDeletionDialogVisible(false)
            galleryViewModel.setIsSelectable(false)
        }
    }
}

val bottomBarHeight = 50.dp

@Composable
fun GalleryActivityComposeScreen(activityActions: GalleryActivity.Actions, fragmentManager: FragmentManager, galleryViewModel: GalleryViewModel = viewModel()){

    val bottomBarTranslationValue by animateDpAsState(
        targetValue = if(galleryViewModel.isSelectable.value) 0.dp else bottomBarHeight,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                PhotosList(bottomBarTranslationValue)
                BottomBar(activityActions, bottomBarTranslationValue)
            }
            OverlayBackground()
            Header(activityActions)
            
            if(galleryViewModel.isPhotoDeletionDialogVisible.value){
                PhotoDeletionDialog(
                    onCancel = {
                        galleryViewModel.setIsPhotoDeletionDialogVisible(false)
                    },
                    onDelete = {
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
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

@Composable
fun OverlayBackground(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.black_700),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun BoxScope.Header(activityActions: GalleryActivity.Actions){

    val hideChoiceButton = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = if(hideChoiceButton) Arrangement.Start else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        BackButton(activityActions)
        if(!hideChoiceButton){
            ChoiceButton()
        }
    }
}