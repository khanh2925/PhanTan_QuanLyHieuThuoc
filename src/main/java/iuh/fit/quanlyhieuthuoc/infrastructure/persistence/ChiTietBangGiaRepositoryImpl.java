package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.BangGia;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietBangGia;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietBangGiaRepository;

import java.util.List;

public class ChiTietBangGiaRepositoryImpl
        extends AbstractGenericRepositoryImpl<ChiTietBangGia, ChiTietBangGia.Id>
        implements ChiTietBangGiaRepository {

    public ChiTietBangGiaRepositoryImpl() {
        super(ChiTietBangGia.class);
    }

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
    public ChiTietBangGia timChiTietTheoKhoangGia(String maBangGia, double giaNhap) {
        return doInTransaction(em -> {
            List<ChiTietBangGia> list = em.createQuery(
                    "SELECT c FROM ChiTietBangGia c " +
                    "WHERE c.id.maBangGia = :ma AND :gia BETWEEN c.id.giaTu AND c.id.giaDen",
                    ChiTietBangGia.class)
                    .setParameter("ma", maBangGia)
                    .setParameter("gia", giaNhap)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public boolean themChiTietBangGia(ChiTietBangGia ctbg) {
        return doInTransaction(em -> {
            BangGia bgManaged = em.find(BangGia.class, ctbg.getBangGia().getMaBangGia());
            if (bgManaged != null) ctbg.setBangGia(bgManaged);
            em.persist(ctbg);
            return true;
        });
    }

    /**
     * Cập nhật chi tiết bảng giá.
     * Vì PK (giaTu, giaDen) có thể thay đổi, dùng native UPDATE để định danh theo giá trị cũ.
     */
    @Override
    public boolean capNhatChiTietBangGia(ChiTietBangGia ctbg, double giaTuCu, double giaDenCu) {
        return doInTransaction(em -> {
            int updated = em.createNativeQuery(
                    "UPDATE ChiTietBangGia SET GiaTu=?, GiaDen=?, TiLe=? " +
                    "WHERE MaBangGia=? AND GiaTu=? AND GiaDen=?")
                    .setParameter(1, ctbg.getGiaTu())
                    .setParameter(2, ctbg.getGiaDen())
                    .setParameter(3, ctbg.getTiLe())
                    .setParameter(4, ctbg.getBangGia().getMaBangGia())
                    .setParameter(5, giaTuCu)
                    .setParameter(6, giaDenCu)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean xoaChiTietBangGia(String maBangGia, double giaTu, double giaDen) {
        return doInTransaction(em -> {
            int deleted = em.createQuery(
                    "DELETE FROM ChiTietBangGia c " +
                    "WHERE c.id.maBangGia = :ma AND c.id.giaTu = :gtu AND c.id.giaDen = :gdn")
                    .setParameter("ma", maBangGia)
                    .setParameter("gtu", giaTu)
                    .setParameter("gdn", giaDen)
                    .executeUpdate();
            return deleted > 0;
        });
    }

    @Override
    public boolean xoaChiTietTheoMaBangGia(String maBangGia) {
        return doInTransaction(em -> {
            int deleted = em.createQuery(
                    "DELETE FROM ChiTietBangGia c WHERE c.id.maBangGia = :ma")
                    .setParameter("ma", maBangGia)
                    .executeUpdate();
            return deleted >= 0;
        });
    }
}
