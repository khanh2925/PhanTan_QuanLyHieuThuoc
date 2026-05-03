package dto;

import java.io.Serializable;

public class DonViTinhDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maDonViTinh;
    private String tenDonViTinh;

    public String getMaDonViTinh() { return maDonViTinh; }
    public void setMaDonViTinh(String maDonViTinh) { this.maDonViTinh = maDonViTinh; }

    public String getTenDonViTinh() { return tenDonViTinh; }
    public void setTenDonViTinh(String tenDonViTinh) { this.tenDonViTinh = tenDonViTinh; }
}
