package service.impl;

import dao.TaiKhoanDao;
import dao.iml.TaiKhoanDaoImpl;
import dto.NhanVienDTO;
import dto.TaiKhoanDTO;
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
        TaiKhoanDTO dto = Mapper.map(tk, TaiKhoanDTO.class);
        if (dto != null) {
            Session.getInstance().setTaiKhoanDangNhap(dto);
        }
        return dto;
    }

    @Override
    public boolean doiMatKhau(String maTaiKhoan, String matKhauCu, String matKhauMoi) {
        return taiKhoanDao.capNhatMatKhau(maTaiKhoan, matKhauMoi);
    }

    @Override
    public String timTaiKhoanQuenMatKhau(String maNhanVien, String tenNhanVien, String soDienThoai, java.time.LocalDate ngaySinh) {
        return taiKhoanDao.timTaiKhoanQuenMK(maNhanVien, tenNhanVien, soDienThoai, ngaySinh);
    }

    @Override
    public boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi) {
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
        TaiKhoanDTO tk = Session.getInstance().getTaiKhoanDangNhap();
        if (tk == null) return null;
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNhanVien(tk.getMaNhanVien());
        nv.setTenNhanVien(tk.getTenNhanVien());
        nv.setVaiTro(tk.getVaiTro());
        nv.setTrangThai(tk.isNhanVienDangLam() ? "Đang làm" : "Nghỉ việc");
        return nv;
    }

    @Override
    public void dangXuat() {
        Session.getInstance().clearSession();
    }
}
