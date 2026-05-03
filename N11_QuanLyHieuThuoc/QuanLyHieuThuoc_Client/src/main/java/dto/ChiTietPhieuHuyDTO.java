package dto;

import java.io.Serializable;

public class ChiTietPhieuHuyDTO implements Serializable {
    private String maPhieuHuy;
    private String maLo;
    private LoSanPhamDTO loSanPham;
    private int soLuongHuy;
    private String lyDoChiTiet;
    private double donGiaNhap;
    private double thanhTien;
    private int trangThai;
    private DonViTinhDTO donViTinh;

    public ChiTietPhieuHuyDTO() {}

    public String getMaPhieuHuy() { return maPhieuHuy; }
    public void setMaPhieuHuy(String maPhieuHuy) { this.maPhieuHuy = maPhieuHuy; }
    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }
    public LoSanPhamDTO getLoSanPham() { return loSanPham; }
    public void setLoSanPham(LoSanPhamDTO loSanPham) { this.loSanPham = loSanPham; }
    public int getSoLuongHuy() { return soLuongHuy; }
    public void setSoLuongHuy(int soLuongHuy) { this.soLuongHuy = soLuongHuy; }
    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public void setLyDoChiTiet(String lyDoChiTiet) { this.lyDoChiTiet = lyDoChiTiet; }
    public double getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }
    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
    public DonViTinhDTO getDonViTinh() { return donViTinh; }
    public void setDonViTinh(DonViTinhDTO donViTinh) { this.donViTinh = donViTinh; }
}
