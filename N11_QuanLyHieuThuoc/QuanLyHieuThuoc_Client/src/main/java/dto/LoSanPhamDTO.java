package dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * DTO cho lô sản phẩm - hiển thị thông tin tồn kho
 */
public class LoSanPhamDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maLo;
    private String maSanPham;
    private String tenSanPham;
    private String hanSuDung;
    private int soLuongTon;
    private String trangThai;
    private int soNgayConLai;
    private boolean sapHetHan;
    private boolean hetHan;

    public LoSanPhamDTO() {}

    // Getters & Setters
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
    

    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    public int getSoNgayConLai() { return soNgayConLai; }
    public void setSoNgayConLai(int soNgayConLai) { this.soNgayConLai = soNgayConLai; }
    
    public boolean isSapHetHan() { return sapHetHan; }
    public void setSapHetHan(boolean sapHetHan) { this.sapHetHan = sapHetHan; }
    
    public boolean isHetHan() { return hetHan; }
    public void setHetHan(boolean hetHan) { this.hetHan = hetHan; }
}
