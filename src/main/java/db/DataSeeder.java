package db;

import entity.*;
import entity.DuongDung;
import entity.HinhThucKM;
import entity.LoaiSanPham;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

/**
 * Khởi tạo dữ liệu mẫu cho toàn bộ hệ thống.
 * Chỉ chạy nếu DB trống (COUNT(NhanVien) == 0).
 */
public class DataSeeder {

    public static void seed() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Long count = em.createQuery("SELECT COUNT(n) FROM NhanVien n", Long.class)
                           .getSingleResult();
            if (count > 0) {
                System.out.println("[DataSeeder] Dữ liệu đã tồn tại, bỏ qua.");
                return;
            }

            tx.begin();

            // ── 1. DonViTinh ──────────────────────────────────────────────────
            DonViTinh dvtVien = new DonViTinh("DVT-001", "Viên");
            DonViTinh dvtHop  = new DonViTinh("DVT-002", "Hộp");
            DonViTinh dvtLo   = new DonViTinh("DVT-003", "Lọ");
            DonViTinh dvtChai = new DonViTinh("DVT-004", "Chai");
            DonViTinh dvtGoi  = new DonViTinh("DVT-005", "Gói");
            DonViTinh dvtVi   = new DonViTinh("DVT-006", "Vỉ");
            em.persist(dvtVien); em.persist(dvtHop);  em.persist(dvtLo);
            em.persist(dvtChai); em.persist(dvtGoi);  em.persist(dvtVi);

            // ── 2. NhaCungCap ─────────────────────────────────────────────────
            NhaCungCap ncc1 = new NhaCungCap("NCC-20240101-0001",
                    "Dược phẩm Savi", "0281234567",
                    "8 Nguyễn Huệ, Q.1, TP.HCM", "info@savi.com.vn");
            NhaCungCap ncc2 = new NhaCungCap("NCC-20240101-0002",
                    "Công ty Dược TW3", "0283456789",
                    "34 Đoàn Văn Bơ, Q.4, TP.HCM", "tw3@tw3pharma.com");
            NhaCungCap ncc3 = new NhaCungCap("NCC-20240101-0003",
                    "Dược phẩm OPV", "0289876543",
                    "4 Bình Lợi, Bình Thạnh, TP.HCM", "opv@opv.vn");
            em.persist(ncc1); em.persist(ncc2); em.persist(ncc3);

            // ── 3. KhachHang ──────────────────────────────────────────────────
            KhachHang kh1 = new KhachHang("KH-20240101-0001", "Nguyễn Văn An",
                    true,  "0901234567", LocalDate.of(1990, 5, 10), true);
            KhachHang kh2 = new KhachHang("KH-20240101-0002", "Trần Thị Bình",
                    false, "0912345678", LocalDate.of(1985, 8, 22), true);
            KhachHang kh3 = new KhachHang("KH-20240101-0003", "Lê Văn Cường",
                    true,  "0923456789", LocalDate.of(2000, 3, 15), true);
            KhachHang kh4 = new KhachHang("KH-20240101-0004", "Phạm Thị Dung",
                    false, "0934567890", LocalDate.of(1995, 11, 30), true);
            em.persist(kh1); em.persist(kh2); em.persist(kh3); em.persist(kh4);

            // ── 4. NhanVien ───────────────────────────────────────────────────
            NhanVien nv1 = new NhanVien("NV-20240101-0001", "Trần Văn Minh",
                    true, LocalDate.of(1985, 3, 12), "0901111111",
                    "123 Lê Văn Sỹ, Q.3, TP.HCM", true, 1, true);
            NhanVien nv2 = new NhanVien("NV-20240101-0002", "Nguyễn Thị Lan",
                    false, LocalDate.of(1992, 7, 25), "0902222222",
                    "456 Cách Mạng Tháng 8, Q.10, TP.HCM", false, 1, true);
            NhanVien nv3 = new NhanVien("NV-20240101-0003", "Lê Văn Hùng",
                    true, LocalDate.of(1990, 11, 8), "0903333333",
                    "789 Hoàng Văn Thụ, Phú Nhuận, TP.HCM", false, 2, true);
            em.persist(nv1); em.persist(nv2); em.persist(nv3);

