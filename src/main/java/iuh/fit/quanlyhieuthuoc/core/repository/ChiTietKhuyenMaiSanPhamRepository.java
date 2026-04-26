package iuh.fit.quanlyhieuthuoc.core.repository;

import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietKhuyenMaiSanPham;

/**
 * Repository interface for ChiTietKhuyenMaiSanPham entity
 */
public interface ChiTietKhuyenMaiSanPhamRepository {

    /**
     * Tìm kiếm chi tiết khuyến mãi sản phẩm bằng mã KM
     */
    List<ChiTietKhuyenMaiSanPham> timKiemChiTietKhuyenMaiSanPhamBangMa(String maKM);

    /**
     * Thêm sản phẩm vào khuyến mãi
     */
    boolean themChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm);

    /**
     * Xóa 1 sản phẩm ra khỏi khuyến mãi
     */
    boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSP);

    /**
     * Xóa toàn bộ sản phẩm của 1 khuyến mãi
     */
    boolean xoaTatCaSanPhamCuaKM(String maKM);

    /**
     * Kiểm tra tồn tại
     */
    boolean daTonTai(String maKM, String maSP);

    /**
     * Lấy toàn bộ chi tiết với JOIN (SP + KM)
     */
    List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaCoJoin(String maKM);

    /**
     * Lấy chi tiết khuyến mãi đang hoạt động theo mã sản phẩm
     */
    List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiDangHoatDongTheoMaSP(String maSP);
}
