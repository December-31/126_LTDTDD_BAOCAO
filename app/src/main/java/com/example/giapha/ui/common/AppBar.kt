package com.example.giapha.ui.common

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.giapha.databinding.ViewAppBarBinding

/**
 * Gọi: binding.appBar.setup(this, "Hồ sơ cá nhân")
 * Tiêu đề dài: setup(this, "Quan hệ cha/mẹ", subtitle = "Nguyễn Văn Nam")
 * Màn gốc không có nút quay lại: showBack = false
 */
fun ViewAppBarBinding.setup(
    activity: ComponentActivity,
    title: String,
    subtitle: String? = null,
    showBack: Boolean = true
) {
    // Chiều cao chuẩn của thanh tiêu đề theo thiết bị
    val tv = TypedValue()
    root.context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
    val barHeight = TypedValue.complexToDimensionPixelSize(tv.data, root.resources.displayMetrics)

    // Cộng thêm chiều cao thanh trạng thái để thanh nâu phủ liền lên trên
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        v.setPadding(0, top, 0, 0)
        v.layoutParams = v.layoutParams.apply { height = barHeight + top }
        insets
    }

    tvTitle.text = title
    tvSubtitle.text = subtitle
    tvSubtitle.visibility = if (subtitle.isNullOrEmpty()) View.GONE else View.VISIBLE
    btnBack.visibility = if (showBack) View.VISIBLE else View.GONE
    btnBack.setOnClickListener { activity.onBackPressedDispatcher.onBackPressed() }
}

/** Thêm một nút biểu tượng vào bên phải thanh tiêu đề (ví dụ ba chấm). */
fun ViewAppBarBinding.setAction(context: Context, iconRes: Int, description: String, onClick: () -> Unit) {
    actionContainer.removeAllViews()
    val size = (48 * context.resources.displayMetrics.density).toInt()
    val button = ImageButton(context).apply {
        layoutParams = FrameLayout.LayoutParams(size, size)
        setImageResource(iconRes)
        contentDescription = description
        val out = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, out, true)
        setBackgroundResource(out.resourceId)
        setOnClickListener { onClick() }
    }
    actionContainer.addView(button)
}