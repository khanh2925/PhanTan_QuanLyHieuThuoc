package presentation.nhanvien;

import com.toedter.calendar.JDateChooser;
import presentation.component.button.PillButton;
import presentation.component.chart.BieuDoCotJFreeChart;
import presentation.component.chart.DuLieuBieuDoCot;
import network.ClientService;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ThongKeNhanVien_GUI extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 2091812809316193815L;
    private JDateChooser dateTuNgay, dateDenNgay;
    private JComboBox<String> cmbCaLam;
    private JButton btnLoc, btnXuatExcel;
    private JLabel lblTongDoanhSo, lblSoHoaDon, lblTrungBinhDon;
    private JLabel lblSoPhieuTra, lblSoPhieuHuy, lblTyLeHoan;
    private BieuDoCotJFreeChart bieuDoHieuSuat;
    private final ClientService svc;
    private final String maNhanVienHienTai;

    private double tongDoanhSo;
    private int soHoaDon;
    private double giaTriTrungBinh;
    private int soPhieuTra;
    private double tongTienTra;
    private int soPhieuHuy;
    private double tyLeHoanTra;

    public ThongKeNhanVien_GUI(String maNV) {
        this.maNhanVienHienTai = maNV;
        this.svc = new ClientService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnFilter.add(lblTuNgay);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd-MM-yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTuNgay.setBounds(20, 50, 150, 30);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateTuNgay.setDate(cal.getTime());
        pnFilter.add(dateTuNgay);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(200, 25, 80, 20);
        pnFilter.add(lblDenNgay);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd-MM-yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDenNgay.setBounds(200, 50, 150, 30);
        dateDenNgay.setDate(new Date());
        pnFilter.add(dateDenNgay);

        JLabel lblCaLam = new JLabel("Ca làm");
        lblCaLam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCaLam.setBounds(380, 25, 80, 20);
        pnFilter.add(lblCaLam);

        cmbCaLam = new JComboBox<>();
        cmbCaLam.addItem("Tất cả");
        cmbCaLam.addItem("Ca 1 (Sáng)");
        cmbCaLam.addItem("Ca 2 (Chiều)");
        cmbCaLam.addItem("Ca 3 (Tối)");
        cmbCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCaLam.setBounds(380, 50, 130, 30);
        pnFilter.add(cmbCaLam);

        btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(540, 45, 120, 35);
        pnFilter.add(btnLoc);

        btnXuatExcel = new PillButton("Xuất Excel");
        btnXuatExcel.setBounds(670, 45, 120, 35);
        pnFilter.add(btnXuatExcel);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan hiệu suất"),
                new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 180));

        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);

        lblTongDoanhSo = createLabel(pnStats, "Doanh số bán:", fTitle, fValue, new Color(40, 167, 69));
        lblSoHoaDon = createLabel(pnStats, "Số hóa đơn:", fTitle, fValue, new Color(0x005a9e));
        lblTrungBinhDon = createLabel(pnStats, "TB / Hóa đơn:", fTitle, fValue, new Color(102, 16, 242));
        lblSoPhieuTra = createLabel(pnStats, "Số phiếu trả:", fTitle, fValue, new Color(255, 140, 0));
        lblSoPhieuHuy = createLabel(pnStats, "Số phiếu hủy:", fTitle, fValue, new Color(220, 53, 69));
        lblTyLeHoan = createLabel(pnStats, "Tỷ lệ hoàn trả:", fTitle, fValue, Color.DARK_GRAY);

        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ hiệu suất cá nhân"));
        pnChart.setBackground(Color.WHITE);

        bieuDoHieuSuat = new BieuDoCotJFreeChart();
        bieuDoHieuSuat.setTieuDeTrucX("Chỉ số");
        bieuDoHieuSuat.setTieuDeTrucY("Số lượng");
        pnChart.add(bieuDoHieuSuat, BorderLayout.CENTER);

        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnStats, BorderLayout.NORTH);
        pnTopSection.add(pnChart, BorderLayout.CENTER);
        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnMain.add(pnContent, BorderLayout.CENTER);

        btnLoc.addActionListener(this);
        btnLoc.addMouseListener(this);
        btnXuatExcel.addActionListener(this);
        btnXuatExcel.addMouseListener(this);

        loadData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnLoc)) {
            loadData();
        } else if (source.equals(btnXuatExcel)) {
            xuatFileExcel();
        }
    }

    @Override public void mouseEntered(MouseEvent e) { if (e.getSource() instanceof JButton b) b.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
    @Override public void mouseExited(MouseEvent e) { if (e.getSource() instanceof JButton b) b.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}

    private JLabel createLabel(JPanel p, String t, Font f1, Font f2, Color c) {
        JPanel pChild = new JPanel(new BorderLayout(5, 5));
        pChild.setOpaque(false);
        JLabel lTitle = new JLabel(t);
        lTitle.setFont(f1);
        JLabel lValue = new JLabel("0");
        lValue.setFont(f2);
        lValue.setForeground(c);
        pChild.add(lTitle, BorderLayout.NORTH);
        pChild.add(lValue, BorderLayout.CENTER);
        p.add(pChild);
        return lValue;
    }

    private void loadData() {
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();
        if (tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày tháng!");
            return;
        }
        if (tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }
        int caLam = cmbCaLam.getSelectedIndex();
        updateStatsFromService(tu, den, caLam);
        renderStats(tu, den);
    }

    private void updateStatsFromService(Date tu, Date den, int caLam) {
        try {
            Object remote = svc.getThongKeNhanVien(maNhanVienHienTai, tu, den, caLam);
            if (remote != null) {
                java.lang.reflect.Field f;
                f = remote.getClass().getDeclaredField("tongDoanhSo"); f.setAccessible(true); tongDoanhSo = getDouble(f.get(remote));
                f = remote.getClass().getDeclaredField("soHoaDon"); f.setAccessible(true); soHoaDon = getInt(f.get(remote));
                f = remote.getClass().getDeclaredField("tongTienTra"); f.setAccessible(true); tongTienTra = getDouble(f.get(remote));
                f = remote.getClass().getDeclaredField("soPhieuTra"); f.setAccessible(true); soPhieuTra = getInt(f.get(remote));
                f = remote.getClass().getDeclaredField("soPhieuHuy"); f.setAccessible(true); soPhieuHuy = getInt(f.get(remote));
                f = remote.getClass().getDeclaredField("tyLeHoanTra"); f.setAccessible(true); tyLeHoanTra = getDouble(f.get(remote));
                giaTriTrungBinh = soHoaDon > 0 ? tongDoanhSo / soHoaDon : 0;
                return;
            }
        } catch (Exception ignored) {
        }
        tongDoanhSo = 0;
        soHoaDon = 0;
        giaTriTrungBinh = 0;
        soPhieuTra = 0;
        tongTienTra = 0;
        soPhieuHuy = 0;
        tyLeHoanTra = 0;
    }

    private void renderStats(Date tu, Date den) {
        DecimalFormat dfTien = new DecimalFormat("#,##0 đ");
        DecimalFormat dfSo = new DecimalFormat("#,##0");
        DecimalFormat dfTyLe = new DecimalFormat("0.00'%'");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String range = String.format("(%s - %s)", sdf.format(tu), sdf.format(den));
        String caText = cmbCaLam.getSelectedItem().toString();

        lblTongDoanhSo.setText("<html>" + dfTien.format(tongDoanhSo) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + range + "</span></html>");
        lblSoHoaDon.setText("<html>" + dfSo.format(soHoaDon) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + caText + "</span></html>");
        lblTrungBinhDon.setText(dfTien.format(giaTriTrungBinh));
        lblSoPhieuTra.setText("<html>" + dfSo.format(soPhieuTra) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>Tiền trả: " + dfTien.format(tongTienTra) + "</span></html>");
        lblSoPhieuHuy.setText(dfSo.format(soPhieuHuy));
        lblTyLeHoan.setText(dfTyLe.format(tyLeHoanTra));

        bieuDoHieuSuat.xoaToanBoDuLieu();
        bieuDoHieuSuat.setTieuDeBieuDo("Tương quan các chỉ số giao dịch");
        Color c1 = new Color(0x005a9e);
        Color c2 = new Color(255, 140, 0);
        Color c3 = new Color(220, 53, 69);
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Hóa Đơn", "Giao dịch", soHoaDon, c1));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Trả", "Giao dịch", soPhieuTra, c2));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Hủy", "Giao dịch", soPhieuHuy, c3));
    }

    private void xuatFileExcel() {
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();
        if (tu == null || den == null || tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hợp lệ để xuất file!");
            return;
        }

        int caLam = cmbCaLam.getSelectedIndex();
        updateStatsFromService(tu, den, caLam);
        renderStats(tu, den);
        String tenNhanVien = layTenNhanVienHienTai();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo hiệu suất cá nhân");
        fileChooser.setSelectedFile(new File("BaoCao_" + tenNhanVien.replaceAll("\\s+", "_") + "_" + maNhanVienHienTai + ".xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) file = new File(file.getAbsolutePath() + ".xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hiệu Suất Cá Nhân");
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0"));

            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("BÁO CÁO HIỆU SUẤT CÁ NHÂN");
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sheet.createRow(1).createCell(0).setCellValue("Thời gian: " + sdf.format(tu) + " - " + sdf.format(den));
            sheet.createRow(2).createCell(0).setCellValue("Nhân viên: " + maNhanVienHienTai + " - " + tenNhanVien);
            sheet.createRow(3).createCell(0).setCellValue("Ca làm việc: " + cmbCaLam.getSelectedItem());

            Row headerRow = sheet.createRow(5);
            String[] headers = {"Chỉ số", "Giá trị", "Ghi chú"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 6;
            addMetricRow(sheet, rowNum++, "Tổng doanh số", tongDoanhSo, currencyStyle, "VNĐ");
            addMetricRow(sheet, rowNum++, "Số lượng hóa đơn", soHoaDon, null, "Hóa đơn");
            addMetricRow(sheet, rowNum++, "Giá trị trung bình/HĐ", giaTriTrungBinh, currencyStyle, "VNĐ");
            addMetricRow(sheet, rowNum++, "Số phiếu trả hàng", soPhieuTra, null, "Phiếu");
            addMetricRow(sheet, rowNum++, "Tổng tiền trả lại", tongTienTra, currencyStyle, "VNĐ");
            addMetricRow(sheet, rowNum++, "Số phiếu hủy", soPhieuHuy, null, "Phiếu");

            Row rateRow = sheet.createRow(rowNum++);
            rateRow.createCell(0).setCellValue("Tỷ lệ hoàn trả");
            Cell rateVal = rateRow.createCell(1);
            rateVal.setCellValue(tyLeHoanTra / 100.0);
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(format.getFormat("0.00%"));
            rateVal.setCellStyle(percentStyle);
            rateRow.createCell(2).setCellValue("% theo số lượng");

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + file.getAbsolutePath());
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage());
        }
    }

    private String layTenNhanVienHienTai() {
        try {
            java.util.List<?> dsNV = svc.getDanhSachNhanVienThongKe();
            if (dsNV != null) {
                for (Object obj : dsNV) {
                    if (obj instanceof String[] nv && nv.length >= 2 && nv[0] != null && nv[0].trim().equalsIgnoreCase(maNhanVienHienTai.trim())) {
                        return nv[1];
                    }
                }
            }
        } catch (Exception ignored) {}
        return "Không xác định";
    }

    private int getInt(Object o) { return o instanceof Number n ? n.intValue() : 0; }
    private double getDouble(Object o) { return o instanceof Number n ? n.doubleValue() : 0d; }

    private void addMetricRow(Sheet sheet, int rowIdx, String label, double val, CellStyle style, String note) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        Cell valCell = row.createCell(1);
        valCell.setCellValue(val);
        if (style != null) valCell.setCellStyle(style);
        row.createCell(2).setCellValue(note);
    }
}
