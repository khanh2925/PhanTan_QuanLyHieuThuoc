package dao;

import entity.ChiTietPhieuNhap;

import java.util.List;

public interface ChiTietPhieuNhapDao {

    List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap);
}
