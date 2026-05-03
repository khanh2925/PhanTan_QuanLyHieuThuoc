package dto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho khách hàng
 */
public class KhachHangDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String gioiTinh;
    private String ngaySinh;
    private int soLanMua;
    private double tongChiTieu;

    public KhachHangDTO() {}

    // Getters & Setters
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    

    
    public int getSoLanMua() { return soLanMua; }
    public void setSoLanMua(int soLanMua) { this.soLanMua = soLanMua; }
    
    public double getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(double tongChiTieu) { this.tongChiTieu = tongChiTieu; }
}
