package com.example.giapha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giapha.ui.common.setup
import com.example.giapha.ui.common.setAction
import com.example.giapha.ui.common.setRoleBadge
import com.example.giapha.ui.common.setBlockedBadge
import com.example.giapha.ui.common.attachPhoneFormat
import com.example.giapha.databinding.ActivityMainBinding
import com.example.giapha.ui.common.showConfirmDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBar.setup(this, "Hồ sơ cá nhân")
        binding.appBar.setAction(this, R.drawable.ic_more_vert, "Menu") { }

        binding.phoneInput.etPhone.attachPhoneFormat()
        binding.otp.onCompleted = { code -> binding.otp.isError = code != "123456" }

        binding.b1.setRoleBadge("Admin")
        binding.b2.setRoleBadge("QuanLy")
        binding.b3.setRoleBadge("ThanhVien")
        binding.b4.setRoleBadge("ThanhVienHo")
        binding.b5.setRoleBadge("KhachTam")
        binding.b6.setBlockedBadge()

        binding.b1.setOnClickListener {
            showConfirmDialog(
                "Xóa Gia phả họ Nguyễn?",
                "Toàn bộ dữ liệu — thành viên, cây gia phả, tiểu sử, bình luận — sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác.",
                "Xóa"
            ) { }
        }
        binding.b3.setOnClickListener {
            showConfirmDialog("Cấp quyền Quản lý?", "Nguyễn Minh Khôi sẽ có thể chỉnh sửa cây gia phả.", "Cấp", destructive = false) { }
        }

        binding.b2.setOnClickListener { binding.stateView.showLoading() }
        binding.b4.setOnClickListener { binding.stateView.showEmpty("Chưa có thành viên nào.") }
        binding.b5.setOnClickListener { binding.stateView.showError { binding.stateView.showLoading() } }
        binding.b6.setOnClickListener { binding.stateView.hide() }

        binding.pagination.onPageChange = { p -> binding.pagination.setPage(p, 15) }
        binding.pagination.setPage(2, 15)
    }
}