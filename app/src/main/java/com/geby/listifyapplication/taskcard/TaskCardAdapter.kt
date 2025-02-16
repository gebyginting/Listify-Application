package com.geby.listifyapplication.taskcard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geby.listifyapplication.databinding.TaskCardBinding

class TaskCardAdapter(private val onItemClick: (String) -> Unit) : ListAdapter<TaskCardData, TaskCardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }

    class MyViewHolder(private val binding: TaskCardBinding, val onItemClick: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(card: TaskCardData) {
            binding.tvItemTitle.text = card.title
            binding.tvItemDate.text = card.date

            binding.root.setOnClickListener {
                onItemClick(card.title)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskCardData>() {
            override fun areItemsTheSame(oldItem: TaskCardData, newItem: TaskCardData): Boolean {
                return oldItem.title == newItem.title
            }
            override fun areContentsTheSame(oldItem: TaskCardData, newItem: TaskCardData): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
