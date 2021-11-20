package com.rio.commerce.core.data

import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.model.DataErrorResponse

class DataErrorMapper : Mapper<Any, DataErrorResponse, DataError> {

    override fun transform(request: Any?, response: DataErrorResponse): DataError {
        return response.run {
            DataError(
                status,
                message,
            )
        }
    }
}