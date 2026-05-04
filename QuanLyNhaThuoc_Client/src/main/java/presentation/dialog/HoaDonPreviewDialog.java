package presentation.dialog;

import javax.swing.*;
import java.awt.*;
import dto.ChiTietHoaDonDTO;
import dto.HoaDonDTO;

public class HoaDonPreviewDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    public HoaDonPreviewDialog(Window owner, HoaDonDTO hd) {
        super(owner, "Xem hóa đơn bán hàng", ModalityType.APPLICATION_MODAL);

        setSize(500, 700); // Chiều cao lớn hơn xíu để chứa đủ thông tin
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        // Vùng hiển thị nội dung HTML
        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setEditable(false);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tạo HTML từ đối tượng Hóa Đơn
        tp.setText(buildHTML(hd));
        
        // Fix lỗi scroll tự chạy xuống cuối
        tp.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(tp);
        scroll.setBorder(null);

        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setBackground(new Color(240, 240, 240));

        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottom.add(btnClose);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String buildHTML(HoaDonDTO hd) {
        StringBuilder html = new StringBuilder();
        String tenKhach = hd.getTenKhachHang() != null ? hd.getTenKhachHang() : "Khách lẻ";
        String sdtKhach = hd.getSdtKhachHang() != null ? hd.getSdtKhachHang() : "";
        String tenNhanVien = hd.getTenNhanVien() != null ? hd.getTenNhanVien() : "";
        String ngayLap = hd.getNgayLap() != null ? hd.getNgayLap() : "";

        html.append("""
        <html>
        <body style='font-family: Segoe UI; padding: 15px;'>
        <h2 style='text-align: center;'>HIỆU THUỐC HOÀ AN</h2>
        <h3 style='text-align: center;'>HÓA ĐƠN BÁN HÀNG</h3>
        <p style='text-align:center;'>Mã HĐ: <b>%s</b></p>
        <hr>
        <p><b>Khách hàng:</b> %s</p>
        <p><b>SĐT:</b> %s</p>
        <p><b>Ngày lập:</b> %s</p>
        <p><b>Nhân viên:</b> %s</p>
        <hr>
        """.formatted(
                hd.getMaHoaDon(),
                tenKhach,
                sdtKhach,
                ngayLap,
                tenNhanVien
        ));

        html.append("""
        <table style='width:100%%; border-collapse: collapse;'>
           <tr style='background-color: #f2f2f2;'>
              <th style='text-align:left; padding: 5px; border-bottom: 1px solid #ccc;'>Tên thuốc</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>ĐVT</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>SL</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>Đơn giá</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>T.Tiền</th>
           </tr>
        """);

        if (hd.getChiTietList() != null) {
            for (ChiTietHoaDonDTO ct : hd.getChiTietList()) {
                String tenSP = ct.getTenSanPham() != null ? ct.getTenSanPham() : "";
                String dvt = ct.getDonViTinh() != null ? ct.getDonViTinh() : "";

                html.append("""
                <tr>
                   <td style='padding: 5px; border-bottom: 1px solid #eee;'>%s</td>
                   <td style='text-align:center; padding: 5px; border-bottom: 1px solid #eee; font-size: 12px;'>%s</td>
                   <td style='text-align:center; padding: 5px; border-bottom: 1px solid #eee;'>%d</td>
                   <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
                   <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
                </tr>
                """.formatted(tenSP, dvt, ct.getSoLuong(), ct.getDonGia(), ct.getThanhTien()));
            }
        }

        html.append("</table>");

        double tongGiamGia = hd.getGiamGia() > 0 ? hd.getGiamGia() : hd.getTongTien() - hd.getThanhToan();

        html.append("<div style='text-align:right; margin-top: 15px;'>");
        html.append(String.format("<p>Tổng tiền hàng: <b>%,.0f đ</b></p>", hd.getTongTien()));

        if (tongGiamGia > 0) {
            html.append(String.format("<p>Giảm giá: -%,.0f đ</p>", tongGiamGia));
        }

        html.append("<hr>");
        html.append(String.format("<h3 style='color:#D32F2F; margin: 10px 0;'>TỔNG THANH TOÁN: %,.0f đ</h3>", hd.getThanhToan()));
        html.append("</div>");

        html.append("""
        <hr>
        <p style='text-align:center; font-style:italic;'>Cảm ơn quý khách & Hẹn gặp lại!</p>
        </body></html>
        """);

        return html.toString();
    }
}
