package com.gesturesnap.ai.camera.model

import java.io.Serializable

open class PhotoInfo(
    open val path: String = "",
    open val name: String = "",
    open val dateTaken: Long = 0L,
    open val size: Long = 0,
    open val width: Int = 0,
    open val height: Int = 0,
) : Serializable
