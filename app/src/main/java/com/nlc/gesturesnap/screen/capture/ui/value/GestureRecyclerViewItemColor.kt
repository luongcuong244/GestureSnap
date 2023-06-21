package com.nlc.gesturesnap.screen.capture.ui.value

import android.graphics.Color

enum class GestureRecyclerViewItemColor(val value: Int) {
    ACTIVE_COLOR(Color.WHITE),
    INACTIVE_COLOR(Color.parseColor("#80000000"))
}