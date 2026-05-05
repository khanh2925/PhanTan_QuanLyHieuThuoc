package network;

import dto.TaiKhoanDTO;
import dto.HoaDonCreateUpdateDTO;
import dto.ChiTietHoaDonCreateUpdateDTO;
import dto.SanPhamDTO;
import dto.KhachHangDTO;
import dto.LoSanPhamDTO;
import dto.QuyCachDongGoiDTO;
import dto.PhieuHuyDTO;
import dto.ChiTietPhieuHuyDTO;
import dto.PhieuTraDTO;
import dto.ChiTietPhieuTraDTO;
import dto.HoaDonDTO;
import dto.ChiTietHoaDonDTO;
import dto.PhieuNhapDTO;
import dto.ChiTietPhieuNhapDTO;
import dto.NhanVienDTO;
import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;
import dto.DonViTinhDTO;
import dto.KhuyenMaiDTO;
import dto.NhaCungCapDTO;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import entity.HoaDon;
import entity.ChiTietHoaDon;
import entity.SanPham;
import entity.KhachHang;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.DonViTinh;
import entity.NhanVien;
import entity.NhaCungCap;
import entity.PhieuHuy;
import entity.PhieuNhap;
import entity.ChiTietPhieuNhap;
import entity.PhieuTra;
import entity.ChiTietPhieuTra;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.KhuyenMai;
import entity.ChiTietKhuyenMaiSanPham;
import entity.LoaiSanPham;
import entity.DuongDung;
import entity.HinhThucKM;
import mapper.Mapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * High-level client service that wraps socket requests.
 */
public class ClientService {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
    public <T> List<T> getAllNhanVien() {
        Response r = sendReq(CommandType.NHANVIEN_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (List<T>) toNhanVienList(list);
            }
            return java.util.Collections.emptyList();
    }

