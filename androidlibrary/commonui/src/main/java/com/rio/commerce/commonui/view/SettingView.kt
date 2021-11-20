package com.rio.commerce.commonui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.rio.commerce.commonui.R

class SettingView : ConstraintLayout {
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
        val view = View.inflate(context, R.layout.view_settings_item, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.SettingView)

        val tvTitle: TextView = view.findViewById(R.id.title)
        val imgIndicator: ImageView = view.findViewById(R.id.indicator)

        tvTitle.text = a.getString(R.styleable.SettingView_text)
        imgIndicator.visibility = if (a.getBoolean(R.styleable.SettingView_showIndicator, true)) {
            View.VISIBLE
        } else {
            View.GONE
        }

        isClickable = true
        isFocusable = true

        with(TypedValue()) {
            context.theme.resolveAttribute(R.attr.selectableItemBackground, this, true)

            if (this.resourceId != 0) {
                setBackgroundResource(resourceId)
            } else {
                setBackgroundColor(this.data)
            }
        }

        a.recycle()
    }
}