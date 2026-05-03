package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import dto.HinhThucKM;

@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDon implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maHoaDon;
        private String maLo;

        public Id() {}

        public Id(String maHoaDon, String maLo) {
            this.maHoaDon = maHoaDon;
            this.maLo = maLo;
        }
    public String getMaHoaDon() { return maHoaDon; }
    public String getMaLo() { return maLo; }


        public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
        public void setMaLo(String maLo) { this.maLo = maLo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(maHoaDon, that.maHoaDon) && Objects.equals(maLo, that.maLo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maHoaDon, maLo);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maHoaDon")
    @JoinColumn(name = "maHoaDon")
    private HoaDon hoaDon;

    @ManyToOne
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoSanPham loSanPham;

    private double soLuong;

    @ManyToOne
    @JoinColumn(name = "maDonViTinh")
    private DonViTinh donViTinh;

    private double giaBan;

    @ManyToOne
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;

    private double thanhTien;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon, LoSanPham loSanPham, double soLuong,
                         DonViTinh donViTinh, double giaBan, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setLoSanPham(loSanPham);
        setSoLuong(soLuong);
        setDonViTinh(donViTinh);
        setGiaBan(giaBan);
        setKhuyenMai(khuyenMai);
    }

    public void capNhatThanhTien() {
        double tongTienGoc = this.soLuong * this.giaBan;
        double tienGiam = 0;

        if (this.khuyenMai != null) {
            HinhThucKM hinhThuc = this.khuyenMai.getHinhThuc();
            double giaTriKM = this.khuyenMai.getGiaTri();

            if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                tienGiam = tongTienGoc * (giaTriKM / 100.0);
            } else if (hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                tienGiam = giaTriKM * this.soLuong;
            }
        }
        this.thanhTien = Math.max(0, tongTienGoc - tienGiam);
    }

    public HoaDon getHoaDon() { return hoaDon; }
    public LoSanPham getLoSanPham() { return loSanPham; }
    public double getSoLuong() { return soLuong; }
    public DonViTinh getDonViTinh() { return donViTinh; }
    public double getGiaBan() { return giaBan; }
    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public double getThanhTien() { return thanhTien; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) id.setMaHoaDon(hoaDon.getMaHoaDon());
    }



    public void setLoSanPham(LoSanPham loSanPham) {
        this.loSanPham = loSanPham;
        if (loSanPham != null) id.setMaLo(loSanPham.getMaLo());
    }

    public SanPham getSanPham() {
        return loSanPham != null ? loSanPham.getSanPham() : null;
    }



    public void setSoLuong(double soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0.");
        this.soLuong = soLuong;
        capNhatThanhTien();
    }



    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null) throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
    }



    public void setGiaBan(double giaBan) {
        if (giaBan < 0) throw new IllegalArgumentException("Giá bán không được âm.");
        this.giaBan = giaBan;
        capNhatThanhTien();
    }



    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai != null && khuyenMai.isKhuyenMaiHoaDon()) {
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        }
        this.khuyenMai = khuyenMai;
        capNhatThanhTien();
    }



    @Override
    public String toString() {
        return String.format("CTHD: %s | ĐVT: %s | SL: %.1f | Giá: %.0f | Thành tiền: %.0f",
                (loSanPham != null && loSanPham.getSanPham() != null)
                        ? loSanPham.getSanPham().getTenSanPham() : "null",
                (donViTinh != null) ? donViTinh.getTenDonViTinh() : "null",
                soLuong, giaBan, thanhTien);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon)) return false;
        ChiTietHoaDon other = (ChiTietHoaDon) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

