package service;

import java.util.List;
import dto.NhanVienDTO;

public interface NhanVienService {
    List<NhanVienDTO> layTatCaNhanVien();
    NhanVienDTO timNhanVienTheoMa(String maNhanVien);
    List<NhanVienDTO> timNhanVien(String tuKhoa);
    List<NhanVienDTO> timNhanVienTheoSoDienThoai(String soDienThoai);
    boolean themNhanVien(NhanVienDTO nv);
    boolean capNhatNhanVien(NhanVienDTO nv);
    boolean xoaNhanVien(String maNhanVien);
    boolean capNhatTrangThai(String maNhanVien, boolean trangThai);
    String taoMaNhanVienTuDong();
    void refreshCache();
}