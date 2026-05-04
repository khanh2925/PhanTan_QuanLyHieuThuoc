package service;

import java.util.List;

import entity.QuyCachDongGoi;

public interface QuyCachDongGoiService {
    List<QuyCachDongGoi> layTatCaQuyCachDongGoi();
    String taoMaQuyCach();
    boolean themQuyCachDongGoi(QuyCachDongGoi quyCach);
    boolean capNhatQuyCachDongGoi(QuyCachDongGoi quyCach);
    QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham);
    List<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham);
    QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh);
    boolean xoaQuyCachDongGoi(String maQuyCach);
}
