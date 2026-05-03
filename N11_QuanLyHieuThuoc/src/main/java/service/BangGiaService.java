package service;

import java.util.List;
import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;

public interface BangGiaService {
    List<BangGiaDTO> layTatCaBangGia();
    BangGiaDTO layBangGiaDangHoatDong();
    BangGiaDTO layBangGiaTheoMa(String maBangGia);
    boolean themBangGia(BangGiaDTO bg);
    boolean capNhatBangGia(BangGiaDTO bg);
    boolean huyHoatDongTatCaTruBangGia(String maBangGia);
    boolean xoaBangGia(String maBangGia);
    String taoMaBangGia();
    List<ChiTietBangGiaDTO> layChiTietTheoMaBangGia(String maBangGia);
    boolean themChiTietBangGia(ChiTietBangGiaDTO ct);
    boolean xoaTatCaChiTiet(String maBangGia);
    void refreshCache();
}