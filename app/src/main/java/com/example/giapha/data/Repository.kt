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
}
