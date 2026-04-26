package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.*;

import java.util.List;

public interface HoaDonRepository {

    HoaDon timHoaDonTheoMa(String maHD);

    List<HoaDon> layTatCaHoaDon();

    boolean themHoaDon(HoaDon hd);

    String taoMaHoaDon();

    List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai);

    double layDoanhThuTheoThang(int thang, int nam);

    void refreshCache();

    int demSoHoaDonTheoThang(int thang, int nam);
}
