package service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import dao.LoSanPhamDao;
import dao.iml.LoSanPhamDaoImpl;
import dto.LoSanPhamDTO;
import entity.LoSanPham;
import mapper.Mapper;
import service.LoSanPhamService;

public class LoSanPhamServiceImpl implements LoSanPhamService {

    private final LoSanPhamDao loSanPhamDao;

    public LoSanPhamServiceImpl() {
        this.loSanPhamDao = new LoSanPhamDaoImpl();
    }

    public LoSanPhamServiceImpl(LoSanPhamDao loSanPhamDao) {
        this.loSanPhamDao = loSanPhamDao;
    }

    @Override
    public List<LoSanPhamDTO> layTatCaLoSanPham() {
        return Mapper.mapList(loSanPhamDao.layTatCaLoSanPham(), LoSanPhamDTO.class);
    }

    @Override
    public LoSanPhamDTO layLoSanPhamTheoMa(String maLo) {
        return Mapper.map(loSanPhamDao.timLoTheoMa(maLo), LoSanPhamDTO.class);
    }

    @Override
    public List<LoSanPhamDTO> layLoSanPhamTheoMaSanPham(String maSanPham) {
        return Mapper.mapList(loSanPhamDao.layDanhSachLoTheoMaSanPham(maSanPham), LoSanPhamDTO.class);
    }

    @Override
    public List<LoSanPhamDTO> layLoSanPhamConHang(String maSanPham) {
        return Mapper.mapList(loSanPhamDao.layDanhSachLoTheoMaSanPham(maSanPham), LoSanPhamDTO.class);
    }

    @Override
    public List<LoSanPhamDTO> layLoSanPhamSapHetHan(int soNgay) {
        List<LoSanPham> danhSach = loSanPhamDao.layDanhSachLoSPToiHanSuDung();
        if (soNgay > 0 && soNgay < 90) {
            LocalDate hanToiDa = LocalDate.now().plusDays(soNgay);
            danhSach = danhSach.stream()
                    .filter(lo -> lo.getHanSuDung() != null && !lo.getHanSuDung().isAfter(hanToiDa))
                    .collect(Collectors.toList());
        }
        return Mapper.mapList(danhSach, LoSanPhamDTO.class);
    }

    @Override
    public List<LoSanPhamDTO> layLoSanPhamTonKhoThap(int nguongTon) {
        List<LoSanPham> danhSach = loSanPhamDao.layTatCaLoSanPham().stream()
                .filter(lo -> lo.getSoLuongTon() <= nguongTon)
                .collect(Collectors.toList());
        return Mapper.mapList(danhSach, LoSanPhamDTO.class);
    }

    @Override
    public boolean themLoSanPham(LoSanPhamDTO lo) {
        return loSanPhamDao.themLoSanPham(Mapper.map(lo, LoSanPham.class));
    }

    @Override
    public boolean capNhatLoSanPham(LoSanPhamDTO lo) {
        return loSanPhamDao.capNhatLoSanPham(Mapper.map(lo, LoSanPham.class));
    }

    @Override
    public boolean capNhatSoLuongTon(String maLo, int soLuongMoi) {
        LoSanPham lo = loSanPhamDao.timLoTheoMa(maLo);
        if (lo == null) return false;
        lo.setSoLuongTon(soLuongMoi);
        return loSanPhamDao.capNhatLoSanPham(lo);
    }

    @Override
    public String taoMaLoTuDong(String maSanPham) {
        return loSanPhamDao.taoMaLoTuDong();
    }

    @Override
    public void refreshCache() {
        loSanPhamDao.clearCache();
    }
}