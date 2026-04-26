package iuh.fit.quanlyhieuthuoc.core.service;

import java.time.LocalDate;
import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.PhieuNhap;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuNhap;

public interface PhieuNhapService {
    List<PhieuNhap> layTatCaPhieuNhap();
    PhieuNhap layPhieuNhapTheoMa(String maPhieuNhap);
    List<PhieuNhap> layPhieuNhapTheoNgay(LocalDate ngay);
    List<PhieuNhap> layPhieuNhapTheoNhaCungCap(String maNhaCungCap);
    boolean themPhieuNhap(PhieuNhap pn);
    boolean capNhatPhieuNhap(PhieuNhap pn);
    List<ChiTietPhieuNhap> layChiTietPhieuNhap(String maPhieuNhap);
    String taoMaPhieuNhapTuDong();
    void refreshCache();
}
