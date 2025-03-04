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
    private lateinit var status: String

    // Menggunakan ViewModel dengan ViewModelFactory
    private val listViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        status = intent.getStringExtra("TASK_STATUS") ?: "On Going" // Default jika null

        observeTaskList()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvListTask.layoutManager = LinearLayoutManager(this)

        adapter = TaskCardAdapter(isListPage = true) { selectedTask ->
            listViewModel.update(selectedTask)
        }
        binding.rvListTask.adapter = adapter
    }

    private fun observeTaskList() {
        listViewModel.getAllTasksByCategory(status).observe(this) { taskList ->
            adapter.submitList(taskList)
            binding.tvNotaskmessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
        }
        // Tampilkan judul berdasarkan kategori
        binding.tvPageTitle.text = "$status List"
    }
}