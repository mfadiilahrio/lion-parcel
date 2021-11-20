package com.rio.commerce.core.data

sealed class Result<T, E> {
    class Empty<T, E> : Result<T, E>()

    data class Unauthorized<T, E>(val error: E) : Result<T, E>()
    data class Success<T, E>(val response: T) : Result<T, E>()
    data class Failure<T, E>(val error: E) : Result<T, E>()
    data class Exception<T, E>(val throwable: Throwable) : Result<T, E>()

    companion object {
        fun <T, E> success(response: T): Result<T, E> = Success(response)
        fun <T, E> exception(throwable: Throwable): Result<T, E> = Exception(throwable)
        fun <T, E> fail(error: E): Result<T, E> = Failure(error)
        fun <T, E> empty(): Result<T, E> = Empty()
        fun <T, E> unauthorized(error: E): Result<T, E> = Unauthorized(error)
    }
}