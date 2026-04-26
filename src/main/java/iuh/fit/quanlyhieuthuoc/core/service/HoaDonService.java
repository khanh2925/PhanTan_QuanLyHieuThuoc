package iuh.fit.quanlyhieuthuoc.core.service;

import java.time.LocalDate;
import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.HoaDon;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietHoaDon;

public interface HoaDonService {
    List<HoaDon> layTatCaHoaDon();
    HoaDon layHoaDonTheoMa(String maHoaDon);
    List<HoaDon> layHoaDonTheoNgay(LocalDate ngay);
    List<HoaDon> layHoaDonTheoKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay);
    List<HoaDon> layHoaDonTheoNhanVien(String maNhanVien);
    List<HoaDon> layHoaDonTheoKhachHang(String maKhachHang);
    boolean themHoaDon(HoaDon hd);
    boolean capNhatHoaDon(HoaDon hd);
    List<ChiTietHoaDon> layChiTietHoaDon(String maHoaDon);
    String taoMaHoaDonTuDong();
    void refreshCache();
}
