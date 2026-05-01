package dao.iml;

import entity.NhaCungCap;
import dao.NhaCungCapDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDaoImpl
        extends AbstractGenericDaoImpl<NhaCungCap, String>
        implements NhaCungCapDao {

    public NhaCungCapDaoImpl() {
        super(NhaCungCap.class);
    }

    @Override
    public List<NhaCungCap> layTatCaNhaCungCap() {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT n FROM NhaCungCap n ORDER BY n.maNhaCungCap DESC",
                        NhaCungCap.class).getResultList()
        );
    }

    @Override
    public boolean themNhaCungCap(NhaCungCap ncc) {
        return doInTransaction(em -> {
            em.persist(ncc);
            return true;
        });
    }

    @Override
    public boolean capNhatNhaCungCap(NhaCungCap ncc) {
        return doInTransaction(em -> {
            em.merge(ncc);
            return true;
        });
    }

    @Override
    public String taoMaTuDong() {
        String ngay = LocalDate.now().toString().replaceAll("-", "");
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Object> result = em.createNativeQuery(
                    "SELECT MAX(RIGHT(MaNhaCungCap, 4)) FROM NhaCungCap WHERE MaNhaCungCap LIKE 'NCC-%'")
                    .getResultList();
            int so = 1;
            if (!result.isEmpty() && result.get(0) != null) {
                try {
                    so = Integer.parseInt(result.get(0).toString()) + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            return String.format("NCC-%s-%04d", ngay, so);
        });
    }

    @Override
    public NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword) {
        return doInTransaction(em -> {
            List<NhaCungCap> list = em.createQuery(
                    "SELECT n FROM NhaCungCap n WHERE n.maNhaCungCap = :kw OR n.soDienThoai = :kw",
                    NhaCungCap.class)
                    .setParameter("kw", keyword)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    /**
     * Tìm kiếm nâng cao theo keyword, khu vực, trạng thái, tiêu chí sắp xếp.
     * Khu vực được lọc ở tầng Java (trích phần sau dấu phẩy cuối trong DiaChi).
     */
    @Override
    public List<NhaCungCap> timKiemNCC(String keyword, String khuVuc, String trangThai, String tieuChi) {
        return doInTransaction(em -> {
            // Xây JPQL động
            StringBuilder jpql = new StringBuilder("SELECT n FROM NhaCungCap n WHERE 1 = 1");

            if (!keyword.isEmpty()) {
                jpql.append(" AND (n.maNhaCungCap LIKE :kw OR n.tenNhaCungCap LIKE :kw" +
                        " OR n.soDienThoai LIKE :kw OR n.email LIKE :kw)");
            }
            if (trangThai.equals("Đang hợp tác")) {
                jpql.append(" AND n.hoatDong = true");
            } else if (trangThai.equals("Ngừng hợp tác")) {
                jpql.append(" AND n.hoatDong = false");
            }
            if (tieuChi.equals("Tên A-Z")) {
                jpql.append(" ORDER BY n.tenNhaCungCap ASC");
            } else if (tieuChi.equals("Mới nhất")) {
                jpql.append(" ORDER BY n.maNhaCungCap DESC");
            } else {
                jpql.append(" ORDER BY n.maNhaCungCap");
            }

            var query = em.createQuery(jpql.toString(), NhaCungCap.class);
            if (!keyword.isEmpty()) {
                query.setParameter("kw", "%" + keyword + "%");
            }

            List<NhaCungCap> ds = query.getResultList();

            // Lọc khu vực ở tầng Java (trích phần sau dấu phẩy cuối trong DiaChi)
            boolean filterKhuVuc = !khuVuc.equals("Tất cả");
            if (!filterKhuVuc) return ds;

            List<NhaCungCap> filtered = new ArrayList<>();
            for (NhaCungCap ncc : ds) {
                String khuVucNCC = getKhuVucFromDiaChi(ncc.getDiaChi());
                if (khuVucNCC.equalsIgnoreCase(khuVuc)) {
                    filtered.add(ncc);
                }
            }
            return filtered;
        });
    }

    /** Trích khu vực (tỉnh/thành) từ địa chỉ — phần sau dấu phẩy cuối */
    private String getKhuVucFromDiaChi(String diaChi) {
        if (diaChi == null || diaChi.isBlank()) return "Không xác định";
        if (diaChi.contains(",")) {
            return diaChi.substring(diaChi.lastIndexOf(",") + 1).trim();
        }
        return diaChi.trim();
    }

    /** JPA không dùng static cache — không cần làm gì */
    @Override
    public void refreshCache() {
        // No-op
    }
}
