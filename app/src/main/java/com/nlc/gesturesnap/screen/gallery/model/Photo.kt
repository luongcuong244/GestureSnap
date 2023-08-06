package com.nlc.gesturesnap.screen.gallery.model

import android.net.Uri
import com.nlc.gesturesnap.model.PhotoInfo

data class Photo(override val path: String, override val uri: Uri, var isSelecting: Boolean = false) : PhotoInfo(path, uri)
