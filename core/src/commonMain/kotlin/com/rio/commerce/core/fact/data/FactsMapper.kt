package com.rio.commerce.core.fact.data

import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.model.DataList
import com.rio.commerce.core.data.model.Pagination

class FactsMapper :
    Mapper<Any, FactsResponse, DataList<Fact>> {

    override fun transform(request: Any?, response: FactsResponse): DataList<Fact> {
        val pagination = Pagination(
            response.current_page,
            response.per_page,
            response.last_page,
            response.data.size,
            response.total
        )

        val fact = response.data.run {
            map {
                Fact(
                    it.fact,
                    it.length,
                    pagination
                )
            }
        }

        return DataList(fact, null)
    }
}