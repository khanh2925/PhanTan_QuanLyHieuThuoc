package dao;

import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

import java.util.List;

/**
 * Dao interface for PhieuHuy (Destruction Voucher)
 */
public interface PhieuHuyDao {

    /**
     * Xóa cache để load lại dữ liệu mới từ DB
     */
    void clearCache();

    /**
     * Lấy tất cả phiếu huỷ (OPTIMIZED - dùng JOIN, CÓ CACHE)
     */
    List<PhieuHuy> layTatCaPhieuHuy();

    /**
     * Đếm số phiếu hủy chưa duyệt (cho Dashboard)
     */
    int demPhieuHuyChuaDuyet();

    /**
     * Tính tổng tiền hủy hàng theo tháng (cho biểu đồ)
     * @param thang Tháng (1-12)
     * @param nam Năm
     * @return Tổng tiền hàng bị hủy
     */
    double tinhTongTienHuyTheoThang(int thang, int nam);

    /**
     * Lấy phiếu huỷ theo mã (OPTIMIZED - dùng JOIN)
     */
    PhieuHuy layTheoMa(String maPhieuHuy);

    /**
     * Lấy danh sách chi tiết theo mã phiếu
     */
    List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy);

    /**
     * Thêm phiếu huỷ + chi tiết (Transaction) + TRỪ TỒN KHO
     */
    boolean themPhieuHuy(PhieuHuy ph);

    /**
     * Cập nhật trạng thái phiếu hủy
     */
    boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi);

    /**
     * Tính lại tổng tiền trên entity
     */
    Double tinhTongTienTheoChiTiet(String maPhieuHuy);

    /**
     * Tạo mã tự động PH-yyyyMMdd-xxxx
     */
    String taoMaPhieuHuy();

    /**
     * Xoá phiếu huỷ (xoá cả chi tiết)
     */
    boolean xoa(String maPhieuHuy);

    /**
     * Kiểm tra trạng thái chi tiết
     */
    boolean checkTrangThai(String maPhieuHuy);

    /**
     * Cập nhật trạng thái phiếu huỷ nếu đủ điều kiện
     */
    boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy);

    /**
     * Đếm số phiếu hủy của nhân viên đã lập trong ngày hiện tại
     */
    int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien);
}
