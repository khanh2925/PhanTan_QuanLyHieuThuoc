package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.DonViTinh;
import iuh.fit.quanlyhieuthuoc.core.entity.QuyCachDongGoi;
import iuh.fit.quanlyhieuthuoc.core.entity.SanPham;
import iuh.fit.quanlyhieuthuoc.core.repository.QuyCachDongGoiRepository;

import java.util.ArrayList;
import java.util.List;

public class QuyCachDongGoiRepositoryImpl
        extends AbstractGenericRepositoryImpl<QuyCachDongGoi, String>
        implements QuyCachDongGoiRepository {

    public QuyCachDongGoiRepositoryImpl() {
        super(QuyCachDongGoi.class);
    }

    @Override
    public ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT q FROM QuyCachDongGoi q JOIN FETCH q.donViTinh JOIN FETCH q.sanPham",
                        QuyCachDongGoi.class).getResultList())
        );
    }

    @Override
    public String taoMaQuyCach() {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT TOP 1 MaQuyCach FROM QuyCachDongGoi WHERE MaQuyCach LIKE 'QC-%' ORDER BY MaQuyCach DESC")
                    .getResultList();
            if (!result.isEmpty()) {
                String last = result.get(0);
                if (last != null && last.matches("^QC-\\d{6}$")) {
                    try {
                        int lastNum = Integer.parseInt(last.substring(3));
                        return String.format("QC-%06d", lastNum + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            return "QC-000001";
        });
    }

    @Override
    public boolean themQuyCachDongGoi(QuyCachDongGoi q) {
        return doInTransaction(em -> {
            // Attach managed references để tránh detached entity exception
            SanPham sp = em.find(SanPham.class, q.getSanPham().getMaSanPham());
            if (sp != null) q.setSanPham(sp);
            DonViTinh dvt = em.find(DonViTinh.class, q.getDonViTinh().getMaDonViTinh());
            if (dvt != null) q.setDonViTinh(dvt);
            em.persist(q);
            return true;
        });
    }

    @Override
    public boolean capNhatQuyCachDongGoi(QuyCachDongGoi q) {
        return doInTransaction(em -> {
            em.merge(q);
            return true;
        });
    }

    @Override
    public QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham) {
        return doInTransaction(em -> {
            List<QuyCachDongGoi> list = em.createQuery(
                    "SELECT q FROM QuyCachDongGoi q JOIN FETCH q.donViTinh JOIN FETCH q.sanPham " +
                    "WHERE q.sanPham.maSanPham = :maSP AND q.donViGoc = true",
                    QuyCachDongGoi.class)
                    .setParameter("maSP", maSanPham)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public ArrayList<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham) {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT q FROM QuyCachDongGoi q JOIN FETCH q.donViTinh JOIN FETCH q.sanPham " +
                        "WHERE q.sanPham.maSanPham = :maSP ORDER BY q.heSoQuyDoi ASC",
                        QuyCachDongGoi.class)
                        .setParameter("maSP", maSanPham)
                        .getResultList())
        );
    }

    @Override
    public QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh) {
        return doInTransaction(em -> {
            List<QuyCachDongGoi> list = em.createQuery(
                    "SELECT q FROM QuyCachDongGoi q JOIN FETCH q.donViTinh JOIN FETCH q.sanPham " +
                    "WHERE q.sanPham.maSanPham = :maSP AND q.donViTinh.maDonViTinh = :maDVT",
                    QuyCachDongGoi.class)
                    .setParameter("maSP", maSanPham)
                    .setParameter("maDVT", maDonViTinh)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public boolean xoaQuyCachDongGoi(String maQuyCach) {
        return doInTransaction(em -> {
            QuyCachDongGoi q = em.find(QuyCachDongGoi.class, maQuyCach);
            if (q != null) {
                em.remove(q);
                return true;
            }
            return false;
        });
    }
}
