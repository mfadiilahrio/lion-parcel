package com.rio.commerce.feature.transaction.di.module

import com.rio.commerce.common.qualifier.ActivityScope
import com.rio.commerce.feature.transaction.view.FactActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [FactModule::class])
    @ActivityScope
    abstract fun bindFactActivity(): FactActivity

}