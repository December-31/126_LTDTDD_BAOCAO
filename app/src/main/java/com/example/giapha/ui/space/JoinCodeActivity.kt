package com.example.giapha.ui.space

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityJoinCodeBinding
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.setup

/** GD-05 — Nhập mã lời mời để xem tạm một không gian (Khách tạm). */
class JoinCodeActivity : AppCompatActivity() {

    private lateinit var b: ActivityJoinCodeBinding
    private val repo get() = Services.repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityJoinCodeBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.appBar.setup(this, "Nhập mã lời mời")
        b.root.applyBottomInsetPadding()

        b.etCode.doAfterTextChanged {
            b.btnConfirm.isEnabled = !it.isNullOrBlank()
            showError(null)
        }
        b.btnConfirm.setOnClickListener {
            when (val r = repo.vaoBangMa(b.etCode.text.toString())) {
                is Kq.Loi -> showError(r.thongBao)
                is Kq.Ok -> {
                    Routes.moKhongGian(this, r.giaTri.idKhongGian)
                    finish()   // Back từ cây quay về GD-03, không quay lại màn này
                }
            }
        }
    }

    private fun showError(message: String?) {
        b.etCode.isActivated = message != null
        b.tvError.text = message
        b.tvError.visibility = if (message != null) View.VISIBLE else View.GONE
    }
}