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
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.databinding.ActivityTreeBinding
import com.example.giapha.model.VaiTro
import com.example.giapha.tree.LayoutEngine
import com.example.giapha.ui.Routes
import com.example.giapha.ui.common.applyBottomMarginInset
import com.example.giapha.ui.common.setAction
import com.example.giapha.ui.common.setup

class TreeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_KHONG_GIAN = "idKhongGian"
    }

    private lateinit var b: ActivityTreeBinding
    private val repo get() = Services.repository
    private var idKhongGian = 0
    private var vaiTro: VaiTro? = null
    private var daCoCay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTreeBinding.inflate(layoutInflater)
        setContentView(b.root)
        idKhongGian = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)

        b.appBar.setup(this, "")
        b.fab.applyBottomMarginInset()
        b.fab.setOnClickListener { Routes.moThemO(this, idKhongGian) }
        b.viewport.khiChamThe = { idO -> Routes.moChiTietO(this, idKhongGian, idO) }
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
        b.viewport.datDuLieu(LayoutEngine.tinh(du), du.oThanhVien)
        daCoCay = true
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
        muc += "Danh sách thành viên" to { Routes.moDanhSachThanhVien(this, idKhongGian) }
        if (vt != VaiTro.KHACH_TAM) muc += "Tạo mã lời mời" to { Routes.moMaMoi(this, idKhongGian) }
        if (vt == VaiTro.ADMIN) {
            muc += "Cài đặt Không gian" to { Routes.moCaiDatKhongGian(this, idKhongGian) }
            muc += "Quản lý vai trò" to { Routes.moQuanLyVaiTro(this, idKhongGian) }
        }
        PopupMenu(this, neo).apply {
            muc.forEachIndexed { i, m -> menu.add(0, i, i, m.first) }
            setOnMenuItemClickListener { muc[it.itemId].second(); true }
            show()
        }
    }
}