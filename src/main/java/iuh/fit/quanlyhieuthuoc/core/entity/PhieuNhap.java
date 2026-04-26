package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PhieuNhap")
public class PhieuNhap implements Serializable {

    @Id
    private String maPhieuNhap;

    private LocalDate ngayNhap;

    @ManyToOne
    @JoinColumn(name = "maNhaCungCap")
    private NhaCungCap nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    private double tongTien;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuNhap> chiTietPhieuNhapList;

    public PhieuNhap() {
        this.chiTietPhieuNhapList = new ArrayList<>();
    }

    public PhieuNhap(String maPhieuNhap, LocalDate ngayNhap,
                     NhaCungCap nhaCungCap, NhanVien nhanVien,
                     List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        setMaPhieuNhap(maPhieuNhap);
        setNgayNhap(ngayNhap);
        setNhaCungCap(nhaCungCap);
        setNhanVien(nhanVien);
        setChiTietPhieuNhapList(chiTietPhieuNhapList);
    }
    public String getMaPhieuNhap() { return maPhieuNhap; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public NhaCungCap getNhaCungCap() { return nhaCungCap; }
    public NhanVien getNhanVien() { return nhanVien; }
    public double getTongTien() { return tongTien; }
    public List<ChiTietPhieuNhap> getChiTietPhieuNhapList() { return chiTietPhieuNhapList; }




    public void setMaPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null)
            throw new IllegalArgumentException("Mã phiếu nhập không được để trống");
        maPhieuNhap = maPhieuNhap.trim();
        if (!maPhieuNhap.matches("^PN-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã phiếu nhập không hợp lệ. Định dạng: PN-yyyymmdd-xxxx");
        this.maPhieuNhap = maPhieuNhap;
    }



    public void setNgayNhap(LocalDate ngayNhap) {
        if (ngayNhap == null)
            throw new IllegalArgumentException("Ngày nhập không được null.");
        if (ngayNhap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày nhập không hợp lệ (không được sau ngày hiện tại).");
        this.ngayNhap = ngayNhap;
    }



    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null)
            throw new IllegalArgumentException("Nhà cung cấp không được null.");
        this.nhaCungCap = nhaCungCap;
    }



    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }

    public void setTongTien(double tongTien) { this.tongTien = tongTien; }



    public void setChiTietPhieuNhapList(List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        this.chiTietPhieuNhapList = (chiTietPhieuNhapList != null) ? chiTietPhieuNhapList : new ArrayList<>();
        capNhatTongTienTheoChiTiet();
    }

    public void capNhatTongTienTheoChiTiet() {
        if (chiTietPhieuNhapList == null || chiTietPhieuNhapList.isEmpty()) {
            this.tongTien = 0;
            return;
        }
        double tong = 0;
        for (ChiTietPhieuNhap ctpn : chiTietPhieuNhapList) {
            tong += ctpn.getThanhTien();
        }
        this.tongTien = Math.round(tong * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format(
                "PhieuNhap{ma='%s', ngay=%s, nhaCungCap='%s', tongTien=%.0f}",
                maPhieuNhap, ngayNhap,
                nhaCungCap != null ? nhaCungCap.getTenNhaCungCap() : "null",
                tongTien);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuNhap)) return false;
        PhieuNhap that = (PhieuNhap) o;
        return Objects.equals(maPhieuNhap, that.maPhieuNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }
}
