package dao.iml;

import entity.ChiTietPhieuNhap;
import dao.ChiTietPhieuNhapDao;

import java.util.List;

public class ChiTietPhieuNhapDaoImpl
        extends AbstractGenericDaoImpl<ChiTietPhieuNhap, ChiTietPhieuNhap.Id>
        implements ChiTietPhieuNhapDao {

    public ChiTietPhieuNhapDaoImpl() {
        super(ChiTietPhieuNhap.class);
    }

    // ============================================================
    // 📦 Lấy danh sách chi tiết phiếu nhập theo mã phiếu
    // ============================================================
    @Override
    public List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT ct FROM ChiTietPhieuNhap ct " +
                        "JOIN FETCH ct.loSanPham lo JOIN FETCH lo.sanPham " +
                        "JOIN FETCH ct.donViTinh " +
                        "WHERE ct.id.maPhieuNhap = :ma ORDER BY ct.id.maLo",
                        ChiTietPhieuNhap.class)
                        .setParameter("ma", maPhieuNhap)
                        .getResultList()
        );
    }
}
