package com.rio.commerce.core.data.model

interface ListableData<E> {
    val list: List<E>
    val metadata: Metadata?
}