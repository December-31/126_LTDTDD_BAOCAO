package com.example.giapha.ui.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/** Số điện thoại hợp lệ: đúng 10 chữ số, bắt đầu bằng 0. */
fun isValidPhone(digits: String) = digits.length == 10 && digits.startsWith("0")

/** Chỉ lấy chữ số từ nội dung ô (bỏ dấu cách). */
val EditText.phoneDigits: String get() = text.toString().filter { it.isDigit() }

/** Tự định dạng "0912 345 678" khi gõ. onChange nhận chuỗi 10 số không có dấu cách. */
fun EditText.attachPhoneFormat(onChange: ((String) -> Unit)? = null) {
    addTextChangedListener(object : TextWatcher {
        private var busy = false
        override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
        override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (busy) return
            busy = true
            val digits = s.toString().filter { it.isDigit() }.take(10)
            val formatted = buildString {
                digits.forEachIndexed { i, ch ->
                    if (i == 4 || i == 7) append(' ')
                    append(ch)
                }
            }
            if (formatted != s.toString()) {
                setText(formatted)
                setSelection(formatted.length)
            }
            busy = false
            onChange?.invoke(digits)
        }
    })
}