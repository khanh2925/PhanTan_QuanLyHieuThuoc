package presentation.quanly;

import dto.NhaCungCap;
import network.ClientService;
import presentation.component.border.RoundedBorder;
import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

@SuppressWarnings("serial")
public class NhaCungCap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    private JTextField txtMaNCC, txtTenNCC, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<String> cboTrangThai;

    private JTextField txtTimKiem;
    private JTable tblNhaCungCap;
    private DefaultTableModel modelNhaCungCap;

    private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem;

    private ClientService svc;
    private List<NhaCungCap> danhSachNhaCungCap;

    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    
    private static final String PH_TEN_NCC = "Nhập tên nhà cung cấp";
    private static final String PH_SDT = "Nhập số điện thoại";
    private static final String PH_EMAIL = "Nhập email (không bắt buộc)";
    private static final String PH_DIA_CHI = "Nhập địa chỉ";
    private static final String PH_TIM_KIEM = "Tìm NCC theo mã hoặc SĐT... (F1 / Ctrl+F)";

    public NhaCungCap_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        svc = new ClientService();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            taiDuLieuNhaCungCap();
        });
        
        setupKeyboardShortcuts();
        addFocusOnShow();
        lamMoiForm();
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
        actionMap.put("timKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("control N"), "themMoi");
        actionMap.put("themMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validData()) {
                    NhaCungCap ncc = getFromForm();
                    String maMoi = svc.taoMaNhaCungCap();
                    ncc.setMaNhaCungCap(maMoi);
                    
                    if (svc.createNhaCungCap(ncc)) {
                        JOptionPane.showMessageDialog(NhaCungCap_GUI.this, "Thêm nhà cung cấp thành công!");
                        danhSachNhaCungCap.add(ncc);
                        hienThiDanhSach(danhSachNhaCungCap);
                        lamMoiForm();
                    } else {
                        JOptionPane.showMessageDialog(NhaCungCap_GUI.this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhat");
        actionMap.put("capNhat", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tblNhaCungCap.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(NhaCungCap_GUI.this, "Vui lòng chọn nhà cung cấp cần cập nhật!");
                    return;
                }
                if (validData()) {
                    NhaCungCap ncc = getFromForm();
                    String maNCC = ncc.getMaNhaCungCap();
                    if (svc.updateNhaCungCap(ncc)) {
                        JOptionPane.showMessageDialog(NhaCungCap_GUI.this, "Cập nhật thông tin thành công!");
                        for (int i = 0; i < danhSachNhaCungCap.size(); i++) {
                            if (danhSachNhaCungCap.get(i).getMaNhaCungCap().equals(maNCC)) {
                                danhSachNhaCungCap.set(i, ncc);
                                break;
                            }
                        }
                        hienThiDanhSach(danhSachNhaCungCap);
                        chonDongTheoMa(maNCC);
                    } else {
                        JOptionPane.showMessageDialog(NhaCungCap_GUI.this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                taiDuLieuNhaCungCap();
            }
        });
        
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
    }
    
    private void addFocusOnShow() {
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtTimKiem.requestFocusInWindow();
                    txtTimKiem.selectAll();
                });
            }
        });
    }
    
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, PH_TIM_KIEM);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton(
                "<html>" +
                        "<center>" +
                            "TÌM KIẾM<br>" +
                            "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                    "</html>"
                );

        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã hoặc SĐT</html>");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin nhà cung cấp"));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(300); 
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40, hText = 35, wLbl = 100, wTxt = 300, gap = 25;
        
        p.add(createLabel("Mã NCC:", xStart, yStart));
        txtMaNCC = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaNCC.setEditable(false);
        txtMaNCC.setBackground(new Color(245, 245, 245));
        p.add(txtMaNCC);

        p.add(createLabel("Tên NCC:", xStart, yStart + gap + hText));
        txtTenNCC = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        PlaceholderSupport.addPlaceholder(txtTenNCC, PH_TEN_NCC);
        p.add(txtTenNCC);
        
        p.add(createLabel("SĐT:", xStart, yStart + (gap + hText) * 2));
        txtSDT = createTextField(xStart + wLbl, yStart + (gap + hText) * 2, wTxt);
        PlaceholderSupport.addPlaceholder(txtSDT, PH_SDT);
        p.add(txtSDT);

        int xCol2 = xStart + wLbl + wTxt + 50;
        
        p.add(createLabel("Email:", xCol2, yStart));
        txtEmail = createTextField(xCol2 + wLbl, yStart, wTxt);
        PlaceholderSupport.addPlaceholder(txtEmail, PH_EMAIL);
        p.add(txtEmail);
        
        p.add(createLabel("Địa chỉ:", xCol2, yStart + gap + hText));
        txtDiaChi = createTextField(xCol2 + wLbl, yStart + gap + hText, wTxt);
        PlaceholderSupport.addPlaceholder(txtDiaChi, PH_DIA_CHI);
        p.add(txtDiaChi);
        
        p.add(createLabel("Trạng thái:", xCol2, yStart + (gap + hText) * 2));
        cboTrangThai = new JComboBox<>(new String[]{"Đang hợp tác", "Ngừng hợp tác"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);
    }

    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0)); 
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); 
        
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        int btnH = 45;
        int btnW = 140;

        btnThem = new PillButton(
                "<html>" +
                    "<center>" +
                        "THÊM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                    "</center>" +
                "</html>"
            );
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm nhà cung cấp mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = new PillButton(
                "<html>" +
                    "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                    "</center>" +
                "</html>"
            );
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật nhà cung cấp đã chọn</html>");
        btnSua.addActionListener(this);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>"
            );
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã NCC", "Tên nhà cung cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái"};
        modelNhaCungCap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhaCungCap = setupTable(modelNhaCungCap);
        
        tblNhaCungCap.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblNhaCungCap.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblNhaCungCap.getColumnModel().getColumn(4).setPreferredWidth(300);

        tblNhaCungCap.addMouseListener(this);

        JScrollPane scr = new JScrollPane(tblNhaCungCap);
        scr.setBorder(createTitledBorder("Danh sách nhà cung cấp"));
        p.add(scr, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            if (validData()) {
                NhaCungCap ncc = getFromForm();
                String maMoi;
                try {
                    maMoi = svc.taoMaNhaCungCap();
                } catch (Exception ex) {
                    maMoi = "NCC-001";
                }
                ncc.setMaNhaCungCap(maMoi);
                
                if (svc.createNhaCungCap(ncc)) {
                    JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công: " + maMoi);
                    danhSachNhaCungCap.add(ncc);
                    hienThiDanhSach(danhSachNhaCungCap);
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        else if (o.equals(btnSua)) {
            int row = tblNhaCungCap.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần cập nhật!");
                return;
            }
            if (validData()) {
                NhaCungCap ncc = getFromForm();
                String maNCC = ncc.getMaNhaCungCap();
                if (svc.updateNhaCungCap(ncc)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                    for (int i = 0; i < danhSachNhaCungCap.size(); i++) {
                        if (danhSachNhaCungCap.get(i).getMaNhaCungCap().equals(maNCC)) {
                            danhSachNhaCungCap.set(i, ncc);
                            break;
                        }
                    }
                    hienThiDanhSach(danhSachNhaCungCap);
                    chonDongTheoMa(maNCC);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            taiDuLieuNhaCungCap();
        }
    }

    private void taiDuLieuNhaCungCap() {
        try {
            danhSachNhaCungCap = new java.util.ArrayList<>();
            List<?> rs = svc.getAllNhaCungCap();
            for (Object o : rs) if (o instanceof NhaCungCap ncc) danhSachNhaCungCap.add(ncc);
        } catch (Exception ex) {
            danhSachNhaCungCap = java.util.Collections.emptyList();
        }
        hienThiDanhSach(danhSachNhaCungCap);
    }
    
    private void hienThiDanhSach(List<NhaCungCap> ds) {
        modelNhaCungCap.setRowCount(0);
        for (NhaCungCap ncc : ds) {
            themDongVaoBang(ncc);
        }
    }

    private void themDongVaoBang(NhaCungCap ncc) {
        modelNhaCungCap.addRow(new Object[]{
            ncc.getMaNhaCungCap(),
            ncc.getTenNhaCungCap(),
            ncc.getSoDienThoai(),
            ncc.getEmail(),
            ncc.getDiaChi(),
            ncc.isHoatDong() ? "Đang hợp tác" : "Ngừng hợp tác"
        });
    }

    private NhaCungCap getFromForm() {
        String ma = txtMaNCC.getText();
        String ten = getTextIgnorePlaceholder(txtTenNCC, PH_TEN_NCC);
        String sdt = getTextIgnorePlaceholder(txtSDT, PH_SDT);
        String email = getTextIgnorePlaceholder(txtEmail, PH_EMAIL);
        String dc = getTextIgnorePlaceholder(txtDiaChi, PH_DIA_CHI);
        boolean hoatDong = cboTrangThai.getSelectedItem().equals("Đang hợp tác");
        
        NhaCungCap ncc = new NhaCungCap();
        if(!ma.isEmpty()) ncc.setMaNhaCungCap(ma); 
        ncc.setTenNhaCungCap(ten);
        ncc.setSoDienThoai(sdt);
        ncc.setEmail(email);
        ncc.setDiaChi(dc);
        ncc.setHoatDong(hoatDong);
        return ncc;
    }

    private void lamMoiForm() {
        try {
            txtMaNCC.setText(svc.taoMaNhaCungCap());
        } catch (Exception ex) {
            txtMaNCC.setText("NCC-001");
        }
        txtTenNCC.setText("");
        PlaceholderSupport.addPlaceholder(txtTenNCC, PH_TEN_NCC);
        txtSDT.setText("");
        PlaceholderSupport.addPlaceholder(txtSDT, PH_SDT);
        txtEmail.setText("");
        PlaceholderSupport.addPlaceholder(txtEmail, PH_EMAIL);
        txtDiaChi.setText("");
        PlaceholderSupport.addPlaceholder(txtDiaChi, PH_DIA_CHI);
        cboTrangThai.setSelectedIndex(0);
        txtTenNCC.requestFocus();
        tblNhaCungCap.clearSelection();
        
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty() || keyword.equals(PH_TIM_KIEM.toLowerCase())) { 
            hienThiDanhSach(danhSachNhaCungCap);
            return;
        }
        
        List<NhaCungCap> ketQua = new java.util.ArrayList<>();
        for (NhaCungCap ncc : danhSachNhaCungCap) {
            if (ncc.getMaNhaCungCap().toLowerCase().contains(keyword) ||
                ncc.getTenNhaCungCap().toLowerCase().contains(keyword) ||
                ncc.getSoDienThoai().contains(keyword)) {
                ketQua.add(ncc);
            }
        }
        
        if (!ketQua.isEmpty()) {
            hienThiDanhSach(ketQua);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhà cung cấp với thông tin: " + keyword);
            hienThiDanhSach(danhSachNhaCungCap);
        }
    }

    private boolean validData() {
        String ten = getTextIgnorePlaceholder(txtTenNCC, PH_TEN_NCC);
        String sdt = getTextIgnorePlaceholder(txtSDT, PH_SDT);
        String email = getTextIgnorePlaceholder(txtEmail, PH_EMAIL);
        String diaChi = getTextIgnorePlaceholder(txtDiaChi, PH_DIA_CHI);

        if (ten.isEmpty()) {
            showError("Tên nhà cung cấp không được rỗng", txtTenNCC);
            return false;
        }
        if (!sdt.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số", txtSDT);
            return false;
        }
        if (!email.isEmpty() && !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            showError("Email không đúng định dạng", txtEmail);
            return false;
        }
        if (diaChi.isEmpty()) {
            showError("Địa chỉ không được rỗng", txtDiaChi);
            return false;
        }
        return true;
    }
    
    private void showError(String mess, JTextField txt) {
        JOptionPane.showMessageDialog(this, mess, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        txt.requestFocus();
        txt.selectAll();
    }
    
    private String getTextIgnorePlaceholder(JTextField txt, String placeholder) {
        String text = txt.getText().trim();
        return text.equals(placeholder) ? "" : text;
    }

    private void chonDongTheoMa(String maNCC) {
        for (int i = 0; i < modelNhaCungCap.getRowCount(); i++) {
            if (modelNhaCungCap.getValueAt(i, 0).toString().equals(maNCC)) {
                tblNhaCungCap.setRowSelectionInterval(i, i);
                tblNhaCungCap.scrollRectToVisible(tblNhaCungCap.getCellRect(i, 0, true));
                txtMaNCC.setText(modelNhaCungCap.getValueAt(i, 0).toString());
                txtTenNCC.setText(modelNhaCungCap.getValueAt(i, 1).toString());
                txtSDT.setText(modelNhaCungCap.getValueAt(i, 2).toString());
                txtEmail.setText(modelNhaCungCap.getValueAt(i, 3) != null ? modelNhaCungCap.getValueAt(i, 3).toString() : "");
                txtDiaChi.setText(modelNhaCungCap.getValueAt(i, 4).toString());
                String trangThai = modelNhaCungCap.getValueAt(i, 5).toString();
                cboTrangThai.setSelectedItem(trangThai);
                break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblNhaCungCap.getSelectedRow();
        if (row >= 0) {
            hienThiThongTinNhaCungCap(row);
        }
    }
    
    private void hienThiThongTinNhaCungCap(int row) {
        if (row < 0) return;
        
        String ma = tblNhaCungCap.getValueAt(row, 0).toString();
        
        NhaCungCap ncc = null;
        for (NhaCungCap n : danhSachNhaCungCap) {
            if (n.getMaNhaCungCap().equals(ma)) {
                ncc = n;
                break;
            }
        }
        
        if (ncc != null) {
            txtMaNCC.setText(ncc.getMaNhaCungCap());
            txtTenNCC.setText(ncc.getTenNhaCungCap());
            txtSDT.setText(ncc.getSoDienThoai());
            txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
            txtDiaChi.setText(ncc.getDiaChi());
            cboTrangThai.setSelectedItem(ncc.isHoatDong() ? "Đang hợp tác" : "Ngừng hợp tác");
            
            btnThem.setEnabled(false);
            btnSua.setEnabled(true);
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 100, 35);
        return lbl;
    }

    private JTextField createTextField(int x, int y, int w) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBounds(x, y, w, 35);
        return txt;
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
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i!=1 && i!=4) 
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Nhà Cung Cấp");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}
