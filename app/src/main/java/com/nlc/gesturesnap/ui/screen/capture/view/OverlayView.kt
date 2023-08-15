package com.nlc.gesturesnap.ui.screen.capture.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.nlc.gesturesnap.R
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: GestureRecognizerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private var deviceRotation = 0

    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { gestureRecognizerResult ->
            for(landmark in gestureRecognizerResult.landmarks()) {
                for(normalizedLandmark in landmark) {

                    val pX = normalizedLandmark.x() * imageWidth * scaleFactor
                    val pY = normalizedLandmark.y() * imageHeight * scaleFactor

                    val calculatedPoint = calculateCoordinate(
                        originalX = pX,
                        originalY = pY
                    )

                    canvas.drawPoint(
                        calculatedPoint.x,
                        calculatedPoint.y,
                        pointPaint
                    )
                }

                HandLandmarker.HAND_CONNECTIONS.forEach {

                    val xStart = gestureRecognizerResult.landmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor
                    val yStart = gestureRecognizerResult.landmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor
                    val xStop = gestureRecognizerResult.landmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor
                    val yStop = gestureRecognizerResult.landmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor

                    val calculatedStartCoords = calculateCoordinate(
                        originalX = xStart,
                        originalY = yStart
                    )

                    val calculatedStopCoords = calculateCoordinate(
                        originalX = xStop,
                        originalY = yStop
                    )

                    canvas.drawLine(
                        calculatedStartCoords.x,
                        calculatedStartCoords.y,
                        calculatedStopCoords.x,
                        calculatedStopCoords.y,
                        linePaint
                    )
                }
            }
        }
    }

    fun setResults(
        gestureRecognizerResult: GestureRecognizerResult,
        imageHeight: Int,
        imageWidth: Int,
        deviceRotation: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = gestureRecognizerResult

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        this.deviceRotation = deviceRotation

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                // when the device changes orientation, the Overlay View doesn't change orientation
                if(deviceRotation == 90 || deviceRotation == 270){
                    min(width * 1f / imageHeight, height * 1f / imageWidth)
                } else {
                    min(width * 1f / imageWidth, height * 1f / imageHeight)
                }
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.

                // when the device changes orientation, the Overlay View doesn't change orientation
                if(deviceRotation == 90 || deviceRotation == 270){
                    max(width * 1f / imageHeight, height * 1f / imageWidth)
                } else {
                    max(width * 1f / imageWidth, height * 1f / imageHeight)
                }
            }
        }
        invalidate()
    }

    // when the device changes orientation, the Overlay View doesn't change orientation
    // so we need to rotate the orientation of the coordinate axis
    private fun calculateCoordinate(originalX: Float, originalY: Float) : PointF{
        return when(deviceRotation){
            90 -> PointF(originalY, height - originalX)
            180 -> PointF(width - originalX, height - originalY)
            270 -> PointF(width - originalY, originalX)
            else -> PointF(originalX, originalY)
        }
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}