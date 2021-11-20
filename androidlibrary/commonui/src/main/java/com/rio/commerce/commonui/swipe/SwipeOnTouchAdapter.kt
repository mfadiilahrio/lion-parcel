package com.rio.commerce.commonui.swipe

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

abstract class SwipeOnItemTouchAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnItemTouchListener {
    private val TOUCH_SLOP = ViewConfiguration.get(context).scaledTouchSlop

    internal var startX: Int = 0
    internal var startY: Int = 0

    //代表开始滑动以显示hide的过程
    internal var beginSlide: Boolean = false

    //代表开始竖直滚动了
    internal var beginScroll: Boolean = false

    //因为在指定动画时禁止一切手势,在动画结束后可能仍会接受到move up但错过了开头的down,所以这个手势被污染了,需要丢弃
    internal var animatingPollute: Boolean = false

    //当HiddenView显示时,targetView被赋值,所以可以用targetView来判断是否当前有显示HiddenView的Item
    internal var targetView: SwipeHolder? = null
    internal var animatingView: SwipeHolder? = null

    //targetView对应的position
    internal var targetPosition: Int = 0

    //代表这个HiddenView可能被点击
    internal var pendingHiddenClick: Boolean = false

    val isBusy: Boolean
        get() = targetView != null


    private var enableSwipe: Boolean = false

    override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent
    ): Boolean {
        if (enableSwipe)
            return false
        if (animatingView != null && animatingView!!.isAnimating) {
            //置标志位,接下来的手势被污染了
            animatingPollute = true
            return true
        } else {
            animatingView = null
        }
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = motionEvent.x.toInt()
                startY = motionEvent.y.toInt()
                beginSlide = false
                beginScroll = false
                animatingPollute = false
                //当targetView不等于空时,我们需要判断以缩回可能显示的隐藏按钮
                targetView?.let {
                    val tx = startX.toFloat()
                    val ty = startY.toFloat()

                    if (isXInHiddenView(tx, ty)) {
                        pendingHiddenClick = true
                    } else {
                        it.animateCollapse()
                        animatingView = it
                        targetView = null
                        targetPosition = -1
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (animatingPollute)
                    return false
                if (beginScroll) {
                    return false
                }
                if (beginSlide) {
                    return true
                }
                if (!beginSlide) {
                    val horizontalDelta = (motionEvent.x - startX).toInt()
                    val verticalDelta = (motionEvent.y - startY).toInt()
                    //达到水平拉动的阈值
                    if (abs(horizontalDelta) > TOUCH_SLOP) {
                        if (recyclerView.findChildViewUnder(
                                motionEvent.x,
                                motionEvent.y
                            ) !is SwipeHolder
                        ) {
                            return false
                        }
                        beginSlide = true
                        targetView = recyclerView.findChildViewUnder(
                            motionEvent.x,
                            motionEvent.y
                        ) as SwipeHolder?
                        if (targetView == null) {
                            beginSlide = false
                            return false
                        }
                        targetPosition = layoutManager.getPosition(targetView!!)
                        startX = motionEvent.x.toInt()
                        return true
                    } else if (abs(verticalDelta) > TOUCH_SLOP) {//达到竖直滚动的阈值
                        beginScroll = true
                        return false
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (animatingPollute) {
                    animatingPollute = false
                    return false
                }
                targetView?.let {
                    val dx = motionEvent.x.toInt()
                    val dy = motionEvent.y.toInt()
                    if (isXInHiddenView(dx.toFloat(), dy.toFloat())) {
                        itemHiddenDidTap(it, targetPosition)
                        targetView = null
                    }
                } ?: run {
                    if (!beginSlide && !beginScroll) {
                        //如果不是在滑动过程中,那么就判定这是一个点击事件
                        val clickView =
                            recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
                        if (clickView != null) {
                            val clickPosition = layoutManager.getPosition(clickView)
                            itemDidTap(clickPosition)
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                beginSlide = false
                beginScroll = false
                animatingPollute = false
            }
        }
        return false
    }


    override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {
        if (animatingView != null && animatingView!!.isAnimating) {
            animatingPollute = true
            return
        } else {
            animatingView = null
        }
        when (motionEvent.action) {
            MotionEvent.ACTION_MOVE -> {
                if (animatingPollute) {
                    return
                }
                targetView?.slide((startX - motionEvent.x).toInt())
            }
            MotionEvent.ACTION_UP -> {
                if (animatingPollute) {
                    animatingPollute = false
                    return
                }
                //这里的ActionUp是响应水平拖动过程中的结束事件,需要重置hiddenView的状态,要么显示,要么隐藏
                val isShow = targetView?.determineShowOrHide() ?: false
                //如果没有显示一定要置空,表示他没有显示
                if (!isShow) {
                    targetView = null
                    targetPosition = -1
                }
            }
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {

    }

    /**
     * 判断x,y是否在当前targetView的范围 并且在targetView的点击区域
     *
     * @param x
     * @param y
     * @return
     */
    private fun isXInHiddenView(x: Float, y: Float): Boolean {
        return recyclerView.findChildViewUnder(
            x,
            y
        ) === targetView && targetView?.isXInHideArea(x.toInt()) == true
    }

    abstract fun itemHiddenDidTap(swipeHolder: SwipeHolder, position: Int)

    abstract fun itemDidTap(position: Int)

    /**
     * to enable swipe,set this to true
     *
     * @param enableSwipe
     */
    fun setEnableSwipe(enableSwipe: Boolean) {
        this.enableSwipe = enableSwipe
    }
}