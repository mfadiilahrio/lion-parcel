package com.rio.commerce.feature.gallery.di.module

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rio.commerce.common.qualifier.FragmentScope
import com.rio.commerce.commonui.view.AdapterListener
import com.rio.commerce.commonui.view.ListAdapter
import com.rio.commerce.commonui.view.RecyclerViewAdapter
import com.rio.commerce.commonui.view.ViewHolderFactory
import com.rio.commerce.core.data.MediaInfo
import com.rio.commerce.feature.gallery.view.GalleryViewHolderFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

@Module
class GalleryModule {

    @Provides
    @FragmentScope
    fun provideLayoutManager(context: Context): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 3)
    }

    @Provides
    @FragmentScope
    fun provideAdapter(viewHolderFactories: @JvmSuppressWildcards Map<Int, ViewHolderFactory>): RecyclerViewAdapter<AdapterListener<MediaInfo>, MediaInfo, *> {
        return ListAdapter(viewHolderFactories)
    }

    @Provides
    @FragmentScope
    @IntoMap
    @IntKey(0)
    fun provideViewHolder(): ViewHolderFactory {
        return GalleryViewHolderFactory()
    }

}