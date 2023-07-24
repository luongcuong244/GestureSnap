package com.nlc.gesturesnap.screen.capture.ui.value

import com.nlc.gesturesnap.R

enum class TimerOption(val icon : Int, val value: Int, val text: String){
    _10S(R.drawable.ic_timer_10s, 10, "10s"),
    _5S(R.drawable.ic_timer_5s, 5, "5s"),
    _3S(R.drawable.ic_timer_3s, 3, "3s"),
    OFF(R.drawable.ic_timer, 0, "Off");
}