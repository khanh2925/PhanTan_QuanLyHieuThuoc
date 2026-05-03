package mapper;

import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;
import dto.ChiTietHoaDonCreateUpdateDTO;
import dto.ChiTietHoaDonDTO;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.ChiTietPhieuNhapDTO;
import dto.HoaDonCreateUpdateDTO;
import dto.HoaDonDTO;
import dto.KhachHangDTO;
import dto.KhuyenMaiDTO;
import dto.LoSanPhamDTO;
import dto.NhanVienDTO;
import dto.PhieuNhapDTO;
import dto.SanPhamDTO;
import dto.PhieuTraDTO;
import dto.TaiKhoanDTO;
import entity.ChiTietHoaDon;
import entity.ChiTietKhuyenMaiSanPham;
import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.DuongDung;
import entity.HinhThucKM;
import entity.HoaDon;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.LoaiSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.PhieuTra;
import entity.SanPham;
import entity.TaiKhoan;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class Mapper {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Mapper() {
    }

    public static <T> T map(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        if (targetClass.isInstance(source)) {
            return targetClass.cast(source);
        }

        Object mapped = mapKnownType(source, targetClass);
        if (mapped != null) {
            return targetClass.cast(mapped);
        }

        Object reflected = mapWithStaticFromEntity(source, targetClass);
        if (reflected != null) {
            return targetClass.cast(reflected);
        }

        throw new IllegalArgumentException("No mapper registered from "
                + source.getClass().getName() + " to " + targetClass.getName());
    }

    public static <T> List<T> mapList(Collection<?> sources, Class<T> targetClass) {
        if (sources == null) {
            return List.of();
        }
        return sources.stream()
                .filter(Objects::nonNull)
                .map(item -> map(item, targetClass))
                .toList();
    }

    private static Object mapKnownType(Object source, Class<?> targetClass) {
        // DTO → Entity mappings
        if (targetClass == KhachHang.class && source instanceof KhachHangDTO dto) return toKhachHang(dto);
        if (targetClass == NhanVien.class && source instanceof NhanVienDTO dto) return toNhanVien(dto);
        if (targetClass == SanPham.class && source instanceof SanPhamDTO dto) return toSanPham(dto);
        if (targetClass == HoaDon.class && source instanceof HoaDonCreateUpdateDTO dto) return toHoaDon(dto);
        if (targetClass == LoSanPham.class && source instanceof LoSanPhamDTO dto) return toLoSanPham(dto);
        if (targetClass == KhuyenMai.class && source instanceof KhuyenMaiDTO dto) return toKhuyenMai(dto);
        if (targetClass == ChiTietKhuyenMaiSanPham.class && source instanceof ChiTietKhuyenMaiSanPhamDTO dto) return toChiTietKhuyenMaiSanPham(dto);
        if (targetClass == PhieuNhap.class && source instanceof PhieuNhapDTO dto) return toPhieuNhap(dto);
        if (targetClass == ChiTietPhieuNhap.class && source instanceof ChiTietPhieuNhapDTO dto) return toChiTietPhieuNhap(dto, null);
        if (targetClass == TaiKhoan.class && source instanceof TaiKhoanDTO dto) return toTaiKhoan(dto);
        if (targetClass == BangGia.class && source instanceof BangGiaDTO dto) return toBangGia(dto);
        if (targetClass == ChiTietBangGia.class && source instanceof ChiTietBangGiaDTO dto) return toChiTietBangGia(dto);
        // Entity → DTO mappings
        if (targetClass == TaiKhoanDTO.class && source instanceof TaiKhoan entity) return toTaiKhoanDTO(entity);
        if (targetClass == KhachHangDTO.class && source instanceof KhachHang entity) return toKhachHangDTO(entity);
        if (targetClass == PhieuTraDTO.class && source instanceof PhieuTra entity) return toPhieuTraDTO(entity);
        if (targetClass == NhanVienDTO.class && source instanceof NhanVien entity) return toNhanVienDTO(entity);
        if (targetClass == SanPhamDTO.class && source instanceof SanPham entity) return toSanPhamDTO(entity);
        if (targetClass == HoaDonDTO.class && source instanceof HoaDon entity) return toHoaDonDTO(entity);
        if (targetClass == ChiTietHoaDonDTO.class && source instanceof ChiTietHoaDon entity) return toChiTietHoaDonDTO(entity);
        if (targetClass == LoSanPhamDTO.class && source instanceof LoSanPham entity) return toLoSanPhamDTO(entity);
        if (targetClass == PhieuNhapDTO.class && source instanceof PhieuNhap entity) return toPhieuNhapDTO(entity);
        if (targetClass == ChiTietPhieuNhapDTO.class && source instanceof ChiTietPhieuNhap entity) return toChiTietPhieuNhapDTO(entity);
        if (targetClass == KhuyenMaiDTO.class && source instanceof KhuyenMai entity) return toKhuyenMaiDTO(entity);
        if (targetClass == ChiTietKhuyenMaiSanPhamDTO.class && source instanceof ChiTietKhuyenMaiSanPham entity) return toChiTietKhuyenMaiSanPhamDTO(entity);
        return null;
    }

    private static Object mapWithStaticFromEntity(Object source, Class<?> targetClass) {
        try {
            Method fromEntity = targetClass.getMethod("fromEntity", source.getClass());
            return fromEntity.invoke(null, source);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static KhachHang toKhachHang(KhachHangDTO dto) {
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(dto.getMaKhachHang());
        khachHang.setTenKhachHang(dto.getTenKhachHang());
        khachHang.setGioiTinh(isMale(dto.getGioiTinh()));
        khachHang.setSoDienThoai(dto.getSoDienThoai());
        khachHang.setNgaySinh(parseDisplayDate(dto.getNgaySinh()));
        khachHang.setHoatDong(true);
        return khachHang;
    }

    private static NhanVien toNhanVien(NhanVienDTO dto) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(dto.getMaNhanVien());
        nhanVien.setTenNhanVien(dto.getTenNhanVien());
        nhanVien.setGioiTinh(isMale(dto.getGioiTinh()));
        nhanVien.setNgaySinh(parseDisplayDate(dto.getNgaySinh()));
        nhanVien.setSoDienThoai(dto.getSoDienThoai());
        nhanVien.setDiaChi(dto.getDiaChi());
        nhanVien.setQuanLy(isManager(dto.getVaiTro()));
        nhanVien.setCaLam(parseCaLam(dto.getCaLam()));
        nhanVien.setTrangThai(isActive(dto.getTrangThai()));
        return nhanVien;
    }

    private static SanPham toSanPham(SanPhamDTO dto) {
        SanPham sanPham = new SanPham();
        sanPham.setMaSanPham(dto.getMaSanPham());
        sanPham.setTenSanPham(dto.getTenSanPham());
        sanPham.setLoaiSanPham(parseLoaiSanPham(dto.getLoaiSanPham()));
        sanPham.setSoDangKy(dto.getSoDangKy());
        sanPham.setDuongDung(parseDuongDung(dto.getDuongDung()));
        if (dto.getGiaNhap() > 0) {
            sanPham.setGiaNhap(dto.getGiaNhap());
        }
        sanPham.setGiaBan(dto.getGiaBan());
        sanPham.setHinhAnh(dto.getHinhAnh());
        sanPham.setKeBanSanPham(dto.getKeBanSanPham());
        sanPham.setHoatDong(dto.isHoatDong());
        return sanPham;
    }

    private static LoSanPham toLoSanPham(LoSanPhamDTO dto) {
        LoSanPham loSanPham = new LoSanPham();
        loSanPham.setMaLo(dto.getMaLo());
        loSanPham.setHanSuDung(parseDisplayDate(dto.getHanSuDung()));
        loSanPham.setSoLuongTon(dto.getSoLuongTon());
        if (hasText(dto.getMaSanPham())) {
            loSanPham.setSanPham(new SanPham(dto.getMaSanPham()));
        }
        return loSanPham;
    }

    private static KhuyenMai toKhuyenMai(KhuyenMaiDTO dto) {
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setMaKM(dto.getMaKM());
        khuyenMai.setTenKM(dto.getTenKM());
        khuyenMai.setNgayBatDau(parseDisplayDateOrToday(dto.getNgayBatDau()));
        khuyenMai.setNgayKetThuc(parseDisplayDateOrToday(dto.getNgayKetThuc()));
        khuyenMai.setTrangThai(dto.isTrangThai());
        khuyenMai.setKhuyenMaiHoaDon(dto.isKhuyenMaiHoaDon());
        khuyenMai.setHinhThuc(parseHinhThucKM(dto.getHinhThuc()));
        khuyenMai.setGiaTri(dto.getGiaTri());
        khuyenMai.setDieuKienApDungHoaDon(dto.getDieuKienApDungHoaDon());
        khuyenMai.setSoLuongKhuyenMai(dto.getSoLuongKhuyenMai());
        return khuyenMai;
    }

    private static BangGia toBangGia(BangGiaDTO dto) {
        BangGia bangGia = new BangGia();
        bangGia.setMaBangGia(dto.getMaBangGia());
        if (hasText(dto.getMaNhanVien())) {
            bangGia.setNhanVien(new NhanVien(dto.getMaNhanVien()));
        }
        bangGia.setTenBangGia(dto.getTenBangGia());
        bangGia.setNgayApDung(dto.getNgayApDung());
        bangGia.setHoatDong(dto.isHoatDong());
        return bangGia;
    }

    private static ChiTietBangGia toChiTietBangGia(ChiTietBangGiaDTO dto) {
        ChiTietBangGia ct = new ChiTietBangGia();
        BangGia bg = new BangGia();
        bg.setMaBangGia(dto.getMaBangGia());
        ct.setBangGia(bg);
        ct.setGiaTu(dto.getGiaTu());
        ct.setGiaDen(dto.getGiaDen());
        ct.setTiLe(dto.getTiLe());
        return ct;
    }

    private static ChiTietKhuyenMaiSanPham toChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPhamDTO dto) {
        ChiTietKhuyenMaiSanPham chiTiet = new ChiTietKhuyenMaiSanPham();
        chiTiet.setSanPham(new SanPham(dto.getMaSanPham()));
        chiTiet.setKhuyenMai(new KhuyenMai(dto.getMaKM()));
        return chiTiet;
    }

    private static PhieuNhap toPhieuNhap(PhieuNhapDTO dto) {
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setMaPhieuNhap(dto.getMaPhieuNhap());
        phieuNhap.setNgayNhap(parseDisplayDateOrToday(dto.getNgayNhap()));
        phieuNhap.setNhaCungCap(new NhaCungCap(dto.getMaNhaCungCap()));
        phieuNhap.setNhanVien(new NhanVien(dto.getMaNhanVien()));
        List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();
        if (dto.getChiTietList() != null) {
            for (ChiTietPhieuNhapDTO chiTietDTO : dto.getChiTietList()) {
                chiTietList.add(toChiTietPhieuNhap(chiTietDTO, phieuNhap));
            }
        }
        phieuNhap.setChiTietPhieuNhapList(chiTietList);
        if (dto.getTongTien() > 0 && chiTietList.isEmpty()) {
            phieuNhap.setTongTien(dto.getTongTien());
        }
        return phieuNhap;
    }

    private static ChiTietPhieuNhap toChiTietPhieuNhap(ChiTietPhieuNhapDTO dto, PhieuNhap phieuNhap) {
        ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
        PhieuNhap owner = phieuNhap;
        if (owner == null && hasText(dto.getMaPhieuNhap())) {
            owner = new PhieuNhap();
            owner.setMaPhieuNhap(dto.getMaPhieuNhap());
            owner.setNgayNhap(LocalDate.now());
            owner.setNhaCungCap(new NhaCungCap("NCC-20000101-0001"));
            owner.setNhanVien(new NhanVien("NV-20000101-0001"));
        }
        if (owner != null) {
            chiTiet.setPhieuNhap(owner);
        }
        LoSanPham loSanPham = new LoSanPham(dto.getMaLo());
        if (hasText(dto.getHanSuDung())) {
            loSanPham.setHanSuDung(parseDisplayDate(dto.getHanSuDung()));
        }
        loSanPham.setSoLuongTon(dto.getSoLuongTon() > 0 ? dto.getSoLuongTon() : dto.getSoLuongNhap());
        if (hasText(dto.getMaSanPham())) {
            loSanPham.setSanPham(new SanPham(dto.getMaSanPham()));
        }
        chiTiet.setLoSanPham(loSanPham);
        chiTiet.setDonViTinh(new DonViTinh(dto.getMaDonViTinh()));
        chiTiet.setSoLuongNhap(dto.getSoLuongNhap());
        chiTiet.setDonGiaNhap(dto.getDonGiaNhap());
        return chiTiet;
    }

    private static TaiKhoan toTaiKhoan(TaiKhoanDTO dto) {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(dto.getMaTaiKhoan());
        taiKhoan.setTenDangNhap(dto.getTenDangNhap());
        taiKhoan.setMatKhau(dto.getMatKhau());
        taiKhoan.setNhanVien(new NhanVien(dto.getMaNhanVien()));
        return taiKhoan;
    }

    private static TaiKhoanDTO toTaiKhoanDTO(TaiKhoan tk) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setMaTaiKhoan(tk.getMaTaiKhoan());
        dto.setTenDangNhap(tk.getTenDangNhap());
        dto.setMatKhau(tk.getMatKhau());
        if (tk.getNhanVien() != null) {
            dto.setMaNhanVien(tk.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(tk.getNhanVien().getTenNhanVien());
            dto.setVaiTro(tk.getNhanVien().isQuanLy() ? "Quản lý" : "Nhân viên");
            dto.setNhanVienDangLam(tk.getNhanVien().isTrangThai());
        }
        return dto;
    }

    private static PhieuTraDTO toPhieuTraDTO(PhieuTra pt) {
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
        dto.setNgayLap(pt.getNgayLap() != null ? pt.getNgayLap().toString() : null);
        dto.setTrangThai(pt.isTrangThai());
        dto.setTongTienHoan(pt.getTongTienHoan());
        return dto;
    }

    private static NhanVienDTO toNhanVienDTO(NhanVien nv) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(nv.getMaNhanVien());
        dto.setTenNhanVien(nv.getTenNhanVien());
        dto.setGioiTinh(nv.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(formatDate(nv.getNgaySinh()));
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setDiaChi(nv.getDiaChi());
        dto.setVaiTro(nv.isQuanLy() ? "Quản lý" : "Nhân viên");
        dto.setCaLam(formatCaLam(nv.getCaLam()));
        dto.setTrangThai(nv.isTrangThai() ? "Đang làm" : "Nghỉ việc");
        return dto;
    }

    private static SanPhamDTO toSanPhamDTO(SanPham sp) {
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

        ChiTietKhuyenMaiSanPham kmHienTai = sp.getKhuyenMaiHienTai();
        if (kmHienTai != null && kmHienTai.getKhuyenMai() != null) {
            KhuyenMai km = kmHienTai.getKhuyenMai();
            dto.setTenKhuyenMai(km.getTenKM());
            if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                dto.setPhanTramGiam(km.getGiaTri());
            }
        }
        return dto;
    }

    private static HoaDonDTO toHoaDonDTO(HoaDon hd) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setMaHoaDon(hd.getMaHoaDon());
        if (hd.getKhachHang() != null) {
            dto.setTenKhachHang(hd.getKhachHang().getTenKhachHang());
            dto.setSdtKhachHang(hd.getKhachHang().getSoDienThoai());
        }
        if (hd.getNhanVien() != null) {
            dto.setTenNhanVien(hd.getNhanVien().getTenNhanVien());
        }
        dto.setNgayLap(formatDate(hd.getNgayLap()));
        dto.setTongTien(hd.getTongTien());
        dto.setGiamGia(hd.getSoTienGiamKhuyenMai());
        dto.setThanhToan(hd.getTongThanhToan());

        List<ChiTietHoaDon> chiTiet = hd.getDanhSachChiTiet();
        if (chiTiet != null && !chiTiet.isEmpty()) {
            dto.setChiTietList(mapList(chiTiet, ChiTietHoaDonDTO.class));
            int soSanPham = 0;
            for (ChiTietHoaDon ct : chiTiet) {
                soSanPham += (int) Math.round(ct.getSoLuong());
            }
            dto.setSoSanPham(soSanPham);
        } else {
            dto.setSoSanPham(0);
        }
        return dto;
    }

    private static ChiTietHoaDonDTO toChiTietHoaDonDTO(ChiTietHoaDon ct) {
        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
        dto.setMaLo(ct.getLoSanPham() != null ? ct.getLoSanPham().getMaLo() : null);
        dto.setTenSanPham(ct.getSanPham() != null ? ct.getSanPham().getTenSanPham() : null);
        dto.setDonViTinh(ct.getDonViTinh() != null ? ct.getDonViTinh().getTenDonViTinh() : null);
        dto.setSoLuong((int) Math.round(ct.getSoLuong()));
        dto.setDonGia(ct.getGiaBan());
        dto.setThanhTien(ct.getThanhTien());

        if (ct.getKhuyenMai() != null) {
            dto.setTenKhuyenMai(ct.getKhuyenMai().getTenKM());
            double tongGoc = ct.getSoLuong() * ct.getGiaBan();
            dto.setGiamGia(Math.max(0, tongGoc - ct.getThanhTien()));
        }
        return dto;
    }

    private static LoSanPhamDTO toLoSanPhamDTO(LoSanPham lo) {
        LoSanPhamDTO dto = new LoSanPhamDTO();
        dto.setMaLo(lo.getMaLo());
        dto.setHanSuDung(formatDate(lo.getHanSuDung()));
        dto.setSoLuongTon(lo.getSoLuongTon());
        if (lo.getSanPham() != null) {
            dto.setMaSanPham(lo.getSanPham().getMaSanPham());
            dto.setTenSanPham(lo.getSanPham().getTenSanPham());
        }

        if (lo.getHanSuDung() != null) {
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), lo.getHanSuDung());
            dto.setSoNgayConLai((int) daysLeft);
            dto.setHetHan(lo.getHanSuDung().isBefore(LocalDate.now()));
            dto.setSapHetHan(!dto.isHetHan() && daysLeft <= 30);
            if (dto.isHetHan()) {
                dto.setTrangThai("Hết hạn");
            } else if (dto.isSapHetHan()) {
                dto.setTrangThai("Sắp hết hạn");
            } else {
                dto.setTrangThai("Còn hạn");
            }
        }
        return dto;
    }

    private static PhieuNhapDTO toPhieuNhapDTO(PhieuNhap pn) {
        PhieuNhapDTO dto = new PhieuNhapDTO();
        dto.setMaPhieuNhap(pn.getMaPhieuNhap());
        dto.setNgayNhap(formatDate(pn.getNgayNhap()));
        if (pn.getNhaCungCap() != null) {
            dto.setMaNhaCungCap(pn.getNhaCungCap().getMaNhaCungCap());
            dto.setTenNhaCungCap(pn.getNhaCungCap().getTenNhaCungCap());
        }
        if (pn.getNhanVien() != null) {
            dto.setMaNhanVien(pn.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(pn.getNhanVien().getTenNhanVien());
        }
        dto.setTongTien(pn.getTongTien());

        List<ChiTietPhieuNhap> chiTiet = pn.getChiTietPhieuNhapList();
        if (chiTiet != null && !chiTiet.isEmpty()) {
            dto.setChiTietList(mapList(chiTiet, ChiTietPhieuNhapDTO.class));
            dto.setSoDongChiTiet(chiTiet.size());
        } else {
            dto.setSoDongChiTiet(0);
        }
        return dto;
    }

    private static ChiTietPhieuNhapDTO toChiTietPhieuNhapDTO(ChiTietPhieuNhap ct) {
        ChiTietPhieuNhapDTO dto = new ChiTietPhieuNhapDTO();
        if (ct.getPhieuNhap() != null) {
            dto.setMaPhieuNhap(ct.getPhieuNhap().getMaPhieuNhap());
        }
        if (ct.getLoSanPham() != null) {
            dto.setMaLo(ct.getLoSanPham().getMaLo());
            dto.setHanSuDung(formatDate(ct.getLoSanPham().getHanSuDung()));
            dto.setSoLuongTon(ct.getLoSanPham().getSoLuongTon());
            if (ct.getLoSanPham().getSanPham() != null) {
                dto.setMaSanPham(ct.getLoSanPham().getSanPham().getMaSanPham());
                dto.setTenSanPham(ct.getLoSanPham().getSanPham().getTenSanPham());
            }
        }
        if (ct.getDonViTinh() != null) {
            dto.setMaDonViTinh(ct.getDonViTinh().getMaDonViTinh());
            dto.setTenDonViTinh(ct.getDonViTinh().getTenDonViTinh());
        }
        dto.setSoLuongNhap(ct.getSoLuongNhap());
        dto.setDonGiaNhap(ct.getDonGiaNhap());
        dto.setThanhTien(ct.getThanhTien());
        return dto;
    }

    private static KhuyenMaiDTO toKhuyenMaiDTO(KhuyenMai km) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setMaKM(km.getMaKM());
        dto.setTenKM(km.getTenKM());
        dto.setNgayBatDau(formatDate(km.getNgayBatDau()));
        dto.setNgayKetThuc(formatDate(km.getNgayKetThuc()));
        dto.setTrangThai(km.isTrangThai());
        dto.setKhuyenMaiHoaDon(km.isKhuyenMaiHoaDon());
        dto.setLoaiKhuyenMai(km.isKhuyenMaiHoaDon() ? "Hóa đơn" : "Sản phẩm");
        dto.setHinhThuc(km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : null);
        dto.setGiaTri(km.getGiaTri());
        dto.setDieuKienApDungHoaDon(km.getDieuKienApDungHoaDon());
        dto.setSoLuongKhuyenMai(km.getSoLuongKhuyenMai());
        dto.setDangHoatDong(km.isDangHoatDong());
        return dto;
    }

    private static ChiTietKhuyenMaiSanPhamDTO toChiTietKhuyenMaiSanPhamDTO(ChiTietKhuyenMaiSanPham ct) {
        ChiTietKhuyenMaiSanPhamDTO dto = new ChiTietKhuyenMaiSanPhamDTO();
        if (ct.getKhuyenMai() != null) {
            dto.setMaKM(ct.getKhuyenMai().getMaKM());
            dto.setTenKM(ct.getKhuyenMai().getTenKM());
            dto.setHinhThuc(ct.getKhuyenMai().getHinhThuc() != null ? ct.getKhuyenMai().getHinhThuc().getMoTa() : null);
            dto.setGiaTri(ct.getKhuyenMai().getGiaTri());
            dto.setNgayBatDau(formatDate(ct.getKhuyenMai().getNgayBatDau()));
            dto.setNgayKetThuc(formatDate(ct.getKhuyenMai().getNgayKetThuc()));
            dto.setDangHoatDong(ct.getKhuyenMai().isDangHoatDong());
        }
        if (ct.getSanPham() != null) {
            dto.setMaSanPham(ct.getSanPham().getMaSanPham());
            dto.setTenSanPham(ct.getSanPham().getTenSanPham());
        }
        return dto;
    }

    private static KhachHangDTO toKhachHangDTO(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(kh.getMaKhachHang());
        dto.setTenKhachHang(kh.getTenKhachHang());
        dto.setSoDienThoai(kh.getSoDienThoai());
        dto.setGioiTinh(kh.isGioiTinh() ? "Nam" : "Nữ");
        dto.setNgaySinh(formatDate(kh.getNgaySinh()));
        return dto;
    }

    private static HoaDon toHoaDon(HoaDonCreateUpdateDTO dto) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(dto.getMaHoaDon());
        hoaDon.setNhanVien(new NhanVien(dto.getMaNhanVien()));
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(dto.getMaKhachHang());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNgayLap(parseDisplayDateOrToday(dto.getNgayLap()));
        hoaDon.setThuocKeDon(dto.isThuocKeDon());
        if (hasText(dto.getMaKhuyenMai())) {
            hoaDon.setKhuyenMai(new KhuyenMai(dto.getMaKhuyenMai()));
        }
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        if (dto.getChiTietList() != null) {
            for (ChiTietHoaDonCreateUpdateDTO chiTietDTO : dto.getChiTietList()) {
                chiTietList.add(toChiTietHoaDon(chiTietDTO, hoaDon));
            }
        }
        hoaDon.setDanhSachChiTiet(chiTietList);
        return hoaDon;
    }

    private static ChiTietHoaDon toChiTietHoaDon(ChiTietHoaDonCreateUpdateDTO dto, HoaDon hoaDon) {
        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setHoaDon(hoaDon);
        chiTiet.setLoSanPham(new LoSanPham(dto.getMaLo()));
        chiTiet.setDonViTinh(new DonViTinh(dto.getMaDonViTinh()));
        chiTiet.setSoLuong(dto.getSoLuong());
        chiTiet.setGiaBan(dto.getGiaBan());
        if (hasText(dto.getMaKhuyenMai())) {
            chiTiet.setKhuyenMai(new KhuyenMai(dto.getMaKhuyenMai()));
        }
        return chiTiet;
    }

    private static LocalDate parseDisplayDate(String value) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DISPLAY_DATE_FORMAT);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(value.trim());
        }
    }

    private static LocalDate parseDisplayDateOrToday(String value) {
        LocalDate parsed = parseDisplayDate(value);
        return parsed != null ? parsed : LocalDate.now();
    }

    private static String formatDate(LocalDate value) {
        if (value == null) {
            return null;
        }
        return DISPLAY_DATE_FORMAT.format(value);
    }

    private static String formatCaLam(int caLam) {
        return switch (caLam) {
            case 1 -> "Ca sáng (6h-14h)";
            case 2 -> "Ca chiều (14h-22h)";
            case 3 -> "Ca tối (22h-6h)";
            default -> "Chưa xác định";
        };
    }

    private static boolean isMale(String value) {
        if (!hasText(value)) {
            return true;
        }
        String normalized = normalize(value);
        return normalized.equals("nam") || normalized.equals("male") || normalized.equals("true") || normalized.equals("1");
    }

    private static boolean isManager(String value) {
        if (!hasText(value)) {
            return false;
        }
        String normalized = normalize(value);
        return normalized.contains("quan") || normalized.contains("manager") || normalized.equals("true") || normalized.equals("1");
    }

    private static boolean isActive(String value) {
        if (!hasText(value)) {
            return true;
        }
        String normalized = normalize(value);
        return !(normalized.contains("nghi") || normalized.equals("false") || normalized.equals("0"));
    }

    private static int parseCaLam(String value) {
        if (!hasText(value)) {
            return 1;
        }
        String normalized = normalize(value);
        if (normalized.contains("2") || normalized.contains("chieu")) {
            return 2;
        }
        if (normalized.contains("3") || normalized.contains("toi")) {
            return 3;
        }
        return 1;
    }

    private static LoaiSanPham parseLoaiSanPham(String value) {
        if (!hasText(value)) {
            return LoaiSanPham.SAN_PHAM_KHAC;
        }
        String normalized = normalize(value);
        for (LoaiSanPham loaiSanPham : LoaiSanPham.values()) {
            if (normalize(loaiSanPham.name()).equals(normalized)
                    || normalize(loaiSanPham.getTenLoai()).equals(normalized)) {
                return loaiSanPham;
            }
        }
        return LoaiSanPham.SAN_PHAM_KHAC;
    }

    private static DuongDung parseDuongDung(String value) {
        if (!hasText(value)) {
            return DuongDung.KHAC;
        }
        String normalized = normalize(value);
        for (DuongDung duongDung : DuongDung.values()) {
            if (normalize(duongDung.name()).equals(normalized)
                    || normalize(duongDung.getTenDuongDung()).equals(normalized)) {
                return duongDung;
            }
        }
        return DuongDung.KHAC;
    }

    private static HinhThucKM parseHinhThucKM(String value) {
        if (!hasText(value)) {
            return HinhThucKM.GIAM_GIA_PHAN_TRAM;
        }
        String normalized = normalize(value);
        for (HinhThucKM hinhThuc : HinhThucKM.values()) {
            if (normalize(hinhThuc.name()).equals(normalized)
                    || normalize(hinhThuc.getMoTa()).equals(normalized)) {
                return hinhThuc;
            }
        }
        if (normalized.contains("tien") || normalized.contains("cash") || normalized.contains("money")) {
            return HinhThucKM.GIAM_GIA_TIEN;
        }
        return HinhThucKM.GIAM_GIA_PHAN_TRAM;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
