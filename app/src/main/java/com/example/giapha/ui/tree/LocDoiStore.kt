package com.example.giapha.ui.tree

import android.content.Context

class LocDoiStore(context: Context) {

    private val sp = context.applicationContext.getSharedPreferences("loc_so_doi", Context.MODE_PRIVATE)

    private fun khoa(idTaiKhoan: Int, idKhongGian: Int) = "n_${idTaiKhoan}_$idKhongGian"

    fun doc(idTaiKhoan: Int, idKhongGian: Int): Int? =
        sp.getInt(khoa(idTaiKhoan, idKhongGian), 0).takeIf { it >= 1 }

    /** n = null: xóa, không lọc nữa. */
    fun luu(idTaiKhoan: Int, idKhongGian: Int, n: Int?) {
        val e = sp.edit()
        if (n == null) e.remove(khoa(idTaiKhoan, idKhongGian)) else e.putInt(khoa(idTaiKhoan, idKhongGian), n)
        e.apply()
    }
}