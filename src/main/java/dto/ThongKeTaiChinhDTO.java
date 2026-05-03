package dto;

import java.io.Serializable;

public class ThongKeTaiChinhDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String thoiGian;
    public double banHang;
    public double nhapHang;
    public double traHang;
    public double huyHang;

    public ThongKeTaiChinhDTO() {
    }

    public ThongKeTaiChinhDTO(String thoiGian, double banHang, double nhapHang, double traHang, double huyHang) {
        this.thoiGian = thoiGian;
        this.banHang = banHang;
        this.nhapHang = nhapHang;
        this.traHang = traHang;
        this.huyHang = huyHang;
    }
}
