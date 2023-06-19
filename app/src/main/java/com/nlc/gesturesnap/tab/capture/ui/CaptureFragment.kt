package com.nlc.gesturesnap.tab.capture.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nlc.gesturesnap.databinding.FragmentCaptureBinding
import com.nlc.gesturesnap.tab.capture.view_model.CaptureViewModel

class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        val captureViewModel =
            ViewModelProvider(this).get(CaptureViewModel::class.java)

        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCapture
        captureViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}