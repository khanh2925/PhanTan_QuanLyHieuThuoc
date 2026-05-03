package dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Map;

public class ItemDonHang implements Serializable {
    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    private SanPham sanPham;
    private LoSanPham loSanPham;
    private ChiTietKhuyenMaiSanPham khuyenMai;
    private Map<String, QuyCachDongGoi> mapQuyCach;
    private String tenDonViHienTai;
    private int soLuongMua;
    private double donGiaGoc;
    private double donGiaSauKM;
    private double thanhTienSauKM;
    private double tongGiamGiaSP;
    private boolean coHangTang = false;
    private boolean khoaChinhSua = false;

    public ItemDonHang(SanPham sp, LoSanPham lo, ChiTietKhuyenMaiSanPham km,
                       Map<String, QuyCachDongGoi> mapQC,
                       String tenDonViMacDinh, double giaMacDinh) {
        this.sanPham = sp;
        this.loSanPham = lo;
        this.khuyenMai = km;
        this.mapQuyCach = mapQC;
        this.tenDonViHienTai = tenDonViMacDinh;
        this.donGiaGoc = giaMacDinh;
        this.soLuongMua = 1;
        tinhLaiThanhTien();
    }

    public SanPham getSanPham() { return sanPham; }
    public LoSanPham getLoSanPham() { return loSanPham; }
    public Map<String, QuyCachDongGoi> getMapQuyCach() { return mapQuyCach; }
    public String getTenDonViHienTai() { return tenDonViHienTai; }
    public QuyCachDongGoi getQuyCachHienTai() { return mapQuyCach.get(tenDonViHienTai); }
    public ChiTietKhuyenMaiSanPham getKhuyenMai() { return khuyenMai; }
    public int getSoLuongMua() { return soLuongMua; }
    public double getDonGiaGoc() { return donGiaGoc; }
    public double getDonGiaSauKM() { return donGiaSauKM; }
    public double getThanhTienSauKM() { return thanhTienSauKM; }
    public double getTongGiamGiaSP() { return tongGiamGiaSP; }
    public boolean isCoHangTang() { return coHangTang; }

    public String getTenKhuyenMai() {
        return coKhuyenMaiHopLe() ? khuyenMai.getKhuyenMai().getTenKM() : "Không có KM";
    }

    public String getTenSanPham() { return sanPham.getTenSanPham(); }
    public String getMaLo() { return loSanPham.getMaLo(); }
    public int getTonKho() { return loSanPham.getSoLuongTon(); }

    public boolean isKhoaChinhSua() { return khoaChinhSua; }

    public void setKhoaChinhSua(boolean khoaChinhSua) { this.khoaChinhSua = khoaChinhSua; }

    public void setSoLuongMua(int soLuong) {
        this.soLuongMua = Math.max(1, soLuong);
        tinhLaiThanhTien();
    }

    public void setDonVi(String tenDonVi) {
        if (!mapQuyCach.containsKey(tenDonVi)) return;
        QuyCachDongGoi qcCu = mapQuyCach.get(tenDonViHienTai);
        QuyCachDongGoi qcMoi = mapQuyCach.get(tenDonVi);
        if (qcCu == null || qcMoi == null) return;
        int heSoCu = qcCu.getHeSoQuyDoi();
        int heSoMoi = qcMoi.getHeSoQuyDoi();
        int slQuyVeNhoNhat = this.soLuongMua * heSoCu;
        int slMoi = slQuyVeNhoNhat / heSoMoi;
        if (slMoi < 1) slMoi = 1;
        this.soLuongMua = slMoi;
        double giaBanGoc = sanPham.getGiaBan();
        double donGiaNoiDung = giaBanGoc * heSoMoi;
        this.donGiaGoc = donGiaNoiDung - donGiaNoiDung * qcMoi.getTiLeGiam();
        this.tenDonViHienTai = tenDonVi;
        tinhLaiThanhTien();
    }

    public void tinhLaiThanhTien() {
        double giaGocDonViHienTai = this.donGiaGoc;
        int heSo = getHeSoQuyCach();
        double tienGiamTren1DonVi = 0;

        if (coKhuyenMaiHopLe()) {
            String hinhThuc = this.khuyenMai.getKhuyenMai().getHinhThuc().name();
            double giaTriKM = this.khuyenMai.getKhuyenMai().getGiaTri();
            if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
                tienGiamTren1DonVi = giaGocDonViHienTai * (giaTriKM / 100.0);
            } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
                tienGiamTren1DonVi = giaTriKM * heSo;
            }
        }
        this.donGiaSauKM = Math.max(0, giaGocDonViHienTai - tienGiamTren1DonVi);
        this.thanhTienSauKM = this.donGiaSauKM * this.soLuongMua;
        this.tongGiamGiaSP = (giaGocDonViHienTai - this.donGiaSauKM) * this.soLuongMua;
    }

    public boolean kiemTraTonKhoHopLe(int soLuongTangThemKhongDungNua) {
        QuyCachDongGoi qc = mapQuyCach.get(tenDonViHienTai);
        if (qc == null) return false;
        int heSo = qc.getHeSoQuyDoi();
        int soLuongCanLay = this.soLuongMua * heSo;
        int tonQuyVeNhoNhat = this.loSanPham.getSoLuongTon();
        return soLuongCanLay <= tonQuyVeNhoNhat;
    }

    public int getSoLuongTangThem() { return 0; }

    public int getHeSoQuyCach() {
        QuyCachDongGoi qc = mapQuyCach.get(tenDonViHienTai);
        return qc != null ? qc.getHeSoQuyDoi() : 1;
    }

    public String getDonViGoc() {
        for (QuyCachDongGoi qc : mapQuyCach.values()) {
            if (qc.isDonViGoc()) {
                return qc.getDonViTinh().getTenDonViTinh();
            }
        }
        return tenDonViHienTai;
    }

    public String getTooltipKM() {
        if (!coKhuyenMaiHopLe()) return null;
        String hinhThuc = khuyenMai.getKhuyenMai().getHinhThuc().name();
        double giaTri = khuyenMai.getKhuyenMai().getGiaTri();
        if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
            double giam = donGiaGoc - donGiaSauKM;
            return String.format("<html>Giá gốc: %s/đv<br>Giảm: %s/đv (%.0f%%)<br>Giá sau giảm: %s/đv</html>",
                    DF.format(donGiaGoc), DF.format(giam), giaTri, DF.format(donGiaSauKM));
        } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
            return String.format("<html>Giá gốc: %s/đv<br>Giảm: %s/đv<br>Giá sau giảm: %s/đv</html>",
                    DF.format(donGiaGoc), DF.format(giaTri), DF.format(donGiaSauKM));
        }
        return null;
    }

    public void setKhuyenMai(ChiTietKhuyenMaiSanPham khuyenMai) {
        this.khuyenMai = khuyenMai;
        tinhLaiThanhTien();
    }

    public String getTextKM() {
        if (!coKhuyenMaiHopLe()) return "Không có KM";
        String hinhThuc = khuyenMai.getKhuyenMai().getHinhThuc().name();
        double giaTri = khuyenMai.getKhuyenMai().getGiaTri();
        if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
            return "Giảm " + (int) giaTri + "%";
        } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
            return "Giảm " + DF.format(giaTri) + "/đv";
        }
        return "Không có KM";
    }

    private boolean coKhuyenMaiHopLe() {
        return khuyenMai != null
                && khuyenMai.getKhuyenMai() != null
                && khuyenMai.getKhuyenMai().getHinhThuc() != null;
    }
}

