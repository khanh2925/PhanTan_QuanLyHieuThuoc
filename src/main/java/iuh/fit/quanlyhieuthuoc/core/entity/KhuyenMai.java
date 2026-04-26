package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import iuh.fit.quanlyhieuthuoc.core.enums.HinhThucKM;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai implements Serializable {

    @Id
    private String maKM;

    private String tenKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean trangThai;
    private boolean khuyenMaiHoaDon;

    @Enumerated(EnumType.STRING)
    private HinhThucKM hinhThuc;

    private double giaTri;
    private double dieuKienApDungHoaDon;
    private int soLuongKhuyenMai;

    public KhuyenMai() {}

    public KhuyenMai(String maKM, String tenKM, LocalDate ngayBatDau, LocalDate ngayKetThuc,
                     boolean trangThai, boolean khuyenMaiHoaDon, HinhThucKM hinhThuc,
                     double giaTri, double dieuKienApDungHoaDon, int soLuongKhuyenMai) {
        setMaKM(maKM);
        setTenKM(tenKM);
        setNgayBatDau(ngayBatDau);
        setNgayKetThuc(ngayKetThuc);
        setTrangThai(trangThai);
        setKhuyenMaiHoaDon(khuyenMaiHoaDon);
        setHinhThuc(hinhThuc);
        setGiaTri(giaTri);
        setDieuKienApDungHoaDon(dieuKienApDungHoaDon);
        setSoLuongKhuyenMai(soLuongKhuyenMai);
    }

    public KhuyenMai(String makm) {
        this.maKM = makm;
    }
    public String getMaKM() { return maKM; }
    public String getTenKM() { return tenKM; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public boolean isTrangThai() { return trangThai; }
    public boolean isKhuyenMaiHoaDon() { return khuyenMaiHoaDon; }
    public HinhThucKM getHinhThuc() { return hinhThuc; }
    public double getGiaTri() { return giaTri; }
    public double getDieuKienApDungHoaDon() { return dieuKienApDungHoaDon; }
    public int getSoLuongKhuyenMai() { return soLuongKhuyenMai; }




    public void setMaKM(String maKM) {
        if (maKM == null)
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống");
        maKM = maKM.trim();
        if (!maKM.matches("^KM-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ. Định dạng: KM-yyyymmdd-xxxx");
        this.maKM = maKM;
    }



    public void setTenKM(String tenKM) {
        if (tenKM == null || tenKM.trim().isEmpty() || tenKM.length() > 200)
            throw new IllegalArgumentException("Tên khuyến mãi không hợp lệ (không rỗng, ≤200 ký tự).");
        this.tenKM = tenKM.trim();
    }



    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null)
            throw new IllegalArgumentException("Ngày bắt đầu không được null.");
        if (this.ngayKetThuc != null && ngayBatDau.isAfter(this.ngayKetThuc))
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        this.ngayBatDau = ngayBatDau;
    }



    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null)
            throw new IllegalArgumentException("Ngày kết thúc không được null.");
        if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau))
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) { this.khuyenMaiHoaDon = khuyenMaiHoaDon; }



    public void setHinhThuc(HinhThucKM hinhThuc) {
        if (hinhThuc == null)
            throw new IllegalArgumentException("Hình thức khuyến mãi không được null.");
        this.hinhThuc = hinhThuc;
    }



    public void setGiaTri(double giaTri) {
        if (giaTri < 0)
            throw new IllegalArgumentException("Giá trị khuyến mãi phải ≥ 0.");
        if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM && giaTri > 100)
            throw new IllegalArgumentException("Giảm giá phần trăm không được vượt quá 100%.");
        this.giaTri = giaTri;
    }



    public void setDieuKienApDungHoaDon(double dieuKienApDungHoaDon) {
        if (dieuKienApDungHoaDon < 0)
            throw new IllegalArgumentException("Điều kiện áp dụng hóa đơn phải ≥ 0.");
        this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
    }



    public void setSoLuongKhuyenMai(int soLuongKhuyenMai) {
        if (soLuongKhuyenMai < 0)
            throw new IllegalArgumentException("Số lượng khuyến mãi phải ≥ 0.");
        this.soLuongKhuyenMai = soLuongKhuyenMai;
        if (this.soLuongKhuyenMai == 0)
            this.trangThai = false;
    }

    public boolean isDangHoatDong() {
        LocalDate now = LocalDate.now();
        return trangThai && soLuongKhuyenMai > 0
                && ngayBatDau != null && ngayKetThuc != null
                && !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc);
    }

    public void capNhatTrangThaiTuDong() {
        LocalDate now = LocalDate.now();
        this.trangThai = (soLuongKhuyenMai > 0
                && ngayBatDau != null && ngayKetThuc != null
                && !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc));
    }

    @Override
    public String toString() {
        return String.format(
                "KhuyenMai{ma='%s', ten='%s', loai=%s, hinhThuc=%s, giaTri=%.2f, SLKM=%d, hoatDong=%s}",
                maKM, tenKM,
                khuyenMaiHoaDon ? "Hóa đơn" : "Sản phẩm",
                hinhThuc, giaTri, soLuongKhuyenMai,
                isDangHoatDong() ? "Đang áp dụng" : "Ngừng");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhuyenMai)) return false;
        KhuyenMai km = (KhuyenMai) o;
        return Objects.equals(maKM, km.maKM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKM);
    }
}
