package com.geby.listifyapplication.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.geby.listifyapplication.R
import com.geby.listifyapplication.databinding.FragmentFirstScreenBinding

class FirstScreen : Fragment() {


    private var _binding: FragmentFirstScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.button.setOnClickListener {
            viewPager?.currentItem = 1
        }
        return  view
    }

}