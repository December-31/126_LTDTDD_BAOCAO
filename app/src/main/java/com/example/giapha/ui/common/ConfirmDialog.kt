package com.example.giapha.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.giapha.R
import com.example.giapha.databinding.DialogConfirmBinding

/**
 * Hộp thoại xác nhận dùng chung.
 * destructive = true: nút xác nhận đỏ (xóa, chặn, cấm, gỡ...); false: nút nâu.
 * Ví dụ:
 *   showConfirmDialog("Xóa Gia phả họ Nguyễn?", "Toàn bộ dữ liệu sẽ bị xóa vĩnh viễn.", "Xóa") { ... }
 */
fun Context.showConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Xác nhận",
    cancelText: String = "Hủy",
    destructive: Boolean = true,
    showIcon: Boolean = destructive,
    onConfirm: () -> Unit
) {
    val b = DialogConfirmBinding.inflate(LayoutInflater.from(this))
    b.tvTitle.text = title
    b.tvMessage.text = message
    b.btnCancel.text = cancelText
    b.btnConfirm.text = confirmText
    if (!destructive) {
        b.btnConfirm.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brown_primary))
    }
    if (!showIcon) b.iconWrap.visibility = android.view.View.GONE


    val dialog = AlertDialog.Builder(this).setView(b.root).create()
    b.btnCancel.setOnClickListener { dialog.dismiss() }
    b.btnConfirm.setOnClickListener {
        dialog.dismiss()
        onConfirm()
    }
    dialog.show()

    val margin = (24 * resources.displayMetrics.density).toInt()
    dialog.window?.apply {
        setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), margin, 0, margin, 0))
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}