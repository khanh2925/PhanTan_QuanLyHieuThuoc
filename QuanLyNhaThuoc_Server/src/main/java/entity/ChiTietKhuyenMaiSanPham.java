package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietKhuyenMaiSanPham")
public class ChiTietKhuyenMaiSanPham implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maSanPham;
        private String maKM;

        public Id() {}

        public Id(String maSanPham, String maKM) {
            this.maSanPham = maSanPham;
            this.maKM = maKM;
        }
    public String getMaSanPham() { return maSanPham; }
    public String getMaKM() { return maKM; }


        public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
        public void setMaKM(String maKM) { this.maKM = maKM; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(maSanPham, that.maSanPham) && Objects.equals(maKM, that.maKM);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maSanPham, maKM);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maSanPham")
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    @ManyToOne
    @MapsId("maKM")
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;

    public ChiTietKhuyenMaiSanPham() {}

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai) {
        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
    }

    public SanPham getSanPham() { return sanPham; }
    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
        id.setMaSanPham(sanPham.getMaSanPham());
    }



    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null)
            throw new IllegalArgumentException("Khuyến mãi không được null.");
        if (khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        this.khuyenMai = khuyenMai;
        id.setMaKM(khuyenMai.getMaKM());
    }

    @Override
    public String toString() {
        return String.format("CTKM{KM='%s', SP='%s', Hình thức=%s}",
                khuyenMai != null ? khuyenMai.getMaKM() : "N/A",
                sanPham != null ? sanPham.getTenSanPham() : "N/A",
                khuyenMai != null ? khuyenMai.getHinhThuc() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietKhuyenMaiSanPham)) return false;
        ChiTietKhuyenMaiSanPham that = (ChiTietKhuyenMaiSanPham) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
