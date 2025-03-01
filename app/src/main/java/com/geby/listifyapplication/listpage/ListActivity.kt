package com.geby.listifyapplication.listpage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.databinding.ActivityListBinding
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.utils.ViewModelFactory

class ListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListBinding
    private var categoryTitle = ""

    // Menggunakan ViewModel dengan ViewModelFactory
    private val listViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeTaskList()
    }

    private fun setupRecyclerView() {
        binding.rvListTask.layoutManager = LinearLayoutManager(this)

        val adapter = TaskCardAdapter(this, isListPage = true) { taskTitle ->
            this@ListActivity.categoryTitle = taskTitle
        }
        binding.rvListTask.adapter = adapter
    }
 
    private fun observeTaskList() {
        listViewModel.getAllTasks().observe(this) { taskList ->
            (binding.rvListTask.adapter as TaskCardAdapter).submitList(taskList)
        }
    }
}