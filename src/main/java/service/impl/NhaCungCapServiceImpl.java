package service.impl;

import java.util.List;

import dao.NhaCungCapDao;
import dao.iml.NhaCungCapDaoImpl;
import entity.NhaCungCap;
import service.NhaCungCapService;

public class NhaCungCapServiceImpl implements NhaCungCapService {

    private final NhaCungCapDao nccDao;

    public NhaCungCapServiceImpl() {
        this.nccDao = new NhaCungCapDaoImpl();
    }

    @Override
    public List<NhaCungCap> layTatCaNhaCungCap() {
        return nccDao.layTatCaNhaCungCap();
    }

    @Override
    public NhaCungCap layNhaCungCapTheoMaHoacSDT(String keyword) {
        return nccDao.timNhaCungCapTheoMaHoacSDT(keyword);
    }

    @Override
    public List<NhaCungCap> timKiemNCC(String keyword, String khuVuc, String trangThai, String tieuChi) {
        return nccDao.timKiemNCC(keyword, khuVuc, trangThai, tieuChi);
    }

    @Override
    public boolean themNhaCungCap(NhaCungCap ncc) {
        return nccDao.themNhaCungCap(ncc);
    }

    @Override
    public boolean capNhatNhaCungCap(NhaCungCap ncc) {
        return nccDao.capNhatNhaCungCap(ncc);
    }

    @Override
    public String taoMaTuDong() {
        return nccDao.taoMaTuDong();
    }

    @Override
    public void refreshCache() {
        nccDao.refreshCache();
    }
}
