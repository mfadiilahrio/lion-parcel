package com.rio.commerce.commonui.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.play.core.splitcompat.SplitCompat

abstract class MultiDexSplitCompatApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(this)
        SplitCompat.install(this)
    }
}