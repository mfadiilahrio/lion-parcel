package com.rio.commerce.feature.showimage.di.component

import com.rio.commerce.android.app.di.component.AppComponent
import com.rio.commerce.common.qualifier.FeatureScope
import com.rio.commerce.feature.showimage.di.FeatureInjector
import com.rio.commerce.feature.showimage.di.module.ActivityBindingModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@FeatureScope
@Component(
    dependencies = [AppComponent::class],
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class
    ]
)
interface ShowImageComponent {
    fun inject(injector: FeatureInjector)
}