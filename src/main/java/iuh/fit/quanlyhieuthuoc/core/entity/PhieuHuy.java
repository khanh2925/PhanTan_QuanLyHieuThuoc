package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PhieuHuy")
public class PhieuHuy implements Serializable {

    @Id
    private String maPhieuHuy;

    private LocalDate ngayLapPhieu;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    private boolean trangThai;
    private double tongTien;

    @OneToMany(mappedBy = "phieuHuy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuHuy> chiTietPhieuHuyList;

    public PhieuHuy() {
        this.chiTietPhieuHuyList = new ArrayList<>();
        this.ngayLapPhieu = LocalDate.now();
    }

    public PhieuHuy(String maPhieuHuy, LocalDate ngayLapPhieu,
                    NhanVien nhanVien, boolean trangThai) {
        setMaPhieuHuy(maPhieuHuy);
        setNgayLapPhieu(ngayLapPhieu);
        setNhanVien(nhanVien);
        setTrangThai(trangThai);
        this.chiTietPhieuHuyList = new ArrayList<>();
        capNhatTongTienTheoChiTiet();
    }

    public PhieuHuy(PhieuHuy other) {
        this.maPhieuHuy = other.maPhieuHuy;
        this.ngayLapPhieu = other.ngayLapPhieu;
        this.nhanVien = other.nhanVien;
        this.trangThai = other.trangThai;
        this.tongTien = other.tongTien;
        this.chiTietPhieuHuyList = new ArrayList<>(other.chiTietPhieuHuyList);
    }
    public String getMaPhieuHuy() { return maPhieuHuy; }
    public LocalDate getNgayLapPhieu() { return ngayLapPhieu; }
    public NhanVien getNhanVien() { return nhanVien; }
    public boolean isTrangThai() { return trangThai; }
    public double getTongTien() { return tongTien; }
    public List<ChiTietPhieuHuy> getChiTietPhieuHuyList() { return chiTietPhieuHuyList; }




    public void setMaPhieuHuy(String maPhieuHuy) {
        if (maPhieuHuy == null)
            throw new IllegalArgumentException("Mã phiếu hủy không được để trống");
        maPhieuHuy = maPhieuHuy.trim();
        if (!maPhieuHuy.matches("^PH-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã phiếu hủy không hợp lệ. Định dạng: PH-yyyymmdd-xxxx");
        this.maPhieuHuy = maPhieuHuy;
    }



    public void setNgayLapPhieu(LocalDate ngayLapPhieu) {
        if (ngayLapPhieu == null || ngayLapPhieu.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày lập phiếu không hợp lệ (không được sau hiện tại).");
        this.ngayLapPhieu = ngayLapPhieu;
    }



    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên quản lý không tồn tại.");
        this.nhanVien = nhanVien;
    }

    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public String getTrangThaiText() {
        return trangThai ? "Đã duyệt" : "Chờ duyệt";
    }



    public void capNhatTongTienTheoChiTiet() {
        if (chiTietPhieuHuyList == null || chiTietPhieuHuyList.isEmpty()) {
            this.tongTien = 0;
            return;
        }
        double sum = 0;
        for (ChiTietPhieuHuy ct : chiTietPhieuHuyList) {
            sum += ct.getThanhTien();
        }
        this.tongTien = Math.round(sum * 100.0) / 100.0;
    }



    public void setChiTietPhieuHuyList(List<ChiTietPhieuHuy> chiTietPhieuHuyList) {
        if (chiTietPhieuHuyList == null)
            throw new IllegalArgumentException("Danh sách chi tiết phiếu hủy không được null.");
        this.chiTietPhieuHuyList = chiTietPhieuHuyList;
        capNhatTongTienTheoChiTiet();
    }

    @Override
    public String toString() {
        return String.format("PhieuHuy[%s - %s - NV:%s - TT:%.2fđ - %s]",
                maPhieuHuy, ngayLapPhieu,
                nhanVien != null ? nhanVien.getMaNhanVien() : "N/A",
                tongTien, getTrangThaiText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuHuy)) return false;
        PhieuHuy phieuHuy = (PhieuHuy) o;
        return Objects.equals(maPhieuHuy, phieuHuy.maPhieuHuy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuHuy);
    }
}
