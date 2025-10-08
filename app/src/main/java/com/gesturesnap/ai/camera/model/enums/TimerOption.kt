package com.gesturesnap.ai.camera.model.enums

import com.gesturesnap.ai.camera.R

enum class TimerOption(val icon : Int, val value: Int, val text: String){
    OFF(R.drawable.ic_timer, 0, "Off"),
    _3S(R.drawable.ic_timer_3s, 3, "3s"),
    _5S(R.drawable.ic_timer_5s, 5, "5s"),
    _10S(R.drawable.ic_timer_10s, 10, "10s"),
}