            // ── 5. TaiKhoan ───────────────────────────────────────────────────
            // Mật khẩu phải: ≥8 ký tự, có chữ hoa, chữ thường, chữ số
            TaiKhoan tk1 = new TaiKhoan("TK-20240101-0001", "admin",      "Admin1234", nv1);
            TaiKhoan tk2 = new TaiKhoan("TK-20240101-0002", "nhanvien01", "Nhanvien01", nv2);
            TaiKhoan tk3 = new TaiKhoan("TK-20240101-0003", "nhanvien02", "Nhanvien02", nv3);
            em.persist(tk1); em.persist(tk2); em.persist(tk3);

            // ── 6. BangGia ────────────────────────────────────────────────────
            BangGia bg1 = new BangGia("BG-20240101-0001", nv1,
                    "Bảng giá cơ bản 2024", LocalDate.of(2024, 1, 1), true);
            em.persist(bg1);

            // ── 7. ChiTietBangGia ─────────────────────────────────────────────
            // Tier 1: giaNhap ∈ [0, 100000)   → nhân 1.30
            // Tier 2: giaNhap ∈ [100000, ∞)   → nhân 1.25
            ChiTietBangGia ctbg1 = new ChiTietBangGia(bg1, 0,      100000,    1.30);
            ChiTietBangGia ctbg2 = new ChiTietBangGia(bg1, 100000, 999999999, 1.25);
            em.persist(ctbg1); em.persist(ctbg2);

            // ── 8. SanPham ────────────────────────────────────────────────────
            // setChiTietBangGiaHienTai() sẽ tính lại giaBan = giaNhap * tiLe
            SanPham sp1 = new SanPham("SP-000001", "Paracetamol 500mg",
                    LoaiSanPham.THUOC, "VD-12345-14",
                    DuongDung.UONG, 3000, null, "Kệ A1", true);
            sp1.setChiTietBangGiaHienTai(ctbg1);   // giaBan = 3 900

            SanPham sp2 = new SanPham("SP-000002", "Vitamin C 1000mg",
                    LoaiSanPham.THUC_PHAM_BO_SUNG, "VD-23456-14",
                    DuongDung.UONG, 8000, null, "Kệ B2", true);
            sp2.setChiTietBangGiaHienTai(ctbg1);   // giaBan = 10 400

            SanPham sp3 = new SanPham("SP-000003", "Amoxicillin 500mg",
                    LoaiSanPham.THUOC, "VD-34567-14",
                    DuongDung.UONG, 12000, null, "Kệ C1", true);
            sp3.setChiTietBangGiaHienTai(ctbg1);   // giaBan = 15 600

            SanPham sp4 = new SanPham("SP-000004", "Omeprazole 20mg",
                    LoaiSanPham.THUOC, "VD-45678-14",
                    DuongDung.UONG, 15000, null, "Kệ C2", true);
            sp4.setChiTietBangGiaHienTai(ctbg1);   // giaBan = 19 500

            SanPham sp5 = new SanPham("SP-000005", "Ibuprofen 400mg",
                    LoaiSanPham.THUOC, "VD-56789-14",
                    DuongDung.UONG, 6000, null, "Kệ A2", true);
            sp5.setChiTietBangGiaHienTai(ctbg1);   // giaBan = 7 800

            em.persist(sp1); em.persist(sp2); em.persist(sp3);
            em.persist(sp4); em.persist(sp5);

            // ── 9. QuyCachDongGoi ─────────────────────────────────────────────
            // donViGoc=true → heSo PHẢI = 1 ; donViGoc=false → heSo PHẢI ≠ 1
            em.persist(new QuyCachDongGoi("QC-000001", dvtVien, sp1,  1, 0.00, true,  true));
            em.persist(new QuyCachDongGoi("QC-000002", dvtHop,  sp1, 10, 0.05, false, true));
            em.persist(new QuyCachDongGoi("QC-000003", dvtVien, sp2,  1, 0.00, true,  true));
            em.persist(new QuyCachDongGoi("QC-000004", dvtLo,   sp2, 30, 0.08, false, true));
            em.persist(new QuyCachDongGoi("QC-000005", dvtVien, sp3,  1, 0.00, true,  true));
            em.persist(new QuyCachDongGoi("QC-000006", dvtVi,   sp3, 10, 0.05, false, true));
            em.persist(new QuyCachDongGoi("QC-000007", dvtVien, sp4,  1, 0.00, true,  true));
            em.persist(new QuyCachDongGoi("QC-000008", dvtHop,  sp4, 28, 0.10, false, true));
            em.persist(new QuyCachDongGoi("QC-000009", dvtVien, sp5,  1, 0.00, true,  true));
            em.persist(new QuyCachDongGoi("QC-000010", dvtVi,   sp5, 10, 0.05, false, true));

