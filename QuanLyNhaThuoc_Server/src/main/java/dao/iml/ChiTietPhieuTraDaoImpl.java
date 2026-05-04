package dao.iml;

import entity.ChiTietPhieuTra;
import entity.DonViTinh;
import dao.ChiTietPhieuTraDao;

import java.util.List;

public class ChiTietPhieuTraDaoImpl
        extends AbstractGenericDaoImpl<ChiTietPhieuTra, ChiTietPhieuTra.Id>
        implements ChiTietPhieuTraDao {

    public ChiTietPhieuTraDaoImpl() {
        super(ChiTietPhieuTra.class);
    }

    // ============================================================
    // 🔍 Lấy danh sách chi tiết theo mã phiếu trả
    //    chiTietHoaDon là insertable=false → chỉ cần JOIN FETCH để đọc
    // ============================================================
    @Override
    public List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT ct FROM ChiTietPhieuTra ct " +
                        "LEFT JOIN FETCH ct.donViTinh " +
                        "WHERE ct.id.maPhieuTra = :ma ORDER BY ct.id.maLo",
                        ChiTietPhieuTra.class)
                        .setParameter("ma", maPhieuTra)
                        .getResultList()
        );
    }

    // ============================================================
    // ➕ Thêm mới 1 chi tiết phiếu trả
    //    Dùng persist trực tiếp — PhieuTra parent đã tồn tại
    // ============================================================
    @Override
    public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
        try {
            return doInTransaction(em -> {
                // Attach managed DonViTinh
                if (ctpt.getDonViTinh() != null) {
                    DonViTinh dvt = em.find(DonViTinh.class, ctpt.getDonViTinh().getMaDonViTinh());
                    if (dvt != null) ctpt.setDonViTinh(dvt);
                }
                // id.maPhieuTra, id.maHoaDon, id.maLo đã được set bởi setPhieuTra() + setChiTietHoaDon()
                // chiTietHoaDon là insertable=false → JPA chỉ dùng các giá trị trong EmbeddedId
                em.persist(ctpt);
                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thêm chi tiết phiếu trả: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🔄 Cập nhật trạng thái 1 chi tiết phiếu trả (JPQL UPDATE)
    // ============================================================
    @Override
    public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon,
                                           String maLo, String maDonViTinh, int trangThaiMoi) {
        return doInTransaction(em -> {
            int updated = em.createNativeQuery(
                    "UPDATE ChiTietPhieuTra SET TrangThai=? " +
                    "WHERE MaPhieuTra=? AND MaHoaDon=? AND MaLo=? AND MaDonViTinh=?")
                    .setParameter(1, trangThaiMoi).setParameter(2, maPhieuTra)
                    .setParameter(3, maHoaDon).setParameter(4, maLo)
                    .setParameter(5, maDonViTinh)
                    .executeUpdate();
            return updated > 0;
        });
    }

    // ============================================================
    // 🔢 Tổng số lượng đã trả (quy về đơn vị gốc) theo HĐ + lô
    //    JOIN QuyCachDongGoi để nhân hệ số — native query
    // ============================================================
    @Override
    public double tongSoLuongDaTra(String maHD, String maLo) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT SUM(ct.SoLuong * qc.HeSoQuyDoi) " +
                    "FROM ChiTietPhieuTra ct " +
                    "JOIN LoSanPham lo ON lo.MaLo = ct.MaLo " +
                    "JOIN QuyCachDongGoi qc ON qc.MaDonViTinh = ct.MaDonViTinh " +
                    "    AND qc.MaSanPham = lo.MaSanPham " +
                    "WHERE ct.MaHoaDon = ? AND ct.MaLo = ?")
                    .setParameter(1, maHD).setParameter(2, maLo)
                    .getResultList();
            return (result.isEmpty() || result.get(0) == null) ? 0.0 : result.get(0).doubleValue();
        });
    }
}
