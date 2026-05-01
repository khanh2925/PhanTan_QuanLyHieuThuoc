package dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;

public class ChiTietKhuyenMaiSanPhamDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String maKM;
    private String tenKM;
    private String maSanPham;
    private String tenSanPham;
    private String hinhThuc;
    private double giaTri;
    private String ngayBatDau;
    private String ngayKetThuc;
    private boolean dangHoatDong;

    public ChiTietKhuyenMaiSanPhamDTO() {}

    public static ChiTietKhuyenMaiSanPhamDTO fromEntity(ChiTietKhuyenMaiSanPham ct) {
        ChiTietKhuyenMaiSanPhamDTO dto = new ChiTietKhuyenMaiSanPhamDTO();
        KhuyenMai km = ct.getKhuyenMai();
        SanPham sp = ct.getSanPham();
        dto.maKM = km != null ? km.getMaKM() : "";
        dto.tenKM = km != null ? km.getTenKM() : "";
        dto.hinhThuc = km != null && km.getHinhThuc() != null ? km.getHinhThuc().name() : "";
        dto.giaTri = km != null ? km.getGiaTri() : 0;
        dto.ngayBatDau = km != null && km.getNgayBatDau() != null ? km.getNgayBatDau().format(DATE_FORMAT) : "";
        dto.ngayKetThuc = km != null && km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(DATE_FORMAT) : "";
        dto.dangHoatDong = km != null && km.isDangHoatDong();
        dto.maSanPham = sp != null ? sp.getMaSanPham() : "";
        dto.tenSanPham = sp != null ? sp.getTenSanPham() : "";
        return dto;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }

    public double getGiaTri() { return giaTri; }
    public void setGiaTri(double giaTri) { this.giaTri = giaTri; }

    public String getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(String ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public String getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(String ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public boolean isDangHoatDong() { return dangHoatDong; }
    public void setDangHoatDong(boolean dangHoatDong) { this.dangHoatDong = dangHoatDong; }
}