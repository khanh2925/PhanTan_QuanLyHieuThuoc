package iuh.fit.quanlyhieuthuoc.core.service;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.SanPham;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietKhuyenMaiSanPham;
import iuh.fit.quanlyhieuthuoc.core.enums.LoaiSanPham;

public interface SanPhamService {
    List<SanPham> layTatCaSanPham();
    SanPham laySanPhamTheoMa(String maSanPham);
    SanPham timSanPhamTheoSoDangKy(String soDangKy);
    List<SanPham> timKiemSanPham(String tuKhoa);
    List<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP);
    boolean themSanPham(SanPham sp);
    boolean capNhatSanPham(SanPham sp);
    boolean xoaSanPham(String maSanPham);
    List<ChiTietKhuyenMaiSanPham> layKhuyenMaiDangApDungChoSanPham(String maSanPham);
    void refreshCache();
}
