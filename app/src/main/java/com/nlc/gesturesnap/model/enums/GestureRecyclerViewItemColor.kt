package com.nlc.gesturesnap.model.enums

import android.graphics.Color

enum class GestureRecyclerViewItemColor(val value: Int) {
    ACTIVE_COLOR(Color.WHITE),
    INACTIVE_COLOR(Color.parseColor("#80808080"))
}