package com.gesturesnap.ai.camera.ui.screen.edit

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.gesturesnap.ai.camera.databinding.ActivityEditPhotoBinding
import com.gesturesnap.ai.camera.helper.MediaHelper
import com.gesturesnap.ai.camera.ui.core.BaseActivity
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter

class EditPhotoActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPhotoBinding
    private lateinit var gpuImage: GPUImage
    private lateinit var filterGroup: GPUImageFilterGroup // Nhóm bộ lọc để áp dụng tất cả các điều chỉnh

    // Các bộ lọc riêng biệt cho từng thông số
    private val exposureFilter = GPUImageExposureFilter(0f)    // Phơi sáng
    private val contrastFilter = GPUImageContrastFilter(1f)      // Tương phản
    private val saturationFilter = GPUImageSaturationFilter(1f)  // Độ bão hòa
    private val temperatureFilter = GPUImageWhiteBalanceFilter(5000f, 0f) // Độ ấm (Tông màu/Nhiệt độ)
    private val brightnessFilter = GPUImageBrightnessFilter(0f) // Độ chói
    private val highlightShadowFilter = GPUImageHighlightShadowFilter(0f, 0f) // (Highlights, Shadows)
    // private val vibranceFilter = GPUImageVibranceFilter(0f) // Độ tươi

    // Lưu ý: Độ chói, Vùng sáng, Vùng tối, Độ tươi cần các bộ lọc phức tạp hơn
    // GPUImage có bộ lọc GPUImageHighlightsShadowFilter cho Vùng sáng/tối.

    companion object {
        const val EXTRA_PHOTO_PATH = "photo_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gpuImage = GPUImage(this)
        gpuImage.setGLSurfaceView(binding.gpuImageView)

        filterGroup = GPUImageFilterGroup(
            listOf(
                exposureFilter,
                brightnessFilter,
                contrastFilter,
                saturationFilter,
                temperatureFilter,
                highlightShadowFilter,
                // vibranceFilter
            )
        )
        gpuImage.setFilter(filterGroup)

        val photoPath = intent.getStringExtra(EXTRA_PHOTO_PATH)
        loadPhoto(photoPath)

        setupSeekBars()

        binding.btnSave.setOnClickListener {
            saveEditedImage()
        }
    }

    private fun loadPhoto(path: String?) {
        if (path != null) {
            try {
                val uri = MediaHelper.getUriFromPath(this, path)
                // Đảm bảo GPUImage load ảnh từ URI
                gpuImage.setImage(uri)
                binding.gpuImageView.requestRender()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Không thể tải ảnh: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Không tìm thấy đường dẫn ảnh", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupSeekBars() {
        // --- Phơi Sáng (Exposure) ---
        // Range: -10.0 đến 10.0 (Giá trị mặc định: 0.0)
        binding.seekBarExposure.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Chuyển đổi progress (0-200) sang (-10.0f đến 10.0f)
                val value = (progress - 100) / 10f
                exposureFilter.setExposure(value)
                binding.gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // --- Tương Phản (Contrast) ---
        // Range: 0.0 đến 4.0 (Giá trị mặc định: 1.0)
        binding.seekBarContrast.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Chuyển đổi progress (0-200) sang (0.0f đến 2.0f) - giữ an toàn
                val value = progress / 100f * 2.0f
                contrastFilter.setContrast(value)
                binding.gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // --- Độ Bão Hòa (Saturation) ---
        // Range: 0.0 đến 2.0 (Giá trị mặc định: 1.0)
        binding.seekBarSaturation.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Chuyển đổi progress (0-200) sang (0.0f đến 2.0f)
                val value = progress / 100f
                saturationFilter.setSaturation(value)
                binding.gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // --- Độ Ấm (Temperature) ---
        // Range: 2000.0 (lạnh) đến 9000.0 (ấm) (Giá trị mặc định: 5000.0)
        binding.seekBarTemperature.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Chuyển đổi progress (0-100) sang (2000.0f đến 9000.0f)
                val minTemp = 2000f
                val maxTemp = 9000f
                val value = minTemp + (progress / 100f) * (maxTemp - minTemp)
                // Chỉ set temperature
                temperatureFilter.setTemperature(value)
                binding.gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // --- Độ Chói (Brightness) ---
        binding.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Range: -1.0 đến +1.0
                val value = (progress - 100) / 100f
                brightnessFilter.setBrightness(value)
                binding.gpuImageView.requestRender()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

// --- Vùng Sáng (Highlights) ---
        binding.seekBarHighlights.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 0 -> 1.0
                val value = progress / 100f
                highlightShadowFilter.setHighlights(value)
                binding.gpuImageView.requestRender()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

// --- Vùng Tối (Shadows) ---
        binding.seekBarShadows.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress / 100f
                highlightShadowFilter.setShadows(value)
                binding.gpuImageView.requestRender()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

// --- Độ Tươi (Vibrance) ---
        /*binding.seekBarVibrance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = (progress - 100) / 100f
                vibranceFilter.setVibrance(value)
                binding.gpuImageView.requestRender()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })*/
    }

    private fun saveEditedImage() {
        // Yêu cầu GPUImage xử lý ảnh và lưu vào một tệp mới
        val path =
            getExternalFilesDir(null)?.absolutePath + "/edited_image_${System.currentTimeMillis()}.jpg"

        /*gpuImage.saveToPictures(
            File(path),
            "image_editor",
            object : GPUImage.On==GPUImageResponseListener {
                override fun onResponse(bitmap: Bitmap) {
                    runOnUiThread {
                        Toast.makeText(this@EditPhotoActivity, "Ảnh đã lưu thành công tại: $path", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onResponse(uri: Uri?) {
                    runOnUiThread {
                        Toast.makeText(this@EditPhotoActivity, "Ảnh đã lưu thành công", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )*/
    }
}