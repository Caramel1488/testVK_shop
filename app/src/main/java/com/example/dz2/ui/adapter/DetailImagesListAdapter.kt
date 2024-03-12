package com.example.dz2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dz2.R
import com.example.dz2.databinding.ItemDetailImagesListLayoutBinding

class DetailImagesListAdapter(): ListAdapter<String, DetailImagesListAdapter.Holder>(
    ImagesDiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding = ItemDetailImagesListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImagesDiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
    class Holder(
        private val binding: ItemDetailImagesListLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String){
            Glide.with(itemView)
                .load(image)
                .error(itemView.context.getDrawable(R.drawable.baseline_error_24))
                .into(binding.detailImageView)
        }
    }
}