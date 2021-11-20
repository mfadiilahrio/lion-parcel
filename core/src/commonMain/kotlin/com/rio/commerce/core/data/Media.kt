package com.rio.commerce.core.data

import com.rio.commerce.core.data.model.IgnoredOnParcel
import com.rio.commerce.core.data.model.Parcelable
import com.rio.commerce.core.data.model.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Parcelize
@Serializable
data class Media(val url: String) : Parcelable {

    enum class Type(val value: String) {
        IMAGE("images"),
        VIDEO("videos")
    }

    @IgnoredOnParcel
    @Transient
    val small: String = getMediaUrl("s260")

    @IgnoredOnParcel
    @Transient
    val medium: String = getMediaUrl("s420")

    @IgnoredOnParcel
    @Transient
    val large: String = getMediaUrl("s740")

    @IgnoredOnParcel
    @Transient
    val original: String = getMediaUrl("s0")

    private fun getMediaUrl(suffix: String): String {
        val extension = url.split(".").lastOrNull()

        return if (extension == "jpg" || extension == "png" || extension == "gif" || extension == "jpeg") {
            url
        } else {
            "${url}=$suffix"
        }
    }
}