package iuh.fit.quanlyhieuthuoc.core.service;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.KhachHang;

public interface KhachHangService {
    List<KhachHang> layTatCaKhachHang();
    KhachHang timKhachHangTheoMa(String maKhachHang);
    KhachHang timKhachHangTheoSoDienThoai(String soDienThoai);
    List<KhachHang> timKiemKhachHang(String tuKhoa);
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean xoaKhachHang(String maKhachHang);
    String taoMaKhachHangTuDong();
    void refreshCache();
}
