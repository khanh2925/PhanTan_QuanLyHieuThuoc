package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuHuy;
import iuh.fit.quanlyhieuthuoc.core.entity.LoSanPham;
import iuh.fit.quanlyhieuthuoc.core.enums.LoaiSanPham;
import iuh.fit.quanlyhieuthuoc.core.repository.LoSanPhamRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoSanPhamRepositoryImpl
        extends AbstractGenericRepositoryImpl<LoSanPham, String>
        implements LoSanPhamRepository {

    public LoSanPhamRepositoryImpl() {
        super(LoSanPham.class);
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void clearCache() {
        // No-op
    }

    @Override
    public ArrayList<LoSanPham> layTatCaLoSanPham() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT l FROM LoSanPham l LEFT JOIN FETCH l.sanPham",
                        LoSanPham.class).getResultList())
        );
    }

    @Override
    public boolean themLoSanPham(LoSanPham lo) {
        return doInTransaction(em -> {
            // Attach SanPham managed reference
            if (lo.getSanPham() != null && lo.getSanPham().getMaSanPham() != null) {
                var sp = em.find(
                        iuh.fit.quanlyhieuthuoc.core.entity.SanPham.class,
                        lo.getSanPham().getMaSanPham());
                if (sp != null) lo.setSanPham(sp);
            }
            em.persist(lo);
            return true;
        });
    }

    @Override
    public boolean capNhatLoSanPham(LoSanPham lo) {
        return doInTransaction(em -> {
            em.merge(lo);
            return true;
        });
    }

    @Override
    public boolean xoaLoSanPham(String maLo) {
        return doInTransaction(em -> {
            LoSanPham lo = em.find(LoSanPham.class, maLo);
            if (lo != null) {
                em.remove(lo);
                return true;
            }
            return false;
        });
    }

    @Override
    public LoSanPham timLoTheoMa(String maLo) {
        return doInTransaction(em -> {
            List<LoSanPham> list = em.createQuery(
                    "SELECT l FROM LoSanPham l LEFT JOIN FETCH l.sanPham WHERE l.maLo = :ma",
                    LoSanPham.class)
                    .setParameter("ma", maLo)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public List<LoSanPham> layDanhSachLoTheoMaSanPham(String maSanPham) {
        LocalDate today = LocalDate.now();
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham " +
                        "WHERE l.sanPham.maSanPham = :maSP " +
                        "AND l.soLuongTon > 0 AND l.hanSuDung >= :today " +
                        "ORDER BY l.hanSuDung ASC",
                        LoSanPham.class)
                        .setParameter("maSP", maSanPham)
                        .setParameter("today", today)
                        .getResultList()
        );
    }

    @Override
    public LoSanPham timLoGanHetHanTheoSanPham(String maSanPham) {
        LocalDate today = LocalDate.now();
        return doInTransaction(em -> {
            List<LoSanPham> list = em.createQuery(
                    "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham " +
                    "WHERE l.sanPham.maSanPham = :maSP " +
                    "AND l.hanSuDung >= :today AND l.soLuongTon > 0 " +
                    "ORDER BY l.hanSuDung ASC",
                    LoSanPham.class)
                    .setParameter("maSP", maSanPham)
                    .setParameter("today", today)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public LoSanPham timLoKeTiepTheoSanPham(String maSanPham, LocalDate hanSuDungHienTai) {
        LocalDate today = LocalDate.now();
        return doInTransaction(em -> {
            List<LoSanPham> list = em.createQuery(
                    "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham " +
                    "WHERE l.sanPham.maSanPham = :maSP " +
                    "AND l.hanSuDung > :hanCu " +
                    "AND l.hanSuDung >= :today " +
                    "AND l.soLuongTon > 0 " +
                    "ORDER BY l.hanSuDung ASC",
                    LoSanPham.class)
                    .setParameter("maSP", maSanPham)
                    .setParameter("hanCu", hanSuDungHienTai)
                    .setParameter("today", today)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    /**
     * Tính tồn thực tế = tồn kho - SL chờ duyệt phiếu hủy - SL chờ duyệt phiếu trả.
     * Dùng native query vì có correlated subqueries.
     */
    @Override
    public int tinhSoLuongTonThucTe(String maLo) {
        final int CTPH_CHO_DUYET = ChiTietPhieuHuy.CHO_DUYET;
        final int CTPT_CHO_DUYET = 0;
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT lo.SoLuongTon " +
                    "- COALESCE((SELECT SUM(ctph.SoLuongHuy) FROM ChiTietPhieuHuy ctph " +
                    "             WHERE ctph.MaLo = lo.MaLo AND ctph.TrangThai = ?), 0) " +
                    "- COALESCE((SELECT SUM(ctpt.SoLuong) FROM ChiTietPhieuTra ctpt " +
                    "             WHERE ctpt.MaLo = lo.MaLo AND ctpt.TrangThai = ?), 0) " +
                    "AS SoLuongTonKhaDung " +
                    "FROM LoSanPham lo WHERE lo.MaLo = ?")
                    .setParameter(1, CTPH_CHO_DUYET)
                    .setParameter(2, CTPT_CHO_DUYET)
                    .setParameter(3, maLo)
                    .getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                int val = result.get(0).intValue();
                return Math.max(0, val);
            }
            return 0;
        });
    }

    @Override
    public String taoMaLoTuDong() {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT TOP 1 MaLo FROM LoSanPham WHERE MaLo LIKE 'LO-%' ORDER BY MaLo DESC")
                    .getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                try {
                    String lastMaLo = result.get(0);
                    int lastNumber = Integer.parseInt(lastMaLo.substring(3));
                    return String.format("LO-%06d", lastNumber + 1);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            return "LO-000001";
        });
    }

    @Override
    public List<LoSanPham> timLoDaHetHanTheoLoai(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null) return new ArrayList<>();
        LocalDate today = LocalDate.now();
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham sp " +
                        "WHERE sp.loaiSanPham = :loai " +
                        "AND l.hanSuDung < :today " +
                        "AND l.soLuongTon > 0",
                        LoSanPham.class)
                        .setParameter("loai", loaiSanPham)
                        .setParameter("today", today)
                        .getResultList()
        );
    }

    /**
     * Thống kê số lô gần hết hạn (0 < HSD - today <= 90) theo loại sản phẩm.
     * Dùng native query vì cần GROUP BY và DATEADD.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<LoaiSanPham, Integer> thongKeSoLoDaHetHanTheoHSDTheoLoai() {
        Map<LoaiSanPham, Integer> map = new LinkedHashMap<>();
        for (LoaiSanPham l : LoaiSanPham.values()) map.put(l, 0);

        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(90);

        doInTransaction(em -> {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT SP.LoaiSanPham, COUNT(*) AS SoLo " +
                    "FROM LoSanPham L JOIN SanPham SP ON L.MaSanPham = SP.MaSanPham " +
                    "WHERE L.SoLuongTon > 0 AND L.HanSuDung > ? AND L.HanSuDung <= ? " +
                    "GROUP BY SP.LoaiSanPham")
                    .setParameter(1, java.sql.Date.valueOf(today))
                    .setParameter(2, java.sql.Date.valueOf(deadline))
                    .getResultList();
            for (Object[] row : rows) {
                try {
                    LoaiSanPham loai = LoaiSanPham.valueOf(((String) row[0]).trim().toUpperCase());
                    map.put(loai, ((Number) row[1]).intValue());
                } catch (Exception ignore) {
                }
            }
            return null;
        });
        return map;
    }

    @Override
    public List<LoSanPham> layDanhSachLoSPToiHanSuDung() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(90);
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham " +
                        "WHERE l.soLuongTon > 0 " +
                        "AND l.hanSuDung > :today " +
                        "AND l.hanSuDung <= :deadline " +
                        "ORDER BY l.hanSuDung ASC",
                        LoSanPham.class)
                        .setParameter("today", today)
                        .setParameter("deadline", deadline)
                        .getResultList()
        );
    }

    @Override
    public List<LoSanPham> layDanhSachLoSPDaHetHan() {
        LocalDate today = LocalDate.now();
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT l FROM LoSanPham l JOIN FETCH l.sanPham " +
                        "WHERE l.soLuongTon > 0 AND l.hanSuDung < :today " +
                        "ORDER BY l.hanSuDung ASC",
                        LoSanPham.class)
                        .setParameter("today", today)
                        .getResultList()
        );
    }

    @Override
    public List<LoSanPham> timLoSanPhamTheoKeyword(String keyword) {
        String kw = "%" + keyword.toLowerCase() + "%";
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT l FROM LoSanPham l LEFT JOIN FETCH l.sanPham sp " +
                        "WHERE LOWER(l.maLo) LIKE :kw " +
                        "OR LOWER(sp.maSanPham) LIKE :kw " +
                        "OR LOWER(sp.tenSanPham) LIKE :kw",
                        LoSanPham.class)
                        .setParameter("kw", kw)
                        .getResultList()
        );
    }

    /** Pure Java logic — không cần DB query */
    @Override
    public boolean kiemTraLoToiHan(LoSanPham lo) {
        if (lo == null || lo.getSanPham() == null) return false;
        LocalDate today = LocalDate.now();
        LocalDate hanSuDung = lo.getHanSuDung();
        LocalDate deadline = today.plusDays(90);
        return hanSuDung.isAfter(today) && !hanSuDung.isAfter(deadline);
    }
}
