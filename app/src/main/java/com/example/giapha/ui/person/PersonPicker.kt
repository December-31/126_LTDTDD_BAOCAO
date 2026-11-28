package com.example.giapha.ui.person

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.giapha.R
import com.example.giapha.data.ChaMeChon
import com.example.giapha.data.KetQuaTim
import com.example.giapha.data.QuanHeMoi
import com.example.giapha.databinding.ItemSearchResultBinding
import com.example.giapha.databinding.ViewPersonPickerBinding
import com.example.giapha.model.LoaiQuanHe
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Khối "tìm một người trong cây rồi chọn" của GD-07.
 * cheDoCon = true: hiện Loại quan hệ + Ngày xác lập. false (vợ/chồng): hiện Ngày cưới + Thứ tự hôn nhân.
 * loaiTru: id người đã được chọn ở khối khác, không hiện lại trong kết quả.
 * khiDoi: gọi khi chọn hoặc bỏ chọn người.
 */
class PersonPicker(
    private val b: ViewPersonPickerBinding,
    private val cheDoCon: Boolean,
    goiY: String,
    private val timKiem: (String) -> List<KetQuaTim>,
    private val loaiTru: () -> Int?,
    private val chonNgay: (LocalDate?, (LocalDate) -> Unit) -> Unit,
    private val khiDoi: () -> Unit
) {
    private val ctx: Context = b.root.context
    private val d = ctx.resources.displayMetrics.density
    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var ketQua: List<KetQuaTim> = emptyList()
    private var chon: KetQuaTim? = null
    private var loai = LoaiQuanHe.RUOT
    private var ngay: LocalDate? = null

    val root: View get() = b.root
    val daChon: Boolean get() = chon != null
    val idDaChon: Int? get() = chon?.idO

    init {
        b.etSearch.hint = goiY
        b.segType.setOptions(LoaiQuanHe.entries.map { it.nhan }, 0)
        b.segType.onChanged = { i -> if (i >= 0) loai = LoaiQuanHe.entries[i] }
        b.etSearch.doAfterTextChanged { tim(it?.toString().orEmpty()) }
        b.fieldXacLap.setOnClickListener {
            chonNgay(ngay) { n -> ngay = n; hienNgay(b.tvXacLap, n) }
        }
        b.fieldCuoi.setOnClickListener {
            chonNgay(ngay) { n -> ngay = n; hienNgay(b.tvCuoi, n) }
        }
    }

    fun hienLoi(co: Boolean) {
        b.fieldSearch.isActivated = co
        b.tvSearchError.visibility = if (co) View.VISIBLE else View.GONE
    }

    fun chaMe(): ChaMeChon? = chon?.let { ChaMeChon(it.idO, loai, ngay) }

    fun voChong(): QuanHeMoi.LaVoChong? = chon?.let {
        val thuTu = b.etOrder.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: (it.soHonNhan + 1)
        QuanHeMoi.LaVoChong(it.idO, ngay, thuTu)
    }

    // ------------------------------------------------------------------ nội bộ

    /** Gõ lại từ khóa thì bỏ chọn cũ và tìm lại. */
    private fun tim(q: String) {
        val daCoChon = chon != null
        chon = null
        hienLoi(false)
        b.emptyBox.visibility = View.GONE
        ketQua = if (q.isBlank()) emptyList()
        else {
            val bo = loaiTru()
            timKiem(q.trim()).filter { it.idO != bo }
        }
        if (q.isNotBlank() && ketQua.isEmpty()) b.emptyBox.visibility = View.VISIBLE
        ve()
        capNhatNhom()
        if (daCoChon) khiDoi()
    }

    private fun ve() {
        b.resultsBox.removeAllViews()
        val inflater = LayoutInflater.from(ctx)
        for (kq in ketQua) {
            val r = ItemSearchResultBinding.inflate(inflater, b.resultsBox, false)
            val bat = chon?.idO == kq.idO
            r.tvName.text = kq.hoTen
            r.tvContext.text = kq.nguCanh
            r.ivCheck.visibility = if (bat) View.VISIBLE else View.GONE
            r.root.background = GradientDrawable().apply {
                cornerRadius = 12 * d
                setColor(ContextCompat.getColor(ctx, if (bat) R.color.green_light else R.color.surface))
                setStroke(
                    d.toInt(),
                    ContextCompat.getColor(ctx, if (bat) R.color.green_mid else R.color.border_default)
                )
            }
            r.root.setOnClickListener {
                chon = kq
                hienLoi(false)
                if (!cheDoCon) b.etOrder.setText((kq.soHonNhan + 1).toString())   // gợi ý thứ tự tiếp theo
                ve()
                capNhatNhom()
                khiDoi()
            }
            b.resultsBox.addView(r.root)
        }
    }

    private fun capNhatNhom() {
        b.groupCon.visibility = if (chon != null && cheDoCon) View.VISIBLE else View.GONE
        b.groupVo.visibility = if (chon != null && !cheDoCon) View.VISIBLE else View.GONE
    }

    private fun hienNgay(tv: TextView, n: LocalDate) {
        tv.text = n.format(fmt)
        tv.setTextColor(ctx.getColor(R.color.text_primary))
    }
}