package com.nlc.gesturesnap.screen.capture.model

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.nlc.gesturesnap.screen.capture.ui.value.GestureCategory
import com.nlc.gesturesnap.screen.capture.ui.value.GestureRecyclerViewItemColor

class GestureDetectOption (
    val inactiveIcon: Drawable,
    val text: String,
    val gestureCategory: GestureCategory,
    var isSelecting: Boolean = false,
    val progressIcon: Drawable? = null,
) {

    val activeIcon: Drawable = inactiveIcon.constantState?.newDrawable()?.mutate()!!

    init {
        DrawableCompat.setTint(this.activeIcon, GestureRecyclerViewItemColor.ACTIVE_COLOR.value)
        DrawableCompat.setTint(this.inactiveIcon, GestureRecyclerViewItemColor.INACTIVE_COLOR.value)
    }
}