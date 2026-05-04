package dao;

import entity.ChiTietHoaDon;

import java.util.List;

public interface ChiTietHoaDonDao {

    ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT);

    List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD);

    int demSoSanPhamBanHomNay(String maNhanVien);
}
