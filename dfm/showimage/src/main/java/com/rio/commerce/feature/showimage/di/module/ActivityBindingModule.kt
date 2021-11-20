package com.rio.commerce.feature.showimage.di.module

import com.rio.commerce.common.qualifier.ActivityScope
import com.rio.commerce.feature.showimage.view.ShowImageActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector()
    @ActivityScope
    abstract fun bindShowImageActivity(): ShowImageActivity
}