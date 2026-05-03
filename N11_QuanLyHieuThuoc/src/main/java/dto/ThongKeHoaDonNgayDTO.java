package dto;

import java.io.Serializable;

public class ThongKeHoaDonNgayDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int soHoaDon;
    private double tongTien;

    public ThongKeHoaDonNgayDTO() {
    }

    public ThongKeHoaDonNgayDTO(int soHoaDon, double tongTien) {
        this.soHoaDon = soHoaDon;
        this.tongTien = tongTien;
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
}
