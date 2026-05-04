package service;

import java.util.Date;
import java.util.List;

public interface ThongKeNhanVienService {
    class KetQuaThongKe {
        public double tongDoanhSo;
        public int soHoaDon;
        public int soPhieuTra;
        public double tongTienTra;
        public int soPhieuHuy;
    }

    KetQuaThongKe getThongKe(Date tuNgay, Date denNgay, String maNhanVien, int caLam);
    List<String[]> getDanhSachNhanVien();
}