    public List<NhanVienDTO> getAllNhanVienDTO() {
        Response r = sendReq(CommandType.NHANVIEN_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            List<NhanVienDTO> result = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof NhanVienDTO dto) result.add(dto);
                else if (item instanceof NhanVien nv) result.add(toNhanVienDTO(nv));
            }
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
        Response r = sendReq(CommandType.NHANVIEN_THEM, toDtoPayload(nv));
            return r != null && r.isSuccess();
    }

    public boolean updateNhanVien(Object nv) {
        Response r = sendReq(CommandType.NHANVIEN_CAP_NHAT, toDtoPayload(nv));
            return r != null && r.isSuccess();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllKhachHang() {
        Response r = sendReq(CommandType.KHACHHANG_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (List<T>) toKhachHangList(list);
            }
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllSanPham() {
        Response r = sendReq(CommandType.SANPHAM_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (List<T>) toSanPhamList(list);
            }
            return java.util.Collections.emptyList();
    }

    public Object findProductByRegistration(String soDangKy) {
        Response r = sendReq(CommandType.SANPHAM_TIM_THEO_SO_DANG_KY, soDangKy);
            if (r != null && r.isSuccess() && r.getData() instanceof SanPhamDTO dto) {
                return Mapper.map(dto, SanPham.class);
            }
            return null;
    }

    public SanPham getProductByCode(String maSanPham) {
        Response r = sendReq(CommandType.SANPHAM_LAY_THEO_MA, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof SanPhamDTO dto) {
                return Mapper.map(dto, SanPham.class);
            }
            if (r != null && r.isSuccess() && r.getData() instanceof SanPham sp) return sp;
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getAllLots() {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (java.util.List<T>) toLoSanPhamList(list);
            }
            return java.util.Collections.emptyList();
    }

    public Object getLotByCode(String maLo) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA, maLo);
            if (r != null && r.isSuccess()) return toLoSanPhamObject(r.getData());
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> searchProducts(String tuKhoa) {
        Response r = sendReq(CommandType.SANPHAM_TIM_KIEM, tuKhoa);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (java.util.List<T>) toSanPhamList(list);
            }
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLotsByProduct(String maSanPham) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA_SP, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return list.stream()
                    .filter(item -> item instanceof LoSanPhamDTO)
                    .map(item -> (T) Mapper.map((LoSanPhamDTO) item, LoSanPham.class))
                    .collect(java.util.stream.Collectors.toList());
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

    public HoaDonDTO getHoaDonByCode(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_THEO_MA, maHD);
            if (r != null && r.isSuccess() && r.getData() instanceof HoaDonDTO hd) return hd;
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<ChiTietHoaDonDTO> getChiTietHoaDonByMaHD(String maHD) {
        Response r = sendReq(CommandType.HOADON_LAY_CHI_TIET, maHD);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<ChiTietHoaDonDTO>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLotsInStockByProduct(String maSanPham) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_CON_HANG, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return (java.util.List<T>) toLoSanPhamList(list);
            }
            return java.util.Collections.emptyList();
    }

    public String taoMaHoaDon() {
        Response r = sendReq(CommandType.HOADON_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createHoaDon(Object hoaDon) {
        Response r = sendReq(CommandType.HOADON_THEM, toDtoPayload(hoaDon));
        return r != null && r.isSuccess();
    }

    public String taoMaBangGia() {
        Response r = sendReq(CommandType.BANGGIA_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getAllBangGia() {
        Response r = sendReq(CommandType.BANGGIA_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> searchBangGia(String keyword) {
        java.util.List<T> all = getAllBangGia();
        if (keyword == null || keyword.isBlank()) return all;
        String kw = keyword.trim().toLowerCase();
        java.util.List<T> rs = new java.util.ArrayList<>();
        for (T o : all) {
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
    public <T> java.util.List<T> getChiTietBangGia(String maBangGia) {
        Response r = sendReq(CommandType.BANGGIA_LAY_CHI_TIET, maBangGia);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    public boolean createBangGia(Object bangGia) {
        Response r = sendReq(CommandType.BANGGIA_THEM, toDtoPayload(bangGia));
            return r != null && r.isSuccess();
    }

    public boolean updateBangGia(Object bangGia) {
        Response r = sendReq(CommandType.BANGGIA_CAP_NHAT, toDtoPayload(bangGia));
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
        Response r = sendReq(CommandType.BANGGIA_THEM_CHI_TIET, toDtoPayload(chiTiet));
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
    public <T> java.util.List<T> getAllDonViTinh() {
        Response r = sendReq(CommandType.DONVITINH_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toDonViTinhList(list);
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<DonViTinhDTO> getAllDonViTinhDTO() {
        Response r = sendReq(CommandType.DONVITINH_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<DonViTinhDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof DonViTinhDTO dto) result.add(dto);
                else if (item instanceof DonViTinh dvt) result.add(toDonViTinhDTO(dvt));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public Object getDonViTinhByCode(String maDonViTinh) {
        Response r = sendReq(CommandType.DONVITINH_LAY_THEO_MA, maDonViTinh);
            if (r != null && r.isSuccess()) return toDonViTinhObject(r.getData());
            return null;
    }

    public boolean createDonViTinh(Object dvt) {
        Response r = sendReq(CommandType.DONVITINH_THEM, toDtoPayload(dvt));
            return r != null && r.isSuccess();
    }

    public boolean updateDonViTinh(Object dvt) {
        Response r = sendReq(CommandType.DONVITINH_CAP_NHAT, toDtoPayload(dvt));
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

    public <T> java.util.List<T> getAllKhachHangForGUI() {
        return getAllKhachHang();
    }

    public Object getKhachHangByCode(String maKhachHang) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_MA, maKhachHang);
            if (r != null && r.isSuccess() && r.getData() instanceof KhachHangDTO dto) {
                return Mapper.map(dto, KhachHang.class);
            }
            return null;
    }

    public Object getKhachHangByPhone(String soDienThoai) {
        Response r = sendReq(CommandType.KHACHHANG_LAY_THEO_SDT, soDienThoai);
            if (r != null && r.isSuccess() && r.getData() instanceof KhachHangDTO dto) {
                return Mapper.map(dto, KhachHang.class);
            }
            return null;
    }

    public boolean createKhachHang(Object kh) {
        Response r = sendReq(CommandType.KHACHHANG_THEM, toDtoPayload(kh));
            return r != null && r.isSuccess();
    }

    public String taoMaKhuyenMai() {
        Response r = sendReq(CommandType.KHUYENMAI_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getAllKhuyenMai() {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toKhuyenMaiList(list);
            return java.util.Collections.emptyList();
    }

    public java.util.List<KhuyenMaiDTO> getAllKhuyenMaiDTO() {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<KhuyenMaiDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof KhuyenMaiDTO dto) result.add(dto);
                else if (item instanceof KhuyenMai km) result.add(toKhuyenMaiDTO(km));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public Object getKhuyenMaiByCode(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_THEO_MA, maKM);
            if (r != null && r.isSuccess()) return toKhuyenMaiObject(r.getData());
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getChiTietKhuyenMaiByMaKM(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_CHI_TIET, maKM);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toChiTietKhuyenMaiList(list);
            return java.util.Collections.emptyList();
    }

    public java.util.List<ChiTietKhuyenMaiSanPhamDTO> getChiTietKhuyenMaiByMaKMDTO(String maKM) {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_CHI_TIET, maKM);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<ChiTietKhuyenMaiSanPhamDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof ChiTietKhuyenMaiSanPhamDTO dto) result.add(dto);
                else if (item instanceof ChiTietKhuyenMaiSanPham ct) result.add(toChiTietKhuyenMaiDTO(ct));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public boolean createKhuyenMai(Object km) {
        Response r = sendReq(CommandType.KHUYENMAI_THEM, toDtoPayload(km));
            return r != null && r.isSuccess();
    }

    public boolean updateKhuyenMai(Object km) {
        Response r = sendReq(CommandType.KHUYENMAI_CAP_NHAT, toDtoPayload(km));
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
        Response r = sendReq(CommandType.KHACHHANG_CAP_NHAT, toDtoPayload(kh));
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
    public <T> java.util.List<T> getAllNhaCungCap() {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toNhaCungCapList(list);
            return java.util.Collections.emptyList();
    }

    public java.util.List<NhaCungCapDTO> getAllNhaCungCapDTO() {
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<NhaCungCapDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof NhaCungCapDTO dto) result.add(dto);
                else if (item instanceof NhaCungCap ncc) result.add(toNhaCungCapDTO(ncc));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    public NhaCungCap getNhaCungCapByCodeOrPhone(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        Response r = sendReq(CommandType.NHACUNGCAP_LAY_THEO_MA_HOAC_SDT, kw);
            if (r != null && r.isSuccess()) return toNhaCungCapObject(r.getData());
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> searchNhaCungCap(String keyword, String khuVuc, String trangThai, String tieuChi) {
        String kw = keyword == null ? "" : keyword.trim();
        Response r = sendReq(CommandType.NHACUNGCAP_TIM_KIEM, new Object[]{ kw, khuVuc, trangThai, tieuChi });
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toNhaCungCapList(list);
            return java.util.Collections.emptyList();
    }

    public boolean createNhaCungCap(Object ncc) {
        Response r = sendReq(CommandType.NHACUNGCAP_THEM, toDtoPayload(ncc));
            return r != null && r.isSuccess();
    }

    public boolean updateNhaCungCap(Object ncc) {
        Response r = sendReq(CommandType.NHACUNGCAP_CAP_NHAT, toDtoPayload(ncc));
            return r != null && r.isSuccess();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getAllPhieuNhap() {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<T>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    public Object getPhieuNhapByCode(String maPhieu) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_THEO_MA, maPhieu);
            if (r != null && r.isSuccess()) return r.getData();
            return null;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getChiTietPhieuNhapByMa(String maPhieu) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_CHI_TIET, maPhieu);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
                return (java.util.List<T>) r.getData();
            }
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getPackagingRulesByProduct(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_THEO_SAN_PHAM, maSanPham);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
                return list.stream()
                    .filter(item -> item instanceof QuyCachDongGoiDTO)
                    .map(item -> (T) Mapper.map((QuyCachDongGoiDTO) item, QuyCachDongGoi.class))
                    .collect(java.util.stream.Collectors.toList());
            }
            return java.util.Collections.emptyList();
    }

    public java.util.List<QuyCachDongGoiDTO> getPackagingRulesByProductDTO(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_THEO_SAN_PHAM, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<QuyCachDongGoiDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof QuyCachDongGoiDTO dto) result.add(dto);
                else if (item instanceof QuyCachDongGoi qc) result.add(toQuyCachDongGoiDTO(qc));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getActivePromotionDetailsByProduct(String maSanPham) {
        Response r = sendReq(CommandType.SANPHAM_LAY_KHUYEN_MAI, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toChiTietKhuyenMaiList(list);
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getActiveKhuyenMai() {
        Response r = sendReq(CommandType.KHUYENMAI_LAY_DANG_HOAT_DONG, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) return (java.util.List<T>) toKhuyenMaiList(list);
        return java.util.Collections.emptyList();
    }

    public boolean reduceKhuyenMaiQuantity(String maKM) {
        return false;
    }

    public int getTotalReturned(String maHD, String maLo) {
        return tongSoLuongDaTra(maHD, maLo);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<HoaDonDTO> searchHoaDonByCustomerPhone(String soDienThoai) {
        Response r = sendReq(CommandType.HOADON_LAY_THEO_KHACHHANG, soDienThoai);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<HoaDonDTO>) r.getData();
        return java.util.Collections.emptyList();
    }

    public java.util.List<HoaDonDTO> searchHoaDonByPhone(String soDienThoai) {
        return searchHoaDonByCustomerPhone(soDienThoai);
    }

    public Object getThongKeHoaDonHomNayCuaNhanVien(String maNhanVien) {
        int soHoaDon = 0;
        double tongTien = 0;
        LocalDate today = LocalDate.now();
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (!today.equals(ngay)) continue;
            if (maNhanVien != null && hd.getMaNhanVien() != null
                    && !maNhanVien.equals(hd.getMaNhanVien())) continue;
            soHoaDon++;
            tongTien += hd.getThanhToan() > 0 ? hd.getThanhToan() : hd.getTongTien();
        }
        return new Object[] { soHoaDon, tongTien };
    }

    public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (PhieuHuyDTO ph : getAllPhieuHuy()) {
            boolean sameNv = maNhanVien == null || ph.getNhanVien() == null || maNhanVien.equals(ph.getNhanVien().getMaNhanVien());
            if (sameNv && today.equals(ph.getNgayLapPhieu())) count++;
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLotsExpired() {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLotsExpiring() {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<?, ?> getExpiredLotCountByCategory() {
        return java.util.Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<HoaDonDTO> getAllHoaDon() {
        Response r = sendReq(CommandType.HOADON_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<HoaDonDTO>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<PhieuTraDTO> getAllPhieuTra() {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<PhieuTraDTO>) r.getData();
            return java.util.Collections.emptyList();
    }

    public PhieuTraDTO getPhieuTraByCode(String maPhieuTra) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_MA, maPhieuTra);
            if (r != null && r.isSuccess() && r.getData() instanceof PhieuTraDTO pt) return pt;
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<ChiTietPhieuTraDTO> getChiTietPhieuTraByMa(String maPhieuTra) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_CHI_TIET, maPhieuTra);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<ChiTietPhieuTraDTO>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<PhieuTraDTO> searchPhieuTraByPhone(String sdt) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_SDT, sdt);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<PhieuTraDTO>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<PhieuTraDTO> searchPhieuTraByKeyword(String keyword) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_THEO_KEYWORD, keyword);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<PhieuTraDTO>) r.getData();
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

    public Response createPhieuTraResponse(Object phieuTra, java.util.List<?> dsChiTiet) {
        Object header = phieuTra;
        Object details = dsChiTiet;
        if (phieuTra instanceof PhieuTra pt) {
            header = toPhieuTraDTO(pt);
            details = toChiTietPhieuTraDTOList(dsChiTiet, pt.getMaPhieuTra());
        }
        return sendReq(CommandType.PHIEUTRA_THEM, new Object[] { header, details });
    }

    public boolean createPhieuTra(Object phieuTra, java.util.List<?> dsChiTiet) {
        Response r = createPhieuTraResponse(phieuTra, dsChiTiet);
        return r != null && r.isSuccess();
    }

    public Object getThongKeNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay, int caLam) {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_THONG_KE, new Object[] { maNhanVien, tuNgay, denNgay, caLam });
        if (r != null && r.isSuccess() && r.getData() != null) return r.getData();
        return tinhThongKeNhanVienFallback(maNhanVien, tuNgay, denNgay);
    }

    private dto.ThongKeNhanVienDTO tinhThongKeNhanVienFallback(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay) {
        dto.ThongKeNhanVienDTO dto = new dto.ThongKeNhanVienDTO();
        LocalDate tu = tuNgay != null ? new java.sql.Date(tuNgay.getTime()).toLocalDate() : null;
        LocalDate den = denNgay != null ? new java.sql.Date(denNgay.getTime()).toLocalDate() : null;
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (!inDateRange(ngay, tu, den)) continue;
            dto.soHoaDon++;
            dto.tongDoanhSo += hd.getThanhToan() > 0 ? hd.getThanhToan() : hd.getTongTien();
        }
        for (PhieuTraDTO pt : getAllPhieuTra()) {
            LocalDate ngay = parseDateFlexible(pt.getNgayLap());
            if (!inDateRange(ngay, tu, den)) continue;
            dto.soPhieuTra++;
            dto.tongTienTra += pt.getTongTienHoan();
        }
        for (PhieuHuyDTO ph : getAllPhieuHuy()) {
            LocalDate ngay = ph.getNgayLapPhieu();
            boolean sameNv = maNhanVien == null || ph.getNhanVien() == null || maNhanVien.equals(ph.getNhanVien().getMaNhanVien());
            if (sameNv && inDateRange(ngay, tu, den)) dto.soPhieuHuy++;
        }
        return dto;
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getDanhSachNhanVienThongKe() {
        Response r = sendReq(CommandType.THONGKE_NHANVIEN_LAY_DANH_SACH, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    public String taoMaPhieuNhap() {
        Response r = sendReq(CommandType.PHIEUNHAP_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createPhieuNhap(Object phieuNhap) {
        Response r = sendReq(CommandType.PHIEUNHAP_THEM, toDtoPayload(phieuNhap));
            return r != null && r.isSuccess();
    }

    public boolean themPhieuNhap(Object phieuNhap) {
        return createPhieuNhap(phieuNhap);
    }

    public String taoMaPhieuHuy() {
        Response r = sendReq(CommandType.PHIEUHUY_TAO_MA, null);
            if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
            return null;
    }

    public boolean createPhieuHuy(Object phieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_THEM, toDtoPayload(phieuHuy));
            return r != null && r.isSuccess();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<PhieuHuyDTO> getAllPhieuHuy() {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<PhieuHuyDTO>) r.getData();
            return java.util.Collections.emptyList();
    }

    public PhieuHuyDTO getPhieuHuyByCode(String maPhieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA, maPhieuHuy);
            if (r != null && r.isSuccess() && r.getData() instanceof PhieuHuyDTO ph) return ph;
            return null;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<ChiTietPhieuHuyDTO> getChiTietPhieuHuyByMa(String maPhieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA, new Object[] { "LAY_CHI_TIET", maPhieuHuy });
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
            return (java.util.List<ChiTietPhieuHuyDTO>) r.getData();
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getAllQuyCachDongGoi() {
        Response r = sendReq(CommandType.QUYCACH_LAY_TAT_CA, null);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    public java.util.List<QuyCachDongGoiDTO> getAllQuyCachDongGoiDTO() {
        Response r = sendReq(CommandType.QUYCACH_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List<?> list) {
            java.util.List<QuyCachDongGoiDTO> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item instanceof QuyCachDongGoiDTO dto) result.add(dto);
                else if (item instanceof QuyCachDongGoi qc) result.add(toQuyCachDongGoiDTO(qc));
            }
            return result;
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getPhieuNhapByNhaCungCap(String maNhaCungCap) {
        Response r = sendReq(CommandType.PHIEUNHAP_LAY_THEO_NCC, maNhaCungCap);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, Object[]> thongKeSanPhamTheoNCC(String maNhaCungCap) {
        return java.util.Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLichSuBanTheoNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay) {
        Response r = sendReq(CommandType.HOADON_LAY_THEO_NHANVIEN, maNhanVien);
            if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<T>) r.getData();
            return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLichSuTraTheoNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay) {
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLichSuHuyTheoNhanVien(String maNhanVien, java.util.Date tuNgay, java.util.Date denNgay) {
        return java.util.Collections.emptyList();
    }

    public java.util.List<PhieuTraDTO> layTatCaPhieuTra() { return getAllPhieuTra(); }
    public void refreshCache() { }
    public <T> java.util.List<T> getAllLots(String keyword) { return getAllLots(); }
    public java.util.List<String[]> getDanhSachNhanVien() { return castList(getDanhSachNhanVienThongKe(), String[].class); }
    public dto.ThongKeNhanVienDTO getThongKe(java.util.Date tuNgay, java.util.Date denNgay, String maNhanVien, int caLam) { return (dto.ThongKeNhanVienDTO) getThongKeNhanVien(maNhanVien, tuNgay, denNgay, caLam); }
    public java.util.ArrayList<SanPham> layTatCaSanPham() { return new java.util.ArrayList<>(castList(getAllSanPham(), SanPham.class)); }
    public SanPham laySanPhamTheoMa(String ma) { return getProductByCode(ma); }
    public boolean themSanPham(Object sp) { Response r = sendReq(CommandType.SANPHAM_THEM, toDtoPayload(sp)); return r != null && r.isSuccess(); }
    public boolean capNhatSanPham(Object sp) { Response r = sendReq(CommandType.SANPHAM_CAP_NHAT, toDtoPayload(sp)); return r != null && r.isSuccess(); }
    public boolean xoaSanPham(String ma) { Response r = sendReq(CommandType.SANPHAM_XOA, ma); return r != null && r.isSuccess(); }
    public void refreshCacheBangGia() { }
    public <T> java.util.List<T> layTatCaQuyCachDongGoi() { return getAllQuyCachDongGoi(); }
    public boolean themQuyCachDongGoi(Object qc) { return createQuyCachDongGoi(qc); }
    public boolean capNhatQuyCachDongGoi(Object qc) { return updateQuyCachDongGoi(qc); }
    public java.util.ArrayList<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String ma) { return new java.util.ArrayList<>(castList(getPackagingRulesByProduct(ma), QuyCachDongGoi.class)); }
    public QuyCachDongGoi timQuyCachGocTheoSanPham(String ma) { return getRootPackagingRuleByProduct(ma); }
    public <T> java.util.List<T> layTatCaDonViTinh() { return getAllDonViTinh(); }
    public String taoMaLoTuDong() {
        Response r = sendReq(CommandType.LOSANPHAM_TAO_MA, null);
        if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
        return null;
    }
    public NhaCungCap timNhaCungCapTheoMaHoacSDT(String kw) { return getNhaCungCapByCodeOrPhone(kw); }
    public double tinhLoiNhuanChinhXacTheoThang(int thang, int nam) {
        return layDoanhThuTheoThang(thang, nam) - tinhTongTienNhapTheoThang(thang, nam) - tinhTongTienTraTheoThang(thang, nam) - tinhTongTienHuyTheoThang(thang, nam);
    }
    public double tinhTongTienNhapTheoThang(int thang, int nam) {
        double tong = 0;
        for (PhieuNhapDTO pn : castList(getAllPhieuNhap(), PhieuNhapDTO.class)) {
            LocalDate ngay = parseDateFlexible(pn.getNgayNhap());
            if (ngay != null && ngay.getMonthValue() == thang && ngay.getYear() == nam) tong += pn.getTongTien();
        }
        return tong;
    }
    public int demKhachHangMoiTheoThang(int thang, int nam) {
        int count = 0;
        for (KhachHang kh : castList(getAllKhachHangForGUI(), KhachHang.class)) {
            if (kh.getMaKhachHang() != null && kh.getMaKhachHang().contains(String.format("%04d%02d", nam, thang))) count++;
        }
        return count;
    }
    public int demPhieuTraChuaDuyet() {
        int count = 0;
        for (PhieuTraDTO pt : getAllPhieuTra()) if (!pt.isTrangThai()) count++;
        return count;
    }
    public int demPhieuHuyChuaDuyet() {
        int count = 0;
        for (PhieuHuyDTO ph : getAllPhieuHuy()) if (!ph.isTrangThai()) count++;
        return count;
    }
    public double layDoanhThuTheoThang(int thang, int nam) {
        double tong = 0;
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (ngay != null && ngay.getMonthValue() == thang && ngay.getYear() == nam) tong += hd.getThanhToan() > 0 ? hd.getThanhToan() : hd.getTongTien();
        }
        return tong;
    }
    public double tinhTongTienTraTheoThang(int thang, int nam) {
        double tong = 0;
        for (PhieuTraDTO pt : getAllPhieuTra()) {
            LocalDate ngay = parseDateFlexible(pt.getNgayLap());
            if (ngay != null && ngay.getMonthValue() == thang && ngay.getYear() == nam) tong += pt.getTongTienHoan();
        }
        return tong;
    }
    public double tinhTongTienHuyTheoThang(int thang, int nam) {
        double tong = 0;
        for (PhieuHuyDTO ph : getAllPhieuHuy()) {
            LocalDate ngay = ph.getNgayLapPhieu();
            if (ngay != null && ngay.getMonthValue() == thang && ngay.getYear() == nam) tong += ph.getTongTien();
        }
        return tong;
    }
    public java.util.List<PhieuHuyDTO> layTatCaPhieuHuy() { return getAllPhieuHuy(); }
    public PhieuHuyDTO layTheoMa(String maPhieuHuy) { return getPhieuHuyByCode(maPhieuHuy); }
    public java.util.List<ChiTietPhieuHuyDTO> layChiTietTheoMaPhieu(String maPhieuHuy) { return getChiTietPhieuHuyByMa(maPhieuHuy); }
    public java.util.List<ChiTietPhieuHuyDTO> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy) { return getChiTietPhieuHuyByMa(maPhieuHuy); }
    public boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThai) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA,
                new Object[] { "CAP_NHAT_TRANG_THAI_CHI_TIET", maPhieuHuy, maLo, trangThai });
        return r != null && r.isSuccess();
    }

    public boolean checkTrangThai(String maPhieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA, new Object[] { "CHECK_TRANG_THAI", maPhieuHuy });
        return r != null && r.isSuccess() && r.getData() instanceof Boolean b && b;
    }

    public boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy) {
        Response r = sendReq(CommandType.PHIEUHUY_LAY_THEO_MA,
                new Object[] { "CAP_NHAT_TRANG_THAI_PHIEU", maPhieuHuy });
        return r != null && r.isSuccess();
    }
    public void clearCache() { }
    public String capNhatTrangThai_GiaoDich(String maPT, String maHD, String maLo, String maDVT, Object nv, int trangThaiMoi) {
        Response r = sendReq(CommandType.PHIEUTRA_CAP_NHAT_TRANG_THAI_GIAO_DICH, new Object[] { maPT, maHD, maLo, maDVT, toDtoPayload(nv), trangThaiMoi });
        if (r != null && r.isSuccess()) return r.getData() != null ? r.getData().toString() : "OK";
        return null;
    }
    public boolean capNhatTrangThaiPhieuTra(String maPT, boolean daDuyet) {
        Response r = sendReq(CommandType.PHIEUTRA_CAP_NHAT_TRANG_THAI, new Object[] { maPT, daDuyet });
        return r != null && r.isSuccess();
    }
    @SuppressWarnings("unchecked")
    public java.util.List<ChiTietPhieuTraDTO> timKiemChiTietBangMaPhieuTra(String maPT) {
        Response r = sendReq(CommandType.PHIEUTRA_LAY_CHI_TIET, maPT);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) return (java.util.List<ChiTietPhieuTraDTO>) r.getData();
        return java.util.Collections.emptyList();
    }
    public PhieuTraDTO timKiemPhieuTraBangMa(String maPT) { return getPhieuTraByCode(maPT); }
    public QuyCachDongGoi getRootPackagingRuleByProduct(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_GOC_THEO_SAN_PHAM, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof QuyCachDongGoi qc) return qc;
        return null;
    }
    @SuppressWarnings("unchecked")
    public <T> java.util.List<T> getLotsByProductWithService(String maSanPham) {
        return getLotsByProduct(maSanPham);
    }

    public Object getProductByCodeWithService(String maSanPham) {
        return getProductByCode(maSanPham);
    }

    public static class BanGhiTaiChinh implements java.io.Serializable {
        public String thoiGian;
        public double banHang;
        public double nhapHang;
        public double traHang;
        public double huyHang;

        public BanGhiTaiChinh(String thoiGian, double banHang, double nhapHang, double traHang, double huyHang) {
            this.thoiGian = thoiGian;
            this.banHang = banHang;
            this.nhapHang = nhapHang;
            this.traHang = traHang;
            this.huyHang = huyHang;
        }
    }

    private LocalDate parseDateFlexible(String value) {
        if (value == null || value.isBlank()) return null;
        try { return LocalDate.parse(value, DISPLAY_DATE_FORMAT); } catch (RuntimeException ignored) { }
        try { return LocalDate.parse(value); } catch (RuntimeException ignored) { }
        if (value.length() >= 10) {
            try { return LocalDate.parse(value.substring(0, 10)); } catch (RuntimeException ignored) { }
        }
        return null;
    }

    private boolean inDateRange(LocalDate ngay, LocalDate tuNgay, LocalDate denNgay) {
        if (ngay == null) return false;
        return (tuNgay == null || !ngay.isBefore(tuNgay)) && (denNgay == null || !ngay.isAfter(denNgay));
    }

    @SuppressWarnings("unchecked")
    private <T> java.util.List<T> castList(java.util.List<?> list, Class<T> type) {
        java.util.List<T> result = new java.util.ArrayList<>();
        for (Object item : list) if (type.isInstance(item)) result.add(type.cast(item));
        return result;
    }

    private Object toDtoPayload(Object data) {
        if (data instanceof HoaDon hd) return toHoaDonCreateUpdateDTO(hd);
        if (data instanceof NhanVien nv) return toNhanVienDTO(nv);
        if (data instanceof KhachHang kh) return toKhachHangDTO(kh);
        if (data instanceof SanPham sp) return toSanPhamDTO(sp);
        if (data instanceof LoSanPham lo) return toLoSanPhamDTO(lo);
        if (data instanceof QuyCachDongGoi qc) return toQuyCachDongGoiDTO(qc);
        if (data instanceof DonViTinh dvt) return toDonViTinhDTO(dvt);
        if (data instanceof NhaCungCap ncc) return toNhaCungCapDTO(ncc);
        if (data instanceof PhieuHuy ph) return toPhieuHuyDTO(ph);
        if (data instanceof PhieuNhap pn) return toPhieuNhapDTO(pn);
        if (data instanceof PhieuTra pt) return toPhieuTraDTO(pt);
        if (data instanceof BangGia bg) return toBangGiaDTO(bg);
        if (data instanceof ChiTietBangGia ctbg) return toChiTietBangGiaDTO(ctbg);
        if (data instanceof KhuyenMai km) return toKhuyenMaiDTO(km);
        return data;
    }

    private NhanVienDTO toNhanVienDTO(NhanVien nv) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(nv.getMaNhanVien());
        dto.setTenNhanVien(nv.getTenNhanVien());
        dto.setGioiTinh(nv.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(nv.getNgaySinh() != null ? nv.getNgaySinh().format(DISPLAY_DATE_FORMAT) : null);
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setDiaChi(nv.getDiaChi());
        dto.setVaiTro(nv.isQuanLy() ? "Quản lý" : "Nhân viên");
        dto.setCaLam(nv.getTenCaLam());
        dto.setTrangThai(nv.isTrangThai() ? "Đang làm" : "Nghỉ việc");
        return dto;
    }

    private KhachHangDTO toKhachHangDTO(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(kh.getMaKhachHang());
        dto.setTenKhachHang(kh.getTenKhachHang());
        dto.setSoDienThoai(kh.getSoDienThoai());
        dto.setGioiTinh(kh.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(kh.getNgaySinh() != null ? kh.getNgaySinh().format(DISPLAY_DATE_FORMAT) : null);
        return dto;
    }

    private SanPhamDTO toSanPhamDTO(SanPham sp) {
        SanPhamDTO dto = new SanPhamDTO();
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setLoaiSanPham(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : null);
        dto.setDuongDung(sp.getDuongDung() != null ? sp.getDuongDung().getTenDuongDung() : null);
        dto.setSoDangKy(sp.getSoDangKy());
        dto.setGiaNhap(sp.getGiaNhap());
        dto.setGiaBan(sp.getGiaBan());
        dto.setHinhAnh(sp.getHinhAnh());
        dto.setKeBanSanPham(sp.getKeBanSanPham());
        dto.setHoatDong(sp.isHoatDong());
        return dto;
    }

    private LoSanPhamDTO toLoSanPhamDTO(LoSanPham lo) {
        LoSanPhamDTO dto = new LoSanPhamDTO();
        dto.setMaLo(lo.getMaLo());
        dto.setHanSuDung(lo.getHanSuDung() != null ? lo.getHanSuDung().format(DISPLAY_DATE_FORMAT) : null);
        dto.setSoLuongTon(lo.getSoLuongTon());
        if (lo.getSanPham() != null) {
            dto.setMaSanPham(lo.getSanPham().getMaSanPham());
            dto.setTenSanPham(lo.getSanPham().getTenSanPham());
        }
        return dto;
    }

    private DonViTinhDTO toDonViTinhDTO(DonViTinh dvt) {
        DonViTinhDTO dto = new DonViTinhDTO();
        dto.setMaDonViTinh(dvt.getMaDonViTinh());
        dto.setTenDonViTinh(dvt.getTenDonViTinh());
        return dto;
    }

    private QuyCachDongGoiDTO toQuyCachDongGoiDTO(QuyCachDongGoi qc) {
        QuyCachDongGoiDTO dto = new QuyCachDongGoiDTO();
        dto.setMaQuyCach(qc.getMaQuyCach());
        dto.setDonViTinh(qc.getDonViTinh() != null ? toDonViTinhDTO(qc.getDonViTinh()) : null);
        dto.setSanPham(qc.getSanPham() != null ? toSanPhamDTO(qc.getSanPham()) : null);
        dto.setHeSoQuyDoi(qc.getHeSoQuyDoi());
        dto.setTiLeGiam(qc.getTiLeGiam());
        dto.setDonViGoc(qc.isDonViGoc());
        dto.setTrangThai(qc.isTrangThai());
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

    private BangGiaDTO toBangGiaDTO(BangGia bg) {
        BangGiaDTO dto = new BangGiaDTO();
        dto.setMaBangGia(bg.getMaBangGia());
        dto.setMaNhanVien(bg.getNhanVien() != null ? bg.getNhanVien().getMaNhanVien() : null);
        dto.setTenBangGia(bg.getTenBangGia());
        dto.setNgayApDung(bg.getNgayApDung());
        dto.setHoatDong(bg.isHoatDong());
        return dto;
    }

    private ChiTietBangGiaDTO toChiTietBangGiaDTO(ChiTietBangGia ct) {
        ChiTietBangGiaDTO dto = new ChiTietBangGiaDTO();
        dto.setMaBangGia(ct.getBangGia() != null ? ct.getBangGia().getMaBangGia() : null);
        dto.setGiaTu(ct.getGiaTu());
        dto.setGiaDen(ct.getGiaDen());
        dto.setTiLe(ct.getTiLe());
        return dto;
    }

    private KhuyenMaiDTO toKhuyenMaiDTO(KhuyenMai km) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setMaKM(km.getMaKM());
        dto.setTenKM(km.getTenKM());
        dto.setNgayBatDau(km.getNgayBatDau() != null ? km.getNgayBatDau().format(DISPLAY_DATE_FORMAT) : null);
        dto.setNgayKetThuc(km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(DISPLAY_DATE_FORMAT) : null);
        dto.setTrangThai(km.isTrangThai());
        dto.setKhuyenMaiHoaDon(km.isKhuyenMaiHoaDon());
        dto.setLoaiKhuyenMai(km.isKhuyenMaiHoaDon() ? "Hóa đơn" : "Sản phẩm");
        dto.setHinhThuc(km.getHinhThuc() != null ? km.getHinhThuc().name() : null);
        dto.setGiaTri(km.getGiaTri());
        dto.setDieuKienApDungHoaDon(km.getDieuKienApDungHoaDon());
        dto.setSoLuongKhuyenMai(km.getSoLuongKhuyenMai());
        dto.setDangHoatDong(km.isDangHoatDong());
        return dto;
    }

    private ChiTietKhuyenMaiSanPhamDTO toChiTietKhuyenMaiDTO(ChiTietKhuyenMaiSanPham ct) {
        ChiTietKhuyenMaiSanPhamDTO dto = new ChiTietKhuyenMaiSanPhamDTO();
        SanPham sp = ct.getSanPham();
        KhuyenMai km = ct.getKhuyenMai();
        if (sp != null) {
            dto.setMaSanPham(sp.getMaSanPham());
            dto.setTenSanPham(sp.getTenSanPham());
        }
        if (km != null) {
            dto.setMaKM(km.getMaKM());
            dto.setTenKM(km.getTenKM());
            dto.setHinhThuc(km.getHinhThuc() != null ? km.getHinhThuc().name() : null);
            dto.setGiaTri(km.getGiaTri());
            dto.setNgayBatDau(km.getNgayBatDau() != null ? km.getNgayBatDau().format(DISPLAY_DATE_FORMAT) : null);
            dto.setNgayKetThuc(km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(DISPLAY_DATE_FORMAT) : null);
            dto.setDangHoatDong(km.isDangHoatDong());
        }
        return dto;
    }

    private Object toNhanVienObject(Object data) {
        if (data instanceof NhanVienDTO dto) {
            try {
                return Mapper.map(dto, NhanVien.class);
            } catch (RuntimeException ex) {
                NhanVien nv = new NhanVien(dto.getMaNhanVien(), dto.getTenNhanVien());
                return nv;
            }
        }
        return data;
    }

    private java.util.List<?> toNhanVienList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toNhanVienObject(item));
        return result;
    }

    private Object toSanPhamObject(Object data) {
        if (data instanceof SanPhamDTO dto) {
            try {
                return Mapper.map(dto, SanPham.class);
            } catch (RuntimeException ex) {
                SanPham sp = new SanPham(dto.getMaSanPham());
                sp.setTenSanPham(dto.getTenSanPham());
                sp.setLoaiSanPham(parseLoaiSanPham(dto.getLoaiSanPham()));
                sp.setDuongDung(parseDuongDung(dto.getDuongDung()));
                sp.setSoDangKy(dto.getSoDangKy());
                if (dto.getGiaNhap() > 0) sp.setGiaNhap(dto.getGiaNhap());
                sp.setGiaBan(dto.getGiaBan());
                sp.setHinhAnh(dto.getHinhAnh());
                sp.setKeBanSanPham(dto.getKeBanSanPham());
                sp.setHoatDong(dto.isHoatDong());
                return sp;
            }
        }
        return data;
    }

    private java.util.List<?> toSanPhamList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toSanPhamObject(item));
        return result;
    }

    private LoaiSanPham parseLoaiSanPham(String value) {
        if (value == null || value.isBlank()) return LoaiSanPham.SAN_PHAM_KHAC;
        for (LoaiSanPham loai : LoaiSanPham.values()) {
            if (loai.name().equalsIgnoreCase(value) || loai.getTenLoai().equalsIgnoreCase(value)) return loai;
        }
        return LoaiSanPham.SAN_PHAM_KHAC;
    }

    private DuongDung parseDuongDung(String value) {
        if (value == null || value.isBlank()) return DuongDung.KHAC;
        for (DuongDung dd : DuongDung.values()) {
            if (dd.name().equalsIgnoreCase(value) || dd.getTenDuongDung().equalsIgnoreCase(value)) return dd;
        }
        return DuongDung.KHAC;
    }

    private Object toDonViTinhObject(Object data) {
        if (data instanceof DonViTinhDTO dto) {
            return new DonViTinh(dto.getMaDonViTinh(), dto.getTenDonViTinh());
        }
        return data;
    }

    private java.util.List<?> toDonViTinhList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toDonViTinhObject(item));
        return result;
    }

    private NhaCungCap toNhaCungCapObject(Object data) {
        if (data instanceof NhaCungCap ncc) return ncc;
        if (data instanceof NhaCungCapDTO dto) {
            NhaCungCap ncc = new NhaCungCap(dto.getMaNhaCungCap(), dto.getTenNhaCungCap(), dto.getSoDienThoai(), dto.getDiaChi(), dto.getEmail());
            ncc.setHoatDong(dto.isHoatDong());
            return ncc;
        }
        return null;
    }

    private java.util.List<?> toNhaCungCapList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) {
            NhaCungCap ncc = toNhaCungCapObject(item);
            if (ncc != null) result.add(ncc);
        }
        return result;
    }

    private HinhThucKM parseHinhThucKM(String value) {
        if (value == null || value.isBlank()) return HinhThucKM.GIAM_GIA_PHAN_TRAM;
        String normalized = value.trim().toUpperCase(java.util.Locale.ROOT);
        for (HinhThucKM hinhThuc : HinhThucKM.values()) {
            if (hinhThuc.name().equals(normalized) || hinhThuc.getMoTa().toUpperCase(java.util.Locale.ROOT).equals(normalized)) {
                return hinhThuc;
            }
        }
        if (normalized.contains("TIEN") || normalized.contains("TIỀN")) return HinhThucKM.GIAM_GIA_TIEN;
        return HinhThucKM.GIAM_GIA_PHAN_TRAM;
    }

    private Object toKhuyenMaiObject(Object data) {
        if (data instanceof KhuyenMaiDTO dto) {
            KhuyenMai km = new KhuyenMai(dto.getMaKM());
            km.setTenKM(dto.getTenKM());
            if (dto.getNgayBatDau() != null && !dto.getNgayBatDau().isBlank()) km.setNgayBatDau(LocalDate.parse(dto.getNgayBatDau(), DISPLAY_DATE_FORMAT));
            if (dto.getNgayKetThuc() != null && !dto.getNgayKetThuc().isBlank()) km.setNgayKetThuc(LocalDate.parse(dto.getNgayKetThuc(), DISPLAY_DATE_FORMAT));
            km.setTrangThai(dto.isTrangThai());
            km.setKhuyenMaiHoaDon(dto.isKhuyenMaiHoaDon());
            km.setHinhThuc(parseHinhThucKM(dto.getHinhThuc()));
            km.setGiaTri(dto.getGiaTri());
            km.setDieuKienApDungHoaDon(dto.getDieuKienApDungHoaDon());
            km.setSoLuongKhuyenMai(dto.getSoLuongKhuyenMai());
            return km;
        }
        return data;
    }

    private java.util.List<?> toKhuyenMaiList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toKhuyenMaiObject(item));
        return result;
    }

    private Object toChiTietKhuyenMaiObject(Object data) {
        if (data instanceof ChiTietKhuyenMaiSanPhamDTO dto) {
            SanPham sp = new SanPham(dto.getMaSanPham());
            sp.setTenSanPham(dto.getTenSanPham());
            KhuyenMai km = new KhuyenMai(dto.getMaKM());
            km.setTenKM(dto.getTenKM());
            km.setHinhThuc(parseHinhThucKM(dto.getHinhThuc()));
            km.setGiaTri(dto.getGiaTri());
            return new ChiTietKhuyenMaiSanPham(sp, km);
        }
        return data;
    }

    private java.util.List<?> toChiTietKhuyenMaiList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toChiTietKhuyenMaiObject(item));
        return result;
    }

    private Object toKhachHangObject(Object data) {
        if (data instanceof KhachHangDTO dto) {
            return Mapper.map(dto, KhachHang.class);
        }
        return data;
    }

    private java.util.List<?> toKhachHangList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toKhachHangObject(item));
        return result;
    }

    private Object toLoSanPhamObject(Object data) {
        if (data instanceof LoSanPhamDTO dto) {
            try {
                LoSanPham lo = new LoSanPham(dto.getMaLo());
                if (dto.getHanSuDung() != null && !dto.getHanSuDung().isBlank()) {
                    lo.setHanSuDung(LocalDate.parse(dto.getHanSuDung(), DISPLAY_DATE_FORMAT));
                }
                lo.setSoLuongTon(dto.getSoLuongTon());
                if (dto.getMaSanPham() != null && dto.getMaSanPham().matches("^SP-\\d{6}$")) {
                    SanPham sp = getProductByCode(dto.getMaSanPham());
                    if (sp != null) lo.setSanPham(sp);
                }
                return lo;
            } catch (RuntimeException ex) {
                return data;
            }
        }
        return data;
    }

    private java.util.List<?> toLoSanPhamList(java.util.List<?> list) {
        java.util.List<Object> result = new java.util.ArrayList<>();
        for (Object item : list) result.add(toLoSanPhamObject(item));
        return result;
    }

    public SanPham getProductByCodeTyped(String maSanPham) { return (SanPham) getProductByCode(maSanPham); }

    public java.util.List<SanPham> getAllSanPhamTyped() { return castList(getAllSanPham(), SanPham.class); }

    @SuppressWarnings("unchecked")
    public java.util.List<SanPhamDTO> getAllSanPhamDTO() {
        Response r = sendReq(CommandType.SANPHAM_LAY_TAT_CA, null);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
            return (java.util.List<SanPhamDTO>) r.getData();
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<SanPhamDTO> searchProductsDTO(String tuKhoa) {
        Response r = sendReq(CommandType.SANPHAM_TIM_KIEM, tuKhoa);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
            return (java.util.List<SanPhamDTO>) r.getData();
        }
        return java.util.Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public java.util.List<LoSanPhamDTO> getLotsByProductDTO(String maSanPham) {
        Response r = sendReq(CommandType.LOSANPHAM_LAY_THEO_MA_SP, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof java.util.List) {
            return (java.util.List<LoSanPhamDTO>) r.getData();
        }
        return java.util.Collections.emptyList();
    }

    public KhachHang getKhachHangByPhoneTyped(String soDienThoai) { return (KhachHang) getKhachHangByPhone(soDienThoai); }

    public java.util.List<HoaDon> searchHoaDonByCustomerPhoneTyped(String soDienThoai) { return castList(searchHoaDonByCustomerPhone(soDienThoai), HoaDon.class); }

    public java.util.List<ChiTietHoaDon> getChiTietHoaDonByMaHDTyped(String maHD) { return castList(getChiTietHoaDonByMaHD(maHD), ChiTietHoaDon.class); }

    public java.util.List<DonViTinh> getAllDonViTinhTyped() { return castList(getAllDonViTinh(), DonViTinh.class); }

    public java.util.List<QuyCachDongGoi> getPackagingRulesByProductTyped(String maSanPham) { return castList(getPackagingRulesByProduct(maSanPham), QuyCachDongGoi.class); }

    public Object getNhanVienByCode(String maNhanVien) {
        Response r = sendReq(CommandType.NHANVIEN_LAY_THEO_MA, maNhanVien);
        if (r != null && r.isSuccess()) return toNhanVienObject(r.getData());
        return null;
    }

    public NhanVien getNhanVienByCodeTyped(String maNhanVien) { return (NhanVien) getNhanVienByCode(maNhanVien); }

    public boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi) {
        Response r = sendReq(CommandType.TAIKHOAN_DAT_LAI_MAT_KHAU, new Object[] { maTaiKhoan, matKhauMoi });
        return r != null && r.isSuccess();
    }

    public String timTaiKhoanQuenMatKhau(String maNV, String tenNV, String sdt, java.time.LocalDate ngaySinh) {
        Response r = sendReq(CommandType.TAIKHOAN_TIM_QUEN_MAT_KHAU, new Object[] { maNV, tenNV, sdt, ngaySinh });
        if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
        return null;
    }

    public String taoMaQuyCach() {
        Response r = sendReq(CommandType.QUYCACH_TAO_MA, null);
        if (r != null && r.isSuccess() && r.getData() != null) return r.getData().toString();
        return null;
    }

    public QuyCachDongGoi getPackagingRuleByProductAndUnit(String maSanPham, String maDonViTinh) {
        Response r = sendReq(CommandType.QUYCACH_LAY_THEO_SAN_PHAM_VA_DVT, new Object[] { maSanPham, maDonViTinh });
        if (r != null && r.isSuccess() && r.getData() instanceof QuyCachDongGoi qc) return qc;
        return null;
    }

    public QuyCachDongGoi getBasePackagingRuleByProduct(String maSanPham) {
        Response r = sendReq(CommandType.QUYCACH_LAY_GOC_THEO_SAN_PHAM, maSanPham);
        if (r != null && r.isSuccess() && r.getData() instanceof QuyCachDongGoi qc) return qc;
        return null;
    }

    public boolean createQuyCachDongGoi(Object qc) { Response r = sendReq(CommandType.QUYCACH_THEM, toDtoPayload(qc)); return r != null && r.isSuccess(); }
    public boolean updateQuyCachDongGoi(Object qc) { Response r = sendReq(CommandType.QUYCACH_CAP_NHAT, toDtoPayload(qc)); return r != null && r.isSuccess(); }
    public boolean deleteQuyCachDongGoi(String maQuyCach) { Response r = sendReq(CommandType.QUYCACH_XOA, maQuyCach); return r != null && r.isSuccess(); }

    public java.util.List<Object[]> layTopSanPhamBanChay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay, int topN) {
        Map<String, Object[]> grouped = new java.util.HashMap<>();
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (!inDateRange(ngay, tuNgay, denNgay) || hd.getChiTietList() == null) continue;
            for (ChiTietHoaDonDTO ct : hd.getChiTietList()) {
                String key = ct.getTenSanPham() != null ? ct.getTenSanPham() : ct.getMaLo();
                Object[] row = grouped.computeIfAbsent(key, k -> new Object[] { "", k, "", 0.0, 0.0 });
                row[3] = ((Number) row[3]).doubleValue() + ct.getSoLuong();
                row[4] = ((Number) row[4]).doubleValue() + ct.getThanhTien();
            }
        }
        java.util.List<Object[]> result = new java.util.ArrayList<>(grouped.values());
        result.sort((a, b) -> Double.compare(((Number) b[4]).doubleValue(), ((Number) a[4]).doubleValue()));
        return result.size() > topN ? result.subList(0, topN) : result;
    }
    public double tinhTongDoanhThuTheoKhoangNgay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        double tong = 0;
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (inDateRange(ngay, tuNgay, denNgay)) tong += hd.getThanhToan() > 0 ? hd.getThanhToan() : hd.getTongTien();
        }
        return tong;
    }
    public double tinhTongDoanhThuKyTruoc(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
        return tinhTongDoanhThuTheoKhoangNgay(tuNgay.minusDays(days), denNgay.minusDays(days));
    }
    public double laySoLuongBanKyTruoc(String maSanPham, java.time.LocalDate tuNgay, java.time.LocalDate denNgay) { return 0; }
    public java.util.List<String> layDanhSachLoaiSanPham() {
        java.util.Set<String> loai = new java.util.TreeSet<>();
        for (SanPhamDTO sp : getAllSanPhamDTO()) if (sp.getLoaiSanPham() != null && !sp.getLoaiSanPham().isBlank()) loai.add(sp.getLoaiSanPham());
        return new java.util.ArrayList<>(loai);
    }
    public java.util.List<Object[]> layLoSapHetHan(int soNgay, String loaiSP) {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Object o : getAllLots()) {
            LoSanPham lo = o instanceof LoSanPham l ? l : null;
            if (lo == null || lo.getHanSuDung() == null) continue;
            long conLai = java.time.temporal.ChronoUnit.DAYS.between(today, lo.getHanSuDung());
            if (conLai < 0 || conLai > soNgay || lo.getSoLuongTon() <= 0) continue;
            SanPham sp = lo.getSanPham();
            String loai = sp != null && sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "";
            if (loaiSP != null && !loaiSP.equalsIgnoreCase("Tất cả") && !loaiSP.isBlank() && !loaiSP.equalsIgnoreCase(loai)) continue;
            result.add(new Object[] { lo.getMaLo(), sp != null ? sp.getTenSanPham() : "", loai, lo.getHanSuDung(), lo.getSoLuongTon(), sp != null ? sp.getGiaBan() : 0.0, sp != null ? sp.getMaSanPham() : "" });
        }
        return result;
    }
    public double tinhTrungBinhBanNgayTheoLo(String maLo, int soNgay) { return 0; }
    public java.util.List<Object[]> laySanPhamTonKhoThap(int nguong, String loaiSP) {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        for (SanPhamDTO sp : getAllSanPhamDTO()) {
            String loai = sp.getLoaiSanPham() != null ? sp.getLoaiSanPham() : "";
            if (loaiSP != null && !loaiSP.equalsIgnoreCase("Tất cả") && !loaiSP.isBlank() && !loaiSP.equalsIgnoreCase(loai)) continue;
            if (sp.getTongTonKho() <= nguong) result.add(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), loai, sp.getTongTonKho(), sp.getGiaNhap(), "", "" });
        }
        return result;
    }
    public double tinhTrungBinhBanNgay(String maSP, int soNgay) { return 0; }
    public Object[] timNhaCungCapGoiY(int nguong) { return new Object[] { "Không có dữ liệu", 0 }; }
    public java.util.List<Object[]> layThongKeTheoLoaiSanPham(int nam) {
        Map<String, Object[]> grouped = new java.util.HashMap<>();
        for (HoaDonDTO hd : getAllHoaDon()) {
            LocalDate ngay = parseDateFlexible(hd.getNgayLap());
            if (ngay == null || ngay.getYear() != nam || hd.getChiTietList() == null) continue;
            for (ChiTietHoaDonDTO ct : hd.getChiTietList()) {
                Object[] row = grouped.computeIfAbsent("Khác", k -> new Object[] { k, 0, 0.0, 0.0 });
                row[1] = ((Integer) row[1]) + ct.getSoLuong();
                row[2] = ((Double) row[2]) + ct.getThanhTien();
            }
        }
        return new java.util.ArrayList<>(grouped.values());
    }
    public java.util.Map<String, Double> layDoanhThuNamTruocTheoLoai(int nam) { return java.util.Collections.emptyMap(); }
    public double tinhTongDoanhThuTheoNam(int nam) { return tinhTongDoanhThuTheoKhoangNgay(LocalDate.of(nam, 1, 1), LocalDate.of(nam, 12, 31)); }
    public java.util.List<BanGhiTaiChinh> getThongKeTaiChinhTheoNgay(java.util.Date tu, java.util.Date den, String maLoaiSP) {
        LocalDate start = tu.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate end = den.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        java.util.List<BanGhiTaiChinh> result = new java.util.ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            result.add(new BanGhiTaiChinh(d.format(DISPLAY_DATE_FORMAT), tinhTongDoanhThuTheoKhoangNgay(d, d), 0, 0, 0));
        }
        return result;
    }
    public java.util.List<BanGhiTaiChinh> getThongKeTaiChinhTheoThang(int nam, String maLoaiSP) {
        java.util.List<BanGhiTaiChinh> result = new java.util.ArrayList<>();
        for (int m = 1; m <= 12; m++) result.add(new BanGhiTaiChinh(String.valueOf(m), layDoanhThuTheoThang(m, nam), tinhTongTienNhapTheoThang(m, nam), tinhTongTienTraTheoThang(m, nam), tinhTongTienHuyTheoThang(m, nam)));
        return result;
    }
    public java.util.List<BanGhiTaiChinh> getThongKeTaiChinhTheoNam(int namS, int namE, String maLoaiSP) {
        java.util.List<BanGhiTaiChinh> result = new java.util.ArrayList<>();
        for (int y = namS; y <= namE; y++) result.add(new BanGhiTaiChinh(String.valueOf(y), tinhTongDoanhThuTheoNam(y), 0, 0, 0));
        return result;
    }

    private PhieuHuyDTO toPhieuHuyDTO(PhieuHuy ph) {
        PhieuHuyDTO dto = new PhieuHuyDTO();
        dto.setMaPhieuHuy(ph.getMaPhieuHuy());
        dto.setNgayLapPhieu(ph.getNgayLapPhieu());
        dto.setTrangThai(ph.isTrangThai());
        if (ph.getNhanVien() != null) {
            dto.NhanVienDTO nv = new dto.NhanVienDTO();
            nv.setMaNhanVien(ph.getNhanVien().getMaNhanVien());
            nv.setTenNhanVien(ph.getNhanVien().getTenNhanVien());
            dto.setNhanVien(nv);
        }

        List<ChiTietPhieuHuyDTO> chiTietList = new ArrayList<>();
        if (ph.getChiTietPhieuHuyList() != null) {
            for (entity.ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                ChiTietPhieuHuyDTO ctDto = new ChiTietPhieuHuyDTO();
                ctDto.setMaPhieuHuy(ph.getMaPhieuHuy());
                ctDto.setSoLuongHuy(ct.getSoLuongHuy());
                ctDto.setLyDoChiTiet(ct.getLyDoChiTiet());
                ctDto.setDonGiaNhap(ct.getDonGiaNhap());
                ctDto.setThanhTien(ct.getThanhTien());
                ctDto.setTrangThai(ct.getTrangThai());
                if (ct.getLoSanPham() != null) {
                    LoSanPhamDTO lo = new LoSanPhamDTO();
                    lo.setMaLo(ct.getLoSanPham().getMaLo());
                    if (ct.getLoSanPham().getSanPham() != null) {
                        lo.setMaSanPham(ct.getLoSanPham().getSanPham().getMaSanPham());
                        lo.setTenSanPham(ct.getLoSanPham().getSanPham().getTenSanPham());
                    }
                    ctDto.setMaLo(lo.getMaLo());
                    ctDto.setLoSanPham(lo);
                }
                if (ct.getDonViTinh() != null) {
                    dto.DonViTinhDTO dvt = new dto.DonViTinhDTO();
                    dvt.setMaDonViTinh(ct.getDonViTinh().getMaDonViTinh());
                    dvt.setTenDonViTinh(ct.getDonViTinh().getTenDonViTinh());
                    ctDto.setDonViTinh(dvt);
                }
                chiTietList.add(ctDto);
            }
        }
        dto.setChiTietPhieuHuyList(chiTietList);
        dto.setTongTien(chiTietList.stream().mapToDouble(ChiTietPhieuHuyDTO::getThanhTien).sum());
        return dto;
    }

    private PhieuTraDTO toPhieuTraDTO(PhieuTra pt) {
        PhieuTraDTO dto = new PhieuTraDTO();
        dto.setMaPhieuTra(pt.getMaPhieuTra());
        if (pt.getKhachHang() != null) {
            dto.setMaKhachHang(pt.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(pt.getKhachHang().getTenKhachHang());
            dto.setSoDienThoai(pt.getKhachHang().getSoDienThoai());
        }
        if (pt.getNhanVien() != null) {
            dto.setMaNhanVien(pt.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(pt.getNhanVien().getTenNhanVien());
        }
        dto.setNgayLap(pt.getNgayLap() != null ? pt.getNgayLap().format(DISPLAY_DATE_FORMAT) : null);
        dto.setTrangThai(pt.isTrangThai());
        dto.setTongTienHoan(pt.getTongTienHoan());
        return dto;
    }

    private List<ChiTietPhieuTraDTO> toChiTietPhieuTraDTOList(java.util.List<?> dsChiTiet, String maPhieuTra) {
        List<ChiTietPhieuTraDTO> result = new ArrayList<>();
        if (dsChiTiet == null) {
            return result;
        }
        for (Object item : dsChiTiet) {
            if (item instanceof ChiTietPhieuTra ct) {
                ChiTietPhieuTraDTO dto = new ChiTietPhieuTraDTO();
                dto.setMaPhieuTra(maPhieuTra);
                if (ct.getChiTietHoaDon() != null) {
                    if (ct.getChiTietHoaDon().getHoaDon() != null) {
                        dto.setMaHoaDon(ct.getChiTietHoaDon().getHoaDon().getMaHoaDon());
                    }
                    if (ct.getChiTietHoaDon().getLoSanPham() != null) {
                        dto.setMaLo(ct.getChiTietHoaDon().getLoSanPham().getMaLo());
                    }
                    if (ct.getChiTietHoaDon().getSanPham() != null) {
                        dto.setTenSanPham(ct.getChiTietHoaDon().getSanPham().getTenSanPham());
                    }
                }
                dto.setSoLuong(ct.getSoLuong());
                dto.setThanhTienHoan(ct.getThanhTienHoan());
                dto.setLyDoChiTiet(ct.getLyDoChiTiet());
                dto.setTrangThai(ct.getTrangThai());
                if (ct.getDonViTinh() != null) {
                    dto.setMaDonViTinh(ct.getDonViTinh().getMaDonViTinh());
                    dto.setTenDonViTinh(ct.getDonViTinh().getTenDonViTinh());
                }
                result.add(dto);
            } else if (item instanceof ChiTietPhieuTraDTO dto) {
                result.add(dto);
            }
        }
        return result;
    }

    private PhieuNhapDTO toPhieuNhapDTO(PhieuNhap pn) {
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setMaPhieuNhap(pn.getMaPhieuNhap());
        dto.setNgayNhap(pn.getNgayNhap() != null ? pn.getNgayNhap().format(DISPLAY_DATE_FORMAT) : null);
        dto.setMaNhanVien(pn.getNhanVien() != null ? pn.getNhanVien().getMaNhanVien() : null);
        dto.setTenNhanVien(pn.getNhanVien() != null ? pn.getNhanVien().getTenNhanVien() : null);
        dto.setMaNhaCungCap(pn.getNhaCungCap() != null ? pn.getNhaCungCap().getMaNhaCungCap() : null);
        dto.setTenNhaCungCap(pn.getNhaCungCap() != null ? pn.getNhaCungCap().getTenNhaCungCap() : null);

        List<ChiTietPhieuNhapDTO> chiTietList = new ArrayList<>();
        if (pn.getChiTietPhieuNhapList() != null) {
            for (ChiTietPhieuNhap ct : pn.getChiTietPhieuNhapList()) {
                ChiTietPhieuNhapDTO ctDto = new ChiTietPhieuNhapDTO();
                ctDto.setMaPhieuNhap(pn.getMaPhieuNhap());
                if (ct.getLoSanPham() != null) {
                    ctDto.setMaLo(ct.getLoSanPham().getMaLo());
                    ctDto.setHanSuDung(ct.getLoSanPham().getHanSuDung() != null ? ct.getLoSanPham().getHanSuDung().format(DISPLAY_DATE_FORMAT) : null);
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
        dto.setChiTietList(chiTietList);
        dto.setSoDongChiTiet(chiTietList.size());
        dto.setTongTien(chiTietList.stream().mapToDouble(ChiTietPhieuNhapDTO::getThanhTien).sum());
        return dto;
    }

    private HoaDonCreateUpdateDTO toHoaDonCreateUpdateDTO(HoaDon hd) {
        HoaDonCreateUpdateDTO dto = new HoaDonCreateUpdateDTO();
        dto.setMaHoaDon(hd.getMaHoaDon());
        dto.setMaNhanVien(hd.getNhanVien() != null ? hd.getNhanVien().getMaNhanVien() : null);
        dto.setMaKhachHang(hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
        dto.setNgayLap(hd.getNgayLap() != null ? hd.getNgayLap().format(DISPLAY_DATE_FORMAT) : null);
        dto.setMaKhuyenMai(hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
        dto.setThuocKeDon(hd.isThuocKeDon());

        List<ChiTietHoaDonCreateUpdateDTO> chiTietList = new ArrayList<>();
        if (hd.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                ChiTietHoaDonCreateUpdateDTO ctDto = new ChiTietHoaDonCreateUpdateDTO();
                ctDto.setMaLo(ct.getLoSanPham() != null ? ct.getLoSanPham().getMaLo() : null);
                ctDto.setMaDonViTinh(ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh() : null);
                ctDto.setSoLuong(ct.getSoLuong());
                ctDto.setGiaBan(ct.getGiaBan());
                ctDto.setMaKhuyenMai(ct.getKhuyenMai() != null ? ct.getKhuyenMai().getMaKM() : null);
                chiTietList.add(ctDto);
            }
        }
        dto.setChiTietList(chiTietList);
        return dto;
    }
}

