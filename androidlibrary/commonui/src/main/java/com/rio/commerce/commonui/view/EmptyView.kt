package com.rio.commerce.commonui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.rio.commerce.commonui.R

class EmptyView : LinearLayout {
    private lateinit var mTvTitle: TextView
    private lateinit var mTvSubtitle: TextView
    private lateinit var mBtAction: CustomButton

    var listener: Listener? = null

    var title: String? = null
        set(value) {
            field = value

            mTvTitle.text = value
        }

    var subtitle: String? = null
        set(value) {
            field = value

            mTvSubtitle.text = value
        }

    var actionTitle: String? = null
        set(value) {
            field = value

            value?.let {
                mBtAction.visibility = View.VISIBLE
                mBtAction.text = it
            } ?: run {
                mBtAction.visibility = View.GONE
            }
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.view_empty, this)

        mTvTitle = view.findViewById(R.id.title)
        mTvSubtitle = view.findViewById(R.id.subtitle)
        mBtAction = view.findViewById(R.id.action)

        mBtAction.setOnClickListener {
            listener?.actionDidTap()
        }
    }

    interface Listener {
        fun actionDidTap()
    }
}