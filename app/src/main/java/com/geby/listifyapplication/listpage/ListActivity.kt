package com.geby.listifyapplication.listpage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.listifyapplication.databinding.ActivityListBinding
import com.geby.listifyapplication.taskcard.TaskCardAdapter
import com.geby.listifyapplication.taskcard.TaskCardDataSource

class ListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListBinding
    private var categoryTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskList()
    }

    private fun taskList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvListTask.layoutManager = layoutManager
        val cardData = TaskCardDataSource.getCardData()
        val adapter = TaskCardAdapter(this, isListPage = true) { category ->
            this@ListActivity.categoryTitle = category
        }
        binding.rvListTask.adapter = adapter
        adapter.submitList(cardData)
        binding.rvListTask.layoutManager = layoutManager
    }
}