package com.nlc.gesturesnap.screen.capture.ui

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.ActivityCaptureBinding
import com.nlc.gesturesnap.screen.capture.ui.component.GestureDetectAdapter
import com.nlc.gesturesnap.screen.capture.ui.listener.ItemClickListener
import com.nlc.gesturesnap.screen.capture.ui.view.CameraFragment
import com.nlc.gesturesnap.screen.capture.view_model.CameraAspectRatioViewModel
import com.nlc.gesturesnap.screen.capture.view_model.GestureDetectViewModel


class CaptureActivity : AppCompatActivity() {

    private var _binding: ActivityCaptureBinding? = null
    
    private val binding get() = _binding!!

    private var gestureDetectAdapter : GestureDetectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CameraFragment())
            .commit()

        setUpGestureDetectViewModel()
        setUpCameraResolutionViewModel()
    }

    private fun setUpGestureDetectViewModel() {
        val gestureDetectViewModel =
            ViewModelProvider(this)[GestureDetectViewModel::class.java]

        gestureDetectViewModel.handGestureOptions.observe(this) {

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            gestureDetectAdapter = GestureDetectAdapter(this, it, object : ItemClickListener {
                override fun onItemClicked(index: Int) {
                    gestureDetectViewModel.setCurrentHandGesture(index)
                }
            })

            binding.recyclerGestureDetect.adapter = gestureDetectAdapter
            binding.recyclerGestureDetect.layoutManager = layoutManager
        }

        gestureDetectViewModel.createOptionList(this)

        gestureDetectViewModel.currentHandGesture.observe(this) {
            gestureDetectAdapter?.updateItem(it)
        }

        gestureDetectViewModel.setCurrentHandGesture(0)
    }

    private fun setUpCameraResolutionViewModel() {

        val deviceWidth = resources.displayMetrics.widthPixels

        val cameraAspectRatioViewModel =
            ViewModelProvider(this)[CameraAspectRatioViewModel::class.java]

        cameraAspectRatioViewModel.cameraAspectRatio.observe(this) {

            var layoutParams : FrameLayout.LayoutParams? = null

            when(it){
                AspectRatio.RATIO_4_3 -> {
                    layoutParams = FrameLayout.LayoutParams(deviceWidth, deviceWidth * 4 / 3)
                }
                AspectRatio.RATIO_16_9 -> {
                    layoutParams = FrameLayout.LayoutParams(deviceWidth, deviceWidth * 16 / 9)
                }
            }

            if(layoutParams != null){
                binding.fragmentContainer.layoutParams = layoutParams
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}