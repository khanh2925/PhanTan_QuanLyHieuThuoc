package service.impl;

import java.util.List;

import dao.NhanVienDao;
import dao.iml.NhanVienDaoImpl;
import dto.NhanVienDTO;
import entity.NhanVien;
import mapper.Mapper;
import service.NhanVienService;

public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienDao nhanVienDao;

    public NhanVienServiceImpl() {
        this.nhanVienDao = new NhanVienDaoImpl();
    }

    public NhanVienServiceImpl(NhanVienDao nhanVienDao) {
        this.nhanVienDao = nhanVienDao;
    }

    @Override
    public List<NhanVienDTO> layTatCaNhanVien() {
        return Mapper.mapList(nhanVienDao.layTatCaNhanVien(), NhanVienDTO.class);
    }

    @Override
    public NhanVienDTO timNhanVienTheoMa(String maNhanVien) {
        return Mapper.map(nhanVienDao.timNhanVienTheoMa(maNhanVien), NhanVienDTO.class);
    }

    @Override
    public List<NhanVienDTO> timNhanVien(String tuKhoa) {
        return Mapper.mapList(nhanVienDao.timNhanVien(tuKhoa), NhanVienDTO.class);
    }

    @Override
    public List<NhanVienDTO> timNhanVienTheoSoDienThoai(String soDienThoai) {
        return Mapper.mapList(nhanVienDao.timNhanVienTheoSoDienThoai(soDienThoai), NhanVienDTO.class);
    }

    @Override
    public boolean themNhanVien(NhanVienDTO nv) {
        return nhanVienDao.themNhanVien(Mapper.map(nv, NhanVien.class));
    }

    @Override
    public boolean capNhatNhanVien(NhanVienDTO nv) {
        return nhanVienDao.capNhatNhanVien(Mapper.map(nv, NhanVien.class));
    }

    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        return nhanVienDao.xoaNhanVien(maNhanVien);
    }

    @Override
    public boolean capNhatTrangThai(String maNhanVien, boolean trangThai) {
        return nhanVienDao.capNhatTrangThai(maNhanVien, trangThai);
    }

    @Override
    public String taoMaNhanVienTuDong() {
        return nhanVienDao.taoMaNhanVienTuDong();
    }

    @Override
    public void refreshCache() {
        nhanVienDao.refreshCache();
    }
}