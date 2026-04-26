package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.HoaDon;
import iuh.fit.quanlyhieuthuoc.core.entity.PhieuHuy;
import iuh.fit.quanlyhieuthuoc.core.entity.PhieuTra;
import iuh.fit.quanlyhieuthuoc.core.repository.HoaDonRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.LichSuNhanVienRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuHuyRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuTraRepository;
import iuh.fit.quanlyhieuthuoc.infrastructure.db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 🔍 Tra cứu LỊCH SỬ bán / trả / huỷ theo NHÂN VIÊN
 * Pha 1: JPQL để lấy danh sách mã chứng từ theo nhân viên + khoảng ngày
 * Pha 2: Delegate load entity đầy đủ sang các Repository chuyên biệt
 */
public class LichSuNhanVienRepositoryImpl implements LichSuNhanVienRepository {

    private final HoaDonRepository hoaDonRepository;
    private final PhieuTraRepository phieuTraRepository;
    private final PhieuHuyRepository phieuHuyRepository;

    public LichSuNhanVienRepositoryImpl() {
        this.hoaDonRepository = new HoaDonRepositoryImpl();
        this.phieuTraRepository = new PhieuTraRepositoryImpl();
        this.phieuHuyRepository = new PhieuHuyRepositoryImpl();
    }

    // ============================================================
    // 🔧 Helper — mở EntityManager + transaction (read-only queries)
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
    // 1️⃣ LỊCH SỬ BÁN HÀNG — Hóa đơn của nhân viên theo khoảng ngày
    // ============================================================
    @Override
    public List<HoaDon> layLichSuBanTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay) {
        // Pha 1: lấy danh sách mã
        List<String> dsMa = doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT h.maHoaDon FROM HoaDon h " +
                    "WHERE h.nhanVien.maNhanVien = :maNV");
            if (tuNgay != null && denNgay != null)
                jpql.append(" AND h.ngayLap BETWEEN :tu AND :den");
            else if (tuNgay != null)
                jpql.append(" AND h.ngayLap >= :tu");
            else if (denNgay != null)
                jpql.append(" AND h.ngayLap <= :den");
            jpql.append(" ORDER BY h.ngayLap DESC, h.maHoaDon DESC");

            var query = em.createQuery(jpql.toString(), String.class)
                    .setParameter("maNV", maNhanVien);
            if (tuNgay != null)  query.setParameter("tu", tuNgay);
            if (denNgay != null) query.setParameter("den", denNgay);
            return query.getResultList();
        });

        // Pha 2: load entity đầy đủ
        List<HoaDon> ketQua = new ArrayList<>();
        for (String ma : dsMa) {
            HoaDon hd = hoaDonRepository.timHoaDonTheoMa(ma);
            if (hd != null) ketQua.add(hd);
        }
        return ketQua;
    }

    // ============================================================
    // 2️⃣ LỊCH SỬ TRẢ HÀNG — Phiếu trả của nhân viên theo khoảng ngày
    // ============================================================
    @Override
    public List<PhieuTra> layLichSuTraTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay) {
        List<String> dsMa = doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT pt.maPhieuTra FROM PhieuTra pt " +
                    "WHERE pt.nhanVien.maNhanVien = :maNV");
            if (tuNgay != null && denNgay != null)
                jpql.append(" AND pt.ngayLap BETWEEN :tu AND :den");
            else if (tuNgay != null)
                jpql.append(" AND pt.ngayLap >= :tu");
            else if (denNgay != null)
                jpql.append(" AND pt.ngayLap <= :den");
            jpql.append(" ORDER BY pt.ngayLap DESC, pt.maPhieuTra DESC");

            var query = em.createQuery(jpql.toString(), String.class)
                    .setParameter("maNV", maNhanVien);
            if (tuNgay != null)  query.setParameter("tu", tuNgay);
            if (denNgay != null) query.setParameter("den", denNgay);
            return query.getResultList();
        });

        List<PhieuTra> ketQua = new ArrayList<>();
        for (String ma : dsMa) {
            PhieuTra pt = phieuTraRepository.timKiemPhieuTraBangMa(ma);
            if (pt != null) ketQua.add(pt);
        }
        return ketQua;
    }

    // ============================================================
    // 3️⃣ LỊCH SỬ HỦY HÀNG — Phiếu hủy của nhân viên theo khoảng ngày
    // ============================================================
    @Override
    public List<PhieuHuy> layLichSuHuyTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay) {
        List<String> dsMa = doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT ph.maPhieuHuy FROM PhieuHuy ph " +
                    "WHERE ph.nhanVien.maNhanVien = :maNV");
            if (tuNgay != null && denNgay != null)
                jpql.append(" AND ph.ngayLapPhieu BETWEEN :tu AND :den");
            else if (tuNgay != null)
                jpql.append(" AND ph.ngayLapPhieu >= :tu");
            else if (denNgay != null)
                jpql.append(" AND ph.ngayLapPhieu <= :den");
            jpql.append(" ORDER BY ph.ngayLapPhieu DESC, ph.maPhieuHuy DESC");

            var query = em.createQuery(jpql.toString(), String.class)
                    .setParameter("maNV", maNhanVien);
            if (tuNgay != null)  query.setParameter("tu", tuNgay);
            if (denNgay != null) query.setParameter("den", denNgay);
            return query.getResultList();
        });

        List<PhieuHuy> ketQua = new ArrayList<>();
        for (String ma : dsMa) {
            PhieuHuy ph = phieuHuyRepository.layTheoMa(ma);
            if (ph != null) ketQua.add(ph);
        }
        return ketQua;
    }
}
