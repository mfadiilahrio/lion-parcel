package com.rio.commerce.commonui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderFactory {
    fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
}