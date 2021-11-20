package com.rio.commerce.commonui.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.rio.commerce.commonui.view.CurrencyEditText
import java.math.BigDecimal

fun EditText.onChange(input: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            input(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.text() = this.text.toString()

fun EditText.setText(text: CharSequence?, adjustSelection: Boolean) {
    text?.let {
        this.setText(it)

        if (adjustSelection) {
            this.setSelection(it.length)
        }
    }
}

fun CurrencyEditText.onAmountChange(input: (BigDecimal) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            input(decimalValue)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
