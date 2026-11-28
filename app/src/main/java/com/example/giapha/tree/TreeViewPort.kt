package com.example.giapha.tree

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.OverScroller
import com.example.giapha.model.OThanhVien
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TreeViewport @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        val CAC_MUC = floatArrayOf(0.5f, 0.75f, 1f, 1.25f, 1.5f)
        const val MUC_MAC_DINH = 2
        private const val MS_ZOOM = 180L
    }

    val cay = TreeView(context)

    /** Nhận id Ô khi người dùng chạm một thẻ. */
    var khiChamThe: ((Int) -> Unit)? = null

    /** Gọi sau mỗi lần đổi mức: (hệ số mới, còn phóng to được, còn thu nhỏ được). */
    var khiDoiZoom: ((Float, Boolean, Boolean) -> Unit)? = null

    private var muc = MUC_MAC_DINH
    val coTheLen: Boolean get() = muc < CAC_MUC.size - 1
    val coTheXuong: Boolean get() = muc > 0

    private val mat = context.resources.displayMetrics.density
    private val cuon = OverScroller(context)
    private var anhDong: ValueAnimator? = null
    private var duX = 0f
    private var duY = 0f

    private val cuChi = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, dx: Float, dy: Float): Boolean {
            duX += dx; duY += dy
            val ix = duX.toInt(); val iy = duY.toInt()
            duX -= ix; duY -= iy
            cuonToi(scrollX + ix, scrollY + iy)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, vx: Float, vy: Float): Boolean {
            cuon.fling(scrollX, scrollY, -vx.toInt(), -vy.toInt(), 0, toiDaX(), 0, toiDaY())
            postInvalidateOnAnimation()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            cay.idTheTai(e.x + scrollX, e.y + scrollY)?.let { khiChamThe?.invoke(it) }
            return true
        }
    })

    init {
        addView(cay, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    // ---- dữ liệu ----

    fun datDuLieu(kq: KetQuaBoCuc, o: List<OThanhVien>, cauHinh: CauHinhCay = CauHinhCay()) {
        cay.datDuLieu(kq, o, cauHinh)
        scrollTo(0, 0)
    }

    // ---- zoom ----

    fun zoomLen() = datMuc(muc + 1)
    fun zoomXuong() = datMuc(muc - 1)

    private fun datMuc(m: Int) {
        if (m < 0 || m >= CAC_MUC.size || m == muc) return
        muc = m
        cuon.abortAnimation()
        val tu = cay.heSo
        val den = CAC_MUC[m]
        val tamX = (scrollX + width / 2f) / (mat * tu)     // điểm giữa khung nhìn, tính theo dp
        val tamY = (scrollY + height / 2f) / (mat * tu)
        anhDong?.cancel()
        anhDong = ValueAnimator.ofFloat(tu, den).apply {
            duration = MS_ZOOM
            addUpdateListener {
                val v = it.animatedValue as Float
                cay.heSo = v
                cuonToi((tamX * mat * v - width / 2f).roundToInt(), (tamY * mat * v - height / 2f).roundToInt())
            }
            start()
        }
        khiDoiZoom?.invoke(den, coTheLen, coTheXuong)
    }

    // ---- cuộn ----

    private fun noiDungRong() = ceil(cay.rongDp * mat * cay.heSo).toInt()
    private fun noiDungCao() = ceil(cay.caoDp * mat * cay.heSo).toInt()
    private fun toiDaX() = max(0, noiDungRong() - width)
    private fun toiDaY() = max(0, noiDungCao() - height)

    private fun cuonToi(x: Int, y: Int) {
        scrollTo(min(max(x, 0), toiDaX()), min(max(y, 0), toiDaY()))
    }

    override fun computeScroll() {
        if (cuon.computeScrollOffset()) {
            cuonToi(cuon.currX, cuon.currY)
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            if (!cuon.isFinished) cuon.abortAnimation()
            duX = 0f; duY = 0f
            parent?.requestDisallowInterceptTouchEvent(true)
        }
        return cuChi.onTouchEvent(e)
    }

    // ---- đo và đặt vị trí: con luôn có kích thước thật của cây ----

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        )
        val tuyetDoi = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        cay.measure(tuyetDoi, tuyetDoi)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        cay.layout(0, 0, cay.measuredWidth, cay.measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        super.onSizeChanged(w, h, ow, oh)
        cuonToi(scrollX, scrollY)
    }
}