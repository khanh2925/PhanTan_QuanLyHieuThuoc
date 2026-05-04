package service;

import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

import java.util.List;

public interface PhieuHuyService {
    List<PhieuHuy> layTatCaPhieuHuy();
    PhieuHuy layPhieuHuyTheoMa(String maPhieuHuy);
    List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy);
    boolean themPhieuHuy(PhieuHuy ph);
    String taoMaPhieuHuy();
    boolean kiemTraTrangThai(String maPhieuHuy);
    boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy);
    boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThai);
    int demPhieuHuyChuaDuyet();
    double tinhTongTienHuyTheoThang(int thang, int nam);
    int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien);
    List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy);
}
