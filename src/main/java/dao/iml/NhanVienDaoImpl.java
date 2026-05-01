package dao.iml;

import entity.NhanVien;
import dao.NhanVienDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDaoImpl
        extends AbstractGenericDaoImpl<NhanVien, String>
        implements NhanVienDao {

    public NhanVienDaoImpl() {
        super(NhanVien.class);
    }

    @Override
    public ArrayList<NhanVien> layTatCaNhanVien() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT n FROM NhanVien n ORDER BY n.maNhanVien DESC",
                        NhanVien.class).getResultList())
        );
    }

    @Override
    public boolean themNhanVien(NhanVien nv) {
        return doInTransaction(em -> {
            em.persist(nv);
            return true;
        });
    }

    @Override
    public boolean capNhatNhanVien(NhanVien nv) {
        return doInTransaction(em -> {
            em.merge(nv);
            return true;
        });
    }

    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        return doInTransaction(em -> {
            NhanVien nv = em.find(NhanVien.class, maNhanVien);
            if (nv != null) {
                em.remove(nv);
                return true;
            }
            return false;
        });
    }

    @Override
    public ArrayList<NhanVien> timNhanVien(String tuKhoa) {
        return doInTransaction(em -> {
            String kw = "%" + tuKhoa.trim() + "%";
            return new ArrayList<>(em.createQuery(
                    "SELECT n FROM NhanVien n WHERE n.maNhanVien LIKE :kw " +
                    "OR n.tenNhanVien LIKE :kw OR n.soDienThoai LIKE :kw",
                    NhanVien.class)
                    .setParameter("kw", kw)
                    .getResultList());
        });
    }

    @Override
    public NhanVien timNhanVienTheoMa(String maNhanVien) {
        return doInTransaction(em -> em.find(NhanVien.class, maNhanVien));
    }

    @Override
    public boolean capNhatTrangThai(String maNhanVien, boolean trangThai) {
        return doInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE NhanVien n SET n.trangThai = :tt WHERE n.maNhanVien = :ma")
                    .setParameter("tt", trangThai)
                    .setParameter("ma", maNhanVien)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public String taoMaNhanVienTuDong() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "NV-" + today + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT LTRIM(RTRIM(MaNhanVien)) FROM NhanVien " +
                    "WHERE MaNhanVien LIKE ? ORDER BY MaNhanVien DESC LIMIT 1")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            if (!result.isEmpty()) {
                String last = result.get(0).trim();
                try {
                    int lastNum = Integer.parseInt(last.substring(last.lastIndexOf('-') + 1).trim());
                    return prefix + String.format("%04d", lastNum + 1);
                } catch (NumberFormatException e) {
                    // ignore, fall through
                }
            }
            return prefix + "0001";
        });
    }

    /** JPA không dùng static cache — không cần làm gì */
    @Override
    public void refreshCache() {
        // No-op: JPA manages its own first-level cache per EntityManager
    }

    @Override
    public List<NhanVien> timNhanVienTheoSoDienThoai(String soDienThoai) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT n FROM NhanVien n WHERE n.soDienThoai LIKE :sdt",
                        NhanVien.class)
                        .setParameter("sdt", "%" + soDienThoai + "%")
                        .getResultList()
        );
    }
}
