package com.example.imagelisttest.ui.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PhotosLoadStateAdapter constructor(private val retry: () -> Unit): LoadStateAdapter<PhotosLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PhotosLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PhotosLoadStateViewHolder {
       return PhotosLoadStateViewHolder.create(parent,retry)
    }
}