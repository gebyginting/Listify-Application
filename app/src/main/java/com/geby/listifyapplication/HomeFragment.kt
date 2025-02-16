package com.geby.listifyapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.addtask.AddTaskActivity
import com.geby.listifyapplication.categorycards.CategoryCardAdapter
import com.geby.listifyapplication.databinding.FragmentHomeBinding
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.taskcard.TaskCardDataSource
import com.geby.quizup.CategoryCardDataSource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var categoryTitle = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aktifkan Edge-to-Edge
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.apply {
                    hide(android.view.WindowInsets.Type.systemBars()) // Sembunyikan status bar
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryCard.layoutManager = layoutManager
        val cardData = CategoryCardDataSource.getCardData()
        val adapter = CategoryCardAdapter { category ->
            this@HomeFragment.categoryTitle = category
        }
        binding.rvCategoryCard.adapter = adapter
        adapter.submitList(cardData)
        binding.rvCategoryCard.layoutManager = layoutManager

        todayTaskList()
        addTaskPage()
        return  view
    }

    private fun todayTaskList() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodayTask.layoutManager = layoutManager
        val cardData = TaskCardDataSource.getCardData()
        val limitedData = cardData.take(3)
        val adapter = TaskCardAdapter { category ->
            this@HomeFragment.categoryTitle = category
        }
        binding.rvTodayTask.adapter = adapter
        adapter.submitList(limitedData)
        binding.rvTodayTask.layoutManager = layoutManager
    }

    private fun addTaskPage() {
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }
    }
}