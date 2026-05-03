package dto;

import java.io.Serializable;

/**
 * DTO cho san pham - dung de hien thi, tim kiem va gui qua socket.
 */
public class SanPhamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maSanPham;
    private String tenSanPham;
    private String loaiSanPham;
    private String duongDung;
    private String soDangKy;
    private double giaNhap;
    private double giaBan;
    private String hinhAnh;
    private String keBanSanPham;
    private boolean hoatDong;
    private int tongTonKho;
    private String tenKhuyenMai;
    private double phanTramGiam;

    public SanPhamDTO() {}

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public String getLoaiSanPham() { return loaiSanPham; }
    public void setLoaiSanPham(String loaiSanPham) { this.loaiSanPham = loaiSanPham; }

    public String getDuongDung() { return duongDung; }
    public void setDuongDung(String duongDung) { this.duongDung = duongDung; }

    public String getSoDangKy() { return soDangKy; }
    public void setSoDangKy(String soDangKy) { this.soDangKy = soDangKy; }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getKeBanSanPham() { return keBanSanPham; }
    public void setKeBanSanPham(String keBanSanPham) { this.keBanSanPham = keBanSanPham; }

    public boolean isHoatDong() { return hoatDong; }
    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }

    public int getTongTonKho() { return tongTonKho; }
    public void setTongTonKho(int tongTonKho) { this.tongTonKho = tongTonKho; }

    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }
}