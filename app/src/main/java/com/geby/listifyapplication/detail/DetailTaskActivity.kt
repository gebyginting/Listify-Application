package com.geby.listifyapplication.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.geby.listifyapplication.databinding.ActivityDetailTaskBinding

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskTitle = intent.getStringExtra("TASK_TITLE")
        binding.tvTaskname.text = taskTitle

    }
}