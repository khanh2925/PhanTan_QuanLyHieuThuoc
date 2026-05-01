package network;

public enum CommandType {

    // ==================== KhachHang ====================
    KHACHHANG_LAY_TAT_CA,
    KHACHHANG_LAY_THEO_MA,          // data: String maKhachHang
    KHACHHANG_LAY_THEO_SDT,         // data: String soDienThoai
    KHACHHANG_TIM_KIEM,             // data: String tuKhoa
    KHACHHANG_THEM,                 // data: KhachHang
    KHACHHANG_CAP_NHAT,             // data: KhachHang
    KHACHHANG_XOA,                  // data: String maKhachHang
    KHACHHANG_TAO_MA,

    // ==================== NhanVien ====================
    NHANVIEN_LAY_TAT_CA,
    NHANVIEN_LAY_THEO_MA,           // data: String maNhanVien
    NHANVIEN_TIM_KIEM,              // data: String tuKhoa
    NHANVIEN_TIM_THEO_SDT,          // data: String soDienThoai
    NHANVIEN_THEM,                  // data: NhanVien
    NHANVIEN_CAP_NHAT,              // data: NhanVien
    NHANVIEN_XOA,                   // data: String maNhanVien
    NHANVIEN_CAP_NHAT_TRANG_THAI,   // data: Object[] { String maNhanVien, boolean trangThai }
    NHANVIEN_TAO_MA,

    // ==================== TaiKhoan ====================
    TAIKHOAN_DANG_NHAP,             // data: Object[] { String tenDangNhap, String matKhau }
    TAIKHOAN_DOI_MAT_KHAU,          // data: Object[] { String maTaiKhoan, String matKhauCu, String matKhauMoi }
    TAIKHOAN_TAO_MOI,               // data: TaiKhoan
    TAIKHOAN_CAP_NHAT,              // data: TaiKhoan
    TAIKHOAN_DANG_XUAT,

    // ==================== SanPham ====================
    SANPHAM_LAY_TAT_CA,
    SANPHAM_LAY_THEO_MA,            // data: String maSanPham
    SANPHAM_TIM_THEO_SO_DANG_KY,    // data: String soDangKy
    SANPHAM_TIM_KIEM,               // data: String tuKhoa
    SANPHAM_LAY_THEO_LOAI,          // data: LoaiSanPham
    SANPHAM_THEM,                   // data: SanPham
    SANPHAM_CAP_NHAT,               // data: SanPham
    SANPHAM_XOA,                    // data: String maSanPham
    SANPHAM_TAO_MA,
    SANPHAM_LAY_KHUYEN_MAI,         // data: String maSanPham

    // ==================== HoaDon ====================
    HOADON_LAY_TAT_CA,
    HOADON_LAY_THEO_MA,             // data: String maHoaDon
    HOADON_LAY_THEO_NGAY,           // data: LocalDate ngay
    HOADON_LAY_THEO_KHOANG_TG,      // data: Object[] { LocalDate tuNgay, LocalDate denNgay }
    HOADON_LAY_THEO_NHANVIEN,       // data: String maNhanVien
    HOADON_LAY_THEO_KHACHHANG,      // data: String maKhachHang
    HOADON_THEM,                    // data: HoaDon
    HOADON_CAP_NHAT,                // data: HoaDon
    HOADON_LAY_CHI_TIET,            // data: String maHoaDon
    HOADON_TAO_MA,

    // ==================== KhuyenMai ====================
    KHUYENMAI_LAY_TAT_CA,
    KHUYENMAI_LAY_THEO_MA,          // data: String maKM
    KHUYENMAI_LAY_DANG_HOAT_DONG,
    KHUYENMAI_LAY_CHI_TIET,         // data: String maKM
    KHUYENMAI_THEM,                 // data: KhuyenMai
    KHUYENMAI_CAP_NHAT,             // data: KhuyenMai
    KHUYENMAI_XOA,                  // data: String maKM
    KHUYENMAI_TAO_MA,

    // ==================== LoSanPham ====================
    LOSANPHAM_LAY_TAT_CA,
    LOSANPHAM_LAY_THEO_MA,          // data: String maLo
    LOSANPHAM_LAY_THEO_MA_SP,       // data: String maSanPham
    LOSANPHAM_LAY_CON_HANG,         // data: String maSanPham
    LOSANPHAM_LAY_SAP_HET_HAN,      // data: int soNgay
    LOSANPHAM_LAY_TON_KHO_THAP,     // data: int nguongTon
    LOSANPHAM_THEM,                 // data: LoSanPham
    LOSANPHAM_CAP_NHAT,             // data: LoSanPham
    LOSANPHAM_CAP_NHAT_SO_LUONG,    // data: Object[] { String maLo, int soLuongMoi }
    LOSANPHAM_TAO_MA,               // data: String maSanPham

    // ==================== PhieuNhap ====================
    PHIEUNHAP_LAY_TAT_CA,
    PHIEUNHAP_LAY_THEO_MA,          // data: String maPhieuNhap
    PHIEUNHAP_LAY_THEO_NGAY,        // data: LocalDate ngay
    PHIEUNHAP_LAY_THEO_NCC,         // data: String maNhaCungCap
    PHIEUNHAP_THEM,                 // data: PhieuNhap
    PHIEUNHAP_LAY_CHI_TIET,         // data: String maPhieuNhap
    PHIEUNHAP_TAO_MA
}
