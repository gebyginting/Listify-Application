package com.geby.listifyapplication.taskcard

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geby.listifyapplication.R
import com.geby.listifyapplication.database.Task
import com.geby.listifyapplication.databinding.TaskCardBinding
import com.geby.listifyapplication.detail.DetailTaskActivity
import com.geby.listifyapplication.utils.DateHelper

class TaskCardAdapter(
    private val isListPage: Boolean = false) : ListAdapter<Task, TaskCardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, isListPage)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    class MyViewHolder(
        private val binding: TaskCardBinding,
        private val isListPage: Boolean 
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(task: Task) {
            binding.tvItemTitle.text = task.title
            binding.tvItemDate.text = DateHelper.formatTime(task.date)

            if (isListPage) {
                binding.cvItemNote.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.list_page_task_card)

            }
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }
}
