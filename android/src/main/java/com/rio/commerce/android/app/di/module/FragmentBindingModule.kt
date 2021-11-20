package com.rio.commerce.android.app.di.module

import com.rio.commerce.android.app.fact.FactsFragment
import com.rio.commerce.common.qualifier.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBindingModule {

    @ContributesAndroidInjector(modules = [FactsModule::class])
    @FragmentScope
    abstract fun bindProductsFragment(): FactsFragment

}
