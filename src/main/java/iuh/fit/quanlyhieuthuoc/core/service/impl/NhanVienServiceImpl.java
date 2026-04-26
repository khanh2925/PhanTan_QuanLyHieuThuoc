package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;
import iuh.fit.quanlyhieuthuoc.core.repository.NhanVienRepository;
import iuh.fit.quanlyhieuthuoc.core.service.NhanVienService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.NhanVienRepositoryImpl;

public class NhanVienServiceImpl implements NhanVienService {
    
    private final NhanVienRepository nhanVienRepository;
    
    public NhanVienServiceImpl() {
        this.nhanVienRepository = new NhanVienRepositoryImpl();
    }
    
    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }
    
    @Override
    public List<NhanVien> layTatCaNhanVien() {
        return nhanVienRepository.layTatCaNhanVien();
    }
    
    @Override
    public NhanVien timNhanVienTheoMa(String maNhanVien) {
        return nhanVienRepository.timNhanVienTheoMa(maNhanVien);
    }
    
    @Override
    public List<NhanVien> timNhanVien(String tuKhoa) {
        return nhanVienRepository.timNhanVien(tuKhoa);
    }
    
    @Override
    public List<NhanVien> timNhanVienTheoSoDienThoai(String soDienThoai) {
        return nhanVienRepository.timNhanVienTheoSoDienThoai(soDienThoai);
    }
    
    @Override
    public boolean themNhanVien(NhanVien nv) {
        return nhanVienRepository.themNhanVien(nv);
    }
    
    @Override
    public boolean capNhatNhanVien(NhanVien nv) {
        return nhanVienRepository.capNhatNhanVien(nv);
    }
    
    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        return nhanVienRepository.xoaNhanVien(maNhanVien);
    }
    
    @Override
    public boolean capNhatTrangThai(String maNhanVien, boolean trangThai) {
        return nhanVienRepository.capNhatTrangThai(maNhanVien, trangThai);
    }
    
    @Override
    public String taoMaNhanVienTuDong() {
        return nhanVienRepository.taoMaNhanVienTuDong();
    }
    
    @Override
    public void refreshCache() {
        nhanVienRepository.refreshCache();
    }
}
