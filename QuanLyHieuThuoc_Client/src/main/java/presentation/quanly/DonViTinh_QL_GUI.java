package presentation.quanly;

import dto.DonViTinh;
import network.ClientService;
import presentation.component.border.RoundedBorder;
import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

@SuppressWarnings("serial")
public class DonViTinh_QL_GUI extends JPanel implements ActionListener {

    // Components UI
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Input fields
    private JTextField txtMaDVT, txtTenDVT;

    // Search & Table
    private JTextField txtTimKiem;
    private JTable tblDonViTinh;
    private DefaultTableModel modelDonViTinh;

    // Buttons
    private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem;

    // service & data
    private ClientService svc;
    private List<DonViTinh> dsDonViTinh;

    // Style
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public DonViTinh_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        svc = new ClientService();
        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. LOAD DATA
        loadDataLenBang();

        // 4. THIẾT LẬP PHÍM TẮT
        setupKeyboardShortcuts();

        // 5. AUTO FOCUS
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtTimKiem.requestFocusInWindow();
                });
            }
        });
    }

    // ======================================================================
    // PHẦN HEADER
    // ======================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // Ô tìm kiếm
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(txtTimKiem);

        // Nút Tìm kiếm
        btnTimKiem = new PillButton(
                "<html>" +
                        "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                        "</html>");
        btnTimKiem.setBounds(560, 22, 180, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.setToolTipText(
                "<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo tên đơn vị tính</html>");
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ======================================================================
    // PHẦN CENTER
    // ======================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PHẦN TRÊN: FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin đơn vị tính"));

        // Form
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // Nút
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- PHẦN DƯỚI: BẢNG ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.0);

        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 100;
        int yStart = 50;
        int hText = 40;
        int wTxt = 400;
        int gap = 30;

        // Hàng 1: Mã Đơn Vị
        p.add(createLabel("Mã ĐVT:", xStart, yStart));
        txtMaDVT = createTextField(xStart + 100, yStart, wTxt);
        txtMaDVT.setEditable(false); // Mã tự sinh từ DAO
        p.add(txtMaDVT);
        try {
            PlaceholderSupport.addPlaceholder(txtMaDVT, svc.taoMaDonViTinh());
        } catch (Exception ex) {
            PlaceholderSupport.addPlaceholder(txtMaDVT, "DVT-001");
        }

        // Hàng 2: Tên Đơn Vị Tính
        yStart += hText + gap;
        p.add(createLabel("Tên ĐVT:", xStart, yStart));
        txtTenDVT = createTextField(xStart + 100, yStart, wTxt);
        p.add(txtTenDVT);
        PlaceholderSupport.addPlaceholder(txtTenDVT, "Nhập tên đơn vị tính");
    }

    // Panel nút bên phải
    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int btnH = 45;
        int btnW = 140;

        btnThem = new PillButton(
                "<html>" +
                        "<center>" +
                        "THÊM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                        "</center>" +
                        "</html>");
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm đơn vị tính mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy = 0;
        p.add(btnThem, gbc);

        btnSua = new PillButton(
                "<html>" +
                        "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                        "</center>" +
                        "</html>");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật thông tin đơn vị tính đang chọn</html>");
        btnSua.addActionListener(this);
        btnSua.setEnabled(false);
        gbc.gridy = 1;
        p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                        "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                        "</center>" +
                        "</html>");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới form nhập liệu</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2;
        p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = { "Mã Đơn Vị Tính", "Tên Đơn Vị Tính" };
        modelDonViTinh = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblDonViTinh = setupTable(modelDonViTinh);

        tblDonViTinh.getColumnModel().getColumn(0).setPreferredWidth(200);

        tblDonViTinh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblDonViTinh.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblDonViTinh);
        scr.setBorder(createTitledBorder("Danh sách đơn vị tính"));
        p.add(scr, BorderLayout.CENTER);
    }

    private void doToForm(int row) {
        if (row < 0)
            return;
        txtMaDVT.setText(tblDonViTinh.getValueAt(row, 0).toString());
        txtMaDVT.setForeground(Color.BLACK);

        txtTenDVT.setText(tblDonViTinh.getValueAt(row, 1).toString());
        txtTenDVT.setForeground(Color.BLACK);

        txtMaDVT.setEditable(false);

        btnThem.setEnabled(false);
        btnSua.setEnabled(true);
    }

    private void loadDataLenBang() {
        modelDonViTinh.setRowCount(0);
        try {
            dsDonViTinh = new java.util.ArrayList<>();
            List<?> rs = svc.getAllDonViTinh();
            for (Object o : rs) if (o instanceof DonViTinh dvt) dsDonViTinh.add(dvt);
        } catch (Exception ex) {
            dsDonViTinh = java.util.Collections.emptyList();
        }
        for (DonViTinh dvt : dsDonViTinh) {
            modelDonViTinh.addRow(new Object[] {
                    dvt.getMaDonViTinh(),
                    dvt.getTenDonViTinh()
            });
        }
    }

    private DonViTinh getFromForm(String maDVT) {
        String ten = txtTenDVT.getText() != null ? txtTenDVT.getText().trim() : "";
        try {
            return new DonViTinh(maDVT, ten);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void lamMoiForm() {
        txtMaDVT.setText("");
        try {
            PlaceholderSupport.addPlaceholder(txtMaDVT, svc.taoMaDonViTinh());
        } catch (Exception ex) {
            PlaceholderSupport.addPlaceholder(txtMaDVT, "DVT-001");
        }

        txtTenDVT.setText("");
        PlaceholderSupport.addPlaceholder(txtTenDVT, "Nhập tên đơn vị tính");

        if (txtTimKiem != null) {
            txtTimKiem.setText("");
            PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính... (F1 / Ctrl+F)");
            txtTimKiem.requestFocus();
        }
        tblDonViTinh.clearSelection();

        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().trim();
        if (kw.isEmpty() || kw.equalsIgnoreCase("Tìm kiếm theo tên đơn vị tính...")) {
            loadDataLenBang();
            return;
        }
        kw = kw.toLowerCase();

        modelDonViTinh.setRowCount(0);
        if (dsDonViTinh != null) {
            for (DonViTinh dvt : dsDonViTinh) {
                if (dvt.getTenDonViTinh().toLowerCase().contains(kw)) {
                    modelDonViTinh.addRow(new Object[] {
                            dvt.getMaDonViTinh(),
                            dvt.getTenDonViTinh()
                    });
                }
            }
        }
    }

    private boolean validData() {
        String ten = txtTenDVT.getText() != null ? txtTenDVT.getText().trim() : "";

        if (ten.isEmpty() || ten.equals("Nhập tên đơn vị tính")) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được rỗng!");
            txtTenDVT.requestFocus();
            return false;
        }
        if (ten.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được vượt quá 50 ký tự!");
            txtTenDVT.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            themDonViTinh();
            return;
        } else if (o.equals(btnSua)) {
            suaDonViTinh();
            return;
        } else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
            return;
        }
    }

    private void themDonViTinh() {
        if (!validData()) {
            return;
        }

        String maMoi;
        try {
            maMoi = svc.taoMaDonViTinh();
        } catch (Exception ex) {
            maMoi = "DVT-001";
        }
        if (maMoi == null || maMoi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không sinh được mã đơn vị tính mới", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DonViTinh dvt = getFromForm(maMoi);
        if (dvt == null) {
            return;
        }

        try {
            if (svc.createDonViTinh(dvt)) {
                JOptionPane.showMessageDialog(this, "Thêm đơn vị tính thành công!");
                txtMaDVT.setText(maMoi);
                modelDonViTinh.addRow(new Object[] {
                        dvt.getMaDonViTinh(),
                        dvt.getTenDonViTinh()
                });
                lamMoiForm();
                txtTenDVT.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc lỗi DB)!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Thêm thất bại: " + ex.getMessage());
        }
    }

    private void suaDonViTinh() {
        int row = tblDonViTinh.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!");
            return;
        }

        String ma = txtMaDVT.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã đơn vị tính không hợp lệ!");
            return;
        }

        if (!validData()) {
            return;
        }

        DonViTinh dvt = getFromForm(ma);
        if (dvt == null) {
            return;
        }

        try {
            if (svc.updateDonViTinh(dvt)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                modelDonViTinh.setValueAt(txtTenDVT.getText(), row, 1);
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + ex.getMessage());
        }
    }

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
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
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

        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                loadDataLenBang();
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

        inputMap.put(KeyStroke.getKeyStroke("control N"), "themDVT");
        actionMap.put("themDVT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themDonViTinh();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhatDVT");
        actionMap.put("capNhatDVT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaDonViTinh();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Vị Tính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DonViTinh_QL_GUI());
            frame.setVisible(true);
        });
    }
}
