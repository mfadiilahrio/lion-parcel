package com.rio.commerce.core.util

object PhoneUtil {
    fun maskPhone(phone: String): String {
        if (phone.length < 10) return phone

        val startIndex = 6
        val endIndex = phone.length - 2

        val number = phone.substring(startIndex, endIndex)
        val hiddenNumber = number.replace(Regex("[0-9]"), "*")

        return phone.take(startIndex) + hiddenNumber + phone.takeLast(2)
    }
}