package dao;

import entity.QuyCachDongGoi;

import java.util.ArrayList;

public interface QuyCachDongGoiDao {

    ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi();

    String taoMaQuyCach();

    boolean themQuyCachDongGoi(QuyCachDongGoi q);

    boolean capNhatQuyCachDongGoi(QuyCachDongGoi q);

    QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham);

    ArrayList<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham);

    QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh);

    boolean xoaQuyCachDongGoi(String maQuyCach);
}
