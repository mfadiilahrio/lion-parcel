package com.rio.commerce.commonui.util

import android.text.TextUtils

object NumberUtil {
    private val JAVA_SPECIFICATION_VERSION =
        getSystemProperty("java.specification.version")
    private val IS_JAVA_1_6 = getJavaVersionMatches("1.6")
    fun isCreatable(str: String): Boolean {
        if (TextUtils.isEmpty(str)) {
            return false
        }
        val chars = str.toCharArray()
        var sz = chars.size
        var hasExp = false
        var hasDecPoint = false
        var allowSigns = false
        var foundDigit = false
        // deal with any possible sign up front
        val start = if (chars[0] == '-' || chars[0] == '+') 1 else 0
        val hasLeadingPlusSign = start == 1 && chars[0] == '+'
        if (sz > start + 1 && chars[start] == '0') { // leading 0
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') { // leading 0x/0X
                var i = start + 2
                if (i == sz) {
                    return false // str == "0x"
                }
                // checking hex (it can't be anything else)
                while (i < chars.size) {
                    if ((chars[i] < '0' || chars[i] > '9')
                        && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')
                    ) {
                        return false
                    }
                    i++
                }
                return true
            } else if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                var i = start + 1
                while (i < chars.size) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false
                    }
                    i++
                }
                return true
            }
        }
        sz-- // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        var i = start
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
            if (chars[i] in '0'..'9') {
                foundDigit = true
                allowSigns = false
            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false
                }
                hasDecPoint = true
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false
                }
                if (!foundDigit) {
                    return false
                }
                hasExp = true
                allowSigns = true
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false
                }
                allowSigns = false
                foundDigit = false // we need a digit after the E
            } else {
                return false
            }
            i++
        }
        if (i < chars.size) {
            if (chars[i] in '0'..'9') {
                return !(IS_JAVA_1_6 && hasLeadingPlusSign && !hasDecPoint)
                // no type qualifier, OK
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false
            }
            if (chars[i] == '.') {
                return if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    false
                } else foundDigit
                // single trailing decimal point after non-exponent is ok
            }
            if (!allowSigns
                && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')
            ) {
                return foundDigit
            }
            return if (chars[i] == 'l'
                || chars[i] == 'L'
            ) {
                // not allowing L with an exponent or decimal point
                foundDigit && !hasExp && !hasDecPoint
            } else false
            // last character is illegal
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit
    }

    private fun getJavaVersionMatches(versionPrefix: String): Boolean {
        return isJavaVersionMatch(JAVA_SPECIFICATION_VERSION, versionPrefix)
    }

    private fun isJavaVersionMatch(
        version: String?,
        versionPrefix: String
    ): Boolean {
        return version?.startsWith(versionPrefix) ?: false
    }

    private fun getSystemProperty(property: String): String? {
        return try {
            System.getProperty(property)
        } catch (ex: SecurityException) {
            // we are not allowed to look at this property
            System.err.println(
                "Caught a SecurityException reading the system property '" + property
                        + "'; the SystemUtils property value will default to null."
            )
            null
        }
    }
}