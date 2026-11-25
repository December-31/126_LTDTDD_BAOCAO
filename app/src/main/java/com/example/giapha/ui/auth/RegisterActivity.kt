package com.example.giapha.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.data.Kq
import com.example.giapha.data.LoiApp
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityRegisterBinding
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applySystemBarsPadding
import com.example.giapha.ui.common.attachPhoneFormat
import com.example.giapha.ui.common.formatPhone
import com.example.giapha.ui.common.isValidPhone
import com.example.giapha.ui.common.setLightStatusBar

/** GD-01 — Đăng ký bằng tên hiển thị + SĐT + OTP. Xong thì tự đăng nhập. */
class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var otp: OtpBlock
    private val repo get() = Services.repository
    private var phone = ""

    private val name get() = b.etName.text.toString().trim()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.root.applySystemBarsPadding()
        setLightStatusBar(true)

        otp = OtpBlock(b.otpBlock)
        otp.onChanged = { b.btnConfirm.isEnabled = otp.isFull }
        otp.onResend = {
            if (repo.guiMaDangKy(phone).laOk) otp.show(formatPhone(phone))
        }

        b.etName.doAfterTextChanged {
            showNameError(false)
            updateSendEnabled()
        }
        b.etName.setOnFocusChangeListener { _, focused ->
            if (!focused && name.isEmpty()) showNameError(true)
        }

        b.phoneInput.etPhone.attachPhoneFormat { digits ->
            phone = digits
            showPhoneError(null)
            if (otp.isVisible) resetOtp()          // đổi số thì phải gửi mã lại
            updateSendEnabled()
        }

        b.btnSend.setOnClickListener {
            when (val r = repo.guiMaDangKy(phone)) {
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
            when (val r = repo.dangKy(name, phone, otp.code)) {
                is Kq.Loi -> when (r.loi) {
                    LoiApp.OTP_SAI -> otp.showError(r.thongBao)
                    LoiApp.DU_LIEU_KHONG_HOP_LE, LoiApp.DA_TON_TAI -> showPhoneError(r.thongBao)
                    else -> showPhoneError(r.thongBao)
                }
                is Kq.Ok -> Routes.sauDangNhap(this, r.giaTri)
            }
        }

        b.tvGoLogin.setOnClickListener { finish() }   // GD-02 nằm ngay dưới trong ngăn xếp
    }

    private fun updateSendEnabled() {
        b.btnSend.isEnabled = name.isNotEmpty() && isValidPhone(phone)
    }

    private fun resetOtp() {
        otp.hide()
        b.btnConfirm.visibility = View.GONE
        b.btnSend.visibility = View.VISIBLE
    }

    private fun showNameError(on: Boolean) {
        b.etName.isActivated = on
        b.tvNameError.visibility = if (on) View.VISIBLE else View.GONE
    }

    /** message = null: xóa lỗi. */
    private fun showPhoneError(message: String?) {
        b.phoneInput.root.isActivated = message != null
        b.tvPhoneError.text = message
        b.tvPhoneError.visibility = if (message != null) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        otp.destroy()
        super.onDestroy()
    }
}