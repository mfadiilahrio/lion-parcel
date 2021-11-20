package com.rio.commerce.core.data.model

@Parcelize
data class ListRequest(
    override var id: Int? = null,
    override var timestamp: Long = 0,
    override var page: Int = 1,
    override var limit: Int = 30,
    override var ignorePaging: Boolean = false,
    override var forceReload: Boolean = false,
    override var keyword: String? = null
) : ListableRequest, ForceReloadableRequest, Searchable, Parcelable