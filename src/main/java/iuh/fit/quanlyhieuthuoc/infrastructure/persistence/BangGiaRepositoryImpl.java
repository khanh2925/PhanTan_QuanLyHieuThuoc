package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.BangGia;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietBangGia;
import iuh.fit.quanlyhieuthuoc.core.repository.BangGiaRepository;

import java.time.LocalDate;
import java.util.List;

public class BangGiaRepositoryImpl
        extends AbstractGenericRepositoryImpl<BangGia, String>
        implements BangGiaRepository {

    public BangGiaRepositoryImpl() {
        super(BangGia.class);
    }

    @Override
    public List<BangGia> layTatCaBangGia() {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT b FROM BangGia b LEFT JOIN FETCH b.nhanVien ORDER BY b.ngayApDung DESC",
                        BangGia.class).getResultList()
        );
    }

    @Override
    public BangGia layBangGiaDangHoatDong() {
        return doInTransaction(em -> {
            List<BangGia> list = em.createQuery(
                    "SELECT b FROM BangGia b LEFT JOIN FETCH b.nhanVien WHERE b.hoatDong = true",
                    BangGia.class).getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public BangGia timBangGiaTheoMa(String maBangGia) {
        return doInTransaction(em -> {
            List<BangGia> list = em.createQuery(
                    "SELECT b FROM BangGia b LEFT JOIN FETCH b.nhanVien WHERE b.maBangGia = :ma",
                    BangGia.class)
                    .setParameter("ma", maBangGia)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public boolean themBangGia(BangGia bg) {
        return doInTransaction(em -> {
            // Attach NhanVien vào context hiện tại nếu cần
            if (bg.getNhanVien() != null && bg.getNhanVien().getMaNhanVien() != null) {
                var nv = em.find(
                        iuh.fit.quanlyhieuthuoc.core.entity.NhanVien.class,
                        bg.getNhanVien().getMaNhanVien());
                if (nv != null) bg.setNhanVien(nv);
            }
            em.persist(bg);
            return true;
        });
    }

    @Override
    public boolean capNhatBangGia(BangGia bg) {
        return doInTransaction(em -> {
            em.merge(bg);
            return true;
        });
    }

    @Override
    public boolean huyHoatDongTatCaTruBangGia(String maBangGia) {
        return doInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE BangGia b SET b.hoatDong = false WHERE b.maBangGia <> :ma")
                    .setParameter("ma", maBangGia)
                    .executeUpdate();
            return updated >= 0; // thành công dù không có gì thay đổi
        });
    }

    @Override
    public boolean xoaBangGia(String maBangGia) {
        return doInTransaction(em -> {
            BangGia bg = em.find(BangGia.class, maBangGia);
            if (bg != null) {
                em.remove(bg);
                return true;
            }
            return false;
        });
    }

    @Override
    public void lamMoiCache() {
        // JPA không dùng static cache — no-op
    }

    // ===== Chi tiết bảng giá (phương thức tiện ích nhúng trong BangGiaRepository) =====

    @Override
    public List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT c FROM ChiTietBangGia c WHERE c.id.maBangGia = :ma",
                        ChiTietBangGia.class)
                        .setParameter("ma", maBangGia)
                        .getResultList()
        );
    }

    @Override
    public boolean themChiTietBangGia(ChiTietBangGia ct) {
        return doInTransaction(em -> {
            // Attach BangGia managed reference
            BangGia bgManaged = em.find(BangGia.class, ct.getBangGia().getMaBangGia());
            if (bgManaged != null) ct.setBangGia(bgManaged);
            em.persist(ct);
            return true;
        });
    }

    @Override
    public boolean xoaTatCaChiTiet(String maBangGia) {
        return doInTransaction(em -> {
            int deleted = em.createQuery(
                    "DELETE FROM ChiTietBangGia c WHERE c.id.maBangGia = :ma")
                    .setParameter("ma", maBangGia)
                    .executeUpdate();
            return deleted >= 0;
        });
    }

    @Override
    public String taoMaBangGia() {
        String today = LocalDate.now().toString().replaceAll("-", "");
        String prefix = "BG-" + today + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT MAX(MaBangGia) FROM BangGia WHERE MaBangGia LIKE ?")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                String last = result.get(0).trim();
                try {
                    int num = Integer.parseInt(last.substring(last.lastIndexOf("-") + 1));
                    return prefix + String.format("%04d", num + 1);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            return prefix + "0001";
        });
    }
}
