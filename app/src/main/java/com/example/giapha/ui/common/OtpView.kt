package com.example.giapha.ui.common

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.giapha.R

/** Ô nhập OTP 6 số. isError = true thì viền đỏ cả 6 ô; gõ tiếp thì tự hết lỗi. */
class OtpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object { const val LENGTH = 6 }

    private val d = resources.displayMetrics.density
    private val boxes = mutableListOf<TextView>()
    private val input = EditText(context)
    private var errorOn = false

    /** Gọi khi nhập đủ 6 số. */
    var onCompleted: ((String) -> Unit)? = null
    /** Gọi mỗi khi nội dung đổi. */
    var onChanged: ((String) -> Unit)? = null

    var isError: Boolean
        get() = errorOn
        set(value) { errorOn = value; refresh() }

    val code: String get() = input.text.toString()

    init {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val font = ResourcesCompat.getFont(context, R.font.be_vietnam_pro_semibold)
        repeat(LENGTH) { i ->
            val tv = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    if (i > 0) marginStart = (12 * d).toInt()
                }
                gravity = Gravity.CENTER
                textSize = 20f
                typeface = font
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            }
            boxes.add(tv)
            row.addView(tv)
        }
        addView(row)

        input.apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            alpha = 0f
            background = null
            isCursorVisible = false
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(LENGTH))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    errorOn = false
                    refresh()
                    onChanged?.invoke(code)
                    if (code.length == LENGTH) onCompleted?.invoke(code)
                }
            })
            setOnFocusChangeListener { _, _ -> refresh() }
        }
        addView(input)
        refresh()
    }

    fun clear() {
        errorOn = false
        input.setText("")
        refresh()
    }

    fun focusInput() {
        input.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun refresh() {
        val text = input.text
        val active = minOf(text.length, LENGTH - 1)
        boxes.forEachIndexed { i, tv ->
            tv.text = text.getOrNull(i)?.toString() ?: ""
            val (strokeDp, colorRes) = when {
                errorOn -> 1.5f to R.color.danger
                input.hasFocus() && i == active -> 2f to R.color.brown_primary
                i < text.length -> 1f to R.color.brown_primary
                else -> 1f to R.color.border_default
            }
            tv.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12 * d
                setColor(ContextCompat.getColor(context, R.color.surface))
                setStroke((strokeDp * d).toInt(), ContextCompat.getColor(context, colorRes))
            }
        }
    }
}