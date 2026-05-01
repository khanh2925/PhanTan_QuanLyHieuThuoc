package dto;


import java.io.Serializable;
import entity.ChiTietHoaDon;

/**
 * DTO cho chi tiết hóa đơn
 */
public class ChiTietHoaDonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tenSanPham;
    private String maLo;
    private String donViTinh;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private String tenKhuyenMai;
    private double giamGia;

    public ChiTietHoaDonDTO() {}

    public static ChiTietHoaDonDTO fromEntity(ChiTietHoaDon ct) {
        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
        dto.tenSanPham = ct.getSanPham() != null ? ct.getSanPham().getTenSanPham() : "";
        dto.maLo = ct.getLoSanPham() != null ? ct.getLoSanPham().getMaLo() : "";
        dto.donViTinh = ct.getDonViTinh() != null ? ct.getDonViTinh().getTenDonViTinh() : "";
        dto.soLuong = (int) ct.getSoLuong();
        dto.donGia = ct.getGiaBan();
        dto.thanhTien = ct.getThanhTien();
        dto.tenKhuyenMai = ct.getKhuyenMai() != null ? ct.getKhuyenMai().getTenKM() : "";
        dto.giamGia = (ct.getSoLuong() * ct.getGiaBan()) - ct.getThanhTien();
        return dto;
    }

    // Getters & Setters
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    
    public String getMaLo() { return maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }
    
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    
    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
    
    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }
    
    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }
}
