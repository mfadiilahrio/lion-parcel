package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.Observable
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.Loading

interface ListViewModelInput<in R> {
    fun execute(request: R)
    fun loadMore(request: R)
}

interface ListViewModelOutput<R, T, Error> {
    val loading: Observable<Loading>
    val result: Observable<DataModel<R, List<T>>>
    val exception: Observable<Throwable>
    val unauthorized: Observable<String>
    val failed: Observable<Error>
}

interface ListViewModel<R, T, Error> {
    val inputs: ListViewModelInput<R>
    val outputs: ListViewModelOutput<R, T, Error>
}