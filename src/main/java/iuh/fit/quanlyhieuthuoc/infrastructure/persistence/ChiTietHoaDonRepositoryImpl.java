package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietHoaDon;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietHoaDonRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ChiTietHoaDonRepositoryImpl
        extends AbstractGenericRepositoryImpl<ChiTietHoaDon, ChiTietHoaDon.Id>
        implements ChiTietHoaDonRepository {

    public ChiTietHoaDonRepositoryImpl() {
        super(ChiTietHoaDon.class);
    }

    // ============================================================
    // 🔍 Tìm chi tiết hóa đơn theo mã HD + mã lô + mã đơn vị tính
    // ============================================================
    @Override
    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT) {
        return doInTransaction(em -> {
            List<ChiTietHoaDon> list = em.createQuery(
                    "SELECT ct FROM ChiTietHoaDon ct " +
                    "JOIN FETCH ct.loSanPham lo JOIN FETCH lo.sanPham " +
                    "JOIN FETCH ct.donViTinh LEFT JOIN FETCH ct.khuyenMai " +
                    "WHERE ct.id.maHoaDon = :maHD " +
                    "AND ct.id.maLo = :maLo " +
                    "AND ct.donViTinh.maDonViTinh = :maDVT",
                    ChiTietHoaDon.class)
                    .setParameter("maHD", maHD)
                    .setParameter("maLo", maLo)
                    .setParameter("maDVT", maDVT)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    // ============================================================
    // 📜 Lấy danh sách chi tiết theo mã hóa đơn
    // ============================================================
    @Override
    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
        return doInTransaction(em ->
                em.createQuery(
                        "SELECT ct FROM ChiTietHoaDon ct " +
                        "JOIN FETCH ct.loSanPham lo JOIN FETCH lo.sanPham " +
                        "JOIN FETCH ct.donViTinh LEFT JOIN FETCH ct.khuyenMai " +
                        "WHERE ct.id.maHoaDon = :maHD ORDER BY ct.id.maLo",
                        ChiTietHoaDon.class)
                        .setParameter("maHD", maHD)
                        .getResultList()
        );
    }

    // ============================================================
    // 📊 Đếm số SP khác nhau đã bán hôm nay của nhân viên
    //    Dùng native query: CAST(NgayLap AS DATE) = CAST(? AS DATE) — SQL Server
    // ============================================================
    @Override
    public int demSoSanPhamBanHomNay(String maNhanVien) {
        LocalDate today = LocalDate.now();
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(DISTINCT sp.MaSanPham) " +
                    "FROM ChiTietHoaDon ct " +
                    "JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon " +
                    "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                    "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                    "WHERE hd.MaNhanVien = ? " +
                    "AND CAST(hd.NgayLap AS DATE) = CAST(? AS DATE)")
                    .setParameter(1, maNhanVien)
                    .setParameter(2, Date.valueOf(today))
                    .getResultList();
            return result.isEmpty() ? 0 : result.get(0).intValue();
        });
    }
}
