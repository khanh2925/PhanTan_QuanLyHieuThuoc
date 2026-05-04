package service;

import java.time.LocalDate;
import java.util.List;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonCreateUpdateDTO;
import dto.HoaDonDTO;
public interface HoaDonService {
    List<HoaDonDTO> layTatCaHoaDon();
    HoaDonDTO layHoaDonTheoMa(String maHoaDon);
    List<HoaDonDTO> layHoaDonTheoNgay(LocalDate ngay);
    List<HoaDonDTO> layHoaDonTheoKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay);
    List<HoaDonDTO> layHoaDonTheoNhanVien(String maNhanVien);
    List<HoaDonDTO> layHoaDonTheoKhachHang(String maKhachHang);
    boolean themHoaDon(HoaDonCreateUpdateDTO hd);
    boolean capNhatHoaDon(HoaDonCreateUpdateDTO hd);
    List<ChiTietHoaDonDTO> layChiTietHoaDon(String maHoaDon);
    String taoMaHoaDonTuDong();
    void refreshCache();
}