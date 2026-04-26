package iuh.fit.quanlyhieuthuoc.core.service.impl;

import iuh.fit.quanlyhieuthuoc.core.entity.TaiKhoan;
import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;
import iuh.fit.quanlyhieuthuoc.core.entity.Session;
import iuh.fit.quanlyhieuthuoc.core.repository.TaiKhoanRepository;
import iuh.fit.quanlyhieuthuoc.core.service.TaiKhoanService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.TaiKhoanRepositoryImpl;

public class TaiKhoanServiceImpl implements TaiKhoanService {
    
    private final TaiKhoanRepository taiKhoanRepository;
    
    public TaiKhoanServiceImpl() {
        this.taiKhoanRepository = new TaiKhoanRepositoryImpl();
    }
    
    @Override
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = taiKhoanRepository.dangNhap(tenDangNhap, matKhau);
        if (tk != null) {
            Session.getInstance().setTaiKhoanDangNhap(tk);
        }
        return tk;
    }
    
    @Override
    public boolean doiMatKhau(String maTaiKhoan, String matKhauCu, String matKhauMoi) {
        // Trong thực tế cần kiểm tra matKhauCu, nhưng do repo chỉ hỗ trợ cập nhật thẳng
        return taiKhoanRepository.capNhatMatKhau(maTaiKhoan, matKhauMoi);
    }
    
    @Override
    public boolean taoTaiKhoan(TaiKhoan tk) {
        return taiKhoanRepository.themTaiKhoan(tk);
    }
    
    @Override
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        return taiKhoanRepository.capNhatTaiKhoan(tk);
    }
    
    @Override
    public TaiKhoan layTaiKhoanTheoNhanVien(String maNhanVien) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public NhanVien layNhanVienDangNhap() {
        TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
        return tk != null ? tk.getNhanVien() : null;
    }
    
    @Override
    public void dangXuat() {
        Session.getInstance().clearSession();
    }
}
