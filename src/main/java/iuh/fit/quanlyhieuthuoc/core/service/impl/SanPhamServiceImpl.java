package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.util.List;
import iuh.fit.quanlyhieuthuoc.core.entity.SanPham;
import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietKhuyenMaiSanPham;
import iuh.fit.quanlyhieuthuoc.core.enums.LoaiSanPham;
import iuh.fit.quanlyhieuthuoc.core.repository.SanPhamRepository;
import iuh.fit.quanlyhieuthuoc.core.service.SanPhamService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.SanPhamRepositoryImpl;

public class SanPhamServiceImpl implements SanPhamService {
    
    private final SanPhamRepository sanPhamRepository;
    
    public SanPhamServiceImpl() {
        this.sanPhamRepository = new SanPhamRepositoryImpl();
    }
    
    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }
    
    @Override
    public List<SanPham> layTatCaSanPham() {
        return sanPhamRepository.layTatCaSanPham();
    }
    
    @Override
    public SanPham laySanPhamTheoMa(String maSanPham) {
        return sanPhamRepository.laySanPhamTheoMa(maSanPham);
    }
    
    @Override
    public SanPham timSanPhamTheoSoDangKy(String soDangKy) {
        return sanPhamRepository.timSanPhamTheoSoDangKy(soDangKy);
    }
    
    @Override
    public List<SanPham> timKiemSanPham(String tuKhoa) {
        return sanPhamRepository.timKiemSanPham(tuKhoa);
    }
    
    @Override
    public List<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
        return sanPhamRepository.laySanPhamTheoLoai(loaiSP);
    }
    
    @Override
    public boolean themSanPham(SanPham sp) {
        return sanPhamRepository.themSanPham(sp);
    }
    
    @Override
    public boolean capNhatSanPham(SanPham sp) {
        return sanPhamRepository.capNhatSanPham(sp);
    }
    
    @Override
    public boolean xoaSanPham(String maSanPham) {
        return sanPhamRepository.xoaSanPham(maSanPham);
    }
    
    @Override
    public List<ChiTietKhuyenMaiSanPham> layKhuyenMaiDangApDungChoSanPham(String maSanPham) {
        return sanPhamRepository.layKhuyenMaiDangApDungChoSanPham(maSanPham);
    }
    
    @Override
    public void refreshCache() {
        sanPhamRepository.refreshCache();
    }
}
