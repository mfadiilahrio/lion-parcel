package com.rio.commerce.commonui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rio.commerce.commonui.view.list.AdapterWithUpdateListener

class ListWithUpdateAdapter<E>(private val mViewHolderFactories: Map<Int, ViewHolderFactory>) :
    RecyclerViewAdapter<AdapterWithUpdateListener<E>, E, RecyclerView.ViewHolder>(),
    AdapterWithUpdateListener<E> {

    override var listener: AdapterWithUpdateListener<E>? = null

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < list.size) {
            val item = holder as ViewHolder<E, AdapterWithUpdateListener<E>>

            item.listener = this
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

    override fun didTap(item: E) {
        listener?.didTap(item)
    }

    override fun deleteDidTap(item: E) {
        listener?.deleteDidTap(item)
    }

    override fun editDidTap(item: E) {
        listener?.editDidTap(item)
    }

    override fun defaultDidTap(item: E) {
        listener?.defaultDidTap(item)
    }
}
