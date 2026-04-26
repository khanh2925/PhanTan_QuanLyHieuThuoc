package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.KhuyenMai;
import iuh.fit.quanlyhieuthuoc.core.repository.KhuyenMaiRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiRepositoryImpl
        extends AbstractGenericRepositoryImpl<KhuyenMai, String>
        implements KhuyenMaiRepository {

    public KhuyenMaiRepositoryImpl() {
        super(KhuyenMai.class);
    }

    @Override
    public KhuyenMai timKhuyenMaiTheoMa(String maKM) {
        return doInTransaction(em -> em.find(KhuyenMai.class, maKM));
    }

    @Override
    public List<KhuyenMai> layTatCaKhuyenMai() {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT k FROM KhuyenMai k ORDER BY k.ngayBatDau DESC",
                        KhuyenMai.class).getResultList()
        );
    }

    @Override
    public boolean themKhuyenMai(KhuyenMai km) {
        return doInTransaction(em -> {
            em.persist(km);
            return true;
        });
    }

    @Override
    public boolean capNhatKhuyenMai(KhuyenMai km) {
        return doInTransaction(em -> {
            em.merge(km);
            return true;
        });
    }

    @Override
    public boolean giamSoLuong(String maKM) {
        return doInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE KhuyenMai k SET k.soLuongKhuyenMai = k.soLuongKhuyenMai - 1 " +
                    "WHERE k.maKM = :ma AND k.soLuongKhuyenMai > 0")
                    .setParameter("ma", maKM)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public List<KhuyenMai> layKhuyenMaiDangHoatDong() {
        LocalDate today = LocalDate.now();
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT k FROM KhuyenMai k " +
                        "WHERE k.trangThai = true " +
                        "AND :today BETWEEN k.ngayBatDau AND k.ngayKetThuc " +
                        "AND k.soLuongKhuyenMai > 0 " +
                        "ORDER BY k.ngayBatDau DESC",
                        KhuyenMai.class)
                        .setParameter("today", today)
                        .getResultList()
        );
    }

    @Override
    public String taoMaKhuyenMai() {
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "KM-" + ngay + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT MAX(MaKM) FROM KhuyenMai WHERE MaKM LIKE ?")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                String maMax = result.get(0).trim();
                String[] parts = maMax.split("-");
                if (parts.length == 3) {
                    try {
                        int soThuTu = Integer.parseInt(parts[2].trim()) + 1;
                        return prefix + String.format("%04d", soThuTu);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            return prefix + "0001";
        });
    }

    @Override
    public boolean xoaKhuyenMai(String maKM) {
        return doInTransaction(em -> {
            KhuyenMai km = em.find(KhuyenMai.class, maKM);
            if (km != null) {
                em.remove(km);
                return true;
            }
            return false;
        });
    }
}
