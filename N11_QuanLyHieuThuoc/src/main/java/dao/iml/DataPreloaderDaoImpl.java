package dao.iml;

import dao.BangGiaDao;
import dao.DataPreloaderDao;
import dao.DonViTinhDao;
import dao.HoaDonDao;
import dao.KhachHangDao;
import dao.KhuyenMaiDao;
import dao.LoSanPhamDao;
import dao.NhaCungCapDao;
import dao.NhanVienDao;
import dao.PhieuHuyDao;
import dao.PhieuNhapDao;
import dao.PhieuTraDao;
import dao.QuyCachDongGoiDao;
import dao.SanPhamDao;
import dao.TaiKhoanDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataPreloaderDaoImpl implements DataPreloaderDao {

    private static boolean isLoaded = false;

    @Override
    public void preloadAllData() {
        if (isLoaded)
            return; // Prevent double loading
        isLoaded = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            System.out.println("⏳ [DataPreloader] Bắt đầu tải dữ liệu ngầm...");
            long start = System.currentTimeMillis();

            try {
                // Batch 1: Basic Dictionaries (Ít phụ thuộc)
                DonViTinhDao donViTinhDao = new DonViTinhDaoImpl();
                NhanVienDao nhanVienDao = new NhanVienDaoImpl();
                NhaCungCapDao nhaCungCapDao = new NhaCungCapDaoImpl();
                KhachHangDao khachHangDao = new KhachHangDaoImpl();
                TaiKhoanDao taiKhoanDao = new TaiKhoanDaoImpl();

                donViTinhDao.layTatCaDonViTinh();
                nhanVienDao.layTatCaNhanVien();
                nhaCungCapDao.layTatCaNhaCungCap();
                khachHangDao.layTatCaKhachHang();
                taiKhoanDao.layTatCaTaiKhoan();

                // Batch 2: Products & Pricing (Nặng hơn)
                SanPhamDao sanPhamDao = new SanPhamDaoImpl();
                BangGiaDao bangGiaDao = new BangGiaDaoImpl();
                LoSanPhamDao loSanPhamDao = new LoSanPhamDaoImpl();
                QuyCachDongGoiDao quyCachDongGoiDao = new QuyCachDongGoiDaoImpl();
                KhuyenMaiDao khuyenMaiDao = new KhuyenMaiDaoImpl();

                sanPhamDao.layTatCaSanPham();
                bangGiaDao.layTatCaBangGia();
                loSanPhamDao.layTatCaLoSanPham();
                quyCachDongGoiDao.layTatCaQuyCachDongGoi();
                khuyenMaiDao.layTatCaKhuyenMai();

                // Batch 3: Transaction History (Nặng nhất)
                // Lưu ý: Các Dao này có thể chưa có cache full list hoặc list quá lớn
                // Chỉ load nếu đã cài đặt cache
                HoaDonDao hoaDonDao = new HoaDonDaoImpl();
                PhieuNhapDao phieuNhapDao = new PhieuNhapDaoImpl();
                PhieuTraDao phieuTraDao = new PhieuTraDaoImpl();
                PhieuHuyDao phieuHuyDao = new PhieuHuyDaoImpl();

                hoaDonDao.layTatCaHoaDon();
                phieuNhapDao.layDanhSachPhieuNhap();
                phieuTraDao.layTatCaPhieuTra();
                phieuHuyDao.layTatCaPhieuHuy();

            } catch (Exception e) {
                System.err.println("❌ [DataPreloader] Lỗi khi tải dữ liệu ngầm: " + e.getMessage());
                e.printStackTrace();
            }

            long end = System.currentTimeMillis();
            System.out.println("✅ [DataPreloader] Hoàn tất tải dữ liệu trong: " + (end - start) + "ms");
            executor.shutdown();
        });
    }
}
