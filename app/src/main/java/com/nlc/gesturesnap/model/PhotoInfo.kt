package com.nlc.gesturesnap.model

import android.net.Uri
import android.util.Size
import java.io.Serializable
import java.util.Date

open class PhotoInfo(
    open val path: String = "",
    open val uri: Uri = Uri.EMPTY,
    open val name: String = "",
    open val dateTaken: Date = Date(0),
    open val size: Long = 0,
    open val resolution: Size = Size(0, 0)
)
