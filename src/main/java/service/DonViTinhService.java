package service;

import java.util.List;

import entity.DonViTinh;

public interface DonViTinhService {
    List<DonViTinh> layTatCaDonViTinh();
    DonViTinh timDonViTinhTheoMa(String maDonViTinh);
    boolean themDonViTinh(DonViTinh dvt);
    boolean capNhatDonViTinh(DonViTinh dvt);
    boolean xoaDonViTinh(String maDonViTinh);
    String taoMaTuDong();
    void refreshCache();
}
