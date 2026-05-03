package service.impl;

import java.util.List;

import dao.DonViTinhDao;
import dao.iml.DonViTinhDaoImpl;
import entity.DonViTinh;
import service.DonViTinhService;

public class DonViTinhServiceImpl implements DonViTinhService {

    private final DonViTinhDao dvtDao;

    public DonViTinhServiceImpl() {
        this.dvtDao = new DonViTinhDaoImpl();
    }

    @Override
    public List<DonViTinh> layTatCaDonViTinh() {
        return dvtDao.layTatCaDonViTinh();
    }

    @Override
    public DonViTinh timDonViTinhTheoMa(String maDonViTinh) {
        return dvtDao.timDonViTinhTheoMa(maDonViTinh);
    }

    @Override
    public boolean themDonViTinh(DonViTinh dvt) {
        return dvtDao.themDonViTinh(dvt);
    }

    @Override
    public boolean capNhatDonViTinh(DonViTinh dvt) {
        return dvtDao.capNhatDonViTinh(dvt);
    }

    @Override
    public boolean xoaDonViTinh(String maDonViTinh) {
        return dvtDao.xoaDonViTinh(maDonViTinh);
    }

    @Override
    public String taoMaTuDong() {
        return dvtDao.taoMaTuDong();
    }

    @Override
    public void refreshCache() {
        // no-op
    }
}
