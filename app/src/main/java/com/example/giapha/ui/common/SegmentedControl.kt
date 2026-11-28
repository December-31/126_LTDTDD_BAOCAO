package com.example.giapha.ui.common

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.giapha.R

/** Nhóm lựa chọn 1-trong-n dạng viên thuốc (Nam/Nữ/Khác, Ruột/Nuôi/Kế). Đặt layout_height = 52dp. */
class SegmentedControl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val d = resources.displayMetrics.density
    private val tabs = mutableListOf<TextView>()

    var selectedIndex = -1
        private set
    /** true: bấm lại mục đang chọn thì bỏ chọn (selectedIndex = -1). */
    var allowDeselect = false
    var onChanged: ((Int) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        val p = (4 * d).toInt()
        setPadding(p, p, p, p)
        background = GradientDrawable().apply {
            cornerRadius = 16 * d
            setColor(ContextCompat.getColor(context, R.color.peach_light))
        }
    }

    fun setOptions(labels: List<String>, selected: Int = -1) {
        removeAllViews()
        tabs.clear()
        val font = ResourcesCompat.getFont(context, R.font.be_vietnam_pro_semibold)
        labels.forEachIndexed { i, nhan ->
            val tv = TextView(context).apply {
                layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                gravity = Gravity.CENTER
                text = nhan
                textSize = 14f
                typeface = font
                setOnClickListener {
                    if (i == selectedIndex && !allowDeselect) return@setOnClickListener
                    chon(if (i == selectedIndex) -1 else i, true)
                }
            }
            tabs.add(tv)
            addView(tv)
        }
        chon(selected, false)
    }

    fun chon(index: Int, thongBao: Boolean = false) {
        selectedIndex = index
        tabs.forEachIndexed { i, tv ->
            val bat = i == index
            tv.setTextColor(ContextCompat.getColor(context, if (bat) R.color.white else R.color.text_secondary))
            tv.background = if (bat) GradientDrawable().apply {
                cornerRadius = 12 * d
                setColor(ContextCompat.getColor(context, R.color.brown_primary))
            } else null
        }
        if (thongBao) onChanged?.invoke(index)
    }
}