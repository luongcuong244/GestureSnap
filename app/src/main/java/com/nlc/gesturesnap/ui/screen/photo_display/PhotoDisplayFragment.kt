package com.nlc.gesturesnap.ui.screen.photo_display

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.ui.screen.photo_display.ingredient.Photo
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel

class PhotoDisplayFragment : Fragment() {

    companion object {
        private const val PHOTO_INFO_KEY = "photo_info_key"

        fun newInstance(photoInfo: PhotoInfo): PhotoDisplayFragment {
            val fragment = PhotoDisplayFragment()
            val args = Bundle()
            args.putSerializable(PHOTO_INFO_KEY, photoInfo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val photoDisplayViewModel =
            ViewModelProvider(this)[PhotoDisplayViewModel::class.java]

        photoDisplayViewModel.setPhotoInfo(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(PHOTO_INFO_KEY, PhotoInfo :: class.java)
            } else {
                arguments?.getSerializable(PHOTO_INFO_KEY) as PhotoInfo
            } ?: PhotoInfo()
        )

        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(LocalContext provides context) {
                    MaterialTheme {
                        PhotoDisplayComposeScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoDisplayComposeScreen(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Background()
            Photo()
            Box(
                Modifier.fillMaxSize()
            ) {

            }
        }
    }
}

@Composable
fun Background(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.white))
    ){

    }
}

@Preview
@Composable
fun Preview(){
    MaterialTheme {
        PhotoDisplayComposeScreen()
    }
}