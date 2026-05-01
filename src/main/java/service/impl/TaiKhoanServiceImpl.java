package service.impl;

import dao.TaiKhoanDao;
import dao.iml.TaiKhoanDaoImpl;
import dto.NhanVienDTO;
import dto.TaiKhoanDTO;
import entity.NhanVien;
import entity.Session;
import entity.TaiKhoan;
import mapper.Mapper;
import service.TaiKhoanService;

public class TaiKhoanServiceImpl implements TaiKhoanService {

    private final TaiKhoanDao taiKhoanDao;

    public TaiKhoanServiceImpl() {
        this.taiKhoanDao = new TaiKhoanDaoImpl();
    }

    @Override
    public TaiKhoanDTO dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = taiKhoanDao.dangNhap(tenDangNhap, matKhau);
        if (tk != null) {
            Session.getInstance().setTaiKhoanDangNhap(tk);
        }
        return Mapper.map(tk, TaiKhoanDTO.class);
    }

    @Override
    public boolean doiMatKhau(String maTaiKhoan, String matKhauCu, String matKhauMoi) {
        return taiKhoanDao.capNhatMatKhau(maTaiKhoan, matKhauMoi);
    }

    @Override
    public boolean taoTaiKhoan(TaiKhoanDTO tk) {
        return taiKhoanDao.themTaiKhoan(Mapper.map(tk, TaiKhoan.class));
    }

    @Override
    public boolean capNhatTaiKhoan(TaiKhoanDTO tk) {
        return taiKhoanDao.capNhatTaiKhoan(Mapper.map(tk, TaiKhoan.class));
    }

    @Override
    public TaiKhoanDTO layTaiKhoanTheoNhanVien(String maNhanVien) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public NhanVienDTO layNhanVienDangNhap() {
        TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
        NhanVien nhanVien = tk != null ? tk.getNhanVien() : null;
        return Mapper.map(nhanVien, NhanVienDTO.class);
    }

    @Override
    public void dangXuat() {
        Session.getInstance().clearSession();
    }
}