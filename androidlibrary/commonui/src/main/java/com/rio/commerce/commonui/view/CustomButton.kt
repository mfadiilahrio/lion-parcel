package com.rio.commerce.commonui.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.rio.commerce.commonui.R

class CustomButton : MaterialButton {
    private val mIdleText = text

    var loadingText: CharSequence? = context.getString(R.string.custom_button_loading)

    var loading: Boolean = false
        set(value) {
            field = value
            if (value) {
                isEnabled = false
                text = loadingText
            } else {
                isEnabled = true
                text = mIdleText
            }
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initXMLAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initXMLAttrs(context, attrs)
    }

    private fun initXMLAttrs(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        loadingText = a.getString(R.styleable.CustomButton_loadingText)
        a.recycle()
    }
}