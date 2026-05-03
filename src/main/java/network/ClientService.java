package network;

import entity.TaiKhoan;
import java.util.List;

/**
 * High-level client service that wraps socket requests.
 */
public class ClientService {
    private final String host;
    private final int port;

    public ClientService() {
        this("localhost", 9090);
    }

    public ClientService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public TaiKhoan login(String username, String password) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Object[] creds = new Object[] { username, password };
            Response r = cs.sendRequest(new Request(CommandType.TAIKHOAN_DANG_NHAP, creds));
            if (r != null && r.isSuccess() && r.getData() != null) {
                return (TaiKhoan) r.getData();
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<?> getAllNhanVien() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHANVIEN_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public String taoMaNhanVien() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHANVIEN_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    public boolean createNhanVien(Object nv) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHANVIEN_THEM, nv));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateNhanVien(Object nv) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHANVIEN_CAP_NHAT, nv));
            return r != null && r.isSuccess();
        }
    }

    @SuppressWarnings("unchecked")
    public List<?> getAllKhachHang() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<?> getAllSanPham() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.SANPHAM_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public Object findProductByRegistration(String soDangKy) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.SANPHAM_TIM_THEO_SO_DANG_KY, soDangKy));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    public Object getProductByCode(String maSanPham) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.SANPHAM_LAY_THEO_MA, maSanPham));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllLots() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.LOSANPHAM_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public Object getLotByCode(String maLo) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.LOSANPHAM_LAY_THEO_MA, maLo));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchProducts(String tuKhoa) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.SANPHAM_TIM_KIEM, tuKhoa));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsByProduct(String maSanPham) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.LOSANPHAM_LAY_THEO_MA_SP, maSanPham));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public int getLotQuantity(String maLo, String maSanPham) throws Exception {
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
            } catch (NoSuchFieldException nsf) {
                // ignore
            }
        }
        return 0;
    }

    public Object getHoaDonByCode(String maHD) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.HOADON_LAY_THEO_MA, maHD));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietHoaDonByMaHD(String maHD) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.HOADON_LAY_CHI_TIET, maHD));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsInStockByProduct(String maSanPham) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.LOSANPHAM_LAY_CON_HANG, maSanPham));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public String taoMaHoaDon() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.HOADON_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    public boolean createHoaDon(Object hoaDon) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.HOADON_THEM, hoaDon));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaBangGia() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllBangGia() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchBangGia(String keyword) throws Exception {
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

    public Object getBangGiaByCode(String maBangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_LAY_THEO_MA, maBangGia));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    public Object getBangGiaDangHoatDong() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_LAY_DANG_HOAT_DONG, null));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietBangGia(String maBangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_LAY_CHI_TIET, maBangGia));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public boolean createBangGia(Object bangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_THEM, bangGia));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateBangGia(Object bangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_CAP_NHAT, bangGia));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteBangGia(String maBangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_XOA, maBangGia));
            return r != null && r.isSuccess();
        }
    }

    public boolean deactivateAllBangGiaExcept(String maBangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_HUY_HOAT_DONG_TAT_CA_TRU, maBangGia));
            return r != null && r.isSuccess();
        }
    }

    public boolean createChiTietBangGia(Object chiTiet) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_THEM_CHI_TIET, chiTiet));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteAllChiTietBangGia(String maBangGia) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.BANGGIA_XOA_TAT_CA_CHI_TIET, maBangGia));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaDonViTinh() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllDonViTinh() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public Object getDonViTinhByCode(String maDonViTinh) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_LAY_THEO_MA, maDonViTinh));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    public boolean createDonViTinh(Object dvt) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_THEM, dvt));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateDonViTinh(Object dvt) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_CAP_NHAT, dvt));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteDonViTinh(String maDonViTinh) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.DONVITINH_XOA, maDonViTinh));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaKhachHang() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllKhachHangForGUI() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public Object getKhachHangByCode(String maKhachHang) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_LAY_THEO_MA, maKhachHang));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    public Object getKhachHangByPhone(String soDienThoai) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_LAY_THEO_SDT, soDienThoai));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    public boolean createKhachHang(Object kh) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_THEM, kh));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaKhuyenMai() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllKhuyenMai() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public Object getKhuyenMaiByCode(String maKM) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_LAY_THEO_MA, maKM));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietKhuyenMaiByMaKM(String maKM) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_LAY_CHI_TIET, maKM));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public boolean createKhuyenMai(Object km) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_THEM, km));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateKhuyenMai(Object km) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_CAP_NHAT, km));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteKhuyenMai(String maKM) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_XOA, maKM));
            return r != null && r.isSuccess();
        }
    }

    public boolean createChiTietKhuyenMai(String maKM, String maSanPham) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_THEM_CHI_TIET_SP, new Object[]{ maKM, maSanPham }));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteChiTietKhuyenMai(String maKM, String maSanPham) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHUYENMAI_XOA_CHI_TIET_SP, new Object[]{ maKM, maSanPham }));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateKhachHang(Object kh) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_CAP_NHAT, kh));
            return r != null && r.isSuccess();
        }
    }

    public boolean deleteKhachHang(String maKhachHang) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.KHACHHANG_XOA, maKhachHang));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaNhaCungCap() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllNhaCungCap() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public Object getNhaCungCapByCodeOrPhone(String keyword) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_LAY_THEO_MA_HOAC_SDT, keyword));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchNhaCungCap(String keyword, String khuVuc, String trangThai, String tieuChi) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_TIM_KIEM, new Object[]{ keyword, khuVuc, trangThai, tieuChi }));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public boolean createNhaCungCap(Object ncc) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_THEM, ncc));
            return r != null && r.isSuccess();
        }
    }

    public boolean updateNhaCungCap(Object ncc) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.NHACUNGCAP_CAP_NHAT, ncc));
            return r != null && r.isSuccess();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllPhieuNhap() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUNHAP_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    public Object getPhieuNhapByCode(String maPhieu) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUNHAP_LAY_THEO_MA, maPhieu));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietPhieuNhapByMa(String maPhieu) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUNHAP_LAY_CHI_TIET, maPhieu));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getPackagingRulesByProduct(String maSanPham) throws Exception {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getActivePromotionDetailsByProduct(String maSanPham) throws Exception {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getActiveKhuyenMai() throws Exception {
        return java.util.Collections.emptyList();
    }

    public boolean reduceKhuyenMaiQuantity(String maKM) throws Exception {
        return false;
    }

    public int getTotalReturned(String maHD, String maLo) throws Exception {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchHoaDonByCustomerPhone(String soDienThoai) throws Exception {
        return java.util.Collections.emptyList();
    }

    public Object getThongKeHoaDonHomNayCuaNhanVien(String maNhanVien) throws Exception {
        return null;
    }

    public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) throws Exception {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsExpired() throws Exception {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsExpiring() throws Exception {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<?, ?> getExpiredLotCountByCategory() throws Exception {
        return java.util.Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllPhieuTra() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_LAY_TAT_CA, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public Object getPhieuTraByCode(String maPhieuTra) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_LAY_THEO_MA, maPhieuTra));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchPhieuTraByPhone(String sdt) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_LAY_THEO_SDT, sdt));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchPhieuTraByKeyword(String keyword) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_LAY_THEO_KEYWORD, keyword));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public String taoMaPhieuTra() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_DEM_HOM_NAY_NV, maNhanVien));
            if (r != null && r.isSuccess() && r.getData() instanceof Number n) return n.intValue();
            return 0;
        }
    }

    public int tongSoLuongDaTra(String maHD, String maLo) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_TONG_SO_LUONG_DA_TRA, new Object[] { maHD, maLo }));
            if (r != null && r.isSuccess() && r.getData() instanceof Number n) return n.intValue();
            return 0;
        }
    }

    public boolean daTraLoTrongHoaDon(String maHD, String maLo) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_DA_TRA_LO, new Object[] { maHD, maLo }));
            if (r != null && r.isSuccess() && r.getData() instanceof Boolean b) return b;
            return false;
        }
    }

    public boolean createPhieuTra(Object phieuTra, java.util.List<?> dsChiTiet) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUTRA_THEM, new Object[] { phieuTra, dsChiTiet }));
            return r != null && r.isSuccess();
        }
    }

    public Object getThongKeNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay, int caLam) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Object[] data = new Object[] { maNhanVien, tuNgay, denNgay, caLam };
            Response r = cs.sendRequest(new Request(CommandType.THONGKE_NHANVIEN_LAY_THONG_KE, data));
            if (r != null && r.isSuccess()) return r.getData();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getDanhSachNhanVienThongKe() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.THONGKE_NHANVIEN_LAY_DANH_SACH, null));
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
        }
    }

    public String taoMaPhieuNhap() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUNHAP_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    public boolean createPhieuNhap(Object phieuNhap) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUNHAP_THEM, phieuNhap));
            return r != null && r.isSuccess();
        }
    }

    public String taoMaPhieuHuy() throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUHUY_TAO_MA, null));
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
        }
    }

    public boolean createPhieuHuy(Object phieuHuy) throws Exception {
        try (ClientSocket cs = new ClientSocket(host, port, 5000)) {
            Response r = cs.sendRequest(new Request(CommandType.PHIEUHUY_THEM, phieuHuy));
            return r != null && r.isSuccess();
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsByProductWithService(String maSanPham) throws Exception {
        return getLotsByProduct(maSanPham);
    }

    public Object getProductByCodeWithService(String maSanPham) throws Exception {
        return getProductByCode(maSanPham);
    }
}

