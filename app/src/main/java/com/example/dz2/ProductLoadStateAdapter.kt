package com.example.dz2

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ProductLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<ProductLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ProductLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ProductLoadStateViewHolder {
        return ProductLoadStateViewHolder.create(parent, retry)
    }
}