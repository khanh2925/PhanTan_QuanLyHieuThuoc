package dto;

import java.io.Serializable;

import entity.TaiKhoan;

public class TaiKhoanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private String maNhanVien;
    private String tenNhanVien;
    private String vaiTro;
    private boolean nhanVienDangLam;

    public TaiKhoanDTO() {}

    public static TaiKhoanDTO fromEntity(TaiKhoan tk) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.maTaiKhoan = tk.getMaTaiKhoan();
        dto.tenDangNhap = tk.getTenDangNhap();
        dto.maNhanVien = tk.getNhanVien() != null ? tk.getNhanVien().getMaNhanVien() : "";
        dto.tenNhanVien = tk.getNhanVien() != null ? tk.getNhanVien().getTenNhanVien() : "";
        dto.vaiTro = tk.getNhanVien() != null && tk.getNhanVien().isQuanLy() ? "Quan ly" : "Nhan vien";
        dto.nhanVienDangLam = tk.getNhanVien() == null || tk.getNhanVien().isTrangThai();
        return dto;
    }

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public boolean isNhanVienDangLam() { return nhanVienDangLam; }
    public void setNhanVienDangLam(boolean nhanVienDangLam) { this.nhanVienDangLam = nhanVienDangLam; }
}