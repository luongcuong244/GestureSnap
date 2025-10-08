package com.gesturesnap.ai.camera.model

import android.net.Uri
import android.util.Size
import java.util.Date

data class SelectablePhoto(
    override val path: String,
    override val uri: Uri,
    override val name: String,
    override val dateTaken: Date,
    override val size: Long,
    override val resolution: Size,
    var isSelecting: Boolean = false
) : PhotoInfo(
    path = path,
    uri = uri,
    name = name,
    dateTaken = dateTaken,
    size = size,
    resolution = resolution
)
