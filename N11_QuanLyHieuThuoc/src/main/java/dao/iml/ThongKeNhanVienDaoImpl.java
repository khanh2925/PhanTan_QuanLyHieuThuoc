package dao.iml;

import dao.ThongKeNhanVienDao;
import db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 📊 Thống kê theo NHÂN VIÊN — toàn bộ dùng native SQL (SQL Server)
 * Không extend AbstractGenericDaoImpl vì không có entity cụ thể.
 */
public class ThongKeNhanVienDaoImpl implements ThongKeNhanVienDao {

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
    // 📊 1. Thống kê nhân viên cụ thể (theo khoảng ngày + ca làm)
    //    maNhanVien = null  → bỏ điều kiện MaNhanVien
    //    caLam      = 0    → bỏ điều kiện CaLam
    // ============================================================
    @Override
    public KetQuaThongKe getThongKe(java.util.Date tuNgay, java.util.Date denNgay,
                                     String maNhanVien, int caLam) {
        KetQuaThongKe kq = new KetQuaThongKe();

        Date sqlTu  = new Date(tuNgay.getTime());
        Date sqlDen = new Date(denNgay.getTime());

        // Điều kiện động
        String condNV = (maNhanVien != null) ? " AND nv.MaNhanVien = ? " : "";
        String condCa = (caLam > 0)          ? " AND nv.CaLam = ? "     : "";

        String sqlBan =
                "SELECT COUNT(*) AS SoDon, COALESCE(SUM(hd.TongThanhToan), 0) AS DoanhThu " +
                "FROM HoaDon hd " +
                "JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien " +
                "WHERE hd.NgayLap BETWEEN ? AND ? " + condNV + condCa;

        String sqlTra =
                "SELECT COUNT(DISTINCT pt.MaPhieuTra) AS SoPhieu, " +
                "COALESCE(SUM(ct.ThanhTienHoan), 0) AS TienTra " +
                "FROM PhieuTra pt " +
                "LEFT JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra = ct.MaPhieuTra " +
                "JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                "WHERE pt.NgayLap BETWEEN ? AND ? " + condNV + condCa;

        String sqlHuy =
                "SELECT COUNT(*) AS SoPhieuHuy " +
                "FROM PhieuHuy ph " +
                "JOIN NhanVien nv ON ph.MaNhanVien = nv.MaNhanVien " +
                "WHERE ph.NgayLapPhieu BETWEEN ? AND ? " + condNV + condCa;

        try {
            doInTransaction(em -> {
                // --- A. BÁN HÀNG ---
                var qBan = em.createNativeQuery(sqlBan);
                int pBan = 1;
                qBan.setParameter(pBan++, sqlTu);
                qBan.setParameter(pBan++, sqlDen);
                if (maNhanVien != null) qBan.setParameter(pBan++, maNhanVien);
                if (caLam > 0)         qBan.setParameter(pBan, caLam);

                Object[] rBan = (Object[]) qBan.getSingleResult();
                kq.soHoaDon    = ((Number) rBan[0]).intValue();
                kq.tongDoanhSo = ((Number) rBan[1]).doubleValue();

                // --- B. TRẢ HÀNG ---
                var qTra = em.createNativeQuery(sqlTra);
                int pTra = 1;
                qTra.setParameter(pTra++, sqlTu);
                qTra.setParameter(pTra++, sqlDen);
                if (maNhanVien != null) qTra.setParameter(pTra++, maNhanVien);
                if (caLam > 0)         qTra.setParameter(pTra, caLam);

                Object[] rTra = (Object[]) qTra.getSingleResult();
                kq.soPhieuTra  = ((Number) rTra[0]).intValue();
                kq.tongTienTra = ((Number) rTra[1]).doubleValue();

                // --- C. HỦY HÀNG ---
                var qHuy = em.createNativeQuery(sqlHuy);
                int pHuy = 1;
                qHuy.setParameter(pHuy++, sqlTu);
                qHuy.setParameter(pHuy++, sqlDen);
                if (maNhanVien != null) qHuy.setParameter(pHuy++, maNhanVien);
                if (caLam > 0)         qHuy.setParameter(pHuy, caLam);

                Object rHuy = qHuy.getSingleResult();
                kq.soPhieuHuy = ((Number) rHuy).intValue();

                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thống kê nhân viên: " + e.getMessage());
        }

        return kq;
    }

    // ============================================================
    // 📋 2. Danh sách nhân viên — {MaNhanVien, TenNhanVien}
    // ============================================================
    @Override
    public List<String[]> getDanhSachNhanVien() {
        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT MaNhanVien, TenNhanVien FROM NhanVien"
                ).getResultList();

                List<String[]> list = new ArrayList<>();
                for (Object[] row : rows) {
                    list.add(new String[]{ (String) row[0], (String) row[1] });
                }
                return list;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi lấy danh sách nhân viên: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ============================================================
    // 📊 3. Thống kê TẤT CẢ nhân viên (cho màn hình Quản lý)
    //    LEFT JOIN 3 derived tables: Ban / Tra / Huy
    //    Mỗi derived table nhận 2 tham số ngày → tổng 6 tham số
    // ============================================================
    @Override
    public List<ThongKeChiTietNV> getThongKeDanhSachNhanVien(java.util.Date tuNgay,
                                                               java.util.Date denNgay) {
        Date sqlTu  = new Date(tuNgay.getTime());
        Date sqlDen = new Date(denNgay.getTime());

        String sql =
                "SELECT " +
                "    nv.MaNhanVien, " +
                "    nv.TenNhanVien, " +
                "    COALESCE(Ban.SoDon,      0) AS SoDon, " +
                "    COALESCE(Ban.DoanhThu,   0) AS DoanhThu, " +
                "    COALESCE(Tra.SoPhieuTra, 0) AS SoPhieuTra, " +
                "    COALESCE(Tra.TienTra,    0) AS TienTra, " +
                "    COALESCE(Huy.SoPhieuHuy, 0) AS SoPhieuHuy " +
                "FROM NhanVien nv " +
                "LEFT JOIN ( " +
                "    SELECT MaNhanVien, COUNT(*) AS SoDon, SUM(TongThanhToan) AS DoanhThu " +
                "    FROM HoaDon " +
                "    WHERE NgayLap BETWEEN ? AND ? " +
                "    GROUP BY MaNhanVien " +
                ") Ban ON nv.MaNhanVien = Ban.MaNhanVien " +
                "LEFT JOIN ( " +
                "    SELECT pt.MaNhanVien, " +
                "           COUNT(DISTINCT pt.MaPhieuTra) AS SoPhieuTra, " +
                "           SUM(ct.ThanhTienHoan) AS TienTra " +
                "    FROM PhieuTra pt " +
                "    LEFT JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra = ct.MaPhieuTra " +
                "    WHERE pt.NgayLap BETWEEN ? AND ? " +
                "    GROUP BY pt.MaNhanVien " +
                ") Tra ON nv.MaNhanVien = Tra.MaNhanVien " +
                "LEFT JOIN ( " +
                "    SELECT MaNhanVien, COUNT(*) AS SoPhieuHuy " +
                "    FROM PhieuHuy " +
                "    WHERE NgayLapPhieu BETWEEN ? AND ? " +
                "    GROUP BY MaNhanVien " +
                ") Huy ON nv.MaNhanVien = Huy.MaNhanVien " +
                "WHERE nv.TrangThai = 1 OR Ban.SoDon > 0 OR Tra.SoPhieuTra > 0 " +
                "ORDER BY DoanhThu DESC";

        try {
            return doInTransaction(em -> {
                @SuppressWarnings("unchecked")
                List<Object[]> rows = em.createNativeQuery(sql)
                        .setParameter(1, sqlTu)   // Ban.tuNgay
                        .setParameter(2, sqlDen)  // Ban.denNgay
                        .setParameter(3, sqlTu)   // Tra.tuNgay
                        .setParameter(4, sqlDen)  // Tra.denNgay
                        .setParameter(5, sqlTu)   // Huy.tuNgay
                        .setParameter(6, sqlDen)  // Huy.denNgay
                        .getResultList();

                List<ThongKeChiTietNV> list = new ArrayList<>();
                for (Object[] row : rows) {
                    list.add(new ThongKeChiTietNV(
                            (String) row[0],
                            (String) row[1],
                            ((Number) row[2]).intValue(),
                            ((Number) row[3]).doubleValue(),
                            ((Number) row[4]).intValue(),
                            ((Number) row[5]).doubleValue(),
                            ((Number) row[6]).intValue()
                    ));
                }
                return list;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thống kê danh sách nhân viên: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
