package com.rio.commerce.core.data.model

interface ListableRequest {
    var id: Int?
    var timestamp: Long
    var page: Int
    var limit: Int
    var ignorePaging: Boolean
}