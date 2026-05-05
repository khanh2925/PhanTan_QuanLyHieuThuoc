package dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class PhieuHuyDTO implements Serializable {
    private String maPhieuHuy;
    private LocalDate ngayLapPhieu;
    private NhanVienDTO nhanVien;
    private boolean trangThai;
    private double tongTien;
    private List<ChiTietPhieuHuyDTO> chiTietPhieuHuyList;

    public PhieuHuyDTO() {}

    public String getMaPhieuHuy() { return maPhieuHuy; }
    public void setMaPhieuHuy(String maPhieuHuy) { this.maPhieuHuy = maPhieuHuy; }
    public LocalDate getNgayLapPhieu() { return ngayLapPhieu; }
    public void setNgayLapPhieu(LocalDate ngayLapPhieu) { this.ngayLapPhieu = ngayLapPhieu; }
    public NhanVienDTO getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVienDTO nhanVien) { this.nhanVien = nhanVien; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public List<ChiTietPhieuHuyDTO> getChiTietPhieuHuyList() { return chiTietPhieuHuyList; }
    public void setChiTietPhieuHuyList(List<ChiTietPhieuHuyDTO> chiTietPhieuHuyList) { this.chiTietPhieuHuyList = chiTietPhieuHuyList; }
}
