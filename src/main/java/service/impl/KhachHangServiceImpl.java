package service.impl;

import java.util.List;

import dao.KhachHangDao;
import dao.iml.KhachHangDaoImpl;
import entity.KhachHang;
import service.KhachHangService;

public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangDao khachHangDao;

    public KhachHangServiceImpl() {
        this.khachHangDao = new KhachHangDaoImpl();
    }

    @Override
    public List<KhachHang> layTatCaKhachHang() {
        return khachHangDao.layTatCaKhachHang();
    }

    @Override
    public KhachHang layKhachHangTheoMa(String maKhachHang) {
        return khachHangDao.timKhachHangTheoMa(maKhachHang);
    }

    @Override
    public KhachHang layKhachHangTheoSDT(String soDienThoai) {
        return khachHangDao.timKhachHangTheoSDT(soDienThoai);
    }

    @Override
    public boolean themKhachHang(KhachHang khachHang) {
        return khachHangDao.themKhachHang(khachHang);
    }

    @Override
    public boolean capNhatKhachHang(KhachHang khachHang) {
        return khachHangDao.capNhatKhachHang(khachHang);
    }

    @Override
    public boolean xoaKhachHang(String maKhachHang) {
        return khachHangDao.xoaKhachHang(maKhachHang);
    }

    @Override
    public String phatSinhMaKhachHangTiepTheo() {
        return khachHangDao.phatSinhMaKhachHangTiepTheo();
    }

    @Override
    public void refreshCache() {
        khachHangDao.refreshCache();
    }
}
