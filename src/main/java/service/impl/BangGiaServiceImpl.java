package service.impl;

import java.util.List;

import dao.BangGiaDao;
import dao.iml.BangGiaDaoImpl;
import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;
import entity.BangGia;
import entity.ChiTietBangGia;
import mapper.Mapper;
import service.BangGiaService;

public class BangGiaServiceImpl implements BangGiaService {

    private final BangGiaDao bangGiaDao;

    public BangGiaServiceImpl() {
        this.bangGiaDao = new BangGiaDaoImpl();
    }

    @Override
    public List<BangGiaDTO> layTatCaBangGia() {
        return Mapper.mapList(bangGiaDao.layTatCaBangGia(), BangGiaDTO.class);
    }

    @Override
    public BangGiaDTO layBangGiaDangHoatDong() {
        return Mapper.map(bangGiaDao.layBangGiaDangHoatDong(), BangGiaDTO.class);
    }

    @Override
    public BangGiaDTO layBangGiaTheoMa(String maBangGia) {
        return Mapper.map(bangGiaDao.timBangGiaTheoMa(maBangGia), BangGiaDTO.class);
    }

    @Override
    public boolean themBangGia(BangGiaDTO bg) {
        return bangGiaDao.themBangGia(Mapper.map(bg, BangGia.class));
    }

    @Override
    public boolean capNhatBangGia(BangGiaDTO bg) {
        return bangGiaDao.capNhatBangGia(Mapper.map(bg, BangGia.class));
    }

    @Override
    public boolean huyHoatDongTatCaTruBangGia(String maBangGia) {
        return bangGiaDao.huyHoatDongTatCaTruBangGia(maBangGia);
    }

    @Override
    public boolean xoaBangGia(String maBangGia) {
        return bangGiaDao.xoaBangGia(maBangGia);
    }

    @Override
    public String taoMaBangGia() {
        return bangGiaDao.taoMaBangGia();
    }

    @Override
    public List<ChiTietBangGiaDTO> layChiTietTheoMaBangGia(String maBangGia) {
        return Mapper.mapList(bangGiaDao.layChiTietTheoMaBangGia(maBangGia), ChiTietBangGiaDTO.class);
    }

    @Override
    public boolean themChiTietBangGia(ChiTietBangGiaDTO ct) {
        return bangGiaDao.themChiTietBangGia(Mapper.map(ct, ChiTietBangGia.class));
    }

    @Override
    public boolean xoaTatCaChiTiet(String maBangGia) {
        return bangGiaDao.xoaTatCaChiTiet(maBangGia);
    }

    @Override
    public void refreshCache() {
        bangGiaDao.lamMoiCache();
    }
}