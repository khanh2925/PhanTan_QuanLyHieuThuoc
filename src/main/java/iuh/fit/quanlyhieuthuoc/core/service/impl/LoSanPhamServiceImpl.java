package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import iuh.fit.quanlyhieuthuoc.core.entity.LoSanPham;
import iuh.fit.quanlyhieuthuoc.core.repository.LoSanPhamRepository;
import iuh.fit.quanlyhieuthuoc.core.service.LoSanPhamService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.LoSanPhamRepositoryImpl;

public class LoSanPhamServiceImpl implements LoSanPhamService {

    private final LoSanPhamRepository loSanPhamRepository;

    public LoSanPhamServiceImpl() {
        this.loSanPhamRepository = new LoSanPhamRepositoryImpl();
    }

    public LoSanPhamServiceImpl(LoSanPhamRepository loSanPhamRepository) {
        this.loSanPhamRepository = loSanPhamRepository;
    }

    @Override
    public List<LoSanPham> layTatCaLoSanPham() {
        return loSanPhamRepository.layTatCaLoSanPham();
    }

    @Override
    public LoSanPham layLoSanPhamTheoMa(String maLo) {
        return loSanPhamRepository.timLoTheoMa(maLo);
    }

    @Override
    public List<LoSanPham> layLoSanPhamTheoMaSanPham(String maSanPham) {
        return loSanPhamRepository.layDanhSachLoTheoMaSanPham(maSanPham);
    }

    @Override
    public List<LoSanPham> layLoSanPhamConHang(String maSanPham) {
        // layDanhSachLoTheoMaSanPham đã lọc SoLuongTon > 0 AND chưa hết hạn
        return loSanPhamRepository.layDanhSachLoTheoMaSanPham(maSanPham);
    }

    @Override
    public List<LoSanPham> layLoSanPhamSapHetHan(int soNgay) {
        // Repository dùng mốc 90 ngày cố định; lọc thêm theo soNgay từ client
        List<LoSanPham> danhSach = loSanPhamRepository.layDanhSachLoSPToiHanSuDung();
        if (soNgay <= 0 || soNgay >= 90) return danhSach;
        java.time.LocalDate hanToiDa = java.time.LocalDate.now().plusDays(soNgay);
        return danhSach.stream()
                .filter(lo -> lo.getHanSuDung() != null && !lo.getHanSuDung().isAfter(hanToiDa))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoSanPham> layLoSanPhamTonKhoThap(int nguongTon) {
        return loSanPhamRepository.layTatCaLoSanPham().stream()
                .filter(lo -> lo.getSoLuongTon() <= nguongTon)
                .collect(Collectors.toList());
    }

    @Override
    public boolean themLoSanPham(LoSanPham lo) {
        return loSanPhamRepository.themLoSanPham(lo);
    }

    @Override
    public boolean capNhatLoSanPham(LoSanPham lo) {
        return loSanPhamRepository.capNhatLoSanPham(lo);
    }

    @Override
    public boolean capNhatSoLuongTon(String maLo, int soLuongMoi) {
        LoSanPham lo = loSanPhamRepository.timLoTheoMa(maLo);
        if (lo == null) return false;
        lo.setSoLuongTon(soLuongMoi);
        return loSanPhamRepository.capNhatLoSanPham(lo);
    }

    @Override
    public String taoMaLoTuDong(String maSanPham) {
        return loSanPhamRepository.taoMaLoTuDong();
    }

    @Override
    public void refreshCache() {
        loSanPhamRepository.clearCache();
    }
}
