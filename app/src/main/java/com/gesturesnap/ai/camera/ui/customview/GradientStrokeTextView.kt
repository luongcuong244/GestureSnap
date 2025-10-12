package com.gesturesnap.ai.camera.ui.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.gesturesnap.ai.camera.R

class GradientStrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val strokePaint = Paint()
    private val fillPaint = Paint()

    // defaults (thích hợp cho look "Logo Maker" của bạn)
    private var strokeWidthPx = dpToPx(3f)
    private var gradientColors = intArrayOf(Color.parseColor("#02C2FF"), Color.parseColor("#8E5BFA"), Color.parseColor("#AA28E5"))
    private var glowColor = Color.parseColor("#ffffff")
    private var glowRadiusPx = dpToPx(3f)

    init {
        // copy paint fill
        fillPaint.set(paint)
        fillPaint.style = Paint.Style.FILL
        fillPaint.isAntiAlias = true
        fillPaint.color = currentTextColor

        strokePaint.set(paint)
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
        strokePaint.strokeJoin = Paint.Join.ROUND
        strokePaint.strokeCap = Paint.Cap.ROUND

        // đọc từ XML
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.GradientStrokeTextView)

            strokeWidthPx = a.getDimension(R.styleable.GradientStrokeTextView_strokeWidth, dpToPx(3f))
            strokePaint.strokeWidth = strokeWidthPx

            val startColor = a.getColor(R.styleable.GradientStrokeTextView_startColor, Color.parseColor("#02C2FF"))
            val centerColor = a.getColor(R.styleable.GradientStrokeTextView_centerColor, Color.parseColor("#8E5BFA"))
            val endColor = a.getColor(R.styleable.GradientStrokeTextView_endColor, Color.parseColor("#AA28E5"))
            gradientColors = intArrayOf(startColor, centerColor, endColor)

            glowColor = a.getColor(R.styleable.GradientStrokeTextView_glowColor, Color.WHITE)
            glowRadiusPx = a.getDimension(R.styleable.GradientStrokeTextView_glowRadius, dpToPx(3f))

            a.recycle()
        }

        // enable shadow
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        strokePaint.setShadowLayer(glowRadiusPx, 0f, 0f, glowColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // shader trải theo chiều ngang của view (từ trái -> phải)
        if (w > 0) {
            val shader = LinearGradient(
                0f, 0f, w.toFloat(), 0f,
                gradientColors, null, Shader.TileMode.CLAMP
            )
            strokePaint.shader = shader
        }
    }

    override fun onDraw(canvas: Canvas) {
        val textStr = text?.toString() ?: return
        if (textStr.isEmpty()) return

        // Nếu TextView có layout (multi-line), vẽ từng dòng theo layout để giữ alignment chính xác
        val tvLayout = layout
        if (tvLayout != null) {
            val lineCount = tvLayout.lineCount
            for (i in 0 until lineCount) {
                val start = tvLayout.getLineStart(i)
                val end = tvLayout.getLineEnd(i)
                val line = textStr.substring(start, end)
                val x = tvLayout.getLineLeft(i) + paddingLeft.toFloat()
                val baseline = tvLayout.getLineBaseline(i) + paddingTop.toFloat()

                // stroke trước, fill sau (stroke rộng bao quanh)
                canvas.drawText(line, x, baseline, strokePaint)
                canvas.drawText(line, x, baseline, fillPaint)
            }
        } else {
            // fallback (single-line)
            val x = (width - paint.measureText(textStr)) / 2f
            val y = (height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
            canvas.drawText(textStr, x, y, strokePaint)
            canvas.drawText(textStr, x, y, fillPaint)
        }
    }

    // helper convert dp -> px
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    // --- Optional: công cụ để chỉnh động (nếu muốn) ---
    fun setStrokeWidthDp(dp: Float) {
        strokeWidthPx = dpToPx(dp)
        strokePaint.strokeWidth = strokeWidthPx
        invalidate()
    }

    fun setGradientColors(leftColor: Int, rightColor: Int) {
        gradientColors = intArrayOf(leftColor, rightColor)
        // shader sẽ được cập nhật ở onSizeChanged (hoặc gọi lại đây nếu width đã có)
        if (width > 0) {
            onSizeChanged(width, height, width, height)
        }
        invalidate()
    }

    fun setGlow(color: Int, radiusDp: Float) {
        glowColor = color
        glowRadiusPx = dpToPx(radiusDp)
        strokePaint.setShadowLayer(glowRadiusPx, 0f, 0f, glowColor)
        invalidate()
    }
}