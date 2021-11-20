package com.rio.commerce.feature.transaction.di.module

import com.rio.commerce.android.app.Contract
import com.rio.commerce.common.qualifier.ActivityScope
import com.rio.commerce.common.qualifier.Host
import com.rio.commerce.common.qualifier.HttpHeaders
import com.rio.commerce.core.base.domain.UseCaseImpl
import com.rio.commerce.core.base.view.ViewModel
import com.rio.commerce.core.base.view.ViewModelImpl
import com.rio.commerce.core.data.DataErrorMapper
import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.repository.RepositoryImpl
import com.rio.commerce.core.fact.data.Fact
import com.rio.commerce.core.fact.data.FactMapper
import com.rio.commerce.core.fact.data.GetFactCloudService
import dagger.Module
import dagger.Provides

@Module
class FactModule {

    @Provides
    @ActivityScope
    fun provideViewModel(
        @HttpHeaders httpHeaders: Map<String, String>,
        @Host host: String,
    ): ViewModel<Nothing, Fact, DataError> {
        val mapper = FactMapper()
        val errorMapper = DataErrorMapper()

        val service = GetFactCloudService(
            httpHeaders,
            host,
            Contract.APP.DETAIL,
            mapper,
            errorMapper
        )

        val repository = RepositoryImpl(service)
        val useCase = UseCaseImpl(repository)

        return ViewModelImpl(useCase, null, false)
    }

}