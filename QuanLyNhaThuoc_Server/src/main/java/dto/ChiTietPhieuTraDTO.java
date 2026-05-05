package dto;

import java.io.Serializable;

public class ChiTietPhieuTraDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuTra;
    private String maHoaDon;
    private String maLo;
    private String tenSanPham;
    private int soLuong;
    private double thanhTienHoan;
    private String lyDoChiTiet;
    private int trangThai;
    private String maDonViTinh;
    private String tenDonViTinh;

    public ChiTietPhieuTraDTO() {}

    public String getMaPhieuTra() { return maPhieuTra; }
    public void setMaPhieuTra(String maPhieuTra) { this.maPhieuTra = maPhieuTra; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getThanhTienHoan() { return thanhTienHoan; }
    public void setThanhTienHoan(double thanhTienHoan) { this.thanhTienHoan = thanhTienHoan; }

    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public void setLyDoChiTiet(String lyDoChiTiet) { this.lyDoChiTiet = lyDoChiTiet; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public String getMaDonViTinh() { return maDonViTinh; }
    public void setMaDonViTinh(String maDonViTinh) { this.maDonViTinh = maDonViTinh; }

    public String getTenDonViTinh() { return tenDonViTinh; }
    public void setTenDonViTinh(String tenDonViTinh) { this.tenDonViTinh = tenDonViTinh; }
}
