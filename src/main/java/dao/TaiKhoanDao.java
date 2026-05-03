package dao;

import java.time.LocalDate;
import java.util.ArrayList;

import entity.TaiKhoan;

/**
 * Dao interface for TaiKhoan entity
 */
public interface TaiKhoanDao {

    /**
     * Lấy toàn bộ tài khoản (kèm thông tin nhân viên)
     */
    ArrayList<TaiKhoan> layTatCaTaiKhoan();

    /**
     * Thêm tài khoản mới
     */
    boolean themTaiKhoan(TaiKhoan tk);

    /**
     * Cập nhật thông tin tài khoản (tên đăng nhập + mật khẩu)
     */
    boolean capNhatTaiKhoan(TaiKhoan tk);

    /**
     * Cập nhật mật khẩu riêng
     */
    boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi);

    /**
     * Xóa tài khoản
     */
    boolean xoaTaiKhoan(String maTaiKhoan);

    /**
     * Kiểm tra đăng nhập
     */
    TaiKhoan dangNhap(String tenDangNhap, String matKhau);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại
     */
    boolean kiemTraTenDangNhapTonTai(String tenDangNhap);

    /**
     * Lấy tài khoản theo mã (kèm nhân viên)
     */
    TaiKhoan layTaiKhoanTheoMa(String maTaiKhoan);

    /**
     * Tạo mã tài khoản tự động
     */
    String taoMaTaiKhoanTuDong();

    /**
     * Tìm Mã Tài Khoản dựa trên thông tin xác thực nhân viên (Quên mật khẩu)
     */
    String timTaiKhoanQuenMK(String maNV, String tenNV, String sdt, LocalDate ngaySinh);
}
