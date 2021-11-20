package com.rio.commerce.commonui.swipe


import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

class HideContentHolder : FrameLayout {

    var showPixel = 0
        set(showPixel) {
            var pixel = showPixel
            if (pixel > width)
                pixel = width
            field = pixel
            this.invalidate()
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
        this.setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipRect(width - this.showPixel, 0, width, height)
        super.onDraw(canvas)
    }
}