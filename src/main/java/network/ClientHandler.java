package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;

import dto.*;
import entity.*;
import entity.LoaiSanPham;
import service.*;
import service.impl.*;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private final KhachHangService khachHangService;
    private final NhanVienService nhanVienService;
    private final TaiKhoanService taiKhoanService;
    private final SanPhamService sanPhamService;
    private final HoaDonService hoaDonService;
    private final KhuyenMaiService khuyenMaiService;
    private final LoSanPhamService loSanPhamService;
    private final PhieuNhapService phieuNhapService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.khachHangService = new KhachHangServiceImpl();
        this.nhanVienService = new NhanVienServiceImpl();
        this.taiKhoanService = new TaiKhoanServiceImpl();
        this.sanPhamService = new SanPhamServiceImpl();
        this.hoaDonService = new HoaDonServiceImpl();
        this.khuyenMaiService = new KhuyenMaiServiceImpl();
        this.loSanPhamService = new LoSanPhamServiceImpl();
        this.phieuNhapService = new PhieuNhapServiceImpl();
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            while (true) {
                Request request = (Request) in.readObject();
                Response response = handleRequest(request);
                out.writeObject(response);
                out.flush();
                out.reset();
            }
        } catch (Exception ex) {
            System.err.println("Client disconnected: " + socket.getInetAddress() + " - " + ex.getMessage());
        }
    }

    private Response handleRequest(Request request) {
        CommandType cmd = request.getCommandType();
        Object data = request.getData();
        Response response = new Response();
        try {
            switch (cmd) {

                // ==================== KhachHang ====================
                case KHACHHANG_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(khachHangService.layTatCaKhachHang());
                }
                case KHACHHANG_LAY_THEO_MA -> {
                    KhachHangDTO kh = khachHangService.timKhachHangTheoMa((String) data);
                    response.setSuccess(kh != null);
                    response.setData(kh);
                    response.setMessage(kh != null ? "Tìm thấy" : "Không tìm thấy khách hàng");
                }
                case KHACHHANG_LAY_THEO_SDT -> {
                    KhachHangDTO kh = khachHangService.timKhachHangTheoSoDienThoai((String) data);
                    response.setSuccess(kh != null);
                    response.setData(kh);
                    response.setMessage(kh != null ? "Tìm thấy" : "Không tìm thấy khách hàng");
                }
                case KHACHHANG_TIM_KIEM -> {
                    response.setSuccess(true);
                    response.setData(khachHangService.timKiemKhachHang((String) data));
                }
                case KHACHHANG_THEM -> {
                    boolean ok = khachHangService.themKhachHang((KhachHangDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm khách hàng thành công" : "Thêm khách hàng thất bại");
                }
                case KHACHHANG_CAP_NHAT -> {
                    boolean ok = khachHangService.capNhatKhachHang((KhachHangDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case KHACHHANG_XOA -> {
                    boolean ok = khachHangService.xoaKhachHang((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa thành công" : "Xóa thất bại");
                }
                case KHACHHANG_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(khachHangService.taoMaKhachHangTuDong());
                }

                // ==================== NhanVien ====================
                case NHANVIEN_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(nhanVienService.layTatCaNhanVien());
                }
                case NHANVIEN_LAY_THEO_MA -> {
                    NhanVienDTO nv = nhanVienService.timNhanVienTheoMa((String) data);
                    response.setSuccess(nv != null);
                    response.setData(nv);
                    response.setMessage(nv != null ? "Tìm thấy" : "Không tìm thấy nhân viên");
                }
                case NHANVIEN_TIM_KIEM -> {
                    response.setSuccess(true);
                    response.setData(nhanVienService.timNhanVien((String) data));
                }
                case NHANVIEN_TIM_THEO_SDT -> {
                    response.setSuccess(true);
                    response.setData(nhanVienService.timNhanVienTheoSoDienThoai((String) data));
                }
                case NHANVIEN_THEM -> {
                    boolean ok = nhanVienService.themNhanVien((NhanVienDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm nhân viên thành công" : "Thêm nhân viên thất bại");
                }
                case NHANVIEN_CAP_NHAT -> {
                    boolean ok = nhanVienService.capNhatNhanVien((NhanVienDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case NHANVIEN_XOA -> {
                    boolean ok = nhanVienService.xoaNhanVien((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa thành công" : "Xóa thất bại");
                }
                case NHANVIEN_CAP_NHAT_TRANG_THAI -> {
                    // data: Object[] { String maNhanVien, boolean trangThai }
                    Object[] params = (Object[]) data;
                    boolean ok = nhanVienService.capNhatTrangThai((String) params[0], (Boolean) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật trạng thái thành công" : "Cập nhật trạng thái thất bại");
                }
                case NHANVIEN_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(nhanVienService.taoMaNhanVienTuDong());
                }

                // ==================== TaiKhoan ====================
                case TAIKHOAN_DANG_NHAP -> {
                    // data: Object[] { String tenDangNhap, String matKhau }
                    Object[] params = (Object[]) data;
                    TaiKhoanDTO tk = taiKhoanService.dangNhap((String) params[0], (String) params[1]);
                    response.setSuccess(tk != null);
                    response.setData(tk);
                    response.setMessage(tk != null ? "Đăng nhập thành công" : "Tên đăng nhập hoặc mật khẩu không đúng");
                }
                case TAIKHOAN_DOI_MAT_KHAU -> {
                    // data: Object[] { String maTaiKhoan, String matKhauCu, String matKhauMoi }
                    Object[] params = (Object[]) data;
                    boolean ok = taiKhoanService.doiMatKhau((String) params[0], (String) params[1], (String) params[2]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Đổi mật khẩu thành công" : "Đổi mật khẩu thất bại");
                }
                case TAIKHOAN_TAO_MOI -> {
                    boolean ok = taiKhoanService.taoTaiKhoan((TaiKhoanDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Tạo tài khoản thành công" : "Tạo tài khoản thất bại");
                }
                case TAIKHOAN_CAP_NHAT -> {
                    boolean ok = taiKhoanService.capNhatTaiKhoan((TaiKhoanDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật tài khoản thành công" : "Cập nhật thất bại");
                }
                case TAIKHOAN_DANG_XUAT -> {
                    taiKhoanService.dangXuat();
                    response.setSuccess(true);
                    response.setMessage("Đăng xuất thành công");
                }

                // ==================== SanPham ====================
                case SANPHAM_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(sanPhamService.layTatCaSanPham());
                }
                case SANPHAM_LAY_THEO_MA -> {
                    SanPhamDTO sp = sanPhamService.laySanPhamTheoMa((String) data);
                    response.setSuccess(sp != null);
                    response.setData(sp);
                    response.setMessage(sp != null ? "Tìm thấy" : "Không tìm thấy sản phẩm");
                }
                case SANPHAM_TIM_THEO_SO_DANG_KY -> {
                    SanPhamDTO sp = sanPhamService.timSanPhamTheoSoDangKy((String) data);
                    response.setSuccess(sp != null);
                    response.setData(sp);
                    response.setMessage(sp != null ? "Tìm thấy" : "Không tìm thấy sản phẩm");
                }
                case SANPHAM_TIM_KIEM -> {
                    response.setSuccess(true);
                    response.setData(sanPhamService.timKiemSanPham((String) data));
                }
                case SANPHAM_LAY_THEO_LOAI -> {
                    response.setSuccess(true);
                    response.setData(sanPhamService.laySanPhamTheoLoai((LoaiSanPham) data));
                }
                case SANPHAM_THEM -> {
                    boolean ok = sanPhamService.themSanPham((SanPhamDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm sản phẩm thành công" : "Thêm sản phẩm thất bại");
                }
                case SANPHAM_CAP_NHAT -> {
                    boolean ok = sanPhamService.capNhatSanPham((SanPhamDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case SANPHAM_XOA -> {
                    boolean ok = sanPhamService.xoaSanPham((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa thành công" : "Xóa thất bại");
                }
                case SANPHAM_TAO_MA -> {
                    // SanPhamService không có taoMa, dùng tạm format SP-
                    response.setSuccess(false);
                    response.setMessage("Chức năng tạo mã sản phẩm chưa được hỗ trợ qua mạng");
                }
                case SANPHAM_LAY_KHUYEN_MAI -> {
                    response.setSuccess(true);
                    response.setData(sanPhamService.layKhuyenMaiDangApDungChoSanPham((String) data));
                }

                // ==================== HoaDon ====================
                case HOADON_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.layTatCaHoaDon());
                }
                case HOADON_LAY_THEO_MA -> {
                    HoaDonDTO hd = hoaDonService.layHoaDonTheoMa((String) data);
                    response.setSuccess(hd != null);
                    response.setData(hd);
                    response.setMessage(hd != null ? "Tìm thấy" : "Không tìm thấy hóa đơn");
                }
                case HOADON_LAY_THEO_NGAY -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.layHoaDonTheoNgay((LocalDate) data));
                }
                case HOADON_LAY_THEO_KHOANG_TG -> {
                    // data: Object[] { LocalDate tuNgay, LocalDate denNgay }
                    Object[] params = (Object[]) data;
                    response.setSuccess(true);
                    response.setData(hoaDonService.layHoaDonTheoKhoangThoiGian((LocalDate) params[0], (LocalDate) params[1]));
                }
                case HOADON_LAY_THEO_NHANVIEN -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.layHoaDonTheoNhanVien((String) data));
                }
                case HOADON_LAY_THEO_KHACHHANG -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.layHoaDonTheoKhachHang((String) data));
                }
                case HOADON_THEM -> {
                    boolean ok = hoaDonService.themHoaDon((HoaDonCreateUpdateDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Tạo hóa đơn thành công" : "Tạo hóa đơn thất bại");
                }
                case HOADON_CAP_NHAT -> {
                    boolean ok = hoaDonService.capNhatHoaDon((HoaDonCreateUpdateDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case HOADON_LAY_CHI_TIET -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.layChiTietHoaDon((String) data));
                }
                case HOADON_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(hoaDonService.taoMaHoaDonTuDong());
                }

                // ==================== KhuyenMai ====================
                case KHUYENMAI_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(khuyenMaiService.layTatCaKhuyenMai());
                }
                case KHUYENMAI_LAY_THEO_MA -> {
                    KhuyenMaiDTO km = khuyenMaiService.layKhuyenMaiTheoMa((String) data);
                    response.setSuccess(km != null);
                    response.setData(km);
                    response.setMessage(km != null ? "Tìm thấy" : "Không tìm thấy khuyến mãi");
                }
                case KHUYENMAI_LAY_DANG_HOAT_DONG -> {
                    response.setSuccess(true);
                    response.setData(khuyenMaiService.layKhuyenMaiDangHoatDong());
                }
                case KHUYENMAI_LAY_CHI_TIET -> {
                    response.setSuccess(true);
                    response.setData(khuyenMaiService.layChiTietKhuyenMaiTheoMaKM((String) data));
                }
                case KHUYENMAI_THEM -> {
                    boolean ok = khuyenMaiService.themKhuyenMai((KhuyenMaiDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm khuyến mãi thành công" : "Thêm khuyến mãi thất bại");
                }
                case KHUYENMAI_CAP_NHAT -> {
                    boolean ok = khuyenMaiService.capNhatKhuyenMai((KhuyenMaiDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case KHUYENMAI_XOA -> {
                    boolean ok = khuyenMaiService.xoaKhuyenMai((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa thành công" : "Xóa thất bại");
                }
                case KHUYENMAI_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(khuyenMaiService.taoMaKhuyenMaiTuDong());
                }

                // ==================== LoSanPham ====================
                case LOSANPHAM_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.layTatCaLoSanPham());
                }
                case LOSANPHAM_LAY_THEO_MA -> {
                    LoSanPhamDTO lo = loSanPhamService.layLoSanPhamTheoMa((String) data);
                    response.setSuccess(lo != null);
                    response.setData(lo);
                    response.setMessage(lo != null ? "Tìm thấy" : "Không tìm thấy lô sản phẩm");
                }
                case LOSANPHAM_LAY_THEO_MA_SP -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.layLoSanPhamTheoMaSanPham((String) data));
                }
                case LOSANPHAM_LAY_CON_HANG -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.layLoSanPhamConHang((String) data));
                }
                case LOSANPHAM_LAY_SAP_HET_HAN -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.layLoSanPhamSapHetHan((Integer) data));
                }
                case LOSANPHAM_LAY_TON_KHO_THAP -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.layLoSanPhamTonKhoThap((Integer) data));
                }
                case LOSANPHAM_THEM -> {
                    boolean ok = loSanPhamService.themLoSanPham((LoSanPhamDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm lô sản phẩm thành công" : "Thêm lô sản phẩm thất bại");
                }
                case LOSANPHAM_CAP_NHAT -> {
                    boolean ok = loSanPhamService.capNhatLoSanPham((LoSanPhamDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case LOSANPHAM_CAP_NHAT_SO_LUONG -> {
                    // data: Object[] { String maLo, int soLuongMoi }
                    Object[] params = (Object[]) data;
                    boolean ok = loSanPhamService.capNhatSoLuongTon((String) params[0], (Integer) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật số lượng thành công" : "Cập nhật số lượng thất bại");
                }
                case LOSANPHAM_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(loSanPhamService.taoMaLoTuDong((String) data));
                }

                // ==================== PhieuNhap ====================
                case PHIEUNHAP_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(phieuNhapService.layTatCaPhieuNhap());
                }
                case PHIEUNHAP_LAY_THEO_MA -> {
                    PhieuNhapDTO pn = phieuNhapService.layPhieuNhapTheoMa((String) data);
                    response.setSuccess(pn != null);
                    response.setData(pn);
                    response.setMessage(pn != null ? "Tìm thấy" : "Không tìm thấy phiếu nhập");
                }
                case PHIEUNHAP_LAY_THEO_NGAY -> {
                    response.setSuccess(true);
                    response.setData(phieuNhapService.layPhieuNhapTheoNgay((LocalDate) data));
                }
                case PHIEUNHAP_LAY_THEO_NCC -> {
                    response.setSuccess(true);
                    response.setData(phieuNhapService.layPhieuNhapTheoNhaCungCap((String) data));
                }
                case PHIEUNHAP_THEM -> {
                    boolean ok = phieuNhapService.themPhieuNhap((PhieuNhapDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm phiếu nhập thành công" : "Thêm phiếu nhập thất bại");
                }
                case PHIEUNHAP_LAY_CHI_TIET -> {
                    response.setSuccess(true);
                    response.setData(phieuNhapService.layChiTietPhieuNhap((String) data));
                }
                case PHIEUNHAP_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(phieuNhapService.taoMaPhieuNhapTuDong());
                }

                default -> {
                    response.setSuccess(false);
                    response.setMessage("Lệnh không được hỗ trợ: " + cmd);
                }
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Lỗi xử lý: " + e.getMessage());
            System.err.println("Lỗi xử lý lệnh " + cmd + ": " + e.getMessage());
        }
        return response;
    }
}
