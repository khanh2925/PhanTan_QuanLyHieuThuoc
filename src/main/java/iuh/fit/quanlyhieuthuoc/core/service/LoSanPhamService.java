package iuh.fit.quanlyhieuthuoc.core.service;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.LoSanPham;

public interface LoSanPhamService {
    List<LoSanPham> layTatCaLoSanPham();
    LoSanPham layLoSanPhamTheoMa(String maLo);
    List<LoSanPham> layLoSanPhamTheoMaSanPham(String maSanPham);
    List<LoSanPham> layLoSanPhamConHang(String maSanPham);
    List<LoSanPham> layLoSanPhamSapHetHan(int soNgay);
    List<LoSanPham> layLoSanPhamTonKhoThap(int nguongTon);
    boolean themLoSanPham(LoSanPham lo);
    boolean capNhatLoSanPham(LoSanPham lo);
    boolean capNhatSoLuongTon(String maLo, int soLuongMoi);
    String taoMaLoTuDong(String maSanPham);
    void refreshCache();
}
