package service;

import java.util.List;
import dto.KhachHangDTO;

public interface KhachHangService {
    List<KhachHangDTO> layTatCaKhachHang();
    KhachHangDTO timKhachHangTheoMa(String maKhachHang);
    KhachHangDTO timKhachHangTheoSoDienThoai(String soDienThoai);
    List<KhachHangDTO> timKiemKhachHang(String tuKhoa);
    boolean themKhachHang(KhachHangDTO kh);
    boolean capNhatKhachHang(KhachHangDTO kh);
    boolean xoaKhachHang(String maKhachHang);
    String taoMaKhachHangTuDong();
    void refreshCache();
}