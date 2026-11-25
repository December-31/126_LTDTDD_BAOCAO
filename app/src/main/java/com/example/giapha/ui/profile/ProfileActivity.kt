package com.example.giapha.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityProfileBinding
import com.example.giapha.model.TaiKhoan
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.initialOf
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.time.format.DateTimeFormatter

/** GD-04 — Hồ sơ cá nhân: sửa Tên hiển thị và Email; SĐT chỉ đọc. */
class ProfileActivity : AppCompatActivity() {

    private lateinit var b: ActivityProfileBinding
    private val repo get() = Services.repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.appBar.setup(this, "Hồ sơ cá nhân")
        b.scroll.applyBottomInsetPadding()

        val tk = repo.taiKhoanHienTai() ?: return Routes.dangXuat(this)
        bind(tk)

        b.etName.doAfterTextChanged { setError(b.etName, b.tvNameError, null) }
        b.etEmail.doAfterTextChanged { setError(b.etEmail, b.tvEmailError, null) }
        b.btnSave.setOnClickListener { save() }
        b.btnLogout.setOnClickListener {
            showConfirmDialog(
                "Đăng xuất?", "Bạn sẽ cần đăng nhập lại bằng mã OTP.", "Đăng xuất", destructive = false
            ) { Routes.dangXuat(this) }
        }
    }

    private fun bind(tk: TaiKhoan) {
        b.tvAvatar.text = initialOf(tk.tenHienThi)
        b.etName.setText(tk.tenHienThi)
        b.etEmail.setText(tk.email.orEmpty())
        // "0912345678" -> "+84 912 345 678"
        b.tvPhone.text = "+84 " + tk.sdt.drop(1).chunked(3).joinToString(" ")
        b.tvCreated.text = tk.ngayTao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        b.tvLastLogin.text =
            tk.lanDangNhapCuoi?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")) ?: "—"
    }

    private fun save() {
        val name = b.etName.text.toString()
        when (val r = repo.capNhatHoSo(name, b.etEmail.text.toString())) {
            is Kq.Loi ->
                if (name.isBlank()) setError(b.etName, b.tvNameError, r.thongBao)
                else setError(b.etEmail, b.tvEmailError, r.thongBao)
            is Kq.Ok -> {
                bind(r.giaTri)
                currentFocus?.clearFocus()
                WindowCompat.getInsetsController(window, b.root).hide(WindowInsetsCompat.Type.ime())
                Snackbar.make(b.root, "Đã cập nhật", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setError(et: EditText, tv: TextView, message: String?) {
        et.isActivated = message != null
        tv.text = message
        tv.visibility = if (message != null) View.VISIBLE else View.GONE
    }
}
