package com.nlc.gesturesnap.tab.guide.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nlc.gesturesnap.databinding.FragmentGuideBinding
import com.nlc.gesturesnap.tab.guide.view_model.GuideViewModel

class GuideFragment : Fragment() {

    private var _binding: FragmentGuideBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val guideViewModel =
                ViewModelProvider(this).get(GuideViewModel::class.java)

        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGuide
        guideViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}