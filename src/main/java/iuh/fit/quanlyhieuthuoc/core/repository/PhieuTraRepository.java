package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuTra;
import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;
import iuh.fit.quanlyhieuthuoc.core.entity.PhieuTra;

import java.util.List;

/**
 * Repository interface for PhieuTra (Return Voucher)
 */
public interface PhieuTraRepository {

    /**
     * Xóa cache để load lại dữ liệu mới từ DB
     */
    void clearCache();

    /**
     * Tìm phiếu theo mã (OPTIMIZED - dùng JOIN)
     */
    PhieuTra timKiemPhieuTraBangMa(String maPhieuTra);

    /**
     * Đếm số phiếu trả chưa duyệt (cho Dashboard)
     */
    int demPhieuTraChuaDuyet();

    /**
     * Tính tổng tiền trả hàng theo tháng (cho biểu đồ)
     * @param thang Tháng (1-12)
     * @param nam Năm
     * @return Tổng tiền đã hoàn trả
     */
    double tinhTongTienTraTheoThang(int thang, int nam);

    /**
     * Lấy tất cả phiếu trả (OPTIMIZED - dùng JOIN, CÓ CACHE)
     */
    List<PhieuTra> layTatCaPhieuTra();

    /**
     * Thêm phiếu trả và chi tiết
     */
    boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet);

    /**
     * Cập nhật trạng thái (transaction)
     * @return "OK" hoặc "OK|mã_phiếu_hủy" nếu tạo phiếu hủy, "ERR" nếu lỗi
     */
    String capNhatTrangThai_GiaoDich(String maPhieuTra, String maHoaDon, String maLo, String maDonViTinh,
                                      NhanVien nv, int trangThaiMoi);

    /**
     * Sinh mã tự động
     */
    String taoMaPhieuTra();

    /**
     * Kiểm tra đã trả lô trong hóa đơn chưa
     */
    boolean daTraLoTrongHoaDon(String maHD, String maLo);

    /**
     * Đếm số phiếu trả của nhân viên đã tạo trong ngày hiện tại
     */
    int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien);

    /**
     * Làm mới cache
     */
    void refreshCache();

    /**
     * Tìm phiếu trả theo SĐT khách hàng (OPTIMIZED - dùng JOIN)
     */
    List<PhieuTra> timPhieuTraTheoSoDienThoai(String sdt);

    /**
     * Tìm phiếu trả theo keyword (mã phiếu, tên KH, SĐT)
     */
    List<PhieuTra> timPhieuTraTheoKeyword(String keyword);
}
