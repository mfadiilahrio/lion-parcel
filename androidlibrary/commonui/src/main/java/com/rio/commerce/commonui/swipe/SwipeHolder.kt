package com.rio.commerce.commonui.swipe

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

class SwipeHolder : FrameLayout {

    private lateinit var hideContentHolder: HideContentHolder
    private lateinit var mainContent: View

    var isAnimating: Boolean = false
        internal set

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        hideContentHolder = getChildAt(0) as HideContentHolder
        mainContent = getChildAt(1)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        hideContentHolder.layout(
            right - hideContentHolder.measuredWidth,
            0,
            right,
            hideContentHolder.measuredHeight
        )
        mainContent.layout(0, 0, right, mainContent.measuredHeight)
    }

    fun slide(x: Int) {
        if (x < 0) {
            mainContent.scrollTo(0, 0)
            hideContentHolder.showPixel = 0
            return
        }
        val hideContentHolderWidth = hideContentHolder.width
        if (x > hideContentHolderWidth) {
            val delta = (x - hideContentHolderWidth).toFloat()
            mainContent.scrollTo((hideContentHolderWidth + delta * (1 - delta / x)).toInt(), 0)
        } else {
            mainContent.scrollTo(x, 0)
        }
        hideContentHolder.showPixel = x
    }

    /**
     * hide the hidden view
     */
    fun reset() {
        mainContent.scrollTo(0, 0)
        hideContentHolder.showPixel = 0
    }

    private fun animatedAlignShow() {
        val hideContentHolderWidth = hideContentHolder.width
        val deltaScrollX = mainContent.scrollX - hideContentHolderWidth
        val deltaShowPixel = hideContentHolder.showPixel - hideContentHolderWidth
        val valueAnimator = ValueAnimator.ofFloat(0.0f)
        valueAnimator.duration = ALIGN_ANIMATION_DURATION.toLong()
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            mainContent.scrollTo(
                hideContentHolderWidth + (deltaScrollX * (1 - fraction)).toInt(),
                0
            )
            hideContentHolder.showPixel =
                hideContentHolderWidth + (deltaShowPixel * (1 - fraction)).toInt()
        }
        valueAnimator.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                mainContent.scrollTo(hideContentHolderWidth, 0)
                isAnimating = false

            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.start()
        isAnimating = true
    }

    internal fun animateCollapse() {
        val deltaShowPixel = hideContentHolder.showPixel
        val deltaScrollX = mainContent.scrollX
        val valueAnimator = ValueAnimator.ofFloat(0.0f)
        valueAnimator.duration = ALIGN_ANIMATION_DURATION.toLong()
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            mainContent.scrollTo((deltaScrollX * (1 - fraction)).toInt(), 0)
            hideContentHolder.showPixel = (deltaShowPixel * (1 - fraction)).toInt()
        }
        valueAnimator.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                mainContent.scrollTo(0, 0)
                hideContentHolder.showPixel = 0
                isAnimating = false

            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.start()
        isAnimating = true
    }

    fun isXInHideArea(x: Int): Boolean {
        return x > hideContentHolder.left
    }

    fun determineShowOrHide(): Boolean {
        return if (hideContentHolder.showPixel < hideContentHolder.width / 3) {
            animateCollapse()
            false
        } else {
            animatedAlignShow()
            true
        }

    }

    companion object {
        const val ALIGN_ANIMATION_DURATION = 200
    }

}