package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "LoSanPham")
public class LoSanPham implements Serializable {

    @Id
    private String maLo;

    private LocalDate hanSuDung;
    private int soLuongTon;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    public LoSanPham() {}

    public LoSanPham(String maLo, LocalDate hanSuDung, int soLuongTon, SanPham sanPham) {
        setMaLo(maLo);
        setHanSuDung(hanSuDung);
        setSoLuongTon(soLuongTon);
        setSanPham(sanPham);
    }

    public LoSanPham(String maLo) {
        setMaLo(maLo);
    }

    public LoSanPham(LoSanPham other) {
        this.maLo = other.maLo;
        this.hanSuDung = other.hanSuDung;
        this.soLuongTon = other.soLuongTon;
        this.sanPham = other.sanPham;
    }
    public String getMaLo() { return maLo; }
    public LocalDate getHanSuDung() { return hanSuDung; }
    public int getSoLuongTon() { return soLuongTon; }
    public SanPham getSanPham() { return sanPham; }




    public void setMaLo(String maLo) {
        if (maLo == null)
            throw new IllegalArgumentException("Mã lô không được để trống");
        maLo = maLo.trim();
        if (!maLo.matches("^LO-\\d{6}$"))
            throw new IllegalArgumentException("Mã lô không hợp lệ. Định dạng: LO-xxxxxx");
        this.maLo = maLo;
    }



    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null)
            throw new IllegalArgumentException("Hạn sử dụng không được rỗng.");
        if (hanSuDung.isBefore(LocalDate.now().minusYears(50)))
            throw new IllegalArgumentException("Hạn sử dụng không hợp lệ.");
        this.hanSuDung = hanSuDung;
    }



    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0)
            throw new IllegalArgumentException("Số lượng tồn phải ≥ 0.");
        this.soLuongTon = soLuongTon;
    }



    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    public void capNhatSoLuongTon(int delta) {
        int moi = this.soLuongTon + delta;
        if (moi < 0)
            throw new IllegalArgumentException("Không đủ hàng tồn trong kho để thực hiện thao tác.");
        this.soLuongTon = moi;
    }

    public boolean isHetHan() {
        return hanSuDung != null && hanSuDung.isBefore(LocalDate.now());
    }

    public boolean isConHan() {
        return hanSuDung != null && !hanSuDung.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Lô %s | HSD: %s | Tồn: %d | %s%s",
                maLo, hanSuDung, soLuongTon,
                sanPham != null ? sanPham.getMaSanPham() : "Không rõ sản phẩm",
                isHetHan() ? " ⚠ (Hết hạn)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoSanPham)) return false;
        LoSanPham that = (LoSanPham) o;
        return Objects.equals(maLo, that.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }
}
