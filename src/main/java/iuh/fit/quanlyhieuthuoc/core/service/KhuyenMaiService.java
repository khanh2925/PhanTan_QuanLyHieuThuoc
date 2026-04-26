package iuh.fit.quanlyhieuthuoc.core.service;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.KhuyenMai;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietKhuyenMaiSanPham;

public interface KhuyenMaiService {
    List<KhuyenMai> layTatCaKhuyenMai();
    KhuyenMai layKhuyenMaiTheoMa(String maKM);
    List<KhuyenMai> layKhuyenMaiDangHoatDong();
    List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaKM(String maKM);
    boolean themKhuyenMai(KhuyenMai km);
    boolean capNhatKhuyenMai(KhuyenMai km);
    boolean xoaKhuyenMai(String maKM);
    String taoMaKhuyenMaiTuDong();
    void refreshCache();
}
