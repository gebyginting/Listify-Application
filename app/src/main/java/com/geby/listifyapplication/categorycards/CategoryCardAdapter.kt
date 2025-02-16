package com.geby.listifyapplication.categorycards

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geby.listifyapplication.databinding.ListCategoryBinding

class CategoryCardAdapter(private val onItemClick: (String) -> Unit) : ListAdapter<CategoryCardData, CategoryCardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }

    class MyViewHolder(private val binding: ListCategoryBinding, val onItemClick: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(card: CategoryCardData) {
            binding.ivCategory.setImageResource(card.ivCategory)
            binding.tvCategoryStatus.text = card.title
            binding.tvCategoryCount.text = "${card.count} tasks"

            binding.root.setOnClickListener {
                onItemClick(card.title)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryCardData>() {
            override fun areItemsTheSame(oldItem: CategoryCardData, newItem: CategoryCardData): Boolean {
                return oldItem.title == newItem.title
            }
            override fun areContentsTheSame(oldItem: CategoryCardData, newItem: CategoryCardData): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
