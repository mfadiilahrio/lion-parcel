package com.rio.commerce.feature.gallery.di.module

import com.rio.commerce.common.qualifier.ActivityScope
import com.rio.commerce.feature.gallery.view.GalleryActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector()
    @ActivityScope
    abstract fun bindGalleryActivity(): GalleryActivity
}