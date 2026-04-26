package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietHoaDon;

import java.util.List;

public interface ChiTietHoaDonRepository {

    ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT);

    List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD);

    int demSoSanPhamBanHomNay(String maNhanVien);
}
