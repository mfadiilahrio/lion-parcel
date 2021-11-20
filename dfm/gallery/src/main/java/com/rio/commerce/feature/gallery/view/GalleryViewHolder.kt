package com.rio.commerce.feature.gallery.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.auto.factory.AutoFactory
import com.rio.commerce.commonui.view.ViewHolder
import com.rio.commerce.commonui.view.ViewHolderFactory
import com.rio.commerce.core.data.MediaInfo
import com.rio.commerce.feature.gallery.R

@AutoFactory(implementing = [ViewHolderFactory::class])
class GalleryViewHolder(parent: ViewGroup) : ViewHolder<MediaInfo, Nothing>(
    LayoutInflater.from(parent.context).inflate(R.layout.view_gallery, parent, false)
) {

    override var listener: Nothing? = null

    private val mGallery: ImageView = itemView.findViewById(R.id.gallery)
    private val mChecked: ImageView = itemView.findViewById(R.id.checked)

    override fun onBindViewHolder(item: MediaInfo) {
        Glide.with(itemView.context)
            .load(item.media.small)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(mGallery)

        mChecked.visibility = View.GONE
    }
}