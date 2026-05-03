package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "HoaDon")
public class HoaDon implements Serializable {

    @Id
    private String maHoaDon;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    private LocalDate ngayLap;
    private double tongTien;
    private double tongThanhToan;
    private double soTienGiamKhuyenMai;

    @ManyToOne
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> danhSachChiTiet;

    private boolean thuocKeDon;

    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
        this.ngayLap = LocalDate.now();
        this.thuocKeDon = false;
        this.tongTien = 0;
        this.tongThanhToan = 0;
        this.soTienGiamKhuyenMai = 0;
    }

    public HoaDon(String maHoaDon, NhanVien nhanVien, KhachHang khachHang,
                  LocalDate ngayLap, KhuyenMai khuyenMai,
                  List<ChiTietHoaDon> danhSachChiTiet, boolean thuocKeDon) {
        setMaHoaDon(maHoaDon);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
        setNgayLap(ngayLap);
        setThuocKeDon(thuocKeDon);
        setDanhSachChiTiet(danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>());
        setKhuyenMai(khuyenMai);
    }

    public void capNhatDuLieuHoaDon() {
        double tongTienHangGoc = 0;
        double tongTienGiamTuSanPham = 0;
        boolean coKhuyenMaiSanPham = false;

        for (ChiTietHoaDon ct : danhSachChiTiet) {
            double thanhTienGoc = ct.getSoLuong() * ct.getGiaBan();
            tongTienHangGoc += thanhTienGoc;
            if (ct.getKhuyenMai() != null) {
                coKhuyenMaiSanPham = true;
                tongTienGiamTuSanPham += (thanhTienGoc - ct.getThanhTien());
            }
        }

        this.tongTien = tongTienHangGoc;

        if (coKhuyenMaiSanPham) {
            this.khuyenMai = null;
            this.soTienGiamKhuyenMai = tongTienGiamTuSanPham;
        } else {
            this.soTienGiamKhuyenMai = tinhGiamGiaTheoHoaDon(tongTienHangGoc);
        }

        this.tongThanhToan = this.tongTien - this.soTienGiamKhuyenMai;
        if (this.tongThanhToan < 0) this.tongThanhToan = 0;
    }

    private double tinhGiamGiaTheoHoaDon(double tongTienGoc) {
        if (this.khuyenMai == null
                || !this.khuyenMai.isDangHoatDong()
                || !this.khuyenMai.isKhuyenMaiHoaDon()) {
            return 0;
        }
        if (tongTienGoc < this.khuyenMai.getDieuKienApDungHoaDon()) {
            return 0;
        }

        double tienGiam = 0;
        switch (this.khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM ->
                tienGiam = tongTienGoc * (this.khuyenMai.getGiaTri() / 100.0);
            case GIAM_GIA_TIEN ->
                tienGiam = this.khuyenMai.getGiaTri();
            default ->
                tienGiam = 0;
        }
        return Math.min(tienGiam, tongTienGoc);
    }
    public String getMaHoaDon() { return maHoaDon; }
    public NhanVien getNhanVien() { return nhanVien; }
    public KhachHang getKhachHang() { return khachHang; }
    public LocalDate getNgayLap() { return ngayLap; }
    public double getTongTien() { return tongTien; }
    public double getTongThanhToan() { return tongThanhToan; }
    public double getSoTienGiamKhuyenMai() { return soTienGiamKhuyenMai; }
    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public List<ChiTietHoaDon> getDanhSachChiTiet() { return danhSachChiTiet; }
    public boolean isThuocKeDon() { return thuocKeDon; }






    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null)
            throw new IllegalArgumentException("Mã hoá đơn không được để trống");
        maHoaDon = maHoaDon.trim();
        if (!maHoaDon.matches("^HD-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng: HD-yyyymmdd-xxxx");
        this.maHoaDon = maHoaDon;
    }



    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }



    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null) throw new IllegalArgumentException("Khách hàng không được null.");
        this.khachHang = khachHang;
    }



    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày lập không hợp lệ.");
        this.ngayLap = ngayLap;
    }

    public void setThuocKeDon(boolean thuocKeDon) { this.thuocKeDon = thuocKeDon; }



    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        if (danhSachChiTiet == null)
            throw new IllegalArgumentException("Danh sách chi tiết hoá đơn không được null.");
        this.danhSachChiTiet = danhSachChiTiet;
        capNhatDuLieuHoaDon();
    }



    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null) {
            this.khuyenMai = null;
        } else if (!khuyenMai.isKhuyenMaiHoaDon()) {
            this.khuyenMai = null;
        } else {
            this.khuyenMai = khuyenMai;
        }
        capNhatDuLieuHoaDon();
    }

    @Override
    public String toString() {
        return String.format(
                "HoaDon[%s | KH:%s | Tổng Gốc:%.0f | Giảm KM:-%.0f | Thanh Toán:%.0f | Thuốc kê đơn:%s]",
                maHoaDon,
                khachHang != null ? khachHang.getTenKhachHang() : "Khách lẻ",
                tongTien, soTienGiamKhuyenMai, tongThanhToan,
                thuocKeDon ? "Có" : "Không");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoaDon)) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}

