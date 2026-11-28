package com.example.giapha.ui.tree

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.giapha.R
import com.example.giapha.data.DuLieuCay
import com.example.giapha.data.DuongAn
import com.example.giapha.data.danhSachDuongAn
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.example.giapha.databinding.ActivityTreeBinding
import com.example.giapha.databinding.ItemQuanHeAnBinding
import com.example.giapha.databinding.SheetQuanHeAnBinding
import com.example.giapha.model.VaiTro
import com.example.giapha.tree.LayoutEngine
import com.example.giapha.tree.NutQuanHe
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomMarginInset
import com.example.giapha.ui.common.setAction
import com.example.giapha.ui.common.setup
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.giapha.databinding.DialogLocDoiBinding
import com.example.giapha.tree.BoLocDoi
import com.example.giapha.tree.KetQuaBoCuc

class TreeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
    }

    private lateinit var b: ActivityTreeBinding
    private val repo get() = Services.repository
    private var idKhongGian = 0
    private var vaiTro: VaiTro? = null
    private var daCoCay = false
    private var duHienTai: DuLieuCay? = null
    private lateinit var kho: LocDoiStore
    private var locN: Int? = null               // số đời đang lọc (null = không lọc)
    private var lyDoKhongLoc: String? = null    // null = người xem lọc được

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTreeBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)

        b.appBar.setup(this, "")
        b.fab.applyBottomMarginInset()
        b.fab.setOnClickListener { Routes.moThemO(this, idKhongGian) }
        b.viewport.khiChamThe = { idO -> Routes.moChiTietO(this, idKhongGian, idO) }
        b.viewport.khiBamAn = { nut -> anQuanHe(nut) }
        kho = LocDoiStore(this)
        b.tvFilterClear.setOnClickListener { datLoc(null) }
    }

    override fun onResume() {
        super.onResume()
        tai()
    }

    private fun tai() {
        if (!daCoCay) b.stateView.showLoading()
        when (val r = repo.duLieuCay(idKhongGian)) {
            is Kq.Loi -> {
                daCoCay = false
                b.fab.visibility = View.GONE
                b.stateView.showError(r.thongBao) { tai() }
            }
            is Kq.Ok -> hienCay(r.giaTri)
        }
    }

    private fun hienCay(du: DuLieuCay) {
        vaiTro = du.toi.vaiTro
        duHienTai = du
        b.viewport.choPhepAn = du.toi.vaiTro.laQuanTri
        b.appBar.tvTitle.text = du.khongGian.tenKhongGian
        datNutBenPhai(du.khongGian.cheDoRiengTu)
        b.fab.visibility = if (du.toi.vaiTro.laQuanTri) View.VISIBLE else View.GONE

        if (du.oThanhVien.isEmpty()) {
            b.stateView.showEmpty(
                if (du.toi.vaiTro.laQuanTri) "Chưa có thành viên nào. Bấm \"Thêm thành viên\" để bắt đầu."
                else "Chưa có thành viên nào trong cây."
            )
        } else {
            b.stateView.hide()
        }

        // Lọc số đời: chỉ áp dụng khi Ô của người xem đang nằm trên cây
        val kqDay = LayoutEngine.tinh(du)
        val moc = du.toi.idOThanhVien
        lyDoKhongLoc = tinhLyDoKhongLoc(du, moc, kqDay)
        val n = if (moc != null && lyDoKhongLoc == null) kho.doc(du.toi.idTaiKhoan, idKhongGian) else null
        locN = n
        b.bandFilter.visibility = if (n != null) View.VISIBLE else View.GONE
        if (n != null) b.tvFilterInfo.text = "Đang lọc $n đời ·"
        val kq = if (moc != null && n != null) LayoutEngine.tinh(du, loc = BoLocDoi(moc, n)) else kqDay

        b.viewport.datDuLieu(kq, du.oThanhVien)
        daCoCay = true
    }

    // ---- lọc số đời ----

    /** null = lọc được; ngược lại là lý do ngắn hiện trong ngoặc ở mục menu. */
    private fun tinhLyDoKhongLoc(du: DuLieuCay, moc: Int?, kq: KetQuaBoCuc): String? {
        if (moc == null || du.oThanhVien.none { it.idOThanhVien == moc }) return "cần được gán Ô"
        val the = kq.the.firstOrNull { it.id == moc } ?: return "Ô của bạn nằm trong quan hệ ẩn"
        if (the.doi == null) return "Ô của bạn chưa nối với ai"
        return null
    }

    /** n = null: bỏ lọc. Lưu trên máy rồi vẽ lại từ dữ liệu đang có. */
    private fun datLoc(n: Int?) {
        val du = duHienTai ?: return
        kho.luu(du.toi.idTaiKhoan, idKhongGian, n)
        hienCay(du)
        b.viewport.post { b.viewport.scrollTo(0, 0) }
    }

    private fun moHopLoc() {
        val du = duHienTai ?: return
        val moc = du.toi.idOThanhVien ?: return
        val nMax = LayoutEngine.soDoiToiDa(du, moc) ?: return
        val d = DialogLocDoiBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(d.root).create()

        d.tvHint.text = "Số đời hiển thị quanh Ô của bạn, tính cả bạn (từ 1 đến $nMax)."
        fun hienSo(v: Int) {
            d.etN.setText(v.toString())
            d.etN.setSelection(d.etN.text.length)
            d.tvError.visibility = View.GONE
        }
        val macDinh = (locN ?: minOf(3, nMax)).coerceIn(1, nMax)
        fun soHienTai() = d.etN.text.toString().toIntOrNull()?.coerceIn(1, nMax) ?: macDinh
        hienSo(macDinh)

        d.btnMinus.setOnClickListener { hienSo((soHienTai() - 1).coerceAtLeast(1)) }
        d.btnPlus.setOnClickListener { hienSo((soHienTai() + 1).coerceAtMost(nMax)) }
        d.btnAll.setOnClickListener { dialog.dismiss(); datLoc(null) }
        d.btnApply.setOnClickListener {
            val v = d.etN.text.toString().toIntOrNull()
            if (v == null || v !in 1..nMax) {
                d.tvError.text = "Vui lòng nhập số từ 1 đến $nMax"
                d.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            dialog.dismiss()
            datLoc(v)
        }

        dialog.show()
        val le = (24 * resources.displayMetrics.density).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), le, 0, le, 0))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    // ---- ẩn quan hệ (nhấn giữ đường nối → nhãn "Ẩn" → thanh "Hoàn tác") ----

    private fun datAn(nut: NutQuanHe, an: Boolean): Kq<Unit> = when (nut) {
        is NutQuanHe.ChaMeCon -> repo.anQuanHeChaMe(nut.idCon, nut.idPhuHuynh, an)
        is NutQuanHe.HonNhan -> repo.anHonNhan(nut.idHonNhan, an)
    }

    private fun moTa(nut: NutQuanHe): String {
        val ten = { id: Int -> duHienTai?.oThanhVien?.firstOrNull { it.idOThanhVien == id }?.hoTen ?: "?" }
        return when (nut) {
            is NutQuanHe.ChaMeCon -> "${ten(nut.idCon)} — ${nut.idPhuHuynh.joinToString(" & ") { ten(it) }}"
            is NutQuanHe.HonNhan -> "${ten(nut.idChong)} — ${ten(nut.idVo)}"
        }
    }

    private fun anQuanHe(nut: NutQuanHe) {
        val mota = moTa(nut)
        when (val r = datAn(nut, true)) {
            is Kq.Loi -> Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
            is Kq.Ok -> {
                tai()
                val bar = Snackbar.make(b.root, "Đã ẩn: $mota", Snackbar.LENGTH_LONG)
                    .setAction("Hoàn tác") { hoanTacAn(nut) }
                    .setActionTextColor(getColor(R.color.peach_light))
                if (b.fab.visibility == View.VISIBLE) bar.anchorView = b.fab
                bar.show()
            }
        }
    }

    private fun hoanTacAn(nut: NutQuanHe) {
        val r = datAn(nut, false)
        if (r is Kq.Loi) Snackbar.make(b.root, r.thongBao, Snackbar.LENGTH_LONG).show()
        tai()
    }

    // ---- GD-20: bottom sheet "Quan hệ đã ẩn (n)" ----

    private fun moQuanHeAn() {
        val sheet = BottomSheetDialog(this)
        val sb = SheetQuanHeAnBinding.inflate(layoutInflater)
        sheet.setContentView(sb.root)
        (sb.root.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)   // để thấy góc bo của bg_sheet
        veDanhSachAn(sb)
        sheet.show()
    }

    private fun veDanhSachAn(sb: SheetQuanHeAnBinding) {
        val ds = duHienTai?.let { danhSachDuongAn(it) }.orEmpty()
        sb.tvTitle.text = "Quan hệ đã ẩn (${ds.size})"
        sb.tvEmpty.visibility = if (ds.isEmpty()) View.VISIBLE else View.GONE
        sb.list.removeAllViews()
        for (d in ds) {
            val dong = ItemQuanHeAnBinding.inflate(layoutInflater, sb.list, false)
            dong.tvTen.text = d.tenCap
            dong.tvLoai.text = d.loai
            dong.btnHienLai.setOnClickListener { hienLai(d); veDanhSachAn(sb) }
            sb.list.addView(dong.root)
        }
    }

    private fun hienLai(d: DuongAn) {
        val r = if (d.idHonNhan != null) repo.anHonNhan(d.idHonNhan, false)
        else repo.anQuanHeChaMe(d.idCon!!, d.idPhuHuynh, false)
        if (r is Kq.Loi) android.widget.Toast.makeText(this, r.thongBao, android.widget.Toast.LENGTH_LONG).show()
        tai()      // cây phía sau vẽ lại; duHienTai được cập nhật trước khi sheet vẽ lại danh sách
    }

    // ---- thanh tiêu đề: biểu tượng riêng tư (nếu có) + ba chấm ----

    private fun datNutBenPhai(riengTu: Boolean) {
        val dp = resources.displayMetrics.density
        b.appBar.setAction(this, R.drawable.ic_more_vert, "Menu") { }
        val nutBaCham = b.appBar.actionContainer.getChildAt(0)
        nutBaCham.setOnClickListener { moMenu(it) }

        b.appBar.actionContainer.removeAllViews()
        val hang = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        if (riengTu) {
            val khoa = ImageView(this).apply {
                setImageResource(R.drawable.ic_lock)
                imageTintList = ColorStateList.valueOf(Color.WHITE)
                contentDescription = "Không gian riêng tư"
            }
            hang.addView(khoa, LinearLayout.LayoutParams((20 * dp).toInt(), (20 * dp).toInt()).apply {
                marginEnd = (4 * dp).toInt()
            })
        }
        hang.addView(nutBaCham)
        b.appBar.actionContainer.addView(hang, FrameLayout.LayoutParams(-2, (48 * dp).toInt()))
    }

    // ---- menu ba chấm theo vai trò (QUYET_DINH mục 7) ----

    private fun moMenu(neo: View) {
        val vt = vaiTro ?: return
        val muc = mutableListOf<Pair<String, () -> Unit>>()
        var chiSoKhoa = -1
        muc += "Danh sách thành viên" to { Routes.moDanhSachThanhVien(this, idKhongGian) }
        if (vt != VaiTro.KHACH_TAM) muc += "Tạo mã lời mời" to { Routes.moMaMoi(this, idKhongGian) }
        // Mọi vai trò đều thấy mục này; ai chưa có Ô thì bị vô hiệu
        val ly = lyDoKhongLoc
        if (ly != null) {
            chiSoKhoa = muc.size
            muc += "Lọc số đời ($ly)" to { }
        } else {
            muc += (if (locN != null) "Lọc số đời (đang lọc $locN)" else "Lọc số đời") to { moHopLoc() }
        }
        if (vt.laQuanTri) {
            val n = duHienTai?.let { danhSachDuongAn(it).size } ?: 0
            muc += "Quan hệ đã ẩn ($n)" to { moQuanHeAn() }
        }
        if (vt == VaiTro.ADMIN) {
            muc += "Cài đặt Không gian" to { Routes.moCaiDatKhongGian(this, idKhongGian) }
            muc += "Quản lý vai trò" to { Routes.moQuanLyVaiTro(this, idKhongGian) }
        }
        PopupMenu(this, neo).apply {
            muc.forEachIndexed { i, m -> menu.add(0, i, i, m.first).isEnabled = i != chiSoKhoa }
            setOnMenuItemClickListener { muc[it.itemId].second(); true }
            show()
        }
    }
}