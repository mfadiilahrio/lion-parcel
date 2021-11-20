package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.Observable
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.ListableRequest
import com.rio.commerce.core.data.model.Loading

interface SearchViewModelInput<in R : ListableRequest> {
    fun loadSavedResults(request: R)
    fun search(request: R)
}

interface SearchViewModelOutput<R, T> {
    val loading: Observable<Loading>
    val result: Observable<DataModel<R, List<T>>>
    val exception: Observable<Throwable>
    val unauthorized: Observable<String>
}

interface SearchViewModel<R : ListableRequest, T> {
    val inputs: SearchViewModelInput<R>
    val outputs: SearchViewModelOutput<R, T>
}