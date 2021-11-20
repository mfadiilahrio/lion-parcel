package com.rio.commerce.android.app.di.module

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rio.commerce.android.app.Contract
import com.rio.commerce.android.app.fact.FactViewHolderFactory
import com.rio.commerce.common.qualifier.FragmentScope
import com.rio.commerce.common.qualifier.Host
import com.rio.commerce.common.qualifier.HttpHeaders
import com.rio.commerce.commonui.view.AdapterListener
import com.rio.commerce.commonui.view.ListAdapter
import com.rio.commerce.commonui.view.RecyclerViewAdapter
import com.rio.commerce.commonui.view.ViewHolderFactory
import com.rio.commerce.core.base.domain.UseCaseImpl
import com.rio.commerce.core.base.view.ListViewModel
import com.rio.commerce.core.base.view.ListViewModelImpl
import com.rio.commerce.core.data.DataErrorMapper
import com.rio.commerce.core.data.cache.CacheImpl
import com.rio.commerce.core.data.cache.Expire
import com.rio.commerce.core.data.cache.ListCacheService
import com.rio.commerce.core.data.cache.sql.Database
import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.model.DataList
import com.rio.commerce.core.data.model.ListRequest
import com.rio.commerce.core.data.repository.CacheableListRepositoryImpl
import com.rio.commerce.core.fact.data.Fact
import com.rio.commerce.core.fact.data.FactsMapper
import com.rio.commerce.core.fact.data.GetFactsCloudService
import com.rio.commerce.core.fact.data.cache.FactSqlDataSource
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

@Module
class FactsModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        db: Database,
        @HttpHeaders httpHeaders: Map<String, String>,
        @Host host: String,
    ): ListViewModel<ListRequest, Fact, DataError> {

        val mapper = FactsMapper()
        val errorMapper = DataErrorMapper()

        val dataSource =
            FactSqlDataSource(
                db
            )
        val cache = CacheImpl(dataSource, Expire.ONE_DAY)
        val cacheService =
            ListCacheService<ListRequest, DataList<Fact>, Fact, DataError>(
                cache
            )
        val service = GetFactsCloudService(
            httpHeaders,
            host,
            Contract.APP.LIST,
            mapper,
            errorMapper
        )

        val repository = CacheableListRepositoryImpl(service, cacheService, cache, false)

        val useCase = UseCaseImpl(repository)

        return ListViewModelImpl(useCase, null, true)
    }

    @Provides
    @FragmentScope
    fun provideLayoutManager(context: Context): RecyclerView.LayoutManager {
        return LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    @Provides
    @FragmentScope
    fun provideAdapter(viewHolderFactories: @JvmSuppressWildcards Map<Int, ViewHolderFactory>): RecyclerViewAdapter<AdapterListener<Fact>, Fact, *> {
        return ListAdapter(viewHolderFactories)
    }

    @Provides
    @FragmentScope
    @IntoMap
    @IntKey(0)
    fun provideViewHolder(): ViewHolderFactory {
        return FactViewHolderFactory()
    }

}