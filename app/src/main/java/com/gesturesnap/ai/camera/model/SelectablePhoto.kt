package com.gesturesnap.ai.camera.model

data class SelectablePhoto(
    override val path: String,
    override val name: String,
    override val dateTaken: Long,
    override val size: Long,
    override val width: Int = 0,
    override val height: Int = 0,
    var isSelecting: Boolean = false
) : PhotoInfo(
    path = path,
    name = name,
    dateTaken = dateTaken,
    size = size,
    width = width,
    height = height,
)
