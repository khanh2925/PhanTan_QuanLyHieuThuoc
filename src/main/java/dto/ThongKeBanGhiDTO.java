package dto;

import java.io.Serializable;

public class ThongKeBanGhiDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String thoiGian;
    public double doanhThu;
    public int soLuongDon;

    public ThongKeBanGhiDTO() {
    }

    public ThongKeBanGhiDTO(String thoiGian, double doanhThu, int soLuongDon) {
        this.thoiGian = thoiGian;
        this.doanhThu = doanhThu;
        this.soLuongDon = soLuongDon;
    }
}
