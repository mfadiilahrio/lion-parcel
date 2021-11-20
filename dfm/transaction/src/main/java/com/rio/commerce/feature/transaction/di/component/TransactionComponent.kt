package com.rio.commerce.feature.transaction.di.component

import com.rio.commerce.android.app.di.component.AppComponent
import com.rio.commerce.common.qualifier.FeatureScope
import com.rio.commerce.feature.transaction.di.FeatureInjector
import com.rio.commerce.feature.transaction.di.module.ActivityBindingModule
import com.rio.commerce.feature.transaction.di.module.FragmentBindingModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@FeatureScope
@Component(
    dependencies = [AppComponent::class],
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class]
)
interface TransactionComponent {
    fun inject(injector: FeatureInjector)
}