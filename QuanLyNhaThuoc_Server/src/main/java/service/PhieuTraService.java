package service;

import java.util.List;

import dto.ChiTietPhieuTraDTO;
import dto.PhieuTraDTO;
import entity.ChiTietPhieuTra;
import entity.NhanVien;
import entity.PhieuTra;

public interface PhieuTraService {
    List<PhieuTraDTO> layTatCaPhieuTra();
    PhieuTra layPhieuTraTheoMa(String maPhieuTra);
    PhieuTraDTO layPhieuTraDTOTheoMa(String maPhieuTra);
    List<PhieuTraDTO> timPhieuTraTheoSoDienThoai(String sdt);
    List<PhieuTraDTO> timPhieuTraTheoKeyword(String keyword);
    boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet);
    List<ChiTietPhieuTraDTO> timKiemChiTietBangMaPhieuTra(String maPhieuTra);
    String capNhatTrangThaiGiaoDich(String maPhieuTra, String maHoaDon, String maLo, String maDonViTinh, NhanVien nhanVien, int trangThaiMoi);
    boolean capNhatTrangThaiPhieuTra(String maPhieuTra, boolean daDuyet);
    String taoMaPhieuTra();
    int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien);
    int tongSoLuongDaTra(String maHD, String maLo);
    boolean daTraLoTrongHoaDon(String maHD, String maLo);
}
