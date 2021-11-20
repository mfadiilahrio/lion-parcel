package com.rio.commerce.core.fact.data

import com.rio.commerce.core.data.Mapper

class FactMapper : Mapper<Any, FactResponse.Fact, Fact> {

    override fun transform(request: Any?, response: FactResponse.Fact): Fact {
        return response.run {
            Fact(
                fact,
                length,
                null
            )
        }

    }
}