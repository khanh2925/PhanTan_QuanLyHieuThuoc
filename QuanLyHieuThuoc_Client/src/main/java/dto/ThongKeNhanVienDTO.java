package dto;

import java.io.Serializable;

public class ThongKeNhanVienDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public double tongDoanhSo = 0;
    public int soHoaDon = 0;
    public int soPhieuTra = 0;
    public double tongTienTra = 0;
    public int soPhieuHuy = 0;

    public double getGiaTriTrungBinh() {
        return soHoaDon == 0 ? 0 : tongDoanhSo / soHoaDon;
    }

    public double getTyLeHoanTra() {
        return soHoaDon == 0 ? 0 : ((double) soPhieuTra / soHoaDon) * 100;
    }
}
