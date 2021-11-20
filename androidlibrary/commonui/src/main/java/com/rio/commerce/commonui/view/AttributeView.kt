package com.rio.commerce.commonui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.rio.commerce.commonui.R

class AttributeView : LinearLayout {

    private lateinit var mTvKey: TextView
    private lateinit var mTvValue: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val view =
            View.inflate(context, R.layout.view_attribute, this)

        mTvKey = view.findViewById(R.id.key)
        mTvValue = view.findViewById(R.id.value)
    }

}