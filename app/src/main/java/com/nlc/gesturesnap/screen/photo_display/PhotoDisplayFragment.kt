package com.nlc.gesturesnap.screen.photo_display

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.screen.photo_display.ui.PhotoDisplayComposeScreen
import com.nlc.gesturesnap.screen.photo_display.view_model.PhotoDisplayViewModel

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
            } ?: PhotoInfo("", Uri.EMPTY)
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

@Preview
@Composable
fun Preview(){
    MaterialTheme {
        PhotoDisplayComposeScreen()
    }
}