package iuh.fit.quanlyhieuthuoc.core.service;

import iuh.fit.quanlyhieuthuoc.core.entity.TaiKhoan;
import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;

public interface TaiKhoanService {
    TaiKhoan dangNhap(String tenDangNhap, String matKhau);
    boolean doiMatKhau(String maTaiKhoan, String matKhauCu, String matKhauMoi);
    boolean taoTaiKhoan(TaiKhoan tk);
    boolean capNhatTaiKhoan(TaiKhoan tk);
    TaiKhoan layTaiKhoanTheoNhanVien(String maNhanVien);
    NhanVien layNhanVienDangNhap();
    void dangXuat();
}
