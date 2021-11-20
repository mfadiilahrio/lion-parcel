package com.rio.commerce.commonui.view

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rio.commerce.commonui.R
import com.rio.commerce.core.data.model.Parcelable
import com.rio.commerce.core.data.model.Parcelize
import kotlinx.android.parcel.RawValue
import px
import java.io.Serializable

class ActionSheet : BottomSheetDialogFragment() {

    enum class Style {
        DEFAULT, CANCEL, DESTRUCTIVE
    }

    @Parcelize
    data class Action(
        val title: String,
        val type: Style,
        val selectableIcon: Boolean = false,
        val setEnabledDisabled: Boolean = false,
        val selected: Boolean = false,
        val enabled: Boolean = false,
        val handler: @RawValue ((Action) -> Unit)?
    ) : Parcelable, Serializable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.view_action_sheet, container, false)

        val title = arguments?.getString(TITLE)
        val message = arguments?.getString(MESSAGE)
        val actions = arguments?.getParcelableArrayList<Action>(ACTIONS)

        val tvTitle: TextView = view.findViewById(R.id.bottom_sheet_title)
        val tvSubtitle: TextView = view.findViewById(R.id.bottom_sheet_message)

        val buttonContainer: ViewGroup = view.findViewById(R.id.button_container)
        val cancelContainer: ViewGroup = view.findViewById(R.id.cancel_container)

        title?.let { tvTitle.text = it } ?: run { tvTitle.visibility = View.GONE }
        message?.let { tvSubtitle.text = it } ?: run { tvSubtitle.visibility = View.GONE }

        dialog?.setOnShowListener {
            val sheet = it as? BottomSheetDialog ?: return@setOnShowListener

            val bottomSheet = sheet.findViewById<View>(R.id.design_bottom_sheet)

            bottomSheet?.background = null
        }

        actions?.forEach { action ->
            val sheetContainer = when (action.type) {
                Style.DEFAULT, Style.DESTRUCTIVE -> buttonContainer
                Style.CANCEL -> {
                    cancelContainer.visibility = View.VISIBLE
                    cancelContainer
                }
            }

            // Do not show separator if type is cancel and appear on the first index
            if (action.type != Style.CANCEL || (action.type == Style.CANCEL && cancelContainer.childCount > 0)) {
                View(this.context).apply {
                    val params =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.px)

                    layoutParams = params
                    setBackgroundColor(ContextCompat.getColor(view.context, R.color.grey300))
                }.run {
                    sheetContainer.addView(this)
                }
            }

            TextView(this.context).apply {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                setOnClickListener { _ ->
                    dismissAllowingStateLoss()
                    action.handler?.invoke(action)
                }

                val font = when (action.type) {
                    Style.DEFAULT, Style.DESTRUCTIVE -> if (action.selected) {
                        R.font.bold
                    } else {
                        R.font.medium
                    }
                    Style.CANCEL -> R.font.bold
                }

                val color = when (action.type) {
                    Style.DEFAULT, Style.CANCEL -> if (action.setEnabledDisabled) {
                        if (action.enabled) {
                            ContextCompat.getColor(
                                view.context,
                                R.color.colorPrimaryText
                            )
                        } else {
                            ContextCompat.getColor(
                                view.context,
                                R.color.grey300
                            )
                        }
                    } else {
                        ContextCompat.getColor(
                            view.context,
                            R.color.colorPrimaryText
                        )
                    }
                    Style.DESTRUCTIVE -> ContextCompat.getColor(view.context, R.color.error)
                }

                setTextColor(color)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.body_2))
                setPadding(15.px, 15.px, 15.px, 15.px)

                layoutParams = params
                typeface = ResourcesCompat.getFont(view.context, font)
                isClickable = true
                text = action.title
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                with(TypedValue()) {
                    view.context.theme.resolveAttribute(R.attr.selectableItemBackground, this, true)

                    if (this.resourceId != 0) {
                        setBackgroundResource(resourceId)
                    } else {
                        setBackgroundColor(this.data)
                    }
                }
            }.run {
                sheetContainer.addView(this)
            }
        }

        return view
    }

    companion object {
        const val TITLE = "title"
        const val MESSAGE = "message"
        const val ACTIONS = "actions"

        fun newInstance(
            title: String? = null,
            message: String? = null,
            actions: List<Action>
        ): ActionSheet {

            val fragment = ActionSheet()

            val args = Bundle().apply {
                putString(TITLE, title)
                putString(MESSAGE, message)
                putParcelableArrayList(ACTIONS, ArrayList(actions))
            }

            fragment.arguments = args

            return fragment
        }
    }
}

