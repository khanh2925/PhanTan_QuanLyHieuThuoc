package dto;

import dto.TaiKhoanDTO;
import java.io.Serializable;

public class Session implements Serializable {
    private static Session instance;

    private TaiKhoanDTO taiKhoanDangNhap;

    private Session() {}

    public static Session getInstance() {
        if (instance == null)
            instance = new Session();
        return instance;
    }

    public void setTaiKhoanDangNhap(TaiKhoanDTO dto) {
        this.taiKhoanDangNhap = dto;
    }

    public TaiKhoanDTO getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    public void clearSession() {
        taiKhoanDangNhap = null;
    }
}

