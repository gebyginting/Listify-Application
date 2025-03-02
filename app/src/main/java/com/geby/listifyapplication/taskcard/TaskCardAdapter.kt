package com.geby.listifyapplication.taskcard

import android.annotation.SuppressLint
import android.content.Context
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
    private val context: Context,
    private val isListPage: Boolean = false,
    private val onItemClick: (String) -> Unit) : ListAdapter<Task, TaskCardAdapter.MyViewHolder>(DIFF_CALLBACK) {

        private val listTasks = ArrayList<Task>()

    fun setListTasks(listTasks: List<Task>) {
        submitList(listTasks)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick, isListPage)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }

    class MyViewHolder(
        private val binding: TaskCardBinding,
        val onItemClick: (String) -> Unit,
        private val isListPage: Boolean 
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(card: Task) {
            binding.tvItemTitle.text = card.title
            binding.tvItemDate.text = DateHelper.formatTime(card.date)

            if (isListPage) {
                binding.cvItemNote.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.list_page_task_card)

            } else {
                return
            }
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailTaskActivity::class.java).apply {
                    putExtra("TASK_ID", card.id)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.title == newItem.title
            }
            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
