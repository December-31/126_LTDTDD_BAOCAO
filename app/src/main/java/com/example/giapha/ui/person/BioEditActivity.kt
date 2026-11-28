package com.example.giapha.ui.person

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityBioEditBinding
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.setTextAction
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

/**
 * GD-09b — Viết mới hoặc sửa tiểu sử (Admin, Quản lý).
 * Chế độ Sửa khi Ô đã có tiểu sử. "Lưu" chỉ sáng khi có nội dung và khác bản gốc.
 * Thoát khi đã gõ dở thì hỏi "Bỏ qua thay đổi?".
 */
class BioEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityBioEditBinding
    private lateinit var btnSave: TextView
    private val repo get() = Services.repository
    private var idKhongGian = 0
    private var idO = 0
    private var goc = ""          // nội dung lúc mở màn; rỗng = chế độ Viết mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)
        if (repo.vaiTroCuaToi(idKhongGian)?.vaiTro?.laQuanTri != true) { finish(); return }
        when (val r = repo.tieuSuVaBinhLuan(idO)) {
            is Kq.Loi -> {
                Toast.makeText(this, r.thongBao, Toast.LENGTH_LONG).show()
                finish(); return
            }
            is Kq.Ok -> goc = r.giaTri.tieuSu?.noiDung.orEmpty()
        }

        b = ActivityBioEditBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.appBar.setup(this, if (goc.isEmpty()) "Viết tiểu sử" else "Sửa tiểu sử")
        btnSave = b.appBar.setTextAction(this, "Lưu") { luu() }
        b.content.applyBottomInsetPadding()

        b.etBio.setText(goc)
        b.etBio.doAfterTextChanged { capNhat() }
        b.etBio.requestFocus()
        b.etBio.setSelection(b.etBio.text.length)
        capNhat()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!coThayDoi()) { thoat(this); return }
                showConfirmDialog(
                    title = "Bỏ qua thay đổi?",
                    message = "Nội dung bạn đang viết sẽ không được lưu.",
                    confirmText = "Bỏ qua",
                    cancelText = "Tiếp tục viết",
                    showIcon = false
                ) { thoat(this) }
            }
        })
    }

    private fun thoat(cb: OnBackPressedCallback) {
        cb.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
    }

    private fun noiDung() = b.etBio.text.toString().trim()

    private fun coThayDoi() = noiDung() != goc.trim()

    private fun capNhat() {
        val ok = noiDung().isNotEmpty() && coThayDoi()
        btnSave.isEnabled = ok
        btnSave.alpha = if (ok) 1f else 0.5f
        val so = NumberFormat.getIntegerInstance(Locale("vi", "VN")).format(b.etBio.text.length)
        b.tvCount.text = "$so ký tự"
    }

    private fun luu() {
        btnSave.isEnabled = false            // tránh bấm hai lần khi đang lưu
        btnSave.alpha = 0.5f
        when (val r = repo.luuTieuSu(idO, noiDung())) {
            is Kq.Loi -> {
                Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
                capNhat()
            }
            is Kq.Ok -> {
                Toast.makeText(this, "Đã lưu tiểu sử", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
