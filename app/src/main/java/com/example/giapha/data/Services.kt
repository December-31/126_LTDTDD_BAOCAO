package com.example.giapha.data

/**
 * Nơi duy nhất tạo Repository. Màn hình gọi: Services.repository.dangNhap(...)
 * Đối tượng khởi tạo lười, một lần cho mỗi lần chạy app, nên dữ liệu thêm/sửa
 * sống cho tới khi tiến trình app bị tắt. Nối backend thì chỉ đổi dòng này.
 */
object Services {
    val repository: Repository by lazy { FakeRepository() }
}
