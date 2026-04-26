package iuh.fit.quanlyhieuthuoc.core.repository;

import java.util.ArrayList;
import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;

/**
 * Repository interface for NhanVien entity
 */
public interface NhanVienRepository {

    /**
     * Lấy toàn bộ nhân viên
     */
    ArrayList<NhanVien> layTatCaNhanVien();

    /**
     * Thêm nhân viên mới
     */
    boolean themNhanVien(NhanVien nv);

    /**
     * Cập nhật thông tin nhân viên
     */
    boolean capNhatNhanVien(NhanVien nv);

    /**
     * Xóa nhân viên
     */
    boolean xoaNhanVien(String maNhanVien);

    /**
     * Tìm nhân viên theo mã, tên hoặc số điện thoại (LIKE gần đúng)
     */
    ArrayList<NhanVien> timNhanVien(String tuKhoa);

    /**
     * Tìm nhân viên chính xác theo mã
     */
    NhanVien timNhanVienTheoMa(String maNhanVien);

    /**
     * Cập nhật trạng thái làm việc
     */
    boolean capNhatTrangThai(String maNhanVien, boolean trangThai);

    /**
     * Tạo mã nhân viên tự động
     */
    String taoMaNhanVienTuDong();

    /**
     * Force refresh cache - Xóa cache và load lại từ DB
     */
    void refreshCache();

    /**
     * Tìm nhân viên theo số điện thoại (từ cache)
     */
    List<NhanVien> timNhanVienTheoSoDienThoai(String soDienThoai);
}
