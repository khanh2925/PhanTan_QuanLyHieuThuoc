package service;

import java.util.List;

import entity.KhachHang;

public interface KhachHangService {
    List<KhachHang> layTatCaKhachHang();
    KhachHang layKhachHangTheoMa(String maKhachHang);
    KhachHang layKhachHangTheoSDT(String soDienThoai);
    boolean themKhachHang(KhachHang khachHang);
    boolean capNhatKhachHang(KhachHang khachHang);
    boolean xoaKhachHang(String maKhachHang);
    String phatSinhMaKhachHangTiepTheo();
    void refreshCache();
}
