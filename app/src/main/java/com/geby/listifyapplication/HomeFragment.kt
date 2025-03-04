package com.geby.listifyapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.addtask.AddTaskActivity
import com.geby.listifyapplication.categorycards.CategoryCardAdapter
import com.geby.listifyapplication.databinding.FragmentHomeBinding
import com.geby.listifyapplication.listpage.ListActivity
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.utils.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // Setup UI
        setupCategoryCards()
        setupTodayTaskList()
        setupAddTaskButton()
        setupSeeAllTasksButton()
        return view
    }

    private fun setupCategoryCards() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryCard.layoutManager = layoutManager

        val adapter = CategoryCardAdapter { selectedStatus ->
            // Pindah ke fragment atau activity yang menampilkan task berdasarkan status
            val intent = Intent(requireContext(), ListActivity::class.java).apply {
                putExtra("TASK_STATUS", selectedStatus)
            }
            startActivity(intent)
        }
        binding.rvCategoryCard.adapter = adapter

        homeViewModel.getCategoryCards().observe(viewLifecycleOwner) { categoryList ->
            adapter.submitList(categoryList)
        }
    }

    private fun setupTodayTaskList() {
        val adapter = TaskCardAdapter { selectedTask ->
            homeViewModel.update(selectedTask)
        }
        binding.rvTodayTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodayTask.adapter = adapter

        homeViewModel.getAllTasksByCategory("On Going").observe(viewLifecycleOwner) { taskList ->
            Log.d("DEBUG", "Task List: ${taskList.map { it.title }}")
            adapter.submitList(taskList.take(3))
            binding.tvNotaskmessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupAddTaskButton() {
        binding.addTaskButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSeeAllTasksButton() {
        binding.tvButtonseeall.setOnClickListener {
            val intent = Intent(requireContext(), com.geby.listifyapplication.listpage.ListActivity::class.java)
            startActivity(intent)
        }
    }

    //    agar tidak memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}