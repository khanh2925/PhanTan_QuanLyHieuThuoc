package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.DonViTinh;
import iuh.fit.quanlyhieuthuoc.core.repository.DonViTinhRepository;

import java.util.List;

public class DonViTinhRepositoryImpl
        extends AbstractGenericRepositoryImpl<DonViTinh, String>
        implements DonViTinhRepository {

    public DonViTinhRepositoryImpl() {
        super(DonViTinh.class);
    }

    @Override
    public List<DonViTinh> layTatCaDonViTinh() {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT d FROM DonViTinh d ORDER BY d.maDonViTinh",
                        DonViTinh.class).getResultList()
        );
    }

    @Override
    public boolean themDonViTinh(DonViTinh dvt) {
        return doInTransaction(em -> {
            em.persist(dvt);
            return true;
        });
    }

    @Override
    public boolean capNhatDonViTinh(DonViTinh dvt) {
        return doInTransaction(em -> {
            em.merge(dvt);
            return true;
        });
    }

    @Override
    public boolean xoaDonViTinh(String maDonViTinh) {
        return doInTransaction(em -> {
            DonViTinh dvt = em.find(DonViTinh.class, maDonViTinh);
            if (dvt != null) {
                em.remove(dvt);
                return true;
            }
            return false;
        });
    }

    @Override
    public DonViTinh timDonViTinhTheoMa(String maDonViTinh) {
        return doInTransaction(em -> em.find(DonViTinh.class, maDonViTinh));
    }

    @Override
    public String taoMaTuDong() {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Object> result = em.createNativeQuery(
                    "SELECT MAX(CAST(SUBSTRING(MaDonViTinh, 5, 3) AS INT)) FROM DonViTinh")
                    .getResultList();
            int so = 1;
            if (!result.isEmpty() && result.get(0) != null) {
                try {
                    so = Integer.parseInt(result.get(0).toString()) + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            return String.format("DVT-%03d", so);
        });
    }
}
