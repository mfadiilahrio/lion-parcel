package com.rio.commerce.commonui.view

import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<in E, L>(itemView: android.view.View) :
    RecyclerView.ViewHolder(itemView) {

    abstract var listener: L?

    abstract fun onBindViewHolder(item: E)

    object Type {
        const val LIST_LOADING = 999
    }
}
