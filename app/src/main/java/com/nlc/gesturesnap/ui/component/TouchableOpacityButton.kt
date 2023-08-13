package com.nlc.gesturesnap.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

@Composable
fun TouchableOpacityButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    opacity: Float = 0.7f,
    content: @Composable BoxScope.() -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if(isPressed.value) opacity else 1.0f,
        label = ""
    )

    val updatedEnable = remember { mutableStateOf(enable) }
    LaunchedEffect(enable) {
        updatedEnable.value = enable
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if(!updatedEnable.value){
                            return@detectTapGestures
                        }
                        try {
                            isPressed.value = true
                            awaitRelease()
                        } finally {
                            onClick()
                            delay(200) // duration of animation
                            isPressed.value = false
                        }
                    },
                )
            }
            .alpha(alpha)
    ) {
        content()
    }
}