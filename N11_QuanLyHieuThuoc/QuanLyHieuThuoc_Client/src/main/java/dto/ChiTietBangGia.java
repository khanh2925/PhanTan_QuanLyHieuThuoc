package dto;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietBangGia")
public class ChiTietBangGia implements Serializable {

        @Embeddable
    public static class Id implements Serializable {
        private String maBangGia;
        private double giaTu;
        private double giaDen;

        public Id() {}

        public Id(String maBangGia, double giaTu, double giaDen) {
            this.maBangGia = maBangGia;
            this.giaTu = giaTu;
            this.giaDen = giaDen;
        }
    public String getMaBangGia() { return maBangGia; }
        public double getGiaTu() { return giaTu; }
        public double getGiaDen() { return giaDen; }


        public void setMaBangGia(String maBangGia) { this.maBangGia = maBangGia; }
        public void setGiaTu(double giaTu) { this.giaTu = giaTu; }
        public void setGiaDen(double giaDen) { this.giaDen = giaDen; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Double.compare(giaTu, that.giaTu) == 0
                    && Double.compare(giaDen, that.giaDen) == 0
                    && Objects.equals(maBangGia, that.maBangGia);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maBangGia, giaTu, giaDen);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("maBangGia")
    @JoinColumn(name = "maBangGia")
    private BangGia bangGia;

    private double tiLe;

    public ChiTietBangGia() {}

    public ChiTietBangGia(BangGia bangGia, double giaTu, double giaDen, double tiLe) {
        setBangGia(bangGia);
        setGiaTu(giaTu);
        setGiaDen(giaDen);
        setTiLe(tiLe);
    }

    public BangGia getBangGia() { return bangGia; }
    public double getTiLe() { return tiLe; }
    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }



    public void setBangGia(BangGia bangGia) {
        if (bangGia == null)
            throw new IllegalArgumentException("Bảng giá không được null.");
        this.bangGia = bangGia;
        id.setMaBangGia(bangGia.getMaBangGia());
    }

    public double getGiaTu() { return id.giaTu; }

    public void setGiaTu(double giaTu) {
        if (giaTu < 0)
            throw new IllegalArgumentException("Giá từ phải lớn hơn hoặc bằng 0.");
        id.setGiaTu(giaTu);
    }

    public double getGiaDen() { return id.giaDen; }

    public void setGiaDen(double giaDen) {
        if (giaDen < id.giaTu)
            throw new IllegalArgumentException("Giá đến phải lớn hơn hoặc bằng giá từ.");
        id.setGiaDen(giaDen);
    }



    public void setTiLe(double tiLe) {
        if (tiLe <= 0 || tiLe > 5)
            throw new IllegalArgumentException("Tỉ lệ giá phải > 0 và ≤ 5 (ví dụ: 1.2 = lời 20%).");
        this.tiLe = tiLe;
    }

    @Override
    public String toString() {
        return String.format("CTBG[%s, tỉ lệ=%.2f, khoảng=%.0f–%.0f]",
                bangGia != null ? bangGia.getMaBangGia() : "N/A",
                tiLe, id.giaTu, id.giaDen);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietBangGia)) return false;
        ChiTietBangGia that = (ChiTietBangGia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

