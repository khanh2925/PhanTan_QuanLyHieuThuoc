package dao.iml;

import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import dao.ChiTietKhuyenMaiSanPhamDao;

import java.time.LocalDate;
import java.util.List;

public class ChiTietKhuyenMaiSanPhamDaoImpl
        extends AbstractGenericDaoImpl<ChiTietKhuyenMaiSanPham, ChiTietKhuyenMaiSanPham.Id>
        implements ChiTietKhuyenMaiSanPhamDao {

    public ChiTietKhuyenMaiSanPhamDaoImpl() {
        super(ChiTietKhuyenMaiSanPham.class);
    }

    @Override
    public List<ChiTietKhuyenMaiSanPham> timKiemChiTietKhuyenMaiSanPhamBangMa(String maKM) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT c FROM ChiTietKhuyenMaiSanPham c " +
                        "JOIN FETCH c.sanPham JOIN FETCH c.khuyenMai " +
                        "WHERE c.id.maKM = :maKM",
                        ChiTietKhuyenMaiSanPham.class)
                        .setParameter("maKM", maKM)
                        .getResultList()
        );
    }

    @Override
    public boolean themChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm) {
        return doInTransaction(em -> {
            // Kiểm tra đã tồn tại
            ChiTietKhuyenMaiSanPham.Id id = new ChiTietKhuyenMaiSanPham.Id(
                    ctkm.getSanPham().getMaSanPham(), ctkm.getKhuyenMai().getMaKM());
            if (em.find(ChiTietKhuyenMaiSanPham.class, id) != null) {
                System.err.println("CTKM đã tồn tại, bỏ qua.");
                return false;
            }
            // Attach managed references
            SanPham sp = em.find(SanPham.class, ctkm.getSanPham().getMaSanPham());
            KhuyenMai km = em.find(KhuyenMai.class, ctkm.getKhuyenMai().getMaKM());
            if (sp != null) ctkm.setSanPham(sp);
            if (km != null) ctkm.setKhuyenMai(km);
            em.persist(ctkm);
            return true;
        });
    }

    @Override
    public boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSP) {
        return doInTransaction(em -> {
            ChiTietKhuyenMaiSanPham.Id id = new ChiTietKhuyenMaiSanPham.Id(maSP, maKM);
            ChiTietKhuyenMaiSanPham ctkm = em.find(ChiTietKhuyenMaiSanPham.class, id);
            if (ctkm != null) {
                em.remove(ctkm);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean xoaTatCaSanPhamCuaKM(String maKM) {
        return doInTransaction(em -> {
            int deleted = em.createQuery(
                    "DELETE FROM ChiTietKhuyenMaiSanPham c WHERE c.id.maKM = :maKM")
                    .setParameter("maKM", maKM)
                    .executeUpdate();
            return deleted >= 0;
        });
    }

    @Override
    public boolean daTonTai(String maKM, String maSP) {
        return doInTransaction(em -> {
            ChiTietKhuyenMaiSanPham.Id id = new ChiTietKhuyenMaiSanPham.Id(maSP, maKM);
            return em.find(ChiTietKhuyenMaiSanPham.class, id) != null;
        });
    }

    @Override
    public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaCoJoin(String maKM) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT c FROM ChiTietKhuyenMaiSanPham c " +
                        "JOIN FETCH c.sanPham JOIN FETCH c.khuyenMai " +
                        "WHERE c.id.maKM = :maKM",
                        ChiTietKhuyenMaiSanPham.class)
                        .setParameter("maKM", maKM)
                        .getResultList()
        );
    }

    /**
     * Lấy CTKM đang hoạt động theo mã SP — chỉ lấy khuyến mãi sản phẩm (không phải hóa đơn).
     */
    @Override
    public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiDangHoatDongTheoMaSP(String maSP) {
        LocalDate today = LocalDate.now();
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT c FROM ChiTietKhuyenMaiSanPham c " +
                        "JOIN FETCH c.sanPham JOIN FETCH c.khuyenMai km " +
                        "WHERE c.id.maSanPham = :maSP " +
                        "AND km.trangThai = true " +
                        "AND :today BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
                        "AND km.soLuongKhuyenMai > 0 " +
                        "AND km.khuyenMaiHoaDon = false",
                        ChiTietKhuyenMaiSanPham.class)
                        .setParameter("maSP", maSP)
                        .setParameter("today", today)
                        .getResultList()
        );
    }
}
