package com.rio.commerce.commonui.view

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.rio.commerce.commonui.R
import java.math.BigDecimal
import java.util.*
import kotlin.math.abs


class CurrencyTextWatcher
    (
    private val mEditText: TextInputEditText,
    private val mFormatType: String,
    private val mLocale: Locale,
    private val mAllowEmpty: Boolean
) : TextWatcher {

    private var maxLength = java.lang.Double.MAX_VALUE

    private var mCurrent = ""
    private var mIsInsertingSelected = false

    var maxAmount: BigDecimal? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        if (s.toString() != mCurrent) {
            mEditText.removeTextChangedListener(this)

            val cleanText = s.toString().replace("[^\\d]".toRegex(), "")

            mEditText.setText(cleanText)
            mEditText.addTextChangedListener(this)
        }
    }


    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        if (start == 0 && before == 0) {
            mIsInsertingSelected = true
        }
    }


    @Synchronized
    override fun afterTextChanged(s: Editable) {

        if (s.toString() != mCurrent) {
            mEditText.removeTextChangedListener(this)

            var digits = s.toString()

            if (mIsInsertingSelected) {
                digits = toDouble(digits).toString()
            }

            val formattedText: String

            var value = try {
                java.lang.Double.parseDouble(digits)
            } catch (nfe: NumberFormatException) {
                toDouble(digits)
            }

            maxAmount?.let {
                if (value > it.toDouble()) {
                    value = it.toDouble()
                }
            }

            formattedText =
                if (s.toString() == mEditText.context.getString(R.string.currency_symbol) && mAllowEmpty) {
                    ""
                } else if ("-1" == s.toString()) {
                    ""
                } else {
                    String.format(mLocale, mFormatType, value)
                }

            mCurrent = formattedText
            mEditText.setText(formattedText)
            mEditText.setSelection(formattedText.length)
            mEditText.addTextChangedListener(this)
        }
    }

    private fun deleteLastChar(cleanText: String): String {
        return if (cleanText.isNotEmpty()) {
            cleanText.substring(0, cleanText.length - 1)
        } else {
            "0"
        }
    }

    /**
     * @param doubleValue String with special characters
     * @return a double value of string
     */
    private fun toDouble(doubleValue: String): Double {
        var str = doubleValue
        str = str.replace("[^\\d]".toRegex(), "")

        if (str.isNotEmpty()) {

            var divider = 100

            if (mFormatType.contains(".0f")) divider = 1

            var value = java.lang.Double.parseDouble(str)
            val sValue = abs(value / divider).toString()
            val doublePlaces = sValue.indexOf('.').toDouble()

            if (doublePlaces > maxLength) {
                value = java.lang.Double.parseDouble(deleteLastChar(str))
            }

            return value / divider
        } else {
            return 0.0
        }
    }
}