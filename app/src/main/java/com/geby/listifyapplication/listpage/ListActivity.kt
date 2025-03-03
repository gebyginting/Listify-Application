package com.geby.listifyapplication.listpage

import android.os.Bundle
import android.view.View
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
    private lateinit var adapter: TaskCardAdapter

    // Menggunakan ViewModel dengan ViewModelFactory
    private val listViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeTaskList()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvListTask.layoutManager = LinearLayoutManager(this)

        adapter = TaskCardAdapter(isListPage = true)
        binding.rvListTask.adapter = adapter
    }

    private fun observeTaskList() {
        listViewModel.getAllTasks().observe(this@ListActivity) { taskList ->
            adapter.submitList(taskList)
            binding.tvNotaskmessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}