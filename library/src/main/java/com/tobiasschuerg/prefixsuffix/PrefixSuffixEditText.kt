package com.tobiasschuerg.prefixsuffix

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.tobiasschuerg.library.R


/**
 * [AppCompatEditText] with easy prefix and suffix support.
 *
 * Inspired by https://gist.github.com/morristech/5480419
 */
class PrefixSuffixEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int = 0
) : AppCompatEditText(context, attrs) {


    private val textPaint: TextPaint by lazy {
        TextPaint().apply {
            color = currentTextColor
            textSize = this@PrefixSuffixEditText.textSize
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
            this.typeface = typeface
        }
    }

    private val prefixDrawable: PrefixDrawable by lazy { PrefixDrawable(paint) }

    var prefix: String = ""
        set(value) {
            if (value.isNotBlank()) {
                Log.v(TAG, "prefix: $value")
            }
            field = value
            prefixDrawable.text = value
            updatePrefixDrawable()
        }

    var suffix: String? = null
        set(value) {
            if (!value.isNullOrBlank()) {
                Log.v(TAG, "suffix: $value")
            }
            field = value
            invalidate()
        }

    var prefixSuffixColor: Int = currentTextColor
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    // These are used to store details obtained from the EditText's rendering process
    private val firstLineBounds = Rect()

    private var isInitialized = false

    init {
        updatePrefixDrawable()
        isInitialized = true

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PrefixSuffixEditText)
        prefix = typedArray.getString(R.styleable.PrefixSuffixEditText_prefix) ?: ""
        suffix = typedArray.getString(R.styleable.PrefixSuffixEditText_suffix)
        prefixSuffixColor = typedArray.getColor(R.styleable.PrefixSuffixEditText_prefixSuffixColor, currentTextColor)
        typedArray.recycle()
    }

    override fun setTypeface(typeface: Typeface) {
        super.setTypeface(typeface)

        if (isInitialized) {
            // this is first called from the constructor when it's not initialized, yet
            textPaint.typeface = typeface
        }

        postInvalidate()
    }

    public override fun onDraw(c: Canvas) {

        val lineBounds = getLineBounds(0, firstLineBounds)
        prefixDrawable.let {
            it.lineBounds = lineBounds
            it.paint = textPaint
        }

        super.onDraw(c)

        // Now we can calculate what we need!
        val text = text.toString()
        val prefixText: String = prefixDrawable.text
        val visibleHint = if(text.isEmpty()) hint else ""
        val textWidth: Float = textPaint.measureText(prefixText + text + visibleHint) + paddingLeft

        suffix?.let {
            // We need to draw this like this because
            // setting a right drawable doesn't work properly and we want this
            // just after the text we are editing (but untouchable)
            val y2 = firstLineBounds.bottom - textPaint.descent()
            c.drawText(it, textWidth, y2, textPaint)
        }
    }

    private fun updatePrefixDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setCompoundDrawablesRelative(prefixDrawable, null, null, null)
        } else {
            setCompoundDrawables(prefixDrawable, null, null, null)
        }
    }

    companion object {
        private const val TAG = "PrefixSuffixEditText"
    }
}
