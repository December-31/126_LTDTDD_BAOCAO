package com.example.giapha.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Danh sách "Quan hệ đã ẩn" (GD-20): đếm đúng số đường, nét T chung của hai người đã cưới nhau tính một. */
class QuanHeAnTest {

    private fun ds(repo: FakeRepository) = danhSachDuongAn((repo.duLieuCay(1) as Kq.Ok).giaTri)

    private fun admin(): FakeRepository =
        FakeRepository().also { assertTrue(it.dangNhap("0905222444", SampleData.OTP).laOk) }

    @Test
    fun duLieuMauCoMotDuongAn() {
        val d = ds(admin())
        assertEquals(1, d.size)                           // Nguyễn Công Đạt: hai dòng cha/mẹ, hai phụ huynh đã cưới nhau nên tính một đường
        assertEquals(2, d[0].idPhuHuynh.size)
        assertTrue(d[0].tenCap.startsWith("Nguyễn Công Đạt — "))
        assertEquals("Cha/mẹ–con · Ruột", d[0].loai)
    }

    @Test
    fun anHonNhanVaChaMeConThemDong() {
        val repo = admin()
        repo.anHonNhan(5, true)
        assertEquals(2, ds(repo).size)
        repo.anQuanHeChaMe(18, listOf(8, 9), true)        // Khôi – Nam & Hạnh (hai người đã cưới nhau): một đường
        val d = ds(repo)
        assertEquals(3, d.size)
        assertEquals(1, d.count { it.idHonNhan != null })
        assertEquals("Hôn nhân · Đang kết hôn", d.first { it.idHonNhan != null }.loai)
    }

    @Test
    fun hienLaiThiDongBienMat() {
        val repo = admin()
        val dong = ds(repo)[0]
        assertTrue(repo.anQuanHeChaMe(dong.idCon!!, dong.idPhuHuynh, false).laOk)
        assertTrue(ds(repo).isEmpty())
    }
}