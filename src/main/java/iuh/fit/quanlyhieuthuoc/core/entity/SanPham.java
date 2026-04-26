package iuh.fit.quanlyhieuthuoc.core.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import iuh.fit.quanlyhieuthuoc.core.enums.DuongDung;
import iuh.fit.quanlyhieuthuoc.core.enums.LoaiSanPham;

@Entity
@Table(name = "SanPham")
public class SanPham implements Serializable {

    @Id
    private String maSanPham;

    private String tenSanPham;

    @Enumerated(EnumType.STRING)
    private LoaiSanPham loaiSanPham;

    private String soDangKy;

    @Enumerated(EnumType.STRING)
    private DuongDung duongDung;

    private double giaNhap;
    private double giaBan;
    private String hinhAnh;
    private String keBanSanPham;
    private boolean hoatDong;

    @Transient
    private ChiTietBangGia chiTietBangGiaHienTai;

    @Transient
    private ChiTietKhuyenMaiSanPham khuyenMaiHienTai;

    public SanPham() {}

    public SanPham(String maSanPham) {
        setMaSanPham(maSanPham);
    }

    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy,
                   DuongDung duongDung, double giaNhap, String hinhAnh,
                   String keBanSanPham, boolean hoatDong) {
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setLoaiSanPham(loaiSanPham);
        setSoDangKy(soDangKy);
        setDuongDung(duongDung);
        setGiaNhap(giaNhap);
        setHinhAnh(hinhAnh);
        setKeBanSanPham(keBanSanPham);
        setHoatDong(hoatDong);
        this.giaBan = 0;
    }

    public SanPham(SanPham sp) {
        this.maSanPham = sp.maSanPham;
        this.tenSanPham = sp.tenSanPham;
        this.loaiSanPham = sp.loaiSanPham;
        this.soDangKy = sp.soDangKy;
        this.duongDung = sp.duongDung;
        this.giaNhap = sp.giaNhap;
        this.giaBan = sp.giaBan;
        this.hinhAnh = sp.hinhAnh;
        this.keBanSanPham = sp.keBanSanPham;
        this.hoatDong = sp.hoatDong;
        this.chiTietBangGiaHienTai = sp.chiTietBangGiaHienTai;
        this.khuyenMaiHienTai = sp.khuyenMaiHienTai;
    }
    public String getMaSanPham() { return maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public LoaiSanPham getLoaiSanPham() { return loaiSanPham; }
    public String getSoDangKy() { return soDangKy; }
    public DuongDung getDuongDung() { return duongDung; }
    public double getGiaNhap() { return giaNhap; }
    public String getHinhAnh() { return hinhAnh; }
    public String getKeBanSanPham() { return keBanSanPham; }
    public boolean isHoatDong() { return hoatDong; }
    public ChiTietBangGia getChiTietBangGiaHienTai() { return chiTietBangGiaHienTai; }
    public ChiTietKhuyenMaiSanPham getKhuyenMaiHienTai() { return khuyenMaiHienTai; }




    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null)
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        maSanPham = maSanPham.trim();
        if (!maSanPham.matches("^SP-\\d{6}$"))
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ. Định dạng: SP-xxxxxx");
        this.maSanPham = maSanPham;
    }



    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        if (tenSanPham.length() > 100)
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 100 ký tự.");
        this.tenSanPham = tenSanPham.trim();
    }



    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null)
            throw new IllegalArgumentException("Loại sản phẩm không được null.");
        this.loaiSanPham = loaiSanPham;
    }



    public void setSoDangKy(String soDangKy) {
        if (soDangKy != null && soDangKy.length() > 20)
            throw new IllegalArgumentException("Số đăng ký không hợp lệ (tối đa 20 ký tự).");
        this.soDangKy = soDangKy;
    }

    public void setDuongDung(DuongDung duongDung) { this.duongDung = duongDung; }



    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0)
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        this.giaNhap = giaNhap;
        capNhatGiaBanTheoTiLe();
    }

    public double getGiaBan() {
        if (chiTietBangGiaHienTai == null) {
            System.err.println("⚠ CẢNH BÁO: Sản phẩm '" + maSanPham + " - " + tenSanPham
                    + "' không có bảng giá. Giá bán = 0.");
            giaBan = 0;
        }
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }



    public void setChiTietBangGiaHienTai(ChiTietBangGia chiTietBangGiaHienTai) {
        if (chiTietBangGiaHienTai == null)
            throw new IllegalArgumentException("Sản phẩm phải có bảng giá để xác định giá bán.");
        this.chiTietBangGiaHienTai = chiTietBangGiaHienTai;
        capNhatGiaBanTheoTiLe();
    }

    public void capNhatGiaBanTheoTiLe() {
        if (chiTietBangGiaHienTai == null) {
            this.giaBan = 0;
            return;
        }
        double tiLe = chiTietBangGiaHienTai.getTiLe();
        if (tiLe <= 0)
            throw new IllegalArgumentException("Tỉ lệ bảng giá không hợp lệ (phải > 0).");
        this.giaBan = Math.round(giaNhap * tiLe);
    }



    public void setHinhAnh(String hinhAnh) {
        if (hinhAnh != null && hinhAnh.length() > 255)
            throw new IllegalArgumentException("Đường dẫn hình ảnh không được vượt quá 255 ký tự.");
        this.hinhAnh = hinhAnh;
    }



    public void setKeBanSanPham(String keBanSanPham) {
        if (keBanSanPham != null && keBanSanPham.length() > 100)
            throw new IllegalArgumentException("Kệ bán sản phẩm không được vượt quá 100 ký tự.");
        this.keBanSanPham = keBanSanPham;
    }

    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }

    public void setKhuyenMaiHienTai(ChiTietKhuyenMaiSanPham khuyenMaiHienTai) {
        this.khuyenMaiHienTai = khuyenMaiHienTai;
    }

    @Override
    public String toString() {
        return String.format(
                "SanPham[%s - %s, loại=%s, giá nhập=%.0f, tỉ lệ=%s, giá bán=%.0f, KM=%s]",
                maSanPham, tenSanPham,
                loaiSanPham != null ? loaiSanPham : "N/A",
                giaNhap,
                chiTietBangGiaHienTai != null ? chiTietBangGiaHienTai.getTiLe() : "Chưa có bảng giá",
                giaBan,
                khuyenMaiHienTai != null ? khuyenMaiHienTai.getKhuyenMai().getMaKM() : "Không");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanPham)) return false;
        SanPham sp = (SanPham) o;
        return Objects.equals(maSanPham, sp.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}
