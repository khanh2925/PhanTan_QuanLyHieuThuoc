package service;

import java.util.List;

import dto.KhachHangDTO;
import entity.KhachHang;

public interface KhachHangService {
    List<KhachHang> layTatCaKhachHang();
    KhachHangDTO timKhachHangTheoMa(String maKhachHang);
    KhachHangDTO timKhachHangTheoSoDienThoai(String soDienThoai);
    List<KhachHangDTO> timKiemKhachHang(String tuKhoa);
    boolean themKhachHang(KhachHangDTO khachHang);
    boolean capNhatKhachHang(KhachHangDTO khachHang);
    boolean xoaKhachHang(String maKhachHang);
    String taoMaKhachHangTuDong();
    void refreshCache();
}
