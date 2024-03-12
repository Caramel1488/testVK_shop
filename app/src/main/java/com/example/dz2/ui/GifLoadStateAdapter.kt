package com.example.dz2.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class GifLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<GifLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: GifLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)

    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): GifLoadStateViewHolder {
        return GifLoadStateViewHolder.create(parent, retry)
    }
}