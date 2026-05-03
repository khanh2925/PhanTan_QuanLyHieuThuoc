package dao;

import java.util.ArrayList;

import entity.KhachHang;

/**
 * Dao interface for KhachHang entity
 */
public interface KhachHangDao {

    /**
     * Lấy toàn bộ khách hàng
     */
    ArrayList<KhachHang> layTatCaKhachHang();

    /**
     * Thêm khách hàng mới
     */
    boolean themKhachHang(KhachHang kh);

    /**
     * Cập nhật thông tin khách hàng
     */
    boolean capNhatKhachHang(KhachHang kh);

    /**
     * Xóa khách hàng
     */
    boolean xoaKhachHang(String maKhachHang);

    /**
     * Tìm khách hàng theo mã / tên / SĐT (LIKE gần đúng)
     */
    ArrayList<KhachHang> timKhachHang(String tuKhoa);

    /**
     * Tìm khách hàng đang hoạt động
     */
    ArrayList<KhachHang> timKhachHangHoatDong();

    /**
     * Tìm khách hàng chính xác theo mã
     */
    KhachHang timKhachHangTheoMa(String maKhachHang);

    /**
     * Refresh cache
     */
    void refreshCache();

    /**
     * Tìm 1 khách hàng chính xác theo SĐT
     */
    KhachHang timKhachHangTheoSoDienThoai(String soDienThoai);

    /**
     * Phát sinh mã khách hàng tiếp theo dạng KH-yyyymmdd-xxxx
     */
    String phatSinhMaKhachHangTiepTheo();

    /**
     * Đếm số khách hàng mới trong tháng
     */
    int demKhachHangMoiTheoThang(int thang, int nam);
}
