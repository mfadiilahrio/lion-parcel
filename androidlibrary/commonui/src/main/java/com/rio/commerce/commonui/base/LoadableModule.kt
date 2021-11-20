package com.rio.commerce.commonui.base

import com.rio.commerce.commonui.di.FeatureModule

interface LoadableModule {
    fun addModuleInjector(module: FeatureModule)
}