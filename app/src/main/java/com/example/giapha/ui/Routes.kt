package com.example.giapha.ui
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.giapha.data.Services
import com.example.giapha.model.LoaiTaiKhoan
import com.example.giapha.model.TaiKhoan
import com.example.giapha.ui.auth.LoginActivity
import com.example.giapha.ui.profile.ProfileActivity
import com.example.giapha.ui.space.JoinCodeActivity
import com.example.giapha.ui.space.SpaceListActivity

/** Mọi lối đi giữa các màn hình. Dòng ghi "TẠM" sẽ được thay khi làm tới màn đó. */
object Routes {

    private fun Activity.tam(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun Activity.moMoi(cls: Class<*>) {
        startActivity(Intent(this, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    fun sauDangNhap(activity: Activity, tk: TaiKhoan) {
        if (tk.loaiTaiKhoan == LoaiTaiKhoan.ADMIN_APP) {
            activity.tam("TẠM: AdminApp → GD-21 (bước 11)")
        } else {
            activity.moMoi(SpaceListActivity::class.java)
        }
    }

    fun dangXuat(activity: Activity) {
        Services.repository.dangXuat()
        activity.moMoi(LoginActivity::class.java)
    }

    fun moKhongGian(activity: Activity, idKhongGian: Int) =
        activity.tam("TẠM: mở cây GD-06 của không gian #$idKhongGian (bước 7)")

    fun moHoSo(activity: Activity) =
        activity.startActivity(Intent(activity, ProfileActivity::class.java))

    fun moNhapMa(activity: Activity) =
        activity.startActivity(Intent(activity, JoinCodeActivity::class.java))
}
