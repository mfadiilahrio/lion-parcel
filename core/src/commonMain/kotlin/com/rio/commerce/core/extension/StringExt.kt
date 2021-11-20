package com.rio.commerce.core.extension

import com.rio.commerce.core.util.Date
import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse

fun String.apiDateToMillis(): Double {
    val dateFormat = DateFormat(Date.API_FORMAT)

    return try {
        dateFormat.parse(this).utc.unixMillis
    } catch (e: Exception) {
        try {
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(this).utc.unixMillis
        } catch (e: Exception) {
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ").parse(this).utc.unixMillis
        }
    }
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
}

fun String.getStyledFont(
    alignment: String? = "left",
    color: String,
    fontSize: String = "medium"
): String {
    val addBodyStart = toLowerCase().contains("<body>")
    val addBodyEnd = toLowerCase().contains("</body>")
    return "<style type=\"text/css\">" +
            "@font-face {" +
            "   font-family: CustomFont;src: url(\"file:///android_res/font/regular.otf\")" +
            "} " +
            "body {" +
            "   font-family: CustomFont;font-size: $fontSize; margin: 0; padding: 0;text-align: $alignment;color: $color!important" +
            "}" +
            "p, p span {" +
            "   color: $color!important" +
            "}" +
            "</style>" +
            (if (addBodyStart) "<body>" else "") + this + if (addBodyEnd) "</body>" else ""
}

fun String.getNewsStyle(styles: String?, color: String): String {
    val addBodyStart = toLowerCase().contains("<body>")
    val addBodyEnd = toLowerCase().contains("</body>")
    return "<style type=\"text/css\">" +
            "@font-face {" +
            "   font-family: CustomFont;src: url(\"file:///android_res/font/medium.otf\")" +
            "} " +
            "body {" +
            "   font-family: CustomFont;color: $color!important" +
            "} " +
            "$styles" +
            "</style>" +
            (if (addBodyStart) "<body>" else "") + this + if (addBodyEnd) "</body>" else ""
}