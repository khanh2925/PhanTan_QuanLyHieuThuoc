package dao;

import java.util.List;

import entity.DonViTinh;

/**
 * Dao interface for DonViTinh entity
 */
public interface DonViTinhDao {

    /**
     * Lấy toàn bộ đơn vị tính
     */
    List<DonViTinh> layTatCaDonViTinh();

    /**
     * Thêm đơn vị tính
     */
    boolean themDonViTinh(DonViTinh dvt);

    /**
     * Cập nhật tên đơn vị tính
     */
    boolean capNhatDonViTinh(DonViTinh dvt);

    /**
     * Xóa đơn vị tính
     */
    boolean xoaDonViTinh(String maDonViTinh);

    /**
     * Tìm đơn vị tính theo mã
     */
    DonViTinh timDonViTinhTheoMa(String maDonViTinh);

    /**
     * Sinh mã tự động theo định dạng DVT-xxx
     */
    String taoMaTuDong();
}
