package dao;

import java.util.List;

import entity.BangGia;
import entity.ChiTietBangGia;

/**
 * Dao interface for BangGia entity
 */
public interface BangGiaDao {

    /**
     * Lấy tất cả bảng giá
     */
    List<BangGia> layTatCaBangGia();

    /**
     * Lấy bảng giá đang hoạt động
     */
    BangGia layBangGiaDangHoatDong();

    /**
     * Tìm bảng giá theo mã
     */
    BangGia timBangGiaTheoMa(String maBangGia);

    /**
     * Thêm bảng giá mới
     */
    boolean themBangGia(BangGia bg);

    /**
     * Cập nhật bảng giá
     */
    boolean capNhatBangGia(BangGia bg);

    /**
     * Hủy hoạt động tất cả bảng giá trừ bảng giá chỉ định
     */
    boolean huyHoatDongTatCaTruBangGia(String maBangGia);

    /**
     * Xóa bảng giá
     */
    boolean xoaBangGia(String maBangGia);

    /**
     * Làm mới cache
     */
    void lamMoiCache();

    /**
     * Lấy danh sách chi tiết bảng giá theo mã bảng giá
     */
    List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia);

    /**
     * Thêm chi tiết bảng giá
     */
    boolean themChiTietBangGia(ChiTietBangGia ct);

    /**
     * Xóa toàn bộ chi tiết của một bảng giá
     */
    boolean xoaTatCaChiTiet(String maBangGia);

    /**
     * Sinh mã bảng giá tự động
     */
    String taoMaBangGia();
}
