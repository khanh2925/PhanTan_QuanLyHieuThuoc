package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuNhap;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietPhieuNhapRepository;

import java.util.List;

public class ChiTietPhieuNhapRepositoryImpl
        extends AbstractGenericRepositoryImpl<ChiTietPhieuNhap, ChiTietPhieuNhap.Id>
        implements ChiTietPhieuNhapRepository {

    public ChiTietPhieuNhapRepositoryImpl() {
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
