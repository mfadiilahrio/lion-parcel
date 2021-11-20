package com.rio.commerce.commonui.extension

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rio.commerce.commonui.R

fun Fragment.contactSupport(
    context: Context,
    @StringRes email: Int,
    @StringRes title: Int,
    @StringRes message: Int,
    throwable: Throwable
) {
    contactSupport(context, getString(email), getString(title), getString(message), throwable)
}

fun Fragment.contactSupport(
    context: Context,
    email: String,
    title: String,
    message: String,
    throwable: Throwable
) {
    sendMail(context, arrayOf(email), title, message, throwable)
}

fun Fragment.sendMail(
    context: Context,
    recipients: Array<String>,
    title: String,
    message: String,
    throwable: Throwable
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.report) { _, _ ->
            openMail(recipients, message, throwable)
        }
        .show()
}

fun Fragment.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Fragment.getToolBarHeight(): Int {

    val ctx = context ?: return 0

    val attrs = intArrayOf(R.attr.actionBarSize)
    val ta = ctx.obtainStyledAttributes(attrs)
    val toolBarHeight = ta.getDimensionPixelSize(0, -1)
    ta.recycle()
    return toolBarHeight
}

fun Fragment.getTopBarHeight(): Int {
    var result = 0
    result = getStatusBarHeight() + getToolBarHeight()
    return result
}

private fun Fragment.openMail(recipients: Array<String>, message: String, throwable: Throwable) {
    val intent = Intent(Intent.ACTION_SEND)

    intent.type = "plain/text"
    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
    intent.putExtra(Intent.EXTRA_SUBJECT, message)
    intent.putExtra(Intent.EXTRA_TEXT, throwable.localizedMessage)

    activity?.startActivity(Intent.createChooser(intent, getString(R.string.report)))
}
