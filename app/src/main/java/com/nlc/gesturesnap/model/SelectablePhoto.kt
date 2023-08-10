package com.nlc.gesturesnap.model

import android.net.Uri

data class SelectablePhoto(override val path: String, override val uri: Uri, var isSelecting: Boolean = false) : PhotoInfo(path, uri)
