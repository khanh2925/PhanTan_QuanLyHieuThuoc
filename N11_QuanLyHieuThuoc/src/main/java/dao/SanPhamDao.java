package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.ChiTietKhuyenMaiSanPham;
import entity.SanPham;
import entity.LoaiSanPham;

/**
 * Dao interface for SanPham entity
 */
public interface SanPhamDao {

    /**
     * Lấy toàn bộ sản phẩm
     */
    ArrayList<SanPham> layTatCaSanPham();

    /**
     * Thêm sản phẩm mới
     */
    boolean themSanPham(SanPham sp);

    /**
     * Cập nhật thông tin sản phẩm
     */
    boolean capNhatSanPham(SanPham sp);

    /**
     * Xóa sản phẩm
     */
    boolean xoaSanPham(String maSanPham);

    /**
     * Lấy sản phẩm theo mã
     */
    SanPham laySanPhamTheoMa(String maSanPham);

    /**
     * Tìm sản phẩm chính xác theo số đăng ký
     */
    SanPham timSanPhamTheoSoDangKy(String soDangKy);

    /**
     * Tìm kiếm sản phẩm theo mã / tên / số đăng ký (LIKE gần đúng)
     */
    ArrayList<SanPham> timKiemSanPham(String tuKhoa);

    /**
     * Lấy danh sách sản phẩm theo loại
     */
    ArrayList<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP);

    /**
     * Lấy danh sách chi tiết khuyến mãi đang áp dụng cho một sản phẩm
     */
    List<ChiTietKhuyenMaiSanPham> layKhuyenMaiDangApDungChoSanPham(String maSanPham);

    /**
     * Thống kê sản phẩm theo nhà cung cấp
     */
    Map<String, Object[]> thongKeSanPhamTheoNCC(String maNCC);

    /**
     * Refresh cache bảng giá
     */
    void refreshCacheBangGia();

    /**
     * Refresh cache sản phẩm
     */
    void refreshCache();
}