            // ── 10. LoSanPham ─────────────────────────────────────────────────
            // soLuongTon đã trừ số lượng bán trong các HoaDon dưới đây
            LoSanPham lo1 = new LoSanPham("LO-000001", LocalDate.of(2026, 12, 31), 490, sp1);
            LoSanPham lo2 = new LoSanPham("LO-000002", LocalDate.of(2027, 6,  30), 300, sp1);
            LoSanPham lo3 = new LoSanPham("LO-000003", LocalDate.of(2026, 9,  30), 195, sp2);
            LoSanPham lo4 = new LoSanPham("LO-000004", LocalDate.of(2026, 12, 31), 480, sp3);
            LoSanPham lo5 = new LoSanPham("LO-000005", LocalDate.of(2026, 12, 31), 250, sp4);
            LoSanPham lo6 = new LoSanPham("LO-000006", LocalDate.of(2027, 3,  31), 400, sp5);
            em.persist(lo1); em.persist(lo2); em.persist(lo3);
            em.persist(lo4); em.persist(lo5); em.persist(lo6);

            // ── 11. KhuyenMai ─────────────────────────────────────────────────
            KhuyenMai km1 = new KhuyenMai(
                    "KM-20240101-0001", "Giảm 10% Paracetamol & Vitamin C",
                    LocalDate.of(2024, 1, 1), LocalDate.of(2027, 12, 31),
                    true, false,
                    HinhThucKM.GIAM_GIA_PHAN_TRAM, 10.0, 0.0, 500);
            KhuyenMai km2 = new KhuyenMai(
                    "KM-20240601-0001", "Giảm 50.000đ hóa đơn từ 500.000đ",
                    LocalDate.of(2024, 6, 1), LocalDate.of(2027, 6, 30),
                    true, true,
                    HinhThucKM.GIAM_GIA_TIEN, 50000.0, 500000.0, 100);
            em.persist(km1); em.persist(km2);

            // ── 12. ChiTietKhuyenMaiSanPham ───────────────────────────────────
            em.persist(new ChiTietKhuyenMaiSanPham(sp1, km1));
            em.persist(new ChiTietKhuyenMaiSanPham(sp2, km1));

            // ── 13. PhieuNhap + ChiTietPhieuNhap ─────────────────────────────
            PhieuNhap pn1 = new PhieuNhap();
            pn1.setMaPhieuNhap("PN-20240115-0001");
            pn1.setNgayNhap(LocalDate.of(2024, 1, 15));
            pn1.setNhaCungCap(ncc1);
            pn1.setNhanVien(nv1);
            ChiTietPhieuNhap ctpn1 = new ChiTietPhieuNhap(pn1, lo1, dvtVien, 500, 3000.0);
            ChiTietPhieuNhap ctpn2 = new ChiTietPhieuNhap(pn1, lo2, dvtVien, 300, 3000.0);
            pn1.getChiTietPhieuNhapList().add(ctpn1);
            pn1.getChiTietPhieuNhapList().add(ctpn2);
            pn1.capNhatTongTienTheoChiTiet();
            em.persist(pn1);   // CASCADE ALL → persist ctpn1, ctpn2

            PhieuNhap pn2 = new PhieuNhap();
            pn2.setMaPhieuNhap("PN-20240115-0002");
            pn2.setNgayNhap(LocalDate.of(2024, 1, 15));
            pn2.setNhaCungCap(ncc2);
            pn2.setNhanVien(nv2);
            ChiTietPhieuNhap ctpn3 = new ChiTietPhieuNhap(pn2, lo3, dvtVien, 200, 8000.0);
            ChiTietPhieuNhap ctpn4 = new ChiTietPhieuNhap(pn2, lo4, dvtVien, 500, 12000.0);
            pn2.getChiTietPhieuNhapList().add(ctpn3);
            pn2.getChiTietPhieuNhapList().add(ctpn4);
            pn2.capNhatTongTienTheoChiTiet();
            em.persist(pn2);

