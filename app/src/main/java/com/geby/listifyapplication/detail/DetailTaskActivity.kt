package com.geby.listifyapplication.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.databinding.ActivityDetailTaskBinding
import com.geby.listifyapplication.utils.ViewModelFactory

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailTaskBinding
    private lateinit var homeViewModel: HomeViewModel
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = obtainViewModel(this@DetailTaskActivity)
        taskId = intent.getIntExtra("TASK_ID", -1)

        if (taskId != -1) {
            homeViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    binding.tvTaskname.text = task.title
                    binding.tvDescription.text = task.description
                }
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): HomeViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HomeViewModel::class.java]
    }
}