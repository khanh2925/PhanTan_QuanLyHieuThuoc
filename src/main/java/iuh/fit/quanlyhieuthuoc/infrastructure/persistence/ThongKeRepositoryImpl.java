package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.repository.ThongKeRepository;
import iuh.fit.quanlyhieuthuoc.infrastructure.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * ThongKe impl — thuần native SQL (SQL Server specific: MONTH, YEAR, GETDATE,
 * DATEADD, FORMAT, ROW_NUMBER, OVER, PARTITION BY, WITH CTE, TOP).
 * Không extend AbstractGenericRepositoryImpl (không có entity tương ứng).
 */
public class ThongKeRepositoryImpl implements ThongKeRepository {

    public ThongKeRepositoryImpl() {
    }

    // ============================================================
    // 🔧 Helper — mở EntityManager + transaction
    // ============================================================
    private <R> R doInTransaction(Function<EntityManager, R> fn) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            R result = fn.apply(em);
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException(ex);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // ============================================================
    // 📊 Lợi nhuận ước tính (25.5% doanh thu)
    // ============================================================
    @Override
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon " +
                        "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?")
                        .setParameter(1, thang).setParameter(2, nam).getResultList();
                double dt = (r.isEmpty() || r.get(0) == null) ? 0 : r.get(0).doubleValue();
                return dt * 0.255;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi tính lợi nhuận: " + e.getMessage());
            return 0;
        }
    }

    // ============================================================
    // 📊 Lợi nhuận chính xác (doanh thu - giá nhập thực tế)
    // ============================================================
    @Override
    public double tinhLoiNhuanChinhXacTheoThang(int thang, int nam) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(cthd.ThanhTien), 0), " +
                        "COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi * sp.GiaNhap), 0) " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND sp.MaSanPham = qc.MaSanPham " +
                        "WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?")
                        .setParameter(1, thang).setParameter(2, nam).getResultList();
                if (r.isEmpty()) return 0.0;
                Object[] row = r.get(0);
                double dt = row[0] == null ? 0 : ((Number) row[0]).doubleValue();
                double cp = row[1] == null ? 0 : ((Number) row[1]).doubleValue();
                return dt - cp;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi tính lợi nhuận chính xác: " + e.getMessage());
            return tinhLoiNhuanTheoThang(thang, nam);
        }
    }

    // ============================================================
    // 📊 Top N sản phẩm bán chạy
    // ============================================================
    @Override
    public List<Object[]> layTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int topN) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> r = em.createNativeQuery(
                        "SELECT TOP (?) sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, " +
                        "SUM(cthd.SoLuong * qc.HeSoQuyDoi), SUM(cthd.ThanhTien) " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND sp.MaSanPham = qc.MaSanPham " +
                        "WHERE hd.NgayLap BETWEEN ? AND ? " +
                        "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham " +
                        "ORDER BY SUM(cthd.SoLuong * qc.HeSoQuyDoi) DESC")
                        .setParameter(1, topN)
                        .setParameter(2, java.sql.Date.valueOf(tuNgay))
                        .setParameter(3, java.sql.Date.valueOf(denNgay))
                        .getResultList();
                List<Object[]> result = new ArrayList<>();
                for (Object rawRow : r) {
                    Object[] row = (Object[]) rawRow;
                    result.add(new Object[] {
                            row[0], row[1], row[2],
                            row[3] == null ? 0.0 : ((Number) row[3]).doubleValue(),
                            row[4] == null ? 0.0 : ((Number) row[4]).doubleValue()
                    });
                }
                return result;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi lấy top SP bán chạy: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ============================================================
    // 📊 Tổng doanh thu theo khoảng ngày (đã trừ hoàn trả)
    // ============================================================
    @Override
    public double tinhTongDoanhThuTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT " +
                        "COALESCE((SELECT SUM(TongThanhToan) FROM HoaDon WHERE NgayLap BETWEEN ? AND ?), 0) " +
                        "- COALESCE((SELECT SUM(TongTienHoan) FROM PhieuTra WHERE NgayLap BETWEEN ? AND ? AND DaDuyet = 1), 0)")
                        .setParameter(1, java.sql.Date.valueOf(tuNgay))
                        .setParameter(2, java.sql.Date.valueOf(denNgay))
                        .setParameter(3, java.sql.Date.valueOf(tuNgay))
                        .setParameter(4, java.sql.Date.valueOf(denNgay))
                        .getResultList();
                return (r.isEmpty() || r.get(0) == null) ? 0.0 : r.get(0).doubleValue();
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi tính tổng doanh thu: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public double laySoLuongBanKyTruoc(String maSanPham, LocalDate tuNgay, LocalDate denNgay) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND lo.MaSanPham = qc.MaSanPham " +
                        "WHERE lo.MaSanPham = ? AND hd.NgayLap BETWEEN ? AND ?")
                        .setParameter(1, maSanPham)
                        .setParameter(2, java.sql.Date.valueOf(tuNgay))
                        .setParameter(3, java.sql.Date.valueOf(denNgay))
                        .getResultList();
                return (r.isEmpty() || r.get(0) == null) ? 0.0 : r.get(0).doubleValue();
            });
        } catch (RuntimeException e) { return 0; }
    }

    @Override
    public double tinhTongDoanhThuKyTruoc(LocalDate tuNgay, LocalDate denNgay) {
        return tinhTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);
    }

    // ============================================================
    // 📦 Sản phẩm tồn kho thấp (ROW_NUMBER + dynamic filter)
    // ============================================================
    @Override
    public List<Object[]> laySanPhamTonKhoThap(int nguongTonKho, String loaiSanPham) {
        try {
            return doInTransaction(em -> {
                boolean locLoai = loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả");
                String sql =
                        "SELECT sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, " +
                        "COALESCE(SUM(lo.SoLuongTon), 0) AS TongTonKho, " +
                        "sp.GiaNhap, ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                        "FROM SanPham sp " +
                        "LEFT JOIN LoSanPham lo ON sp.MaSanPham = lo.MaSanPham " +
                        "    AND lo.HanSuDung >= GETDATE() AND lo.SoLuongTon > 0 " +
                        "LEFT JOIN ( " +
                        "    SELECT lo_pn.MaSanPham, pn.MaNhaCungCap, " +
                        "           ROW_NUMBER() OVER (PARTITION BY lo_pn.MaSanPham ORDER BY pn.NgayNhap DESC) AS rn " +
                        "    FROM ChiTietPhieuNhap ctpn " +
                        "    INNER JOIN LoSanPham lo_pn ON ctpn.MaLo = lo_pn.MaLo " +
                        "    INNER JOIN PhieuNhap pn ON ctpn.MaPhieuNhap = pn.MaPhieuNhap " +
                        ") AS pn_latest ON sp.MaSanPham = pn_latest.MaSanPham AND pn_latest.rn = 1 " +
                        "LEFT JOIN NhaCungCap ncc ON pn_latest.MaNhaCungCap = ncc.MaNhaCungCap " +
                        "WHERE sp.HoatDong = 1 " +
                        (locLoai ? "AND sp.LoaiSanPham = ? " : "") +
                        "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.GiaNhap, " +
                        "         ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                        "HAVING COALESCE(SUM(lo.SoLuongTon), 0) <= ? " +
                        "ORDER BY TongTonKho ASC";

                Query q = em.createNativeQuery(sql);
                int idx = 1;
                if (locLoai) q.setParameter(idx++, loaiSanPham);
                q.setParameter(idx, nguongTonKho);

                @SuppressWarnings("unchecked")
                List<Object[]> rows = q.getResultList();
                List<Object[]> result = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    result.add(new Object[] {
                            row[0], row[1], row[2],
                            row[3] == null ? 0 : ((Number) row[3]).intValue(),
                            row[4] == null ? 0.0 : ((Number) row[4]).doubleValue(),
                            row[5], row[6]
                    });
                }
                return result;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi lấy SP tồn kho thấp: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public double tinhTrungBinhBanNgay(String maSanPham, int soNgay) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND lo.MaSanPham = qc.MaSanPham " +
                        "WHERE lo.MaSanPham = ? " +
                        "    AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())")
                        .setParameter(1, (double) soNgay)
                        .setParameter(2, maSanPham)
                        .setParameter(3, soNgay)
                        .getResultList();
                return (r.isEmpty() || r.get(0) == null) ? 0.0 : r.get(0).doubleValue();
            });
        } catch (RuntimeException e) { return 0; }
    }

    @Override
    public Object[] timNhaCungCapGoiY(int nguongTonKho) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> r = em.createNativeQuery(
                        "WITH SP_TonThap AS ( " +
                        "    SELECT sp.MaSanPham, COALESCE(SUM(lo.SoLuongTon), 0) AS TongTon, pn_latest.MaNhaCungCap " +
                        "    FROM SanPham sp " +
                        "    LEFT JOIN LoSanPham lo ON sp.MaSanPham = lo.MaSanPham " +
                        "        AND lo.HanSuDung >= GETDATE() AND lo.SoLuongTon > 0 " +
                        "    LEFT JOIN ( " +
                        "        SELECT lo_pn.MaSanPham, pn.MaNhaCungCap, " +
                        "               ROW_NUMBER() OVER (PARTITION BY lo_pn.MaSanPham ORDER BY pn.NgayNhap DESC) AS rn " +
                        "        FROM ChiTietPhieuNhap ctpn " +
                        "        INNER JOIN LoSanPham lo_pn ON ctpn.MaLo = lo_pn.MaLo " +
                        "        INNER JOIN PhieuNhap pn ON ctpn.MaPhieuNhap = pn.MaPhieuNhap " +
                        "    ) AS pn_latest ON sp.MaSanPham = pn_latest.MaSanPham AND pn_latest.rn = 1 " +
                        "    WHERE sp.HoatDong = 1 " +
                        "    GROUP BY sp.MaSanPham, pn_latest.MaNhaCungCap " +
                        "    HAVING COALESCE(SUM(lo.SoLuongTon), 0) <= ? " +
                        ") " +
                        "SELECT TOP 1 ncc.TenNhaCungCap, COUNT(*) " +
                        "FROM SP_TonThap stt " +
                        "INNER JOIN NhaCungCap ncc ON stt.MaNhaCungCap = ncc.MaNhaCungCap " +
                        "GROUP BY ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                        "ORDER BY COUNT(*) DESC")
                        .setParameter(1, nguongTonKho)
                        .getResultList();
                if (r.isEmpty()) return new Object[] { "Không có dữ liệu", 0 };
                Object[] row = (Object[]) r.get(0);
                return new Object[] { row[0], row[1] == null ? 0 : ((Number) row[1]).intValue() };
            });
        } catch (RuntimeException e) { return new Object[] { "Không có dữ liệu", 0 }; }
    }

    @Override
    public List<String> layDanhSachLoaiSanPham() {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<String> r = em.createNativeQuery(
                        "SELECT DISTINCT LoaiSanPham FROM SanPham WHERE HoatDong = 1 ORDER BY LoaiSanPham")
                        .getResultList();
                List<String> out = new ArrayList<>();
                for (String s : r) { if (s != null && !s.isEmpty()) out.add(s); }
                return out;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    // ============================================================
    // ⏰ Lô sắp hết hạn
    // ============================================================
    @Override
    public List<Object[]> layLoSapHetHan(int soNgay, String loaiSanPham) {
        try {
            return doInTransaction(em -> {
                boolean locLoai = loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả");
                String sql =
                        "SELECT lo.MaLo, sp.TenSanPham, sp.LoaiSanPham, " +
                        "lo.HanSuDung, lo.SoLuongTon, sp.GiaBan, sp.MaSanPham " +
                        "FROM LoSanPham lo " +
                        "INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE lo.HanSuDung >= GETDATE() " +
                        "    AND lo.HanSuDung <= DATEADD(DAY, ?, GETDATE()) " +
                        "    AND lo.SoLuongTon > 0 AND sp.HoatDong = 1 " +
                        (locLoai ? "AND sp.LoaiSanPham = ? " : "") +
                        "ORDER BY lo.HanSuDung ASC, lo.SoLuongTon DESC";

                Query q = em.createNativeQuery(sql);
                int idx = 1;
                q.setParameter(idx++, soNgay);
                if (locLoai) q.setParameter(idx, loaiSanPham);

                @SuppressWarnings("unchecked")
                List<Object[]> rows = q.getResultList();
                List<Object[]> result = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    java.sql.Date han = (java.sql.Date) row[3];
                    result.add(new Object[] {
                            row[0], row[1], row[2],
                            han != null ? han.toLocalDate() : null,
                            row[4] == null ? 0 : ((Number) row[4]).intValue(),
                            row[5] == null ? 0.0 : ((Number) row[5]).doubleValue(),
                            row[6]
                    });
                }
                return result;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi lấy lô sắp hết hạn: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public double tinhTrungBinhBanNgayTheoLo(String maLo, int soNgay) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND lo.MaSanPham = qc.MaSanPham " +
                        "WHERE cthd.MaLo = ? AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())")
                        .setParameter(1, (double) soNgay).setParameter(2, maLo).setParameter(3, soNgay)
                        .getResultList();
                double tb = (r.isEmpty() || r.get(0) == null) ? 0 : r.get(0).doubleValue();
                if (tb < 0.01) return tinhTrungBinhBanNgayTuMaLo(em, maLo, soNgay);
                return tb;
            });
        } catch (RuntimeException e) { return 0.1; }
    }

    private double tinhTrungBinhBanNgayTuMaLo(EntityManager em, String maLo, int soNgay) {
        @SuppressWarnings("unchecked")
        List<Number> r = em.createNativeQuery(
                "SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? " +
                "FROM ChiTietHoaDon cthd " +
                "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                "    AND lo.MaSanPham = qc.MaSanPham " +
                "WHERE lo.MaSanPham = (SELECT MaSanPham FROM LoSanPham WHERE MaLo = ?) " +
                "    AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())")
                .setParameter(1, (double) soNgay).setParameter(2, maLo).setParameter(3, soNgay)
                .getResultList();
        double tb = (r.isEmpty() || r.get(0) == null) ? 0 : r.get(0).doubleValue();
        return tb > 0.01 ? tb : 0.1;
    }

    // ============================================================
    // 📊 Thống kê theo loại sản phẩm
    // ============================================================
    @Override
    public List<Object[]> layThongKeTheoLoaiSanPham(int nam) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT sp.LoaiSanPham, COUNT(DISTINCT sp.MaSanPham), " +
                        "SUM(cthd.ThanhTien), SUM(cthd.SoLuong * qc.HeSoQuyDoi * sp.GiaNhap) " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh " +
                        "    AND sp.MaSanPham = qc.MaSanPham " +
                        "WHERE YEAR(hd.NgayLap) = ? AND sp.HoatDong = 1 " +
                        "GROUP BY sp.LoaiSanPham ORDER BY SUM(cthd.ThanhTien) DESC")
                        .setParameter(1, nam).getResultList();
                List<Object[]> result = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    result.add(new Object[] {
                            row[0],
                            row[1] == null ? 0 : ((Number) row[1]).intValue(),
                            row[2] == null ? 0.0 : ((Number) row[2]).doubleValue(),
                            row[3] == null ? 0.0 : ((Number) row[3]).doubleValue()
                    });
                }
                return result;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public Map<String, Double> layDoanhThuNamTruocTheoLoai(int nam) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT sp.LoaiSanPham, SUM(cthd.ThanhTien) " +
                        "FROM ChiTietHoaDon cthd " +
                        "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                        "INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo " +
                        "INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE YEAR(hd.NgayLap) = ? AND sp.HoatDong = 1 " +
                        "GROUP BY sp.LoaiSanPham")
                        .setParameter(1, nam - 1).getResultList();
                Map<String, Double> map = new HashMap<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    if (row[0] != null)
                        map.put((String) row[0], row[1] == null ? 0.0 : ((Number) row[1]).doubleValue());
                }
                return map;
            });
        } catch (RuntimeException e) { return new HashMap<>(); }
    }

    @Override
    public double tinhTongDoanhThuTheoNam(int nam) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Number> r = em.createNativeQuery(
                        "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon WHERE YEAR(NgayLap) = ?")
                        .setParameter(1, nam).getResultList();
                return (r.isEmpty() || r.get(0) == null) ? 0.0 : r.get(0).doubleValue();
            });
        } catch (RuntimeException e) { return 0; }
    }

    // ============================================================
    // 📊 Thống kê theo ngày/tháng/năm với bộ lọc động
    //    Giữ nguyên pattern dynamic SQL (tương đương JDBC version)
    // ============================================================

    private String buildFilterLoaiSP(String loaiSP) {
        if (loaiSP != null && !loaiSP.equals("Tất cả")) {
            return " AND sp.LoaiSanPham = N'" + loaiSP.replace("'", "''") + "' ";
        }
        return "";
    }

    private String buildFilterKM(String maKM) {
        if (maKM != null && !maKM.equals("Tất cả")) {
            return " AND (hd.MaKM = ? OR ct.MaKM = ?) ";
        }
        return "";
    }

    @Override
    public List<BanGhiThongKe> getDoanhThuTheoNgay(Date tuNgay, Date denNgay, String loaiSP, String maKM) {
        try {
            return doInTransaction(em -> {
                String filterL = buildFilterLoaiSP(loaiSP);
                String filterK = buildFilterKM(maKM);
                boolean hasKM = maKM != null && !maKM.equals("Tất cả");

                String sql = "SELECT FORMAT(hd.NgayLap, 'dd/MM/yyyy'), SUM(ct.ThanhTien), COUNT(DISTINCT hd.MaHoaDon) " +
                        "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                        "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE hd.NgayLap BETWEEN ? AND ? " + filterL + filterK +
                        " GROUP BY hd.NgayLap ORDER BY hd.NgayLap ASC";

                Query q = em.createNativeQuery(sql);
                q.setParameter(1, new java.sql.Date(tuNgay.getTime()));
                q.setParameter(2, new java.sql.Date(denNgay.getTime()));
                int idx = 3;
                if (hasKM) { q.setParameter(idx++, maKM); q.setParameter(idx++, maKM); }

                @SuppressWarnings("unchecked")
                List<Object[]> rows = q.getResultList();
                List<BanGhiThongKe> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new BanGhiThongKe((String) row[0],
                            row[1] == null ? 0 : ((Number) row[1]).doubleValue(),
                            row[2] == null ? 0 : ((Number) row[2]).intValue()));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public List<BanGhiThongKe> getDoanhThuTheoThang(int nam, String loaiSP, String maKM) {
        try {
            return doInTransaction(em -> {
                String filterL = buildFilterLoaiSP(loaiSP);
                String filterK = buildFilterKM(maKM);
                boolean hasKM = maKM != null && !maKM.equals("Tất cả");

                String sql = "SELECT MONTH(hd.NgayLap), SUM(ct.ThanhTien), COUNT(DISTINCT hd.MaHoaDon) " +
                        "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                        "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE YEAR(hd.NgayLap) = ? " + filterL + filterK +
                        " GROUP BY MONTH(hd.NgayLap) ORDER BY MONTH(hd.NgayLap) ASC";

                Query q = em.createNativeQuery(sql).setParameter(1, nam);
                int idx = 2;
                if (hasKM) { q.setParameter(idx++, maKM); q.setParameter(idx++, maKM); }

                @SuppressWarnings("unchecked")
                List<Object[]> rows = q.getResultList();
                List<BanGhiThongKe> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new BanGhiThongKe("T" + ((Number) row[0]).intValue(),
                            row[1] == null ? 0 : ((Number) row[1]).doubleValue(),
                            row[2] == null ? 0 : ((Number) row[2]).intValue()));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public List<BanGhiThongKe> getDoanhThuTheoNam(int namBatDau, int namKetThuc, String loaiSP, String maKM) {
        try {
            return doInTransaction(em -> {
                String filterL = buildFilterLoaiSP(loaiSP);
                String filterK = buildFilterKM(maKM);
                boolean hasKM = maKM != null && !maKM.equals("Tất cả");

                String sql = "SELECT YEAR(hd.NgayLap), SUM(ct.ThanhTien), COUNT(DISTINCT hd.MaHoaDon) " +
                        "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                        "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE YEAR(hd.NgayLap) BETWEEN ? AND ? " + filterL + filterK +
                        " GROUP BY YEAR(hd.NgayLap) ORDER BY YEAR(hd.NgayLap) ASC";

                Query q = em.createNativeQuery(sql).setParameter(1, namBatDau).setParameter(2, namKetThuc);
                int idx = 3;
                if (hasKM) { q.setParameter(idx++, maKM); q.setParameter(idx++, maKM); }

                @SuppressWarnings("unchecked")
                List<Object[]> rows = q.getResultList();
                List<BanGhiThongKe> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new BanGhiThongKe(String.valueOf(((Number) row[0]).intValue()),
                            row[1] == null ? 0 : ((Number) row[1]).doubleValue(),
                            row[2] == null ? 0 : ((Number) row[2]).intValue()));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public List<String[]> getDanhSachKhuyenMai() {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery("SELECT MaKM, TenKM FROM KhuyenMai").getResultList();
                List<String[]> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new String[] { (String) row[0], (String) row[1] });
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public double getTongDoanhThuTrongKhoang(Date tuNgay, Date denNgay, String loaiSP, String maKM) {
        try {
            return doInTransaction(em -> {
                String filterL = buildFilterLoaiSP(loaiSP);
                String filterK = buildFilterKM(maKM);
                boolean hasKM = maKM != null && !maKM.equals("Tất cả");

                String sql = "SELECT COALESCE(SUM(ct.ThanhTien), 0) " +
                        "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                        "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                        "WHERE hd.NgayLap BETWEEN ? AND ? " + filterL + filterK;

                Query q = em.createNativeQuery(sql)
                        .setParameter(1, new java.sql.Date(tuNgay.getTime()))
                        .setParameter(2, new java.sql.Date(denNgay.getTime()));
                int idx = 3;
                if (hasKM) { q.setParameter(idx++, maKM); q.setParameter(idx++, maKM); }

                @SuppressWarnings("unchecked")
                List<Number> r = q.getResultList();
                return (r.isEmpty() || r.get(0) == null) ? 0.0 : r.get(0).doubleValue();
            });
        } catch (RuntimeException e) { return 0; }
    }

    // ============================================================
    // 📊 Thống kê tài chính (Ban + Nhap + Tra + Huy) theo tháng/ngày/năm
    //    Dùng UNION ALL — giữ nguyên SQL Server syntax
    // ============================================================

    @Override
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoThang(int nam, String loaiSP) {
        try {
            return doInTransaction(em -> {
                String f = buildFilterLoaiSP(loaiSP);
                String sql = ("SELECT T.Thang, SUM(T.B) AS Ban, SUM(T.N) AS Nhap, SUM(T.T) AS Tra, SUM(T.H) AS Huy " +
                        "FROM ( " +
                        "  SELECT MONTH(hd.NgayLap) Thang, SUM(ct.ThanhTien) B, 0 N, 0 T, 0 H " +
                        "  FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon=ct.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(hd.NgayLap)=? " + f + " GROUP BY MONTH(hd.NgayLap) " +
                        "  UNION ALL " +
                        "  SELECT MONTH(pn.NgayNhap),0,SUM(ct.ThanhTien),0,0 " +
                        "  FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap=ct.MaPhieuNhap " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(pn.NgayNhap)=? " + f + " GROUP BY MONTH(pn.NgayNhap) " +
                        "  UNION ALL " +
                        "  SELECT MONTH(pt.NgayLap),0,0,SUM(ct.ThanhTienHoan),0 " +
                        "  FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra=ct.MaPhieuTra " +
                        "  JOIN HoaDon hd ON ct.MaHoaDon=hd.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(pt.NgayLap)=? AND pt.DaDuyet=1 " + f + " GROUP BY MONTH(pt.NgayLap) " +
                        "  UNION ALL " +
                        "  SELECT MONTH(ph.NgayLapPhieu),0,0,0,SUM(ct.ThanhTien) " +
                        "  FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy=ct.MaPhieuHuy " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(ph.NgayLapPhieu)=? " + f + " GROUP BY MONTH(ph.NgayLapPhieu) " +
                        ") AS T GROUP BY T.Thang ORDER BY T.Thang");

                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(sql)
                        .setParameter(1, nam).setParameter(2, nam).setParameter(3, nam).setParameter(4, nam)
                        .getResultList();
                List<BanGhiTaiChinh> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new BanGhiTaiChinh("T" + ((Number) row[0]).intValue(),
                            n(row[1]), n(row[2]), n(row[3]), n(row[4])));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoNgay(Date tuNgay, Date denNgay, String loaiSP) {
        try {
            return doInTransaction(em -> {
                String f = buildFilterLoaiSP(loaiSP);
                java.sql.Date d1 = new java.sql.Date(tuNgay.getTime());
                java.sql.Date d2 = new java.sql.Date(denNgay.getTime());
                String sql = ("SELECT T.Ngay, SUM(T.B) Ban, SUM(T.N) Nhap, SUM(T.T) Tra, SUM(T.H) Huy " +
                        "FROM ( " +
                        "  SELECT hd.NgayLap Ngay, SUM(ct.ThanhTien) B, 0 N, 0 T, 0 H " +
                        "  FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon=ct.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE hd.NgayLap BETWEEN ? AND ? " + f + " GROUP BY hd.NgayLap " +
                        "  UNION ALL " +
                        "  SELECT pn.NgayNhap,0,SUM(ct.ThanhTien),0,0 " +
                        "  FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap=ct.MaPhieuNhap " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE pn.NgayNhap BETWEEN ? AND ? " + f + " GROUP BY pn.NgayNhap " +
                        "  UNION ALL " +
                        "  SELECT pt.NgayLap,0,0,SUM(ct.ThanhTienHoan),0 " +
                        "  FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra=ct.MaPhieuTra " +
                        "  JOIN HoaDon hd ON ct.MaHoaDon=hd.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE pt.NgayLap BETWEEN ? AND ? AND pt.DaDuyet=1 " + f + " GROUP BY pt.NgayLap " +
                        "  UNION ALL " +
                        "  SELECT ph.NgayLapPhieu,0,0,0,SUM(ct.ThanhTien) " +
                        "  FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy=ct.MaPhieuHuy " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE ph.NgayLapPhieu BETWEEN ? AND ? " + f + " GROUP BY ph.NgayLapPhieu " +
                        ") AS T GROUP BY T.Ngay ORDER BY T.Ngay");

                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(sql)
                        .setParameter(1, d1).setParameter(2, d2)
                        .setParameter(3, d1).setParameter(4, d2)
                        .setParameter(5, d1).setParameter(6, d2)
                        .setParameter(7, d1).setParameter(8, d2)
                        .getResultList();

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                List<BanGhiTaiChinh> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    String label = (row[0] instanceof java.sql.Date) ? sdf.format((java.sql.Date) row[0]) : "";
                    list.add(new BanGhiTaiChinh(label, n(row[1]), n(row[2]), n(row[3]), n(row[4])));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoNam(int namBatDau, int namKetThuc, String loaiSP) {
        try {
            return doInTransaction(em -> {
                String f = buildFilterLoaiSP(loaiSP);
                String sql = ("SELECT T.Nam, SUM(T.B) Ban, SUM(T.N) Nhap, SUM(T.T) Tra, SUM(T.H) Huy " +
                        "FROM ( " +
                        "  SELECT YEAR(hd.NgayLap) Nam, SUM(ct.ThanhTien) B, 0 N, 0 T, 0 H " +
                        "  FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon=ct.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(hd.NgayLap) BETWEEN ? AND ? " + f + " GROUP BY YEAR(hd.NgayLap) " +
                        "  UNION ALL " +
                        "  SELECT YEAR(pn.NgayNhap),0,SUM(ct.ThanhTien),0,0 " +
                        "  FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap=ct.MaPhieuNhap " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(pn.NgayNhap) BETWEEN ? AND ? " + f + " GROUP BY YEAR(pn.NgayNhap) " +
                        "  UNION ALL " +
                        "  SELECT YEAR(pt.NgayLap),0,0,SUM(ct.ThanhTienHoan),0 " +
                        "  FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra=ct.MaPhieuTra " +
                        "  JOIN HoaDon hd ON ct.MaHoaDon=hd.MaHoaDon " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(pt.NgayLap) BETWEEN ? AND ? AND pt.DaDuyet=1 " + f + " GROUP BY YEAR(pt.NgayLap) " +
                        "  UNION ALL " +
                        "  SELECT YEAR(ph.NgayLapPhieu),0,0,0,SUM(ct.ThanhTien) " +
                        "  FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy=ct.MaPhieuHuy " +
                        "  JOIN LoSanPham lo ON ct.MaLo=lo.MaLo JOIN SanPham sp ON lo.MaSanPham=sp.MaSanPham " +
                        "  WHERE YEAR(ph.NgayLapPhieu) BETWEEN ? AND ? " + f + " GROUP BY YEAR(ph.NgayLapPhieu) " +
                        ") AS T GROUP BY T.Nam ORDER BY T.Nam");

                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(sql)
                        .setParameter(1, namBatDau).setParameter(2, namKetThuc)
                        .setParameter(3, namBatDau).setParameter(4, namKetThuc)
                        .setParameter(5, namBatDau).setParameter(6, namKetThuc)
                        .setParameter(7, namBatDau).setParameter(8, namKetThuc)
                        .getResultList();

                List<BanGhiTaiChinh> list = new ArrayList<>();
                for (Object rawRow : rows) {
                    Object[] row = (Object[]) rawRow;
                    list.add(new BanGhiTaiChinh(String.valueOf(((Number) row[0]).intValue()),
                            n(row[1]), n(row[2]), n(row[3]), n(row[4])));
                }
                return list;
            });
        } catch (RuntimeException e) { return new ArrayList<>(); }
    }

    @Override
    public ThongKeHoaDonNgay thongKeHoaDonHomNayCuaNhanVien(String maNhanVien) {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> r = em.createNativeQuery(
                        "SELECT COUNT(*), COALESCE(SUM(TongThanhToan), 0) FROM HoaDon " +
                        "WHERE MaNhanVien = ? AND CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)")
                        .setParameter(1, maNhanVien).getResultList();
                if (r.isEmpty()) return new ThongKeHoaDonNgay(0, 0);
                Object[] row = (Object[]) r.get(0);
                return new ThongKeHoaDonNgay(
                        row[0] == null ? 0 : ((Number) row[0]).intValue(),
                        row[1] == null ? 0 : ((Number) row[1]).doubleValue());
            });
        } catch (RuntimeException e) { return new ThongKeHoaDonNgay(0, 0); }
    }

    /** Shorthand để convert null-safe số */
    private static double n(Object o) {
        return (o == null) ? 0.0 : ((Number) o).doubleValue();
    }
}
