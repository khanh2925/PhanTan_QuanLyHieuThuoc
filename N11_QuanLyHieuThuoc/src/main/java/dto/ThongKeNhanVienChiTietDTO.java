package dto;

import java.io.Serializable;

public class ThongKeNhanVienChiTietDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String maNV;
    public String tenNV;
    public int soHoaDon;
    public double doanhThu;
    public int soPhieuTra;
    public double tienTra;
    public int soPhieuHuy;

    public ThongKeNhanVienChiTietDTO() {
    }

    public ThongKeNhanVienChiTietDTO(String maNV, String tenNV, int soHoaDon, double doanhThu, int soPhieuTra, double tienTra, int soPhieuHuy) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.soHoaDon = soHoaDon;
        this.doanhThu = doanhThu;
        this.soPhieuTra = soPhieuTra;
        this.tienTra = tienTra;
        this.soPhieuHuy = soPhieuHuy;
    }

    public double getThucThu() {
        return doanhThu - tienTra;
    }
}
