package com.rio.commerce.feature.gallery.di.component

import com.rio.commerce.android.app.di.component.AppComponent
import com.rio.commerce.common.qualifier.FeatureScope
import com.rio.commerce.feature.gallery.di.FeatureInjector
import com.rio.commerce.feature.gallery.di.module.ActivityBindingModule
import com.rio.commerce.feature.gallery.di.module.FragmentBindingModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@FeatureScope
@Component(
    dependencies = [AppComponent::class],
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class
    ]
)
interface GalleryComponent {
    fun inject(injector: FeatureInjector)
}