package com.example.giapha.ui.person

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.R
import com.example.giapha.data.FormO
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityPersonFormBinding
import com.example.giapha.model.GioiTinh
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.setTextAction
import com.example.giapha.ui.common.setup
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.graphics.Color
import android.widget.LinearLayout
import com.example.giapha.data.QuanHeMoi
import com.example.giapha.databinding.ViewPersonPickerBinding

/**
 * GD-07 — Thêm/Sửa Ô thành viên. Chỉ Admin, Quản lý.
 * Không truyền EXTRA_ID_O: chế độ Thêm. Có EXTRA_ID_O: chế độ Sửa (không có khối quan hệ).
 */
class PersonFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityPersonFormBinding
    private lateinit var btnSave: TextView
    private val repo get() = Services.repository
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var idKhongGian = 0
    private var idO = 0
    private val cheDoSua get() = idO != 0

    private var gioiTinh: GioiTinh? = null
    private var ngaySinh: LocalDate? = null
    private var ngayMat: LocalDate? = null
    private var cheDoQuanHe = 0      // 0 chưa chọn, 1 là con, 2 là vợ/chồng, 3 độc lập
    private var picker1: PersonPicker? = null
    private var picker2: PersonPicker? = null      // cha/mẹ thứ hai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)
        if (!cheDoSua && repo.vaiTroCuaToi(idKhongGian)?.vaiTro?.laQuanTri != true) {
            finish(); return
        }

        b = ActivityPersonFormBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.appBar.setup(this, if (cheDoSua) "Sửa thông tin" else "Thêm thành viên")
        btnSave = b.appBar.setTextAction(this, "Lưu") { luu() }
        b.scroll.applyBottomInsetPadding()

        // Giới tính: không bắt buộc, bấm lại để bỏ chọn
        b.segGender.allowDeselect = true
        b.segGender.setOptions(GioiTinh.entries.map { it.nhan })
        b.segGender.onChanged = { i -> gioiTinh = GioiTinh.entries.getOrNull(i) }

        // Công tắc "Còn sống": màu xanh vừa khi bật
        b.swAlive.trackTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(getColor(R.color.green_mid), getColor(R.color.border_default))
        )
        b.swAlive.setOnCheckedChangeListener { _, conSong ->
            b.groupDeath.visibility = if (conSong) View.GONE else View.VISIBLE
            if (conSong) {
                ngayMat = null
                hienNgay(b.tvDeath, null, "Chọn ngày mất")
                hienLoiNgayMat(false)
            }
        }

        // Họ tên: Lưu chỉ sáng khi đã có tên; để trống rồi rời ô thì báo lỗi
        b.etName.doAfterTextChanged {
            capNhatNutLuu()
            if (!it.isNullOrBlank()) hienLoiTen(false)
        }
        b.etName.setOnFocusChangeListener { _, coFocus ->
            if (!coFocus) hienLoiTen(b.etName.text.isBlank())
        }

        b.fieldBirth.setOnClickListener {
            chonNgay(ngaySinh, null) { d ->
                ngaySinh = d
                hienNgay(b.tvBirth, d, "Chọn ngày sinh")
            }
        }
        b.fieldDeath.setOnClickListener {
            chonNgay(ngayMat, ngaySinh) { d ->
                ngayMat = d
                hienNgay(b.tvDeath, d, "Chọn ngày mất")
                hienLoiNgayMat(false)
            }
        }

        if (cheDoSua) {
            nap()
            b.cardRelation.visibility = View.GONE
        } else {
            b.btnRelCon.setOnClickListener { chonQuanHe(1) }
            b.btnRelSpouse.setOnClickListener { chonQuanHe(2) }
            b.btnRelNone.setOnClickListener { chonQuanHe(3) }
            b.btnAddSecond.setOnClickListener { themChaMeThuHai() }
            chonQuanHe(0)
        }
        capNhatNutLuu()
    }

    /** Chế độ Sửa: đổ dữ liệu có sẵn vào form. */
    private fun nap() {
        when (val r = repo.chiTietO(idO)) {
            is Kq.Loi -> {
                Toast.makeText(this, r.thongBao, Toast.LENGTH_LONG).show()
                finish()
            }
            is Kq.Ok -> {
                if (!r.giaTri.toi.vaiTro.laQuanTri) { finish(); return }
                val o = r.giaTri.o
                idKhongGian = o.idKhongGian
                b.etName.setText(o.hoTen)
                b.etNickname.setText(o.bietDanh ?: "")
                gioiTinh = o.gioiTinh
                b.segGender.chon(o.gioiTinh?.let { GioiTinh.entries.indexOf(it) } ?: -1)
                ngaySinh = o.ngaySinh
                hienNgay(b.tvBirth, ngaySinh, "Chọn ngày sinh")
                b.etHometown.setText(o.queQuan ?: "")
                b.etCccd.setText(o.cccd ?: "")
                b.swAlive.isChecked = o.conSong
                ngayMat = o.ngayMat
                hienNgay(b.tvDeath, ngayMat, "Chọn ngày mất")
            }
        }
    }

    private fun luu() {
        val ten = b.etName.text.toString().trim()
        if (ten.isEmpty()) { hienLoiTen(true); return }
        val conSong = b.swAlive.isChecked
        if (!conSong && ngayMat == null) { hienLoiNgayMat(true); return }

        // Quan hệ: chọn "con" hoặc "vợ/chồng" thì phải chọn được một người
        val quanHe: QuanHeMoi? = when (cheDoQuanHe) {
            1, 2 -> {
                val p = picker1 ?: return
                if (!p.daChon) {
                    p.hienLoi(true)
                    b.scroll.post { b.scroll.smoothScrollTo(0, b.cardRelation.top) }
                    return
                }
                if (cheDoQuanHe == 1) QuanHeMoi.LaCon(listOfNotNull(p.chaMe(), picker2?.chaMe()))
                else p.voChong()
            }
            else -> null
        }

        val f = FormO(
            hoTen = ten,
            bietDanh = b.etNickname.text.toString().trim().ifEmpty { null },
            gioiTinh = gioiTinh,
            ngaySinh = ngaySinh,
            queQuan = b.etHometown.text.toString().trim().ifEmpty { null },
            cccd = b.etCccd.text.toString().trim().ifEmpty { null },
            conSong = conSong,
            ngayMat = if (conSong) null else ngayMat
        )
        when (val r = if (cheDoSua) repo.suaO(idO, f) else repo.themO(idKhongGian, f, quanHe)) {
            is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
            is Kq.Ok -> {
                Toast.makeText(this, if (cheDoSua) "Đã lưu thay đổi" else "Đã thêm thành viên", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // ------------------------------------------------------------------ phụ trợ

    private fun capNhatNutLuu() {
        val ok = b.etName.text.isNotBlank()
        btnSave.isEnabled = ok
        btnSave.alpha = if (ok) 1f else 0.5f
    }

    private fun hienLoiTen(co: Boolean) {
        b.etName.isActivated = co
        b.tvNameError.visibility = if (co) View.VISIBLE else View.GONE
    }

    private fun hienLoiNgayMat(co: Boolean) {
        b.fieldDeath.isActivated = co
        b.tvDeathError.visibility = if (co) View.VISIBLE else View.GONE
    }

    private fun hienNgay(tv: TextView, d: LocalDate?, goiY: String) {
        if (d == null) {
            tv.text = goiY
            tv.setTextColor(getColor(R.color.text_hint))
        } else {
            tv.text = d.format(fmt)
            tv.setTextColor(getColor(R.color.text_primary))
        }
    }

    /** Không cho chọn ngày ở tương lai; toiThieu (ngày sinh) chặn ngày mất sớm hơn ngày sinh. */
    private fun chonNgay(hienTai: LocalDate?, toiThieu: LocalDate?, khiChon: (LocalDate) -> Unit) {
        val m = hienTai ?: LocalDate.now()
        DatePickerDialog(this, { _, y, th, ng -> khiChon(LocalDate.of(y, th + 1, ng)) },
            m.year, m.monthValue - 1, m.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            toiThieu?.let {
                datePicker.minDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }.show()
    }

    /** Chọn một trong ba nút quan hệ; đổi nút thì khối tìm người làm lại từ đầu. */
    private fun chonQuanHe(che: Int) {
        cheDoQuanHe = che
        picker1 = null
        picker2 = null
        b.pickerHost.removeAllViews()

        listOf(b.btnRelCon to 1, b.btnRelSpouse to 2, b.btnRelNone to 3).forEach { (btn, i) ->
            val bat = i == che
            btn.backgroundTintList = ColorStateList.valueOf(
                if (bat) getColor(R.color.brown_primary) else Color.TRANSPARENT
            )
            btn.setTextColor(getColor(if (bat) R.color.white else R.color.brown_primary))
        }

        if (che == 1 || che == 2) {
            val p = taoPicker(cheDoCon = che == 1, goiY = "Tìm theo tên...") { picker2?.idDaChon }
            picker1 = p
            b.pickerHost.addView(p.root)
        }
        capNhatNutThem()
    }

    private fun themChaMeThuHai() {
        val p = taoPicker(cheDoCon = true, goiY = "Tìm cha/mẹ thứ hai...") { picker1?.idDaChon }
        (p.root.layoutParams as LinearLayout.LayoutParams).topMargin =
            (16 * resources.displayMetrics.density).toInt()
        picker2 = p
        b.pickerHost.addView(p.root)
        capNhatNutThem()
    }

    /** Nút "+ Thêm cha/mẹ thứ hai": chỉ khi là con, đã chọn người thứ nhất, chưa có khối thứ hai. */
    private fun capNhatNutThem() {
        b.btnAddSecond.visibility =
            if (cheDoQuanHe == 1 && picker1?.daChon == true && picker2 == null) View.VISIBLE else View.GONE
    }

    private fun taoPicker(cheDoCon: Boolean, goiY: String, loaiTru: () -> Int?): PersonPicker {
        val pb = ViewPersonPickerBinding.inflate(layoutInflater, b.pickerHost, false)
        return PersonPicker(
            b = pb,
            cheDoCon = cheDoCon,
            goiY = goiY,
            timKiem = { q ->
                when (val r = repo.timO(idKhongGian, q)) {
                    is Kq.Ok -> r.giaTri
                    is Kq.Loi -> emptyList()
                }
            },
            loaiTru = loaiTru,
            chonNgay = { hienTai, khiChon -> chonNgay(hienTai, null, khiChon) },
            khiDoi = { capNhatNutThem() }
        )
    }
}