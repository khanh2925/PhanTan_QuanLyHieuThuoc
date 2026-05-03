package dto;

import java.io.Serializable;

public class PhieuTraDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuTra;
    private String maKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String maNhanVien;
    private String tenNhanVien;
    private String ngayLap;
    private boolean trangThai;
    private double tongTienHoan;

    public PhieuTraDTO() {}

    public String getMaPhieuTra() { return maPhieuTra; }
    public void setMaPhieuTra(String maPhieuTra) { this.maPhieuTra = maPhieuTra; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getNgayLap() { return ngayLap; }
    public void setNgayLap(String ngayLap) { this.ngayLap = ngayLap; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public double getTongTienHoan() { return tongTienHoan; }
    public void setTongTienHoan(double tongTienHoan) { this.tongTienHoan = tongTienHoan; }
}
