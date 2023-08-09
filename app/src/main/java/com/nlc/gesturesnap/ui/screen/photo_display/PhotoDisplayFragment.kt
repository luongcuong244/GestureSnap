package com.nlc.gesturesnap.ui.screen.photo_display

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.model.PhotoInfo
import com.nlc.gesturesnap.ui.screen.photo_display.ingredient.Background
import com.nlc.gesturesnap.ui.screen.photo_display.ingredient.BottomBar
import com.nlc.gesturesnap.ui.screen.photo_display.ingredient.Header
import com.nlc.gesturesnap.ui.screen.photo_display.ingredient.Photo
import com.nlc.gesturesnap.view_model.photo_display.PhotoDisplayViewModel
import java.io.Serializable

class PhotoDisplayFragment : Fragment() {

    companion object {
        private const val ARGUMENT_KEY = "argument_key"

        fun newInstance(argument: Argument): PhotoDisplayFragment {
            val fragment = PhotoDisplayFragment()
            val args = Bundle()
            args.putSerializable(ARGUMENT_KEY, argument)
            fragment.arguments = args
            return fragment
        }
    }

    class Argument (
        val initialPhotoSize : DpSize = DpSize.Zero,
        val initialPhotoPosition : Offset = Offset.Zero,
        val photo: PhotoInfo = PhotoInfo()
    ) : Serializable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val photoDisplayViewModel =
            ViewModelProvider(this)[PhotoDisplayViewModel::class.java]

        photoDisplayViewModel.setFragmentArgument(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(ARGUMENT_KEY, Argument :: class.java)
            } else {
                arguments?.getSerializable(ARGUMENT_KEY) as Argument
            } ?: Argument()
        )

        photoDisplayViewModel.setIsFragmentOpen(true)

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

    val screenSizePx = remember {
        mutableStateOf(IntSize.Zero)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    screenSizePx.value = it.size
                }
        ) {
            Background()
            if(screenSizePx.value != IntSize.Zero){
                Photo(screenSizePx.value)
            }
            ViewContainer()
        }
    }
}

@Composable
fun ViewContainer(photoDisplayViewModel: PhotoDisplayViewModel = viewModel()){

    val isFragmentOpen = remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if(isFragmentOpen.value) 1f else 0f,
        animationSpec = tween(durationMillis = AppConstant.ANIMATION_DURATION_MILLIS),
        label = "",
    )

    LaunchedEffect(photoDisplayViewModel.isFragmentOpen.value){
        isFragmentOpen.value = photoDisplayViewModel.isFragmentOpen.value
    }

    Box(
        Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        Header()
        BottomBar()
    }
}