package iuh.fit.quanlyhieuthuoc.core.repository;

import iuh.fit.quanlyhieuthuoc.core.entity.ChiTietPhieuNhap;

import java.util.List;

public interface ChiTietPhieuNhapRepository {

    List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap);
}
