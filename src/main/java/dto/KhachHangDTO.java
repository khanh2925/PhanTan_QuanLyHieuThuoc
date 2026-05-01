package dto;


import java.io.Serializable;
import entity.KhachHang;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho khách hàng
 */
public class KhachHangDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String gioiTinh;
    private String ngaySinh;
    private int soLanMua;
    private double tongChiTieu;

    public KhachHangDTO() {}

    public static KhachHangDTO fromEntity(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.maKhachHang = kh.getMaKhachHang();
        dto.tenKhachHang = kh.getTenKhachHang();
        dto.soDienThoai = kh.getSoDienThoai();
        dto.gioiTinh = kh.isGioiTinh() ? "Nam" : "Nữ";
        dto.ngaySinh = kh.getNgaySinh() != null ? 
            kh.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        return dto;
    }

    // Getters & Setters
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    

    
    public int getSoLanMua() { return soLanMua; }
    public void setSoLanMua(int soLanMua) { this.soLanMua = soLanMua; }
    
    public double getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(double tongChiTieu) { this.tongChiTieu = tongChiTieu; }
}
