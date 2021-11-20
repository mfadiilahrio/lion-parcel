package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.single.map
import com.badoo.reaktive.subject.publish.PublishSubject
import com.rio.commerce.core.base.domain.UseCase
import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.Loading

class ViewModelImpl<R, T, E, Error>(
    useCase: UseCase<R, T, Error>,
    mapper: Mapper<R, T, E>?,
    loadInitialCache: Boolean = false
) : ViewModel<R, E, Error>, ViewModelInput<R>, ViewModelOutput<R, E, Error> {
    override val inputs: ViewModelInput<R> = this
    override val outputs: ViewModelOutput<R, E, Error> = this

    override val loading: Observable<Loading>
    override val failed: Observable<Error>
    override val unauthorized: Observable<Error>
    override val exception: Observable<Throwable>
    override val result: Observable<DataModel<R?, E>>

    private val mStartProperty = PublishSubject<R?>()

    init {

        val loadingProperty = PublishSubject<Loading>()
        val exceptionProperty = PublishSubject<Throwable>()
        val failedProperty = PublishSubject<Error>()
        val unauthorizedProperty = PublishSubject<Error>()
        val requestCache: Observable<DataModel<R?, E>>?

        loading = loadingProperty
        exception = exceptionProperty
        failed = failedProperty
        unauthorized = unauthorizedProperty

        if (loadInitialCache) {
            requestCache = mStartProperty
                .flatMapSingle { request ->
                    useCase.execute(request, true).map { DataModel(request, it, true, null) }
                }
                .flatMap {
                    when (it.item) {
                        is Result.Success -> {
                            @Suppress("UNCHECKED_CAST")
                            val item = mapper?.transform(null, it.item.response)
                                ?: it.item.response as E

                            observableOf(DataModel(it.request, item, true, null))
                        }
                        else -> observableOfEmpty()
                    }
                }
        } else {
            requestCache = null
        }

        val data = mStartProperty
            .doOnBeforeNext { loadingProperty.onNext(Loading(true)) }
            .flatMapSingle { request ->
                useCase.execute(request).map { DataModel(request, it, false, null) }
            }
            .doOnBeforeNext { loadingProperty.onNext(Loading(false)) }
            .flatMap {
                when (it.item) {
                    is Result.Success -> {
                        @Suppress("UNCHECKED_CAST")
                        val item = mapper?.transform(null, it.item.response)
                            ?: it.item.response as E

                        observableOf(
                            DataModel(
                                it.request,
                                item,
                                it.isFromInitialCache,
                                it.metadata
                            )
                        )
                    }
                    is Result.Exception -> {
                        exceptionProperty.onNext(it.item.throwable)
                        observableOfEmpty()
                    }
                    is Result.Unauthorized -> {
                        unauthorizedProperty.onNext(it.item.error)
                        observableOfEmpty()
                    }
                    is Result.Failure -> {
                        failedProperty.onNext(it.item.error)
                        observableOfEmpty()
                    }
                    else -> observableOfEmpty()
                }
            }

        result = if (requestCache != null) {
            merge(requestCache, data)
        } else {
            data
        }
    }

    override fun execute(request: R?) {
        mStartProperty.onNext(request)
    }
}