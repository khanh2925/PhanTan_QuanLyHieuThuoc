package dto;

import java.io.Serializable;

import entity.ChiTietPhieuNhap;

public class ChiTietPhieuNhapDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuNhap;
    private String maLo;
    private String maSanPham;
    private String tenSanPham;
    private String hanSuDung;
    private int soLuongTon;
    private String maDonViTinh;
    private String tenDonViTinh;
    private int soLuongNhap;
    private double donGiaNhap;
    private double thanhTien;

    public ChiTietPhieuNhapDTO() {}

    public static ChiTietPhieuNhapDTO fromEntity(ChiTietPhieuNhap ct) {
        ChiTietPhieuNhapDTO dto = new ChiTietPhieuNhapDTO();
        dto.maPhieuNhap = ct.getPhieuNhap() != null ? ct.getPhieuNhap().getMaPhieuNhap() : "";
        dto.maLo = ct.getLoSanPham() != null ? ct.getLoSanPham().getMaLo() : "";
        dto.maSanPham = ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null
                ? ct.getLoSanPham().getSanPham().getMaSanPham() : "";
        dto.tenSanPham = ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null
                ? ct.getLoSanPham().getSanPham().getTenSanPham() : "";
        dto.hanSuDung = ct.getLoSanPham() != null && ct.getLoSanPham().getHanSuDung() != null
                ? ct.getLoSanPham().getHanSuDung().toString() : "";
        dto.soLuongTon = ct.getLoSanPham() != null ? ct.getLoSanPham().getSoLuongTon() : 0;
        dto.maDonViTinh = ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh() : "";
        dto.tenDonViTinh = ct.getDonViTinh() != null ? ct.getDonViTinh().getTenDonViTinh() : "";
        dto.soLuongNhap = ct.getSoLuongNhap();
        dto.donGiaNhap = ct.getDonGiaNhap();
        dto.thanhTien = ct.getThanhTien();
        return dto;
    }

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }

    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public String getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(String hanSuDung) { this.hanSuDung = hanSuDung; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public String getMaDonViTinh() { return maDonViTinh; }
    public void setMaDonViTinh(String maDonViTinh) { this.maDonViTinh = maDonViTinh; }

    public String getTenDonViTinh() { return tenDonViTinh; }
    public void setTenDonViTinh(String tenDonViTinh) { this.tenDonViTinh = tenDonViTinh; }

    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public double getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}