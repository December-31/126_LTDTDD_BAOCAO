package com.example.giapha.ui.person

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.R
import com.example.giapha.data.DanhSachThanhTuu
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityThanhTuuBinding
import com.example.giapha.databinding.DialogThanhTuuBinding
import com.example.giapha.databinding.ItemThanhTuuBinding
import com.example.giapha.model.ThanhTuu
import com.example.giapha.ui.common.applyBottomMarginInset
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.time.Year

/**
 * GD-10 — Thành tựu của một Ô.
 * Mọi vai trò xem được (Khách tạm bị chặn nếu không gian riêng tư). Thêm/sửa/xóa: Admin, Quản lý.
 * Danh sách xếp theo năm giảm dần (thành tựu không có năm nằm cuối); tải lại mỗi lần hiện lại.
 */
class ThanhTuuActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
        private const val NAM_NHO_NHAT = 1800
    }

    private lateinit var b: ActivityThanhTuuBinding
    private val repo get() = Services.repository
    private var idKhongGian = 0
    private var idO = 0
    private var daCoDuLieu = false
    private var quanTri = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityThanhTuuBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)

        b.appBar.setup(this, "Thành tựu")
        b.fab.applyBottomMarginInset()
        b.fab.setOnClickListener { hopThanhTuu(null) }
        b.btnAddEmpty.setOnClickListener { hopThanhTuu(null) }
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoDuLieu) b.stateView.showLoading()
        when (val r = repo.thanhTuuCua(idO)) {
            is Kq.Loi -> {
                daCoDuLieu = false
                b.fab.visibility = View.GONE
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hien(r.giaTri)
        }
    }

    // ------------------------------------------------------------------ hiển thị

    private fun hien(d: DanhSachThanhTuu) {
        daCoDuLieu = true
        b.stateView.hide()
        quanTri = d.toi.vaiTro.laQuanTri
        b.appBar.tvTitle.text = "Thành tựu — ${d.o.hoTen}"

        val rong = d.ds.isEmpty()
        b.scroll.visibility = if (rong) View.GONE else View.VISIBLE
        b.emptyBox.visibility = if (rong) View.VISIBLE else View.GONE
        b.btnAddEmpty.visibility = if (quanTri) View.VISIBLE else View.GONE
        b.fab.visibility = if (quanTri && !rong) View.VISIBLE else View.GONE

        b.listThanhTuu.removeAllViews()
        for (t in d.ds) b.listThanhTuu.addView(dongThanhTuu(t).root)
    }

    private fun dongThanhTuu(t: ThanhTuu): ItemThanhTuuBinding {
        val row = ItemThanhTuuBinding.inflate(layoutInflater, b.listThanhTuu, false)
        row.tvName.text = t.tenThanhTuu

        row.tvYear.visibility = if (t.nam != null) View.VISIBLE else View.GONE
        row.tvYear.text = t.nam?.toString()

        val moTa = t.moTa?.takeIf { it.isNotBlank() }
        if (moTa == null) {
            row.tvDesc.visibility = View.GONE
        } else {
            row.tvDesc.visibility = View.VISIBLE
            row.tvDesc.text = moTa
            // Chỉ hiện "Xem thêm" khi chữ thật sự bị cắt ở dòng thứ hai
            row.tvDesc.post {
                val l = row.tvDesc.layout
                if (l != null && l.getEllipsisCount(l.lineCount - 1) > 0) row.btnMore.visibility = View.VISIBLE
            }
            row.btnMore.setOnClickListener {
                row.tvDesc.maxLines = Int.MAX_VALUE
                row.tvDesc.ellipsize = null
                row.btnMore.visibility = View.GONE
            }
        }

        row.actions.visibility = if (quanTri) View.VISIBLE else View.GONE
        row.btnEdit.setOnClickListener { hopThanhTuu(t) }
        row.btnDelete.setOnClickListener { hoiXoa(t) }
        return row
    }

    // ------------------------------------------------------------------ thêm / sửa

    /** sua = null: thêm mới (hộp trống). Khác null: sửa, nạp sẵn dữ liệu cũ. */
    private fun hopThanhTuu(sua: ThanhTuu?) {
        val d = DialogThanhTuuBinding.inflate(LayoutInflater.from(this))
        var nam: Int? = sua?.nam

        d.tvTitle.text = if (sua == null) "Thêm thành tựu" else "Sửa thành tựu"
        if (sua != null) {
            d.etName.setText(sua.tenThanhTuu)
            d.etName.setSelection(d.etName.text.length)
            d.etDesc.setText(sua.moTa.orEmpty())
        }

        fun capNhatNam() {
            val n = nam
            d.tvYear.text = n?.toString() ?: "Chọn năm"
            d.tvYear.setTextColor(ContextCompat.getColor(this, if (n != null) R.color.text_primary else R.color.text_hint))
        }

        fun capNhatNut() {
            d.btnSave.isEnabled = d.etName.text.isNotBlank()
        }

        capNhatNam()
        capNhatNut()
        d.etName.doAfterTextChanged {
            d.tvError.visibility = View.GONE
            capNhatNut()
        }
        d.fieldYear.setOnClickListener {
            hopChonNam(nam) { nam = it; capNhatNam() }
        }

        val dialog = AlertDialog.Builder(this).setView(d.root).create()
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnSave.setOnClickListener {
            d.btnSave.isEnabled = false          // tránh bấm hai lần khi đang lưu
            d.btnSave.text = "Đang lưu..."
            val ten = d.etName.text.toString()
            val moTa = d.etDesc.text.toString()
            val r = if (sua == null) repo.themThanhTuu(idO, ten, nam, moTa)
            else repo.suaThanhTuu(sua.idThanhTuu, ten, nam, moTa)
            when (r) {
                is Kq.Loi -> {
                    d.tvError.text = r.thongBao
                    d.tvError.visibility = View.VISIBLE
                    d.btnSave.text = "Lưu"
                    capNhatNut()
                }
                is Kq.Ok -> {
                    dialog.dismiss()
                    tai()
                    thongBao(if (sua == null) "Đã thêm thành tựu" else "Đã lưu thành tựu")
                }
            }
        }

        dialog.show()
        val le = (24 * resources.displayMetrics.density).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), le, 0, le, 0))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    /** Chọn năm bằng bánh số. "Bỏ chọn" chỉ hiện khi đang có năm. */
    private fun hopChonNam(hienTai: Int?, onChon: (Int?) -> Unit) {
        val nayNam = Year.now().value
        val np = NumberPicker(this).apply {
            minValue = NAM_NHO_NHAT
            maxValue = nayNam
            value = hienTai ?: nayNam
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS   // chỉ cuộn, không gõ
        }
        val pad = (16 * resources.displayMetrics.density).toInt()
        val khung = FrameLayout(this).apply {
            setPadding(0, pad, 0, pad)
            addView(np, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        }
        val bd = AlertDialog.Builder(this)
            .setTitle("Chọn năm")
            .setView(khung)
            .setPositiveButton("Chọn") { _, _ -> onChon(np.value) }
            .setNegativeButton("Hủy", null)
        if (hienTai != null) bd.setNeutralButton("Bỏ chọn") { _, _ -> onChon(null) }
        bd.show()
    }

    // ------------------------------------------------------------------ xóa

    private fun hoiXoa(t: ThanhTuu) {
        showConfirmDialog(
            title = "Xóa thành tựu \"${t.tenThanhTuu}\"?",
            message = "",
            confirmText = "Xóa",
            showIcon = false
        ) {
            when (val r = repo.xoaThanhTuu(t.idThanhTuu)) {
                is Kq.Loi -> thongBao(r.thongBao, dai = true)
                is Kq.Ok -> {
                    tai()
                    thongBao("Đã xóa thành tựu")
                }
            }
        }
    }

    private fun thongBao(text: String, dai: Boolean = false) {
        val sb = Snackbar.make(b.root, text, if (dai) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
        if (b.fab.visibility == View.VISIBLE) sb.anchorView = b.fab
        sb.show()
    }
}
