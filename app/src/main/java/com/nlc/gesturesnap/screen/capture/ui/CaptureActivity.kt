package com.nlc.gesturesnap.screen.capture.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.ActivityCaptureBinding
import com.nlc.gesturesnap.screen.capture.ui.component.GestureDetectAdapter
import com.nlc.gesturesnap.screen.capture.ui.listener.ItemClickListener
import com.nlc.gesturesnap.screen.capture.ui.view.CameraFragment
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

//        val width = resources.displayMetrics.widthPixels
//
//        // Đặt kích thước cho view
//        // Ví dụ: 1:1, 16:9, hoặc 4:3
//
//        // int height = width * 9 / 16; // 16:9
//        // int height = width * 3 / 4; // 4:3
//        val layoutParams = FrameLayout.LayoutParams(width, width * 4 / 3)
//        binding.fragmentContainer.layoutParams = layoutParams
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}