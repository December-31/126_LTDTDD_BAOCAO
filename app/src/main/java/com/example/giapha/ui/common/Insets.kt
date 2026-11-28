package com.example.giapha.ui.common

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.view.ViewGroup

/** Icon thanh trạng thái tối (dùng cho màn nền sáng, không có thanh nâu). */
fun Activity.setLightStatusBar(light: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = light
}

/** Đệm thêm chỗ cho thanh hệ thống và bàn phím. */
fun View.applySystemBarsPadding() {
    val l = paddingLeft; val t = paddingTop; val r = paddingRight; val b = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        v.setPadding(l + bars.left, t + bars.top, r + bars.right, b + bars.bottom)
        insets
    }
}

/** Chỉ chừa chỗ ở đáy (thanh điều hướng, bàn phím); dùng khi phần trên đã có thanh tiêu đề tự xử lý. */
fun View.applyBottomInsetPadding() {
    val base = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, base + bars.bottom)
        insets
    }
}

/** Đẩy nút nổi lên khỏi thanh điều hướng: cộng chiều cao thanh hệ thống vào lề dưới đang có. */
fun View.applyBottomMarginInset() {
    val base = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
        val lp = v.layoutParams as ViewGroup.MarginLayoutParams
        lp.bottomMargin = base + bottom
        v.layoutParams = lp
        insets
    }
}