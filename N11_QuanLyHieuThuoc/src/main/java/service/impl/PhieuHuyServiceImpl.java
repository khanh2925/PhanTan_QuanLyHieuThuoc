package service.impl;

import dao.ChiTietPhieuHuyDao;
import dao.PhieuHuyDao;
import dao.iml.ChiTietPhieuHuyDaoImpl;
import dao.iml.PhieuHuyDaoImpl;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;
import service.PhieuHuyService;

import java.util.List;

public class PhieuHuyServiceImpl implements PhieuHuyService {

    private final PhieuHuyDao phieuHuyDao;
    private final ChiTietPhieuHuyDao chiTietPhieuHuyDao;

    public PhieuHuyServiceImpl() {
        this.phieuHuyDao = new PhieuHuyDaoImpl();
        this.chiTietPhieuHuyDao = new ChiTietPhieuHuyDaoImpl();
    }

    @Override public List<PhieuHuy> layTatCaPhieuHuy() { return phieuHuyDao.layTatCaPhieuHuy(); }

    @Override public PhieuHuy layPhieuHuyTheoMa(String ma) { return phieuHuyDao.layTheoMa(ma); }

    @Override public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String ma) { return phieuHuyDao.layChiTietTheoMaPhieu(ma); }

    @Override public boolean themPhieuHuy(PhieuHuy ph) { return phieuHuyDao.themPhieuHuy(ph); }

    @Override public String taoMaPhieuHuy() { return phieuHuyDao.taoMaPhieuHuy(); }

    @Override public boolean kiemTraTrangThai(String ma) { return phieuHuyDao.checkTrangThai(ma); }

    @Override public boolean capNhatTrangThaiPhieuHuy(String ma) { return phieuHuyDao.capNhatTrangThaiPhieuHuy(ma); }

    @Override public boolean capNhatTrangThaiChiTiet(String maPH, String maLo, int trangThai) {
        return chiTietPhieuHuyDao.capNhatTrangThaiChiTiet(maPH, maLo, trangThai);
    }

    @Override public int demPhieuHuyChuaDuyet() { return phieuHuyDao.demPhieuHuyChuaDuyet(); }

    @Override public double tinhTongTienHuyTheoThang(int thang, int nam) { return phieuHuyDao.tinhTongTienHuyTheoThang(thang, nam); }

    @Override public int demSoPhieuHuyHomNayCuaNhanVien(String maNV) { return phieuHuyDao.demSoPhieuHuyHomNayCuaNhanVien(maNV); }

    @Override public List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPH) {
        return chiTietPhieuHuyDao.timKiemChiTietPhieuHuyBangMa(maPH);
    }
}
