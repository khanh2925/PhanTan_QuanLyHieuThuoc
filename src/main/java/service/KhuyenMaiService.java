package service;

import java.util.List;

import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.KhuyenMaiDTO;

public interface KhuyenMaiService {
    List<KhuyenMaiDTO> layTatCaKhuyenMai();
    KhuyenMaiDTO layKhuyenMaiTheoMa(String maKM);
    List<KhuyenMaiDTO> layKhuyenMaiDangHoatDong();
    List<ChiTietKhuyenMaiSanPhamDTO> layChiTietKhuyenMaiTheoMaKM(String maKM);
    boolean themKhuyenMai(KhuyenMaiDTO km);
    boolean themChiTietKhuyenMaiSanPham(String maKM, String maSanPham);
    boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSanPham);
    boolean capNhatKhuyenMai(KhuyenMaiDTO km);
    boolean xoaKhuyenMai(String maKM);
    String taoMaKhuyenMaiTuDong();
    void refreshCache();
}
