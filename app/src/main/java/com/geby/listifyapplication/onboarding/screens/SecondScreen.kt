package com.geby.listifyapplication.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geby.listifyapplication.R
import com.geby.listifyapplication.databinding.FragmentSecondScreenBinding
import com.geby.listifyapplication.user.UserModel
import com.geby.listifyapplication.user.UserPreference

@Suppress("DEPRECATION")
class SecondScreen : Fragment() {

    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!
    private var userModel: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        userModel = arguments?.getParcelable("USER") ?: UserModel(name = "Guest")

        setUser()
        return  view
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    private fun setUser() {
        binding.startButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            if (username.isNotEmpty()) {
                saveUser(username)
                findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
                onBoardingFinished()
            }
            else {
                Toast.makeText(requireContext(), "Nama Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveUser(name: String) {
        val userPreference = UserPreference(requireContext())
        val newUser = UserModel(name = name) // Buat instance baru
        userPreference.setUser(newUser)
        Toast.makeText(requireContext(), "Data tersimpan", Toast.LENGTH_SHORT).show()
    }

}