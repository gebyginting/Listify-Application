package com.geby.listifyapplication.taskcard

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private val isListPage: Boolean = false,
    private val onTaskCompleted: (Task) -> Unit
) : ListAdapter<Task, TaskCardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, isListPage, onTaskCompleted)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    class MyViewHolder(
        private val binding: TaskCardBinding,
        private val isListPage: Boolean,
        private val onTaskCompleted: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(task: Task) {
            binding.tvItemTitle.text = task.title
            binding.tvItemDate.text = DateHelper.formatTime(task.date)

            if (isListPage) {
                binding.cvItemNote.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.list_page_task_card)

            }

            binding.root.setOnLongClickListener {
                showConfirmationDialog(task)
                true
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                binding.root.context.startActivity(intent)
            }
        }

        private fun showConfirmationDialog(task: Task) {
            val context = binding.root.context
            AlertDialog.Builder(context)
                .setTitle("Mark as Completed")
                .setMessage("Are you sure you want to mark this task as completed?")
                .setPositiveButton("Yes") { _, _ ->
                    val updatedTask = task.copy(status = "Completed")
                    onTaskCompleted(updatedTask)
                    Toast.makeText(context, "Task marked as completed", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    fun updateList(newList: List<Task>) {
        submitList(newList) // Gunakan submitList() yang disediakan ListAdapter

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
