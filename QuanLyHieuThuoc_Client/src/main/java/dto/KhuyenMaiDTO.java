package dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class KhuyenMaiDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String maKM;
    private String tenKM;
    private String ngayBatDau;
    private String ngayKetThuc;
    private boolean trangThai;
    private boolean khuyenMaiHoaDon;
    private String loaiKhuyenMai;
    private String hinhThuc;
    private double giaTri;
    private double dieuKienApDungHoaDon;
    private int soLuongKhuyenMai;
    private boolean dangHoatDong;

    public KhuyenMaiDTO() {}

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }

    public String getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(String ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public String getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(String ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public boolean isKhuyenMaiHoaDon() { return khuyenMaiHoaDon; }
    public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) { this.khuyenMaiHoaDon = khuyenMaiHoaDon; }

    public String getLoaiKhuyenMai() { return loaiKhuyenMai; }
    public void setLoaiKhuyenMai(String loaiKhuyenMai) { this.loaiKhuyenMai = loaiKhuyenMai; }

    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }

    public double getGiaTri() { return giaTri; }
    public void setGiaTri(double giaTri) { this.giaTri = giaTri; }

    public double getDieuKienApDungHoaDon() { return dieuKienApDungHoaDon; }
    public void setDieuKienApDungHoaDon(double dieuKienApDungHoaDon) { this.dieuKienApDungHoaDon = dieuKienApDungHoaDon; }

    public int getSoLuongKhuyenMai() { return soLuongKhuyenMai; }
    public void setSoLuongKhuyenMai(int soLuongKhuyenMai) { this.soLuongKhuyenMai = soLuongKhuyenMai; }

    public boolean isDangHoatDong() { return dangHoatDong; }
    public void setDangHoatDong(boolean dangHoatDong) { this.dangHoatDong = dangHoatDong; }
}