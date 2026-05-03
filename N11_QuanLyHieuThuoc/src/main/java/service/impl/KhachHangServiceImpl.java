package service.impl;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<KhachHang> layTatCaKhachHang() {
        return khachHangDao.layTatCaKhachHang();
    }

    @Override
    public KhachHangDTO timKhachHangTheoMa(String maKhachHang) {
        KhachHang kh = khachHangDao.timKhachHangTheoMa(maKhachHang);
        return kh != null ? Mapper.map(kh, KhachHangDTO.class) : null;
    }

    @Override
    public KhachHangDTO timKhachHangTheoSoDienThoai(String soDienThoai) {
        KhachHang kh = khachHangDao.timKhachHangTheoSoDienThoai(soDienThoai);
        return kh != null ? Mapper.map(kh, KhachHangDTO.class) : null;
    }

    @Override
    public List<KhachHangDTO> timKiemKhachHang(String tuKhoa) {
        return khachHangDao.timKhachHang(tuKhoa).stream()
                .map(kh -> Mapper.map(kh, KhachHangDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean themKhachHang(KhachHangDTO khachHangDTO) {
        KhachHang kh = Mapper.map(khachHangDTO, KhachHang.class);
        return kh != null && khachHangDao.themKhachHang(kh);
    }

    @Override
    public boolean capNhatKhachHang(KhachHangDTO khachHangDTO) {
        KhachHang kh = Mapper.map(khachHangDTO, KhachHang.class);
        return kh != null && khachHangDao.capNhatKhachHang(kh);
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
