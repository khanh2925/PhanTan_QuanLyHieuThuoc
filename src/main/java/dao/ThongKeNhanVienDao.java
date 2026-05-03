package dao;

import java.util.Date;
import java.util.List;

/**
 * Dao interface for ThongKeNhanVien (Employee Statistics)
 */
public interface ThongKeNhanVienDao {

    // ============================================================
    // 📊 INNER CLASSES (DTOs)
    // ============================================================

    /**
     * DTO cho kết quả thống kê nhân viên
     */
    class KetQuaThongKe {
        public double tongDoanhSo = 0;
        public int soHoaDon = 0;
        public int soPhieuTra = 0;
        public double tongTienTra = 0;
        public int soPhieuHuy = 0;

        public double getGiaTriTrungBinh() {
            return soHoaDon == 0 ? 0 : tongDoanhSo / soHoaDon;
        }

        public double getTyLeHoanTra() {
            return soHoaDon == 0 ? 0 : ((double) (soPhieuTra) / soHoaDon) * 100;
        }
    }

    /**
     * DTO cho thống kê chi tiết nhân viên
     */
    class ThongKeChiTietNV {
        public String maNV;
        public String tenNV;
        public int soHoaDon;
        public double doanhThu;
        public int soPhieuTra;
        public double tienTra;
        public int soPhieuHuy;

        public ThongKeChiTietNV(String maNV, String tenNV, int soHoaDon, double doanhThu, int soPhieuTra,
                                double tienTra, int soPhieuHuy) {
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

    // ============================================================
    // 📊 METHODS
    // ============================================================

    /**
     * Lấy thống kê cho nhân viên cụ thể
     * @param tuNgay Ngày bắt đầu
     * @param denNgay Ngày kết thúc
     * @param maNhanVien Mã nhân viên đang đăng nhập (Bắt buộc)
     * @param caLam Ca làm việc (0: Tất cả, 1: Sáng, 2: Chiều, 3: Tối)
     */
    KetQuaThongKe getThongKe(Date tuNgay, Date denNgay, String maNhanVien, int caLam);

    /**
     * Lấy danh sách nhân viên
     * @return List chứa String[]: {MaNhanVien, TenNhanVien}
     */
    List<String[]> getDanhSachNhanVien();

    /**
     * Lấy danh sách thống kê của TẤT CẢ nhân viên trong khoảng thời gian
     * Dùng cho bảng thống kê của Quản lý
     */
    List<ThongKeChiTietNV> getThongKeDanhSachNhanVien(Date tuNgay, Date denNgay);
}
