package network;

import dto.*;
import dto.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * High-level client service that wraps socket requests.
 */
public class ClientService {
    private final String host;
    private final int port;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ClientService() {
        this("localhost", 9090);
    }

    public ClientService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalDate.parse(trimmed, DISPLAY_DATE_FORMAT);
        } catch (Exception ignored) {
            try {
                return LocalDate.parse(trimmed, ISO_DATE_FORMAT);
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private Response sendReq(CommandType cmd, Object data) {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            return cs.sendRequest(new Request(cmd, data));
        } catch (Exception e) {
            System.err.println("ClientService [" + cmd + "]: " + e.getMessage());
            return null;
        }
    }

    public TaiKhoanDTO login(String username, String password) {
        Response r = sendReq(CommandType.TAIKHOAN_DANG_NHAP, new Object[] { username, password });
        if (r != null && r.isSuccess() && r.getData() instanceof TaiKhoanDTO dto) {
            return dto;
        }
        return null;
    }

    public List<NhanVien> getAllNhanVien() {
        Response r = sendReq(CommandType.NHANVIEN_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<NhanVien> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof NhanVienDTO dto) result.add(toNhanVien(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public String taoMaNhanVien() {
        Response r = sendReq(CommandType.NHANVIEN_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createNhanVien(Object nv) {
        Object payload = nv instanceof NhanVien e ? toNhanVienDTO(e) : nv;
        Response r = sendReq(CommandType.NHANVIEN_THEM, payload);
            return r != null && r.isSuccess();
    }

    public boolean updateNhanVien(Object nv) {
        Object payload = nv instanceof NhanVien e ? toNhanVienDTO(e) : nv;
        Response r = sendReq(CommandType.NHANVIEN_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    public List<KhachHang> getAllKhachHang() {
        Response r = sendReq(CommandType.KHACHHANG_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<KhachHang> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof KhachHangDTO dto) result.add(toKhachHang(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<?> getAllSanPham() {
        Response r = sendReq(CommandType.SANPHAM_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public Object findProductByRegistration(String soDangKy) {
        Response r = sendReq(CommandType.SANPHAM_TIM_THEO_SO_DANG_KY, soDangKy);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public Object getProductByCode(String maSanPham) {
        Response r = sendReq(CommandType.SANPHAM_LAY_THEO_MA, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof SanPhamDTO dto) return toSanPham(dto);
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllLots() {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return mapLotList((java.util.List<?>) r.getData());
            }
            return java.util.Collections.emptyList();
    }

    public Object getLotByCode(String maLo) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA, maLo);
            if (r != null && r.isSuccess() && r.getData() instanceof LoSanPhamDTO dto) return toLoSanPham(dto);
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchProducts(String tuKhoa) {
        Response r = sendReq(CommandType.SANPHAM_TIM_KIEM, tuKhoa);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsByProduct(String maSanPham) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA_SP, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return mapLotList((java.util.List<?>) r.getData());
            }
            return java.util.Collections.emptyList();
    }

    public int getLotQuantity(String maLo, String maSanPham) {
        java.util.List<?> lots = getLotsByProduct(maSanPham);
        for (Object o : lots) {
            try {
                java.lang.reflect.Field fMa = o.getClass().getDeclaredField("maLo");
                fMa.setAccessible(true);
                Object val = fMa.get(o);
                if (val != null && val.toString().equals(maLo)) {
                    java.lang.reflect.Field fQty = o.getClass().getDeclaredField("soLuongTon");
                    fQty.setAccessible(true);
                    Object q = fQty.get(o);
                    if (q instanceof Number) return ((Number) q).intValue();
                }
            } catch (Exception nsf) {
                // ignore
            }
        }
        return 0;
    }

    public HoaDon getHoaDonByCode(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_THEO_MA, maHD);
        if (r != null && r.isSuccess() && r.getData() instanceof HoaDonDTO dto) return toHoaDon(dto);
        return null;
    }

    public java.util.List<ChiTietHoaDon> getChiTietHoaDonByMaHD(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_CHI_TIET, maHD);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<ChiTietHoaDon> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof ChiTietHoaDonDTO dto) result.add(toChiTietHoaDon(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsInStockByProduct(String maSanPham) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_CON_HANG, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public String taoMaHoaDon() {
        Response r = sendReq(CommandType.HOADON_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createHoaDon(Object hoaDon) {
        Response r = sendReq(CommandType.HOADON_THEM, hoaDon);
            return r != null && r.isSuccess();
    }

    public String taoMaBangGia() {
        Response r = sendReq(CommandType.BANGGIA_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllBangGia() {
        Response r = sendReq(CommandType.BANGGIA_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchBangGia(String keyword) {
        java.util.List<?> all = getAllBangGia();
        if (keyword == null || keyword.isBlank()) return all;
        String kw = keyword.trim().toLowerCase();
        java.util.List<Object> rs = new java.util.ArrayList<>();
        for (Object o : all) {
            if (o == null) continue;
            try {
                java.lang.reflect.Method m1 = o.getClass().getMethod("getMaBangGia");
                java.lang.reflect.Method m2 = o.getClass().getMethod("getTenBangGia");
                Object ma = m1.invoke(o);
                Object ten = m2.invoke(o);
                String s = String.valueOf(ma).toLowerCase() + " " + String.valueOf(ten).toLowerCase();
                if (s.contains(kw)) rs.add(o);
            } catch (Exception ignored) {
            }
        }
        return rs;
    }

    public Object getBangGiaByCode(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_LAY_THEO_MA, maBangGia);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public Object getBangGiaDangHoatDong() {
        Response r = sendReq(CommandType.BANGGIA_LAY_DANG_HOAT_DONG, null);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietBangGia(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_LAY_CHI_TIET, maBangGia);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public boolean createBangGia(Object bangGia) {
        Object payload = bangGia instanceof BangGia e ? toBangGiaDTO(e) : bangGia;
        Response r = sendReq(CommandType.BANGGIA_THEM, payload);
            return r != null && r.isSuccess();
    }

    public boolean updateBangGia(Object bangGia) {
        Object payload = bangGia instanceof BangGia e ? toBangGiaDTO(e) : bangGia;
        Response r = sendReq(CommandType.BANGGIA_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    public boolean deleteBangGia(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_XOA, maBangGia);
            return r != null && r.isSuccess();
    }

    public boolean deactivateAllBangGiaExcept(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_HUY_HOAT_DONG_TAT_CA_TRU, maBangGia);
            return r != null && r.isSuccess();
    }

    public boolean createChiTietBangGia(Object chiTiet) {
        Response r = sendReq(CommandType.BANGGIA_THEM_CHI_TIET, chiTiet);
            return r != null && r.isSuccess();
    }

    public boolean deleteAllChiTietBangGia(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_XOA_TAT_CA_CHI_TIET, maBangGia);
            return r != null && r.isSuccess();
    }

    public String taoMaDonViTinh() {
        Response r = sendReq(CommandType.DONVITINH_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public java.util.List<DonViTinh> getAllDonViTinh() {
        Response r = sendReq(CommandType.DONVITINH_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<DonViTinh> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof DonViTinhDTO dto) result.add(toDonViTinh(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public Object getDonViTinhByCode(String maDonViTinh) {
        Response r = sendReq(CommandType.DONVITINH_LAY_THEO_MA, maDonViTinh);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public boolean createDonViTinh(Object dvt) {
        Object payload = dvt instanceof DonViTinh e ? toDonViTinhDTO(e) : dvt;
        Response r = sendReq(CommandType.DONVITINH_THEM, payload);
            return r != null && r.isSuccess();
    }

    public boolean updateDonViTinh(Object dvt) {
        Object payload = dvt instanceof DonViTinh e ? toDonViTinhDTO(e) : dvt;
        Response r = sendReq(CommandType.DONVITINH_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    public boolean deleteDonViTinh(String maDonViTinh) {
        Response r = sendReq(CommandType.DONVITINH_XOA, maDonViTinh);
            return r != null && r.isSuccess();
    }

    public String taoMaKhachHang() {
        Response r = sendReq(CommandType.KHACHHANG_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public java.util.List<KhachHang> getAllKhachHangForGUI() {
        return getAllKhachHang();
    }

    public KhachHang getKhachHangByCode(String maKhachHang) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_MA, maKhachHang);
        if (r != null && r.isSuccess() && r.getData() instanceof KhachHangDTO dto) return toKhachHang(dto);
        return null;
    }

    public KhachHang getKhachHangByPhone(String soDienThoai) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_SDT, soDienThoai);
        if (r != null && r.isSuccess() && r.getData() instanceof KhachHangDTO dto) return toKhachHang(dto);
        return null;
    }

    public boolean createKhachHang(Object kh) {
        Object payload = kh instanceof KhachHang e ? toKhachHangDTO(e) : kh;
        Response r = sendReq(CommandType.KHACHHANG_THEM, payload);
            return r != null && r.isSuccess();
    }

    public String taoMaKhuyenMai() {
        Response r = sendReq(CommandType.KHUYENMAI_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllKhuyenMai() {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public Object getKhuyenMaiByCode(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_THEO_MA, maKM);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public java.util.List<ChiTietKhuyenMaiSanPham> getChiTietKhuyenMaiByMaKM(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_CHI_TIET, maKM);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<ChiTietKhuyenMaiSanPham> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof ChiTietKhuyenMaiSanPhamDTO dto) result.add(toChiTietKhuyenMaiSanPham(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public boolean createKhuyenMai(Object km) {
        Object payload = km instanceof KhuyenMai e ? toKhuyenMaiDTO(e) : km;
        Response r = sendReq(CommandType.KHUYENMAI_THEM, payload);
            return r != null && r.isSuccess();
    }

    public boolean updateKhuyenMai(Object km) {
        Object payload = km instanceof KhuyenMai e ? toKhuyenMaiDTO(e) : km;
        Response r = sendReq(CommandType.KHUYENMAI_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    public boolean deleteKhuyenMai(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_XOA, maKM);
            return r != null && r.isSuccess();
    }

    public boolean createChiTietKhuyenMai(String maKM, String maSanPham) {
        Response r = sendReq(CommandType.KHUYENMAI_THEM_CHI_TIET_SP, new Object[]{ maKM, maSanPham });
            return r != null && r.isSuccess();
    }

    public boolean deleteChiTietKhuyenMai(String maKM, String maSanPham) {
        Response r = sendReq(CommandType.KHUYENMAI_XOA_CHI_TIET_SP, new Object[]{ maKM, maSanPham });
            return r != null && r.isSuccess();
    }

    public boolean updateKhachHang(Object kh) {
        Object payload = kh instanceof KhachHang e ? toKhachHangDTO(e) : kh;
        Response r = sendReq(CommandType.KHACHHANG_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    public boolean deleteKhachHang(String maKhachHang) {
        Response r = sendReq(CommandType.KHACHHANG_XOA, maKhachHang);
            return r != null && r.isSuccess();
    }

    public String taoMaNhaCungCap() {
        Response r = sendReq(CommandType.NHACUNGCAP_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public java.util.List<NhaCungCap> getAllNhaCungCap() {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<NhaCungCap> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof NhaCungCapDTO dto) result.add(toNhaCungCap(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public Object getNhaCungCapByCodeOrPhone(String keyword) {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_THEO_MA_HOAC_SDT, keyword);
            if (r != null && r.isSuccess() && r.getData() instanceof NhaCungCapDTO dto) return toNhaCungCap(dto);
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchNhaCungCap(String keyword, String khuVuc, String trangThai, String tieuChi) {
        Response r = sendReq(CommandType.NHACUNGCAP_TIM_KIEM, new Object[]{ keyword, khuVuc, trangThai, tieuChi });
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public boolean createNhaCungCap(Object ncc) {
        Object payload = ncc instanceof NhaCungCap e ? toNhaCungCapDTO(e) : ncc;
        Response r = sendReq(CommandType.NHACUNGCAP_THEM, payload);
            return r != null && r.isSuccess();
    }

    public boolean updateNhaCungCap(Object ncc) {
        Object payload = ncc instanceof NhaCungCap e ? toNhaCungCapDTO(e) : ncc;
        Response r = sendReq(CommandType.NHACUNGCAP_CAP_NHAT, payload);
            return r != null && r.isSuccess();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllPhieuNhap() {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public Object getPhieuNhapByCode(String maPhieu) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_THEO_MA, maPhieu);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietPhieuNhapByMa(String maPhieu) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_CHI_TIET, maPhieu);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public java.util.List<QuyCachDongGoi> getPackagingRulesByProduct(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_THEO_SAN_PHAM, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<QuyCachDongGoi> result = new java.util.ArrayList<>();
            for (Object item : raw) {
                if (item instanceof QuyCachDongGoiDTO dto) result.add(toQuyCachDongGoi(dto));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<ChiTietKhuyenMaiSanPham> getActivePromotionDetailsByProduct(String maSanPham) {
        Response r = sendReq(CommandType.SANPHAM_LAY_KHUYEN_MAI, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<ChiTietKhuyenMaiSanPham> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof ChiTietKhuyenMaiSanPhamDTO dto) result.add(toChiTietKhuyenMaiSanPham(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<KhuyenMai> getActiveKhuyenMai() {
        java.util.List<KhuyenMai> result = new java.util.ArrayList<>();
        for (KhuyenMai km : getAllKhuyenMaiEntity()) if (km.isDangHoatDong()) result.add(km);
        return result;
    }

    public boolean reduceKhuyenMaiQuantity(String maKM) {
        KhuyenMai km = getKhuyenMaiEntityByCode(maKM);
        if (km == null) return false;
        km.setSoLuongKhuyenMai(Math.max(0, km.getSoLuongKhuyenMai() - 1));
        return updateKhuyenMai(toKhuyenMaiDTO(km));
    }

    public int getTotalReturned(String maHD, String maLo) {
        return tongSoLuongDaTra(maHD, maLo);
    }

    public java.util.List<HoaDon> searchHoaDonByCustomerPhone(String soDienThoai) {
        java.util.List<HoaDon> result = new java.util.ArrayList<>();
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null
                    && hd.getKhachHang().getSoDienThoai().contains(soDienThoai)) {
                result.add(hd);
            }
        }
        return result;
    }

    public java.util.List<HoaDon> searchHoaDonByPhone(String soDienThoai) {
        return searchHoaDonByCustomerPhone(soDienThoai);
    }

    public Object getThongKeHoaDonHomNayCuaNhanVien(String maNhanVien) {
        LocalDate today = LocalDate.now();
        int soHoaDon = 0;
        double doanhThu = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNhanVien() != null && maNhanVien.equals(hd.getNhanVien().getMaNhanVien())
                    && today.equals(hd.getNgayLap())) {
                soHoaDon++;
                doanhThu += hd.getTongThanhToan();
            }
        }
        return new Object[] { soHoaDon, doanhThu };
    }

    public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (PhieuHuy ph : getAllPhieuHuy()) {
            if (ph.getNhanVien() != null && maNhanVien.equals(ph.getNhanVien().getMaNhanVien())
                    && today.equals(ph.getNgayLapPhieu())) {
                count++;
            }
        }
        return count;
    }

    public java.util.List<LoSanPham> getLotsExpired() {
        java.util.List<LoSanPham> result = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (LoSanPham lo : getAllLotsEntity()) {
            if (lo.getHanSuDung() != null && lo.getHanSuDung().isBefore(today)) result.add(lo);
        }
        return result;
    }

    public java.util.List<LoSanPham> getLotsExpiring() {
        java.util.List<LoSanPham> result = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(30);
        for (LoSanPham lo : getAllLotsEntity()) {
            if (lo.getHanSuDung() != null && !lo.getHanSuDung().isBefore(today) && !lo.getHanSuDung().isAfter(limit)) {
                result.add(lo);
            }
        }
        return result;
    }

    public java.util.Map<String, Integer> getExpiredLotCountByCategory() {
        java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
        for (LoSanPham lo : getLotsExpired()) {
            String loai = lo.getSanPham() != null && lo.getSanPham().getLoaiSanPham() != null
                    ? lo.getSanPham().getLoaiSanPham().name()
                    : "KHAC";
            result.put(loai, result.getOrDefault(loai, 0) + 1);
        }
        return result;
    }

    public java.util.List<PhieuTra> getAllPhieuTra() {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<PhieuTra> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof PhieuTraDTO dto) result.add(toPhieuTra(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public PhieuTra getPhieuTraByCode(String maPhieuTra) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_MA, maPhieuTra);
        if (r != null && r.isSuccess() && r.getData() instanceof PhieuTraDTO dto) return toPhieuTra(dto);
        return null;
    }

    public java.util.List<PhieuTra> searchPhieuTraByPhone(String sdt) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_SDT, sdt);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<PhieuTra> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof PhieuTraDTO dto) result.add(toPhieuTra(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<PhieuTra> searchPhieuTraByKeyword(String keyword) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_KEYWORD, keyword);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<PhieuTra> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof PhieuTraDTO dto) result.add(toPhieuTra(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public String taoMaPhieuTra() {
        Response r = sendReq(CommandType.PHIEUTRA_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) {
        Response r = sendReq(CommandType.PHIEUTRA_DEM_HOM_NAY_NV, maNhanVien);
            if (r != null && r.isSuccess() && r.getData() instanceof Number n) return n.intValue();
            return 0;
    }

    public int tongSoLuongDaTra(String maHD, String maLo) {
        Response r = sendReq(CommandType.PHIEUTRA_TONG_SO_LUONG_DA_TRA, new Object[] { maHD, maLo });
            if (r != null && r.isSuccess() && r.getData() instanceof Number n) return n.intValue();
            return 0;
    }

    public boolean daTraLoTrongHoaDon(String maHD, String maLo) {
        Response r = sendReq(CommandType.PHIEUTRA_DA_TRA_LO, new Object[] { maHD, maLo });
            if (r != null && r.isSuccess() && r.getData() instanceof Boolean b) return b;
            return false;
    }

    public boolean createPhieuTra(Object phieuTra, java.util.List<?> dsChiTiet) {
        Response r = sendReq(CommandType.PHIEUTRA_THEM, new Object[] { phieuTra, dsChiTiet });
            return r != null && r.isSuccess();
    }

    public ThongKeNhanVienDTO getThongKeNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay, int caLam) {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_THONG_KE, new Object[] { maNhanVien, tuNgay, denNgay, caLam });
        if (r != null && r.isSuccess() && r.getData() instanceof ThongKeNhanVienDTO dto) return dto;
        return null;
    }

    public java.util.List<String[]> getDanhSachNhanVienThongKe() {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_DANH_SACH, null);
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            for (Object o : raw) if (o instanceof String[] arr) rows.add(arr);
        }
        return rows;
    }

    public String taoMaPhieuNhap() {
        Response r = sendReq(CommandType.PHIEUNHAP_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createPhieuNhap(Object phieuNhap) {
        Object payload = phieuNhap instanceof PhieuNhap pn ? toPhieuNhapDTO(pn) : phieuNhap;
        Response r = sendReq(CommandType.PHIEUNHAP_THEM, payload);
            return r != null && r.isSuccess();
    }

    public String taoMaPhieuHuy() {
        Response r = sendReq(CommandType.PHIEUHUY_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createPhieuHuy(Object phieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_THEM, phieuHuy);
            return r != null && r.isSuccess();
    }

    public String timTaiKhoanQuenMatKhau(String maNV, String tenNV, String sdt, LocalDate ngaySinh) {
        Response r = sendReq(CommandType.TAIKHOAN_TIM_QUEN_MAT_KHAU, new Object[] { maNV, tenNV, sdt, ngaySinh });
        return r != null && r.isSuccess() && r.getData() != null ? r.getData().toString() : null;
    }

    public boolean datLaiMatKhau(String maTaiKhoan, String matKhauMoi) {
        Response r = sendReq(CommandType.TAIKHOAN_DAT_LAI_MAT_KHAU, new Object[] { maTaiKhoan, matKhauMoi });
        return r != null && r.isSuccess();
    }

    public boolean refreshCacheSanPham() {
        Response r = sendReq(CommandType.SANPHAM_REFRESH_CACHE, null);
        return r != null && r.isSuccess();
    }

    public boolean createSanPham(SanPhamDTO dto) {
        Response r = sendReq(CommandType.SANPHAM_THEM, dto);
        return r != null && r.isSuccess();
    }

    public boolean createSanPham(SanPham sp) {
        return createSanPham(toSanPhamDTO(sp));
    }

    public boolean updateSanPham(SanPhamDTO dto) {
        Response r = sendReq(CommandType.SANPHAM_CAP_NHAT, dto);
        return r != null && r.isSuccess();
    }

    public boolean updateSanPham(SanPham sp) {
        return updateSanPham(toSanPhamDTO(sp));
    }

    public boolean deleteSanPham(String maSanPham) {
        Response r = sendReq(CommandType.SANPHAM_XOA, maSanPham);
        return r != null && r.isSuccess();
    }

    public List<SanPham> getAllSanPhamEntity() {
        List<SanPham> result = new ArrayList<>();
        for (Object item : getAllSanPham()) if (item instanceof SanPhamDTO dto) result.add(toSanPham(dto));
        return result;
    }

    public SanPham getSanPhamEntityByCode(String maSanPham) {
        Object sp = getProductByCode(maSanPham);
        return sp instanceof SanPham entity ? entity : null;
    }

    public List<SanPham> searchSanPhamEntity(String tuKhoa) {
        Response r = sendReq(CommandType.SANPHAM_TIM_KIEM, tuKhoa);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<SanPham> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof SanPhamDTO dto) result.add(toSanPham(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public List<LoSanPham> getAllLotsEntity() {
        List<LoSanPham> result = new ArrayList<>();
        for (Object item : getAllLots()) if (item instanceof LoSanPham lo) result.add(lo);
        return result;
    }

    public LoSanPham getLotEntityByCode(String maLo) {
        Object lo = getLotByCode(maLo);
        return lo instanceof LoSanPham entity ? entity : null;
    }

    public List<LoSanPham> getLotsByProductEntity(String maSanPham) {
        List<LoSanPham> result = new ArrayList<>();
        for (Object item : getLotsByProduct(maSanPham)) if (item instanceof LoSanPham lo) result.add(lo);
        return result;
    }

    public List<LoSanPham> searchLoSanPham(String keyword) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        List<LoSanPham> result = new ArrayList<>();
        for (LoSanPham lo : getAllLotsEntity()) {
            String blob = lo.getMaLo().toLowerCase();
            if (lo.getSanPham() != null) {
                blob += " " + String.valueOf(lo.getSanPham().getMaSanPham()).toLowerCase();
                blob += " " + String.valueOf(lo.getSanPham().getTenSanPham()).toLowerCase();
            }
            if (blob.contains(kw)) result.add(lo);
        }
        return result;
    }

    public String taoMaQuyCach() {
        Response r = sendReq(CommandType.QUYCACH_TAO_MA, null);
        return r != null && r.isSuccess() && r.getData() != null ? r.getData().toString() : null;
    }

    public List<QuyCachDongGoi> getAllQuyCachDongGoi() {
        Response r = sendReq(CommandType.QUYCACH_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<QuyCachDongGoi> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof QuyCachDongGoiDTO dto) result.add(toQuyCachDongGoi(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public List<QuyCachDongGoi> getQuyCachByProduct(String maSanPham) {
        return getPackagingRulesByProduct(maSanPham);
    }

    public QuyCachDongGoi getQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh) {
        Response r = sendReq(CommandType.QUYCACH_LAY_THEO_SAN_PHAM_VA_DVT, new Object[] { maSanPham, maDonViTinh });
        if (r != null && r.isSuccess() && r.getData() instanceof QuyCachDongGoiDTO dto) return toQuyCachDongGoi(dto);
        return null;
    }

    public QuyCachDongGoi getQuyCachGocTheoSanPham(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_GOC_THEO_SAN_PHAM, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof QuyCachDongGoiDTO dto) return toQuyCachDongGoi(dto);
        return null;
    }

    public boolean createQuyCach(QuyCachDongGoi qc) {
        Response r = sendReq(CommandType.QUYCACH_THEM, toQuyCachDongGoiDTO(qc));
        return r != null && r.isSuccess();
    }

    public boolean updateQuyCach(QuyCachDongGoi qc) {
        Response r = sendReq(CommandType.QUYCACH_CAP_NHAT, toQuyCachDongGoiDTO(qc));
        return r != null && r.isSuccess();
    }

    public boolean deleteQuyCach(String maQuyCach) {
        Response r = sendReq(CommandType.QUYCACH_XOA, maQuyCach);
        return r != null && r.isSuccess();
    }

    public List<BangGia> getAllBangGiaEntity() {
        List<BangGia> result = new ArrayList<>();
        for (Object item : getAllBangGia()) if (item instanceof BangGiaDTO dto) result.add(toBangGia(dto));
        return result;
    }

    public List<ChiTietBangGia> getChiTietBangGiaEntity(String maBangGia) {
        List<ChiTietBangGia> result = new ArrayList<>();
        for (Object item : getChiTietBangGia(maBangGia)) if (item instanceof ChiTietBangGiaDTO dto) result.add(toChiTietBangGia(dto));
        return result;
    }

    public List<KhuyenMai> getAllKhuyenMaiEntity() {
        List<KhuyenMai> result = new ArrayList<>();
        for (Object item : getAllKhuyenMai()) if (item instanceof KhuyenMaiDTO dto) result.add(toKhuyenMai(dto));
        return result;
    }

    public KhuyenMai getKhuyenMaiEntityByCode(String maKM) {
        Object km = getKhuyenMaiByCode(maKM);
        return km instanceof KhuyenMaiDTO dto ? toKhuyenMai(dto) : null;
    }

    public List<ChiTietKhuyenMaiSanPham> getChiTietKhuyenMaiByMaKMEntity(String maKM) {
        List<ChiTietKhuyenMaiSanPham> result = new ArrayList<>();
        for (Object item : getChiTietKhuyenMaiByMaKM(maKM)) if (item instanceof ChiTietKhuyenMaiSanPhamDTO dto) result.add(toChiTietKhuyenMaiSanPham(dto));
        return result;
    }

    public List<HoaDon> getAllHoaDon() {
        Response r = sendReq(CommandType.HOADON_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<HoaDon> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof HoaDonDTO dto) result.add(toHoaDon(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public HoaDon getHoaDonEntityByCode(String maHD) {
        Object hd = getHoaDonByCode(maHD);
        return hd instanceof HoaDonDTO dto ? toHoaDon(dto) : null;
    }

    public List<PhieuTra> getAllPhieuTraEntity() {
        return getAllPhieuTra();
    }

    public PhieuTra getPhieuTraEntityByCode(String maPhieuTra) {
        return getPhieuTraByCode(maPhieuTra);
    }

    public List<ChiTietPhieuTra> getChiTietPhieuTraEntity(String maPhieuTra) {
        return getChiTietPhieuTraByMa(maPhieuTra);
    }

    public List<PhieuTra> searchPhieuTraByKeywordEntity(String keyword) {
        return searchPhieuTraByKeyword(keyword);
    }

    public List<PhieuHuy> getAllPhieuHuy() {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            List<PhieuHuy> result = new ArrayList<>();
            for (Object item : raw) if (item instanceof PhieuHuyDTO dto) result.add(toPhieuHuy(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<PhieuNhap> getPhieuNhapByNhaCungCap(String maNhaCungCap) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_THEO_NCC, maNhaCungCap);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<PhieuNhap> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof PhieuNhapDTO dto) result.add(toPhieuNhap(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<ChiTietPhieuNhapDTO> getChiTietPhieuNhap(String maPhieuNhap) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_CHI_TIET, maPhieuNhap);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<ChiTietPhieuNhapDTO> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof ChiTietPhieuNhapDTO dto) result.add(dto);
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<PhieuNhap> getAllPhieuNhapEntity() {
        java.util.List<PhieuNhap> result = new java.util.ArrayList<>();
        for (Object item : getAllPhieuNhap()) if (item instanceof PhieuNhapDTO dto) result.add(toPhieuNhap(dto));
        return result;
    }

    public java.util.List<PhieuNhap> searchPhieuNhap(String keyword, java.util.Date tuNgay, java.util.Date denNgay) {
        java.util.List<PhieuNhap> result = new java.util.ArrayList<>();
        for (PhieuNhap pn : getAllPhieuNhapEntity()) {
            boolean matchKeyword = keyword == null || keyword.isBlank()
                    || pn.getMaPhieuNhap().toLowerCase().contains(keyword.toLowerCase())
                    || (pn.getNhaCungCap() != null && pn.getNhaCungCap().getTenNhaCungCap() != null
                    && pn.getNhaCungCap().getTenNhaCungCap().toLowerCase().contains(keyword.toLowerCase()));
            LocalDate ngay = pn.getNgayNhap();
            boolean matchDate = true;
            if (tuNgay != null && ngay != null) matchDate &= !ngay.isBefore(tuNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            if (denNgay != null && ngay != null) matchDate &= !ngay.isAfter(denNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            if (matchKeyword && matchDate) result.add(pn);
        }
        return result;
    }

    public List<String> getDanhSachLoaiSanPham() {
        List<String> result = new ArrayList<>();
        for (LoaiSanPham loai : LoaiSanPham.values()) result.add(loai.name());
        return result;
    }

    public List<Object[]> getSanPhamTonKhoThap(int nguong, String loaiSP) {
        java.util.Map<String, Object[]> map = new java.util.LinkedHashMap<>();
        for (LoSanPham lo : getAllLotsEntity()) {
            if (lo.getSanPham() == null) continue;
            SanPham product = getSanPhamEntityByCode(lo.getSanPham().getMaSanPham());
            if (product == null) product = lo.getSanPham();
            if (loaiSP != null && product.getLoaiSanPham() != null && !loaiSP.equals(product.getLoaiSanPham().name())) continue;
            if (lo.getSoLuongTon() >= nguong) continue;
            final SanPham spFinal = product;
            Object[] row = map.computeIfAbsent(spFinal.getMaSanPham(), k -> new Object[] {
                    spFinal.getMaSanPham(), spFinal.getTenSanPham(),
                    spFinal.getLoaiSanPham() != null ? spFinal.getLoaiSanPham().name() : null,
                    0, spFinal.getGiaNhap(), null, suggestSupplierNameForProduct(spFinal.getMaSanPham())
            });
            row[3] = ((Integer) row[3]) + lo.getSoLuongTon();
        }
        return new ArrayList<>(map.values());
    }

    public java.util.List<Object[]> getLoSapHetHan(int soNgay, String loaiSP) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusDays(Math.max(0, soNgay));
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        for (LoSanPham lo : getAllLotsEntity()) {
            if (lo.getHanSuDung() == null || lo.getHanSuDung().isBefore(now) || lo.getHanSuDung().isAfter(end)) continue;
            if (loaiSP != null && !loaiSP.isBlank()) {
                String loai = lo.getSanPham() != null && lo.getSanPham().getLoaiSanPham() != null ? lo.getSanPham().getLoaiSanPham().name() : "";
                if (!loaiSP.equals(loai)) continue;
            }
            String tenSp = lo.getSanPham() != null ? lo.getSanPham().getTenSanPham() : "N/A";
            String maSp = lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : "";
            String loai = lo.getSanPham() != null && lo.getSanPham().getLoaiSanPham() != null ? lo.getSanPham().getLoaiSanPham().name() : "KHAC";
            double giaBan = lo.getSanPham() != null ? lo.getSanPham().getGiaBan() : 0d;
            result.add(new Object[] { lo.getMaLo(), tenSp, loai, lo.getHanSuDung(), lo.getSoLuongTon(), giaBan, maSp });
        }
        return result;
    }

    public double getTrungBinhBanNgayTheoLo(String maLo, int soNgay) {
        if (soNgay <= 0 || maLo == null) return 0;
        LocalDate from = LocalDate.now().minusDays(soNgay);
        double sold = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() == null || hd.getNgayLap().isBefore(from)) continue;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (ct.getLoSanPham() != null && maLo.equals(ct.getLoSanPham().getMaLo())) sold += ct.getSoLuong();
            }
        }
        return sold / soNgay;
    }

    public double getDoanhThuTheoThang(int thang, int nam) {
        double total = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() != null && hd.getNgayLap().getMonthValue() == thang && hd.getNgayLap().getYear() == nam) {
                total += hd.getTongThanhToan();
            }
        }
        return total;
    }

    public double tinhTongTienNhapTheoThang(int thang, int nam) {
        double total = 0;
        for (PhieuNhap pn : getAllPhieuNhapEntity()) {
            if (pn.getNgayNhap() != null && pn.getNgayNhap().getMonthValue() == thang && pn.getNgayNhap().getYear() == nam) {
                total += pn.getTongTien();
            }
        }
        return total;
    }

    public double tinhTongTienTraTheoThang(int thang, int nam) {
        double total = 0;
        for (PhieuTra pt : getAllPhieuTra()) {
            if (pt.getNgayLap() != null && pt.getNgayLap().getMonthValue() == thang && pt.getNgayLap().getYear() == nam) {
                total += pt.getTongTienHoan();
            }
        }
        return total;
    }

    public double tinhTongTienHuyTheoThang(int thang, int nam) {
        double total = 0;
        for (PhieuHuy ph : getAllPhieuHuy()) {
            if (ph.getNgayLapPhieu() != null && ph.getNgayLapPhieu().getMonthValue() == thang && ph.getNgayLapPhieu().getYear() == nam) {
                total += ph.getTongTien();
            }
        }
        return total;
    }

    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        return getDoanhThuTheoThang(thang, nam) - tinhTongTienNhapTheoThang(thang, nam)
                - tinhTongTienTraTheoThang(thang, nam) - tinhTongTienHuyTheoThang(thang, nam);
    }

    public int demKhachHangMoiTheoThang(int thang, int nam) {
        return 0;
    }

    public int demPhieuTraChuaDuyet() {
        int count = 0;
        for (PhieuTra pt : getAllPhieuTra()) if (!pt.isTrangThai()) count++;
        return count;
    }

    public int demPhieuHuyChuaDuyet() {
        int count = 0;
        for (PhieuHuy ph : getAllPhieuHuy()) if (!ph.isTrangThai()) count++;
        return count;
    }

    public PhieuHuy getPhieuHuyByCode(String maPhieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA, maPhieuHuy);
        if (r != null && r.isSuccess() && r.getData() instanceof PhieuHuyDTO dto) return toPhieuHuy(dto);
        return null;
    }

    public java.util.List<ChiTietPhieuHuy> getChiTietPhieuHuy(String maPhieuHuy) {
        PhieuHuy ph = getPhieuHuyByCode(maPhieuHuy);
        if (ph != null && ph.getChiTietPhieuHuyList() != null) return ph.getChiTietPhieuHuyList();
        return java.util.Collections.emptyList();
    }

    public boolean capNhatTrangThaiChiTietPhieuHuy(String maPhieuHuy, String maLo, int trangThai) {
        PhieuHuy ph = getPhieuHuyByCode(maPhieuHuy);
        if (ph == null || ph.getChiTietPhieuHuyList() == null) return false;
        boolean changed = false;
        for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
            if (ct.getLoSanPham() != null && maLo.equals(ct.getLoSanPham().getMaLo())) {
                ct.setTrangThai(trangThai);
                changed = true;
            }
        }
        return changed;
    }

    public boolean kiemTraTrangThaiPhieuHuy(String maPhieuHuy) {
        for (ChiTietPhieuHuy ct : getChiTietPhieuHuy(maPhieuHuy)) {
            if (ct.getTrangThai() == ChiTietPhieuHuy.CHO_DUYET) return false;
        }
        return true;
    }

    public boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy) {
        PhieuHuy ph = getPhieuHuyByCode(maPhieuHuy);
        if (ph == null) return false;
        ph.setTrangThai(true);
        return true;
    }

    public java.util.List<Object[]> getThongKeTheoLoaiSanPham(int nam) {
        java.util.Map<String, Object[]> map = new java.util.LinkedHashMap<>();
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() == null || hd.getNgayLap().getYear() != nam) continue;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (ct.getLoSanPham() == null || ct.getLoSanPham().getSanPham() == null) continue;
                String key = ct.getLoSanPham().getSanPham().getLoaiSanPham() != null ? ct.getLoSanPham().getSanPham().getLoaiSanPham().name() : "KHAC";
                Object[] row = map.computeIfAbsent(key, k -> new Object[] { k, 0, 0d, 0d });
                row[1] = ((Integer) row[1]) + ct.getSoLuong();
                row[2] = ((Double) row[2]) + ct.getThanhTien();
            }
        }
        return new java.util.ArrayList<>(map.values());
    }

    public java.util.Map<String, Double> getDoanhThuNamTruocTheoLoai(int nam) {
        java.util.Map<String, Double> map = new java.util.LinkedHashMap<>();
        for (Object[] row : getThongKeTheoLoaiSanPham(nam - 1)) map.put((String) row[0], (Double) row[2]);
        return map;
    }

    public double getTongDoanhThuTheoNam(int nam) {
        double total = 0;
        for (Object[] row : getThongKeTheoLoaiSanPham(nam)) total += (Double) row[2];
        return total;
    }

    public double getTrungBinhBanNgay(String maSP, int soNgay) {
        if (soNgay <= 0) return 0;
        LocalDate from = LocalDate.now().minusDays(soNgay);
        double sold = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() == null || hd.getNgayLap().isBefore(from)) continue;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null
                        && maSP.equals(ct.getLoSanPham().getSanPham().getMaSanPham())) {
                    sold += ct.getSoLuong();
                }
            }
        }
        return sold / soNgay;
    }

    public Object[] getNhaCungCapGoiY(int nguong) {
        java.util.Map<String, Integer> countMap = new java.util.LinkedHashMap<>();
        for (Object[] row : getSanPhamTonKhoThap(nguong, null)) {
            String tenNcc = row[6] == null ? "Không rõ" : row[6].toString();
            countMap.put(tenNcc, countMap.getOrDefault(tenNcc, 0) + 1);
        }
        String best = "Không có dữ liệu";
        int count = 0;
        for (java.util.Map.Entry<String, Integer> e : countMap.entrySet()) {
            if (e.getValue() > count) { best = e.getKey(); count = e.getValue(); }
        }
        return new Object[] { best, count };
    }

    public List<ThongKeTaiChinhDTO> getThongKeTaiChinhTheoNgay(java.util.Date tu, java.util.Date den, String maLoaiSP) {
        return thongKeTaiChinhTheoNgay(tu.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                den.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
    }

    public List<ThongKeTaiChinhDTO> getThongKeTaiChinhTheoThang(int nam, String maLoaiSP) {
        java.util.Map<Integer, ThongKeTaiChinhDTO> map = new java.util.LinkedHashMap<>();
        for (int thang = 1; thang <= 12; thang++) map.put(thang, new ThongKeTaiChinhDTO("T" + thang, 0, 0, 0, 0));
        for (ThongKeTaiChinhDTO item : thongKeTaiChinhTheoNgay(LocalDate.of(nam, 1, 1), LocalDate.of(nam, 12, 31))) {
            LocalDate d = parseDate(item.thoiGian);
            if (d == null) {
                continue;
            }
            ThongKeTaiChinhDTO bucket = map.get(d.getMonthValue());
            bucket.banHang += item.banHang;
            bucket.nhapHang += item.nhapHang;
            bucket.traHang += item.traHang;
            bucket.huyHang += item.huyHang;
        }
        return new ArrayList<>(map.values());
    }

    public List<ThongKeTaiChinhDTO> getThongKeTaiChinhTheoNam(int namS, int namE, String maLoaiSP) {
        java.util.Map<Integer, ThongKeTaiChinhDTO> map = new java.util.LinkedHashMap<>();
        for (int nam = namS; nam <= namE; nam++) map.put(nam, new ThongKeTaiChinhDTO(String.valueOf(nam), 0, 0, 0, 0));
        for (ThongKeTaiChinhDTO item : thongKeTaiChinhTheoNgay(LocalDate.of(namS, 1, 1), LocalDate.of(namE, 12, 31))) {
            LocalDate d = parseDate(item.thoiGian);
            if (d == null) {
                continue;
            }
            ThongKeTaiChinhDTO bucket = map.get(d.getYear());
            if (bucket != null) {
                bucket.banHang += item.banHang;
                bucket.nhapHang += item.nhapHang;
                bucket.traHang += item.traHang;
                bucket.huyHang += item.huyHang;
            }
        }
        return new ArrayList<>(map.values());
    }

    public List<Object[]> getTopSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int topN) {
        java.util.Map<String, Object[]> map = new java.util.HashMap<>();
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() == null || hd.getNgayLap().isBefore(tuNgay) || hd.getNgayLap().isAfter(denNgay)) continue;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (ct.getLoSanPham() == null || ct.getLoSanPham().getSanPham() == null) continue;
                SanPham sp = ct.getLoSanPham().getSanPham();
                Object[] row = map.computeIfAbsent(sp.getMaSanPham(), k -> new Object[] {
                        sp.getMaSanPham(), sp.getTenSanPham(),
                        sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "",
                        0d, 0d
                });
                row[3] = ((Double) row[3]) + ct.getSoLuong();
                row[4] = ((Double) row[4]) + ct.getThanhTien();
            }
        }
        List<Object[]> list = new ArrayList<>(map.values());
        list.sort((a, b) -> Double.compare((Double) b[3], (Double) a[3]));
        return list.size() > topN ? new ArrayList<>(list.subList(0, topN)) : list;
    }

    public double getTongDoanhThuTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        double total = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() != null && !hd.getNgayLap().isBefore(tuNgay) && !hd.getNgayLap().isAfter(denNgay)) {
                total += hd.getTongThanhToan();
            }
        }
        return total;
    }

    public double getTongDoanhThuKyTruoc(LocalDate tuNgay, LocalDate denNgay) {
        return getTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);
    }

    public double getSoLuongBanKyTruoc(String maSP, LocalDate tuNgay, LocalDate denNgay) {
        double total = 0;
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() == null || hd.getNgayLap().isBefore(tuNgay) || hd.getNgayLap().isAfter(denNgay)) continue;
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                if (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null
                        && maSP.equals(ct.getLoSanPham().getSanPham().getMaSanPham())) {
                    total += ct.getSoLuong();
                }
            }
        }
        return total;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsByProductWithService(String maSanPham) {
        return getLotsByProduct(maSanPham);
    }

    public Object getProductByCodeWithService(String maSanPham) {
        return getProductByCode(maSanPham);
    }

    public NhanVien getNhanVienByCode(String maNhanVien) {
        Response r = sendReq(CommandType.NHANVIEN_LAY_THEO_MA, maNhanVien);
        if (r != null && r.isSuccess() && r.getData() instanceof NhanVienDTO dto) {
            return toNhanVien(dto);
        }
        return null;
    }

    public boolean updateNhanVienEntity(NhanVien nhanVien) {
        if (nhanVien == null) return false;
        Response r = sendReq(CommandType.NHANVIEN_CAP_NHAT, toNhanVienDTO(nhanVien));
        return r != null && r.isSuccess();
    }

    public boolean changePassword(String maTaiKhoan, String matKhauCu, String matKhauMoi) {
        Response r = sendReq(CommandType.TAIKHOAN_DOI_MAT_KHAU, new Object[] { maTaiKhoan, matKhauCu, matKhauMoi });
        return r != null && r.isSuccess();
    }

    public java.util.List<ChiTietPhieuTra> getChiTietPhieuTraByMa(String maPhieuTra) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_CHI_TIET, maPhieuTra);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> raw) {
            java.util.List<ChiTietPhieuTra> result = new java.util.ArrayList<>();
            for (Object item : raw) if (item instanceof ChiTietPhieuTraDTO dto) result.add(toChiTietPhieuTra(dto));
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public String capNhatTrangThaiPhieuTraGiaoDich(String maPT, String maHD, String maLo, String maDVT, NhanVien nv, int trangThaiMoi) {
        Response r = sendReq(CommandType.PHIEUTRA_CAP_NHAT_TRANG_THAI_GIAO_DICH,
                new Object[] { maPT, maHD, maLo, maDVT, nv, trangThaiMoi });
        return r != null && r.isSuccess() && r.getData() != null ? r.getData().toString() : null;
    }

    public boolean capNhatTrangThaiPhieuTra(String maPT, boolean daDuyet) {
        Response r = sendReq(CommandType.PHIEUTRA_CAP_NHAT_TRANG_THAI, new Object[] { maPT, daDuyet });
        return r != null && r.isSuccess();
    }

    private java.util.List<LoSanPham> mapLotList(java.util.List<?> raw) {
        java.util.List<LoSanPham> result = new java.util.ArrayList<>();
        for (Object item : raw) {
            if (item instanceof LoSanPhamDTO dto) result.add(toLoSanPham(dto));
        }
        return result;
    }

    private SanPham toSanPham(SanPhamDTO dto) {
        SanPham sp = new SanPham();
        sp.setMaSanPham(dto.getMaSanPham());
        sp.setTenSanPham(dto.getTenSanPham());
        sp.setSoDangKy(dto.getSoDangKy());
        sp.setGiaBan(dto.getGiaBan());
        if (dto.getGiaNhap() > 0) sp.setGiaNhap(dto.getGiaNhap());
        sp.setHinhAnh(dto.getHinhAnh());
        sp.setKeBanSanPham(dto.getKeBanSanPham());
        sp.setHoatDong(dto.isHoatDong());
        try {
            sp.setLoaiSanPham(LoaiSanPham.valueOf(dto.getLoaiSanPham()));
        } catch (Exception ignored) {}
        try {
            sp.setDuongDung(DuongDung.valueOf(dto.getDuongDung()));
        } catch (Exception ignored) {}
        return sp;
    }

    private LoSanPham toLoSanPham(LoSanPhamDTO dto) {
        LoSanPham lo = new LoSanPham();
        lo.setMaLo(dto.getMaLo());
        if (dto.getHanSuDung() != null && !dto.getHanSuDung().isBlank()) {
            LocalDate parsed = parseDate(dto.getHanSuDung());
            if (parsed != null) {
                lo.setHanSuDung(parsed);
            }
        }
        lo.setSoLuongTon(dto.getSoLuongTon());
        if (dto.getMaSanPham() != null && !dto.getMaSanPham().isBlank()) {
            lo.setSanPham(new SanPham(dto.getMaSanPham()));
        }
        return lo;
    }

    private NhaCungCap toNhaCungCap(NhaCungCapDTO dto) {
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNhaCungCap(dto.getMaNhaCungCap());
        ncc.setTenNhaCungCap(dto.getTenNhaCungCap());
        ncc.setSoDienThoai(dto.getSoDienThoai());
        ncc.setDiaChi(dto.getDiaChi());
        ncc.setEmail(dto.getEmail());
        ncc.setHoatDong(dto.isHoatDong());
        return ncc;
    }

    private KhachHang toKhachHang(KhachHangDTO dto) {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(dto.getMaKhachHang());
        kh.setTenKhachHang(dto.getTenKhachHang());
        kh.setSoDienThoai(dto.getSoDienThoai());
        kh.setGioiTinh("Nam".equalsIgnoreCase(dto.getGioiTinh()));
        if (dto.getNgaySinh() != null && !dto.getNgaySinh().isBlank()) {
            LocalDate parsed = parseDate(dto.getNgaySinh());
            if (parsed != null) {
                kh.setNgaySinh(parsed);
            }
        }
        return kh;
    }

    private DonViTinh toDonViTinh(DonViTinhDTO dto) {
        return new DonViTinh(dto.getMaDonViTinh(), dto.getTenDonViTinh());
    }

    private DonViTinhDTO toDonViTinhDTO(DonViTinh dvt) {
        DonViTinhDTO dto = new DonViTinhDTO();
        dto.setMaDonViTinh(dvt.getMaDonViTinh());
        dto.setTenDonViTinh(dvt.getTenDonViTinh());
        return dto;
    }

    private NhaCungCapDTO toNhaCungCapDTO(NhaCungCap ncc) {
        NhaCungCapDTO dto = new NhaCungCapDTO();
        dto.setMaNhaCungCap(ncc.getMaNhaCungCap());
        dto.setTenNhaCungCap(ncc.getTenNhaCungCap());
        dto.setSoDienThoai(ncc.getSoDienThoai());
        dto.setDiaChi(ncc.getDiaChi());
        dto.setEmail(ncc.getEmail());
        dto.setHoatDong(ncc.isHoatDong());
        return dto;
    }

    private KhachHangDTO toKhachHangDTO(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(kh.getMaKhachHang());
        dto.setTenKhachHang(kh.getTenKhachHang());
        dto.setSoDienThoai(kh.getSoDienThoai());
        dto.setGioiTinh(kh.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(kh.getNgaySinh() != null ? kh.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        return dto;
    }

    private BangGiaDTO toBangGiaDTO(BangGia bg) {
        BangGiaDTO dto = new BangGiaDTO();
        dto.setMaBangGia(bg.getMaBangGia());
        dto.setTenBangGia(bg.getTenBangGia());
        dto.setNgayApDung(bg.getNgayApDung());
        dto.setHoatDong(bg.isHoatDong());
        if (bg.getNhanVien() != null) dto.setMaNhanVien(bg.getNhanVien().getMaNhanVien());
        return dto;
    }

    private QuyCachDongGoi toQuyCachDongGoi(QuyCachDongGoiDTO dto) {
        DonViTinh dvt = new DonViTinh(dto.getDonViTinh().getMaDonViTinh(), dto.getDonViTinh().getTenDonViTinh());
        QuyCachDongGoi qc = new QuyCachDongGoi(dto.getMaQuyCach(), dvt, toSanPham(dto.getSanPham()),
                dto.getHeSoQuyDoi(), dto.getTiLeGiam(), dto.isDonViGoc(), dto.isTrangThai());
        return qc;
    }

    private QuyCachDongGoiDTO toQuyCachDongGoiDTO(QuyCachDongGoi qc) {
        QuyCachDongGoiDTO dto = new QuyCachDongGoiDTO();
        dto.setMaQuyCach(qc.getMaQuyCach());
        DonViTinhDTO dvt = new DonViTinhDTO();
        dvt.setMaDonViTinh(qc.getDonViTinh().getMaDonViTinh());
        dvt.setTenDonViTinh(qc.getDonViTinh().getTenDonViTinh());
        dto.setDonViTinh(dvt);
        dto.setSanPham(toSanPhamDTO(qc.getSanPham()));
        dto.setHeSoQuyDoi(qc.getHeSoQuyDoi());
        dto.setTiLeGiam(qc.getTiLeGiam());
        dto.setDonViGoc(qc.isDonViGoc());
        dto.setTrangThai(qc.isTrangThai());
        return dto;
    }

    private SanPhamDTO toSanPhamDTO(SanPham sp) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setSoDangKy(sp.getSoDangKy());
        dto.setGiaBan(sp.getGiaBan());
        dto.setGiaNhap(sp.getGiaNhap());
        dto.setHinhAnh(sp.getHinhAnh());
        dto.setKeBanSanPham(sp.getKeBanSanPham());
        dto.setHoatDong(sp.isHoatDong());
        dto.setLoaiSanPham(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
        dto.setDuongDung(sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
        return dto;
    }

    private NhanVien toNhanVien(NhanVienDTO dto) {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(dto.getMaNhanVien());
        nv.setTenNhanVien(dto.getTenNhanVien());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nv.setDiaChi(dto.getDiaChi());
        nv.setQuanLy("Quản lý".equalsIgnoreCase(dto.getVaiTro()) || "Quan ly".equalsIgnoreCase(dto.getVaiTro()));
        nv.setTrangThai(dto.getTrangThai() == null || !dto.getTrangThai().toLowerCase().contains("ngh"));
        if (dto.getNgaySinh() != null && !dto.getNgaySinh().isBlank()) {
            LocalDate parsed = parseDate(dto.getNgaySinh());
            if (parsed != null) {
                nv.setNgaySinh(parsed);
            }
        }
        return nv;
    }

    private NhanVienDTO toNhanVienDTO(NhanVien nv) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(nv.getMaNhanVien());
        dto.setTenNhanVien(nv.getTenNhanVien());
        dto.setGioiTinh(nv.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(nv.getNgaySinh() != null ? nv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setDiaChi(nv.getDiaChi());
        dto.setVaiTro(nv.isQuanLy() ? "Quản lý" : "Nhân viên");
        dto.setCaLam(String.valueOf(nv.getCaLam()));
        dto.setTrangThai(nv.isTrangThai() ? "Đang làm" : "Nghỉ việc");
        return dto;
    }

    private PhieuTra toPhieuTra(PhieuTraDTO dto) {
        PhieuTra pt = new PhieuTra();
        pt.setMaPhieuTra(dto.getMaPhieuTra());
        if (dto.getMaKhachHang() != null) {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang(dto.getMaKhachHang());
            kh.setTenKhachHang(dto.getTenKhachHang());
            kh.setSoDienThoai(dto.getSoDienThoai());
            pt.setKhachHang(kh);
        }
        if (dto.getMaNhanVien() != null) {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(dto.getMaNhanVien());
            nv.setTenNhanVien(dto.getTenNhanVien());
            pt.setNhanVien(nv);
        }
        if (dto.getNgayLap() != null && !dto.getNgayLap().isBlank()) {
            LocalDate parsed = parseDate(dto.getNgayLap());
            if (parsed != null) {
                pt.setNgayLap(parsed);
            }
        }
        pt.setTrangThai(dto.isTrangThai());
        try {
            java.lang.reflect.Field field = PhieuTra.class.getDeclaredField("tongTienHoan");
            field.setAccessible(true);
            field.set(pt, dto.getTongTienHoan());
        } catch (Exception ignored) {}
        return pt;
    }

    private ChiTietPhieuTra toChiTietPhieuTra(ChiTietPhieuTraDTO dto) {
        ChiTietPhieuTra ct = new ChiTietPhieuTra();
        ChiTietPhieuTra.Id id = new ChiTietPhieuTra.Id(dto.getMaPhieuTra(), dto.getMaHoaDon(), dto.getMaLo());
        ct.setId(id);
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(dto.getMaHoaDon());
        LoSanPham lo = new LoSanPham(dto.getMaLo());
        SanPham sp = new SanPham();
        sp.setMaSanPham("SP-000001");
        if (dto.getTenSanPham() != null && !dto.getTenSanPham().isBlank()) {
            sp.setTenSanPham(dto.getTenSanPham());
        } else {
            sp.setTenSanPham("N/A");
        }
        lo.setSanPham(sp);
        ChiTietHoaDon cthd = new ChiTietHoaDon();
        cthd.setHoaDon(hoaDon);
        cthd.setLoSanPham(lo);
        cthd.setSoLuong(Math.max(1, dto.getSoLuong()));
        DonViTinh dvt = null;
        if (dto.getMaDonViTinh() != null) {
            dvt = new DonViTinh(dto.getMaDonViTinh());
            if (dto.getTenDonViTinh() != null && !dto.getTenDonViTinh().isBlank()) {
                dvt.setTenDonViTinh(dto.getTenDonViTinh());
            } else {
                dvt.setTenDonViTinh("Đơn vị");
            }
            cthd.setDonViTinh(dvt);
            ct.setDonViTinh(dvt);
        }
        cthd.setGiaBan(0);
        ct.setChiTietHoaDon(cthd);
        ct.setLyDoChiTiet(dto.getLyDoChiTiet());
        ct.setSoLuong(Math.max(1, dto.getSoLuong()));
        ct.setThanhTienHoan(dto.getThanhTienHoan());
        ct.setTrangThai(dto.getTrangThai());
        return ct;
    }

    private PhieuNhapDTO toPhieuNhapDTO(PhieuNhap phieuNhap) {
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setMaPhieuNhap(phieuNhap.getMaPhieuNhap());
        dto.setNgayNhap(phieuNhap.getNgayNhap() != null ? phieuNhap.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        if (phieuNhap.getNhaCungCap() != null) {
            dto.setMaNhaCungCap(phieuNhap.getNhaCungCap().getMaNhaCungCap());
            dto.setTenNhaCungCap(phieuNhap.getNhaCungCap().getTenNhaCungCap());
        }
        if (phieuNhap.getNhanVien() != null) {
            dto.setMaNhanVien(phieuNhap.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(phieuNhap.getNhanVien().getTenNhanVien());
        }
        dto.setTongTien(phieuNhap.getTongTien());
        java.util.List<ChiTietPhieuNhapDTO> chiTietList = new java.util.ArrayList<>();
        if (phieuNhap.getChiTietPhieuNhapList() != null) {
            for (ChiTietPhieuNhap ct : phieuNhap.getChiTietPhieuNhapList()) {
                ChiTietPhieuNhapDTO ctDto = new ChiTietPhieuNhapDTO();
                ctDto.setMaPhieuNhap(phieuNhap.getMaPhieuNhap());
                if (ct.getLoSanPham() != null) {
                    ctDto.setMaLo(ct.getLoSanPham().getMaLo());
                    ctDto.setHanSuDung(ct.getLoSanPham().getHanSuDung() != null ? ct.getLoSanPham().getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
                    ctDto.setSoLuongTon(ct.getLoSanPham().getSoLuongTon());
                    if (ct.getLoSanPham().getSanPham() != null) {
                        ctDto.setMaSanPham(ct.getLoSanPham().getSanPham().getMaSanPham());
                        ctDto.setTenSanPham(ct.getLoSanPham().getSanPham().getTenSanPham());
                    }
                }
                if (ct.getDonViTinh() != null) {
                    ctDto.setMaDonViTinh(ct.getDonViTinh().getMaDonViTinh());
                    ctDto.setTenDonViTinh(ct.getDonViTinh().getTenDonViTinh());
                }
                ctDto.setSoLuongNhap(ct.getSoLuongNhap());
                ctDto.setDonGiaNhap(ct.getDonGiaNhap());
                ctDto.setThanhTien(ct.getThanhTien());
                chiTietList.add(ctDto);
            }
        }
        dto.setSoDongChiTiet(chiTietList.size());
        dto.setChiTietList(chiTietList);
        return dto;
    }

    private BangGia toBangGia(BangGiaDTO dto) {
        BangGia bg = new BangGia();
        bg.setMaBangGia(dto.getMaBangGia());
        bg.setNhanVien(new NhanVien(dto.getMaNhanVien(), dto.getMaNhanVien(), false, 1));
        bg.setTenBangGia(dto.getTenBangGia());
        bg.setNgayApDung(dto.getNgayApDung());
        bg.setHoatDong(dto.isHoatDong());
        return bg;
    }

    private ChiTietBangGia toChiTietBangGia(ChiTietBangGiaDTO dto) {
        return new ChiTietBangGia(new BangGia(dto.getMaBangGia()), dto.getGiaTu(), dto.getGiaDen(), dto.getTiLe());
    }

    private KhuyenMai toKhuyenMai(KhuyenMaiDTO dto) {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(dto.getMaKM());
        km.setTenKM(dto.getTenKM());
        km.setNgayBatDau(parseDate(dto.getNgayBatDau()));
        km.setNgayKetThuc(parseDate(dto.getNgayKetThuc()));
        km.setTrangThai(dto.isTrangThai());
        km.setKhuyenMaiHoaDon(dto.isKhuyenMaiHoaDon());
        km.setHinhThuc(HinhThucKM.valueOf(dto.getHinhThuc()));
        km.setGiaTri(dto.getGiaTri());
        km.setDieuKienApDungHoaDon(dto.getDieuKienApDungHoaDon());
        km.setSoLuongKhuyenMai(dto.getSoLuongKhuyenMai());
        return km;
    }

    private KhuyenMaiDTO toKhuyenMaiDTO(KhuyenMai km) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setMaKM(km.getMaKM());
        dto.setTenKM(km.getTenKM());
        dto.setNgayBatDau(km.getNgayBatDau().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dto.setNgayKetThuc(km.getNgayKetThuc().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dto.setTrangThai(km.isTrangThai());
        dto.setKhuyenMaiHoaDon(km.isKhuyenMaiHoaDon());
        dto.setHinhThuc(km.getHinhThuc().name());
        dto.setGiaTri(km.getGiaTri());
        dto.setDieuKienApDungHoaDon(km.getDieuKienApDungHoaDon());
        dto.setSoLuongKhuyenMai(km.getSoLuongKhuyenMai());
        dto.setDangHoatDong(km.isDangHoatDong());
        return dto;
    }

    private ChiTietKhuyenMaiSanPham toChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPhamDTO dto) {
        return new ChiTietKhuyenMaiSanPham(new SanPham(dto.getMaSanPham()), new KhuyenMai(dto.getMaKM()));
    }

    private HoaDon toHoaDon(HoaDonDTO dto) {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(dto.getMaHoaDon());
        NhanVien nv = new NhanVien("NV-20240101-0001", dto.getTenNhanVien() != null ? dto.getTenNhanVien() : "N/A", false, 1);
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang("KH-20240101-0001");
        kh.setTenKhachHang(dto.getTenKhachHang() != null ? dto.getTenKhachHang() : "Vãng lai");
        kh.setSoDienThoai(dto.getSdtKhachHang() != null && !dto.getSdtKhachHang().isBlank() ? dto.getSdtKhachHang() : "0000000000");
        kh.setNgaySinh(LocalDate.of(1990, 1, 1));
        hd.setNhanVien(nv);
        hd.setKhachHang(kh);
        LocalDate parsedNgayLap = parseDate(dto.getNgayLap());
        if (parsedNgayLap != null) {
            hd.setNgayLap(parsedNgayLap);
        }
        List<ChiTietHoaDon> ds = new ArrayList<>();
        if (dto.getChiTietList() != null) {
            for (ChiTietHoaDonDTO ctDto : dto.getChiTietList()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                LoSanPham lo = new LoSanPham(ctDto.getMaLo());
                SanPham sp = new SanPham("SP-000001");
                sp.setTenSanPham(ctDto.getTenSanPham());
                lo.setSanPham(sp);
                ct.setLoSanPham(lo);
                ct.setGiaBan(ctDto.getDonGia());
                ct.setSoLuong(ctDto.getSoLuong());
                ds.add(ct);
            }
        }
        hd.setDanhSachChiTiet(ds);
        return hd;
    }

    private ChiTietHoaDon toChiTietHoaDon(ChiTietHoaDonDTO dto) {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        LoSanPham lo = new LoSanPham(dto.getMaLo());
        SanPham sp = new SanPham("SP-000001");
        sp.setTenSanPham(dto.getTenSanPham());
        lo.setSanPham(sp);
        ct.setLoSanPham(lo);
        ct.setGiaBan(dto.getDonGia());
        ct.setSoLuong(dto.getSoLuong());
        return ct;
    }

    private PhieuHuy toPhieuHuy(PhieuHuyDTO dto) {
        PhieuHuy ph = new PhieuHuy();
        ph.setMaPhieuHuy(dto.getMaPhieuHuy());
        ph.setNgayLapPhieu(dto.getNgayLapPhieu());
        ph.setTrangThai(dto.isTrangThai());
        if (dto.getNhanVien() != null) {
            ph.setNhanVien(new NhanVien(dto.getNhanVien().getMaNhanVien(), dto.getNhanVien().getTenNhanVien(), false, 1));
        }
        if (dto.getChiTietPhieuHuyList() != null) {
            List<ChiTietPhieuHuy> ds = new ArrayList<>();
            for (ChiTietPhieuHuyDTO ctDto : dto.getChiTietPhieuHuyList()) {
                ds.add(toChiTietPhieuHuy(ctDto, ph));
            }
            ph.setChiTietPhieuHuyList(ds);
        }
        return ph;
    }

    private ChiTietPhieuHuy toChiTietPhieuHuy(ChiTietPhieuHuyDTO dto, PhieuHuy ph) {
        ChiTietPhieuHuy ct = new ChiTietPhieuHuy();
        ct.setPhieuHuy(ph);
        LoSanPham lo = dto.getLoSanPham() != null ? toLoSanPham(dto.getLoSanPham()) : new LoSanPham(dto.getMaLo());
        ct.setLoSanPham(lo);
        ct.setSoLuongHuy(Math.max(1, dto.getSoLuongHuy()));
        ct.setDonGiaNhap(dto.getDonGiaNhap() > 0 ? dto.getDonGiaNhap() : 1);
        ct.setLyDoChiTiet(dto.getLyDoChiTiet());
        if (dto.getDonViTinh() != null) ct.setDonViTinh(toDonViTinh(dto.getDonViTinh()));
        ct.setTrangThai(dto.getTrangThai() >= 1 && dto.getTrangThai() <= 3 ? dto.getTrangThai() : ChiTietPhieuHuy.CHO_DUYET);
        return ct;
    }

    private PhieuNhap toPhieuNhap(PhieuNhapDTO dto) {
        PhieuNhap pn = new PhieuNhap();
        pn.setMaPhieuNhap(dto.getMaPhieuNhap());
        if (dto.getNgayNhap() != null && !dto.getNgayNhap().isBlank()) {
            LocalDate parsed = parseDate(dto.getNgayNhap());
            if (parsed != null) {
                pn.setNgayNhap(parsed);
            }
        }
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNhaCungCap(dto.getMaNhaCungCap() != null ? dto.getMaNhaCungCap() : "NCC-20240101-0001");
        ncc.setTenNhaCungCap(dto.getTenNhaCungCap() != null ? dto.getTenNhaCungCap() : "N/A");
        ncc.setSoDienThoai("0000000000");
        pn.setNhaCungCap(ncc);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(dto.getMaNhanVien() != null ? dto.getMaNhanVien() : "NV-20240101-0001");
        nv.setTenNhanVien(dto.getTenNhanVien() != null ? dto.getTenNhanVien() : "N/A");
        pn.setNhanVien(nv);
        pn.setTongTien(dto.getTongTien());
        List<ChiTietPhieuNhap> ds = new ArrayList<>();
        if (dto.getChiTietList() != null) {
            for (ChiTietPhieuNhapDTO ctDto : dto.getChiTietList()) {
                ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                ct.setPhieuNhap(pn);
                LoSanPham lo = new LoSanPham(ctDto.getMaLo());
                lo.setSoLuongTon(ctDto.getSoLuongTon());
                if (ctDto.getHanSuDung() != null && !ctDto.getHanSuDung().isBlank()) {
                    LocalDate parsed = parseDate(ctDto.getHanSuDung());
                    if (parsed != null) {
                        lo.setHanSuDung(parsed);
                    }
                }
                SanPham sp = new SanPham();
                sp.setMaSanPham(ctDto.getMaSanPham() != null ? ctDto.getMaSanPham() : "SP-000001");
                sp.setTenSanPham(ctDto.getTenSanPham() != null ? ctDto.getTenSanPham() : "N/A");
                lo.setSanPham(sp);
                ct.setLoSanPham(lo);
                DonViTinh dvt = new DonViTinh();
                dvt.setMaDonViTinh(ctDto.getMaDonViTinh() != null ? ctDto.getMaDonViTinh() : "DVT-20240101-0001");
                dvt.setTenDonViTinh(ctDto.getTenDonViTinh() != null ? ctDto.getTenDonViTinh() : "Đơn vị");
                ct.setDonViTinh(dvt);
                ct.setSoLuongNhap(Math.max(1, ctDto.getSoLuongNhap()));
                ct.setDonGiaNhap(ctDto.getDonGiaNhap() > 0 ? ctDto.getDonGiaNhap() : 1);
                ds.add(ct);
            }
        }
        pn.setChiTietPhieuNhapList(ds);
        return pn;
    }

    private String suggestSupplierNameForProduct(String maSanPham) {
        for (Object obj : getAllPhieuNhap()) {
            if (obj instanceof PhieuNhapDTO pn && pn.getChiTietList() != null) {
                for (ChiTietPhieuNhapDTO ct : pn.getChiTietList()) {
                    if (maSanPham.equals(ct.getMaSanPham())) return pn.getTenNhaCungCap();
                }
            }
        }
        return "Không rõ";
    }

    private List<ThongKeTaiChinhDTO> thongKeTaiChinhTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        java.util.Map<LocalDate, ThongKeTaiChinhDTO> map = new java.util.LinkedHashMap<>();
        for (LocalDate d = tuNgay; !d.isAfter(denNgay); d = d.plusDays(1)) {
            map.put(d, new ThongKeTaiChinhDTO(d.toString(), 0, 0, 0, 0));
        }
        for (HoaDon hd : getAllHoaDon()) {
            if (hd.getNgayLap() != null && !hd.getNgayLap().isBefore(tuNgay) && !hd.getNgayLap().isAfter(denNgay)) {
                map.get(hd.getNgayLap()).banHang += hd.getTongThanhToan();
            }
        }
        for (Object obj : getAllPhieuNhap()) {
            if (obj instanceof PhieuNhapDTO pn && pn.getNgayNhap() != null) {
                LocalDate d = parseDate(pn.getNgayNhap());
                if (d != null && !d.isBefore(tuNgay) && !d.isAfter(denNgay)) {
                    map.get(d).nhapHang += pn.getTongTien();
                }
            }
        }
        for (PhieuTra pt : getAllPhieuTra()) {
            if (pt.getNgayLap() != null && !pt.getNgayLap().isBefore(tuNgay) && !pt.getNgayLap().isAfter(denNgay)) {
                map.get(pt.getNgayLap()).traHang += pt.getTongTienHoan();
            }
        }
        for (PhieuHuy ph : getAllPhieuHuy()) {
            if (ph.getNgayLapPhieu() != null && !ph.getNgayLapPhieu().isBefore(tuNgay) && !ph.getNgayLapPhieu().isAfter(denNgay)) {
                map.get(ph.getNgayLapPhieu()).huyHang += ph.getTongTien();
            }
        }
        return new ArrayList<>(map.values());
    }
}

