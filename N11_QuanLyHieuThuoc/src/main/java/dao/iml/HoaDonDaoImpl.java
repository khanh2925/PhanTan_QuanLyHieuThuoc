package dao.iml;

import entity.*;
import dao.HoaDonDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDaoImpl
        extends AbstractGenericDaoImpl<HoaDon, String>
        implements HoaDonDao {

    public HoaDonDaoImpl() {
        super(HoaDon.class);
    }

    // ============================================================
    // 🔍 Tìm hóa đơn theo mã — JOIN FETCH header + khởi tạo lazy chi tiết
    // ============================================================
    @Override
    public HoaDon timHoaDonTheoMa(String maHD) {
        return doInTransaction(em -> {
            List<HoaDon> list = em.createQuery(
                    "SELECT h FROM HoaDon h " +
                    "JOIN FETCH h.nhanVien JOIN FETCH h.khachHang LEFT JOIN FETCH h.khuyenMai " +
                    "WHERE h.maHoaDon = :ma",
                    HoaDon.class)
                    .setParameter("ma", maHD)
                    .getResultList();
            if (list.isEmpty()) return null;
            HoaDon hd = list.get(0);
            // Khởi tạo lazy collection danhSachChiTiet trong cùng transaction
            // @ManyToOne trên ChiTietHoaDon là EAGER → auto-loaded khi collection khởi tạo
            hd.getDanhSachChiTiet().size();
            return hd;
        });
    }

    // ============================================================
    // 📜 Lấy tất cả hóa đơn — không dùng static cache (JPA quản lý)
    // ============================================================
    @Override
    public List<HoaDon> layTatCaHoaDon() {
        return doInTransaction(em -> {
            List<HoaDon> list = em.createQuery(
                    "SELECT h FROM HoaDon h " +
                    "JOIN FETCH h.nhanVien JOIN FETCH h.khachHang LEFT JOIN FETCH h.khuyenMai " +
                    "ORDER BY h.ngayLap DESC, h.maHoaDon DESC",
                    HoaDon.class).getResultList();
            // Khởi tạo lazy collection cho từng hóa đơn trong cùng transaction
            for (HoaDon h : list) {
                h.getDanhSachChiTiet().size();
            }
            return new ArrayList<>(list);
        });
    }

    // ============================================================
    // ➕ Thêm hóa đơn + cập nhật tồn kho (transaction đơn)
    // ============================================================
    @Override
    public boolean themHoaDon(HoaDon hd) {
        try {
            return doInTransaction(em -> {
                // 1. Tính toán tổng tiền / giảm giá
                hd.capNhatDuLieuHoaDon();

                // 2. Attach managed references cho HoaDon header
                NhanVien nv = em.find(NhanVien.class, hd.getNhanVien().getMaNhanVien());
                if (nv != null) hd.setNhanVien(nv);

                KhachHang kh = em.find(KhachHang.class, hd.getKhachHang().getMaKhachHang());
                if (kh != null) hd.setKhachHang(kh);

                // 3. Attach managed refs cho từng ChiTietHoaDon
                for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                    LoSanPham lo = em.find(LoSanPham.class, ct.getLoSanPham().getMaLo());
                    if (lo != null) ct.setLoSanPham(lo);

                    DonViTinh dvt = em.find(DonViTinh.class, ct.getDonViTinh().getMaDonViTinh());
                    if (dvt != null) ct.setDonViTinh(dvt);

                    if (ct.getKhuyenMai() != null) {
                        KhuyenMai kmSP = em.find(KhuyenMai.class, ct.getKhuyenMai().getMaKM());
                        if (kmSP != null) ct.setKhuyenMai(kmSP);
                    }
                }

                // 4. Attach KM hóa đơn nếu có
                //    setKhuyenMai() gọi capNhatDuLieuHoaDon() → OK vì chi tiết đã có managed refs
                if (hd.getKhuyenMai() != null) {
                    KhuyenMai kmHD = em.find(KhuyenMai.class, hd.getKhuyenMai().getMaKM());
                    if (kmHD != null) hd.setKhuyenMai(kmHD);
                }

                // 5. Persist HoaDon — CASCADE ALL tự động persist ChiTietHoaDon
                //    @MapsId đảm bảo EmbeddedId được điền từ managed associations
                em.persist(hd);
                // Flush ngay để INSERT HoaDon + ChiTietHoaDon trước khi UPDATE tồn kho
                em.flush();

                // 6. Cập nhật tồn kho cho từng dòng chi tiết
                for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                    String maSP = ct.getLoSanPham().getSanPham().getMaSanPham();
                    String maDVT = ct.getDonViTinh().getMaDonViTinh();
                    String maLo  = ct.getLoSanPham().getMaLo();

                    // Lấy hệ số quy đổi từ QuyCachDongGoi
                    @SuppressWarnings("unchecked")
                    List<Object> heSoList = em.createQuery(
                            "SELECT q.heSoQuyDoi FROM QuyCachDongGoi q " +
                            "WHERE q.sanPham.maSanPham = :maSP " +
                            "AND q.donViTinh.maDonViTinh = :maDVT")
                            .setParameter("maSP", maSP)
                            .setParameter("maDVT", maDVT)
                            .setMaxResults(1)
                            .getResultList();
                    double heSo = heSoList.isEmpty()
                            ? 1.0
                            : ((Number) heSoList.get(0)).doubleValue();

                    double soLuongBase = ct.getSoLuong() * heSo;

                    // Dùng native query để tránh type-mismatch (double vs int column)
                    // Giống hệt SQL gốc của JDBC version
                    int updated = em.createNativeQuery(
                            "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? " +
                            "WHERE MaLo = ? AND SoLuongTon >= ?")
                            .setParameter(1, soLuongBase)
                            .setParameter(2, maLo)
                            .setParameter(3, soLuongBase)
                            .executeUpdate();

                    if (updated == 0) {
                        // RuntimeException → doInTransaction rollback toàn bộ giao dịch
                        throw new RuntimeException("Tồn kho không đủ cho lô: " + maLo);
                    }
                }
                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thêm hóa đơn (đã rollback): " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🧾 Tạo mã hóa đơn — COUNT theo prefix ngày hôm nay
    // ============================================================
    @Override
    public String taoMaHoaDon() {
        String prefix = "HD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            long count = (result.isEmpty() || result.get(0) == null) ? 0L : result.get(0).longValue();
            return String.format("%s%04d", prefix, count + 1);
        });
    }

    // ============================================================
    // 🔍 Tìm hóa đơn theo số điện thoại khách hàng
    // ============================================================
    @Override
    public List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai) {
        return doInTransaction(em -> {
            List<HoaDon> list = em.createQuery(
                    "SELECT h FROM HoaDon h " +
                    "JOIN FETCH h.nhanVien JOIN FETCH h.khachHang kh LEFT JOIN FETCH h.khuyenMai " +
                    "WHERE kh.soDienThoai = :sdt " +
                    "ORDER BY h.ngayLap DESC",
                    HoaDon.class)
                    .setParameter("sdt", soDienThoai)
                    .getResultList();
            for (HoaDon h : list) {
                h.getDanhSachChiTiet().size();
            }
            return new ArrayList<>(list);
        });
    }

    // ============================================================
    // 📊 Thống kê — dùng native query (MONTH/YEAR là SQL Server functions)
    // ============================================================
    @Override
    public double layDoanhThuTheoThang(int thang, int nam) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon " +
                    "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?")
                    .setParameter(1, thang)
                    .setParameter(2, nam)
                    .getResultList();
            return (result.isEmpty() || result.get(0) == null) ? 0.0 : result.get(0).doubleValue();
        });
    }

    @Override
    public int demSoHoaDonTheoThang(int thang, int nam) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM HoaDon " +
                    "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?")
                    .setParameter(1, thang)
                    .setParameter(2, nam)
                    .getResultList();
            return result.isEmpty() ? 0 : result.get(0).intValue();
        });
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void refreshCache() {
        // No-op
    }
}
