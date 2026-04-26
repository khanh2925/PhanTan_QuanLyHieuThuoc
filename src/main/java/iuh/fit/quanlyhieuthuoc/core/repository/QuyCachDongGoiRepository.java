package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.QuyCachDongGoi;

import java.util.ArrayList;

public interface QuyCachDongGoiRepository {

    ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi();

    String taoMaQuyCach();

    boolean themQuyCachDongGoi(QuyCachDongGoi q);

    boolean capNhatQuyCachDongGoi(QuyCachDongGoi q);

    QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham);

    ArrayList<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham);

    QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh);

    boolean xoaQuyCachDongGoi(String maQuyCach);
}
