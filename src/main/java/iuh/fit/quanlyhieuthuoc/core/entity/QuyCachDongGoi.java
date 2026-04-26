package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "QuyCachDongGoi")
public class QuyCachDongGoi implements Serializable {

    @Id
    private String maQuyCach;

    @ManyToOne
    @JoinColumn(name = "maDonViTinh")
    private DonViTinh donViTinh;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    private int heSoQuyDoi;
    private double tiLeGiam;
    private boolean donViGoc;
    private boolean trangThai;

    public QuyCachDongGoi() {}

    public QuyCachDongGoi(String maQuyCach, DonViTinh donViTinh, SanPham sanPham,
                          int heSoQuyDoi, double tiLeGiam, boolean donViGoc, boolean trangThai) {
        setMaQuyCach(maQuyCach);
        setDonViTinh(donViTinh);
        setSanPham(sanPham);
        setTiLeGiam(tiLeGiam);
        setTrangThai(trangThai);
        this.heSoQuyDoi = heSoQuyDoi;
        this.donViGoc = donViGoc;
        validateConsistency();
    }
    public String getMaQuyCach() { return maQuyCach; }
    public DonViTinh getDonViTinh() { return donViTinh; }
    public SanPham getSanPham() { return sanPham; }
    public int getHeSoQuyDoi() { return heSoQuyDoi; }
    public double getTiLeGiam() { return tiLeGiam; }
    public boolean isDonViGoc() { return donViGoc; }
    public boolean isTrangThai() { return trangThai; }




    public void setMaQuyCach(String maQuyCach) {
        if (maQuyCach == null || !maQuyCach.matches("^QC-\\d{6}$"))
            throw new IllegalArgumentException("Mã quy cách phải theo định dạng QC-xxxxxx (ví dụ QC-000001).");
        this.maQuyCach = maQuyCach;
    }



    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
    }



    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }



    public void setHeSoQuyDoi(int heSoQuyDoi) {
        if (heSoQuyDoi <= 0)
            throw new IllegalArgumentException("Hệ số quy đổi phải lớn hơn 0.");
        this.heSoQuyDoi = heSoQuyDoi;
        validateConsistency();
    }



    public void setTiLeGiam(double tiLeGiam) {
        if (tiLeGiam < 0 || tiLeGiam > 1)
            throw new IllegalArgumentException("Tỉ lệ giảm phải trong khoảng [0, 1].");
        this.tiLeGiam = tiLeGiam;
    }



    public void setDonViGoc(boolean donViGoc) {
        this.donViGoc = donViGoc;
        validateConsistency();
    }

    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    private void validateConsistency() {
        if (this.heSoQuyDoi <= 0)
            throw new IllegalArgumentException("Hệ số quy đổi phải lớn hơn 0.");
        if (this.donViGoc && this.heSoQuyDoi != 1)
            throw new IllegalArgumentException("Đơn vị gốc phải có hệ số quy đổi = 1.");
        if (!this.donViGoc && this.heSoQuyDoi == 1)
            throw new IllegalArgumentException("Chỉ đơn vị gốc mới được có hệ số quy đổi = 1.");
    }

    public double getGiaBan() {
        return sanPham.getGiaBan() - sanPham.getGiaBan() * tiLeGiam;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (x%d, Giảm %.0f%%)%s",
                sanPham != null ? sanPham.getTenSanPham() : "N/A",
                donViTinh != null ? donViTinh.getTenDonViTinh() : "N/A",
                heSoQuyDoi, tiLeGiam * 100,
                donViGoc ? " [Gốc]" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuyCachDongGoi)) return false;
        QuyCachDongGoi other = (QuyCachDongGoi) o;
        return Objects.equals(maQuyCach, other.maQuyCach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maQuyCach);
    }
}
