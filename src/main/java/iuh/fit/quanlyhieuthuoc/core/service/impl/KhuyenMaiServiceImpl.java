package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietKhuyenMaiSanPham;
import iuh.fit.quanlyhieuthuoc.core.entity.KhuyenMai;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietKhuyenMaiSanPhamRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.KhuyenMaiRepository;
import iuh.fit.quanlyhieuthuoc.core.service.KhuyenMaiService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.ChiTietKhuyenMaiSanPhamRepositoryImpl;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.KhuyenMaiRepositoryImpl;

public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final ChiTietKhuyenMaiSanPhamRepository chiTietKhuyenMaiRepository;

    public KhuyenMaiServiceImpl() {
        this.khuyenMaiRepository = new KhuyenMaiRepositoryImpl();
        this.chiTietKhuyenMaiRepository = new ChiTietKhuyenMaiSanPhamRepositoryImpl();
    }

    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository,
                                ChiTietKhuyenMaiSanPhamRepository chiTietKhuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.chiTietKhuyenMaiRepository = chiTietKhuyenMaiRepository;
    }

    @Override
    public List<KhuyenMai> layTatCaKhuyenMai() {
        return khuyenMaiRepository.layTatCaKhuyenMai();
    }

    @Override
    public KhuyenMai layKhuyenMaiTheoMa(String maKM) {
        return khuyenMaiRepository.timKhuyenMaiTheoMa(maKM);
    }

    @Override
    public List<KhuyenMai> layKhuyenMaiDangHoatDong() {
        return khuyenMaiRepository.layKhuyenMaiDangHoatDong();
    }

    @Override
    public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaKM(String maKM) {
        return chiTietKhuyenMaiRepository.layChiTietKhuyenMaiTheoMaCoJoin(maKM);
    }

    @Override
    public boolean themKhuyenMai(KhuyenMai km) {
        return khuyenMaiRepository.themKhuyenMai(km);
    }

    @Override
    public boolean capNhatKhuyenMai(KhuyenMai km) {
        return khuyenMaiRepository.capNhatKhuyenMai(km);
    }

    @Override
    public boolean xoaKhuyenMai(String maKM) {
        return khuyenMaiRepository.xoaKhuyenMai(maKM);
    }

    @Override
    public String taoMaKhuyenMaiTuDong() {
        return khuyenMaiRepository.taoMaKhuyenMai();
    }

    @Override
    public void refreshCache() {
        // KhuyenMaiRepository không có cache
    }
}
