package presentation.quanly;

import com.toedter.calendar.JDateChooser;
import dto.BangGiaDTO;
import dto.ChiTietBangGiaDTO;
import entity.SanPham;
import network.ClientService;
import presentation.component.border.RoundedBorder;
import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BangGia_GUI extends JPanel implements ActionListener, MouseListener {

    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    private final ClientService svc;
    private final java.text.DecimalFormat dfNumber = new java.text.DecimalFormat("#,###");

    private JPanel pnHeader, pnCenter;
    private JTextField txtMaBG, txtTenBG, txtTimKiem, txtGiaTu, txtGiaDen, txtTiLe;
    private JDateChooser txtNgayApDung;
    private JComboBox<String> cboTrangThai;
    private JCheckBox chkHoatDong, chkKhoangCuoi;
    private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnThemCT, btnSuaCT, btnXoaCT, btnLamMoiCT;
    private JTable tblBangGia, tblChiTiet, tblMoPhong;
    private DefaultTableModel modelBangGia, modelChiTiet, modelMoPhong;

    private String maBangGiaDangChon;
    private final List<ChiTietBangGiaDTO> dsChiTietTam = new ArrayList<>();
    private int chiTietDangChon = -1;

    public BangGia_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        svc = new ClientService();
        initialize();
        setupKeyboardShortcuts();
        addFocusOnShow();
        lamMoiVaTaiBangGia();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
    }

    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên bảng giá... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("TÌM KIẾM");
        btnTimKiem.setBounds(540, 22, 160, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(this);
        pnHeader.add(btnTimKiem);
    }

    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);

        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin bảng giá"));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangDanhSach(pnTab1);
        tabbedPane.addTab("Danh sách Bảng Giá", pnTab1);

        JPanel pnTab2 = new JPanel(new BorderLayout());
        pnTab2.setBackground(Color.WHITE);
        taoBangChiTiet(pnTab2);
        tabbedPane.addTab("Cấu hình Quy tắc giá", pnTab2);

        JPanel pnTab3 = new JPanel(new BorderLayout());
        pnTab3.setBackground(Color.WHITE);
        taoBangMoPhong(pnTab3);
        tabbedPane.addTab("Xem thử giá bán (Mô phỏng)", pnTab3);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
        splitPane.setDividerLocation(290);
        splitPane.setResizeWeight(0.0);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40, hText = 35, wLbl = 120, wTxt = 300, gap = 25;

        JLabel lblMaBG = new JLabel("Mã BG:");
        lblMaBG.setFont(FONT_TEXT);
        lblMaBG.setBounds(xStart, yStart, 120, 35);
        p.add(lblMaBG);

        txtMaBG = new JTextField();
        txtMaBG.setFont(FONT_TEXT);
        txtMaBG.setBounds(xStart + wLbl, yStart, wTxt, 35);
        txtMaBG.setEditable(false);
        p.add(txtMaBG);

        JLabel lblTenBG = new JLabel("Tên BG:");
        lblTenBG.setFont(FONT_TEXT);
        lblTenBG.setBounds(xStart, yStart + (hText + gap), 120, 35);
        p.add(lblTenBG);

        txtTenBG = new JTextField();
        txtTenBG.setFont(FONT_TEXT);
        txtTenBG.setBounds(xStart + wLbl, yStart + (hText + gap), wTxt, 35);
        PlaceholderSupport.addPlaceholder(txtTenBG, "Nhập tên bảng giá");
        p.add(txtTenBG);

        chkHoatDong = new JCheckBox("Đặt làm bảng giá mặc định (Áp dụng ngay)");
        chkHoatDong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        chkHoatDong.setForeground(new Color(0, 100, 0));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(xStart + wLbl, yStart + (hText + gap) * 2 + 5, 420, hText);
        p.add(chkHoatDong);

        int xCol2 = xStart + wLbl + wTxt + 50;
        JLabel lblNgayApDung = new JLabel("Ngày áp dụng:");
        lblNgayApDung.setFont(FONT_TEXT);
        lblNgayApDung.setBounds(xCol2, yStart, 120, 35);
        p.add(lblNgayApDung);

        txtNgayApDung = new JDateChooser();
        txtNgayApDung.setFont(FONT_TEXT);
        txtNgayApDung.setBounds(xCol2 + wLbl, yStart, wTxt, 35);
        txtNgayApDung.setDateFormatString("dd/MM/yyyy");
        txtNgayApDung.setDate(java.sql.Date.valueOf(LocalDate.now()));
        p.add(txtNgayApDung);

        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(FONT_TEXT);
        lblTrangThai.setBounds(xCol2, yStart + (hText + gap), 120, 35);
        p.add(lblTrangThai);

        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngưng hoạt động"});
        cboTrangThai.setFont(FONT_TEXT);
        cboTrangThai.setBounds(xCol2 + wLbl, yStart + (hText + gap), wTxt, 35);
        p.add(cboTrangThai);

        cboTrangThai.addActionListener(e -> chkHoatDong.setEnabled(cboTrangThai.getSelectedIndex() == 0));
        chkHoatDong.addActionListener(e -> { if (chkHoatDong.isSelected()) cboTrangThai.setSelectedIndex(0); });
    }

    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        btnThem = new PillButton("TẠO MỚI");
        btnThem.setFont(FONT_BOLD);
        btnThem.addActionListener(this);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = new PillButton("CẬP NHẬT");
        btnSua.setFont(FONT_BOLD);
        btnSua.addActionListener(this);
        btnSua.setEnabled(false);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnLamMoi = new PillButton("LÀM MỚI");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"STT", "Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
        modelBangGia = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblBangGia = setupTable(modelBangGia);
        tblBangGia.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) chonBangGia(); });
        p.add(new JScrollPane(tblBangGia), BorderLayout.CENTER);
    }

    private void taoBangChiTiet(JPanel p) {
        String[] cols = {"STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
        modelChiTiet = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblChiTiet = setupTable(modelChiTiet);
        tblChiTiet.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) chonChiTiet(); });
        p.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnBottom.setBackground(Color.WHITE);
        txtGiaTu = new JTextField(8);
        txtGiaDen = new JTextField(8);
        txtTiLe = new JTextField(6);
        chkKhoangCuoi = new JCheckBox("Trở lên");
        btnThemCT = new PillButton("Thêm CT");
        btnSuaCT = new PillButton("Sửa CT");
        btnXoaCT = new PillButton("Xóa CT");
        btnLamMoiCT = new PillButton("Làm mới CT");
        for (JButton b : new JButton[]{btnThemCT, btnSuaCT, btnXoaCT, btnLamMoiCT}) b.addActionListener(this);
        pnBottom.add(new JLabel("Từ")); pnBottom.add(txtGiaTu);
        pnBottom.add(new JLabel("Đến")); pnBottom.add(txtGiaDen);
        pnBottom.add(chkKhoangCuoi);
        pnBottom.add(new JLabel("Tỉ lệ")); pnBottom.add(txtTiLe);
        pnBottom.add(btnThemCT); pnBottom.add(btnSuaCT); pnBottom.add(btnXoaCT); pnBottom.add(btnLamMoiCT);
        p.add(pnBottom, BorderLayout.SOUTH);
    }

    private void taoBangMoPhong(JPanel p) {
        String[] cols = {"STT", "Mã SP", "Tên thuốc mẫu", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhong = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblMoPhong = setupTable(modelMoPhong);
        p.add(new JScrollPane(tblMoPhong), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == btnLamMoi) {
                lamMoiVaTaiBangGia();
            } else if (o == btnTimKiem) {
                loadBangGia(txtTimKiem.getText().trim());
            } else if (o == btnThem) {
                themBangGia();
            } else if (o == btnSua) {
                capNhatBangGia();
            } else if (o == btnThemCT) {
                themChiTietBangGia();
            } else if (o == btnSuaCT) {
                suaChiTietBangGia();
            } else if (o == btnXoaCT) {
                xoaChiTietBangGia();
            } else if (o == btnLamMoiCT) {
                resetChiTietForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBangGia(String keyword) throws Exception {
        modelBangGia.setRowCount(0);
        List<?> list = (keyword == null || keyword.isBlank()) ? svc.getAllBangGia() : svc.searchBangGia(keyword);
        int stt = 1;
        for (Object o : list) {
            if (!(o instanceof BangGiaDTO bg)) continue;
            modelBangGia.addRow(new Object[]{stt++, bg.getMaBangGia(), bg.getTenBangGia(), bg.getNgayApDung(), bg.getMaNhanVien(), bg.isHoatDong() ? "Hoạt động" : "Ngưng"});
        }
    }

    private void chonBangGia() {
        int row = tblBangGia.getSelectedRow();
        if (row < 0) return;
        maBangGiaDangChon = modelBangGia.getValueAt(row, 1).toString();
        txtMaBG.setText(maBangGiaDangChon);
        txtTenBG.setText(String.valueOf(modelBangGia.getValueAt(row, 2)));

        Object ngayObj = modelBangGia.getValueAt(row, 3);
        if (ngayObj instanceof LocalDate) {
            txtNgayApDung.setDate(java.sql.Date.valueOf((LocalDate) ngayObj));
        } else if (ngayObj != null && !ngayObj.toString().isEmpty()) {
            try {
                txtNgayApDung.setDate(java.sql.Date.valueOf(LocalDate.parse(ngayObj.toString())));
            } catch (Exception ignored) {}
        }

        String trangThai = String.valueOf(modelBangGia.getValueAt(row, 5));
        boolean hoatDong = "Hoạt động".equals(trangThai);
        cboTrangThai.setSelectedIndex(hoatDong ? 0 : 1);
        chkHoatDong.setSelected(hoatDong);

        btnSua.setEnabled(true);
        loadChiTiet(maBangGiaDangChon);
    }

    private void chonChiTiet() {
        int row = tblChiTiet.getSelectedRow();
        chiTietDangChon = row;
        if (row < 0 || row >= dsChiTietTam.size()) return;
        ChiTietBangGiaDTO ct = dsChiTietTam.get(row);
        txtGiaTu.setText(String.valueOf(ct.getGiaTu()));
        txtGiaDen.setText(ct.getGiaDen() == 0 ? "" : String.valueOf(ct.getGiaDen()));
        txtTiLe.setText(String.valueOf(ct.getTiLe()));
        chkKhoangCuoi.setSelected(ct.getGiaDen() == 0 || ct.getGiaDen() == ct.getGiaTu());
    }

    private void loadChiTiet(String maBangGia) {
        try {
            modelChiTiet.setRowCount(0);
            dsChiTietTam.clear();
            List<?> list = svc.getChiTietBangGia(maBangGia);
            int stt = 1;
            for (Object o : list) {
                if (!(o instanceof ChiTietBangGiaDTO ct)) continue;
                dsChiTietTam.add(ct);
                modelChiTiet.addRow(new Object[]{stt++, ct.getGiaTu(), ct.getGiaDen() == 0 ? "∞" : ct.getGiaDen(), ct.getTiLe(), ct.getGiaTu() * ct.getTiLe()});
            }
            modelMoPhong.setRowCount(0);
            stt = 1;
            List<SanPham> danhSach = svc.getAllSanPhamTyped();
            for (SanPham sp : danhSach) {
                double giaBan = tinhGiaBanTheoBangGia(sp.getGiaNhap());
                double tiLe = sp.getGiaNhap() > 0 ? ((giaBan / sp.getGiaNhap()) * 100.0) : 0;
                modelMoPhong.addRow(new Object[]{stt++, sp.getMaSanPham(), sp.getTenSanPham(), dfNumber.format(sp.getGiaNhap()), String.format("%.2f%%", tiLe), dfNumber.format(giaBan)});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được chi tiết bảng giá: " + ex.getMessage());
        }
    }

    private double tinhGiaBanTheoBangGia(double giaNhap) {
        if (giaNhap <= 0 || dsChiTietTam.isEmpty()) {
            return giaNhap;
        }
        for (ChiTietBangGiaDTO ct : dsChiTietTam) {
            double tu = ct.getGiaTu();
            double den = ct.getGiaDen();
            boolean hopLe = den <= 0 ? giaNhap >= tu : (giaNhap >= tu && giaNhap <= den);
            if (hopLe) {
                return giaNhap * ct.getTiLe();
            }
        }
        return giaNhap;
    }

    private void themBangGia() throws Exception {
        BangGiaDTO bg = new BangGiaDTO();
        bg.setMaBangGia(svc.taoMaBangGia());
        bg.setMaNhanVien(null);
        bg.setTenBangGia(txtTenBG.getText().trim());
        bg.setNgayApDung(getNgayApDung());
        bg.setHoatDong(cboTrangThai.getSelectedIndex() == 0);
        if (bg.isHoatDong()) svc.deactivateAllBangGiaExcept(bg.getMaBangGia());
        if (!svc.createBangGia(bg)) throw new IllegalStateException("Thêm bảng giá thất bại");
        xuLyLamMoi();
        loadBangGia("");
    }

    private void capNhatBangGia() throws Exception {
        if (maBangGiaDangChon == null) throw new IllegalStateException("Chọn bảng giá cần cập nhật");
        BangGiaDTO bg = new BangGiaDTO();
        bg.setMaBangGia(maBangGiaDangChon);
        
        int row = tblBangGia.getSelectedRow();
        if (row >= 0) {
            Object maNhanVien = modelBangGia.getValueAt(row, 4);
            bg.setMaNhanVien(maNhanVien != null ? maNhanVien.toString() : null);
        } else {
            bg.setMaNhanVien(null);
        }

        bg.setTenBangGia(txtTenBG.getText().trim());
        bg.setNgayApDung(getNgayApDung());
        bg.setHoatDong(cboTrangThai.getSelectedIndex() == 0);
        if (bg.isHoatDong()) svc.deactivateAllBangGiaExcept(bg.getMaBangGia());
        if (!svc.updateBangGia(bg)) throw new IllegalStateException("Cập nhật bảng giá thất bại");
        loadBangGia("");
    }

    private void themChiTietBangGia() throws Exception {
        if (maBangGiaDangChon == null) throw new IllegalStateException("Chọn bảng giá trước");
        ChiTietBangGiaDTO ct = parseChiTietForm();
        ct.setMaBangGia(maBangGiaDangChon);
        if (!svc.createChiTietBangGia(ct)) throw new IllegalStateException("Thêm chi tiết thất bại");
        loadChiTiet(maBangGiaDangChon);
        resetChiTietForm();
    }

    private void suaChiTietBangGia() throws Exception {
        if (maBangGiaDangChon == null) throw new IllegalStateException("Chọn bảng giá trước");
        if (chiTietDangChon < 0 || chiTietDangChon >= dsChiTietTam.size()) throw new IllegalStateException("Chọn 1 dòng chi tiết cần sửa");
        ChiTietBangGiaDTO ct = parseChiTietForm();
        ct.setMaBangGia(maBangGiaDangChon);
        dsChiTietTam.set(chiTietDangChon, ct);
        if (!svc.deleteAllChiTietBangGia(maBangGiaDangChon)) throw new IllegalStateException("Không xóa được chi tiết cũ");
        for (ChiTietBangGiaDTO item : dsChiTietTam) {
            if (!svc.createChiTietBangGia(item)) throw new IllegalStateException("Không lưu lại được chi tiết");
        }
        loadChiTiet(maBangGiaDangChon);
    }

    private void xoaChiTietBangGia() throws Exception {
        if (maBangGiaDangChon == null) throw new IllegalStateException("Chọn bảng giá trước");
        if (JOptionPane.showConfirmDialog(this, "Xóa toàn bộ chi tiết của bảng giá này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        if (!svc.deleteAllChiTietBangGia(maBangGiaDangChon)) throw new IllegalStateException("Xóa chi tiết thất bại");
        loadChiTiet(maBangGiaDangChon);
    }

    private void resetChiTietForm() {
        txtGiaTu.setText(""); txtGiaDen.setText(""); txtTiLe.setText(""); chkKhoangCuoi.setSelected(false);
    }

    private ChiTietBangGiaDTO parseChiTietForm() {
        double giaTu = parseDouble(txtGiaTu.getText());
        double giaDen = chkKhoangCuoi.isSelected() ? giaTu : parseDouble(txtGiaDen.getText());
        double tiLe = parseDouble(txtTiLe.getText());
        ChiTietBangGiaDTO ct = new ChiTietBangGiaDTO();
        ct.setGiaTu(giaTu);
        ct.setGiaDen(giaDen);
        ct.setTiLe(tiLe);
        return ct;
    }

    private double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Giá trị không được trống");
        return Double.parseDouble(s.trim().replace(",", ""));
    }

    private LocalDate getNgayApDung() {
        java.util.Date d = txtNgayApDung.getDate();
        if (d == null) throw new IllegalArgumentException("Chọn ngày áp dụng");
        return new java.sql.Date(d.getTime()).toLocalDate();
    }

    private void xuLyLamMoi() {
        txtMaBG.setText("");
        txtTenBG.setText("");
        txtTimKiem.setText("");
        txtNgayApDung.setDate(java.sql.Date.valueOf(LocalDate.now()));
        cboTrangThai.setSelectedIndex(0);
        chkHoatDong.setSelected(false);
        chkHoatDong.setEnabled(true);
        maBangGiaDangChon = null;
        chiTietDangChon = -1;
        btnSua.setEnabled(false);
        resetChiTietForm();
        modelBangGia.setRowCount(0);
        modelChiTiet.setRowCount(0);
        modelMoPhong.setRowCount(0);
    }

    private void lamMoiVaTaiBangGia() {
        xuLyLamMoi();
        try {
            loadBangGia("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được bảng giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_TEXT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title, TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
    }

    private void setupKeyboardShortcuts() {
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        am.put("refresh", new AbstractAction() { public void actionPerformed(ActionEvent e) { lamMoiVaTaiBangGia(); } });
    }

    private void addFocusOnShow() { }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản Lý Bảng Giá");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 850);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new BangGia_GUI());
        frame.setVisible(true);
    }
}
