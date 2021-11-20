package com.rio.commerce.commonui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.rio.commerce.commonui.R

class LoadingView : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val view = View.inflate(context, R.layout.view_loading, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        val tvTitle: TextView = view.findViewById(R.id.loading_text)
        tvTitle.text = a.getString(R.styleable.SettingView_text)

        a.recycle()
    }
}