package iuh.fit.quanlyhieuthuoc.core.repository;

import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietBangGia;

/**
 * Repository interface for ChiTietBangGia entity
 */
public interface ChiTietBangGiaRepository {

    /**
     * Lấy danh sách chi tiết bảng giá theo mã bảng giá
     */
    List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia);

    /**
     * Lấy chi tiết bảng giá theo khoảng giá
     */
    ChiTietBangGia timChiTietTheoKhoangGia(String maBangGia, double giaNhap);

    /**
     * Thêm chi tiết bảng giá mới
     */
    boolean themChiTietBangGia(ChiTietBangGia ctbg);

    /**
     * Cập nhật chi tiết bảng giá
     */
    boolean capNhatChiTietBangGia(ChiTietBangGia ctbg, double giaTuCu, double giaDenCu);

    /**
     * Xóa chi tiết bảng giá (theo khoảng giá)
     */
    boolean xoaChiTietBangGia(String maBangGia, double giaTu, double giaDen);

    /**
     * Xóa toàn bộ chi tiết của 1 bảng giá
     */
    boolean xoaChiTietTheoMaBangGia(String maBangGia);
}
