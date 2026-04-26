package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.time.LocalDate;
import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.HoaDon;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietHoaDon;
import iuh.fit.quanlyhieuthuoc.core.repository.HoaDonRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietHoaDonRepository;
import iuh.fit.quanlyhieuthuoc.core.service.HoaDonService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.HoaDonRepositoryImpl;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.ChiTietHoaDonRepositoryImpl;

public class HoaDonServiceImpl implements HoaDonService {
    
    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    
    public HoaDonServiceImpl() {
        this.hoaDonRepository = new HoaDonRepositoryImpl();
        this.chiTietHoaDonRepository = new ChiTietHoaDonRepositoryImpl();
    }
    
    @Override
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonRepository.layTatCaHoaDon();
    }
    
    @Override
    public HoaDon layHoaDonTheoMa(String maHoaDon) {
        return hoaDonRepository.timHoaDonTheoMa(maHoaDon);
    }
    
    @Override
    public List<HoaDon> layHoaDonTheoNgay(LocalDate ngay) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public List<HoaDon> layHoaDonTheoKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public List<HoaDon> layHoaDonTheoNhanVien(String maNhanVien) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public List<HoaDon> layHoaDonTheoKhachHang(String maKhachHang) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public boolean themHoaDon(HoaDon hd) {
        return hoaDonRepository.themHoaDon(hd);
    }
    
    @Override
    public boolean capNhatHoaDon(HoaDon hd) {
        throw new UnsupportedOperationException("Chưa được hỗ trợ ở Repository");
    }
    
    @Override
    public List<ChiTietHoaDon> layChiTietHoaDon(String maHoaDon) {
        return chiTietHoaDonRepository.layDanhSachChiTietTheoMaHD(maHoaDon);
    }
    
    @Override
    public String taoMaHoaDonTuDong() {
        return hoaDonRepository.taoMaHoaDon();
    }
    
    @Override
    public void refreshCache() {
        hoaDonRepository.refreshCache();
    }
}
