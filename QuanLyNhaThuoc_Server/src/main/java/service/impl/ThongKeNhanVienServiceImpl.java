package service.impl;

import java.util.Date;
import java.util.List;

import dao.ThongKeNhanVienDao;
import dao.iml.ThongKeNhanVienDaoImpl;
import service.ThongKeNhanVienService;

public class ThongKeNhanVienServiceImpl implements ThongKeNhanVienService {
    private final ThongKeNhanVienDao thongKeNhanVienDao;

    public ThongKeNhanVienServiceImpl() {
        this.thongKeNhanVienDao = new ThongKeNhanVienDaoImpl();
    }

    @Override
    public KetQuaThongKe getThongKe(Date tuNgay, Date denNgay, String maNhanVien, int caLam) {
        ThongKeNhanVienDao.KetQuaThongKe raw = thongKeNhanVienDao.getThongKe(tuNgay, denNgay, maNhanVien, caLam);
        KetQuaThongKe result = new KetQuaThongKe();
        result.tongDoanhSo = raw.tongDoanhSo;
        result.soHoaDon = raw.soHoaDon;
        result.soPhieuTra = raw.soPhieuTra;
        result.tongTienTra = raw.tongTienTra;
        result.soPhieuHuy = raw.soPhieuHuy;
        return result;
    }

    @Override
    public List<String[]> getDanhSachNhanVien() {
        return thongKeNhanVienDao.getDanhSachNhanVien();
    }
}
