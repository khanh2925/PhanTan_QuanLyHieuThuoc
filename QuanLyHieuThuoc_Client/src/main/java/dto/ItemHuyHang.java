package dto;

import java.io.Serializable;

public class ItemHuyHang implements Serializable {

    private String maLo;
    private String tenSanPham;
    private int soLuongTon;
    private int soLuongHuy;
    private double donGiaNhap;
    private String lyDo;
    private String hinhAnh;
    private QuyCachDongGoi quyCachHienTai;
    private QuyCachDongGoi quyCachGoc;

    public ItemHuyHang(String maLo, String tenSanPham, int soLuongTon,
                       double donGiaNhap, String hinhAnh) {
        this.maLo = maLo;
        this.tenSanPham = tenSanPham;
        this.soLuongTon = soLuongTon;
        this.donGiaNhap = donGiaNhap;
        this.hinhAnh = hinhAnh;
        this.soLuongHuy = 1;
        this.lyDo = "";
    }

    public String getMaLo() { return maLo; }
    public String getTenSanPham() { return tenSanPham; }
    public int getSoLuongTon() { return soLuongTon; }
    public int getSoLuongHuy() { return soLuongHuy; }
    public void setSoLuongHuy(int soLuongHuy) { this.soLuongHuy = soLuongHuy; }
    public double getDonGiaNhap() { return donGiaNhap; }

    public double getThanhTien() {
        return donGiaNhap * getSoLuongHuyTheoGoc();
    }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public int getSoLuongHuyTheoGoc() {
        if (quyCachHienTai == null || quyCachGoc == null) {
            return soLuongHuy;
        }
        return soLuongHuy * quyCachHienTai.getHeSoQuyDoi();
    }

    public QuyCachDongGoi getQuyCachHienTai() { return quyCachHienTai; }
    public void setQuyCachHienTai(QuyCachDongGoi qc) { this.quyCachHienTai = qc; }
    public QuyCachDongGoi getQuyCachGoc() { return quyCachGoc; }
    public void setQuyCachGoc(QuyCachDongGoi qc) { this.quyCachGoc = qc; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}

