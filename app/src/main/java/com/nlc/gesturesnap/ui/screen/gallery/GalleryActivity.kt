package com.nlc.gesturesnap.ui.screen.gallery

import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.nlc.gesturesnap.model.SelectablePhoto
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.BackButton
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.BottomBar
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.ChoiceButton
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotoDisplayFragmentView
import com.nlc.gesturesnap.ui.screen.gallery.ingredient.PhotosList
import com.nlc.gesturesnap.view_model.gallery.GalleryViewModel

class GalleryActivity : AppCompatActivity() {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val allPhotos = MediaHelper.getAllPhotos(this).map {
            SelectablePhoto(it.path, it.uri)
        }

        val galleryViewModel =
            ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]

        galleryViewModel.setPhotos(allPhotos)

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .align(Alignment.TopCenter),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        BackButton(activityActions)
        ChoiceButton()
    }
}