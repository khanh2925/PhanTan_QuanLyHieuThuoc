package service;

import java.util.List;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.SanPhamDTO;
import entity.LoaiSanPham;

public interface SanPhamService {
    List<SanPhamDTO> layTatCaSanPham();
    SanPhamDTO laySanPhamTheoMa(String maSanPham);
    SanPhamDTO timSanPhamTheoSoDangKy(String soDangKy);
    List<SanPhamDTO> timKiemSanPham(String tuKhoa);
    List<SanPhamDTO> laySanPhamTheoLoai(LoaiSanPham loaiSP);
    boolean themSanPham(SanPhamDTO sp);
    boolean capNhatSanPham(SanPhamDTO sp);
    boolean xoaSanPham(String maSanPham);
    List<ChiTietKhuyenMaiSanPhamDTO> layKhuyenMaiDangApDungChoSanPham(String maSanPham);
    void refreshCache();
}