package service;

import java.util.List;

import entity.NhaCungCap;

public interface NhaCungCapService {
    List<NhaCungCap> layTatCaNhaCungCap();
    NhaCungCap layNhaCungCapTheoMaHoacSDT(String keyword);
    List<NhaCungCap> timKiemNCC(String keyword, String khuVuc, String trangThai, String tieuChi);
    boolean themNhaCungCap(NhaCungCap ncc);
    boolean capNhatNhaCungCap(NhaCungCap ncc);
    String taoMaTuDong();
    void refreshCache();
}
