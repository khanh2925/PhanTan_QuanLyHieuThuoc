package service;

import dto.NhanVienDTO;
import dto.TaiKhoanDTO;

public interface TaiKhoanService {
    TaiKhoanDTO dangNhap(String tenDangNhap, String matKhau);
    boolean doiMatKhau(String maTaiKhoan, String matKhauCu, String matKhauMoi);
    String timTaiKhoanQuenMatKhau(String maNhanVien, String tenNhanVien, String soDienThoai, java.time.LocalDate ngaySinh);
    boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi);
    boolean taoTaiKhoan(TaiKhoanDTO tk);
    boolean capNhatTaiKhoan(TaiKhoanDTO tk);
    TaiKhoanDTO layTaiKhoanTheoNhanVien(String maNhanVien);
    NhanVienDTO layNhanVienDangNhap();
    void dangXuat();
}