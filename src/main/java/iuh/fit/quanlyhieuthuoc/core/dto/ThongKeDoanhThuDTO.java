package iuh.fit.quanlyhieuthuoc.core.dto;

import java.time.LocalDate;

/**
 * DTO cho thống kê doanh thu
 */
public class ThongKeDoanhThuDTO {
    private LocalDate ngay;
    private String thangNam;
    private double doanhThu;
    private double tienNhap;
    private double tienTraHang;
    private double tienHuyHang;
    private double loiNhuan;
    private int soHoaDon;
    private int soPhieuNhap;
    private int soPhieuTra;
    private int soPhieuHuy;

    public ThongKeDoanhThuDTO() {}

    // Tính lợi nhuận tự động
    public void tinhLoiNhuan() {
        this.loiNhuan = this.doanhThu - this.tienNhap - this.tienTraHang - this.tienHuyHang;
    }

    // Getters & Setters
    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }
    
    public String getThangNam() { return thangNam; }
    public void setThangNam(String thangNam) { this.thangNam = thangNam; }
    
    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
    
    public double getTienNhap() { return tienNhap; }
    public void setTienNhap(double tienNhap) { this.tienNhap = tienNhap; }
    
    public double getTienTraHang() { return tienTraHang; }
    public void setTienTraHang(double tienTraHang) { this.tienTraHang = tienTraHang; }
    
    public double getTienHuyHang() { return tienHuyHang; }
    public void setTienHuyHang(double tienHuyHang) { this.tienHuyHang = tienHuyHang; }
    
    public double getLoiNhuan() { return loiNhuan; }
    public void setLoiNhuan(double loiNhuan) { this.loiNhuan = loiNhuan; }
    
    public int getSoHoaDon() { return soHoaDon; }
    public void setSoHoaDon(int soHoaDon) { this.soHoaDon = soHoaDon; }
    
    public int getSoPhieuNhap() { return soPhieuNhap; }
    public void setSoPhieuNhap(int soPhieuNhap) { this.soPhieuNhap = soPhieuNhap; }
    
    public int getSoPhieuTra() { return soPhieuTra; }
    public void setSoPhieuTra(int soPhieuTra) { this.soPhieuTra = soPhieuTra; }
    
    public int getSoPhieuHuy() { return soPhieuHuy; }
    public void setSoPhieuHuy(int soPhieuHuy) { this.soPhieuHuy = soPhieuHuy; }
}
