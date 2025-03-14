package com.geby.listifyapplication.listpage

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.databinding.ActivityListBinding
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.utils.ViewModelFactory
import java.util.Locale

class ListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListBinding
    private lateinit var adapter: TaskCardAdapter
    private var taskList: List<Task> = emptyList() // Awalnya kosong, nanti di-update
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
        searchFeature() // Tambahkan panggilan fitur pencarian
    }

    private fun setupRecyclerView() {
        binding.rvListTask.layoutManager = LinearLayoutManager(this)

        adapter = TaskCardAdapter(isListPage = true) { selectedTask ->
            listViewModel.update(selectedTask)
        }
        binding.rvListTask.adapter = adapter
    }

    private fun observeTaskList() {
        listViewModel.getAllTasksByCategory(status).observe(this) { tasks ->
            taskList = tasks // Simpan daftar asli sebelum pencarian
            adapter.submitList(taskList)
            binding.tvNotaskmessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
        }
        // Tampilkan judul berdasarkan kategori
        binding.tvPageTitle.text = "$status List"
    }

    private fun searchFeature() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Task>()
            for (item in taskList) {
                if (item.title!!.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(item)
                }
            }

            if (filteredList.isEmpty()) {
                binding.tvNotaskmessage.visibility = View.VISIBLE
                adapter.updateList(emptyList())
            } else {
                adapter.updateList(filteredList)
            }
        }
    }
}