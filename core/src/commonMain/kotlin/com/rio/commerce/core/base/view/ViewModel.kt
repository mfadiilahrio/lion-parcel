package com.rio.commerce.core.base.view

import com.badoo.reaktive.observable.Observable
import com.rio.commerce.core.data.model.DataModel
import com.rio.commerce.core.data.model.Loading

interface ViewModelInput<R> {
    fun execute(request: R?)
}

interface ViewModelOutput<R, T, Error> {
    val loading: Observable<Loading>
    val exception: Observable<Throwable>
    val unauthorized: Observable<Error>
    val failed: Observable<Error>
    val result: Observable<DataModel<R?, T>>
}

interface ViewModel<R, T, Error> {
    val inputs: ViewModelInput<R>
    val outputs: ViewModelOutput<R, T, Error>
}
