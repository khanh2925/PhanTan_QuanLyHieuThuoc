package dto;

import java.io.Serializable;

public class ChiTietBangGiaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maBangGia;
    private double giaTu;
    private double giaDen;
    private double tiLe;

    public ChiTietBangGiaDTO() {
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public double getGiaTu() {
        return giaTu;
    }

    public void setGiaTu(double giaTu) {
        this.giaTu = giaTu;
    }

    public double getGiaDen() {
        return giaDen;
    }

    public void setGiaDen(double giaDen) {
        this.giaDen = giaDen;
    }

    public double getTiLe() {
        return tiLe;
    }

    public void setTiLe(double tiLe) {
        this.tiLe = tiLe;
    }
}