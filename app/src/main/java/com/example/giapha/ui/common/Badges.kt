package com.example.giapha.ui.common

import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.giapha.R

/**
 * Đặt chữ và màu cho nhãn vai trò.
 * role dùng đúng giá trị trong CSDL: "Admin", "QuanLy", "ThanhVien", "ThanhVienHo", "KhachTam".
 * TextView cần có style Widget.GiaPha.Badge.
 */
fun TextView.setRoleBadge(role: String) {
    val (label, bg, fg, stroke) = when (role) {
        "Admin" -> BadgeSpec("Admin", R.color.brown_primary, R.color.white, null)
        "QuanLy" -> BadgeSpec("Quản lý", R.color.peach, R.color.brown_pressed, null)
        "ThanhVien" -> BadgeSpec("Thành viên", R.color.green_light, R.color.green_dark, null)
        "ThanhVienHo" -> BadgeSpec("Thành viên hờ", R.color.surface, R.color.brown_primary, R.color.brown_primary)
        else -> BadgeSpec("Khách tạm", R.color.peach_light, R.color.text_secondary, null)
    }
    text = label
    applyBadge(bg, fg, stroke)
}

/** Nhãn "Đã bị chặn" (thành viên bị chặn trong không gian) hoặc "Đã khóa" (AdminApp). */
fun TextView.setBlockedBadge(label: String = "Đã bị chặn") {
    text = label
    isAllCaps = false
    applyBadge(R.color.peach_light, R.color.danger, null)
}

private data class BadgeSpec(val label: String, val bg: Int, val fg: Int, val stroke: Int?)

private fun TextView.applyBadge(bgRes: Int, fgRes: Int, strokeRes: Int?) {
    val d = resources.displayMetrics.density
    background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100 * d
        setColor(ContextCompat.getColor(context, bgRes))
        if (strokeRes != null) setStroke((1 * d).toInt(), ContextCompat.getColor(context, strokeRes))
    }
    setTextColor(ContextCompat.getColor(context, fgRes))
}