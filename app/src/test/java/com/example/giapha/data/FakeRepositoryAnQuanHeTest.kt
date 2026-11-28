package com.example.giapha.data

import com.example.giapha.tree.LayoutEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Ẩn/hiện lại quan hệ qua FakeRepository, rồi dựng cây bằng LayoutEngine (chạy bằng JVM). */
class FakeRepositoryAnQuanHeTest {

    private fun dangNhap(repo: FakeRepository, sdt: String) {
        assertTrue(repo.dangNhap(sdt, SampleData.OTP).laOk)
    }

    private fun soThe(repo: FakeRepository): Int =
        LayoutEngine.tinh((repo.duLieuCay(1) as Kq.Ok).giaTri).the.size

    @Test
    fun adminAnRoiHienLaiHonNhan() {
        val repo = FakeRepository()
        val goc = soThe(repo)
        assertTrue(repo.anHonNhan(5, true).laOk)
        assertEquals(goc - 5, soThe(repo))      // Hạnh, Khôi, Thảo, An, Gia Hân
        assertTrue(repo.anHonNhan(5, false).laOk)
        assertEquals(goc, soThe(repo))
    }

    @Test
    fun anDuongChaMeConChiDoiHienThi() {
        val repo = FakeRepository()
        dangNhap(repo, "0905222444")
        val truoc = (repo.duLieuCay(1) as Kq.Ok).giaTri.quanHeChaMe.size
        val theGoc = soThe(repo)
        assertTrue(repo.anQuanHeChaMe(18, listOf(8, 9), true).laOk)       // Khôi – Nam & Hạnh
        val du = (repo.duLieuCay(1) as Kq.Ok).giaTri
        assertEquals(truoc, du.quanHeChaMe.size)                    // dữ liệu vẫn nguyên
        assertTrue(du.quanHeChaMe.filter { it.idCon == 18 }.none { it.hienThiLienKet })
        assertEquals(theGoc - 3, LayoutEngine.tinh(du).the.size)
        assertTrue(repo.anQuanHeChaMe(18, listOf(8, 9), false).laOk)
        assertTrue((repo.duLieuCay(1) as Kq.Ok).giaTri.quanHeChaMe.filter { it.idCon == 18 }.all { it.hienThiLienKet })
    }


    @Test
    fun khachTamKhongAnDuoc() {
        val repo = FakeRepository()
        dangNhap(repo, "0933456789")                 // Khách tạm họ Nguyễn
        val r = repo.anHonNhan(5, true)
        assertTrue(r is Kq.Loi && r.loi == LoiApp.KHONG_CO_QUYEN)
        val r2 = repo.anQuanHeChaMe(18, listOf(8, 9), true)
        assertTrue(r2 is Kq.Loi && r2.loi == LoiApp.KHONG_CO_QUYEN)
    }

    @Test
    fun khongTimThayQuanHe() {
        val repo = FakeRepository()
        dangNhap(repo, "0905222444")
        assertTrue(repo.anHonNhan(9999, true) is Kq.Loi)
        assertTrue(repo.anQuanHeChaMe(18, listOf(1), true) is Kq.Loi)        // 1 không phải cha mẹ của 18
    }
}