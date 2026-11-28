package com.example.giapha.data

import com.example.giapha.model.*

/** Một dòng trong danh sách không gian (GD-03): không gian kèm vai trò của người đang đăng nhập. */
data class KhongGianCuaToi(
    val khongGian: KhongGian,
    val vaiTro: VaiTro
)

/** Toàn bộ dữ liệu để vẽ cây (GD-06, GD-24). Bố cục tính ở LayoutEngine, không tính ở đây. */
data class DuLieuCay(
    val khongGian: KhongGian,
    val toi: ThanVienTrongKGian,                // vai trò của người đang xem
    val oThanhVien: List<OThanhVien>,
    val quanHeChaMe: List<QuanHeChaMe>,         // gồm cả dòng đang ẩn (HienThiLienKet = false)
    val honNhan: List<QHeHonNhan>,
    val anhDangDung: Map<Int, String?>          // id Ô -> đường dẫn ảnh đang dùng
)

/** Một người liên quan trong khối "Quan hệ gia đình" của GD-08. nhan: Ruột/Nuôi/Kế hoặc trạng thái hôn nhân. */
data class NguoiLienQuan(
    val idO: Int,
    val hoTen: String,
    val nhan: String?,
    val conSong: Boolean
)

/** Dữ liệu của màn GD-08. */
data class ChiTietO(
    val o: OThanhVien,
    val toi: ThanVienTrongKGian,          // vai trò của người đang xem
    val chaMe: List<NguoiLienQuan>,
    val voChong: List<NguoiLienQuan>,
    val con: List<NguoiLienQuan>
)

/** Tác động của việc xóa một Ô, dùng dựng nội dung hộp cảnh báo ở GD-08. */
data class TacDongXoa(
    val hoTen: String,
    val soChaMe: Int,
    val soVoChong: Int,
    val soCon: Int,
    val soThanhTuu: Int,
    val soBinhLuan: Int,
    val soTieuSu: Int,
    val tachKhoiCayChinh: List<String>     // họ tên những người sẽ mất liên kết với cây chính
)

/**
 * Cửa duy nhất giữa màn hình và dữ liệu. Màn hình không đọc SampleData trực tiếp.
 * Hiện chạy bằng FakeRepository (bộ nhớ); sau này thay bằng bản gọi API.
 * Các nhóm hàm cho bước 8–11 sẽ được thêm vào khi làm tới bước đó.
 */
interface Repository {

    // ---- Tài khoản (GD-01, 02, 04) ----
    fun guiMaDangKy(sdt: String): Kq<Unit>
    fun guiMaDangNhap(sdt: String): Kq<Unit>
    fun dangKy(ten: String, sdt: String, otp: String): Kq<TaiKhoan>
    fun dangNhap(sdt: String, otp: String): Kq<TaiKhoan>
    fun dangXuat()
    fun taiKhoanHienTai(): TaiKhoan?
    fun capNhatHoSo(tenHienThi: String, email: String?): Kq<TaiKhoan>

    // ---- Không gian (GD-03, 05) ----
    fun danhSachKhongGian(tuKhoa: String = ""): Kq<List<KhongGianCuaToi>>
    fun taoKhongGian(ten: String): Kq<KhongGian>
    fun vaoBangMa(ma: String): Kq<KhongGian>
    fun vaiTroCuaToi(idKhongGian: Int): ThanVienTrongKGian?

    // ---- Cây gia phả (GD-06) ----
    fun duLieuCay(idKhongGian: Int): Kq<DuLieuCay>

    // ---- Chi tiết Ô (GD-08) ----
    fun chiTietO(idO: Int): Kq<ChiTietO>
    /** conSong = false: xác nhận tử vong (bắt buộc có ngayMat). conSong = true: hủy xác nhận. Admin/Quản lý. */
    fun datTuVong(idO: Int, conSong: Boolean, ngayMat: java.time.LocalDate?): Kq<OThanhVien>
    /** Chỉ Admin. */
    fun datTruongToc(idO: Int, laTruongToc: Boolean): Kq<OThanhVien>
    /** Admin/Quản lý. Chưa xóa gì, chỉ tính tác động. */
    fun xemTruocXoaO(idO: Int): Kq<TacDongXoa>
    /** Xóa chỉ Ô này, các dòng quan hệ nối tới nó và dữ liệu riêng của nó (QUYET_DINH mục 5.5). */
    fun xoaO(idO: Int): Kq<Unit>
}
