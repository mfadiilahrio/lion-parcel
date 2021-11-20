package com.rio.commerce.commonui.extension

import androidx.core.content.ContextCompat
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.rio.commerce.commonui.R
import com.rio.commerce.core.data.model.Loading

private fun SkeletonLayout.defaultTheme(cornerRadius: Int) {
    maskCornerRadius = cornerRadius.toFloat()
    shimmerColor = ContextCompat.getColor(this.context, R.color.skeletonShimmer)
    maskColor = ContextCompat.getColor(this.context, R.color.skeleton)
}

fun SkeletonLayout.showSkeletonWithTheme(cornerRadius: Int = 25) {
    defaultTheme(cornerRadius)

    showSkeleton()
}

fun SkeletonLayout.toggleSkeleton(cornerRadius: Int = 25, loading: Loading) {

    if (loading.start) {
        defaultTheme(cornerRadius)

        showSkeleton()
    } else {
        showOriginal()
    }
}