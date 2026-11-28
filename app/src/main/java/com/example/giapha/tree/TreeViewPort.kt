package com.example.giapha.tree

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
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
        const val HE_SO_NHO_NHAT = 0.5f
        const val HE_SO_LON_NHAT = 1.5f
    }

    val cay = TreeView(context)

    /** Nhận id Ô khi người dùng chạm một thẻ. */
    var khiChamThe: ((Int) -> Unit)? = null

    private val mat = context.resources.displayMetrics.density
    private val cuon = OverScroller(context)
    private var duX = 0f
    private var duY = 0f

    private val phongTo = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(d: ScaleGestureDetector): Boolean {
            datHeSo(cay.heSo * d.scaleFactor, d.focusX, d.focusY)
            return true
        }
    })

    private val cuChi = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, dx: Float, dy: Float): Boolean {
            if (phongTo.isInProgress) return false
            duX += dx; duY += dy
            val ix = duX.toInt(); val iy = duY.toInt()
            duX -= ix; duY -= iy
            cuonToi(scrollX + ix, scrollY + iy)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, vx: Float, vy: Float): Boolean {
            if (phongTo.isInProgress) return false
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

    /** Đặt cây mới. Vị trí cuộn và mức zoom hiện tại được giữ (chỉ kẹp lại trong phạm vi mới). */
    fun datDuLieu(kq: KetQuaBoCuc, o: List<OThanhVien>, cauHinh: CauHinhCay = CauHinhCay()) {
        cay.datDuLieu(kq, o, cauHinh)
        cuonToi(scrollX, scrollY)
    }

    // ---- zoom ----

    /** Đặt mức zoom, giữ điểm (fx, fy) của khung nhìn đứng yên trên màn hình. */
    private fun datHeSo(moi: Float, fx: Float, fy: Float) {
        val cu = cay.heSo
        val v = moi.coerceIn(HE_SO_NHO_NHAT, HE_SO_LON_NHAT)
        if (v == cu) return
        val tamX = (scrollX + fx) / (mat * cu)      // điểm giữ yên, tính theo dp
        val tamY = (scrollY + fy) / (mat * cu)
        cuon.abortAnimation()
        cay.heSo = v
        cuonToi((tamX * mat * v - fx).roundToInt(), (tamY * mat * v - fy).roundToInt())
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
        phongTo.onTouchEvent(e)
        cuChi.onTouchEvent(e)
        return true
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