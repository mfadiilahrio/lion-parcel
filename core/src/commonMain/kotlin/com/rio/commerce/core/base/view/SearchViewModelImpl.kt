package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.createMainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.subject.publish.PublishSubject
import com.rio.commerce.core.base.domain.UseCase
import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.model.*

class SearchViewModelImpl<R, T : ListableData<*>, E, Error>(
    private val searchAfter: Int,
    useCase: UseCase<R, T, Error>,
    mapper: Mapper<R, T, List<E>>?,
    debounce: Long = 800,
    allowEmptyKeyword: Boolean = false
) : SearchViewModel<R, E>, SearchViewModelInput<R>,
    SearchViewModelOutput<R, E> where R : ListableRequest, R : Searchable {

    override val inputs: SearchViewModelInput<R> = this
    override val outputs: SearchViewModelOutput<R, E> = this

    override val loading: Observable<Loading>
    override val result: Observable<DataModel<R, List<E>>>
    override val exception: Observable<Throwable>
    override val unauthorized: Observable<String>

    private val mRequest = PublishSubject<R>()
    private val mListProperty = PublishSubject<R>()

    init {
        val loadingProperty = PublishSubject<Loading>()
        val errorProperty = PublishSubject<Throwable>()
        val unauthorizedProperty = PublishSubject<String>()

        loading = loadingProperty
        exception = errorProperty
        unauthorized = unauthorizedProperty

        val savedResult = mListProperty
            .flatMapSingle { request ->
                useCase.execute(request, true).map { DataModel(request, it, true, null) }
            }
            .flatMap {
                when (it.item) {
                    is Result.Success -> {
                        @Suppress("UNCHECKED_CAST")
                        val list =
                            mapper?.transform(null, it.item.response)
                                ?: it.item.response.list as List<E>

                        observableOf(
                            DataModel(
                                it.request,
                                list,
                                it.isFromInitialCache,
                                it.metadata
                            )
                        )
                    }
                    else -> observableOfEmpty()
                }
            }

        val request = mRequest
            .debounce(debounce, createMainScheduler())
            .filter { (it.keyword?.length ?: 0) >= searchAfter || allowEmptyKeyword }
            .doOnBeforeNext { loadingProperty.onNext(Loading(true)) }
            .flatMapSingle { request ->
                useCase.execute(request).map { DataModel(request, it, false, null) }
            }
            .doOnBeforeNext { loadingProperty.onNext(Loading(false)) }
            .flatMap {
                when (it.item) {
                    is Result.Success -> {
                        @Suppress("UNCHECKED_CAST")
                        val list =
                            mapper?.transform(null, it.item.response)
                                ?: it.item.response.list as List<E>

                        observableOf(
                            DataModel(
                                it.request,
                                list,
                                it.isFromInitialCache,
                                it.metadata
                            )
                        )
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
            }.share()

        result = merge(savedResult, request)
    }

    override fun search(request: R) {
        mRequest.onNext(request)
    }

    override fun loadSavedResults(request: R) {
        //mListProperty.onNext(request)
    }
}