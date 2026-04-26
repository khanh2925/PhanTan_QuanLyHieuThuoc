package iuh.fit.quanlyhieuthuoc.core.repository;

import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.KhuyenMai;

/**
 * Repository interface for KhuyenMai entity
 */
public interface KhuyenMaiRepository {

    /**
     * Tìm khuyến mãi theo mã
     */
    KhuyenMai timKhuyenMaiTheoMa(String maKM);

    /**
     * Lấy tất cả khuyến mãi
     */
    List<KhuyenMai> layTatCaKhuyenMai();

    /**
     * Thêm khuyến mãi
     */
    boolean themKhuyenMai(KhuyenMai km);

    /**
     * Cập nhật khuyến mãi
     */
    boolean capNhatKhuyenMai(KhuyenMai km);

    /**
     * Giảm số lượng khuyến mãi sau khi áp dụng
     */
    boolean giamSoLuong(String maKM);

    /**
     * Lấy danh sách khuyến mãi đang hoạt động
     */
    List<KhuyenMai> layKhuyenMaiDangHoatDong();

    /**
     * Sinh mã khuyến mãi tự động
     */
    String taoMaKhuyenMai();

    /**
     * Xóa khuyến mãi theo mã
     */
    boolean xoaKhuyenMai(String maKM);
}
