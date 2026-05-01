package service.impl;

import java.util.List;

import dao.KhachHangDao;
import dao.iml.KhachHangDaoImpl;
import dto.KhachHangDTO;
import entity.KhachHang;
import mapper.Mapper;
import service.KhachHangService;

public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangDao khachHangDao;

    public KhachHangServiceImpl() {
        this.khachHangDao = new KhachHangDaoImpl();
    }

    public KhachHangServiceImpl(KhachHangDao khachHangDao) {
        this.khachHangDao = khachHangDao;
    }

    @Override
    public List<KhachHangDTO> layTatCaKhachHang() {
        return Mapper.mapList(khachHangDao.layTatCaKhachHang(), KhachHangDTO.class);
    }

    @Override
    public KhachHangDTO timKhachHangTheoMa(String maKhachHang) {
        return Mapper.map(khachHangDao.timKhachHangTheoMa(maKhachHang), KhachHangDTO.class);
    }

    @Override
    public KhachHangDTO timKhachHangTheoSoDienThoai(String soDienThoai) {
        return Mapper.map(khachHangDao.timKhachHangTheoSoDienThoai(soDienThoai), KhachHangDTO.class);
    }

    @Override
    public List<KhachHangDTO> timKiemKhachHang(String tuKhoa) {
        return Mapper.mapList(khachHangDao.timKhachHang(tuKhoa), KhachHangDTO.class);
    }

    @Override
    public boolean themKhachHang(KhachHangDTO kh) {
        return khachHangDao.themKhachHang(Mapper.map(kh, KhachHang.class));
    }

    @Override
    public boolean capNhatKhachHang(KhachHangDTO kh) {
        return khachHangDao.capNhatKhachHang(Mapper.map(kh, KhachHang.class));
    }

    @Override
    public boolean xoaKhachHang(String maKhachHang) {
        return khachHangDao.xoaKhachHang(maKhachHang);
    }

    @Override
    public String taoMaKhachHangTuDong() {
        return khachHangDao.phatSinhMaKhachHangTiepTheo();
    }

    @Override
    public void refreshCache() {
        khachHangDao.refreshCache();
    }
}