package service;

import java.time.LocalDate;
import java.util.List;
import dto.ChiTietPhieuNhapDTO;
import dto.PhieuNhapDTO;

public interface PhieuNhapService {
    List<PhieuNhapDTO> layTatCaPhieuNhap();
    PhieuNhapDTO layPhieuNhapTheoMa(String maPhieuNhap);
    List<PhieuNhapDTO> layPhieuNhapTheoNgay(LocalDate ngay);
    List<PhieuNhapDTO> layPhieuNhapTheoNhaCungCap(String maNhaCungCap);
    boolean themPhieuNhap(PhieuNhapDTO pn);
    boolean capNhatPhieuNhap(PhieuNhapDTO pn);
    List<ChiTietPhieuNhapDTO> layChiTietPhieuNhap(String maPhieuNhap);
    String taoMaPhieuNhapTuDong();
    void refreshCache();
}