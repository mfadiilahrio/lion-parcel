package com.rio.commerce.android.app.di.component

import android.content.Context
import com.rio.commerce.android.app.Application
import com.rio.commerce.android.app.di.module.*
import com.rio.commerce.common.qualifier.Host
import com.rio.commerce.common.qualifier.HttpHeaders
import com.rio.commerce.core.UserPreference
import com.rio.commerce.core.data.cache.sql.Database
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        CacheModule::class,
        NetworkModule::class,
        PreferenceModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class
    ]
)
interface AppComponent : AndroidInjector<Application> {

    fun context(): Context
    fun database(): Database
    fun libPhoneNumber(): PhoneNumberUtil
    fun preference(): UserPreference
    fun locale(): Locale

    @Host
    fun host(): String

    @HttpHeaders
    fun httpHeaders(): Map<String, String>

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun network(module: NetworkModule): Builder

        fun preference(module: PreferenceModule): Builder

        fun cache(module: CacheModule): Builder

        fun build(): AppComponent
    }

    override fun inject(app: Application)

}