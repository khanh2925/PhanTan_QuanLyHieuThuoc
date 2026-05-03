package service.impl;

import java.time.LocalDate;
import java.util.List;

import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.iml.ChiTietHoaDonDaoImpl;
import dao.iml.HoaDonDaoImpl;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonCreateUpdateDTO;
import dto.HoaDonDTO;
import entity.HoaDon;
import mapper.Mapper;
import service.HoaDonService;

public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonDao hoaDonDao;
    private final ChiTietHoaDonDao chiTietHoaDonDao;

    public HoaDonServiceImpl() {
        this.hoaDonDao = new HoaDonDaoImpl();
        this.chiTietHoaDonDao = new ChiTietHoaDonDaoImpl();
    }

    @Override
    public List<HoaDonDTO> layTatCaHoaDon() {
        return Mapper.mapList(hoaDonDao.layTatCaHoaDon(), HoaDonDTO.class);
    }

    @Override
    public HoaDonDTO layHoaDonTheoMa(String maHoaDon) {
        return Mapper.map(hoaDonDao.timHoaDonTheoMa(maHoaDon), HoaDonDTO.class);
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoNgay(LocalDate ngay) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoNhanVien(String maNhanVien) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoKhachHang(String maKhachHang) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public boolean themHoaDon(HoaDonCreateUpdateDTO hd) {
        return hoaDonDao.themHoaDon(Mapper.map(hd, HoaDon.class));
    }

    @Override
    public boolean capNhatHoaDon(HoaDonCreateUpdateDTO hd) {
        throw new UnsupportedOperationException("Chua duoc ho tro o Dao");
    }

    @Override
    public List<ChiTietHoaDonDTO> layChiTietHoaDon(String maHoaDon) {
        return Mapper.mapList(chiTietHoaDonDao.layDanhSachChiTietTheoMaHD(maHoaDon), ChiTietHoaDonDTO.class);
    }

    @Override
    public String taoMaHoaDonTuDong() {
        return hoaDonDao.taoMaHoaDon();
    }

    @Override
    public void refreshCache() {
        hoaDonDao.refreshCache();
    }
}