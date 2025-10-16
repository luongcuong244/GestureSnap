package com.gesturesnap.ai.camera.ui.screen.edit

import android.graphics.*
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import com.gesturesnap.ai.camera.R
import androidx.core.graphics.createBitmap
import com.gesturesnap.ai.camera.ui.core.BaseActivity
import kotlin.math.cos
import kotlin.math.sin

class EditPhotoActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private lateinit var originalBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)

        imageView = findViewById(R.id.imageView)

        val path = intent.getStringExtra("photo_path")
        originalBitmap = BitmapFactory.decodeFile(path)
        imageView.setImageBitmap(originalBitmap)

        setupSeekBars()
    }

    private fun setupSeekBars() {
        val seekIds = listOf(
            R.id.seekExposure, R.id.seekBrightness, R.id.seekHighlights,
            R.id.seekShadows, R.id.seekContrast, R.id.seekSaturation,
            R.id.seekVibrance, R.id.seekWarmth, R.id.seekHue
        )

        for (id in seekIds) {
            findViewById<SeekBar>(id).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    applyFilters()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun applyFilters() {
        val exposure = (findViewById<SeekBar>(R.id.seekExposure).progress - 100) / 100f
        val brightness = (findViewById<SeekBar>(R.id.seekBrightness).progress - 100) / 1f
        val contrast = (findViewById<SeekBar>(R.id.seekContrast).progress / 100f)
        val saturation = (findViewById<SeekBar>(R.id.seekSaturation).progress / 100f)
        val warmth = (findViewById<SeekBar>(R.id.seekWarmth).progress - 100) / 100f
        val hue = findViewById<SeekBar>(R.id.seekHue).progress.toFloat()

        val cm = ColorMatrix()

        // Exposure / Brightness
        cm.postConcat(ColorMatrix(floatArrayOf(
            1f + exposure, 0f, 0f, 0f, brightness,
            0f, 1f + exposure, 0f, 0f, brightness,
            0f, 0f, 1f + exposure, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        )))

        // Contrast
        val scale = contrast
        val translate = (-0.5f * scale + 0.5f) * 255f
        cm.postConcat(ColorMatrix(floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        )))

        // Saturation
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)
        cm.postConcat(satMatrix)

        // Hue
        cm.postConcat(adjustHue(hue))

        val filtered = createBitmap(originalBitmap.width, originalBitmap.height)
        val canvas = Canvas(filtered)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        imageView.setImageBitmap(filtered)
    }

    private fun adjustHue(degrees: Float): ColorMatrix {
        val matrix = ColorMatrix()
        val hue = Math.toRadians(degrees.toDouble())
        val cos = cos(hue)
        val sin = sin(hue)
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f

        val mat = floatArrayOf(
            (lumR + cos * (1 - lumR) + sin * (-lumR)).toFloat(),
            (lumG + cos * (-lumG) + sin * (-lumG)).toFloat(),
            (lumB + cos * (-lumB) + sin * (1 - lumB)).toFloat(),
            0f, 0f,

            (lumR + cos * (-lumR) + sin * 0.143f).toFloat(),
            (lumG + cos * (1 - lumG) + sin * 0.14f).toFloat(),
            (lumB + cos * (-lumB) + sin * (-0.283f)).toFloat(),
            0f, 0f,

            (lumR + cos * (-lumR) + sin * (-(1 - lumR))).toFloat(),
            (lumG + cos * (-lumG) + sin * lumG).toFloat(),
            (lumB + cos * (1 - lumB) + sin * lumB).toFloat(),
            0f, 0f,

            0f, 0f, 0f, 1f, 0f
        )

        matrix.set(mat)
        return matrix
    }
}