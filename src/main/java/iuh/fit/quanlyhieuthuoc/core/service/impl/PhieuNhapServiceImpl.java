package iuh.fit.quanlyhieuthuoc.core.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuNhap;
import iuh.fit.quanlyhieuthuoc.core.entity.PhieuNhap;
import iuh.fit.quanlyhieuthuoc.core.repository.ChiTietPhieuNhapRepository;
import iuh.fit.quanlyhieuthuoc.core.repository.PhieuNhapRepository;
import iuh.fit.quanlyhieuthuoc.core.service.PhieuNhapService;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.ChiTietPhieuNhapRepositoryImpl;
import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.PhieuNhapRepositoryImpl;

public class PhieuNhapServiceImpl implements PhieuNhapService {

    private final PhieuNhapRepository phieuNhapRepository;
    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    public PhieuNhapServiceImpl() {
        this.phieuNhapRepository = new PhieuNhapRepositoryImpl();
        this.chiTietPhieuNhapRepository = new ChiTietPhieuNhapRepositoryImpl();
    }

    public PhieuNhapServiceImpl(PhieuNhapRepository phieuNhapRepository,
                                ChiTietPhieuNhapRepository chiTietPhieuNhapRepository) {
        this.phieuNhapRepository = phieuNhapRepository;
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
    }

    @Override
    public List<PhieuNhap> layTatCaPhieuNhap() {
        return phieuNhapRepository.layDanhSachPhieuNhap();
    }

    @Override
    public PhieuNhap layPhieuNhapTheoMa(String maPhieuNhap) {
        return phieuNhapRepository.timPhieuNhapTheoMa(maPhieuNhap);
    }

    @Override
    public List<PhieuNhap> layPhieuNhapTheoNgay(LocalDate ngay) {
        Date sqlDate = Date.valueOf(ngay);
        return phieuNhapRepository.timKiemPhieuNhap(null, sqlDate, sqlDate);
    }

    @Override
    public List<PhieuNhap> layPhieuNhapTheoNhaCungCap(String maNhaCungCap) {
        return phieuNhapRepository.layPhieuNhapTheoNhaCungCap(maNhaCungCap);
    }

    @Override
    public boolean themPhieuNhap(PhieuNhap pn) {
        return phieuNhapRepository.themPhieuNhap(pn);
    }

    @Override
    public boolean capNhatPhieuNhap(PhieuNhap pn) {
        throw new UnsupportedOperationException("Chức năng cập nhật phiếu nhập chưa được hỗ trợ");
    }

    @Override
    public List<ChiTietPhieuNhap> layChiTietPhieuNhap(String maPhieuNhap) {
        return chiTietPhieuNhapRepository.timKiemChiTietPhieuNhapBangMa(maPhieuNhap);
    }

    @Override
    public String taoMaPhieuNhapTuDong() {
        return phieuNhapRepository.taoMaPhieuNhap();
    }

    @Override
    public void refreshCache() {
        // PhieuNhapRepository không có cache
    }
}
