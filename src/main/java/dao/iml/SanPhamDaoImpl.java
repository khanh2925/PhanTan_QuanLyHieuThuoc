package dao.iml;

import entity.ChiTietKhuyenMaiSanPham;
import entity.SanPham;
import entity.LoaiSanPham;
import dao.ChiTietKhuyenMaiSanPhamDao;
import dao.SanPhamDao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SanPhamDaoImpl
        extends AbstractGenericDaoImpl<SanPham, String>
        implements SanPhamDao {

    /**
     * Delegate để lấy khuyến mãi đang áp dụng cho sản phẩm.
     * Các bảng giá (chiTietBangGiaHienTai) là trách nhiệm của service layer — không load ở đây.
     */
    private final ChiTietKhuyenMaiSanPhamDao chiTietKM_Dao;

    public SanPhamDaoImpl() {
        super(SanPham.class);
        this.chiTietKM_Dao = new ChiTietKhuyenMaiSanPhamDaoImpl();
    }

    @Override
    public ArrayList<SanPham> layTatCaSanPham() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT s FROM SanPham s",
                        SanPham.class).getResultList())
        );
    }

    @Override
    public boolean themSanPham(SanPham sp) {
        return doInTransaction(em -> {
            em.persist(sp);
            return true;
        });
    }

    @Override
    public boolean capNhatSanPham(SanPham sp) {
        return doInTransaction(em -> {
            em.merge(sp);
            return true;
        });
    }

    @Override
    public boolean xoaSanPham(String maSanPham) {
        return doInTransaction(em -> {
            SanPham sp = em.find(SanPham.class, maSanPham);
            if (sp != null) {
                em.remove(sp);
                return true;
            }
            return false;
        });
    }

    @Override
    public SanPham laySanPhamTheoMa(String maSanPham) {
        return doInTransaction(em -> em.find(SanPham.class, maSanPham));
    }

    @Override
    public SanPham timSanPhamTheoSoDangKy(String soDangKy) {
        return doInTransaction(em -> {
            List<SanPham> list = em.createQuery(
                    "SELECT s FROM SanPham s WHERE s.soDangKy = :sdk",
                    SanPham.class)
                    .setParameter("sdk", soDangKy)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public ArrayList<SanPham> timKiemSanPham(String tuKhoa) {
        return doInTransaction(em -> {
            String kw = "%" + tuKhoa.trim() + "%";
            return new ArrayList<>(em.createQuery(
                    "SELECT s FROM SanPham s WHERE s.maSanPham LIKE :kw " +
                    "OR s.tenSanPham LIKE :kw OR s.soDangKy LIKE :kw",
                    SanPham.class)
                    .setParameter("kw", kw)
                    .getResultList());
        });
    }

    @Override
    public ArrayList<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT s FROM SanPham s WHERE s.loaiSanPham = :loai",
                        SanPham.class)
                        .setParameter("loai", loaiSP)
                        .getResultList())
        );
    }

    /**
     * Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm (delegate to ChiTietKMDao).
     */
    @Override
    public List<ChiTietKhuyenMaiSanPham> layKhuyenMaiDangApDungChoSanPham(String maSanPham) {
        return chiTietKM_Dao.layChiTietKhuyenMaiDangHoatDongTheoMaSP(maSanPham);
    }

    /**
     * Thống kê sản phẩm theo nhà cung cấp — dùng native query vì JOIN nhiều bảng.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object[]> thongKeSanPhamTheoNCC(String maNCC) {
        Map<String, Object[]> result = new LinkedHashMap<>();
        return doInTransaction(em -> {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, " +
                    "COUNT(DISTINCT pn.MaPhieuNhap) AS SoLanNhap, " +
                    "SUM(ct.SoLuongNhap) AS TongSoLuong " +
                    "FROM PhieuNhap pn " +
                    "JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap = ct.MaPhieuNhap " +
                    "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                    "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                    "WHERE pn.MaNhaCungCap = ? " +
                    "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham " +
                    "ORDER BY TongSoLuong DESC")
                    .setParameter(1, maNCC)
                    .getResultList();
            for (Object[] row : rows) {
                String maSP = (String) row[0];
                result.put(maSP, new Object[]{
                        row[1],                   // TenSanPham
                        row[2],                   // LoaiSanPham
                        ((Number) row[3]).intValue(),  // SoLanNhap
                        ((Number) row[4]).intValue()   // TongSoLuong
                });
            }
            return result;
        });
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void refreshCacheBangGia() {
        // No-op
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void refreshCache() {
        // No-op
    }
}
