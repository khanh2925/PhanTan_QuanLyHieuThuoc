package dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * DTO cho hóa đơn - dùng để hiển thị danh sách và báo cáo
 */
public class HoaDonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maHoaDon;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String tenNhanVien;
    private String ngayLap;
    private double tongTien;
    private double giamGia;
    private double thanhToan;
    private int soSanPham;
    private List<ChiTietHoaDonDTO> chiTietList;

    public HoaDonDTO() {
        this.chiTietList = new ArrayList<>();
    }

    // Getters & Setters
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    
    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }
    
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    
    public String getNgayLap() { return ngayLap; }
    public void setNgayLap(String ngayLap) { this.ngayLap = ngayLap; }
    
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    
    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }
    
    public double getThanhToan() { return thanhToan; }
    public void setThanhToan(double thanhToan) { this.thanhToan = thanhToan; }
    
    public int getSoSanPham() { return soSanPham; }
    public void setSoSanPham(int soSanPham) { this.soSanPham = soSanPham; }
    
    public List<ChiTietHoaDonDTO> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietHoaDonDTO> chiTietList) { this.chiTietList = chiTietList; }
}
