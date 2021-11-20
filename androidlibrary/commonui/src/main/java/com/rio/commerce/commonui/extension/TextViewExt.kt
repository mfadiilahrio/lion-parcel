package com.rio.commerce.commonui.extension

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.rio.commerce.commonui.CustomTypefaceSpan
import com.rio.commerce.commonui.R

fun TextView.setText(
    text: String,
    markedText: String,
    @ColorRes color: Int,
    @FontRes font: Int = R.font.bold
) {
    val spannableText = SpannableString(text)

    val startIndex = text.indexOf(markedText)
    val endIndex = startIndex + markedText.length
    val typeface = ResourcesCompat.getFont(this.context, font)

    spannableText.setSpan(
        CustomTypefaceSpan(typeface),
        startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannableText.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, color)),
        startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannableText
}