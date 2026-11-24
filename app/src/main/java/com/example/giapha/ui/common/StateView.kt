package com.example.giapha.ui.common

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.giapha.R
import com.google.android.material.button.MaterialButton

/**
 * Trạng thái dùng chung cho màn danh sách. Đặt đè lên danh sách (cùng kích thước):
 *   stateView.showLoading()                      khung xương nhấp nháy
 *   stateView.showEmpty("Chưa có thành viên.")   hình minh họa + một dòng chữ
 *   stateView.showError { tải lại }              icon lỗi + "Thử lại"
 *   stateView.hide()                             có dữ liệu thì ẩn đi
 */
class StateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val skeletonList: LinearLayout
    private val messageBox: LinearLayout
    private val ivState: ImageView
    private val tvState: TextView
    private val btnRetry: MaterialButton
    private var pulse: ValueAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_state, this, true)
        skeletonList = findViewById(R.id.skeletonList)
        messageBox = findViewById(R.id.messageBox)
        ivState = findViewById(R.id.ivState)
        tvState = findViewById(R.id.tvState)
        btnRetry = findViewById(R.id.btnRetry)

        setBackgroundColor(ContextCompat.getColor(context, R.color.bg_screen))
        isClickable = true // chặn chạm xuyên xuống danh sách phía sau
        repeat(6) {
            LayoutInflater.from(context).inflate(R.layout.item_skeleton_row, skeletonList, true)
        }
        visibility = View.GONE
    }

    fun showLoading() {
        visibility = View.VISIBLE
        messageBox.visibility = View.GONE
        skeletonList.visibility = View.VISIBLE
        startPulse()
    }

    fun showEmpty(text: String) {
        showMessage(R.drawable.ic_empty_tree, text)
        btnRetry.visibility = View.GONE
    }

    fun showError(
        text: String = "Không thể tải dữ liệu. Vui lòng kiểm tra kết nối.",
        onRetry: () -> Unit
    ) {
        showMessage(R.drawable.ic_cloud_off, text)
        btnRetry.visibility = View.VISIBLE
        btnRetry.setOnClickListener { onRetry() }
    }

    fun hide() {
        stopPulse()
        visibility = View.GONE
    }

    private fun showMessage(iconRes: Int, text: String) {
        stopPulse()
        visibility = View.VISIBLE
        skeletonList.visibility = View.GONE
        messageBox.visibility = View.VISIBLE
        ivState.setImageResource(iconRes)
        tvState.text = text
    }

    private fun startPulse() {
        if (pulse?.isRunning == true) return
        pulse = ValueAnimator.ofFloat(0.45f, 1f).apply {
            duration = 800
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { skeletonList.alpha = it.animatedValue as Float }
            start()
        }
    }

    private fun stopPulse() {
        pulse?.cancel()
        pulse = null
        skeletonList.alpha = 1f
    }

    override fun onDetachedFromWindow() {
        stopPulse()
        super.onDetachedFromWindow()
    }
}