            // ── 14. HoaDon + ChiTietHoaDon ───────────────────────────────────
            HoaDon hd1 = new HoaDon();
            hd1.setMaHoaDon("HD-20240115-0001");
            hd1.setNhanVien(nv2);
            hd1.setKhachHang(kh1);
            hd1.setNgayLap(LocalDate.of(2024, 1, 15));
            hd1.setThuocKeDon(false);
            // lo1 = 500 nhập, 490 tồn → bán 10; lo3 = 200 nhập, 195 tồn → bán 5
            ChiTietHoaDon cthd1 = new ChiTietHoaDon(hd1, lo1, 10.0, dvtVien, sp1.getGiaBan(), null);
            ChiTietHoaDon cthd2 = new ChiTietHoaDon(hd1, lo3,  5.0, dvtVien, sp2.getGiaBan(), null);
            hd1.getDanhSachChiTiet().add(cthd1);
            hd1.getDanhSachChiTiet().add(cthd2);
            hd1.capNhatDuLieuHoaDon();  // tongTien=91000, tongThanhToan=91000
            em.persist(hd1);            // CASCADE ALL → persist cthd1, cthd2

            HoaDon hd2 = new HoaDon();
            hd2.setMaHoaDon("HD-20240116-0001");
            hd2.setNhanVien(nv3);
            hd2.setKhachHang(kh2);
            hd2.setNgayLap(LocalDate.of(2024, 1, 16));
            hd2.setThuocKeDon(false);
            // lo4 = 500 nhập, 480 tồn → bán 20
            ChiTietHoaDon cthd3 = new ChiTietHoaDon(hd2, lo4, 20.0, dvtVien, sp3.getGiaBan(), null);
            hd2.getDanhSachChiTiet().add(cthd3);
            hd2.capNhatDuLieuHoaDon();  // tongTien=312000, tongThanhToan=312000
            em.persist(hd2);

            // ── 15. PhieuHuy + ChiTietPhieuHuy ───────────────────────────────
            PhieuHuy ph1 = new PhieuHuy();
            ph1.setMaPhieuHuy("PH-20240120-0001");
            ph1.setNgayLapPhieu(LocalDate.of(2024, 1, 20));
            ph1.setNhanVien(nv1);
            ph1.setTrangThai(false);   // chờ duyệt
            ChiTietPhieuHuy ctph1 = new ChiTietPhieuHuy(
                    ph1, lo2, 10, 3000.0, "Hàng sắp hết hạn", dvtVien,
                    ChiTietPhieuHuy.CHO_DUYET);
            ph1.getChiTietPhieuHuyList().add(ctph1);
            ph1.capNhatTongTienTheoChiTiet();
            em.persist(ph1);           // CASCADE ALL → persist ctph1

            // ── 16. PhieuTra + ChiTietPhieuTra ───────────────────────────────
            PhieuTra pt1 = new PhieuTra();
            pt1.setMaPhieuTra("PT-20240116-0001");
            pt1.setKhachHang(kh1);
            pt1.setNhanVien(nv2);
            pt1.setNgayLap(LocalDate.of(2024, 1, 16));
            pt1.setTrangThai(false);   // chờ duyệt
            // Trả 2 viên Paracetamol từ cthd1 (đã mua 10, giá 3900/viên → hoàn 7800)
            ChiTietPhieuTra ctpt1 = new ChiTietPhieuTra(
                    pt1, cthd1, "Dị ứng thuốc", 2, 0);
            ctpt1.setDonViTinh(dvtVien);
            pt1.getChiTietPhieuTraList().add(ctpt1);
            pt1.capNhatTongTienHoan();
            em.persist(pt1);           // CASCADE ALL → persist ctpt1

            tx.commit();
            System.out.println("[DataSeeder] ✅ Hoàn tất: " +
                    "6 DVT | 3 NCC | 4 KH | 3 NV | 3 TK | " +
                    "1 BG + 2 CTBG | 5 SP | 10 QC | 6 LO | " +
                    "2 KM + 2 CTKM | 2 PN + 4 CTPN | " +
                    "2 HD + 3 CTHD | 1 PH + 1 CTPH | 1 PT + 1 CTPT");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("[DataSeeder] ❌ Lỗi khi seed dữ liệu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
