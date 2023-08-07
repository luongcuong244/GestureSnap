package com.nlc.gesturesnap.model

import android.net.Uri
import java.io.Serializable

open class PhotoInfo(open val path: String = "", open val uri: Uri = Uri.EMPTY) : Serializable {
}
