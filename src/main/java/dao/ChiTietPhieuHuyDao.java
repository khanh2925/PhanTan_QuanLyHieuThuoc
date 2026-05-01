package dao;

import entity.ChiTietPhieuHuy;

import java.util.List;

/**
 * Dao interface for ChiTietPhieuHuy (Destruction Voucher Detail)
 */
public interface ChiTietPhieuHuyDao {

    /**
     * Lấy danh sách chi tiết phiếu huỷ theo mã phiếu (OPTIMIZED - dùng JOIN)
     */
    List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy);

    /**
     * Cập nhật trạng thái chi tiết
     */
    boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThaiMoi);

    /**
     * Xoá chi tiết (và hoàn tồn nếu cần)
     */
    boolean xoaChiTietPhieuHuy(ChiTietPhieuHuy ct);

    /**
     * Kiểm tra tất cả chi tiết đã xử lý chưa
     */
    boolean tatCaChiTietDaXuLy(String maPhieuHuy);
}
