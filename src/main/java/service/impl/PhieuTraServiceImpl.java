package service.impl;

import java.util.List;

import dao.ChiTietPhieuTraDao;
import dao.PhieuTraDao;
import dao.iml.ChiTietPhieuTraDaoImpl;
import dao.iml.PhieuTraDaoImpl;
import dto.ChiTietPhieuTraDTO;
import dto.PhieuTraDTO;
import entity.ChiTietPhieuTra;
import entity.PhieuTra;
import mapper.Mapper;
import service.PhieuTraService;

public class PhieuTraServiceImpl implements PhieuTraService {

    private final PhieuTraDao phieuTraDao;
    private final ChiTietPhieuTraDao chiTietPhieuTraDao;

    public PhieuTraServiceImpl() {
        this.phieuTraDao = new PhieuTraDaoImpl();
        this.chiTietPhieuTraDao = new ChiTietPhieuTraDaoImpl();
    }

    @Override
    public List<PhieuTraDTO> layTatCaPhieuTra() {
        return Mapper.mapList(phieuTraDao.layTatCaPhieuTra(), PhieuTraDTO.class);
    }

    @Override
    public PhieuTra layPhieuTraTheoMa(String maPhieuTra) {
        return phieuTraDao.timKiemPhieuTraBangMa(maPhieuTra);
    }

    @Override
    public List<PhieuTraDTO> timPhieuTraTheoSoDienThoai(String sdt) {
        return Mapper.mapList(phieuTraDao.timPhieuTraTheoSoDienThoai(sdt), PhieuTraDTO.class);
    }

    @Override
    public List<PhieuTraDTO> timPhieuTraTheoKeyword(String keyword) {
        return Mapper.mapList(phieuTraDao.timPhieuTraTheoKeyword(keyword), PhieuTraDTO.class);
    }

    @Override
    public boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet) {
        return phieuTraDao.themPhieuTraVaChiTiet(pt, dsChiTiet);
    }

    @Override
    public String taoMaPhieuTra() {
        return phieuTraDao.taoMaPhieuTra();
    }

    @Override
    public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) {
        return phieuTraDao.demSoPhieuTraHomNayCuaNhanVien(maNhanVien);
    }

    @Override
    public int tongSoLuongDaTra(String maHD, String maLo) {
        return (int) chiTietPhieuTraDao.tongSoLuongDaTra(maHD, maLo);
    }

    @Override
    public boolean daTraLoTrongHoaDon(String maHD, String maLo) {
        return phieuTraDao.daTraLoTrongHoaDon(maHD, maLo);
    }
}
