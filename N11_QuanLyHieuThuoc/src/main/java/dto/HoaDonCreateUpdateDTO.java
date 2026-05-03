package dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HoaDonCreateUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHoaDon;
    private String maNhanVien;
    private String maKhachHang;
    private String ngayLap;
    private String maKhuyenMai;
    private boolean thuocKeDon;
    private List<ChiTietHoaDonCreateUpdateDTO> chiTietList;

    public HoaDonCreateUpdateDTO() {
        this.chiTietList = new ArrayList<>();
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getNgayLap() { return ngayLap; }
    public void setNgayLap(String ngayLap) { this.ngayLap = ngayLap; }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public boolean isThuocKeDon() { return thuocKeDon; }
    public void setThuocKeDon(boolean thuocKeDon) { this.thuocKeDon = thuocKeDon; }

    public List<ChiTietHoaDonCreateUpdateDTO> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietHoaDonCreateUpdateDTO> chiTietList) {
        this.chiTietList = chiTietList != null ? chiTietList : new ArrayList<>();
    }
}