package service.impl;

import java.util.List;

import dao.QuyCachDongGoiDao;
import dao.iml.QuyCachDongGoiDaoImpl;
import entity.QuyCachDongGoi;
import service.QuyCachDongGoiService;

public class QuyCachDongGoiServiceImpl implements QuyCachDongGoiService {
    private final QuyCachDongGoiDao quyCachDongGoiDao;

    public QuyCachDongGoiServiceImpl() {
        this.quyCachDongGoiDao = new QuyCachDongGoiDaoImpl();
    }

    @Override
    public List<QuyCachDongGoi> layTatCaQuyCachDongGoi() {
        return quyCachDongGoiDao.layTatCaQuyCachDongGoi();
    }

    @Override
    public String taoMaQuyCach() {
        return quyCachDongGoiDao.taoMaQuyCach();
    }

    @Override
    public boolean themQuyCachDongGoi(QuyCachDongGoi quyCach) {
        return quyCachDongGoiDao.themQuyCachDongGoi(quyCach);
    }

    @Override
    public boolean capNhatQuyCachDongGoi(QuyCachDongGoi quyCach) {
        return quyCachDongGoiDao.capNhatQuyCachDongGoi(quyCach);
    }

    @Override
    public QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham) {
        return quyCachDongGoiDao.timQuyCachGocTheoSanPham(maSanPham);
    }

    @Override
    public List<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham) {
        return quyCachDongGoiDao.layDanhSachQuyCachTheoSanPham(maSanPham);
    }

    @Override
    public QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh) {
        return quyCachDongGoiDao.timQuyCachTheoSanPhamVaDonVi(maSanPham, maDonViTinh);
    }

    @Override
    public boolean xoaQuyCachDongGoi(String maQuyCach) {
        return quyCachDongGoiDao.xoaQuyCachDongGoi(maQuyCach);
    }
}
