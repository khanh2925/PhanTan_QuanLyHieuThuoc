package iuh.fit.quanlyhieuthuoc.core.service;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;

public interface NhanVienService {
    List<NhanVien> layTatCaNhanVien();
    NhanVien timNhanVienTheoMa(String maNhanVien);
    List<NhanVien> timNhanVien(String tuKhoa);
    List<NhanVien> timNhanVienTheoSoDienThoai(String soDienThoai);
    boolean themNhanVien(NhanVien nv);
    boolean capNhatNhanVien(NhanVien nv);
    boolean xoaNhanVien(String maNhanVien);
    boolean capNhatTrangThai(String maNhanVien, boolean trangThai);
    String taoMaNhanVienTuDong();
    void refreshCache();
}
