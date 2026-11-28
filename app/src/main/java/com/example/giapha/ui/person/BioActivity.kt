package com.example.giapha.ui.person

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.data.TieuSuVaBinhLuan
import com.example.giapha.databinding.ActivityBioBinding
import com.example.giapha.databinding.ItemCommentBinding
import com.example.giapha.model.VaiTro
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.initialOf
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.example.giapha.ui.common.tuongDoi
import com.google.android.material.snackbar.Snackbar
import java.time.format.DateTimeFormatter

/**
 * GD-09a — Tiểu sử và bình luận của một Ô.
 * Mọi vai trò xem được. Viết/sửa tiểu sử: Admin, Quản lý. Bình luận: Admin, Quản lý, Thành viên.
 * Xóa bình luận: tác giả hoặc Admin/Quản lý. Tải lại mỗi lần hiện lại để thấy bản vừa lưu ở GD-09b.
 */
class BioActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityBioBinding
    private val repo get() = Services.repository
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var idKhongGian = 0
    private var idO = 0
    private var daCoDuLieu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityBioBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)

        b.appBar.setup(this, "Tiểu sử")
        b.inputBar.applyBottomInsetPadding()   // chừa chỗ cho thanh điều hướng và bàn phím

        b.btnEdit.setOnClickListener { Routes.moSoanTieuSu(this, idKhongGian, idO) }
        b.btnWrite.setOnClickListener { Routes.moSoanTieuSu(this, idKhongGian, idO) }
        b.etComment.doAfterTextChanged { capNhatNutGui() }
        b.btnSend.setOnClickListener { gui() }
        capNhatNutGui()
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoDuLieu) b.stateView.showLoading()
        when (val r = repo.tieuSuVaBinhLuan(idO)) {
            is Kq.Loi -> {
                daCoDuLieu = false
                b.inputBar.visibility = View.GONE
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hien(r.giaTri)
        }
    }

    // ------------------------------------------------------------------ hiển thị

    private fun hien(d: TieuSuVaBinhLuan) {
        daCoDuLieu = true
        b.stateView.hide()
        val vt = d.toi.vaiTro
        val quanTri = vt.laQuanTri
        val duocBinhLuan = quanTri || vt == VaiTro.THANH_VIEN

        b.appBar.tvTitle.text = "Tiểu sử — ${d.o.hoTen}"

        // Khối tiểu sử
        val ts = d.tieuSu
        b.bioFilled.visibility = if (ts != null) View.VISIBLE else View.GONE
        b.bioEmpty.visibility = if (ts == null) View.VISIBLE else View.GONE
        b.btnEdit.visibility = if (ts != null && quanTri) View.VISIBLE else View.GONE
        b.btnWrite.visibility = if (quanTri) View.VISIBLE else View.GONE
        if (ts != null) {
            b.tvBio.text = ts.noiDung
            b.tvAuthor.text = "Viết bởi ${ts.tenNguoiViet} — ${ts.ngayCapNhat.format(fmt)}"
        }

        // Khối bình luận
        b.tvCommentHead.text = "Bình luận (${d.binhLuan.size})"
        b.commentsEmpty.visibility = if (d.binhLuan.isEmpty()) View.VISIBLE else View.GONE
        b.listComments.removeAllViews()
        for (c in d.binhLuan) {
            val row = ItemCommentBinding.inflate(layoutInflater, b.listComments, false)
            row.tvAvatar.text = initialOf(c.tenNguoi)
            row.tvName.text = c.tenNguoi
            row.tvContent.text = c.noiDung
            row.tvTime.text = c.thoiGian.tuongDoi()
            row.btnDelete.visibility = if (c.xoaDuoc) View.VISIBLE else View.GONE
            row.btnDelete.setOnClickListener { hoiXoa(c.id) }
            b.listComments.addView(row.root)
        }

        b.inputBar.visibility = if (duocBinhLuan) View.VISIBLE else View.GONE
    }

    // ------------------------------------------------------------------ bình luận

    private fun capNhatNutGui() {
        val ok = b.etComment.text.isNotBlank()
        b.btnSend.isEnabled = ok
        b.btnSend.alpha = if (ok) 1f else 0.4f
    }

    private fun gui() {
        val nd = b.etComment.text.toString().trim()
        if (nd.isEmpty()) return
        b.btnSend.isEnabled = false          // tránh bấm hai lần khi đang gửi
        b.btnSend.alpha = 0.4f
        when (val r = repo.themBinhLuan(idO, nd)) {
            is Kq.Loi -> {
                Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
                capNhatNutGui()
            }
            is Kq.Ok -> {
                b.etComment.text?.clear()
                tai()
                // Bình luận mới nằm đầu danh sách: cuộn tới tiêu đề "Bình luận"
                b.scroll.post { b.scroll.smoothScrollTo(0, b.tvCommentHead.top) }
            }
        }
    }

    private fun hoiXoa(idBinhLuan: Int) {
        showConfirmDialog(
            title = "Xóa bình luận này?",
            message = "",
            confirmText = "Xóa",
            showIcon = false
        ) {
            when (val r = repo.xoaBinhLuan(idBinhLuan)) {
                is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
                is Kq.Ok -> {
                    tai()
                    Snackbar.make(b.root, "Đã xóa bình luận", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
