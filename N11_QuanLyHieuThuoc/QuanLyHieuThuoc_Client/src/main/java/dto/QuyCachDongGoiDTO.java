package dto;

import java.io.Serializable;

public class QuyCachDongGoiDTO implements Serializable {
    private String maQuyCach;
    private DonViTinhDTO donViTinh;
    private SanPhamDTO sanPham;
    private int heSoQuyDoi;
    private double tiLeGiam;
    private boolean donViGoc;
    private boolean trangThai;

    public QuyCachDongGoiDTO() {}

    public String getMaQuyCach() { return maQuyCach; }
    public void setMaQuyCach(String maQuyCach) { this.maQuyCach = maQuyCach; }
    public DonViTinhDTO getDonViTinh() { return donViTinh; }
    public void setDonViTinh(DonViTinhDTO donViTinh) { this.donViTinh = donViTinh; }
    public SanPhamDTO getSanPham() { return sanPham; }
    public void setSanPham(SanPhamDTO sanPham) { this.sanPham = sanPham; }
    public int getHeSoQuyDoi() { return heSoQuyDoi; }
    public void setHeSoQuyDoi(int heSoQuyDoi) { this.heSoQuyDoi = heSoQuyDoi; }
    public double getTiLeGiam() { return tiLeGiam; }
    public void setTiLeGiam(double tiLeGiam) { this.tiLeGiam = tiLeGiam; }
    public boolean isDonViGoc() { return donViGoc; }
    public void setDonViGoc(boolean donViGoc) { this.donViGoc = donViGoc; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}
