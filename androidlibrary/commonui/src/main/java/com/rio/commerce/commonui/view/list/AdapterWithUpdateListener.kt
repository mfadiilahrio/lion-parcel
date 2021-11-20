package com.rio.commerce.commonui.view.list

interface AdapterWithUpdateListener<T> {
    fun didTap(item: T)
    fun editDidTap(item: T)
    fun deleteDidTap(item: T)
    fun defaultDidTap(item: T)
}