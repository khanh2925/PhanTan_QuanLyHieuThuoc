package iuh.fit.quanlyhieuthuoc.core.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name="KhachHang")
public class KhachHang implements Serializable {

    @Id
    @Column(name = "MaKhachHang", length = 50)
    private String maKhachHang;
    
    @Column(name = "TenKhachHang", columnDefinition = "NVARCHAR(100)")
    private String tenKhachHang;
    
    @Column(name = "GioiTinh")
    private boolean gioiTinh;
    
    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;
    
    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;
    
    @Column(name = "HoatDong")
    private boolean hoatDong = true;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<HoaDon> danhSachHoaDon;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh,
                     String soDienThoai, LocalDate ngaySinh, boolean hoatDong) {
        setMaKhachHang(maKhachHang);
        setTenKhachHang(tenKhachHang);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setNgaySinh(ngaySinh);
        setHoatDong(hoatDong);
    }
    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public boolean isGioiTinh() { return gioiTinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public List<HoaDon> getDanhSachHoaDon() { return danhSachHoaDon; }




    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null)
            throw new IllegalArgumentException("Mã khách hàng không được để trống");
        maKhachHang = maKhachHang.trim();
        if (!maKhachHang.matches("^KH-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ. Định dạng: KH-yyyymmdd-xxxx");
        }
        this.maKhachHang = maKhachHang;
    }



    public void setTenKhachHang(String tenKhachHang) {
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty())
            throw new IllegalArgumentException("Tên khách hàng không được rỗng.");
        if (tenKhachHang.length() > 100)
            throw new IllegalArgumentException("Tên khách hàng không vượt quá 100 ký tự.");
        this.tenKhachHang = tenKhachHang.trim();
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }



    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || !soDienThoai.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("SĐT không hợp lệ (10 chữ số, bắt đầu bằng 0).");
        this.soDienThoai = soDienThoai;
    }



    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        if (ngaySinh.isAfter(LocalDate.now().minusYears(16)))
            throw new IllegalArgumentException("Khách hàng phải từ 16 tuổi trở lên.");
        this.ngaySinh = ngaySinh;
    }

    public boolean isHoatDong() { return hoatDong; }
    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
        this.danhSachHoaDon = danhSachHoaDon;
    }

    public String getTrangThaiText() {
		return hoatDong ? "Hoạt động" : "Ngừng";
	}

    @Override
    public String toString() {
        return String.format(
                "KhachHang{ma='%s', ten='%s', sdt='%s', %s}",
                maKhachHang,
                tenKhachHang,
                soDienThoai,
                hoatDong ? "Hoạt động" : "Ngừng"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof KhachHang))
            return false;
        KhachHang that = (KhachHang) o;
        return Objects.equals(maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }
}
