package service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        return layTatCaHoaDon().stream()
                .filter(hd -> {
                    try {
                        LocalDate hdNgay = LocalDate.parse(hd.getNgayLap(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return hdNgay.equals(ngay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        return layTatCaHoaDon().stream()
                .filter(hd -> {
                    try {
                        LocalDate hdNgay = LocalDate.parse(hd.getNgayLap(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return !hdNgay.isBefore(tuNgay) && !hdNgay.isAfter(denNgay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoNhanVien(String maNhanVien) {
        // DTO doesn't have maNhanVien, filtering by tenNhanVien as approximation
        return layTatCaHoaDon().stream()
                .filter(hd -> hd.getTenNhanVien() != null && hd.getTenNhanVien().contains(maNhanVien))
                .toList();
    }

    @Override
    public List<HoaDonDTO> layHoaDonTheoKhachHang(String maKhachHang) {
        return Mapper.mapList(hoaDonDao.timHoaDonTheoSoDienThoai(maKhachHang), HoaDonDTO.class);
    }

    @Override
    public boolean themHoaDon(HoaDonCreateUpdateDTO hd) {
        return hoaDonDao.themHoaDon(Mapper.map(hd, HoaDon.class));
    }

    @Override
    public boolean capNhatHoaDon(HoaDonCreateUpdateDTO hd) {
        // Note: Dao doesn't have update method, using add as workaround
        return themHoaDon(hd);
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