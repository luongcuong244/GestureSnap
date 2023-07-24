package com.nlc.gesturesnap.helper

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.view.OrientationEventListener
import android.view.Surface
import androidx.lifecycle.LiveData


/**
 * Calculates closest 90-degree orientation to compensate for the device
 * rotation relative to sensor orientation, i.e., allows user to see camera
 * frames with the expected orientation.
 */
class OrientationLiveData(
    context: Context
) : LiveData<Int>() {

    private val listener = object : OrientationEventListener(context.applicationContext) {
        override fun onOrientationChanged(orientation: Int) {
            val rotation = when {
                orientation <= 45 -> 0
                orientation <= 135 -> 90
                orientation <= 225 -> 180
                orientation <= 315 -> 270
                else -> 0
            }
            if (rotation != value) postValue(rotation)
        }
    }

    override fun onActive() {
        super.onActive()
        listener.enable()
    }

    override fun onInactive() {
        super.onInactive()
        listener.disable()
    }
}