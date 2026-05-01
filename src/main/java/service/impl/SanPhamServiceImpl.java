package service.impl;

import java.util.List;

import dao.SanPhamDao;
import dao.iml.SanPhamDaoImpl;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.SanPhamDTO;
import entity.LoaiSanPham;
import entity.SanPham;
import mapper.Mapper;
import service.SanPhamService;

public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamDao sanPhamDao;

    public SanPhamServiceImpl() {
        this.sanPhamDao = new SanPhamDaoImpl();
    }

    public SanPhamServiceImpl(SanPhamDao sanPhamDao) {
        this.sanPhamDao = sanPhamDao;
    }

    @Override
    public List<SanPhamDTO> layTatCaSanPham() {
        return Mapper.mapList(sanPhamDao.layTatCaSanPham(), SanPhamDTO.class);
    }

    @Override
    public SanPhamDTO laySanPhamTheoMa(String maSanPham) {
        return Mapper.map(sanPhamDao.laySanPhamTheoMa(maSanPham), SanPhamDTO.class);
    }

    @Override
    public SanPhamDTO timSanPhamTheoSoDangKy(String soDangKy) {
        return Mapper.map(sanPhamDao.timSanPhamTheoSoDangKy(soDangKy), SanPhamDTO.class);
    }

    @Override
    public List<SanPhamDTO> timKiemSanPham(String tuKhoa) {
        return Mapper.mapList(sanPhamDao.timKiemSanPham(tuKhoa), SanPhamDTO.class);
    }

    @Override
    public List<SanPhamDTO> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
        return Mapper.mapList(sanPhamDao.laySanPhamTheoLoai(loaiSP), SanPhamDTO.class);
    }

    @Override
    public boolean themSanPham(SanPhamDTO sp) {
        return sanPhamDao.themSanPham(Mapper.map(sp, SanPham.class));
    }

    @Override
    public boolean capNhatSanPham(SanPhamDTO sp) {
        return sanPhamDao.capNhatSanPham(Mapper.map(sp, SanPham.class));
    }

    @Override
    public boolean xoaSanPham(String maSanPham) {
        return sanPhamDao.xoaSanPham(maSanPham);
    }

    @Override
    public List<ChiTietKhuyenMaiSanPhamDTO> layKhuyenMaiDangApDungChoSanPham(String maSanPham) {
        return Mapper.mapList(sanPhamDao.layKhuyenMaiDangApDungChoSanPham(maSanPham), ChiTietKhuyenMaiSanPhamDTO.class);
    }

    @Override
    public void refreshCache() {
        sanPhamDao.refreshCache();
    }
}