package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietPhieuHuy")
public class ChiTietPhieuHuy implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maPhieuHuy;
        private String maLo;

        public Id() {}

        public Id(String maPhieuHuy, String maLo) {
            this.maPhieuHuy = maPhieuHuy;
            this.maLo = maLo;
        }
    public String getMaPhieuHuy() { return maPhieuHuy; }
    public String getMaLo() { return maLo; }


        public void setMaPhieuHuy(String maPhieuHuy) { this.maPhieuHuy = maPhieuHuy; }
        public void setMaLo(String maLo) { this.maLo = maLo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(maPhieuHuy, that.maPhieuHuy) && Objects.equals(maLo, that.maLo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maPhieuHuy, maLo);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maPhieuHuy")
    @JoinColumn(name = "maPhieuHuy")
    private PhieuHuy phieuHuy;

    @ManyToOne
    @MapsId("maLo")
    @JoinColumn(name = "maLo")
    private LoSanPham loSanPham;

    private int soLuongHuy;
    private String lyDoChiTiet;
    private double donGiaNhap;
    private double thanhTien;
    private int trangThai;

    @ManyToOne
    @JoinColumn(name = "maDonViTinh")
    private DonViTinh donViTinh;

    public static final int CHO_DUYET = 1;
    public static final int HUY_HANG = 2;
    public static final int TU_CHOI = 3;

    public ChiTietPhieuHuy() {}

    public ChiTietPhieuHuy(PhieuHuy phieuHuy, LoSanPham loSanPham,
                           int soLuongHuy, double donGiaNhap,
                           String lyDoChiTiet, DonViTinh donViTinh, int trangThai) {
        setPhieuHuy(phieuHuy);
        setLoSanPham(loSanPham);
        setSoLuongHuy(soLuongHuy);
        setDonGiaNhap(donGiaNhap);
        setLyDoChiTiet(lyDoChiTiet);
        setDonViTinh(donViTinh);
        setTrangThai(trangThai);
        capNhatThanhTien();
    }

    public PhieuHuy getPhieuHuy() { return phieuHuy; }
    public LoSanPham getLoSanPham() { return loSanPham; }
    public int getSoLuongHuy() { return soLuongHuy; }
    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public double getDonGiaNhap() { return donGiaNhap; }
    public double getThanhTien() { return thanhTien; }
    public DonViTinh getDonViTinh() { return donViTinh; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setPhieuHuy(PhieuHuy phieuHuy) {
        if (phieuHuy == null)
            throw new IllegalArgumentException("Phiếu hủy không được rỗng.");
        this.phieuHuy = phieuHuy;
        id.setMaPhieuHuy(phieuHuy.getMaPhieuHuy());
    }



    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được rỗng.");
        this.loSanPham = loSanPham;
        id.setMaLo(loSanPham.getMaLo());
    }



    public void setSoLuongHuy(int soLuongHuy) {
        if (soLuongHuy <= 0)
            throw new IllegalArgumentException("Số lượng hủy phải lớn hơn 0.");
        this.soLuongHuy = soLuongHuy;
        capNhatThanhTien();
    }



    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 500)
            throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 500 ký tự.");
        this.lyDoChiTiet = lyDoChiTiet;
    }



    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap <= 0)
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0.");
        this.donGiaNhap = donGiaNhap;
        capNhatThanhTien();
    }



    public void capNhatThanhTien() {
        this.thanhTien = Math.round(soLuongHuy * donGiaNhap * 100.0) / 100.0;
    }

    public void setDonViTinh(DonViTinh donViTinh) { this.donViTinh = donViTinh; }



    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) {
        if (trangThai < 1 || trangThai > 3)
            throw new IllegalArgumentException(
                    "Trạng thái chi tiết không hợp lệ (1=Chờ duyệt, 2=Đã hủy hàng, 3=Đã từ chối hủy).");
        this.trangThai = trangThai;
    }

    public String getTrangThaiText() {
        return switch (trangThai) {
            case CHO_DUYET -> "Chờ duyệt";
            case HUY_HANG -> "Đã hủy hàng";
            case TU_CHOI -> "Đã từ chối hủy";
            default -> "Không rõ";
        };
    }

    @Override
    public String toString() {
        return String.format("CTPH[%s - Lô:%s - SL:%d - Trạng thái:%s - Giá:%.2f - Thành tiền:%.2f]",
                phieuHuy != null ? phieuHuy.getMaPhieuHuy() : "N/A",
                loSanPham != null ? loSanPham.getMaLo() : "N/A",
                soLuongHuy, getTrangThaiText(), donGiaNhap, thanhTien);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuHuy)) return false;
        ChiTietPhieuHuy that = (ChiTietPhieuHuy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

