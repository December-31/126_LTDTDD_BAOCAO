package com.example.giapha.ui.person

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.giapha.R
import com.example.giapha.data.ChieuQuanHe
import com.example.giapha.data.DongQuanHeChaMe
import com.example.giapha.data.Kq
import com.example.giapha.data.QuanHeChaMeCuaO
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityRelationParentsBinding
import com.example.giapha.databinding.DialogRelationAddBinding
import com.example.giapha.databinding.DialogRelationEditBinding
import com.example.giapha.databinding.ItemRelationRowBinding
import com.example.giapha.databinding.ViewPersonPickerBinding
import com.example.giapha.model.LoaiQuanHe
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * GD-12 — Quản lý quan hệ cha/mẹ–con của một Ô (Admin, Quản lý).
 * Hai khối: cha/mẹ của Ô và con của Ô. Thêm / sửa / gỡ xong thì ở lại màn này và tải lại danh sách.
 */
class ParentRelationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityRelationParentsBinding
    private val repo get() = Services.repository
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var idKhongGian = 0
    private var idO = 0
    private var daCoDuLieu = false
    private var hoTenO = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRelationParentsBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)

        b.appBar.setup(this, "Quan hệ cha/mẹ")
        b.scroll.applyBottomInsetPadding()
        b.btnAddParent.setOnClickListener { hopThem(ChieuQuanHe.LA_CON) }
        b.btnAddChild.setOnClickListener { hopThem(ChieuQuanHe.LA_PHU_HUYNH) }
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoDuLieu) b.stateView.showLoading()
        when (val r = repo.quanHeChaMeCua(idO)) {
            is Kq.Loi -> {
                daCoDuLieu = false
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hien(r.giaTri)
        }
    }

    // ------------------------------------------------------------------ hiển thị

    private fun hien(d: QuanHeChaMeCuaO) {
        daCoDuLieu = true
        b.stateView.hide()
        hoTenO = d.o.hoTen

        // Tên dài thì xuống dòng phụ, không cắt tiêu đề
        b.appBar.tvSubtitle.text = hoTenO
        b.appBar.tvSubtitle.visibility = View.VISIBLE

        b.tvHeadParents.text = "Cha/Mẹ của $hoTenO"
        b.tvHeadChildren.text = "Con của $hoTenO"
        veKhoi(b.listParents, b.emptyParents, d.chaMe, ChieuQuanHe.LA_CON)
        veKhoi(b.listChildren, b.emptyChildren, d.con, ChieuQuanHe.LA_PHU_HUYNH)
    }

    /** chieu = LA_CON: danh sách là cha/mẹ của Ô. LA_PHU_HUYNH: danh sách là con của Ô. */
    private fun veKhoi(ds: ViewGroup, trong: View, dong: List<DongQuanHeChaMe>, chieu: ChieuQuanHe) {
        ds.removeAllViews()
        trong.visibility = if (dong.isEmpty()) View.VISIBLE else View.GONE
        dong.forEachIndexed { i, r ->
            val row = ItemRelationRowBinding.inflate(layoutInflater, ds, false)
            row.divider.visibility = if (i == 0) View.GONE else View.VISIBLE
            row.tvName.text = r.hoTen
            row.tvName.setTextColor(getColor(if (r.conSong) R.color.text_primary else R.color.text_hint))
            row.flAvatar.setBackgroundResource(if (r.conSong) R.drawable.bg_circle_green else R.drawable.bg_circle_dead)
            datNhan(row.tvTag, r.loai.nhan)
            row.tvDate.text = r.ngayXacLap?.let { "Ngày xác lập: ${it.format(fmt)}" } ?: ""
            row.tvDate.visibility = if (r.ngayXacLap == null) View.GONE else View.VISIBLE

            val idCon = if (chieu == ChieuQuanHe.LA_CON) idO else r.idNguoi
            val idPhuHuynh = if (chieu == ChieuQuanHe.LA_CON) r.idNguoi else idO
            row.infoBox.setOnClickListener { Routes.moChiTietO(this, idKhongGian, r.idNguoi) }
            row.btnEdit.setOnClickListener { hopSua(r, chieu, idCon, idPhuHuynh) }
            row.btnDelete.setOnClickListener { hopGo(r, idCon, idPhuHuynh) }
            ds.addView(row.root)
        }
    }

    private fun datNhan(tv: TextView, nhan: String) {
        tv.text = nhan
        tv.setTextColor(getColor(R.color.brown_pressed))
        tv.background = GradientDrawable().apply {
            cornerRadius = 100 * resources.displayMetrics.density
            setColor(getColor(R.color.peach))
        }
    }

    // ------------------------------------------------------------------ hộp thêm

    /** "Thêm cha/mẹ" (LA_CON) hoặc "Thêm con" (LA_PHU_HUYNH): tìm người, chọn loại, ngày, Lưu. */
    private fun hopThem(chieu: ChieuQuanHe) {
        val d = DialogRelationAddBinding.inflate(layoutInflater)
        d.tvTitle.text = if (chieu == ChieuQuanHe.LA_CON) "Thêm cha/mẹ" else "Thêm con"

        val picker = PersonPicker(
            b = ViewPersonPickerBinding.inflate(layoutInflater, d.pickerHost, false),
            cheDoCon = true,
            goiY = "Tìm kiếm theo tên...",
            timKiem = { q ->
                when (val r = repo.timUngVienChaMe(idO, chieu, q)) {
                    is Kq.Ok -> r.giaTri
                    is Kq.Loi -> emptyList()
                }
            },
            loaiTru = { null },
            chonNgay = { hienTai, khiChon -> chonNgay(hienTai, khiChon) },
            khiDoi = { d.tvError.visibility = View.GONE }
        )
        d.pickerHost.addView(picker.root)   // phải thêm trước khi tạo hộp để bàn phím hiện được

        val dialog = AlertDialog.Builder(this).setView(d.root).create()
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnConfirm.setOnClickListener {
            val chon = picker.chaMe()
            if (chon == null) { picker.hienLoi(true); return@setOnClickListener }
            when (val r = repo.themQuanHeChaMe(idO, chieu, chon.idO, chon.loai, chon.ngayXacLap)) {
                is Kq.Loi -> { d.tvError.text = r.thongBao; d.tvError.visibility = View.VISIBLE }
                is Kq.Ok -> {
                    dialog.dismiss()
                    tai()
                    Snackbar.make(b.root, "Đã thêm quan hệ", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        hienHop(dialog)
    }

    // ------------------------------------------------------------------ hộp sửa

    /** Người đã cố định; chỉ đổi được loại quan hệ và ngày xác lập. */
    private fun hopSua(r: DongQuanHeChaMe, chieu: ChieuQuanHe, idCon: Int, idPhuHuynh: Int) {
        val d = DialogRelationEditBinding.inflate(layoutInflater)
        val dp = resources.displayMetrics.density
        d.rowPerson.background = GradientDrawable().apply {
            cornerRadius = 12 * dp
            setColor(getColor(R.color.bg_screen))
            setStroke(dp.toInt(), getColor(R.color.border_default))
        }
        d.tvPersonName.text = r.hoTen
        d.tvPersonContext.text =
            if (chieu == ChieuQuanHe.LA_CON) "Cha/mẹ của $hoTenO" else "Con của $hoTenO"

        var loai = r.loai
        var ngay = r.ngayXacLap
        d.segType.setOptions(LoaiQuanHe.entries.map { it.nhan }, loai.ordinal)
        d.segType.onChanged = { i -> if (i >= 0) loai = LoaiQuanHe.entries[i]; d.tvError.visibility = View.GONE }

        fun hienNgay() {
            val co = ngay != null
            d.tvDate.text = ngay?.format(fmt) ?: "Chọn ngày (tùy chọn)"
            d.tvDate.setTextColor(getColor(if (co) R.color.text_primary else R.color.text_hint))
            d.tvClearDate.visibility = if (co) View.VISIBLE else View.GONE
        }
        hienNgay()
        d.fieldDate.setOnClickListener { chonNgay(ngay) { n -> ngay = n; hienNgay(); d.tvError.visibility = View.GONE } }
        d.tvClearDate.setOnClickListener { ngay = null; hienNgay() }

        val dialog = AlertDialog.Builder(this).setView(d.root).create()
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnConfirm.setOnClickListener {
            when (val kq = repo.suaQuanHeChaMe(idCon, idPhuHuynh, loai, ngay)) {
                is Kq.Loi -> { d.tvError.text = kq.thongBao; d.tvError.visibility = View.VISIBLE }
                is Kq.Ok -> {
                    dialog.dismiss()
                    tai()
                    Snackbar.make(b.root, "Đã cập nhật quan hệ", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        hienHop(dialog)
    }

    // ------------------------------------------------------------------ hộp gỡ

    private fun hopGo(r: DongQuanHeChaMe, idCon: Int, idPhuHuynh: Int) {
        showConfirmDialog(
            title = "Gỡ quan hệ với ${r.hoTen}?",
            message = "Thao tác này không xóa Ô ${r.hoTen} khỏi cây, chỉ gỡ liên kết quan hệ.",
            confirmText = "Gỡ",
            showIcon = false
        ) {
            when (val kq = repo.xoaQuanHeChaMe(idCon, idPhuHuynh)) {
                is Kq.Loi -> Snackbar.make(b.root, kq.thongBao, Snackbar.LENGTH_LONG).show()
                is Kq.Ok -> {
                    tai()
                    Snackbar.make(b.root, "Đã gỡ quan hệ", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ------------------------------------------------------------------ tiện ích

    /** Không cho chọn ngày ở tương lai. */
    private fun chonNgay(hienTai: LocalDate?, khiChon: (LocalDate) -> Unit) {
        val m = hienTai ?: LocalDate.now()
        DatePickerDialog(
            this, { _, y, th, ng -> khiChon(LocalDate.of(y, th + 1, ng)) },
            m.year, m.monthValue - 1, m.dayOfMonth
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }

    /** Hộp cách mép 24dp hai bên, co lại khi bàn phím hiện. */
    private fun hienHop(dialog: AlertDialog) {
        dialog.show()
        val le = (24 * resources.displayMetrics.density).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), le, 0, le, 0))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}
