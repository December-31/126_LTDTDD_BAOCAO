package com.example.giapha.ui.auth

import android.os.Bundle
import android.view.View
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.giapha.R
import com.example.giapha.data.Kq
import com.example.giapha.data.LoiApp
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityLoginBinding
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applySystemBarsPadding
import com.example.giapha.ui.common.attachPhoneFormat
import com.example.giapha.ui.common.formatPhone
import com.example.giapha.ui.common.isValidPhone
import com.example.giapha.ui.common.setLightStatusBar

/** GD-02 — Đăng nhập bằng SĐT + OTP. */
class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private lateinit var otp: OtpBlock
    private val repo get() = Services.repository
    private var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.root.applySystemBarsPadding()
        setLightStatusBar(true)

        otp = OtpBlock(b.otpBlock)
        otp.onChanged = { b.btnConfirm.isEnabled = otp.isFull }
        otp.onResend = {
            if (repo.guiMaDangNhap(phone).laOk) otp.show(formatPhone(phone))
        }

        b.phoneInput.etPhone.attachPhoneFormat { digits ->
            phone = digits
            showPhoneError(null)
            if (otp.isVisible) resetOtp()          // đổi số thì phải gửi mã lại
            b.btnSend.isEnabled = isValidPhone(digits)
        }

        b.btnSend.setOnClickListener {
            when (val r = repo.guiMaDangNhap(phone)) {
                is Kq.Loi -> showPhoneError(r.thongBao)
                is Kq.Ok -> {
                    b.btnSend.visibility = View.GONE
                    b.btnConfirm.visibility = View.VISIBLE
                    b.btnConfirm.isEnabled = false
                    otp.show(formatPhone(phone))
                }
            }
        }

        b.btnConfirm.setOnClickListener {
            when (val r = repo.dangNhap(phone, otp.code)) {
                is Kq.Loi ->
                    if (r.loi == LoiApp.OTP_SAI) otp.showError(r.thongBao) else showPhoneError(r.thongBao)
                is Kq.Ok -> Routes.sauDangNhap(this, r.giaTri)
            }
        }

        b.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun resetOtp() {
        otp.hide()
        b.btnConfirm.visibility = View.GONE
        b.btnSend.visibility = View.VISIBLE
    }

    /** message = null: trở về dòng gợi ý bình thường. */
    private fun showPhoneError(message: String?) {
        b.phoneInput.root.isActivated = message != null
        b.tvPhoneHint.text = message ?: "Mã OTP bảo mật sẽ gửi qua SMS"
        b.tvPhoneHint.setTextColor(
            getColor(if (message != null) R.color.danger else R.color.text_hint)
        )
    }

    override fun onDestroy() {
        otp.destroy()
        super.onDestroy()
    }
}