package iuh.fit.quanlyhieuthuoc.core.repository;

import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.NhaCungCap;

/**
 * Repository interface for NhaCungCap entity
 */
public interface NhaCungCapRepository {

    /**
     * Lấy toàn bộ nhà cung cấp
     */
    List<NhaCungCap> layTatCaNhaCungCap();

    /**
     * Thêm nhà cung cấp mới
     */
    boolean themNhaCungCap(NhaCungCap ncc);

    /**
     * Cập nhật nhà cung cấp
     */
    boolean capNhatNhaCungCap(NhaCungCap ncc);

    /**
     * Sinh mã tự động NCC-yyyyMMdd-xxxx
     */
    String taoMaTuDong();

    /**
     * Tìm nhà cung cấp theo mã hoặc SĐT
     */
    NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword);

    /**
     * Tìm kiếm nâng cao cho giao diện TraCuuNhaCungCap
     */
    List<NhaCungCap> timKiemNCC(String keyword, String khuVuc, String trangThai, String tieuChi);

    /**
     * Force refresh cache
     */
    void refreshCache();
}
