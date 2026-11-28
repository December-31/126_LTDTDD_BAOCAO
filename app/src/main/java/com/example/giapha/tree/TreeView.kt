package com.example.giapha.tree

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.giapha.R
import com.example.giapha.model.OThanhVien
import kotlin.math.ceil

class TreeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private companion object {
        const val NET_DUONG = 1.5f
        const val CHU_TEN = 10f
        const val CHU_DOI = 11f
        const val CHU_TIEU_DE = 12f
        val XANH_NHAT = Color.parseColor("#DCEBD2")   // avatar + nền nhóm chẵn
        val KEM = Color.parseColor("#FBF1E4")         // nền nhóm lẻ
    }

    private val mat = context.resources.displayMetrics.density

    private var ketQua: KetQuaBoCuc? = null
    private var cauHinh = CauHinhCay()
    private var oTheoId: Map<Int, OThanhVien> = emptyMap()
    private var chuTen: Map<Int, StaticLayout> = emptyMap()

    /** Mức phóng: 1f = 100%. Bước 7c nối với nút +/−. */
    var heSo: Float = 1f
        set(v) {
            field = v
            requestLayout()
            invalidate()
        }

    val rongDp: Float get() = ketQua?.rong ?: 0f
    val caoDp: Float get() = ketQua?.cao ?: 0f

    /** Toạ độ chạm tính theo pixel của view; trả về id Ô nếu chạm trúng thẻ. */
    fun idTheTai(xPx: Float, yPx: Float): Int? {
        val kq = ketQua ?: return null
        val s = mat * heSo
        val x = xPx / s
        val y = yPx / s
        return kq.the.lastOrNull {
            x >= it.x && x <= it.x + cauHinh.theRong && y >= it.y && y <= it.y + cauHinh.theCao
        }?.id
    }

    // ---- màu và cọ ----
    private val nau = ResourcesCompat.getColor(resources, R.color.brown_primary, null)
    private val nauDam = ResourcesCompat.getColor(resources, R.color.text_primary, null)
    private val chuPhu = ResourcesCompat.getColor(resources, R.color.text_secondary, null)
    private val vien = ResourcesCompat.getColor(resources, R.color.border_default, null)
    private val vang = ResourcesCompat.getColor(resources, R.color.gold, null)
    private val do_ = ResourcesCompat.getColor(resources, R.color.danger, null)
    private val dai1 = ResourcesCompat.getColor(resources, R.color.peach_light, null)

    private val fontTen: Typeface = font(R.font.be_vietnam_pro_medium, Typeface.DEFAULT)
    private val fontDam: Typeface = font(R.font.be_vietnam_pro_semibold, Typeface.DEFAULT_BOLD)

    private val cuNet = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = NET_DUONG; color = nau; strokeCap = Paint.Cap.BUTT
    }
    private val cuDut = Paint(cuNet).apply { pathEffect = DashPathEffect(floatArrayOf(5f, 4f), 0f) }
    private val cuDo = Paint(cuNet).apply { color = do_ }
    private val cuDoDut = Paint(cuDo).apply { pathEffect = DashPathEffect(floatArrayOf(5f, 4f), 0f) }
    private val cuDac = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = nau }
    private val cuDau = Paint(cuNet).apply { color = do_; strokeWidth = 1.8f; strokeCap = Paint.Cap.ROUND }
    private val cuNen = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val cuThe = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = Color.WHITE }
    private val cuVienThe = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1f; color = vien }
    private val cuVienVang = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 2f; color = vang }
    private val cuAvatar = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = XANH_NHAT }
    private val cuVuongMien = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = vang }

    private val chuCai = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = nau; textSize = 16f; typeface = fontDam; textAlign = Paint.Align.CENTER
    }
    private val chuNhanDoi = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = chuPhu; textSize = CHU_DOI; typeface = fontTen
    }
    private val chuTieuDe = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = nauDam; textSize = CHU_TIEU_DE; typeface = fontDam
    }

    private val khung = RectF()
    private val duong = Path()

    private fun font(id: Int, du: Typeface): Typeface =
        try { ResourcesCompat.getFont(context, id) ?: du } catch (_: Exception) { du }

    // ---------------------------------------------------------------------------------------

    /** Đặt dữ liệu. `o` là danh sách Ô cần để lấy tên, họ tên, Trưởng tộc. */
    fun datDuLieu(kq: KetQuaBoCuc, o: List<OThanhVien>, cauHinh: CauHinhCay = CauHinhCay()) {
        ketQua = kq
        this.cauHinh = cauHinh
        oTheoId = o.associateBy { it.idOThanhVien }
        taoChuTen(kq)
        requestLayout()
        invalidate()
    }

    private fun taoChuTen(kq: KetQuaBoCuc) {
        val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = nauDam; textSize = CHU_TEN; typeface = fontTen
        }
        val rong = (cauHinh.theRong - 4f).toInt()
        val m = HashMap<Int, StaticLayout>()
        for (t in kq.the) {
            val ten = oTheoId[t.id]?.hoTen ?: "?"
            m[t.id] = StaticLayout.Builder.obtain(ten, 0, ten.length, tp, rong)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setMaxLines(2)
                .setEllipsize(TextUtils.TruncateAt.END)
                .setIncludePad(false)
                .build()
        }
        chuTen = m
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val kq = ketQua
        val w = if (kq == null) 0 else ceil(kq.rong * mat * heSo).toInt()
        val h = if (kq == null) 0 else ceil(kq.cao * mat * heSo).toInt()
        setMeasuredDimension(
            resolveSize(w, widthMeasureSpec),
            resolveSize(h, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        val kq = ketQua ?: return
        canvas.save()
        canvas.scale(mat * heSo, mat * heSo)

        veDai(canvas, kq)
        veNen(canvas, kq)
        veNet(canvas, kq)
        veChamVaDau(canvas, kq)
        for (t in kq.the) veThe(canvas, t)
        veTieuDe(canvas, kq)

        canvas.restore()
    }

    // ---- từng lớp, theo thứ tự "đường vẽ trước, thẻ vẽ sau" ----

    private fun veDai(c: Canvas, kq: KetQuaBoCuc) {
        for ((i, d) in kq.dai.withIndex()) {
            if (i % 2 == 0) {
                cuNen.color = dai1
                cuNen.alpha = 120
                c.drawRect(0f, d.y, kq.rong, d.y + d.cao, cuNen)
                cuNen.alpha = 255
            }
            val nhan = "Đời ${d.doi}"
            val vach = d.y + d.cao / 2f - (chuNhanDoi.ascent() + chuNhanDoi.descent()) / 2f
            c.drawText(nhan, 12f, vach, chuNhanDoi)
        }
    }

    private fun veNen(c: Canvas, kq: KetQuaBoCuc) {
        for (n in kq.nen) {
            cuNen.color = if (n.chiSo % 2 == 0) XANH_NHAT else KEM
            cuNen.alpha = 170
            khung.set(n.x, n.y, n.x + n.rong, n.y + n.cao)
            c.drawRoundRect(khung, 10f, 10f, cuNen)
            cuNen.alpha = 255
        }
    }

    private fun veNet(c: Canvas, kq: KetQuaBoCuc) {
        // Vẽ liền trước, đứt sau cho gọn. Mỗi đoạn là đường ngang hoặc đứng.
        for (n in kq.net) {
            val p = when {
                n.kieu == KieuNet.DUT && n.canHuyet -> cuDoDut
                n.kieu == KieuNet.DUT -> cuDut
                n.canHuyet -> cuDo
                else -> cuNet
            }
            duong.reset()
            duong.moveTo(n.x1, n.y1)
            duong.lineTo(n.x2, n.y2)
            c.drawPath(duong, p)
        }
    }

    private fun veChamVaDau(c: Canvas, kq: KetQuaBoCuc) {
        for (d in kq.chamHonNhan) c.drawCircle(d.x, d.y, 3f, cuDac)
        val r = 4.5f
        for (d in kq.dauHuy) {
            // nền trắng nhỏ để dấu × không dính vào đường
            cuNen.color = Color.WHITE
            c.drawCircle(d.x, d.y, r + 2f, cuNen)
            c.drawLine(d.x - r, d.y - r, d.x + r, d.y + r, cuDau)
            c.drawLine(d.x - r, d.y + r, d.x + r, d.y - r, cuDau)
        }
    }

    private fun veThe(c: Canvas, t: TheTrenCay) {
        val o = oTheoId[t.id]
        val w = cauHinh.theRong
        val h = cauHinh.theCao
        khung.set(t.x, t.y, t.x + w, t.y + h)
        c.drawRoundRect(khung, 10f, 10f, cuThe)
        val truongToc = o?.laTruongToc == true
        if (truongToc) {
            khung.inset(1f, 1f)
            c.drawRoundRect(khung, 9f, 9f, cuVienVang)
        } else {
            khung.inset(0.5f, 0.5f)
            c.drawRoundRect(khung, 9.5f, 9.5f, cuVienThe)
        }

        // avatar tròn đường kính 36, chữ cái đầu của tên gọi (từ cuối)
        val cx = t.x + w / 2f
        val cy = t.y + 8f + 18f
        c.drawCircle(cx, cy, 18f, cuAvatar)
        val chu = chuCaiDau(o?.hoTen)
        c.drawText(chu, cx, cy - (chuCai.ascent() + chuCai.descent()) / 2f, chuCai)

        // tên tối đa 2 dòng, dưới avatar
        val bo = chuTen[t.id]
        if (bo != null) {
            c.save()
            val cao = bo.height
            val dy = t.y + 48f + (28f - cao) / 2f
            c.translate(t.x + 2f, dy)
            bo.draw(c)
            c.restore()
        }

        if (truongToc) veVuongMien(c, cx, t.y)
    }

    /** Vương miện nhỏ vàng đè lên mép trên thẻ. */
    private fun veVuongMien(c: Canvas, cx: Float, top: Float) {
        val y = top - 3f
        duong.reset()
        duong.moveTo(cx - 7f, y + 8f)
        duong.lineTo(cx - 8f, y)
        duong.lineTo(cx - 3.5f, y + 4f)
        duong.lineTo(cx, y - 2f)
        duong.lineTo(cx + 3.5f, y + 4f)
        duong.lineTo(cx + 8f, y)
        duong.lineTo(cx + 7f, y + 8f)
        duong.close()
        c.drawPath(duong, cuVuongMien)
    }

    private fun veTieuDe(c: Canvas, kq: KetQuaBoCuc) {
        for (t in kq.tieuDe) {
            // y của engine là mép trên dòng chữ
            c.drawText(t.chu, t.x, t.y - chuTieuDe.ascent(), chuTieuDe)
        }
    }

    private fun chuCaiDau(ten: String?): String =
        ten?.trim()?.split(' ')?.lastOrNull { it.isNotEmpty() }?.first()?.uppercase() ?: "?"
}