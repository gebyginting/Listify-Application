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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.addtask.AddTaskActivity
import com.geby.listifyapplication.categorycards.CategoryCardAdapter
import com.geby.listifyapplication.databinding.FragmentHomeBinding
import com.geby.listifyapplication.detail.DetailTaskActivity
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.utils.ViewModelFactory
import com.geby.quizup.CategoryCardDataSource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
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

        // ViewModel
        homeViewModel = obtainViewModel()

        // Setup UI
        setupCategoryCards()
        setupTodayTaskList()
        setupAddTaskButton()
        setupSeeAllTasksButton()
        return  view
    }

    private fun setupCategoryCards() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryCard.layoutManager = layoutManager

        val adapter = CategoryCardAdapter { _ ->

        }
        binding.rvCategoryCard.adapter = adapter
        adapter.submitList(CategoryCardDataSource.getCardData())
    }

    private fun setupTodayTaskList() {
        binding.rvTodayTask.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TaskCardAdapter(requireContext()) { taskTitle ->
            val intent = Intent(requireContext(), DetailTaskActivity::class.java)
            intent.putExtra("TASK_TITLE", taskTitle)
            startActivity(intent)
        }
        binding.rvTodayTask.adapter = adapter
        // **Ambil Data dari ViewModel**
        homeViewModel.getAllTasks().observe(viewLifecycleOwner) { taskList ->
            if (taskList.isNotEmpty()) {
                adapter.submitList(taskList.take(3)) // Hanya ambil 3 tugas pertama
            } else {
                binding.tvNotaskmessage.visibility = View.VISIBLE
            }
        }    }

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

    private fun obtainViewModel(): HomeViewModel {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        return ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    //    agar tidak memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_ADD_TASK && resultCode == Activity.RESULT_OK) {
//            // ✅ Perbarui daftar task setelah kembali dari AddTaskActivity
//            homeViewModel.getAllTasks()
//        }
//    }

//    companion object {
//        const val REQUEST_ADD_TASK = 1
//    }


}