package com.example.giapha.model

import java.time.LocalDate

/** Khóa chính (idCon, idPhuHuynh). */
data class QuanHeChaMe(
    val idCon: Int,
    val idPhuHuynh: Int,
    val loai: LoaiQuanHe,
    val ngayXacLap: LocalDate? = null,
    val hienThiLienKet: Boolean = true     // false = đã ẩn do tách nhánh
)

data class QHeHonNhan(
    val id: Int,
    val idChong: Int,
    val idVo: Int,
    val ngayCoi: LocalDate? = null,
    val ngayLyHon: LocalDate? = null,
    val trangThai: TrangThaiHonNhan = TrangThaiHonNhan.DANG_KET_HON,
    val thuTuHonNhan: Int? = null,
    val hienThiLienKet: Boolean = true
)