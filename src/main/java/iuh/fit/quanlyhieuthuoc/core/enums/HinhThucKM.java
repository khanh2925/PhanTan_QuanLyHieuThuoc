package iuh.fit.quanlyhieuthuoc.core.enums;

/**
 * Mô tả: Hình thức khuyến mãi áp dụng cho sản phẩm / hóa đơn.
 */
public enum HinhThucKM {
    GIAM_GIA_PHAN_TRAM("Giảm giá %"),
    GIAM_GIA_TIEN("Giảm giá tiền mặt");

    private final String moTa;

    HinhThucKM(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    @Override
    public String toString() {
        return moTa;
    }
}
