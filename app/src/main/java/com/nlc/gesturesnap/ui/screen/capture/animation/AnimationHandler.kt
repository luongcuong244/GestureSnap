package com.nlc.gesturesnap.ui.screen.capture.animation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.ActivityCaptureBinding
import com.nlc.gesturesnap.model.enums.CameraOption
import com.nlc.gesturesnap.model.enums.FlashOption
import com.nlc.gesturesnap.model.enums.TimerOption

class AnimationHandler(private val context: Context, private val binding: ActivityCaptureBinding) {

    private val goldenColor = Color.parseColor("#ffd700")

    fun showMenuBar(type : CameraOption){

        val startPosition : Int
        val endPosition : Int
        val startWidth : Int
        val endWidth : Int

        when(type){
            CameraOption.TIMER_OPTION -> {

                startPosition = binding.timerButton.left
                endPosition = 0
                startWidth = binding.timerButton.width
                endWidth = binding.optionBarContainer.width

                setupForMenuBarShowingAnimation(
                    binding.timerButton,
                    binding.timerViewModel?.timerOption?.value?.icon,
                    startWidth,
                    startPosition
                )

                binding.substituteItemButton.setOnClickListener {
                    hideMenuBar(CameraOption.TIMER_OPTION)
                }

                TimerOption.values().forEach {
                    val timerOption = it

                    val textView = LayoutInflater.from(context).inflate(R.layout.item_text_view, binding.menuBarListView, false) as TextView
                    textView.text = it.text

                    val isSelecting = binding.timerViewModel?.timerOption?.value?.equals(it) ?: false
                    if(isSelecting){
                        textView.setTextColor(goldenColor)
                    }

                    textView.setOnClickListener {
                        binding.timerViewModel?.setTimerOption(timerOption)
                        binding.substituteItemButton.setImageDrawable(ContextCompat.getDrawable(context, timerOption.icon))
                        hideMenuBar(CameraOption.TIMER_OPTION)
                    }

                    binding.menuBarListView.addView(textView)
                }
            }
            CameraOption.FLASH_OPTION -> {
                startPosition = binding.flashButton.left
                endPosition = 0
                startWidth = binding.flashButton.width
                endWidth = binding.optionBarContainer.width

                setupForMenuBarShowingAnimation(
                    binding.flashButton,
                    binding.cameraModeViewModel?.flashOption?.value?.icon,
                    startWidth,
                    startPosition
                )

                binding.substituteItemButton.setOnClickListener {
                    hideMenuBar(CameraOption.FLASH_OPTION)
                }

                FlashOption.values().forEach {
                    val flashOption = it

                    val textView = LayoutInflater.from(context).inflate(R.layout.item_text_view, binding.menuBarListView, false) as TextView
                    textView.text = flashOption.text

                    val isSelecting = binding.cameraModeViewModel?.flashOption?.value?.equals(it) ?: false
                    if(isSelecting){
                        textView.setTextColor(goldenColor)
                    }

                    textView.setOnClickListener {
                        binding.cameraModeViewModel?.switchFlashMode(flashOption)
                        binding.substituteItemButton.setImageDrawable(ContextCompat.getDrawable(context, flashOption.icon))
                        hideMenuBar(CameraOption.FLASH_OPTION)
                    }

                    binding.menuBarListView.addView(textView)
                }
            }
            else -> {
                return
            }
        }

        runMenuBarAnimation(startPosition, endPosition, startWidth, endWidth)
    }

    private fun setupForMenuBarShowingAnimation(realButton : ImageButton, substituteButtonIconId: Int?, menuBarWidth: Int, menuBarPosition: Int){
        val layoutParams = binding.menuBar.layoutParams
        layoutParams.width = menuBarWidth
        binding.menuBar.layoutParams = layoutParams
        binding.menuBar.x = menuBarPosition.toFloat()
        binding.menuBar.requestLayout()

        substituteButtonIconId?.let {
            binding.substituteItemButton.setImageDrawable(ContextCompat.getDrawable(context, it))
        }

        realButton.alpha = 0f
        realButton.isEnabled = false

        binding.optionBar.visibility = View.VISIBLE
        binding.menuBar.visibility = View.VISIBLE

        val fadeOutAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.optionBar.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.optionBar.startAnimation(fadeOutAnimation)

        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding.menuBarListView.startAnimation(fadeInAnimation)
    }

    private fun hideMenuBar(type: CameraOption){
        val startPosition : Int
        val endPosition : Int
        val startWidth : Int
        val endWidth : Int

        when(type){
            CameraOption.TIMER_OPTION -> {

                startPosition = 0
                endPosition = binding.timerButton.left
                startWidth = binding.optionBarContainer.width
                endWidth = binding.timerButton.width

                setupForMenuBarHidingAnimation(
                    startWidth,
                    startPosition
                )

                runMenuBarAnimation(startPosition, endPosition, startWidth, endWidth) {
                    binding.menuBar.visibility = View.GONE

                    binding.timerButton.alpha = 1f
                    binding.timerButton.isEnabled = true

                    binding.menuBarListView.removeAllViews()
                }
            }
            CameraOption.FLASH_OPTION -> {
                startPosition = 0
                endPosition = binding.flashButton.left
                startWidth = binding.optionBarContainer.width
                endWidth = binding.flashButton.width

                setupForMenuBarHidingAnimation(
                    startWidth,
                    startPosition
                )

                runMenuBarAnimation(startPosition, endPosition, startWidth, endWidth) {
                    binding.menuBar.visibility = View.GONE

                    binding.flashButton.alpha = 1f
                    binding.flashButton.isEnabled = true

                    binding.menuBarListView.removeAllViews()
                }
            }
            else -> {
                return
            }
        }
    }

    private fun setupForMenuBarHidingAnimation(menuBarWidth: Int, menuBarPosition: Int){
        val layoutParams = binding.menuBar.layoutParams
        layoutParams.width = menuBarWidth
        binding.menuBar.layoutParams = layoutParams
        binding.menuBar.x = menuBarPosition.toFloat()
        binding.menuBar.requestLayout()

        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.optionBar.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        binding.optionBar.startAnimation(fadeInAnimation)

        val fadeOutAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        binding.menuBarListView.startAnimation(fadeOutAnimation)
    }

    private fun runMenuBarAnimation(startPosition: Int, endPosition: Int, startWidth: Int, endWidth: Int, onAnimationEnd : (() -> Unit)? = null){

        val duration = 200L

        val layoutParams = binding.menuBar.layoutParams

        val widthAnimator: ValueAnimator = ValueAnimator.ofInt(startWidth, endWidth)
        widthAnimator.duration = duration
        widthAnimator.interpolator = LinearInterpolator()
        widthAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.width = value
            binding.menuBar.layoutParams = layoutParams
            binding.menuBar.requestLayout()
        }

        var positionAnimator : ValueAnimator? = null

        if(startPosition != endPosition){
            positionAnimator = ValueAnimator.ofFloat(0f, 1f)
            positionAnimator.duration = duration
            positionAnimator.interpolator = LinearInterpolator()
            positionAnimator.addUpdateListener {
                val fraction = it.animatedValue as Float
                val newX = (startPosition + fraction * (endPosition - startPosition))
                binding.menuBar.x = newX
                binding.menuBar.requestLayout()
            }
        }

        val animatorListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                if (onAnimationEnd != null) {
                    onAnimationEnd()
                }
            }

            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        }

        positionAnimator?.addListener(animatorListener)
        widthAnimator.addListener(animatorListener)

        positionAnimator?.start()
        widthAnimator.start()
    }
}