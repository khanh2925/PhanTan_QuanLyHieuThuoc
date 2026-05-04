package service.impl;

import java.util.List;

import dao.ChiTietKhuyenMaiSanPhamDao;
import dao.KhuyenMaiDao;
import dao.iml.ChiTietKhuyenMaiSanPhamDaoImpl;
import dao.iml.KhuyenMaiDaoImpl;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.KhuyenMaiDTO;
import entity.KhuyenMai;
import mapper.Mapper;
import service.KhuyenMaiService;

public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiDao khuyenMaiDao;
    private final ChiTietKhuyenMaiSanPhamDao chiTietKhuyenMaiDao;

    public KhuyenMaiServiceImpl() {
        this.khuyenMaiDao = new KhuyenMaiDaoImpl();
        this.chiTietKhuyenMaiDao = new ChiTietKhuyenMaiSanPhamDaoImpl();
    }

    @Override
    public List<KhuyenMaiDTO> layTatCaKhuyenMai() {
        return Mapper.mapList(khuyenMaiDao.layTatCaKhuyenMai(), KhuyenMaiDTO.class);
    }

    @Override
    public KhuyenMaiDTO layKhuyenMaiTheoMa(String maKM) {
        return Mapper.map(khuyenMaiDao.timKhuyenMaiTheoMa(maKM), KhuyenMaiDTO.class);
    }

    @Override
    public List<KhuyenMaiDTO> layKhuyenMaiDangHoatDong() {
        return Mapper.mapList(khuyenMaiDao.layKhuyenMaiDangHoatDong(), KhuyenMaiDTO.class);
    }

    @Override
    public List<ChiTietKhuyenMaiSanPhamDTO> layChiTietKhuyenMaiTheoMaKM(String maKM) {
        return Mapper.mapList(chiTietKhuyenMaiDao.layChiTietKhuyenMaiTheoMaCoJoin(maKM), ChiTietKhuyenMaiSanPhamDTO.class);
    }

    @Override
    public boolean themKhuyenMai(KhuyenMaiDTO km) {
        return khuyenMaiDao.themKhuyenMai(Mapper.map(km, KhuyenMai.class));
    }

    @Override
    public boolean themChiTietKhuyenMaiSanPham(String maKM, String maSanPham) {
        KhuyenMai km = khuyenMaiDao.timKhuyenMaiTheoMa(maKM);
        if (km == null || km.isKhuyenMaiHoaDon()) return false;
        List<entity.ChiTietKhuyenMaiSanPham> ds = chiTietKhuyenMaiDao.timKiemChiTietKhuyenMaiSanPhamBangMa(maKM);
        for (entity.ChiTietKhuyenMaiSanPham ct : ds) {
            if (ct.getSanPham() != null && maSanPham.equals(ct.getSanPham().getMaSanPham())) return true;
        }
        entity.SanPham sp = new dao.iml.SanPhamDaoImpl().laySanPhamTheoMa(maSanPham);
        if (sp == null) return false;
        return chiTietKhuyenMaiDao.themChiTietKhuyenMaiSanPham(new entity.ChiTietKhuyenMaiSanPham(sp, km));
    }

    @Override
    public boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSanPham) {
        return chiTietKhuyenMaiDao.xoaChiTietKhuyenMaiSanPham(maKM, maSanPham);
    }

    @Override
    public boolean capNhatKhuyenMai(KhuyenMaiDTO km) {
        return khuyenMaiDao.capNhatKhuyenMai(Mapper.map(km, KhuyenMai.class));
    }

    @Override
    public boolean xoaKhuyenMai(String maKM) {
        return khuyenMaiDao.xoaKhuyenMai(maKM);
    }

    @Override
    public String taoMaKhuyenMaiTuDong() {
        return khuyenMaiDao.taoMaKhuyenMai();
    }

    @Override
    public void refreshCache() {
        // no-op
    }
}
