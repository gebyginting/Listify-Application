package com.geby.listifyapplication.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.geby.listifyapplication.HomeViewModel
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.databinding.ActivityDetailTaskBinding
import com.geby.listifyapplication.edit.EditTaskActivity
import com.geby.listifyapplication.utils.DateHelper
import com.geby.listifyapplication.utils.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailTaskBinding
    private lateinit var currentTask: Task
    private var taskId: Int = -1

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getIntExtra("TASK_ID", -1)

        setUp()
        showDetailTask()
        deleteTask()
    }

    private fun setUp() {
        with(binding) {
            editButton.setOnClickListener {
                val intent = Intent(this@DetailTaskActivity, EditTaskActivity::class.java).apply {
                    putExtra("TASK_ID", taskId)
                }
                startActivity(intent)
            }
            backButton.setOnClickListener {
                finish()
            }

        }
    }
    private fun showDetailTask() {
        if (taskId != -1) {
            homeViewModel.getTaskById(taskId).observe(this) { task ->
                currentTask = task
                with(binding) {
                    task?.let {
                        tvTaskname.text = task.title
                        tvDescription.text = task.description
                        tvTaskstatus.text = task.status
                        tvDate.text = DateHelper.formatDate(task.date)
                        tvTime.text = DateHelper.formatTime(task.date)
                    }
                }
            }
        }
    }

    private fun deleteTask() {
        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete This Task?")
                .setMessage("Your task will be delete permanently.")
                .setPositiveButton("Yes") { _, _ ->
                    homeViewModel.delete(currentTask)

                    homeViewModel.getTaskById(taskId).observe(this) { task ->
                        if (task == null) {  // Task sudah berhasil dihapus
                            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                            finish()  // Kembali ke halaman sebelumnya
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}