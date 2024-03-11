package com.example.dz2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dz2.databinding.ItemListGifLayoutBinding
import com.example.dz2.networking.Product

class ProductListAdapter : PagingDataAdapter<Product, ProductListAdapter.GifHolder>(COMPARATOR) {


    override fun onBindViewHolder(holder: GifHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifHolder {
        val binding =
            ItemListGifLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifHolder(binding)
    }

    class GifHolder(
        private val binding: ItemListGifLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            val gifPath = product.images[0]
            Glide.with(itemView)
                .load(gifPath)
                .into(binding.imageView)
        }

    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return oldProduct.id == newProduct.id
            }

            override fun areContentsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return oldProduct == newProduct
            }
        }
    }


}