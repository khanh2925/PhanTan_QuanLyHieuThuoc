package dao.iml;

import entity.*;
import dao.PhieuNhapDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDaoImpl
        extends AbstractGenericDaoImpl<PhieuNhap, String>
        implements PhieuNhapDao {

    public PhieuNhapDaoImpl() {
        super(PhieuNhap.class);
    }

    // ============================================================
    // 📜 Lấy danh sách phiếu nhập (header only, không load chi tiết)
    // ============================================================
    @Override
    public List<PhieuNhap> layDanhSachPhieuNhap() {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT p FROM PhieuNhap p " +
                        "JOIN FETCH p.nhanVien JOIN FETCH p.nhaCungCap " +
                        "ORDER BY p.ngayNhap DESC",
                        PhieuNhap.class).getResultList())
        );
    }

    // ============================================================
    // 🔍 Tìm phiếu nhập theo mã — bao gồm cả danh sách chi tiết
    // ============================================================
    @Override
    public PhieuNhap timPhieuNhapTheoMa(String maPhieuNhap) {
        return doInTransaction(em -> {
            List<PhieuNhap> list = em.createQuery(
                    "SELECT p FROM PhieuNhap p " +
                    "JOIN FETCH p.nhanVien JOIN FETCH p.nhaCungCap " +
                    "WHERE p.maPhieuNhap = :ma",
                    PhieuNhap.class)
                    .setParameter("ma", maPhieuNhap)
                    .getResultList();
            if (list.isEmpty()) return null;
            PhieuNhap pn = list.get(0);
            // Khởi tạo lazy collection chi tiết trong cùng transaction
            pn.getChiTietPhieuNhapList().size();
            return pn;
        });
    }

    // ============================================================
    // ➕ Thêm phiếu nhập: tạo LoSanPham mới + persist phiếu + CASCADE chi tiết
    // ============================================================
    @Override
    public boolean themPhieuNhap(PhieuNhap pn) {
        try {
            return doInTransaction(em -> {
                // 1. Attach managed refs cho header
                NhaCungCap ncc = em.find(NhaCungCap.class, pn.getNhaCungCap().getMaNhaCungCap());
                if (ncc != null) pn.setNhaCungCap(ncc);
                NhanVien nv = em.find(NhanVien.class, pn.getNhanVien().getMaNhanVien());
                if (nv != null) pn.setNhanVien(nv);

                // 2. Xử lý từng ChiTietPhieuNhap
                for (ChiTietPhieuNhap ct : pn.getChiTietPhieuNhapList()) {
                    // Set back-reference sang parent (bắt buộc với @MapsId)
                    ct.setPhieuNhap(pn);

                    // LoSanPham là mới — cần persist trước khi persist PhieuNhap
                    LoSanPham lo = ct.getLoSanPham();
                    SanPham sp = em.find(SanPham.class, lo.getSanPham().getMaSanPham());
                    if (sp != null) lo.setSanPham(sp);
                    em.persist(lo); // Lô mới → persist riêng

                    // Attach managed DonViTinh
                    DonViTinh dvt = em.find(DonViTinh.class, ct.getDonViTinh().getMaDonViTinh());
                    if (dvt != null) ct.setDonViTinh(dvt);
                }

                // 3. Tính tổng tiền
                pn.capNhatTongTienTheoChiTiet();

                // 4. Persist phiếu nhập (CASCADE ALL → persist ChiTietPhieuNhap)
                em.persist(pn);
                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thêm phiếu nhập (đã rollback): " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🧾 Tạo mã phiếu nhập tự động — MAX theo prefix ngày
    // ============================================================
    @Override
    public String taoMaPhieuNhap() {
        String ngayHomNay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PN-" + ngayHomNay + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT MAX(MaPhieuNhap) FROM PhieuNhap WHERE MaPhieuNhap LIKE ?")
                    .setParameter(1, prefix + "%")
                    .getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                String maMax = result.get(0).trim();
                try {
                    int soCuoi = Integer.parseInt(maMax.substring(maMax.length() - 4));
                    return String.format("%s%04d", prefix, soCuoi + 1);
                } catch (NumberFormatException ignore) {}
            }
            return prefix + "0001";
        });
    }

    // ============================================================
    // 🔍 Tìm kiếm phiếu nhập theo keyword + khoảng ngày
    // ============================================================
    @Override
    public List<PhieuNhap> timKiemPhieuNhap(String keyword, java.util.Date tuNgay, java.util.Date denNgay) {
        LocalDate start = new java.sql.Date(tuNgay.getTime()).toLocalDate();
        LocalDate end   = new java.sql.Date(denNgay.getTime()).toLocalDate();
        String kw = "%" + keyword.trim() + "%";
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT p FROM PhieuNhap p " +
                        "JOIN FETCH p.nhanVien nv JOIN FETCH p.nhaCungCap ncc " +
                        "WHERE (p.maPhieuNhap LIKE :kw OR ncc.tenNhaCungCap LIKE :kw OR nv.tenNhanVien LIKE :kw) " +
                        "AND p.ngayNhap BETWEEN :start AND :end",
                        PhieuNhap.class)
                        .setParameter("kw", kw)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .getResultList())
        );
    }

    // ============================================================
    // 🔍 Lọc phiếu nhập theo mã nhà cung cấp
    // ============================================================
    @Override
    public List<PhieuNhap> layPhieuNhapTheoNhaCungCap(String maNCC) {
        return doInTransaction(em ->
                new ArrayList<>(em.createQuery(
                        "SELECT p FROM PhieuNhap p JOIN FETCH p.nhanVien " +
                        "JOIN FETCH p.nhaCungCap ncc " +
                        "WHERE ncc.maNhaCungCap = :maNCC ORDER BY p.ngayNhap DESC",
                        PhieuNhap.class)
                        .setParameter("maNCC", maNCC)
                        .getResultList())
        );
    }

    // ============================================================
    // 📊 Thống kê — MONTH/YEAR là SQL Server functions → native query
    // ============================================================
    @Override
    public double tinhTongTienNhapTheoThang(int thang, int nam) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(TongTien), 0) FROM PhieuNhap " +
                    "WHERE MONTH(NgayNhap) = ? AND YEAR(NgayNhap) = ?")
                    .setParameter(1, thang)
                    .setParameter(2, nam)
                    .getResultList();
            return (result.isEmpty() || result.get(0) == null) ? 0.0 : result.get(0).doubleValue();
        });
    }
}
