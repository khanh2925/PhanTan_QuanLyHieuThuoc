package dao.iml;

import entity.ChiTietPhieuHuy;
import dao.ChiTietPhieuHuyDao;

import java.util.List;

public class ChiTietPhieuHuyDaoImpl
        extends AbstractGenericDaoImpl<ChiTietPhieuHuy, ChiTietPhieuHuy.Id>
        implements ChiTietPhieuHuyDao {

    public ChiTietPhieuHuyDaoImpl() {
        super(ChiTietPhieuHuy.class);
    }

    // ============================================================
    // 🔍 Lấy danh sách chi tiết theo mã phiếu (JPQL + JOIN FETCH)
    // ============================================================
    @Override
    public List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy) {
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
    // 🔄 Cập nhật trạng thái + hoàn tồn kho nếu TỪ CHỐI (=3)
    // ============================================================
    @Override
    public boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThaiMoi) {
        try {
            return doInTransaction(em -> {
                // 1. Update trạng thái chi tiết
                int updated = em.createQuery(
                        "UPDATE ChiTietPhieuHuy ct SET ct.trangThai = :tt " +
                        "WHERE ct.id.maPhieuHuy = :maPH AND ct.id.maLo = :maLo")
                        .setParameter("tt", trangThaiMoi)
                        .setParameter("maPH", maPhieuHuy)
                        .setParameter("maLo", maLo)
                        .executeUpdate();

                if (updated == 0) return false;

                // 2. TỪ CHỐI (=3): cộng lại tồn kho (đã trừ khi tạo phiếu hủy)
                if (trangThaiMoi == ChiTietPhieuHuy.TU_CHOI) {
                    em.createNativeQuery(
                            "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + " +
                            "(SELECT SoLuongHuy FROM ChiTietPhieuHuy WHERE MaPhieuHuy=? AND MaLo=?) " +
                            "WHERE MaLo = ?")
                            .setParameter(1, maPhieuHuy)
                            .setParameter(2, maLo)
                            .setParameter(3, maLo)
                            .executeUpdate();
                }
                // HUY_HANG (=2): tồn kho đã trừ khi tạo phiếu → không làm gì thêm

                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái CT phiếu hủy: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🗑️ Xoá chi tiết + hoàn tồn kho nếu CHO_DUYET hoặc HUY_HANG
    // ============================================================
    @Override
    public boolean xoaChiTietPhieuHuy(ChiTietPhieuHuy ct) {
        try {
            return doInTransaction(em -> {
                ChiTietPhieuHuy managed = em.find(
                        ChiTietPhieuHuy.class,
                        new ChiTietPhieuHuy.Id(
                                ct.getPhieuHuy().getMaPhieuHuy(),
                                ct.getLoSanPham().getMaLo()));
                if (managed == null) return false;

                // Cộng lại tồn nếu trangThai = 1 (Chờ duyệt) hoặc 2 (Hủy hàng)
                // Không cộng khi trangThai = 3 (Từ chối) vì đã cộng lại khi từ chối
                if (managed.getTrangThai() == ChiTietPhieuHuy.CHO_DUYET
                        || managed.getTrangThai() == ChiTietPhieuHuy.HUY_HANG) {
                    em.createNativeQuery(
                            "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaLo = ?")
                            .setParameter(1, managed.getSoLuongHuy())
                            .setParameter(2, managed.getLoSanPham().getMaLo())
                            .executeUpdate();
                }

                em.remove(managed);
                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi xóa chi tiết phiếu hủy: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ✅ Kiểm tra tất cả chi tiết đã xử lý chưa (không còn CHO_DUYET)
    // ============================================================
    @Override
    public boolean tatCaChiTietDaXuLy(String maPhieuHuy) {
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
}
