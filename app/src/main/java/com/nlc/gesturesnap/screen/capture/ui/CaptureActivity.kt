package com.nlc.gesturesnap.screen.capture.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.ActivityCaptureBinding
import com.nlc.gesturesnap.screen.capture.view_model.CaptureViewModel

class CaptureActivity : AppCompatActivity() {

    private var _binding: ActivityCaptureBinding? = null
    
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCaptureBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CameraFragment())
            .commit()

        val captureViewModel =
            ViewModelProvider(this).get(CaptureViewModel::class.java)

        captureViewModel.text.observe(this) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}