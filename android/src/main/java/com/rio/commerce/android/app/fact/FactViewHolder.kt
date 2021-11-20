package com.rio.commerce.android.app.fact

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.auto.factory.AutoFactory
import com.rio.commerce.android.app.R
import com.rio.commerce.commonui.view.ViewHolder
import com.rio.commerce.commonui.view.ViewHolderFactory
import com.rio.commerce.core.fact.data.Fact

@AutoFactory(implementing = [ViewHolderFactory::class])
class FactViewHolder(parent: ViewGroup) :
    ViewHolder<Fact, Nothing>(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_fact,
            parent,
            false
        )
    ) {

    override var listener: Nothing? = null

    private val mTvFact: TextView = itemView.findViewById(R.id.fact)
    private val mTvLength: TextView = itemView.findViewById(R.id.length)

    override fun onBindViewHolder(item: Fact) {

        mTvFact.text = item.fact
        mTvLength.text = item.length.toString()
    }
}