package com.example.giapha.ui.common

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Thời gian dạng tương đối: "Vừa xong", "5 phút trước", "2 giờ trước", "Hôm qua", "3 ngày trước", sau đó là ngày/tháng/năm. */
fun LocalDateTime.tuongDoi(bay: LocalDateTime = LocalDateTime.now()): String {
    val phut = Duration.between(this, bay).toMinutes()
    if (phut < 1) return "Vừa xong"
    if (phut < 60) return "$phut phút trước"
    if (phut < 24 * 60) return "${phut / 60} giờ trước"
    val ngay = ChronoUnit.DAYS.between(this.toLocalDate(), bay.toLocalDate())
    return when {
        ngay == 1L -> "Hôm qua"
        ngay < 7 -> "$ngay ngày trước"
        else -> format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}
