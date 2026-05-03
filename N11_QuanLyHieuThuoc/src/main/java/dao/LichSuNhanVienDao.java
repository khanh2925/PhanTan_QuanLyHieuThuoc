package dao;

import entity.HoaDon;
import entity.PhieuHuy;
import entity.PhieuTra;

import java.time.LocalDate;
import java.util.List;

/**
 * 🔍 Dao chuyên dùng để tra cứu LỊCH SỬ bán / trả / huỷ theo NHÂN VIÊN
 *
 * Ý tưởng: - Pha 1: Query danh sách MÃ chứng từ (HoaDon / PhieuTra / PhieuHuy)
 * theo MaNhanVien + khoảng ngày - Pha 2: Dùng các Dao sẵn có (HoaDonDao,
 * PhieuTraDao, PhieuHuyDao) để load entity đầy đủ
 *
 * Ưu điểm: tránh join nặng + tái sử dụng logic đã có trong các Dao khác
 */
public interface LichSuNhanVienDao {

	/**
	 * Lấy danh sách Hóa đơn do 1 nhân viên lập trong khoảng ngày (có thể null).
	 *
	 * @param maNhanVien Mã nhân viên
	 * @param tuNgay     Ngày bắt đầu (có thể null)
	 * @param denNgay    Ngày kết thúc (có thể null)
	 */
	List<HoaDon> layLichSuBanTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay);

	/**
	 * Lấy danh sách Phiếu trả do 1 nhân viên lập trong khoảng ngày (có thể null).
	 *
	 * @param maNhanVien Mã nhân viên
	 * @param tuNgay     Ngày bắt đầu (có thể null)
	 * @param denNgay    Ngày kết thúc (có thể null)
	 */
	List<PhieuTra> layLichSuTraTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay);

	/**
	 * Lấy danh sách Phiếu hủy do 1 nhân viên lập trong khoảng ngày (có thể null).
	 *
	 * @param maNhanVien Mã nhân viên
	 * @param tuNgay     Ngày bắt đầu (có thể null)
	 * @param denNgay    Ngày kết thúc (có thể null)
	 */
	List<PhieuHuy> layLichSuHuyTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay);

}
