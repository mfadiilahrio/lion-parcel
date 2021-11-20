package com.rio.commerce.commonui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.auto.factory.AutoFactory
import com.rio.commerce.commonui.R

@AutoFactory(implementing = [ViewHolderFactory::class])
class LoadingViewHolder(parent: ViewGroup) : ViewHolder<Nothing, Nothing>(
    LayoutInflater.from(parent.context).inflate(R.layout.view_loading_item, parent, false)
) {

    override var listener: Nothing? = null

    override fun onBindViewHolder(item: Nothing) {}
}
