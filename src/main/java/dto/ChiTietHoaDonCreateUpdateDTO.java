package dto;

import java.io.Serializable;

public class ChiTietHoaDonCreateUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLo;
    private String maDonViTinh;
    private double soLuong;
    private double giaBan;
    private String maKhuyenMai;

    public ChiTietHoaDonCreateUpdateDTO() {}

    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }

    public String getMaDonViTinh() { return maDonViTinh; }
    public void setMaDonViTinh(String maDonViTinh) { this.maDonViTinh = maDonViTinh; }

    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
}