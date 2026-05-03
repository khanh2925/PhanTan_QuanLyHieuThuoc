package service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import dao.ChiTietPhieuNhapDao;
import dao.PhieuNhapDao;
import dao.iml.ChiTietPhieuNhapDaoImpl;
import dao.iml.PhieuNhapDaoImpl;
import dto.ChiTietPhieuNhapDTO;
import dto.PhieuNhapDTO;
import entity.PhieuNhap;
import mapper.Mapper;
import service.PhieuNhapService;

public class PhieuNhapServiceImpl implements PhieuNhapService {

    private final PhieuNhapDao phieuNhapDao;
    private final ChiTietPhieuNhapDao chiTietPhieuNhapDao;

    public PhieuNhapServiceImpl() {
        this.phieuNhapDao = new PhieuNhapDaoImpl();
        this.chiTietPhieuNhapDao = new ChiTietPhieuNhapDaoImpl();
    }

    public PhieuNhapServiceImpl(PhieuNhapDao phieuNhapDao,
                                ChiTietPhieuNhapDao chiTietPhieuNhapDao) {
        this.phieuNhapDao = phieuNhapDao;
        this.chiTietPhieuNhapDao = chiTietPhieuNhapDao;
    }

    @Override
    public List<PhieuNhapDTO> layTatCaPhieuNhap() {
        return Mapper.mapList(phieuNhapDao.layDanhSachPhieuNhap(), PhieuNhapDTO.class);
    }

    @Override
    public PhieuNhapDTO layPhieuNhapTheoMa(String maPhieuNhap) {
        return Mapper.map(phieuNhapDao.timPhieuNhapTheoMa(maPhieuNhap), PhieuNhapDTO.class);
    }

    @Override
    public List<PhieuNhapDTO> layPhieuNhapTheoNgay(LocalDate ngay) {
        Date sqlDate = Date.valueOf(ngay);
        return Mapper.mapList(phieuNhapDao.timKiemPhieuNhap("", sqlDate, sqlDate), PhieuNhapDTO.class);
    }

    @Override
    public List<PhieuNhapDTO> layPhieuNhapTheoNhaCungCap(String maNhaCungCap) {
        return Mapper.mapList(phieuNhapDao.layPhieuNhapTheoNhaCungCap(maNhaCungCap), PhieuNhapDTO.class);
    }

    @Override
    public boolean themPhieuNhap(PhieuNhapDTO pn) {
        return phieuNhapDao.themPhieuNhap(Mapper.map(pn, PhieuNhap.class));
    }

    @Override
    public boolean capNhatPhieuNhap(PhieuNhapDTO pn) {
        throw new UnsupportedOperationException("Chuc nang cap nhat phieu nhap chua duoc ho tro");
    }

    @Override
    public List<ChiTietPhieuNhapDTO> layChiTietPhieuNhap(String maPhieuNhap) {
        return Mapper.mapList(chiTietPhieuNhapDao.timKiemChiTietPhieuNhapBangMa(maPhieuNhap), ChiTietPhieuNhapDTO.class);
    }

    @Override
    public String taoMaPhieuNhapTuDong() {
        return phieuNhapDao.taoMaPhieuNhap();
    }

    @Override
    public void refreshCache() {
        // PhieuNhapDao khong co cache rieng.
    }
}