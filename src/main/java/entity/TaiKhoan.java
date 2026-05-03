package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable {

    @Id
    private String maTaiKhoan;

    private String tenDangNhap;
    private String matKhau;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String matKhau, NhanVien nhanVien) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setNhanVien(nhanVien);
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, NhanVien nhanVien) {
        setMaTaiKhoan(maTaiKhoan);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setNhanVien(nhanVien);
    }
    public String getMaTaiKhoan() { return maTaiKhoan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public NhanVien getNhanVien() { return nhanVien; }




    public void setMaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null)
            throw new IllegalArgumentException("Mã tài khoản không được để trống");
        maTaiKhoan = maTaiKhoan.trim();
        if (!maTaiKhoan.matches("^TK-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ. Định dạng: TK-yyyymmdd-xxxx");
        this.maTaiKhoan = maTaiKhoan;
    }



    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null)
            throw new IllegalArgumentException("Tên đăng nhập không được null.");
        tenDangNhap = tenDangNhap.trim();
        if (tenDangNhap.isEmpty())
            throw new IllegalArgumentException("Tên đăng nhập không được rỗng.");
        if (!tenDangNhap.matches("^[\\S]{5,30}$"))
            throw new IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng, độ dài 5–30 ký tự.");
        this.tenDangNhap = tenDangNhap;
    }



    public void setMatKhau(String matKhau) {
        if (matKhau == null)
            throw new IllegalArgumentException("Mật khẩu không được null.");
        if (matKhau.length() < 8)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        if (!matKhau.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$"))
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường và 1 số.");
        this.matKhau = matKhau;
    }



    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Tài khoản phải gắn với một nhân viên hợp lệ (không null).");
        this.nhanVien = nhanVien;
    }

    @Override
    public String toString() {
        return String.format("TaiKhoan{ma='%s', tenDangNhap='%s', nhanVien='%s'}",
                maTaiKhoan, tenDangNhap,
                nhanVien != null ? nhanVien.getTenNhanVien() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoan)) return false;
        TaiKhoan that = (TaiKhoan) o;
        return Objects.equals(maTaiKhoan, that.maTaiKhoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTaiKhoan);
    }
}
