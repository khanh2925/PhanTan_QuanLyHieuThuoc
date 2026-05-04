package service.impl;

import java.util.List;

import dao.BangGiaDao;
import dao.iml.BangGiaDaoImpl;
import dao.SanPhamDao;
import dao.iml.SanPhamDaoImpl;
import dto.ChiTietKhuyenMaiSanPhamDTO;
import dto.SanPhamDTO;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.LoaiSanPham;
import entity.SanPham;
import mapper.Mapper;
import service.SanPhamService;

public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamDao sanPhamDao;
    private final BangGiaDao bangGiaDao;

    public SanPhamServiceImpl() {
        this.sanPhamDao = new SanPhamDaoImpl();
        this.bangGiaDao = new BangGiaDaoImpl();
    }

    public SanPhamServiceImpl(SanPhamDao sanPhamDao) {
        this.sanPhamDao = sanPhamDao;
        this.bangGiaDao = new BangGiaDaoImpl();
    }

    @Override
    public List<SanPhamDTO> layTatCaSanPham() {
        return sanPhamDao.layTatCaSanPham().stream()
                .map(this::mapSanPhamCoBangGia)
                .toList();
    }

    @Override
    public SanPhamDTO laySanPhamTheoMa(String maSanPham) {
        return mapSanPhamCoBangGia(sanPhamDao.laySanPhamTheoMa(maSanPham));
    }

    @Override
    public SanPhamDTO timSanPhamTheoSoDangKy(String soDangKy) {
        return mapSanPhamCoBangGia(sanPhamDao.timSanPhamTheoSoDangKy(soDangKy));
    }

    @Override
    public List<SanPhamDTO> timKiemSanPham(String tuKhoa) {
        return sanPhamDao.timKiemSanPham(tuKhoa).stream()
                .map(this::mapSanPhamCoBangGia)
                .toList();
    }

    @Override
    public List<SanPhamDTO> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
        return sanPhamDao.laySanPhamTheoLoai(loaiSP).stream()
                .map(this::mapSanPhamCoBangGia)
                .toList();
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

    private SanPhamDTO mapSanPhamCoBangGia(SanPham sanPham) {
        if (sanPham == null) {
            return null;
        }
        boSungGiaBanHienTai(sanPham);
        return Mapper.map(sanPham, SanPhamDTO.class);
    }

    private void boSungGiaBanHienTai(SanPham sanPham) {
        BangGia bangGiaDangHoatDong = bangGiaDao.layBangGiaDangHoatDong();
        if (bangGiaDangHoatDong == null) {
            return;
        }

        List<ChiTietBangGia> danhSachChiTiet = bangGiaDao.layChiTietTheoMaBangGia(bangGiaDangHoatDong.getMaBangGia());
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return;
        }

        double giaNhap = sanPham.getGiaNhap();
        for (ChiTietBangGia chiTiet : danhSachChiTiet) {
            double giaTu = chiTiet.getGiaTu();
            double giaDen = chiTiet.getGiaDen();
            boolean khopKhoang = giaNhap >= giaTu && (giaDen <= 0 || giaNhap <= giaDen);
            if (!khopKhoang) {
                continue;
            }

            ChiTietBangGia chiTietHienTai = new ChiTietBangGia(
                    bangGiaDangHoatDong,
                    chiTiet.getGiaTu(),
                    chiTiet.getGiaDen(),
                    chiTiet.getTiLe());
            sanPham.setChiTietBangGiaHienTai(chiTietHienTai);
            return;
        }
    }
}