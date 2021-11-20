package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.single.map
import com.badoo.reaktive.subject.publish.PublishSubject
import com.rio.commerce.core.base.domain.UseCase
import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.ListableData
import com.rio.commerce.core.data.model.Loading

class ListViewModelImpl<R, T : ListableData<*>, E, Error>(
    useCase: UseCase<R, T, Error>,
    mapper: Mapper<R, T, List<E>>?,
    loadInitialCache: Boolean = false
) : ListViewModel<R, E, Error>, ListViewModelInput<R>, ListViewModelOutput<R, E, Error> {

    override val inputs: ListViewModelInput<R> = this
    override val outputs: ListViewModelOutput<R, E, Error> = this

    override val loading: Observable<Loading>
    override val result: Observable<DataModel<R, List<E>>>
    override val exception: Observable<Throwable>
    override val unauthorized: Observable<String>
    override val failed: Observable<Error>

    private val mListProperty = PublishSubject<R>()
    private val mLoadMoreProperty = PublishSubject<R>()

    init {
        val loadingProperty = PublishSubject<Loading>()
        val errorProperty = PublishSubject<Throwable>()
        val unauthorizedProperty = PublishSubject<String>()
        val failedProperty = PublishSubject<Error>()
        val requestCache: Observable<DataModel<R, T>>?

        val items = mutableListOf<E>()

        var clearItems = false

        loading = loadingProperty
        exception = errorProperty
        failed = failedProperty
        unauthorized = unauthorizedProperty

        if (loadInitialCache) {
            requestCache = mListProperty
                .flatMapSingle { request ->
                    useCase.execute(request, true).map { DataModel(request, it, true, null) }
                }
                .flatMap {
                    when (it.item) {
                        is Result.Success -> {
                            if (it.item.response.list.isNotEmpty()) {
                                loadingProperty.onNext(
                                    Loading(
                                        false,
                                        hideRefreshControlAfterFinished = false
                                    )
                                )
                            }

                            observableOf(
                                DataModel(
                                    it.request,
                                    it.item.response,
                                    it.isFromInitialCache,
                                    it.item.response.metadata
                                )
                            )
                        }
                        else -> observableOfEmpty()
                    }
                }
        } else {
            requestCache = null
        }

        val initialRequest: Observable<DataModel<R, T>> = mListProperty
            .doOnBeforeNext {
                loadingProperty.onNext(
                    Loading(
                        true,
                        hideRefreshControlDuringLoading = items.isEmpty()
                    )
                )
            }
            .flatMapSingle { request ->
                useCase.execute(request).map { DataModel(request, it, false, null) }
            }
            .doOnBeforeNext {
                loadingProperty.onNext(Loading(false))
            }
            .flatMap {
                when (it.item) {
                    is Result.Success -> {
                        observableOf(
                            DataModel(
                                it.request,
                                it.item.response,
                                it.isFromInitialCache,
                                it.item.response.metadata
                            )
                        )
                    }
                    is Result.Failure -> {
                        failedProperty.onNext(it.item.error)
                        observableOfEmpty()
                    }
                    is Result.Unauthorized -> {
                        unauthorizedProperty.onNext("")
                        observableOfEmpty()
                    }
                    is Result.Exception -> {
                        errorProperty.onNext(it.item.throwable)
                        observableOfEmpty()
                    }
                    else -> observableOfEmpty()
                }
            }.doOnBeforeNext {
                clearItems = true
            }

        val nextRequest: Observable<DataModel<R, T>> = mLoadMoreProperty
            .doOnBeforeNext {
                loadingProperty.onNext(
                    Loading(
                        true,
                        hideRefreshControlDuringLoading = true
                    )
                )
            }
            .flatMapSingle { request ->
                useCase.execute(request).map { DataModel(request, it, false, null) }
            }
            .doOnBeforeNext { loadingProperty.onNext(Loading(false)) }
            .flatMap {
                when (it.item) {
                    is Result.Success -> {
                        observableOf(
                            DataModel(
                                it.request,
                                it.item.response,
                                it.isFromInitialCache,
                                it.item.response.metadata
                            )
                        )
                    }
                    else -> observableOfEmpty()
                }
            }.doOnBeforeNext {
                clearItems = false
            }

        fun getItems(data: DataModel<R, T>): DataModel<R, List<E>> {
            if (clearItems) {
                items.clear()
            }

            @Suppress("UNCHECKED_CAST")
            val list = mapper?.transform(null, data.item) ?: data.item.list as List<E>

            items.addAll(list)

            return DataModel(data.request, items as List<E>, data.isFromInitialCache, data.metadata)
        }

        result = if (requestCache != null) {
            merge(requestCache, initialRequest, nextRequest).map {
                getItems(it)
            }
        } else {
            merge(initialRequest, nextRequest).map {
                getItems(it)
            }
        }
    }

    override fun execute(request: R) {
        mListProperty.onNext(request)
    }

    override fun loadMore(request: R) {
        mLoadMoreProperty.onNext(request)
    }
}