package com.example.giapha.data

/** Mã lỗi nghiệp vụ. Màn hình đổi mã này thành câu tiếng Việt hoặc dùng luôn `thongBao`. */
enum class LoiApp {
    MAT_KET_NOI,          // dùng để thử trạng thái lỗi (FakeRepository.giaLapLoi)
    CHUA_DANG_NHAP,
    DU_LIEU_KHONG_HOP_LE,
    OTP_SAI,
    DA_TON_TAI,           // SĐT đã đăng ký
    CHUA_DANG_KY,         // SĐT chưa có tài khoản
    TAI_KHOAN_BI_KHOA,    // AdminApp đã cấm (toàn hệ thống)
    KHONG_TIM_THAY,
    KHONG_CO_QUYEN,
    MA_KHONG_TON_TAI,
    MA_DA_DUNG,
    MA_HET_HAN,
    KHONG_GIAN_RIENG_TU,
    DA_BI_CHAN            // bị chặn trong không gian (BiCam)
}

/** Kết quả một lời gọi Repository: thành công kèm dữ liệu, hoặc lỗi kèm thông báo tiếng Việt. */
sealed class Kq<out T> {
    data class Ok<out T>(val giaTri: T) : Kq<T>()
    data class Loi(val loi: LoiApp, val thongBao: String) : Kq<Nothing>()

    val laOk: Boolean get() = this is Ok

    inline fun khiOk(hanhDong: (T) -> Unit): Kq<T> {
        if (this is Ok) hanhDong(giaTri)
        return this
    }

    inline fun khiLoi(hanhDong: (Loi) -> Unit): Kq<T> {
        if (this is Loi) hanhDong(this)
        return this
    }
}
