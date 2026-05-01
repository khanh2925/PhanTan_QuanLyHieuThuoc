package dao.iml;

import entity.*;
import dao.PhieuTraDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuTraDaoImpl
        extends AbstractGenericDaoImpl<PhieuTra, String>
        implements PhieuTraDao {

    public PhieuTraDaoImpl() {
        super(PhieuTra.class);
    }

    /** JPA không dùng static cache — no-op */
    @Override
    public void clearCache() { /* no-op */ }

    /** JPA không dùng static cache — no-op */
    @Override
    public void refreshCache() { /* no-op */ }

    // ============================================================
    // 🔍 Tìm phiếu theo mã — JOIN FETCH header + khởi tạo lazy chi tiết
    // ============================================================
    @Override
    public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {
        return doInTransaction(em -> {
            List<PhieuTra> list = em.createQuery(
                    "SELECT pt FROM PhieuTra pt " +
                    "JOIN FETCH pt.nhanVien JOIN FETCH pt.khachHang " +
                    "WHERE pt.maPhieuTra = :ma",
                    PhieuTra.class)
                    .setParameter("ma", maPhieuTra)
                    .getResultList();
            if (list.isEmpty()) return null;
            PhieuTra pt = list.get(0);
            pt.getChiTietPhieuTraList().size(); // init LAZY
            return pt;
        });
    }

    // ============================================================
    // 📜 Lấy tất cả phiếu trả — JOIN FETCH header + khởi tạo lazy chi tiết
    // ============================================================
    @Override
    public List<PhieuTra> layTatCaPhieuTra() {
        return doInTransaction(em -> {
            List<PhieuTra> list = em.createQuery(
                    "SELECT pt FROM PhieuTra pt " +
                    "JOIN FETCH pt.nhanVien JOIN FETCH pt.khachHang " +
                    "ORDER BY pt.ngayLap DESC, pt.maPhieuTra DESC",
                    PhieuTra.class).getResultList();
            for (PhieuTra pt : list) {
                pt.getChiTietPhieuTraList().size(); // init LAZY
            }
            return new ArrayList<>(list);
        });
    }

    // ============================================================
    // ➕ Thêm phiếu trả + danh sách chi tiết (CASCADE)
    // ============================================================
    @Override
    public boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet) {
        try {
            return doInTransaction(em -> {
                // 1. Attach managed refs cho PhieuTra header
                NhanVien nv = em.find(NhanVien.class, pt.getNhanVien().getMaNhanVien());
                if (nv != null) pt.setNhanVien(nv);
                KhachHang kh = em.find(KhachHang.class, pt.getKhachHang().getMaKhachHang());
                if (kh != null) pt.setKhachHang(kh);

                // 2. Xử lý từng ChiTietPhieuTra
                for (ChiTietPhieuTra ct : dsChiTiet) {
                    // Set back-reference (id.maPhieuTra via @MapsId)
                    ct.setPhieuTra(pt);

                    // id.maHoaDon + id.maLo đã được set bởi setChiTietHoaDon() ở GUI
                    // chiTietHoaDon là insertable=false → không cần attach managed entity

                    // Attach managed DonViTinh
                    if (ct.getDonViTinh() != null) {
                        DonViTinh dvt = em.find(DonViTinh.class, ct.getDonViTinh().getMaDonViTinh());
                        if (dvt != null) ct.setDonViTinh(dvt);
                    }
                }

                // 3. Gắn collection vào parent (cập nhật tongTienHoan luôn)
                pt.setChiTietPhieuTraList(dsChiTiet);

                // 4. Persist (CASCADE ALL → ChiTietPhieuTra)
                em.persist(pt);
                return true;
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi thêm phiếu trả (đã rollback): " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // 🔄 Cập nhật trạng thái chi tiết + tồn kho + tạo PhieuHuy tự động
    //    Trả về "OK", "OK|maPhieuHuy", hoặc "ERR"
    // ============================================================
    @Override
    public String capNhatTrangThai_GiaoDich(String maPhieuTra, String maHoaDon, String maLo,
                                            String maDonViTinh, NhanVien nv, int trangThaiMoi) {
        try {
            return doInTransaction(em -> {

                // ==================================================
                // 1. Lấy trạng thái cũ + số lượng + mã SP
                // ==================================================
                @SuppressWarnings("unchecked")
                List<Object[]> oldRows = em.createNativeQuery(
                        "SELECT ct.TrangThai, ct.SoLuong, lo.MaSanPham " +
                        "FROM ChiTietPhieuTra ct JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                        "WHERE ct.MaPhieuTra=? AND ct.MaHoaDon=? AND ct.MaLo=? AND ct.MaDonViTinh=?")
                        .setParameter(1, maPhieuTra).setParameter(2, maHoaDon)
                        .setParameter(3, maLo).setParameter(4, maDonViTinh)
                        .getResultList();

                if (oldRows.isEmpty()) return "ERR";
                Object[] row = oldRows.get(0);
                int trangThaiCu  = ((Number) row[0]).intValue();
                int soLuongTra   = ((Number) row[1]).intValue();
                String maSanPham = (String) row[2];

                // ==================================================
                // 2. Chặn đổi từ HỦY (2) sang trạng thái khác
                // ==================================================
                if (trangThaiCu == 2 && trangThaiMoi != 2) return "ERR";

                // ==================================================
                // 3. Lấy hệ số quy đổi
                // ==================================================
                int heSoQuyDoi = 1;
                if (maSanPham != null && maDonViTinh != null && !maDonViTinh.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<Number> heSoList = em.createNativeQuery(
                            "SELECT HeSoQuyDoi FROM QuyCachDongGoi WHERE MaSanPham=? AND MaDonViTinh=?")
                            .setParameter(1, maSanPham).setParameter(2, maDonViTinh)
                            .getResultList();
                    if (!heSoList.isEmpty() && heSoList.get(0) != null) {
                        heSoQuyDoi = heSoList.get(0).intValue();
                    }
                }

                // ==================================================
                // 4. Tính delta tồn kho (quy về đơn vị gốc)
                // ==================================================
                int delta = 0;
                if (trangThaiCu != trangThaiMoi) {
                    int soLuongGoc = soLuongTra * heSoQuyDoi;
                    if (trangThaiCu == 0 && trangThaiMoi == 1) delta = +soLuongGoc; // chờ → nhập kho
                    if (trangThaiCu == 1 && trangThaiMoi == 0) delta = -soLuongGoc; // nhập kho → chờ
                    if (trangThaiCu == 1 && trangThaiMoi == 2) delta = -soLuongGoc; // nhập kho → hủy
                }

                // ==================================================
                // 5. Update tồn kho
                // ==================================================
                if (delta != 0) {
                    em.createNativeQuery(
                            "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaLo = ?")
                            .setParameter(1, delta).setParameter(2, maLo)
                            .executeUpdate();
                }

                // ==================================================
                // 6. Update trạng thái chi tiết
                // ==================================================
                em.createNativeQuery(
                        "UPDATE ChiTietPhieuTra SET TrangThai=? " +
                        "WHERE MaPhieuTra=? AND MaHoaDon=? AND MaLo=? AND MaDonViTinh=?")
                        .setParameter(1, trangThaiMoi).setParameter(2, maPhieuTra)
                        .setParameter(3, maHoaDon).setParameter(4, maLo)
                        .setParameter(5, maDonViTinh)
                        .executeUpdate();

                // ==================================================
                // 7. Nếu chuyển sang HỦY (2) → tạo/cập nhật PhieuHuy tự động
                // ==================================================
                String maPhieuHuyDuocTao = null;

                if (trangThaiMoi == 2 && trangThaiCu != 2) {

                    // 7.1. Lấy thông tin lô + SP + DVT + lyDo
                    @SuppressWarnings("unchecked")
                    List<Object[]> infoRows = em.createNativeQuery(
                            "SELECT lo.MaLo, sp.GiaNhap, dvt.MaDonViTinh, dvt.TenDonViTinh, ctp.LyDoChiTiet " +
                            "FROM LoSanPham lo " +
                            "LEFT JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham " +
                            "LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = ? " +
                            "LEFT JOIN ChiTietPhieuTra ctp ON ctp.MaPhieuTra=? AND ctp.MaHoaDon=? " +
                            "    AND ctp.MaLo=? AND ctp.MaDonViTinh=? " +
                            "WHERE lo.MaLo = ?")
                            .setParameter(1, maDonViTinh).setParameter(2, maPhieuTra)
                            .setParameter(3, maHoaDon).setParameter(4, maLo)
                            .setParameter(5, maDonViTinh).setParameter(6, maLo)
                            .getResultList();

                    if (infoRows.isEmpty()) return "ERR";

                    Object[] info = infoRows.get(0);
                    double donGiaNhap = ((Number) info[1]).doubleValue();
                    String maDVT     = (String) info[2];
                    String lyDoGoc   = (String) info[4];
                    double thanhTien = Math.round(soLuongTra * donGiaNhap * 100.0) / 100.0;

                    String lyDoFinal = (lyDoGoc != null && !lyDoGoc.isEmpty())
                            ? lyDoGoc + " (Huỷ từ phiếu trả " + maPhieuTra + ")"
                            : "Huỷ từ phiếu trả " + maPhieuTra;

                    // 7.2. Kiểm tra đã có PhieuHuy liên kết với PhieuTra này chưa
                    @SuppressWarnings("unchecked")
                    List<Object> maPHList = em.createNativeQuery(
                            "SELECT ph.MaPhieuHuy FROM PhieuHuy ph " +
                            "INNER JOIN ChiTietPhieuHuy ctph ON ph.MaPhieuHuy = ctph.MaPhieuHuy " +
                            "WHERE ctph.LyDoChiTiet LIKE ? ORDER BY ph.MaPhieuHuy DESC LIMIT 1")
                            .setParameter(1, "%phiếu trả " + maPhieuTra + "%")
                            .getResultList();

                    if (!maPHList.isEmpty() && maPHList.get(0) != null) {
                        // 7.3a. Đã có PhieuHuy → thêm/cập nhật chi tiết
                        String maPH = (String) maPHList.get(0);
                        maPhieuHuyDuocTao = maPH;

                        @SuppressWarnings("unchecked")
                        List<Number> existCheck = em.createNativeQuery(
                                "SELECT COUNT(*) FROM ChiTietPhieuHuy WHERE MaPhieuHuy=? AND MaLo=?")
                                .setParameter(1, maPH).setParameter(2, maLo)
                                .getResultList();

                        boolean ctDaTonTai = !existCheck.isEmpty() && existCheck.get(0).intValue() > 0;

                        if (ctDaTonTai) {
                            // Gộp vào CT đã có
                            em.createNativeQuery(
                                    "UPDATE ChiTietPhieuHuy " +
                                    "SET SoLuongHuy = SoLuongHuy + ?, ThanhTien = ThanhTien + ?, " +
                                    "LyDoChiTiet = CONCAT(LyDoChiTiet, '; ', ?) " +
                                    "WHERE MaPhieuHuy=? AND MaLo=?")
                                    .setParameter(1, soLuongTra).setParameter(2, thanhTien)
                                    .setParameter(3, lyDoFinal)
                                    .setParameter(4, maPH).setParameter(5, maLo)
                                    .executeUpdate();
                        } else {
                            // Insert CT mới vào PH đã có
                            em.createNativeQuery(
                                    "INSERT INTO ChiTietPhieuHuy " +
                                    "(MaPhieuHuy,MaLo,SoLuongHuy,LyDoChiTiet,DonGiaNhap,ThanhTien,MaDonViTinh,TrangThai) " +
                                    "VALUES (?,?,?,?,?,?,?,2)")
                                    .setParameter(1, maPH).setParameter(2, maLo)
                                    .setParameter(3, soLuongTra).setParameter(4, lyDoFinal)
                                    .setParameter(5, donGiaNhap).setParameter(6, thanhTien)
                                    .setParameter(7, maDVT)
                                    .executeUpdate();
                        }

                        // Cập nhật TongTien PhieuHuy
                        em.createNativeQuery(
                                "UPDATE PhieuHuy SET TongTien = " +
                                "(SELECT COALESCE(SUM(ThanhTien),0) FROM ChiTietPhieuHuy WHERE MaPhieuHuy=?) " +
                                "WHERE MaPhieuHuy=?")
                                .setParameter(1, maPH).setParameter(2, maPH)
                                .executeUpdate();

                    } else {
                        // 7.3b. Chưa có → tạo PhieuHuy mới
                        String prefix = "PH-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
                        @SuppressWarnings("unchecked")
                        List<Number> countList = em.createNativeQuery(
                                "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?")
                                .setParameter(1, prefix + "%").getResultList();
                        int count = (countList.isEmpty() || countList.get(0) == null) ? 0 : countList.get(0).intValue();
                        String maPH = String.format("%s%04d", prefix, count + 1);
                        maPhieuHuyDuocTao = maPH;

                        em.createNativeQuery(
                                "INSERT INTO PhieuHuy (MaPhieuHuy,NgayLapPhieu,MaNhanVien,TrangThai,TongTien) " +
                                "VALUES (?,?,?,1,?)")
                                .setParameter(1, maPH)
                                .setParameter(2, Date.valueOf(LocalDate.now()))
                                .setParameter(3, nv.getMaNhanVien())
                                .setParameter(4, thanhTien)
                                .executeUpdate();

                        em.createNativeQuery(
                                "INSERT INTO ChiTietPhieuHuy " +
                                "(MaPhieuHuy,MaLo,SoLuongHuy,LyDoChiTiet,DonGiaNhap,ThanhTien,MaDonViTinh,TrangThai) " +
                                "VALUES (?,?,?,?,?,?,?,2)")
                                .setParameter(1, maPH).setParameter(2, maLo)
                                .setParameter(3, soLuongTra).setParameter(4, lyDoFinal)
                                .setParameter(5, donGiaNhap).setParameter(6, thanhTien)
                                .setParameter(7, maDVT)
                                .executeUpdate();
                    }
                }

                // ==================================================
                // 8. Kiểm tra đã xử lý hết chi tiết của PhieuTra chưa
                // ==================================================
                @SuppressWarnings("unchecked")
                List<Number> chuaXuLy = em.createNativeQuery(
                        "SELECT COUNT(*) FROM ChiTietPhieuTra WHERE MaPhieuTra=? AND TrangThai=0")
                        .setParameter(1, maPhieuTra)
                        .getResultList();

                boolean daXuLyHet = chuaXuLy.isEmpty() || chuaXuLy.get(0).intValue() == 0;
                if (daXuLyHet) {
                    em.createNativeQuery("UPDATE PhieuTra SET DaDuyet=1 WHERE MaPhieuTra=?")
                            .setParameter(1, maPhieuTra)
                            .executeUpdate();
                }

                return maPhieuHuyDuocTao != null ? "OK|" + maPhieuHuyDuocTao : "OK";
            });
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái phiếu trả (đã rollback): " + e.getMessage());
            return "ERR";
        }
    }

    // ============================================================
    // 🧾 Tạo mã phiếu trả tự động — MAX theo prefix ngày
    // ============================================================
    @Override
    public String taoMaPhieuTra() {
        String prefix = "PT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<String> result = em.createNativeQuery(
                    "SELECT MAX(MaPhieuTra) FROM PhieuTra WHERE MaPhieuTra LIKE ?")
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
    // 🔔 Đếm số phiếu trả chưa duyệt (cho Dashboard)
    // ============================================================
    @Override
    public int demPhieuTraChuaDuyet() {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(pt) FROM PhieuTra pt WHERE pt.trangThai = false",
                    Long.class).getSingleResult();
            return count.intValue();
        });
    }

    // ============================================================
    // 📊 Tổng tiền trả theo tháng — native (MONTH/YEAR SQL Server)
    // ============================================================
    @Override
    public double tinhTongTienTraTheoThang(int thang, int nam) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(TongTienHoan), 0) FROM PhieuTra " +
                    "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?")
                    .setParameter(1, thang).setParameter(2, nam)
                    .getResultList();
            return (result.isEmpty() || result.get(0) == null) ? 0.0 : result.get(0).doubleValue();
        });
    }

    // ============================================================
    // 🔍 Kiểm tra đã trả lô trong hóa đơn chưa
    // ============================================================
    @Override
    public boolean daTraLoTrongHoaDon(String maHD, String maLo) {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(ct) FROM ChiTietPhieuTra ct " +
                    "WHERE ct.id.maHoaDon = :maHD AND ct.id.maLo = :maLo",
                    Long.class)
                    .setParameter("maHD", maHD)
                    .setParameter("maLo", maLo)
                    .getSingleResult();
            return count > 0;
        });
    }

    // ============================================================
    // 📅 Đếm số phiếu trả hôm nay của nhân viên — native (GETDATE)
    // ============================================================
    @Override
    public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) {
        return doInTransaction(em -> {
            @SuppressWarnings("unchecked")
            List<Number> result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM PhieuTra " +
                    "WHERE MaNhanVien = ? AND DATE(NgayLap) = CURRENT_DATE")
                    .setParameter(1, maNhanVien)
                    .getResultList();
            return result.isEmpty() ? 0 : result.get(0).intValue();
        });
    }

    // ============================================================
    // 🔍 Tìm phiếu trả theo SĐT khách hàng
    // ============================================================
    @Override
    public List<PhieuTra> timPhieuTraTheoSoDienThoai(String sdt) {
        return doInTransaction(em -> {
            List<PhieuTra> list = em.createQuery(
                    "SELECT pt FROM PhieuTra pt " +
                    "JOIN FETCH pt.nhanVien JOIN FETCH pt.khachHang kh " +
                    "WHERE kh.soDienThoai = :sdt ORDER BY pt.ngayLap DESC",
                    PhieuTra.class)
                    .setParameter("sdt", sdt)
                    .getResultList();
            for (PhieuTra pt : list) {
                pt.getChiTietPhieuTraList().size();
            }
            return new ArrayList<>(list);
        });
    }

    // ============================================================
    // 🔍 Tìm phiếu trả theo keyword (mã phiếu, tên KH, SĐT)
    // ============================================================
    @Override
    public List<PhieuTra> timPhieuTraTheoKeyword(String keyword) {
        return doInTransaction(em -> {
            String kw1 = keyword + "%";          // prefix match cho mã phiếu
            String kw2 = "%" + keyword + "%";   // contains match cho tên KH, SĐT
            List<PhieuTra> list = em.createQuery(
                    "SELECT pt FROM PhieuTra pt " +
                    "JOIN FETCH pt.nhanVien JOIN FETCH pt.khachHang kh " +
                    "WHERE UPPER(pt.maPhieuTra) LIKE UPPER(:kw1) " +
                    "   OR kh.tenKhachHang LIKE :kw2 OR kh.soDienThoai LIKE :kw2 " +
                    "ORDER BY pt.ngayLap DESC, pt.maPhieuTra DESC",
                    PhieuTra.class)
                    .setParameter("kw1", kw1)
                    .setParameter("kw2", kw2)
                    .getResultList();
            for (PhieuTra pt : list) {
                pt.getChiTietPhieuTraList().size();
            }
            return new ArrayList<>(list);
        });
    }
}
