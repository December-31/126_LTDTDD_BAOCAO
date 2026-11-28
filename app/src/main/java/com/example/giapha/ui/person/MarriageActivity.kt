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
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.giapha.R
import com.example.giapha.data.DongHonNhan
import com.example.giapha.data.HonNhanCuaO
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityMarriageBinding
import com.example.giapha.databinding.DialogMarriageEditBinding
import com.example.giapha.databinding.DialogRelationAddBinding
import com.example.giapha.databinding.ItemMarriageCardBinding
import com.example.giapha.databinding.ViewPersonPickerBinding
import com.example.giapha.model.TrangThaiHonNhan
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * GD-13 — Quản lý quan hệ hôn nhân của một Ô (Admin, Quản lý).
 * Danh sách theo thứ tự hôn nhân; thêm / sửa (trạng thái, ngày cưới, ngày ly hôn, thứ tự) / xóa.
 * Đổi trạng thái hay xóa hôn nhân không đụng tới quan hệ cha/mẹ–con.
 */
class MarriageActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityMarriageBinding
    private val repo get() = Services.repository
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var idKhongGian = 0
    private var idO = 0
    private var daCoDuLieu = false
    private var soHonNhanCuaO = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMarriageBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)

        b.appBar.setup(this, "Quan hệ hôn nhân")
        b.scroll.applyBottomInsetPadding()
        b.btnAdd.setOnClickListener { hopThem() }
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoDuLieu) b.stateView.showLoading()
        when (val r = repo.honNhanCua(idO)) {
            is Kq.Loi -> {
                daCoDuLieu = false
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hien(r.giaTri)
        }
    }

    // ------------------------------------------------------------------ hiển thị

    private fun hien(d: HonNhanCuaO) {
        daCoDuLieu = true
        b.stateView.hide()
        soHonNhanCuaO = d.ds.size
        b.appBar.tvSubtitle.text = d.o.hoTen
        b.appBar.tvSubtitle.visibility = View.VISIBLE

        b.listMarriages.removeAllViews()
        b.emptyBox.visibility = if (d.ds.isEmpty()) View.VISIBLE else View.GONE
        val nhieu = d.ds.size >= 2   // nhãn "Lần n" chỉ hiện khi có từ 2 cuộc hôn nhân
        d.ds.forEachIndexed { i, h ->
            val c = ItemMarriageCardBinding.inflate(layoutInflater, b.listMarriages, false)
            c.tvName.text = h.hoTen
            c.tvName.setTextColor(getColor(if (h.conSong) R.color.text_primary else R.color.text_hint))
            c.flAvatar.setBackgroundResource(if (h.conSong) R.drawable.bg_circle_green else R.drawable.bg_circle_dead)
            datTrangThai(c.tvStatus, h.trangThai)

            c.rowWedding.visibility = if (h.ngayCoi != null) View.VISIBLE else View.GONE
            c.tvWedding.text = "Ngày cưới: ${h.ngayCoi?.format(fmt)}"
            val coLy = h.trangThai == TrangThaiHonNhan.DA_LY_HON && h.ngayLyHon != null
            c.rowDivorce.visibility = if (coLy) View.VISIBLE else View.GONE
            c.tvDivorce.text = "Ngày ly hôn: ${h.ngayLyHon?.format(fmt)}"

            c.tvOrder.visibility = if (nhieu) View.VISIBLE else View.GONE
            c.tvOrder.text = "Lần ${h.thuTu ?: (i + 1)}"
            c.tvOrder.background = GradientDrawable().apply {
                cornerRadius = 100 * resources.displayMetrics.density
                setStroke(resources.displayMetrics.density.toInt(), getColor(R.color.brown_primary))
            }

            c.infoBox.setOnClickListener { Routes.moChiTietO(this, idKhongGian, h.idNguoi) }
            c.btnEdit.setOnClickListener { hopSua(h) }
            c.btnDelete.setOnClickListener { hopXoa(h) }
            b.listMarriages.addView(c.root)
        }
    }

    /** Đang kết hôn: xanh nhạt; Đã ly hôn: nâu xám nhạt; Góa: xám. Không dùng đỏ. */
    private fun datTrangThai(tv: TextView, t: TrangThaiHonNhan) {
        val (nen, chu) = when (t) {
            TrangThaiHonNhan.DANG_KET_HON -> R.color.green_light to R.color.green_dark
            TrangThaiHonNhan.DA_LY_HON -> R.color.peach to R.color.brown_pressed
            TrangThaiHonNhan.GOA -> R.color.border_default to R.color.text_secondary
        }
        tv.text = t.nhan
        tv.setTextColor(getColor(chu))
        tv.background = GradientDrawable().apply {
            cornerRadius = 100 * resources.displayMetrics.density
            setColor(getColor(nen))
        }
    }

    // ------------------------------------------------------------------ hộp thêm

    private fun hopThem() {
        val d = DialogRelationAddBinding.inflate(layoutInflater)
        d.tvTitle.text = "Thêm hôn nhân"

        val picker = PersonPicker(
            b = ViewPersonPickerBinding.inflate(layoutInflater, d.pickerHost, false),
            cheDoCon = false,                       // chế độ vợ/chồng: Ngày cưới + Thứ tự hôn nhân
            goiY = "Tìm kiếm theo tên...",
            timKiem = { q ->
                when (val r = repo.timUngVienHonNhan(idO, q)) {
                    // Gợi ý thứ tự = số hôn nhân nhiều nhất của hai người + 1
                    is Kq.Ok -> r.giaTri.map { it.copy(soHonNhan = maxOf(it.soHonNhan, soHonNhanCuaO)) }
                    is Kq.Loi -> emptyList()
                }
            },
            loaiTru = { null },
            chonNgay = { hienTai, khiChon -> chonNgay(hienTai, null, khiChon) },
            khiDoi = { d.tvError.visibility = View.GONE }
        )
        d.pickerHost.addView(picker.root)   // thêm trước khi tạo hộp để bàn phím hiện được

        val dialog = AlertDialog.Builder(this).setView(d.root).create()
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnConfirm.setOnClickListener {
            val chon = picker.voChong()
            if (chon == null) { picker.hienLoi(true); return@setOnClickListener }
            when (val r = repo.themHonNhan(idO, chon.idNguoiKia, chon.ngayCuoi, chon.thuTu)) {
                is Kq.Loi -> { d.tvError.text = r.thongBao; d.tvError.visibility = View.VISIBLE }
                is Kq.Ok -> {
                    dialog.dismiss()
                    tai()
                    Snackbar.make(b.root, "Đã thêm hôn nhân", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        hienHop(dialog)
    }

    // ------------------------------------------------------------------ hộp sửa

    private fun hopSua(h: DongHonNhan) {
        val d = DialogMarriageEditBinding.inflate(layoutInflater)
        val dp = resources.displayMetrics.density
        d.rowPerson.background = GradientDrawable().apply {   // hộp xám: không đổi được
            cornerRadius = 12 * dp
            setColor(getColor(R.color.peach_light))
            setStroke(dp.toInt(), getColor(R.color.border_default))
        }
        d.tvPersonName.text = h.hoTen

        var ngayCoi = h.ngayCoi
        var ngayLy = h.ngayLyHon
        var trangThai = h.trangThai
        d.etOrder.setText((h.thuTu ?: 1).toString())

        fun hienNgay(tv: TextView, n: LocalDate?, goiY: String) {
            tv.text = n?.format(fmt) ?: goiY
            tv.setTextColor(getColor(if (n != null) R.color.text_primary else R.color.text_hint))
        }
        fun capNhatTrangThai() {
            d.tvStatus.text = trangThai.nhan
            d.groupDivorce.visibility = if (trangThai == TrangThaiHonNhan.DA_LY_HON) View.VISIBLE else View.GONE
            d.noteBox.visibility = if (trangThai == TrangThaiHonNhan.DANG_KET_HON) View.GONE else View.VISIBLE
        }
        hienNgay(d.tvWedding, ngayCoi, "Chọn ngày (tùy chọn)")
        hienNgay(d.tvDivorce, ngayLy, "Chọn ngày ly hôn")
        capNhatTrangThai()

        d.fieldWedding.setOnClickListener {
            chonNgay(ngayCoi, null) { n -> ngayCoi = n; hienNgay(d.tvWedding, n, ""); d.tvError.visibility = View.GONE }
        }
        d.fieldDivorce.setOnClickListener {
            chonNgay(ngayLy, ngayCoi) { n -> ngayLy = n; hienNgay(d.tvDivorce, n, ""); d.tvError.visibility = View.GONE }
        }
        d.fieldStatus.setOnClickListener {
            PopupMenu(this, d.fieldStatus).apply {
                TrangThaiHonNhan.entries.forEachIndexed { i, t -> menu.add(0, i, i, t.nhan) }
                setOnMenuItemClickListener {
                    trangThai = TrangThaiHonNhan.entries[it.itemId]
                    capNhatTrangThai()
                    d.tvError.visibility = View.GONE
                    true
                }
                show()
            }
        }

        val dialog = AlertDialog.Builder(this).setView(d.root).create()
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnConfirm.setOnClickListener {
            val thuTu = d.etOrder.text.toString().toIntOrNull() ?: 0
            when (val r = repo.suaHonNhan(h.id, ngayCoi, trangThai, ngayLy, thuTu)) {
                is Kq.Loi -> { d.tvError.text = r.thongBao; d.tvError.visibility = View.VISIBLE }
                is Kq.Ok -> {
                    dialog.dismiss()
                    tai()
                    Snackbar.make(b.root, "Đã cập nhật hôn nhân", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        hienHop(dialog)
    }

    // ------------------------------------------------------------------ hộp xóa

    private fun hopXoa(h: DongHonNhan) {
        showConfirmDialog(
            title = "Xóa quan hệ hôn nhân với ${h.hoTen}?",
            message = "",
            confirmText = "Xóa",
            showIcon = false
        ) {
            when (val r = repo.xoaHonNhan(h.id)) {
                is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
                is Kq.Ok -> {
                    tai()
                    Snackbar.make(b.root, "Đã xóa hôn nhân", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ------------------------------------------------------------------ tiện ích

    /** Không cho chọn ngày ở tương lai; toiThieu chặn ngày sớm hơn mốc (ngày ly hôn không trước ngày cưới). */
    private fun chonNgay(hienTai: LocalDate?, toiThieu: LocalDate?, khiChon: (LocalDate) -> Unit) {
        val m = hienTai ?: LocalDate.now()
        DatePickerDialog(
            this, { _, y, th, ng -> khiChon(LocalDate.of(y, th + 1, ng)) },
            m.year, m.monthValue - 1, m.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            toiThieu?.let {
                datePicker.minDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }.show()
    }

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
