package com.rio.commerce.core.data.model

data class DataList<E>(
    override val list: List<E>,
    override val metadata: Metadata?
) : ListableData<E>