package com.nlc.gesturesnap.tab.capture.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.R
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

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CameraFragment())
            .commit()

        val captureViewModel =
            ViewModelProvider(this).get(CaptureViewModel::class.java)

        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        captureViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}