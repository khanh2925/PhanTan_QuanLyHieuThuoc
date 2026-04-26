package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.*;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuHuyRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuyRepositoryImpl
        extends AbstractGenericRepositoryImpl<PhieuHuy, String>
        implements PhieuHuyRepository {

    public PhieuHuyRepositoryImpl() {
        super(PhieuHuy.class);
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void clearCache() { /* no-op */ }

    // ============================================================
    // 📜 Lấy tất cả phiếu huỷ — JOIN FETCH NhanVien + khởi tạo lazy chi tiết
    // ============================================================
    @Override
    public List<PhieuHuy> layTatCaPhieuHuy() {
        return doInTransaction(em -> {
            List<PhieuHuy> list = em.createQuery(
                    "SELECT ph FROM PhieuHuy ph JOIN FETCH ph.nhanVien " +
                    "ORDER BY ph.ngayLapPhieu DESC, ph.maPhieuHuy DESC",
                    PhieuHuy.class).getResultList();
            for (PhieuHuy ph : list) {
                ph.getChiTietPhieuHuyList().size(); // init LAZY
            }
            return new ArrayList<>(list);
        });
    }

    // ============================================================
    // 🔍 Lấy phiếu huỷ theo mã — bao gồm chi tiết
    // ============================================================
    @Override
    public PhieuHuy layTheoMa(String maPhieuHuy) {
        return doInTransaction(em -> {
            List<PhieuHuy> list = em.createQuery(
                    "SELECT ph FROM PhieuHuy ph JOIN FETCH ph.nhanVien " +
                    "WHERE ph.maPhieuHuy = :ma",
                    PhieuHuy.class)
                    .setParameter("ma", maPhieuHuy)
                    .getResultList();
            if (list.isEmpty()) return null;
            PhieuHuy ph = list.get(0);
            ph.getChiTietPhieuHuyList().size(); // init LAZY
            return ph;
        });
    }

    // ============================================================
    // 📜 Lấy danh sách chi tiết theo mã phiếu (public method)
    // ============================================================
    @Override
    public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT ct FROM ChiTietPhieuHuy ct " +
                        "JOIN FETCH ct.loSanPham lo JOIN FETCH lo.sanPham " +
                        "LEFT JOIN FETCH ct.donViTinh " +
                        "WHERE ct.id.maPhieuHuy = :ma ORDER BY ct.id.maLo",
                        ChiTietPhieuHuy.class)
                        .setParameter("ma", maPhieuHuy)
                        .getResultList()
        );
    }

    // ============================================================
    // ➕ Thêm phiếu huỷ + cascade chi tiết + TRỪ TỒN KHO
    // ============================================================
    @Override
    public boolean themPhieuHuy(PhieuHuy ph) {
        try {
            return doInTransaction(em -> {
                // 1. Attach managed NhanVien
                NhanVien nv = em.find(NhanVien.class, ph.getNhanVien().getMaNhanVien());
                if (nv != null) ph.setNhanVien(nv);

                // 2. Tính tổng tiền
                ph.capNhatTongTienTheoChiTiet();

                // 3. Xử lý từng ChiTietPhieuHuy
                for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                    // Set back-reference (owning side) — cần cho @MapsId
                    ct.setPhieuHuy(ph);

                    // Attach managed LoSanPham
                    LoSanPham lo = em.find(LoSanPham.class, ct.getLoSanPham().getMaLo());
                    if (lo != null) ct.setLoSanPham(lo);

                    // Attach managed DonViTinh
                    if (ct.getDonViTinh() != null) {
                        DonViTinh dvt = em.find(DonViTinh.class, ct.getDonViTinh().getMaDonViTinh());
                        if (dvt != null) ct.setDonViTinh(dvt);
                    }
                }

                // 4. Persist PhieuHuy — CASCADE ALL → ChiTietPhieuHuy
                em.persist(ph);
                em.flush(); // INSERT xong trước khi UPDATE tồn kho

                // 5. Trừ tồn kho (soLuongHuy là int, không có type-mismatch)
                for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                    em.createNativeQuery(
                            "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?")
                            .setParameter(1, ct.getSoLuongHuy())
                            .setParameter(2, ct.getLoSanPham().getMaLo())
                            .executeUpdate();
                }

                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thêm phiếu hủy (đã rollback): " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🔄 Cập nhật trạng thái phiếu hủy
    // ============================================================
    @Override
    public boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi) {
        return doInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE PhieuHuy p SET p.trangThai = :tt WHERE p.maPhieuHuy = :ma")
                    .setParameter("tt", trangThaiMoi)
                    .setParameter("ma", maPhieuHuy)
                    .executeUpdate();
            return updated > 0;
        });
    }

    // ============================================================
    // 💰 Tính lại tổng tiền theo chi tiết (không UPDATE DB)
    // ============================================================
    @Override
    public Double tinhTongTienTheoChiTiet(String maPhieuHuy) {
        return doInTransaction(em -> {
            List<PhieuHuy> list = em.createQuery(
                    "SELECT ph FROM PhieuHuy ph JOIN FETCH ph.nhanVien " +
                    "WHERE ph.maPhieuHuy = :ma",
                    PhieuHuy.class)
                    .setParameter("ma", maPhieuHuy)
                    .getResultList();
            if (list.isEmpty()) return null;
            PhieuHuy ph = list.get(0);
            ph.getChiTietPhieuHuyList().size();
            ph.capNhatTongTienTheoChiTiet();
            return ph.getTongTien();
        });
    }

    // ============================================================
    // 🧾 Tạo mã tự động PH-yyyyMMdd-xxxx
    // ============================================================
    @Override
    public String taoMaPhieuHuy() {
        String prefix = "PH-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            int count = (result.isEmpty() || result.get(0) == null) ? 0 : result.get(0).intValue();
            return String.format("%s%04d", prefix, count + 1);
        });
    }

    // ============================================================
    // 🗑️ Xoá phiếu huỷ (CASCADE ALL → ChiTietPhieuHuy tự xóa)
    // ============================================================
    @Override
    public boolean xoa(String maPhieuHuy) {
        return doInTransaction(em -> {
            PhieuHuy ph = em.find(PhieuHuy.class, maPhieuHuy);
            if (ph == null) return false;
            // Khởi tạo lazy collection để CASCADE REMOVE hoạt động đúng
            ph.getChiTietPhieuHuyList().size();
            em.remove(ph);
            return true;
        });
    }

    // ============================================================
    // ✅ Kiểm tra tất cả chi tiết đã xử lý (không còn CHO_DUYET)
    // ============================================================
    @Override
    public boolean checkTrangThai(String maPhieuHuy) {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(ct) FROM ChiTietPhieuHuy ct " +
                    "WHERE ct.id.maPhieuHuy = :ma AND ct.trangThai = :tt",
                    Long.class)
                    .setParameter("ma", maPhieuHuy)
                    .setParameter("tt", ChiTietPhieuHuy.CHO_DUYET)
                    .getSingleResult();
            return count == 0;
        });
    }

    /** Cập nhật trạng thái phiếu hủy nếu đủ điều kiện (không còn CT chờ duyệt) */
    @Override
    public boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy) {
        if (checkTrangThai(maPhieuHuy)) {
            return capNhatTrangThai(maPhieuHuy, true);
        }
        return false;
    }

    // ============================================================
    // 🔔 Đếm số phiếu hủy chưa duyệt (cho Dashboard)
    // ============================================================
    @Override
    public int demPhieuHuyChuaDuyet() {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(ph) FROM PhieuHuy ph WHERE ph.trangThai = false",
                    Long.class)
                    .getSingleResult();
            return count.intValue();
        });
    }

    // ============================================================
    // 📊 Tổng tiền hủy theo tháng — native (MONTH/YEAR SQL Server)
    // ============================================================
    @Override
    public double tinhTongTienHuyTheoThang(int thang, int nam) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(TongTien), 0) FROM PhieuHuy " +
                    "WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ?")
                    .setParameter(1, thang)
                    .setParameter(2, nam)
                    .getResultList();
            return (result.isEmpty() || result.get(0) == null) ? 0.0 : result.get(0).doubleValue();
        });
    }

    // ============================================================
    // 📅 Đếm số phiếu hủy hôm nay của nhân viên — native (GETDATE)
    // ============================================================
    @Override
    public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM PhieuHuy " +
                    "WHERE MaNhanVien = ? AND CAST(NgayLapPhieu AS DATE) = CAST(GETDATE() AS DATE)")
                    .setParameter(1, maNhanVien)
                    .getResultList();
            return result.isEmpty() ? 0 : result.get(0).intValue();
        });
    }
}
