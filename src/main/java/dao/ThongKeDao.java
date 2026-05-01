package dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao interface for ThongKe (Statistics)
 */
public interface ThongKeDao {

    // ============================================================
    // 📊 INNER CLASSES (DTOs)
    // ============================================================

    /**
     * DTO cho thống kê doanh thu
     */
    class BanGhiThongKe {
        public String thoiGian;
        public double doanhThu;
        public int soLuongDon;

        public BanGhiThongKe(String thoiGian, double doanhThu, int soLuongDon) {
            this.thoiGian = thoiGian;
            this.doanhThu = doanhThu;
            this.soLuongDon = soLuongDon;
        }
    }

    /**
     * DTO cho thống kê tài chính
     */
    class BanGhiTaiChinh {
        public String thoiGian;
        public double banHang;
        public double nhapHang;
        public double traHang;
        public double huyHang;

        public BanGhiTaiChinh(String thoiGian, double banHang, double nhapHang, double traHang, double huyHang) {
            this.thoiGian = thoiGian;
            this.banHang = banHang;
            this.nhapHang = nhapHang;
            this.traHang = traHang;
            this.huyHang = huyHang;
        }
    }

    /**
     * DTO cho thống kê hóa đơn ngày
     */
    class ThongKeHoaDonNgay {
        private final int soHoaDon;
        private final double tongTien;

        public ThongKeHoaDonNgay(int soHoaDon, double tongTien) {
            this.soHoaDon = soHoaDon;
            this.tongTien = tongTien;
        }

        public int getSoHoaDon() {
            return soHoaDon;
        }

        public double getTongTien() {
            return tongTien;
        }
    }

    // ============================================================
    // 📊 THỐNG KÊ LỢI NHUẬN
    // ============================================================

    /**
     * Tính lợi nhuận theo tháng (ước tính 25.5% doanh thu)
     */
    double tinhLoiNhuanTheoThang(int thang, int nam);

    /**
     * Tính lợi nhuận chính xác theo tháng (doanh thu - chi phí)
     */
    double tinhLoiNhuanChinhXacTheoThang(int thang, int nam);

    // ============================================================
    // 📊 THỐNG KÊ TOP SẢN PHẨM BÁN CHẠY
    // ============================================================

    /**
     * Lấy top N sản phẩm bán chạy theo khoảng thời gian
     * @return List chứa Object[]: {MaSP, TenSP, LoaiSP, SoLuongBan, DoanhThu}
     */
    List<Object[]> layTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int topN);

    /**
     * Tính tổng doanh thu trong khoảng thời gian (đã trừ hoàn trả)
     */
    double tinhTongDoanhThuTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay);

    /**
     * Lấy doanh số sản phẩm của kỳ trước
     */
    double laySoLuongBanKyTruoc(String maSanPham, LocalDate tuNgay, LocalDate denNgay);

    /**
     * Tính tổng doanh thu kỳ trước
     */
    double tinhTongDoanhThuKyTruoc(LocalDate tuNgay, LocalDate denNgay);

    // ============================================================
    // 📦 THỐNG KÊ TỒN KHO THẤP
    // ============================================================

    /**
     * Lấy danh sách sản phẩm có tồn kho thấp dưới ngưỡng
     * @return List chứa Object[]: {MaSP, TenSP, LoaiSP, TongTonKho, GiaNhap, MaNCC, TenNCC}
     */
    List<Object[]> laySanPhamTonKhoThap(int nguongTonKho, String loaiSanPham);

    /**
     * Tính trung bình số lượng bán/ngày của một sản phẩm
     */
    double tinhTrungBinhBanNgay(String maSanPham, int soNgay);

    /**
     * Đếm số nhà cung cấp xuất hiện nhiều nhất trong danh sách sản phẩm cần nhập
     * @return Object[]: {TenNCC, SoLuongSP}
     */
    Object[] timNhaCungCapGoiY(int nguongTonKho);

    /**
     * Lấy loại sản phẩm để hiển thị trong dropdown
     */
    List<String> layDanhSachLoaiSanPham();

    // ============================================================
    // ⏰ THỐNG KÊ LÔ SẮP HẾT HẠN
    // ============================================================

    /**
     * Lấy danh sách lô sản phẩm sắp hết hạn trong vòng N ngày
     * @return List chứa Object[]: {MaLo, TenSP, LoaiSP, HanSuDung, SoLuongTon, GiaBan, MaSP}
     */
    List<Object[]> layLoSapHetHan(int soNgay, String loaiSanPham);

    /**
     * Tính trung bình số lượng bán/ngày của một LÔ cụ thể
     */
    double tinhTrungBinhBanNgayTheoLo(String maLo, int soNgay);

    // ============================================================
    // 📊 THỐNG KÊ THEO LOẠI SẢN PHẨM
    // ============================================================

    /**
     * Lấy thống kê doanh thu, chi phí, lợi nhuận theo loại sản phẩm trong năm
     * @return List chứa Object[]: {LoaiSP, SoLuongSP, DoanhThu, ChiPhi}
     */
    List<Object[]> layThongKeTheoLoaiSanPham(int nam);

    /**
     * Lấy thống kê theo loại sản phẩm cho năm trước
     * @return Map: LoaiSP -> DoanhThu năm trước
     */
    Map<String, Double> layDoanhThuNamTruocTheoLoai(int nam);

    /**
     * Tính tổng doanh thu theo năm
     */
    double tinhTongDoanhThuTheoNam(int nam);

    // ============================================================
    // 📊 THỐNG KÊ DOANH THU
    // ============================================================

    /**
     * Thống kê theo ngày với bộ lọc
     */
    List<BanGhiThongKe> getDoanhThuTheoNgay(Date tuNgay, Date denNgay, String loaiSP, String maKM);

    /**
     * Thống kê theo tháng với bộ lọc
     */
    List<BanGhiThongKe> getDoanhThuTheoThang(int nam, String loaiSP, String maKM);

    /**
     * Thống kê theo năm với bộ lọc
     */
    List<BanGhiThongKe> getDoanhThuTheoNam(int namBatDau, int namKetThuc, String loaiSP, String maKM);

    /**
     * Lấy danh sách Khuyến mãi đưa vào ComboBox
     */
    List<String[]> getDanhSachKhuyenMai();

    /**
     * Lấy tổng doanh thu trong 1 khoảng thời gian
     */
    double getTongDoanhThuTrongKhoang(Date tuNgay, Date denNgay, String loaiSP, String maKM);

    // ============================================================
    // 📊 THỐNG KÊ TÀI CHÍNH
    // ============================================================

    /**
     * Lấy dữ liệu tài chính theo THÁNG
     */
    List<BanGhiTaiChinh> getThongKeTaiChinhTheoThang(int nam, String loaiSP);

    /**
     * Lấy dữ liệu tài chính theo NGÀY
     */
    List<BanGhiTaiChinh> getThongKeTaiChinhTheoNgay(Date tuNgay, Date denNgay, String loaiSP);

    /**
     * Lấy dữ liệu tài chính theo NĂM
     */
    List<BanGhiTaiChinh> getThongKeTaiChinhTheoNam(int namBatDau, int namKetThuc, String loaiSP);

    /**
     * Thống kê hóa đơn hôm nay của nhân viên
     */
    ThongKeHoaDonNgay thongKeHoaDonHomNayCuaNhanVien(String maNhanVien);
}
