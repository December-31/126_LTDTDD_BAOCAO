package com.example.giapha.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Một "Ô" trên cây, đại diện một người. */
data class OThanhVien(
    val idOThanhVien: Int,
    val idKhongGian: Int,
    val hoTen: String,
    val bietDanh: String? = null,
    val gioiTinh: GioiTinh? = null,
    val ngaySinh: LocalDate? = null,
    val queQuan: String? = null,
    val conSong: Boolean = true,
    val ngayMat: LocalDate? = null,
    val cccd: String? = null,
    val trangThaiLienKet: TrangThaiLienKet = TrangThaiLienKet.DANG_NOI,
    val laTruongToc: Boolean = false
)

data class Avatar(
    val idAvatar: Int,
    val idOThanhVien: Int,
    val duongDanAnh: String? = null,
    val loaiKhung: String? = null,
    val hieuUng: String? = null,
    val trangThai: TrangThaiAvatar = TrangThaiAvatar.DANG_DUNG,
    val ngayTao: LocalDateTime
)

/** Tiểu sử: một bản cho mỗi Ô. */
data class CuocDoi(
    val idCuocDoi: Int,
    val idOThanhVien: Int,
    val idNguoiViet: Int,
    val noiDung: String? = null,
    val ngayCapNhat: LocalDateTime
)

data class BinhLuan(
    val idBinhLuan: Int,
    val idCuocDoi: Int,
    val idNguoiBinhLuan: Int,
    val noiDung: String,
    val ngayBinhLuan: LocalDateTime
)

data class ThanhTuu(
    val idThanhTuu: Int,
    val idOThanhVien: Int,
    val tenThanhTuu: String,
    val moTa: String? = null,
    val nam: Int? = null
)