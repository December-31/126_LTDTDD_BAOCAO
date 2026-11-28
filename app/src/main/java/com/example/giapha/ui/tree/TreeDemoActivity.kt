package com.example.giapha.ui.tree

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giapha.data.Kq
import com.example.giapha.data.Services
import com.example.giapha.tree.LayoutEngine
import com.example.giapha.tree.TreeViewport

class TreeDemoActivity : AppCompatActivity() {

    companion object { const val EXTRA_ID_KHONG_GIAN = "idKhongGian" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra(EXTRA_ID_KHONG_GIAN, 0)
        val dp = resources.displayMetrics.density

        val khung = TreeViewport(this)
        khung.setBackgroundColor(0xFFFFF8F6.toInt())

        val nutLen = Button(this).apply { text = "+" }
        val nutXuong = Button(this).apply { text = "−" }
        val cum = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(nutLen, LinearLayout.LayoutParams((56 * dp).toInt(), (56 * dp).toInt()))
            addView(nutXuong, LinearLayout.LayoutParams((56 * dp).toInt(), (56 * dp).toInt()))
        }
        val goc = FrameLayout(this).apply {
            addView(khung, FrameLayout.LayoutParams(-1, -1))
            addView(cum, FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM or Gravity.END).apply {
                setMargins(0, 0, (16 * dp).toInt(), (24 * dp).toInt())
            })
        }
        setContentView(goc)

        nutLen.setOnClickListener { khung.zoomLen() }
        nutXuong.setOnClickListener { khung.zoomXuong() }
        khung.khiDoiZoom = { _, len, xuong ->
            nutLen.isEnabled = len
            nutXuong.isEnabled = xuong
        }

        when (val kq = Services.repository.duLieuCay(id)) {
            is Kq.Ok -> {
                val du = kq.giaTri
                val ten = du.oThanhVien.associate { it.idOThanhVien to it.hoTen }
                khung.datDuLieu(LayoutEngine.tinh(du), du.oThanhVien)
                khung.khiChamThe = { ido ->
                    Toast.makeText(this, "TẠM: mở GD-08 của ${ten[ido]} (#$ido)", Toast.LENGTH_SHORT).show()
                }
                title = du.khongGian.tenKhongGian
            }
            is Kq.Loi -> {
                Toast.makeText(this, kq.thongBao, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}