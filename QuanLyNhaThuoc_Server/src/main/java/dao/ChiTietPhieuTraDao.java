package dao;

import entity.ChiTietPhieuTra;

import java.util.List;

/**
 * Dao interface for ChiTietPhieuTra (Return Voucher Detail)
 */
public interface ChiTietPhieuTraDao {

    /**
     * Lấy danh sách chi tiết phiếu trả theo mã phiếu trả (OPTIMIZED - dùng JOIN)
     */
    List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra);

    /**
     * Thêm mới 1 chi tiết phiếu trả
     */
    boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt);

    /**
     * Cập nhật trạng thái của 1 chi tiết phiếu trả
     */
    boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maLo, String maDonViTinh,
                                     int trangThaiMoi);

    /**
     * Tính tổng số lượng đã trả của 1 sản phẩm theo mã HĐ + mã lô
     */
    double tongSoLuongDaTra(String maHD, String maLo);
}
