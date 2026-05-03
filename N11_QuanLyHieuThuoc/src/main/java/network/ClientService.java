package network;

import dto.TaiKhoanDTO;
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

    @SuppressWarnings("unchecked")
    public List<?> getAllNhanVien() {
        Response r = sendReq(CommandType.NHANVIEN_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public String taoMaNhanVien() {
        Response r = sendReq(CommandType.NHANVIEN_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createNhanVien(Object nv) {
        Response r = sendReq(CommandType.NHANVIEN_THEM, nv);
            return r != null && r.isSuccess();
    }

    public boolean updateNhanVien(Object nv) {
        Response r = sendReq(CommandType.NHANVIEN_CAP_NHAT, nv);
            return r != null && r.isSuccess();
    }

    @SuppressWarnings("unchecked")
    public List<?> getAllKhachHang() {
        Response r = sendReq(CommandType.KHACHHANG_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (List<?>) r.getData();
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
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllLots() {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public Object getLotByCode(String maLo) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA, maLo);
            if (r != null && r.isSuccess()) return r.getData();
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
                return (java.util.List<?>) r.getData();
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

    public Object getHoaDonByCode(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_THEO_MA, maHD);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietHoaDonByMaHD(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_CHI_TIET, maHD);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<?>) r.getData();
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
        Response r = sendReq(CommandType.BANGGIA_THEM, bangGia);
            return r != null && r.isSuccess();
    }

    public boolean updateBangGia(Object bangGia) {
        Response r = sendReq(CommandType.BANGGIA_CAP_NHAT, bangGia);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllDonViTinh() {
        Response r = sendReq(CommandType.DONVITINH_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public Object getDonViTinhByCode(String maDonViTinh) {
        Response r = sendReq(CommandType.DONVITINH_LAY_THEO_MA, maDonViTinh);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public boolean createDonViTinh(Object dvt) {
        Response r = sendReq(CommandType.DONVITINH_THEM, dvt);
            return r != null && r.isSuccess();
    }

    public boolean updateDonViTinh(Object dvt) {
        Response r = sendReq(CommandType.DONVITINH_CAP_NHAT, dvt);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllKhachHangForGUI() {
        Response r = sendReq(CommandType.KHACHHANG_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public Object getKhachHangByCode(String maKhachHang) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_MA, maKhachHang);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public Object getKhachHangByPhone(String soDienThoai) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_SDT, soDienThoai);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    public boolean createKhachHang(Object kh) {
        Response r = sendReq(CommandType.KHACHHANG_THEM, kh);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getChiTietKhuyenMaiByMaKM(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_CHI_TIET, maKM);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public boolean createKhuyenMai(Object km) {
        Response r = sendReq(CommandType.KHUYENMAI_THEM, km);
            return r != null && r.isSuccess();
    }

    public boolean updateKhuyenMai(Object km) {
        Response r = sendReq(CommandType.KHUYENMAI_CAP_NHAT, km);
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
        Response r = sendReq(CommandType.KHACHHANG_CAP_NHAT, kh);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllNhaCungCap() {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public Object getNhaCungCapByCodeOrPhone(String keyword) {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_THEO_MA_HOAC_SDT, keyword);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchNhaCungCap(String keyword, String khuVuc, String trangThai, String tieuChi) {
        Response r = sendReq(CommandType.NHACUNGCAP_TIM_KIEM, new Object[]{ keyword, khuVuc, trangThai, tieuChi });
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public boolean createNhaCungCap(Object ncc) {
        Response r = sendReq(CommandType.NHACUNGCAP_THEM, ncc);
            return r != null && r.isSuccess();
    }

    public boolean updateNhaCungCap(Object ncc) {
        Response r = sendReq(CommandType.NHACUNGCAP_CAP_NHAT, ncc);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getPackagingRulesByProduct(String maSanPham) {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getActivePromotionDetailsByProduct(String maSanPham) {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getActiveKhuyenMai() {
        return java.util.Collections.emptyList();
    }

    public boolean reduceKhuyenMaiQuantity(String maKM) {
        return false;
    }

    public int getTotalReturned(String maHD, String maLo) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchHoaDonByCustomerPhone(String soDienThoai) {
        return java.util.Collections.emptyList();
    }

    public Object getThongKeHoaDonHomNayCuaNhanVien(String maNhanVien) {
        return null;
    }

    public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsExpired() {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsExpiring() {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<?, ?> getExpiredLotCountByCategory() {
        return java.util.Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getAllPhieuTra() {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public Object getPhieuTraByCode(String maPhieuTra) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_MA, maPhieuTra);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchPhieuTraByPhone(String sdt) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_SDT, sdt);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> searchPhieuTraByKeyword(String keyword) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_KEYWORD, keyword);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
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

    public Object getThongKeNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay, int caLam) {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_THONG_KE, new Object[] { maNhanVien, tuNgay, denNgay, caLam });
        if (r != null && r.isSuccess()) return r.getData();
        return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<?> getDanhSachNhanVienThongKe() {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_DANH_SACH, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<?>) r.getData();
            return java.util.Collections.emptyList();
    }

    public String taoMaPhieuNhap() {
        Response r = sendReq(CommandType.PHIEUNHAP_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createPhieuNhap(Object phieuNhap) {
        Response r = sendReq(CommandType.PHIEUNHAP_THEM, phieuNhap);
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

    @SuppressWarnings("unchecked")
    public java.util.List<?> getLotsByProductWithService(String maSanPham) {
        return getLotsByProduct(maSanPham);
    }

    public Object getProductByCodeWithService(String maSanPham) {
        return getProductByCode(maSanPham);
    }
}

