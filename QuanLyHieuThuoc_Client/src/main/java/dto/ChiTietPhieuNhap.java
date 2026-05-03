package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietPhieuNhap")
public class ChiTietPhieuNhap implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maPhieuNhap;
        private String maLo;

        public Id() {}

        public Id(String maPhieuNhap, String maLo) {
            this.maPhieuNhap = maPhieuNhap;
            this.maLo = maLo;
        }
    public String getMaPhieuNhap() { return maPhieuNhap; }
    public String getMaLo() { return maLo; }


        public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
        public void setMaLo(String maLo) { this.maLo = maLo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(maPhieuNhap, that.maPhieuNhap) && Objects.equals(maLo, that.maLo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maPhieuNhap, maLo);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maPhieuNhap")
    @JoinColumn(name = "maPhieuNhap")
    private PhieuNhap phieuNhap;

    @ManyToOne
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoSanPham loSanPham;

    @ManyToOne
    @JoinColumn(name = "maDonViTinh")
    private DonViTinh donViTinh;

    private int soLuongNhap;
    private double donGiaNhap;
    private double thanhTien;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(PhieuNhap phieuNhap, LoSanPham loSanPham,
                             DonViTinh donViTinh, int soLuongNhap, double donGiaNhap) {
        setPhieuNhap(phieuNhap);
        setLoSanPham(loSanPham);
        setDonViTinh(donViTinh);
        setSoLuongNhap(soLuongNhap);
        setDonGiaNhap(donGiaNhap);
        capNhatThanhTien();
    }

    public ChiTietPhieuNhap(ChiTietPhieuNhap other) {
        this.id = new Id(other.id.maPhieuNhap, other.id.maLo);
        this.phieuNhap = other.phieuNhap;
        this.loSanPham = other.loSanPham;
        this.donViTinh = other.donViTinh;
        this.soLuongNhap = other.soLuongNhap;
        this.donGiaNhap = other.donGiaNhap;
        this.thanhTien = other.thanhTien;
    }

    public PhieuNhap getPhieuNhap() { return phieuNhap; }
    public LoSanPham getLoSanPham() { return loSanPham; }
    public DonViTinh getDonViTinh() { return donViTinh; }
    public int getSoLuongNhap() { return soLuongNhap; }
    public double getDonGiaNhap() { return donGiaNhap; }
    public double getThanhTien() { return thanhTien; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setPhieuNhap(PhieuNhap phieuNhap) {
        if (phieuNhap == null)
            throw new IllegalArgumentException("Phiếu nhập không được null.");
        this.phieuNhap = phieuNhap;
        id.setMaPhieuNhap(phieuNhap.getMaPhieuNhap());
    }



    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được null.");
        this.loSanPham = loSanPham;
        id.setMaLo(loSanPham.getMaLo());
    }



    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
    }



    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap <= 0)
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        this.soLuongNhap = soLuongNhap;
        capNhatThanhTien();
    }



    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap <= 0)
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0.");
        this.donGiaNhap = donGiaNhap;
        capNhatThanhTien();
    }



    public void capNhatThanhTien() {
        this.thanhTien = Math.round(soLuongNhap * donGiaNhap * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format(
                "ChiTietPhieuNhap{PN='%s', Lo='%s', ĐVT='%s', SL=%d, ĐơnGiá=%.2f, ThànhTiền=%.2f}",
                phieuNhap != null ? phieuNhap.getMaPhieuNhap() : "N/A",
                loSanPham != null ? loSanPham.getMaLo() : "N/A",
                donViTinh != null ? donViTinh.getTenDonViTinh() : "N/A",
                soLuongNhap, donGiaNhap, thanhTien);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuNhap)) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

