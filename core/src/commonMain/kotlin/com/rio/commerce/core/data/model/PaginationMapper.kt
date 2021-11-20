package com.rio.commerce.core.data.model

import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.model.response.PaginationResponse

class PaginationMapper : Mapper<Any, PaginationResponse, Pagination> {

    override fun transform(request: Any?, response: PaginationResponse): Pagination {
        return Pagination(
            response.currentPage,
            response.itemsPerPage,
            response.totalPages,
            response.itemCount,
            response.totalItems
        )
    }
}