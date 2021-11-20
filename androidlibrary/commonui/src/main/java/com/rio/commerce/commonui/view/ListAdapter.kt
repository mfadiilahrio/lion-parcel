package com.rio.commerce.commonui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter<E>(private val mViewHolderFactories: Map<Int, ViewHolderFactory>) :
    RecyclerViewAdapter<AdapterListener<E>, E, RecyclerView.ViewHolder>() {

    override var listener: AdapterListener<E>? = null

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < list.size) {
            val item = holder as ViewHolder<E, AdapterListener<E>>

            item.onBindViewHolder(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder = mViewHolderFactories[viewType]?.createViewHolder(parent)

        viewHolder?.let {

            if (viewType != ViewHolder.Type.LIST_LOADING) {
                it.itemView.setOnClickListener { _ -> itemDidTap(it.adapterPosition) }
            }

            return it
        }

        throw IllegalArgumentException("View holder not found")
    }


    private fun itemDidTap(position: Int) {
        if (position > -1 && position < list.size) {
            val item = list[position]

            listener?.didTap(item)
        }
    }
}
