package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.KhachHang;
import iuh.fit.quanlyhieuthuoc.core.repository.KhachHangRepository;
import iuh.fit.quanlyhieuthuoc.core.service.KhachHangService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.KhachHangRepositoryImpl;

public class KhachHangServiceImpl implements KhachHangService {
    
    private final KhachHangRepository khachHangRepository;
    
    public KhachHangServiceImpl() {
        this.khachHangRepository = new KhachHangRepositoryImpl();
    }
    
    public KhachHangServiceImpl(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }
    
    @Override
    public List<KhachHang> layTatCaKhachHang() {
        return khachHangRepository.layTatCaKhachHang();
    }
    
    @Override
    public KhachHang timKhachHangTheoMa(String maKhachHang) {
        return khachHangRepository.timKhachHangTheoMa(maKhachHang);
    }
    
    @Override
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        return khachHangRepository.timKhachHangTheoSoDienThoai(soDienThoai);
    }
    
    @Override
    public List<KhachHang> timKiemKhachHang(String tuKhoa) {
        return khachHangRepository.timKhachHang(tuKhoa);
    }
    
    @Override
    public boolean themKhachHang(KhachHang kh) {
        return khachHangRepository.themKhachHang(kh);
    }
    
    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangRepository.capNhatKhachHang(kh);
    }
    
    @Override
    public boolean xoaKhachHang(String maKhachHang) {
        return khachHangRepository.xoaKhachHang(maKhachHang);
    }
    
    @Override
    public String taoMaKhachHangTuDong() {
        return khachHangRepository.phatSinhMaKhachHangTiepTheo();
    }
    
    @Override
    public void refreshCache() {
        khachHangRepository.refreshCache();
    }
}
