package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import iuh.fit.quanlyhieuthuoc.core.repository.BangGiaRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.DataPreloaderRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.DonViTinhRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.HoaDonRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.KhachHangRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.KhuyenMaiRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.LoSanPhamRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.NhaCungCapRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.NhanVienRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuHuyRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuNhapRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuTraRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.QuyCachDongGoiRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.SanPhamRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.TaiKhoanRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataPreloaderRepositoryImpl implements DataPreloaderRepository {

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
                DonViTinhRepository donViTinhRepository = new DonViTinhRepositoryImpl();
                NhanVienRepository nhanVienRepository = new NhanVienRepositoryImpl();
                NhaCungCapRepository nhaCungCapRepository = new NhaCungCapRepositoryImpl();
                KhachHangRepository khachHangRepository = new KhachHangRepositoryImpl();
                TaiKhoanRepository taiKhoanRepository = new TaiKhoanRepositoryImpl();

                donViTinhRepository.layTatCaDonViTinh();
                nhanVienRepository.layTatCaNhanVien();
                nhaCungCapRepository.layTatCaNhaCungCap();
                khachHangRepository.layTatCaKhachHang();
                taiKhoanRepository.layTatCaTaiKhoan();

                // Batch 2: Products & Pricing (Nặng hơn)
                SanPhamRepository sanPhamRepository = new SanPhamRepositoryImpl();
                BangGiaRepository bangGiaRepository = new BangGiaRepositoryImpl();
                LoSanPhamRepository loSanPhamRepository = new LoSanPhamRepositoryImpl();
                QuyCachDongGoiRepository quyCachDongGoiRepository = new QuyCachDongGoiRepositoryImpl();
                KhuyenMaiRepository khuyenMaiRepository = new KhuyenMaiRepositoryImpl();

                sanPhamRepository.layTatCaSanPham();
                bangGiaRepository.layTatCaBangGia();
                loSanPhamRepository.layTatCaLoSanPham();
                quyCachDongGoiRepository.layTatCaQuyCachDongGoi();
                khuyenMaiRepository.layTatCaKhuyenMai();

                // Batch 3: Transaction History (Nặng nhất)
                // Lưu ý: Các Repository này có thể chưa có cache full list hoặc list quá lớn
                // Chỉ load nếu đã cài đặt cache
                HoaDonRepository hoaDonRepository = new HoaDonRepositoryImpl();
                PhieuNhapRepository phieuNhapRepository = new PhieuNhapRepositoryImpl();
                PhieuTraRepository phieuTraRepository = new PhieuTraRepositoryImpl();
                PhieuHuyRepository phieuHuyRepository = new PhieuHuyRepositoryImpl();

                hoaDonRepository.layTatCaHoaDon();
                phieuNhapRepository.layDanhSachPhieuNhap();
                phieuTraRepository.layTatCaPhieuTra();
                phieuHuyRepository.layTatCaPhieuHuy();

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
