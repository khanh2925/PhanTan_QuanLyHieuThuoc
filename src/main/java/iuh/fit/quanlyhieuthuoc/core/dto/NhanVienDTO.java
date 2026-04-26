package iuh.fit.quanlyhieuthuoc.core.dto;

import iuh.fit.quanlyhieuthuoc.core.entity.NhanVien;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho nhân viên - ẩn thông tin nhạy cảm
 */
public class NhanVienDTO {
    private String maNhanVien;
    private String tenNhanVien;
    private String gioiTinh;
    private String ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro;
    private String caLam;
    private String trangThai;
    private int soHoaDonThang;
    private double doanhThuThang;

    public NhanVienDTO() {}

    public static NhanVienDTO fromEntity(NhanVien nv) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.maNhanVien = nv.getMaNhanVien();
        dto.tenNhanVien = nv.getTenNhanVien();
        dto.gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
        dto.ngaySinh = nv.getNgaySinh() != null ? 
            nv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        dto.soDienThoai = nv.getSoDienThoai();
        dto.diaChi = nv.getDiaChi();
        dto.vaiTro = nv.isQuanLy() ? "Quản lý" : "Nhân viên";
        dto.caLam = getCaLamText(nv.getCaLam());
        dto.trangThai = nv.isTrangThai() ? "Đang làm" : "Nghỉ việc";
        return dto;
    }

    private static String getCaLamText(int caLam) {
        switch (caLam) {
            case 1: return "Ca sáng (6h-14h)";
            case 2: return "Ca chiều (14h-22h)";
            case 3: return "Ca tối (22h-6h)";
            default: return "Chưa xác định";
        }
    }

    // Getters & Setters
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    
    public String getCaLam() { return caLam; }
    public void setCaLam(String caLam) { this.caLam = caLam; }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    public int getSoHoaDonThang() { return soHoaDonThang; }
    public void setSoHoaDonThang(int soHoaDonThang) { this.soHoaDonThang = soHoaDonThang; }
    
    public double getDoanhThuThang() { return doanhThuThang; }
    public void setDoanhThuThang(double doanhThuThang) { this.doanhThuThang = doanhThuThang; }
}
