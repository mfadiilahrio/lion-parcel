package com.rio.commerce.android.app

import com.facebook.stetho.Stetho
import com.rio.commerce.android.app.di.ModuleInjector
import com.rio.commerce.android.app.di.component.DaggerAppComponent
import com.rio.commerce.android.app.di.module.CacheModule
import com.rio.commerce.android.app.di.module.NetworkModule
import com.rio.commerce.android.app.di.module.PreferenceModule
import com.rio.commerce.common.qualifier.Host
import com.rio.commerce.common.qualifier.HttpHeaders
import com.rio.commerce.commonui.base.LoadableModule
import com.rio.commerce.commonui.base.MultiDexSplitCompatApplication
import com.rio.commerce.commonui.di.FeatureModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.util.*
import javax.inject.Inject


class Application : MultiDexSplitCompatApplication(), LoadableModule, HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var locale: Locale

    @Inject
    @HttpHeaders
    lateinit var httpHeaders: Map<String, String>

    @Inject
    @Host
    lateinit var host: String

    private val mModuleAndroidInjectors = mutableListOf<DispatchingAndroidInjector<Any>>()
    private val mInjectedModules = mutableSetOf<FeatureModule>()

    private val mAndroidInjector = AndroidInjector<Any> { instance ->
        if (dispatchingAndroidInjector.maybeInject(instance)) {
            return@AndroidInjector
        }

        mModuleAndroidInjectors.forEach { injector ->
            if (injector.maybeInject(instance)) {
                return@AndroidInjector
            }
        }

        throw IllegalStateException("Injector not found for $instance")
    }

    val appComponent by lazy {

        DaggerAppComponent.builder()
            .application(this)
            .network(
                NetworkModule(
                    getString(R.string.api_host),
                    this
                )
            )
            .preference(PreferenceModule())
            .cache(CacheModule("rio-commerce.db"))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        appComponent.inject(this)

        // Uncomment for disable auto night mode
//        AppCompatDelegate.setDefaultNightMode(
//            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//                Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
//                Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
//                Configuration.UI_MODE_NIGHT_UNDEFINED -> AppCompatDelegate.MODE_NIGHT_NO
//                else -> AppCompatDelegate.MODE_NIGHT_NO
//            }
//        )

        Napier.base(DebugAntilog())
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mAndroidInjector
    }

    override fun addModuleInjector(module: FeatureModule) {
        if (mInjectedModules.contains(module)) {
            return
        }

        val moduleInjector = Class.forName(module.injectorName).run {
            newInstance() as ModuleInjector
        }

        moduleInjector.inject(this)

        mModuleAndroidInjectors.add(moduleInjector.androidInjector())

        mInjectedModules.add(module)
    }

}