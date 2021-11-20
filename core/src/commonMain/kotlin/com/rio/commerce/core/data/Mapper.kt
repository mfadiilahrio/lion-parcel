package com.rio.commerce.core.data

interface Mapper<in R, T, out E> {
    fun transform(request: R?, response: T): E
}