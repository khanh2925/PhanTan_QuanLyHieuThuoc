package network;

import dao.iml.ChiTietPhieuTraDaoImpl;
import dao.iml.PhieuTraDaoImpl;
import dao.iml.QuyCachDongGoiDaoImpl;
import dao.iml.TaiKhoanDaoImpl;
import dao.iml.ThongKeNhanVienDaoImpl;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;
import dto.ChiTietHoaDonCreateUpdateDTO;
import dto.ChiTietHoaDonDTO;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.ChiTietPhieuHuyDTO;
import dto.ChiTietPhieuNhapDTO;
import dto.ChiTietPhieuTraDTO;
import dto.DonViTinhDTO;
import dto.HoaDonCreateUpdateDTO;
import dto.HoaDonDTO;
import dto.KhachHangDTO;
import dto.KhuyenMaiDTO;
import dto.LoSanPhamDTO;
import dto.NhaCungCapDTO;
import dto.NhanVienDTO;
import dto.PhieuHuyDTO;
import dto.PhieuNhapDTO;
import dto.PhieuTraDTO;
import dto.QuyCachDongGoiDTO;
import dto.SanPhamDTO;
import dto.TaiKhoanDTO;
import dto.ThongKeBanGhiDTO;
import dto.ThongKeDoanhThuDTO;
import dto.ThongKeHoaDonNgayDTO;
import dto.ThongKeNhanVienChiTietDTO;
import dto.ThongKeNhanVienDTO;
import dto.ThongKeTaiChinhDTO;
import entity.*;
import entity.LoaiSanPham;
import mapper.Mapper;
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
    private final NhaCungCapService nhaCungCapService;
    private final PhieuHuyService phieuHuyService;
    private final BangGiaService bangGiaService;
    private final DonViTinhService donViTinhService;
    private final PhieuTraDaoImpl phieuTraDao;
    private final ChiTietPhieuTraDaoImpl chiTietPhieuTraDao;
    private final QuyCachDongGoiDaoImpl quyCachDongGoiDao;
    private final TaiKhoanDaoImpl taiKhoanDao;
    private final ThongKeNhanVienDaoImpl thongKeNhanVienDao;

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
        this.nhaCungCapService = new NhaCungCapServiceImpl();
        this.phieuHuyService = new PhieuHuyServiceImpl();
        this.bangGiaService = new BangGiaServiceImpl();
        this.donViTinhService = new DonViTinhServiceImpl();
        this.phieuTraDao = new PhieuTraDaoImpl();
        this.chiTietPhieuTraDao = new ChiTietPhieuTraDaoImpl();
        this.quyCachDongGoiDao = new QuyCachDongGoiDaoImpl();
        this.taiKhoanDao = new TaiKhoanDaoImpl();
        this.thongKeNhanVienDao = new ThongKeNhanVienDaoImpl();
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
                    response.setData(khachHangService.layTatCaKhachHang().stream()
                            .map(kh -> Mapper.map(kh, KhachHangDTO.class))
                            .toList());
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
                case TAIKHOAN_TIM_QUEN_MAT_KHAU -> {
                    Object[] params = (Object[]) data;
                    String maTaiKhoan = taiKhoanDao.timTaiKhoanQuenMK(
                            (String) params[0], (String) params[1], (String) params[2], (LocalDate) params[3]);
                    response.setSuccess(maTaiKhoan != null);
                    response.setData(maTaiKhoan);
                    response.setMessage(maTaiKhoan != null ? "Tìm thấy tài khoản" : "Không xác thực được thông tin");
                }
                case TAIKHOAN_DAT_LAI_MAT_KHAU -> {
                    Object[] params = (Object[]) data;
                    boolean ok = taiKhoanDao.capNhatMatKhau((String) params[0], (String) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Đặt lại mật khẩu thành công" : "Đặt lại mật khẩu thất bại");
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
                case SANPHAM_REFRESH_CACHE -> {
                    sanPhamService.refreshCache();
                    response.setSuccess(true);
                    response.setMessage("Refresh cache sản phẩm thành công");
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
                case KHUYENMAI_THEM_CHI_TIET_SP -> {
                    Object[] params = (Object[]) data;
                    boolean ok = khuyenMaiService.themChiTietKhuyenMaiSanPham((String) params[0], (String) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm sản phẩm vào khuyến mãi thành công" : "Thêm sản phẩm vào khuyến mãi thất bại");
                }
                case KHUYENMAI_XOA_CHI_TIET_SP -> {
                    Object[] params = (Object[]) data;
                    boolean ok = khuyenMaiService.xoaChiTietKhuyenMaiSanPham((String) params[0], (String) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa sản phẩm khỏi khuyến mãi thành công" : "Xóa sản phẩm khỏi khuyến mãi thất bại");
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

                // ==================== PhieuHuy ====================
                case PHIEUHUY_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(phieuHuyService.layTatCaPhieuHuy().stream().map(this::toPhieuHuyDTO).toList());
                }
                case PHIEUHUY_LAY_THEO_MA -> {
                    PhieuHuy ph = phieuHuyService.layPhieuHuyTheoMa((String) data);
                    response.setSuccess(ph != null);
                    response.setData(ph != null ? toPhieuHuyDTO(ph) : null);
                    response.setMessage(ph != null ? "Tìm thấy phiếu hủy" : "Không tìm thấy phiếu hủy");
                }
                case PHIEUHUY_THEM -> {
                    boolean ok = phieuHuyService.themPhieuHuy((PhieuHuy) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm phiếu hủy thành công" : "Thêm phiếu hủy thất bại");
                }
                case PHIEUHUY_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(phieuHuyService.taoMaPhieuHuy());
                }

                // ==================== BangGia ====================
                case BANGGIA_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(bangGiaService.layTatCaBangGia());
                }
                case BANGGIA_LAY_DANG_HOAT_DONG -> {
                    BangGiaDTO bg = bangGiaService.layBangGiaDangHoatDong();
                    response.setSuccess(bg != null);
                    response.setData(bg);
                    response.setMessage(bg != null ? "TÃ¬m tháº¥y báº£ng giÃ¡ Ä‘ang hoáº¡t Ä‘á»™ng" : "KhÃ´ng cÃ³ báº£ng giÃ¡ Ä‘ang hoáº¡t Ä‘á»™ng");
                }
                case BANGGIA_LAY_THEO_MA -> {
                    BangGiaDTO bg = bangGiaService.layBangGiaTheoMa((String) data);
                    response.setSuccess(bg != null);
                    response.setData(bg);
                    response.setMessage(bg != null ? "TÃ¬m tháº¥y" : "KhÃ´ng tÃ¬m tháº¥y báº£ng giÃ¡");
                }
                case BANGGIA_THEM -> {
                    boolean ok = bangGiaService.themBangGia((BangGiaDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "ThÃªm báº£ng giÃ¡ thÃ nh cÃ´ng" : "ThÃªm báº£ng giÃ¡ tháº¥t báº¡i");
                }
                case BANGGIA_CAP_NHAT -> {
                    boolean ok = bangGiaService.capNhatBangGia((BangGiaDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cáº­p nháº­t báº£ng giÃ¡ thÃ nh cÃ´ng" : "Cáº­p nháº­t báº£ng giÃ¡ tháº¥t báº¡i");
                }
                case BANGGIA_XOA -> {
                    boolean ok = bangGiaService.xoaBangGia((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "XÃ³a báº£ng giÃ¡ thÃ nh cÃ´ng" : "XÃ³a báº£ng giÃ¡ tháº¥t báº¡i");
                }
                case BANGGIA_HUY_HOAT_DONG_TAT_CA_TRU -> {
                    boolean ok = bangGiaService.huyHoatDongTatCaTruBangGia((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cáº­p nháº­t tráº¡ng thÃ¡i báº£ng giÃ¡ thÃ nh cÃ´ng" : "Cáº­p nháº­t tráº¡ng thÃ¡i báº£ng giÃ¡ tháº¥t báº¡i");
                }
                case BANGGIA_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(bangGiaService.taoMaBangGia());
                }
                case BANGGIA_LAY_CHI_TIET -> {
                    response.setSuccess(true);
                    response.setData(bangGiaService.layChiTietTheoMaBangGia((String) data));
                }
                case BANGGIA_THEM_CHI_TIET -> {
                    boolean ok = bangGiaService.themChiTietBangGia((ChiTietBangGiaDTO) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "ThÃªm chi tiáº¿t báº£ng giÃ¡ thÃ nh cÃ´ng" : "ThÃªm chi tiáº¿t báº£ng giÃ¡ tháº¥t báº¡i");
                }
                case BANGGIA_XOA_TAT_CA_CHI_TIET -> {
                    boolean ok = bangGiaService.xoaTatCaChiTiet((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "XÃ³a chi tiáº¿t báº£ng giÃ¡ thÃ nh cÃ´ng" : "XÃ³a chi tiáº¿t báº£ng giÃ¡ tháº¥t báº¡i");
                }

                // ==================== DonViTinh ====================
                case DONVITINH_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(donViTinhService.layTatCaDonViTinh().stream().map(this::toDonViTinhDTO).toList());
                }
                case DONVITINH_LAY_THEO_MA -> {
                    DonViTinh dvt = donViTinhService.timDonViTinhTheoMa((String) data);
                    response.setSuccess(dvt != null);
                    response.setData(dvt != null ? toDonViTinhDTO(dvt) : null);
                    response.setMessage(dvt != null ? "TÃ¬m tháº¥y" : "KhÃ´ng tÃ¬m tháº¥y Ä‘Æ¡n vá»‹ tÃ­nh");
                }
                case DONVITINH_THEM -> {
                    DonViTinh dvt = data instanceof DonViTinhDTO dto ? toDonViTinhEntity(dto) : (DonViTinh) data;
                    boolean ok = donViTinhService.themDonViTinh(dvt);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "ThÃªm Ä‘Æ¡n vá»‹ tÃ­nh thÃ nh cÃ´ng" : "ThÃªm Ä‘Æ¡n vá»‹ tÃ­nh tháº¥t báº¡i");
                }
                case DONVITINH_CAP_NHAT -> {
                    DonViTinh dvt = data instanceof DonViTinhDTO dto ? toDonViTinhEntity(dto) : (DonViTinh) data;
                    boolean ok = donViTinhService.capNhatDonViTinh(dvt);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cáº­p nháº­t Ä‘Æ¡n vá»‹ tÃ­nh thÃ nh cÃ´ng" : "Cáº­p nháº­t Ä‘Æ¡n vá»‹ tÃ­nh tháº¥t báº¡i");
                }
                case DONVITINH_XOA -> {
                    boolean ok = donViTinhService.xoaDonViTinh((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "XÃ³a Ä‘Æ¡n vá»‹ tÃ­nh thÃ nh cÃ´ng" : "XÃ³a Ä‘Æ¡n vá»‹ tÃ­nh tháº¥t báº¡i");
                }
                case DONVITINH_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(donViTinhService.taoMaTuDong());
                }

                // ==================== NhaCungCap ====================
                case NHACUNGCAP_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(nhaCungCapService.layTatCaNhaCungCap().stream().map(this::toNhaCungCapDTO).toList());
                }
                case NHACUNGCAP_LAY_THEO_MA_HOAC_SDT -> {
                    NhaCungCap ncc = nhaCungCapService.layNhaCungCapTheoMaHoacSDT((String) data);
                    response.setSuccess(ncc != null);
                    response.setData(ncc != null ? toNhaCungCapDTO(ncc) : null);
                    response.setMessage(ncc != null ? "Tìm thấy" : "Không tìm thấy nhà cung cấp");
                }
                case NHACUNGCAP_TIM_KIEM -> {
                    Object[] params = (Object[]) data;
                    response.setSuccess(true);
                    response.setData(nhaCungCapService.timKiemNCC((String) params[0], (String) params[1], (String) params[2], (String) params[3])
                            .stream().map(this::toNhaCungCapDTO).toList());
                }
                case NHACUNGCAP_THEM -> {
                    NhaCungCap ncc = data instanceof NhaCungCapDTO dto ? toNhaCungCapEntity(dto) : (NhaCungCap) data;
                    boolean ok = nhaCungCapService.themNhaCungCap(ncc);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm nhà cung cấp thành công" : "Thêm nhà cung cấp thất bại");
                }
                case NHACUNGCAP_CAP_NHAT -> {
                    NhaCungCap ncc = data instanceof NhaCungCapDTO dto ? toNhaCungCapEntity(dto) : (NhaCungCap) data;
                    boolean ok = nhaCungCapService.capNhatNhaCungCap(ncc);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật thành công" : "Cập nhật thất bại");
                }
                case NHACUNGCAP_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(nhaCungCapService.taoMaTuDong());
                }

                // ==================== QuyCachDongGoi ====================
                case QUYCACH_LAY_THEO_SAN_PHAM -> {
                    response.setSuccess(true);
                    response.setData(quyCachDongGoiDao.layDanhSachQuyCachTheoSanPham((String) data).stream()
                            .map(this::toQuyCachDongGoiDTO).toList());
                }
                case QUYCACH_LAY_GOC_THEO_SAN_PHAM -> {
                    QuyCachDongGoi qc = quyCachDongGoiDao.timQuyCachGocTheoSanPham((String) data);
                    response.setSuccess(qc != null);
                    response.setData(qc != null ? toQuyCachDongGoiDTO(qc) : null);
                    response.setMessage(qc != null ? "TÃ¬m tháº¥y" : "KhÃ´ng tÃ¬m tháº¥y quy cÃ¡ch gá»‘c");
                }
                case QUYCACH_LAY_THEO_SAN_PHAM_VA_DVT -> {
                    Object[] params = (Object[]) data;
                    QuyCachDongGoi qc = quyCachDongGoiDao.timQuyCachTheoSanPhamVaDonVi((String) params[0], (String) params[1]);
                    response.setSuccess(qc != null);
                    response.setData(qc != null ? toQuyCachDongGoiDTO(qc) : null);
                    response.setMessage(qc != null ? "TÃ¬m tháº¥y" : "KhÃ´ng tÃ¬m tháº¥y quy cÃ¡ch");
                }

                case QUYCACH_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(quyCachDongGoiDao.layTatCaQuyCachDongGoi().stream()
                            .map(this::toQuyCachDongGoiDTO).toList());
                }
                case QUYCACH_THEM -> {
                    QuyCachDongGoi qc = data instanceof QuyCachDongGoiDTO dto ? toQuyCachDongGoiEntity(dto) : (QuyCachDongGoi) data;
                    boolean ok = quyCachDongGoiDao.themQuyCachDongGoi(qc);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Thêm quy cách thành công" : "Thêm quy cách thất bại");
                }
                case QUYCACH_CAP_NHAT -> {
                    QuyCachDongGoi qc = data instanceof QuyCachDongGoiDTO dto ? toQuyCachDongGoiEntity(dto) : (QuyCachDongGoi) data;
                    boolean ok = quyCachDongGoiDao.capNhatQuyCachDongGoi(qc);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cập nhật quy cách thành công" : "Cập nhật quy cách thất bại");
                }
                case QUYCACH_XOA -> {
                    boolean ok = quyCachDongGoiDao.xoaQuyCachDongGoi((String) data);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Xóa quy cách thành công" : "Xóa quy cách thất bại");
                }
                case QUYCACH_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(quyCachDongGoiDao.taoMaQuyCach());
                }

                // ==================== PhieuTra ====================
                case PHIEUTRA_LAY_TAT_CA -> {
                    response.setSuccess(true);
                    response.setData(phieuTraDao.layTatCaPhieuTra().stream().map(this::toPhieuTraDTO).toList());
                }
                case PHIEUTRA_LAY_THEO_MA -> {
                    PhieuTra pt = phieuTraDao.timKiemPhieuTraBangMa((String) data);
                    response.setSuccess(pt != null);
                    response.setData(pt != null ? toPhieuTraDTO(pt) : null);
                    response.setMessage(pt != null ? "TÃ¬m tháº¥y" : "KhÃ´ng tÃ¬m tháº¥y phiáº¿u tráº£");
                }
                case PHIEUTRA_LAY_CHI_TIET -> {
                    response.setSuccess(true);
                    response.setData(chiTietPhieuTraDao.timKiemChiTietBangMaPhieuTra((String) data).stream()
                            .map(this::toChiTietPhieuTraDTO).toList());
                }
                case PHIEUTRA_LAY_THEO_SDT -> {
                    response.setSuccess(true);
                    response.setData(phieuTraDao.timPhieuTraTheoSoDienThoai((String) data).stream().map(this::toPhieuTraDTO).toList());
                }
                case PHIEUTRA_LAY_THEO_KEYWORD -> {
                    response.setSuccess(true);
                    response.setData(phieuTraDao.timPhieuTraTheoKeyword((String) data).stream().map(this::toPhieuTraDTO).toList());
                }
                case PHIEUTRA_THEM -> {
                    Object[] params = (Object[]) data;
                    PhieuTra pt = (PhieuTra) params[0];
                    @SuppressWarnings("unchecked")
                    List<ChiTietPhieuTra> dsChiTiet = (List<ChiTietPhieuTra>) params[1];
                    boolean ok = phieuTraDao.themPhieuTraVaChiTiet(pt, dsChiTiet);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "ThÃªm phiáº¿u tráº£ thÃ nh cÃ´ng" : "ThÃªm phiáº¿u tráº£ tháº¥t báº¡i");
                }
                case PHIEUTRA_CAP_NHAT_TRANG_THAI_GIAO_DICH -> {
                    Object[] params = (Object[]) data;
                    String kq = phieuTraDao.capNhatTrangThai_GiaoDich(
                            (String) params[0], (String) params[1], (String) params[2], (String) params[3],
                            (NhanVien) params[4], (Integer) params[5]);
                    response.setSuccess(kq != null && kq.startsWith("OK"));
                    response.setData(kq);
                    response.setMessage(response.isSuccess() ? "Cáº­p nháº­t chi tiáº¿t phiáº¿u tráº£ thÃ nh cÃ´ng" : "Cáº­p nháº­t chi tiáº¿t phiáº¿u tráº£ tháº¥t báº¡i");
                }
                case PHIEUTRA_CAP_NHAT_TRANG_THAI -> {
                    Object[] params = (Object[]) data;
                    boolean ok = phieuTraDao.capNhatTrangThaiPhieuTra((String) params[0], (Boolean) params[1]);
                    response.setSuccess(ok);
                    response.setMessage(ok ? "Cáº­p nháº­t tráº¡ng thÃ¡i phiáº¿u tráº£ thÃ nh cÃ´ng" : "Cáº­p nháº­t tráº¡ng thÃ¡i phiáº¿u tráº£ tháº¥t báº¡i");
                }
                case PHIEUTRA_TAO_MA -> {
                    response.setSuccess(true);
                    response.setData(phieuTraDao.taoMaPhieuTra());
                }
                case PHIEUTRA_DEM_HOM_NAY_NV -> {
                    response.setSuccess(true);
                    response.setData(phieuTraDao.demSoPhieuTraHomNayCuaNhanVien((String) data));
                }
                case PHIEUTRA_TONG_SO_LUONG_DA_TRA -> {
                    Object[] params = (Object[]) data;
                    response.setSuccess(true);
                    response.setData((int) chiTietPhieuTraDao.tongSoLuongDaTra((String) params[0], (String) params[1]));
                }
                case PHIEUTRA_DA_TRA_LO -> {
                    Object[] params = (Object[]) data;
                    response.setSuccess(true);
                    response.setData(phieuTraDao.daTraLoTrongHoaDon((String) params[0], (String) params[1]));
                }

                // ==================== ThongKe NhanVien ====================
                case THONGKE_NHANVIEN_LAY_THONG_KE -> {
                    Object[] params = (Object[]) data;
                    dao.ThongKeNhanVienDao.KetQuaThongKe kq = thongKeNhanVienDao.getThongKe(
                            (java.util.Date) params[1],
                            (java.util.Date) params[2],
                            (String) params[0],
                            (Integer) params[3]);
                    ThongKeNhanVienDTO dto = new ThongKeNhanVienDTO();
                    dto.tongDoanhSo = kq.tongDoanhSo;
                    dto.soHoaDon = kq.soHoaDon;
                    dto.soPhieuTra = kq.soPhieuTra;
                    dto.tongTienTra = kq.tongTienTra;
                    dto.soPhieuHuy = kq.soPhieuHuy;
                    response.setSuccess(true);
                    response.setData(dto);
                }
                case THONGKE_NHANVIEN_LAY_DANH_SACH -> {
                    response.setSuccess(true);
                    response.setData(thongKeNhanVienDao.getDanhSachNhanVien());
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

    private NhaCungCapDTO toNhaCungCapDTO(entity.NhaCungCap entity) {
        if (entity == null) return null;
        NhaCungCapDTO dto = new NhaCungCapDTO();
        dto.setMaNhaCungCap(entity.getMaNhaCungCap());
        dto.setTenNhaCungCap(entity.getTenNhaCungCap());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setDiaChi(entity.getDiaChi());
        dto.setEmail(entity.getEmail());
        dto.setHoatDong(entity.isHoatDong());
        return dto;
    }

    private entity.NhaCungCap toNhaCungCapEntity(NhaCungCapDTO dto) {
        if (dto == null) return null;
        entity.NhaCungCap entity = new entity.NhaCungCap();
        entity.setMaNhaCungCap(dto.getMaNhaCungCap());
        entity.setTenNhaCungCap(dto.getTenNhaCungCap());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setDiaChi(dto.getDiaChi());
        entity.setEmail(dto.getEmail());
        entity.setHoatDong(dto.isHoatDong());
        return entity;
    }

    private DonViTinhDTO toDonViTinhDTO(entity.DonViTinh entity) {
        if (entity == null) return null;
        DonViTinhDTO dto = new DonViTinhDTO();
        dto.setMaDonViTinh(entity.getMaDonViTinh());
        dto.setTenDonViTinh(entity.getTenDonViTinh());
        return dto;
    }

    private entity.DonViTinh toDonViTinhEntity(DonViTinhDTO dto) {
        if (dto == null) return null;
        return new entity.DonViTinh(dto.getMaDonViTinh(), dto.getTenDonViTinh());
    }

    private QuyCachDongGoiDTO toQuyCachDongGoiDTO(entity.QuyCachDongGoi entity) {
        if (entity == null) return null;
        QuyCachDongGoiDTO dto = new QuyCachDongGoiDTO();
        dto.setMaQuyCach(entity.getMaQuyCach());
        dto.setDonViTinh(toDonViTinhDTO(entity.getDonViTinh()));
        dto.setSanPham(entity.getSanPham() != null ? sanPhamService.laySanPhamTheoMa(entity.getSanPham().getMaSanPham()) : null);
        dto.setHeSoQuyDoi(entity.getHeSoQuyDoi());
        dto.setTiLeGiam(entity.getTiLeGiam());
        dto.setDonViGoc(entity.isDonViGoc());
        dto.setTrangThai(entity.isTrangThai());
        return dto;
    }

    private entity.QuyCachDongGoi toQuyCachDongGoiEntity(QuyCachDongGoiDTO dto) {
        if (dto == null) return null;
        entity.DonViTinh dvt = new entity.DonViTinh(dto.getDonViTinh().getMaDonViTinh(), dto.getDonViTinh().getTenDonViTinh());
        entity.SanPham sp = dto.getSanPham() != null ? Mapper.map(dto.getSanPham(), entity.SanPham.class) : null;
        return new entity.QuyCachDongGoi(dto.getMaQuyCach(), dvt, sp, dto.getHeSoQuyDoi(), dto.getTiLeGiam(),
                dto.isDonViGoc(), dto.isTrangThai());
    }

    private PhieuHuyDTO toPhieuHuyDTO(entity.PhieuHuy entity) {
        if (entity == null) return null;
        PhieuHuyDTO dto = new PhieuHuyDTO();
        dto.setMaPhieuHuy(entity.getMaPhieuHuy());
        dto.setNgayLapPhieu(entity.getNgayLapPhieu());
        dto.setTrangThai(entity.isTrangThai());
        dto.setTongTien(entity.getTongTien());
        if (entity.getNhanVien() != null) {
            NhanVienDTO nv = new NhanVienDTO();
            nv.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            nv.setTenNhanVien(entity.getNhanVien().getTenNhanVien());
            dto.setNhanVien(nv);
        }
        return dto;
    }

    private PhieuTraDTO toPhieuTraDTO(entity.PhieuTra entity) {
        if (entity == null) return null;
        PhieuTraDTO dto = new PhieuTraDTO();
        dto.setMaPhieuTra(entity.getMaPhieuTra());
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getTenKhachHang());
            dto.setSoDienThoai(entity.getKhachHang().getSoDienThoai());
        }
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(entity.getNhanVien().getTenNhanVien());
        }
        dto.setNgayLap(entity.getNgayLap() != null ? entity.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        dto.setTrangThai(entity.isTrangThai());
        dto.setTongTienHoan(entity.getTongTienHoan());
        return dto;
    }

    private ChiTietPhieuTraDTO toChiTietPhieuTraDTO(entity.ChiTietPhieuTra entity) {
        if (entity == null) return null;
        ChiTietPhieuTraDTO dto = new ChiTietPhieuTraDTO();
        dto.setMaPhieuTra(entity.getId() != null ? entity.getId().getMaPhieuTra() : null);
        dto.setMaHoaDon(entity.getId() != null ? entity.getId().getMaHoaDon() : null);
        dto.setMaLo(entity.getId() != null ? entity.getId().getMaLo() : null);
        dto.setTenSanPham(entity.getChiTietHoaDon() != null && entity.getChiTietHoaDon().getSanPham() != null
                ? entity.getChiTietHoaDon().getSanPham().getTenSanPham() : null);
        dto.setSoLuong(entity.getSoLuong());
        dto.setThanhTienHoan(entity.getThanhTienHoan());
        dto.setLyDoChiTiet(entity.getLyDoChiTiet());
        dto.setTrangThai(entity.getTrangThai());
        if (entity.getDonViTinh() != null) {
            dto.setMaDonViTinh(entity.getDonViTinh().getMaDonViTinh());
            dto.setTenDonViTinh(entity.getDonViTinh().getTenDonViTinh());
        }
        return dto;
    }
}
