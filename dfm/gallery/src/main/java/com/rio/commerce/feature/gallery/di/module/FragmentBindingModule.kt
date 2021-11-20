package com.rio.commerce.feature.gallery.di.module

import com.rio.commerce.common.qualifier.FragmentScope
import com.rio.commerce.feature.gallery.view.GalleryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBindingModule {

    @ContributesAndroidInjector(modules = [GalleryModule::class])
    @FragmentScope
    abstract fun bindGalleryFragment(): GalleryFragment
}