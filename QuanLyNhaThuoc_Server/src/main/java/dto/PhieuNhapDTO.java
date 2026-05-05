package dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String maPhieuNhap;
    private String ngayNhap;
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String maNhanVien;
    private String tenNhanVien;
    private double tongTien;
    private int soDongChiTiet;
    private List<ChiTietPhieuNhapDTO> chiTietList;

    public PhieuNhapDTO() {
        this.chiTietList = new ArrayList<>();
    }

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }

    public String getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(String ngayNhap) { this.ngayNhap = ngayNhap; }

    public String getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(String maNhaCungCap) { this.maNhaCungCap = maNhaCungCap; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public int getSoDongChiTiet() { return soDongChiTiet; }
    public void setSoDongChiTiet(int soDongChiTiet) { this.soDongChiTiet = soDongChiTiet; }

    public List<ChiTietPhieuNhapDTO> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuNhapDTO> chiTietList) {
        this.chiTietList = chiTietList != null ? chiTietList : new ArrayList<>();
    }
}