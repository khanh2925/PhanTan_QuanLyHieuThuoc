package dao;

import entity.LoSanPham;
import entity.LoaiSanPham;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface LoSanPhamDao {

    void clearCache();

    ArrayList<LoSanPham> layTatCaLoSanPham();

    boolean themLoSanPham(LoSanPham lo);

    boolean capNhatLoSanPham(LoSanPham lo);

    boolean xoaLoSanPham(String maLo);

    LoSanPham timLoTheoMa(String maLo);

    List<LoSanPham> layDanhSachLoTheoMaSanPham(String maSanPham);

    LoSanPham timLoGanHetHanTheoSanPham(String maSanPham);

    LoSanPham timLoKeTiepTheoSanPham(String maSanPham, LocalDate hanSuDungHienTai);

    int tinhSoLuongTonThucTe(String maLo);

    String taoMaLoTuDong();

    List<LoSanPham> timLoDaHetHanTheoLoai(LoaiSanPham loaiSanPham);

    Map<LoaiSanPham, Integer> thongKeSoLoDaHetHanTheoHSDTheoLoai();

    List<LoSanPham> layDanhSachLoSPToiHanSuDung();

    List<LoSanPham> layDanhSachLoSPDaHetHan();

    List<LoSanPham> timLoSanPhamTheoKeyword(String keyword);

    boolean kiemTraLoToiHan(LoSanPham lo);
}
