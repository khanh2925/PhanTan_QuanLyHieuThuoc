package service;

import java.util.List;
import dto.LoSanPhamDTO;

public interface LoSanPhamService {
    List<LoSanPhamDTO> layTatCaLoSanPham();
    LoSanPhamDTO layLoSanPhamTheoMa(String maLo);
    List<LoSanPhamDTO> layLoSanPhamTheoMaSanPham(String maSanPham);
    List<LoSanPhamDTO> layLoSanPhamConHang(String maSanPham);
    List<LoSanPhamDTO> layLoSanPhamSapHetHan(int soNgay);
    List<LoSanPhamDTO> layLoSanPhamTonKhoThap(int nguongTon);
    boolean themLoSanPham(LoSanPhamDTO lo);
    boolean capNhatLoSanPham(LoSanPhamDTO lo);
    boolean capNhatSoLuongTon(String maLo, int soLuongMoi);
    String taoMaLoTuDong(String maSanPham);
    void refreshCache();
}