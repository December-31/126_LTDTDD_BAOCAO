package com.example.giapha.ui.auth

import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.example.giapha.R
import com.example.giapha.databinding.ViewOtpBlockBinding
import com.example.giapha.ui.common.OtpView

/** Khối nhập OTP dùng chung GD-01 / GD-02. */
class OtpBlock(private val b: ViewOtpBlockBinding) {

    var onChanged: ((String) -> Unit)? = null
    var onResend: (() -> Unit)? = null

    private var timer: CountDownTimer? = null

    val code: String get() = b.otp.code
    val isVisible: Boolean get() = b.root.visibility == View.VISIBLE
    val isFull: Boolean get() = code.length == OtpView.LENGTH

    init {
        b.otp.onChanged = {
            b.tvOtpError.visibility = View.GONE
            onChanged?.invoke(it)
        }
        b.tvResend.setOnClickListener { if (b.tvResend.isEnabled) onResend?.invoke() }
    }

    /** Hiện khối OTP, xóa mã cũ, bắt đầu đếm ngược gửi lại. */
    fun show(phoneText: String) {
        b.root.visibility = View.VISIBLE
        b.tvOtpInfo.text = "Mã xác thực đã gửi tới $phoneText"
        b.otp.clear()
        b.tvOtpError.visibility = View.GONE
        b.otp.focusInput()
        startCountdown()
    }

    fun hide() {
        timer?.cancel()
        b.otp.clear()
        b.tvOtpError.visibility = View.GONE
        b.root.visibility = View.GONE
    }

    fun showError(message: String) {
        b.otp.clear()
        b.otp.isError = true
        b.tvOtpError.text = message
        b.tvOtpError.visibility = View.VISIBLE
    }

    fun destroy() { timer?.cancel() }

    private fun startCountdown() {
        timer?.cancel()
        val ctx = b.root.context
        b.tvResend.isEnabled = false
        b.tvResend.setTextColor(ContextCompat.getColor(ctx, R.color.text_hint))
        timer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(ms: Long) {
                b.tvResend.text = "Gửi lại mã sau ${(ms + 999) / 1000}s"
            }
            override fun onFinish() {
                b.tvResend.text = "Gửi lại mã"
                b.tvResend.isEnabled = true
                b.tvResend.setTextColor(ContextCompat.getColor(ctx, R.color.brown_primary))
            }
        }.start()
    }
}