package com.rio.commerce.commonui.extension

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rio.commerce.commonui.R
import com.rio.commerce.commonui.base.LoadableModule

val AppCompatActivity.appShared: LoadableModule
    get() = application as LoadableModule

fun Activity.contactSupport(
    @StringRes email: Int,
    @StringRes title: Int,
    @StringRes message: Int,
    throwable: Throwable,
    setFinish: Boolean = false
) {
    contactSupport(getString(email), getString(title), getString(message), throwable, setFinish)
}

fun Activity.contactSupport(
    email: String,
    title: String,
    message: String,
    throwable: Throwable,
    setFinish: Boolean = false
) {
    sendMail(arrayOf(email), title, message, throwable, setFinish)
}

fun Activity.sendMail(
    recipients: Array<String>,
    title: String,
    message: String,
    throwable: Throwable,
    setFinish: Boolean = false
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(R.string.cancel) { _, _ ->
            if (setFinish) {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        .setPositiveButton(R.string.report) { _, _ ->
            openMail(recipients, message, throwable)
            if (setFinish) {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        .show()
}

private fun Activity.openMail(recipients: Array<String>, message: String, throwable: Throwable) {
    val intent = Intent(Intent.ACTION_SEND)

    intent.type = "plain/text"
    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
    intent.putExtra(Intent.EXTRA_SUBJECT, message)
    intent.putExtra(Intent.EXTRA_TEXT, throwable.localizedMessage)

    startActivity(Intent.createChooser(intent, getString(R.string.report)))
}

fun AppCompatActivity.showBackButton(@DrawableRes icon: Int = R.drawable.ic_arrow_back) {

    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    supportActionBar?.setHomeAsUpIndicator(icon)
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun Activity.fadingBar(cd: ColorDrawable, position: Int) {
    val minDist = 0
    val maxDist = 10
    cd.alpha = when {
        position > maxDist -> 255
        position < minDist -> 0
        else -> {
            val alpha: Int = (255.0 / maxDist * position).toInt()
            alpha
        }
    }

    val color = if (cd.alpha >= 1) {
        cd.color
    } else {
        Color.TRANSPARENT
    }

    window?.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = color

        }
    }
}

fun Activity.makeActionBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decoration = if (isDarkModeOn(this@makeActionBarTransparent)) {
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                } else {
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                decorView.systemUiVisibility = decoration

            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun Activity.statusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

private fun isDarkModeOn(context: Context): Boolean {
    val currentNightMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}