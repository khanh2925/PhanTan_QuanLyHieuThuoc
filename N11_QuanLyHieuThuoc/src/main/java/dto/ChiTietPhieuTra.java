package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietPhieuTra")
public class ChiTietPhieuTra implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maPhieuTra;
        private String maHoaDon;
        private String maLo;

        public Id() {}

        public Id(String maPhieuTra, String maHoaDon, String maLo) {
            this.maPhieuTra = maPhieuTra;
            this.maHoaDon = maHoaDon;
            this.maLo = maLo;
        }
    public String getMaPhieuTra() { return maPhieuTra; }
    public String getMaHoaDon() { return maHoaDon; }
    public String getMaLo() { return maLo; }


        public void setMaPhieuTra(String maPhieuTra) { this.maPhieuTra = maPhieuTra; }
        public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
        public void setMaLo(String maLo) { this.maLo = maLo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(maPhieuTra, that.maPhieuTra)
                    && Objects.equals(maHoaDon, that.maHoaDon)
                    && Objects.equals(maLo, that.maLo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maPhieuTra, maHoaDon, maLo);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maPhieuTra")
    @JoinColumn(name = "maPhieuTra")
    private PhieuTra phieuTra;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "maHoaDon", referencedColumnName = "maHoaDon",
                    insertable = false, updatable = false),
        @JoinColumn(name = "maLo", referencedColumnName = "maLo",
                    insertable = false, updatable = false)
    })
    private ChiTietHoaDon chiTietHoaDon;

    private String lyDoChiTiet;
    private int soLuong;
    private double thanhTienHoan;
    private int trangThai;

    @ManyToOne
    @JoinColumn(name = "maDonViTinh")
    private DonViTinh donViTinh;

    public ChiTietPhieuTra() {}

    public ChiTietPhieuTra(PhieuTra phieuTra, ChiTietHoaDon chiTietHoaDon,
                           String lyDoChiTiet, int soLuong, int trangThai) {
        setPhieuTra(phieuTra);
        setChiTietHoaDon(chiTietHoaDon);
        setLyDoChiTiet(lyDoChiTiet);
        setSoLuong(soLuong);
        setTrangThai(trangThai);
        capNhatThanhTienHoan();
    }

    public ChiTietPhieuTra(ChiTietPhieuTra other) {
        this.id = new Id(other.id.maPhieuTra, other.id.maHoaDon, other.id.maLo);
        this.phieuTra = other.phieuTra;
        this.chiTietHoaDon = other.chiTietHoaDon;
        this.lyDoChiTiet = other.lyDoChiTiet;
        this.soLuong = other.soLuong;
        this.thanhTienHoan = other.thanhTienHoan;
        this.trangThai = other.trangThai;
    }

    public PhieuTra getPhieuTra() { return phieuTra; }
    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public int getSoLuong() { return soLuong; }
    public double getThanhTienHoan() { return thanhTienHoan; }
    public DonViTinh getDonViTinh() { return donViTinh; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setPhieuTra(PhieuTra phieuTra) {
        if (phieuTra == null)
            throw new IllegalArgumentException("Phiếu trả không được null.");
        this.phieuTra = phieuTra;
        id.setMaPhieuTra(phieuTra.getMaPhieuTra());
    }



    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null)
            throw new IllegalArgumentException("Chi tiết hóa đơn không được null.");
        this.chiTietHoaDon = chiTietHoaDon;
        id.setMaHoaDon(chiTietHoaDon.getId().getMaHoaDon());
        id.setMaLo(chiTietHoaDon.getId().getMaLo());
        capNhatThanhTienHoan();
    }



    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 200)
            throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 200 ký tự.");
        this.lyDoChiTiet = lyDoChiTiet;
    }



    public void setSoLuong(int soLuong) {
        if (soLuong <= 0)
            throw new IllegalArgumentException("Số lượng trả phải > 0.");
        if (this.chiTietHoaDon != null && soLuong > this.chiTietHoaDon.getSoLuong())
            throw new IllegalArgumentException(
                    String.format("Số lượng trả (%d) không được vượt quá số lượng đã mua (%.0f).",
                            soLuong, this.chiTietHoaDon.getSoLuong()));
        this.soLuong = soLuong;
        capNhatThanhTienHoan();
    }

    public void setThanhTienHoan(double thanhTienHoan) { this.thanhTienHoan = thanhTienHoan; }

    public void capNhatThanhTienHoan() {
        if (chiTietHoaDon == null || chiTietHoaDon.getSoLuong() <= 0) {
            this.thanhTienHoan = 0;
            return;
        }
        double donGiaThucTe = chiTietHoaDon.getThanhTien() / chiTietHoaDon.getSoLuong();
        this.thanhTienHoan = Math.round(donGiaThucTe * this.soLuong * 100.0) / 100.0;
    }



    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) {
        if (trangThai < 0 || trangThai > 2)
            throw new IllegalArgumentException("Trạng thái chỉ hợp lệ: 0=Chờ duyệt, 1=Nhập lại hàng, 2=Hủy hàng.");
        this.trangThai = trangThai;
    }

    public String getTrangThaiText() {
        return switch (trangThai) {
            case 0 -> "Chờ duyệt";
            case 1 -> "Nhập lại hàng";
            case 2 -> "Hủy hàng";
            default -> "Không xác định";
        };
    }

    public void setDonViTinh(DonViTinh dvt) { this.donViTinh = dvt; }

    @Override
    public String toString() {
        return String.format("CTPT[%s - %s - SL:%d - Hoàn:%.0fđ - %s]",
                phieuTra != null ? phieuTra.getMaPhieuTra() : "N/A",
                chiTietHoaDon != null && chiTietHoaDon.getSanPham() != null
                        ? chiTietHoaDon.getSanPham().getTenSanPham() : "N/A",
                soLuong, thanhTienHoan, getTrangThaiText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuTra)) return false;
        ChiTietPhieuTra that = (ChiTietPhieuTra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

