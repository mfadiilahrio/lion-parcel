package com.rio.commerce.commonui.view

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.rio.commerce.commonui.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class CurrencyEditText : TextInputEditText {

    private var mFormat: String? = null
    private var mNumberFormat: NumberFormat? = null
    private var mCurrency: Currency? = null
    private var mAllowEmpty = false

    private lateinit var mCurrencyTextWatcher: CurrencyTextWatcher

    private val priceFormat: String
        get() = if (TextUtils.isEmpty(mFormat)) context.getString(R.string.price_format) else mFormat
            ?: context.getString(R.string.price_format)

    val decimalValue: BigDecimal
        get() {
            if (TextUtils.isEmpty(text.toString())) return BigDecimal(0)

            try {

                val currency = mNumberFormat?.parse(text.toString().trim())?.toDouble()

                currency?.let {
                    return BigDecimal(it)
                }

            } catch (e: ParseException) {

            }

            return BigDecimal(0)
        }

    val currency: String
        get() = mCurrency?.currencyCode ?: ""

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        initXMLAttrs(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        initXMLAttrs(context, attrs)
        init()
    }

    fun setAmountLimit(amountLimit: BigDecimal) {
        mCurrencyTextWatcher.maxAmount = amountLimit
    }

    private fun initXMLAttrs(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditText)
        mFormat = a.getString(R.styleable.CurrencyEditText_priceFormat)
        mAllowEmpty = a.getBoolean(R.styleable.CurrencyEditText_allowEmpty, false)
        a.recycle()
    }

    private fun init() {
        inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER

        mCurrencyTextWatcher = CurrencyTextWatcher(
            this,
            priceFormat,
            Locale(context.getString(R.string.language), context.getString(R.string.country)),
            mAllowEmpty
        )

        addTextChangedListener(mCurrencyTextWatcher)

        mNumberFormat = NumberFormat.getCurrencyInstance(
            Locale(
                context.getString(R.string.language),
                context.getString(R.string.country)
            )
        )

        mCurrency =
            Currency.getInstance(
                Locale(
                    context.getString(R.string.language),
                    context.getString(R.string.country)
                )
            )
    }

}