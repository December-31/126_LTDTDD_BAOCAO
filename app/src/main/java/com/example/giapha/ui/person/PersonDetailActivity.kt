package com.example.giapha.ui.person

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.giapha.R
import com.example.giapha.data.ChiTietO
import com.example.giapha.data.Kq
import com.example.giapha.data.NguoiLienQuan
import com.example.giapha.data.Services
import com.example.giapha.data.TacDongXoa
import com.example.giapha.databinding.ActivityPersonDetailBinding
import com.example.giapha.databinding.DialogDeathBinding
import com.example.giapha.databinding.ItemInfoRowBinding
import com.example.giapha.databinding.ItemRelationChipBinding
import com.example.giapha.databinding.ItemRelationGroupBinding
import com.example.giapha.model.OThanhVien
import com.example.giapha.model.VaiTro
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomInsetPadding
import com.example.giapha.ui.common.initialOf
import com.example.giapha.ui.common.setAction
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * GD-08 — Chi tiết Ô thành viên.
 * Mọi vai trò xem được; nút hành động và dòng CCCD hiện theo quyền.
 * Tải lại mỗi lần hiện lại để thấy thay đổi từ màn con.
 */
class PersonDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
        const val EXTRA_ID_O = "idO"
    }

    private lateinit var b: ActivityPersonDetailBinding
    private val repo get() = Services.repository
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var idKhongGian = 0
    private var idO = 0
    private var daCoDuLieu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPersonDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        idO = intent.getIntExtra(EXTRA_ID_O, 0)

        b.appBar.setup(this, "")
        b.scroll.applyBottomInsetPadding()

        b.rowBio.ivNavIcon.setImageResource(R.drawable.ic_book)
        b.rowBio.tvNavTitle.text = "Tiểu sử & bình luận"
        b.rowBio.root.setOnClickListener { Routes.moTieuSu(this, idKhongGian, idO) }
        b.rowAchievements.ivNavIcon.setImageResource(R.drawable.ic_trophy)
        b.rowAchievements.tvNavTitle.text = "Thành tựu"
        b.rowAchievements.root.setOnClickListener { Routes.moThanhTuu(this, idKhongGian, idO) }

        b.btnEditAvatar.setOnClickListener { Routes.moAnhDaiDien(this, idKhongGian, idO) }
        b.btnParents.setOnClickListener { Routes.moQuanHeChaMe(this, idKhongGian, idO) }
        b.btnMarriage.setOnClickListener { Routes.moQuanHeHonNhan(this, idKhongGian, idO) }
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoDuLieu) b.stateView.showLoading()
        when (val r = repo.chiTietO(idO)) {
            is Kq.Loi -> {
                daCoDuLieu = false
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hien(r.giaTri)
        }
    }

    // ------------------------------------------------------------------ hiển thị

    private fun hien(ct: ChiTietO) {
        daCoDuLieu = true
        b.stateView.hide()
        val o = ct.o
        val quanTri = ct.toi.vaiTro.laQuanTri
        val admin = ct.toi.vaiTro == VaiTro.ADMIN

        b.appBar.tvTitle.text = o.hoTen
        if (quanTri) {
            b.appBar.setAction(this, R.drawable.ic_more_vert, "Menu") { moMenu(b.appBar.actionContainer, o) }
        } else {
            b.appBar.actionContainer.removeAllViews()
        }

        // ảnh đại diện (bản hardcode: chữ cái đầu)
        b.tvAvatar.text = initialOf(o.hoTen)
        b.tvAvatar.setBackgroundResource(if (o.conSong) R.drawable.bg_circle_green else R.drawable.bg_circle_dead)
        b.tvAvatar.setTextColor(ContextCompat.getColor(this, if (o.conSong) R.color.brown_primary else R.color.text_hint))
        b.ivCrown.visibility = if (o.laTruongToc) View.VISIBLE else View.GONE
        b.btnEditAvatar.visibility = if (quanTri) View.VISIBLE else View.GONE

        // thông tin cơ bản
        b.infoRows.removeAllViews()
        themDong("Họ tên", o.hoTen)
        themDong("Biệt danh", o.bietDanh ?: "—")
        themDong("Giới tính", o.gioiTinh?.nhan ?: "Không rõ")
        themDong("Ngày sinh", o.ngaySinh?.format(fmt) ?: "Không rõ")
        themDong("Quê quán", o.queQuan ?: "Không rõ")
        if (o.conSong) themDong("Trạng thái", "● Còn sống", R.color.green_dark)
        else themDong("Trạng thái", "Đã mất — " + (o.ngayMat?.format(fmt) ?: "không rõ ngày"))
        // CCCD: chỉ Admin/Quản lý; vai trò khác bỏ hẳn dòng này khỏi layout
        if (quanTri) themDong("CCCD", o.cccd?.chunked(3)?.joinToString(" ") ?: "Chưa có")

        // quan hệ gia đình
        b.relationGroups.removeAllViews()
        themNhom("Cha/Mẹ", ct.chaMe, honNhan = false)
        themNhom("Vợ/Chồng", ct.voChong, honNhan = true)
        themNhom("Con cái", ct.con, honNhan = false)

        // nút theo quyền
        b.btnParents.visibility = if (quanTri) View.VISIBLE else View.GONE
        b.btnMarriage.visibility = if (quanTri) View.VISIBLE else View.GONE
        b.adminBlock.visibility = if (quanTri) View.VISIBLE else View.GONE
        b.btnChief.visibility = if (admin) View.VISIBLE else View.GONE

        b.btnDeath.text = if (o.conSong) "Xác nhận tử vong" else "Hủy xác nhận tử vong"
        b.btnDeath.setOnClickListener { if (o.conSong) hopNgayMat(o) else hoiHuyTuVong(o) }
        b.btnChief.text = if (o.laTruongToc) "Thu hồi danh hiệu Trưởng tộc" else "Gán danh hiệu Trưởng tộc"
        b.btnChief.setOnClickListener { hoiTruongToc(o) }
        b.btnDelete.setOnClickListener { hoiXoa(o) }
    }

    private fun themDong(nhan: String, giaTri: String, mauChu: Int? = null) {
        val row = ItemInfoRowBinding.inflate(layoutInflater, b.infoRows, false)
        row.tvLabel.text = nhan
        row.tvValue.text = giaTri
        if (mauChu != null) row.tvValue.setTextColor(ContextCompat.getColor(this, mauChu))
        b.infoRows.addView(row.root)
    }

    private fun themNhom(ten: String, ds: List<NguoiLienQuan>, honNhan: Boolean) {
        val g = ItemRelationGroupBinding.inflate(layoutInflater, b.relationGroups, false)
        g.tvGroup.text = ten
        if (ds.isEmpty()) {
            g.listPeople.addView(TextView(this).apply {
                text = "Chưa có thông tin"
                setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                textSize = 14f
                setPadding(0, (10 * resources.displayMetrics.density).toInt(), 0, (10 * resources.displayMetrics.density).toInt())
            })
        }
        ds.forEach { n ->
            val c = ItemRelationChipBinding.inflate(layoutInflater, g.listPeople, false)
            c.tvName.text = n.hoTen
            c.chipRoot.alpha = if (n.conSong) 1f else 0.75f
            datNhan(c.tvTag, n.nhan, honNhan)
            c.chipRoot.setOnClickListener { Routes.moChiTietO(this, idKhongGian, n.idO) }
            g.listPeople.addView(c.root)
        }
        b.relationGroups.addView(g.root)
    }

    /** Nhãn nhỏ: loại quan hệ cha/mẹ (đào) hoặc trạng thái hôn nhân ("Đang kết hôn" xanh). */
    private fun datNhan(tv: TextView, nhan: String?, honNhan: Boolean) {
        if (nhan == null) { tv.visibility = View.GONE; return }
        val xanh = honNhan && nhan == "Đang kết hôn"
        val nen = if (xanh) R.color.green_light else R.color.peach
        val chu = when {
            xanh -> R.color.green_dark
            honNhan -> R.color.text_secondary
            else -> R.color.brown_pressed
        }
        tv.text = nhan
        tv.visibility = View.VISIBLE
        tv.setTextColor(ContextCompat.getColor(this, chu))
        tv.background = GradientDrawable().apply {
            cornerRadius = 100 * resources.displayMetrics.density
            setColor(ContextCompat.getColor(this@PersonDetailActivity, nen))
        }
    }

    // ------------------------------------------------------------------ menu và thao tác

    private fun moMenu(neo: View, o: OThanhVien) {
        PopupMenu(this, neo).apply {
            menu.add(0, 0, 0, "Sửa thông tin")
            menu.add(0, 1, 1, "Xóa Ô")
            setOnMenuItemClickListener {
                if (it.itemId == 0) Routes.moSuaO(this@PersonDetailActivity, idKhongGian, o.idOThanhVien)
                else hoiXoa(o)
                true
            }
            show()
        }
    }

    private fun hoiHuyTuVong(o: OThanhVien) {
        showConfirmDialog(
            "Hủy xác nhận tử vong?",
            "${o.hoTen} sẽ được đánh dấu là còn sống và ngày mất sẽ bị xóa.",
            "Đồng ý", destructive = false
        ) { thucHien(repo.datTuVong(o.idOThanhVien, conSong = true, ngayMat = null), "Đã hủy xác nhận tử vong") }
    }

    private fun hoiTruongToc(o: OThanhVien) {
        val gan = !o.laTruongToc
        showConfirmDialog(
            if (gan) "Gán danh hiệu Trưởng tộc?" else "Thu hồi danh hiệu Trưởng tộc?",
            if (gan) "${o.hoTen} sẽ được đánh dấu Trưởng tộc trên cây."
            else "${o.hoTen} sẽ không còn là Trưởng tộc.",
            if (gan) "Gán" else "Thu hồi", destructive = false
        ) { thucHien(repo.datTruongToc(o.idOThanhVien, gan), if (gan) "Đã gán danh hiệu" else "Đã thu hồi danh hiệu") }
    }

    private fun thucHien(r: Kq<OThanhVien>, thanhCong: String) {
        when (r) {
            is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
            is Kq.Ok -> { tai(); Snackbar.make(b.root, thanhCong, Snackbar.LENGTH_SHORT).show() }
        }
    }

    // ------------------------------------------------------------------ xóa Ô

    /** Tính tác động trước, rồi hỏi xác nhận bằng hộp cảnh báo đỏ. */
    private fun hoiXoa(o: OThanhVien) {
        when (val r = repo.xemTruocXoaO(o.idOThanhVien)) {
            is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
            is Kq.Ok -> showConfirmDialog("Xóa ${o.hoTen}?", noiDungXoa(r.giaTri), "Xóa") { xoa(o) }
        }
    }

    private fun xoa(o: OThanhVien) {
        when (val r = repo.xoaO(o.idOThanhVien)) {
            is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
            is Kq.Ok -> Routes.veCay(this, idKhongGian)   // xóa xong quay về GD-06, cây tự tải lại
        }
    }

    private fun noiDungXoa(t: TacDongXoa): String {
        val co = listOfNotNull(
            t.soChaMe.takeIf { it > 0 }?.let { "$it cha/mẹ" },
            t.soVoChong.takeIf { it > 0 }?.let { "$it vợ/chồng" },
            t.soCon.takeIf { it > 0 }?.let { "$it con" },
            t.soThanhTuu.takeIf { it > 0 }?.let { "$it thành tựu" },
            t.soBinhLuan.takeIf { it > 0 }?.let { "$it bình luận" },
            t.soTieuSu.takeIf { it > 0 }?.let { "$it bài tiểu sử" }
        )
        val chinh =
            if (co.isEmpty()) "Hành động này không thể hoàn tác."
            else "Ô này đang có ${noiDanhSach(co)}. Xóa sẽ gỡ toàn bộ các quan hệ và dữ liệu liên quan này — hành động không thể hoàn tác."
        if (t.tachKhoiCayChinh.isEmpty()) return chinh
        val ten = t.tachKhoiCayChinh
        val hienThi = if (ten.size <= 4) noiDanhSach(ten)
        else ten.take(3).joinToString(", ") + " và ${ten.size - 3} người khác"
        return "$chinh\n\nSau khi xóa, ${ten.size} người sẽ tách khỏi cây chính: $hienThi."
    }

    /** ["A", "B", "C"] -> "A, B và C". */
    private fun noiDanhSach(ds: List<String>): String =
        if (ds.size <= 1) ds.joinToString("") else ds.dropLast(1).joinToString(", ") + " và " + ds.last()

    /** Hộp "Xác nhận tử vong": chọn ngày mất; nút "Xác nhận" chỉ bật sau khi đã chọn. */
    private fun hopNgayMat(o: OThanhVien) {
        val d = DialogDeathBinding.inflate(LayoutInflater.from(this))
        var chon: LocalDate? = null
        val dialog = AlertDialog.Builder(this).setView(d.root).create()

        d.fieldDate.setOnClickListener {
            val m = chon ?: LocalDate.now()
            DatePickerDialog(this, { _, y, th, ng ->
                chon = LocalDate.of(y, th + 1, ng)
                d.tvDate.text = chon!!.format(fmt)
                d.tvDate.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                d.tvError.visibility = View.GONE
                d.btnConfirm.isEnabled = true
            }, m.year, m.monthValue - 1, m.dayOfMonth).apply {
                datePicker.maxDate = System.currentTimeMillis()
                o.ngaySinh?.let {
                    datePicker.minDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }.show()
        }
        d.btnCancel.setOnClickListener { dialog.dismiss() }
        d.btnConfirm.setOnClickListener {
            when (val r = repo.datTuVong(o.idOThanhVien, conSong = false, ngayMat = chon)) {
                is Kq.Loi -> { d.tvError.text = r.thongBao; d.tvError.visibility = View.VISIBLE }
                is Kq.Ok -> { dialog.dismiss(); tai(); Snackbar.make(b.root, "Đã xác nhận tử vong", Snackbar.LENGTH_SHORT).show() }
            }
        }
        dialog.show()
        val le = (24 * resources.displayMetrics.density).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), le, 0, le, 0))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
