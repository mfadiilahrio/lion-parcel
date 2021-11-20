package com.rio.commerce.commonui.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.rio.commerce.commonui.R
import com.rio.commerce.commonui.util.NumberUtil
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class CurrencyTextView : AppCompatTextView {
    private var mNumberFormat: NumberFormat? = null
    private var mThousandSymbol: String? = null
    private var mRoundUpDecimalPlaces = 0
    private var mSimplifiedPriceAfterLength = 0
    private var mReadableValues: Map<Double, String>? = null
    private var mCurrency: Currency? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {
        initXMLAttrs(context, attrs)
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initXMLAttrs(context, attrs)
        init()
    }

    private fun initXMLAttrs(
        context: Context,
        attrs: AttributeSet
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CurrencyTextView)
        mThousandSymbol = a.getString(R.styleable.CurrencyTextView_thousandSymbol)
        mRoundUpDecimalPlaces = a.getInt(R.styleable.CurrencyTextView_roundUpDecimalPlaces, 0)
        mSimplifiedPriceAfterLength =
            a.getInt(R.styleable.CurrencyTextView_simplifiedPriceAfterLength, 8)
        a.recycle()
    }

    private val thousandSymbol: String
        get() = (if (TextUtils.isEmpty(mThousandSymbol)) "," else mThousandSymbol)!!

    private var simplifiedPriceAfterLength: Int
        get() = if (mSimplifiedPriceAfterLength == 0) 8 else mSimplifiedPriceAfterLength
        set(length) {
            mSimplifiedPriceAfterLength = length
        }

    private fun init() {
        mReadableValues = linkedMapOf(
            1000000000000.0 to context.getString(R.string.trillion),
            1000000000.0 to context.getString(R.string.billion),
            1000000.0 to context.getString(R.string.million),
            1000.0 to context.getString(R.string.thousand),
            1.0 to ""
        )

        mNumberFormat = NumberFormat.getCurrencyInstance(
            Locale(
                context.getString(R.string.language),
                context.getString(R.string.country)
            )
        )
        mCurrency = Currency.getInstance(
            Locale(
                context.getString(R.string.language),
                context.getString(R.string.country)
            )
        )
    }

    val currency: String
        get() = mCurrency!!.currencyCode

    @SuppressLint("SetTextI18n")
    fun setPrice(
        price: String,
        isSimplified: Boolean,
        prefix: String
    ) {
        if ("-1" == price) {
            text = context.getString(R.string.ask_for_price)
        } else if (!isSimplified && price.length <= simplifiedPriceAfterLength) {
            val formattedPrice = mNumberFormat!!.format(BigDecimal(price))
            text = prefix + formattedPrice
        } else {
            setPrice(price, prefix)
        }
    }

    fun setPrice(price: String) {
        setPrice(price, null, "")
    }

    fun setPrice(price: String, isSimplified: Boolean) {
        setPrice(price, isSimplified, "")
    }

    fun setPrice(
        price: String,
        prefix: String,
        isSimplified: Boolean
    ) {
        setPrice(price, isSimplified, prefix)
    }

    fun setPrice(price: String, prefix: String) {
        setPrice(price, null, prefix)
    }

    @SuppressLint("SetTextI18n")
    fun setPrice(
        from: String,
        to: String,
        isSimplified: Boolean,
        prefix: String
    ) {
        if ("-1" == from || "-1" == to) {
            text = context.getString(R.string.ask_for_price)
        } else if (!isSimplified && to.length <= simplifiedPriceAfterLength) {
            val formattedToPrice = mNumberFormat!!.format(BigDecimal(from))
            var formattedFromPrice = mNumberFormat!!.format(BigDecimal(to))
            formattedFromPrice =
                formattedFromPrice.replace(context.getString(R.string.currency_symbol), "")
            text = "$prefix$formattedToPrice - $formattedFromPrice"
        } else {
            setPrice(from, to, prefix)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setPrice(from: String, to: String?, prefix: String) {
        val stripFrom = from.replace(thousandSymbol, "")
        var stripTo: String? = null
        if ("-1" == from || "-1" == to) {
            text = context.getString(R.string.ask_for_price)
            return
        }
        if ("0" == to) {
            text = prefix + context.getString(R.string.currency_symbol) + from
            return
        }
        if (to != null && to.isNotEmpty()) {
            stripTo = to.replace(thousandSymbol, "")
        }
        if (mReadableValues == null || !NumberUtil.isCreatable(stripFrom) && stripTo != null && !NumberUtil.isCreatable(
                stripTo
            )
        ) {
            text = prefix + context.getString(R.string.currency_symbol) + from + " - " + to
            return
        }
        val doubleFrom = java.lang.Double.valueOf(stripFrom)
        var formattedFrom: String
        var amountFrom = ""
        var flooredFrom = 0.0
        for ((limit, value) in mReadableValues!!) {
            if (doubleFrom >= limit) {
                flooredFrom = if (mRoundUpDecimalPlaces > 0) {
                    Math.floor(doubleFrom / limit * (10.0 * mRoundUpDecimalPlaces)) / (10.0 * mRoundUpDecimalPlaces)
                } else {
                    doubleFrom / limit
                }
                amountFrom = value
                break
            }
        }

        // Double with no decimal
        formattedFrom = if (flooredFrom % 1 == 0.0) {
            flooredFrom.toInt().toString()
        } else {
            flooredFrom.toString()
        }
        if (stripTo == null) {
            text =
                prefix + context.getString(R.string.currency_symbol) + formattedFrom.trim { it <= ' ' } + " " + amountFrom
            return
        }
        val doubleTo = java.lang.Double.valueOf(stripTo)
        var formattedTo: String
        var amountTo = ""
        var flooredTo = 0.0
        for ((limit, value) in mReadableValues!!) {
            if (doubleTo >= limit) {
                flooredTo = if (mRoundUpDecimalPlaces > 0) {
                    Math.floor(doubleTo / limit * (10.0 * mRoundUpDecimalPlaces)) / (10.0 * mRoundUpDecimalPlaces)
                } else {
                    doubleTo / limit
                }
                amountTo = value
                break
            }
        }

        // Double with no decimal
        formattedTo = if (flooredTo % 1 == 0.0) {
            flooredTo.toInt().toString()
        } else {
            flooredTo.toString()
        }
        if (amountFrom != amountTo) {
            formattedFrom += " $amountFrom"
        }
        formattedTo += " $amountTo"
        text =
            prefix + context.getString(R.string.currency_symbol) + formattedFrom.trim { it <= ' ' } + " - " + formattedTo.trim { it <= ' ' }
    }

    val decimalValue: BigDecimal
        get() {
            if (TextUtils.isEmpty(text.toString())) return BigDecimal(0)
            try {
                val currency: Double =
                    mNumberFormat?.parse(text.toString().trim { it <= ' ' })?.toDouble() ?: 0.0
                return BigDecimal(currency)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return BigDecimal(0)
        }
}