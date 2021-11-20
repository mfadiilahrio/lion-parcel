package com.rio.commerce.android.app.di.module

import com.rio.commerce.android.app.MainActivity
import com.rio.commerce.common.qualifier.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector()
    @ActivityScope
    abstract fun bindMainActivity(): MainActivity

}
