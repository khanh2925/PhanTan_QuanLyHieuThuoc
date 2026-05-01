package dao.iml;

import entity.TaiKhoan;
import dao.TaiKhoanDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDaoImpl
        extends AbstractGenericDaoImpl<TaiKhoan, String>
        implements TaiKhoanDao {

    public TaiKhoanDaoImpl() {
        super(TaiKhoan.class);
    }

    @Override
    public ArrayList<TaiKhoan> layTatCaTaiKhoan() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT t FROM TaiKhoan t JOIN FETCH t.nhanVien",
                        TaiKhoan.class).getResultList())
        );
    }

    @Override
    public boolean themTaiKhoan(TaiKhoan tk) {
        return doInTransaction(em -> {
            // Đảm bảo NhanVien đã tồn tại trong context
            if (tk.getNhanVien() != null && tk.getNhanVien().getMaNhanVien() != null) {
                var nv = em.find(
                        entity.NhanVien.class,
                        tk.getNhanVien().getMaNhanVien());
                if (nv != null) tk.setNhanVien(nv);
            }
            em.persist(tk);
            return true;
        });
    }

    @Override
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        if (tk == null || tk.getMaTaiKhoan() == null) return false;
        return doInTransaction(em -> {
            em.merge(tk);
            return true;
        });
    }

    @Override
    public boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi) {
        return doInTransaction(em -> {
            int updated = em.createQuery(
                    "UPDATE TaiKhoan t SET t.matKhau = :mk WHERE t.maTaiKhoan = :ma")
                    .setParameter("mk", matKhauMoi)
                    .setParameter("ma", maTaiKhoan)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public boolean xoaTaiKhoan(String maTaiKhoan) {
        return doInTransaction(em -> {
            TaiKhoan tk = em.find(TaiKhoan.class, maTaiKhoan);
            if (tk != null) {
                em.remove(tk);
                return true;
            }
            return false;
        });
    }

    @Override
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        return doInTransaction(em -> {
            List<TaiKhoan> list = em.createQuery(
                    "SELECT t FROM TaiKhoan t JOIN FETCH t.nhanVien " +
                    "WHERE t.tenDangNhap = :tdn AND t.matKhau = :mk",
                    TaiKhoan.class)
                    .setParameter("tdn", tenDangNhap)
                    .setParameter("mk", matKhau)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(t) FROM TaiKhoan t WHERE t.tenDangNhap = :tdn",
                    Long.class)
                    .setParameter("tdn", tenDangNhap)
                    .getSingleResult();
            return count > 0;
        });
    }

    @Override
    public TaiKhoan layTaiKhoanTheoMa(String maTaiKhoan) {
        return doInTransaction(em -> {
            List<TaiKhoan> list = em.createQuery(
                    "SELECT t FROM TaiKhoan t JOIN FETCH t.nhanVien WHERE t.maTaiKhoan = :ma",
                    TaiKhoan.class)
                    .setParameter("ma", maTaiKhoan)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public String taoMaTaiKhoanTuDong() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "TK-" + today + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT MaTaiKhoan FROM TaiKhoan WHERE MaTaiKhoan LIKE ? ORDER BY MaTaiKhoan DESC LIMIT 1")
                    .setParameter(1, "TK-" + today + "%")
                    .getResultList();
            if (!result.isEmpty()) {
                String last = result.get(0).trim();
                try {
                    int lastNum = Integer.parseInt(last.substring(last.lastIndexOf('-') + 1));
                    return prefix + String.format("%04d", lastNum + 1);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            return prefix + "0001";
        });
    }

    @Override
    public String timTaiKhoanQuenMK(String maNV, String tenNV, String sdt, LocalDate ngaySinh) {
        return doInTransaction(em -> {
            List<String> list = em.createQuery(
                    "SELECT t.maTaiKhoan FROM TaiKhoan t JOIN t.nhanVien n " +
                    "WHERE n.maNhanVien = :maNV AND n.tenNhanVien = :tenNV " +
                    "AND n.soDienThoai = :sdt AND n.ngaySinh = :ns",
                    String.class)
                    .setParameter("maNV", maNV)
                    .setParameter("tenNV", tenNV)
                    .setParameter("sdt", sdt)
                    .setParameter("ns", ngaySinh)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }
}
