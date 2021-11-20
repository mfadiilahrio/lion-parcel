package com.rio.commerce.commonui.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.rio.commerce.commonui.R

class CustomProgressDialog {

    lateinit var dialog: CustomDialog

    private lateinit var layout: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressTitle: TextView

    fun init(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.view_progress_dialog, null)

        layout = view.findViewById(R.id.cp_layout)
        progressBar = view.findViewById(R.id.cp_progress)
        progressTitle = view.findViewById(R.id.cp_title)

        if (title != null) {
            progressTitle.text = title
        }

        // Card Color
        layout.setCardBackgroundColor(Color.parseColor("#70000000"))

        // Progress Bar Color
        setColorFilter(
            progressBar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        )

        // Text Color
        progressTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

        dialog = CustomDialog(context)
        dialog.setContentView(view)
        return dialog
    }

    fun show() {
        return dialog.show()
    }

    fun dismiss() {
        return dialog.dismiss()
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                    insets.consumeSystemWindowInsets()
                }
            }
        }
    }
}