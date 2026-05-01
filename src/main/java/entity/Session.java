package entity;

import java.io.Serializable;

public class Session implements Serializable {
    private static Session instance;

    private TaiKhoan taiKhoanDangNhap;

    private Session() {}

    public static Session getInstance() {
        if (instance == null)
            instance = new Session();
        return instance;
    }

    public void setTaiKhoanDangNhap(TaiKhoan taiKhoan) {
        this.taiKhoanDangNhap = taiKhoan;
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    public void clearSession() {
        taiKhoanDangNhap = null;
    }
}
