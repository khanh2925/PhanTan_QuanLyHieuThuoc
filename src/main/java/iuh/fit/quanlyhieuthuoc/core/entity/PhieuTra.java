package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PhieuTra")
public class PhieuTra implements Serializable {

    @Id
    private String maPhieuTra;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    private LocalDate ngayLap;
    private boolean trangThai;
    private double tongTienHoan;

    @OneToMany(mappedBy = "phieuTra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuTra> chiTietPhieuTraList;

    public PhieuTra() {
        this.chiTietPhieuTraList = new ArrayList<>();
        this.ngayLap = LocalDate.now();
        this.trangThai = false;
        this.tongTienHoan = 0;
    }

    public PhieuTra(String maPhieuTra, KhachHang khachHang, NhanVien nhanVien,
                    LocalDate ngayLap, boolean trangThai,
                    List<ChiTietPhieuTra> chiTietPhieuTraList) {
        setMaPhieuTra(maPhieuTra);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
        setNgayLap(ngayLap);
        setTrangThai(trangThai);
        setChiTietPhieuTraList(chiTietPhieuTraList);
        capNhatTongTienHoan();
    }
    public String getMaPhieuTra() { return maPhieuTra; }
    public KhachHang getKhachHang() { return khachHang; }
    public NhanVien getNhanVien() { return nhanVien; }
    public LocalDate getNgayLap() { return ngayLap; }
    public boolean isTrangThai() { return trangThai; }
    public double getTongTienHoan() { return tongTienHoan; }
    public List<ChiTietPhieuTra> getChiTietPhieuTraList() { return chiTietPhieuTraList; }




    public void setMaPhieuTra(String maPhieuTra) {
        if (maPhieuTra == null)
            throw new IllegalArgumentException("Mã phiếu trả không được để trống");
        maPhieuTra = maPhieuTra.trim();
        if (!maPhieuTra.matches("^PT-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng: PT-yyyymmdd-xxxx");
        this.maPhieuTra = maPhieuTra;
    }



    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null)
            throw new IllegalArgumentException("Khách hàng không được null.");
        this.khachHang = khachHang;
    }



    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }



    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày lập không hợp lệ (≤ ngày hiện tại).");
        this.ngayLap = ngayLap;
    }



    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
        if (trangThai && khachHang != null) {
            capNhatTongTienHoan();
        }
    }





    public void setChiTietPhieuTraList(List<ChiTietPhieuTra> chiTietPhieuTraList) {
        if (chiTietPhieuTraList == null)
            throw new IllegalArgumentException("Danh sách chi tiết phiếu trả không được null.");
        this.chiTietPhieuTraList = chiTietPhieuTraList;
        capNhatTongTienHoan();
    }

    public void capNhatTongTienHoan() {
        if (chiTietPhieuTraList == null || chiTietPhieuTraList.isEmpty()) {
            this.tongTienHoan = 0;
            return;
        }
        this.tongTienHoan = chiTietPhieuTraList.stream()
                .mapToDouble(ChiTietPhieuTra::getThanhTienHoan)
                .sum();
    }

    public String getTrangThaiText() {
        return trangThai ? "Đã duyệt" : "Đang chờ duyệt";
    }

    @Override
    public String toString() {
        return String.format("PhieuTra[%s | %s | %s | %.2fđ | %s]",
                maPhieuTra,
                khachHang != null ? khachHang.getTenKhachHang() : "N/A",
                ngayLap, tongTienHoan, getTrangThaiText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuTra)) return false;
        PhieuTra that = (PhieuTra) o;
        return Objects.equals(maPhieuTra, that.maPhieuTra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuTra);
    }
}
