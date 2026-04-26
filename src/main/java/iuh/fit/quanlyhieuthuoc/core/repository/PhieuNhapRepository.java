package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.PhieuNhap;

import java.util.Date;
import java.util.List;

public interface PhieuNhapRepository {

    List<PhieuNhap> layDanhSachPhieuNhap();

    PhieuNhap timPhieuNhapTheoMa(String maPhieuNhap);

    boolean themPhieuNhap(PhieuNhap pn);

    String taoMaPhieuNhap();

    List<PhieuNhap> timKiemPhieuNhap(String keyword, Date tuNgay, Date denNgay);

    List<PhieuNhap> layPhieuNhapTheoNhaCungCap(String maNCC);

    double tinhTongTienNhapTheoThang(int thang, int nam);
}
