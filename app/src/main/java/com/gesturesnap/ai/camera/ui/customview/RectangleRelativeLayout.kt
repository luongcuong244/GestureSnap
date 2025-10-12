package com.gesturesnap.ai.camera.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.gesturesnap.ai.camera.R
import androidx.core.content.withStyledAttributes

class RectangleRelativeLayout : RelativeLayout {

    private var _aspectRatio = 1F
    private var _dependOnDimension = 0

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getContext().withStyledAttributes(attrs, R.styleable.RectangleRelativeLayout) {
            _aspectRatio = getFloat(R.styleable.RectangleRelativeLayout_rectAspectRatio, 1F)
            _dependOnDimension = getInt(R.styleable.RectangleRelativeLayout_dependOnDimension, 0)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getContext().withStyledAttributes(attrs, R.styleable.RectangleRelativeLayout) {
            _aspectRatio = getFloat(R.styleable.RectangleRelativeLayout_rectAspectRatio, 1F)
            _dependOnDimension = getInt(R.styleable.RectangleRelativeLayout_dependOnDimension, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // If dependOnDimension is 0, then the width is the dependent dimension
        if (_dependOnDimension == 0) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width / _aspectRatio).toInt()
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            return
        }

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = (height * _aspectRatio).toInt()
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }
}