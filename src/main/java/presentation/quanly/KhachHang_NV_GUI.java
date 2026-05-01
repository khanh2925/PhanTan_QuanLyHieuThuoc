package presentation.quanly;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;
import presentation.component.border.RoundedBorder;
import dao.iml.KhachHangDaoImpl;
import entity.KhachHang;
import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class KhachHang_NV_GUI extends JPanel implements ActionListener, DocumentListener, KeyListener {

    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;
    private JTextField txtMaKH, txtTenKH, txtSDT;
    private JDateChooser dateNgaySinh;
    private JComboBox<String> cboGioiTinh;
    private JComboBox<String> cboTrangThai;
    private PillButton btnThem, btnSua, btnLamMoi;
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<KhachHang> listKH = new ArrayList<>();
    private KhachHangDaoImpl kh_dao;
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public KhachHang_NV_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        kh_dao = new KhachHangDaoImpl();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
        loadDataLenBang();
        thietLapPhimTat();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtTimKiem.requestFocusInWindow();
                });
            }
        });
    }

    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên hoặc số điện thoại... (F1/Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Gõ để lọc dữ liệu theo thời gian thực</html>");
        pnHeader.add(txtTimKiem);
        txtTimKiem.getDocument().addDocumentListener(this);
        btnTimKiem = new PillButton("<html>" + "<center>" + "TÌM KIẾM<br>" + "<span style='font-size:10px; color:#888888;'>(Enter)</span>" + "</center>" + "</html>");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.addActionListener(this);
        pnHeader.add(btnTimKiem);
    }

    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin khách hàng"));
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
        int xStart = 50, yStart = 40;
        int hText = 35, wLbl = 100, wTxt = 300, gap = 25;
        int xCol2 = xStart + wLbl + wTxt + 50;
        
        p.add(createLabel("Mã KH:", xStart, yStart));
        txtMaKH = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaKH.setEditable(false);
        PlaceholderSupport.addPlaceholder(txtMaKH, kh_dao.phatSinhMaKhachHangTiepTheo());
        p.add(txtMaKH);
        
        p.add(createLabel("Tên KH:", xStart, yStart + gap + hText));
        txtTenKH = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        p.add(txtTenKH);
        PlaceholderSupport.addPlaceholder(txtTenKH, "Nhập tên khách hàng");
        txtTenKH.addKeyListener(this);
        
        p.add(createLabel("Giới tính:", xStart, yStart + (gap + hText) * 2));
        cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        cboGioiTinh.setBounds(xStart + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);
        
        p.add(createLabel("Số ĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);
        PlaceholderSupport.addPlaceholder(txtSDT, "Nhập số điện thoại");
        
        p.add(createLabel("Ngày sinh:", xCol2, yStart + gap + hText));
        dateNgaySinh = new JDateChooser();
        dateNgaySinh.setBounds(xCol2 + wLbl, yStart + gap + hText, wTxt, 35);
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.setFont(FONT_TEXT);
        p.add(dateNgaySinh);
        
        p.add(createLabel("Trạng thái:", xCol2, yStart + (gap + hText) * 2));
        cboTrangThai = new JComboBox<>(new String[] { "Hoạt động", "Ngưng" });
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

        btnThem = new PillButton("<html>" + "<center>" + "THÊM<br>" + "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" + "</center>" + "</html>");
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm khách hàng mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy = 0;
        p.add(btnThem, gbc);

        btnSua = new PillButton("<html>" + "<center>" + "CẬP NHẬT<br>" + "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" + "</center>" + "</html>");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật thông tin khách hàng đang chọn</html>");
        btnSua.addActionListener(this);
        btnSua.setEnabled(false);
        gbc.gridy = 1;
        p.add(btnSua, gbc);

        btnLamMoi = new PillButton("<html>" + "<center>" + "LÀM MỚI<br>" + "<span style='font-size:10px; color:#888888;'>(F5)</span>" + "</center>" + "</html>");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2;
        p.add(btnLamMoi, gbc);
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

    private void taoBangDanhSach(JPanel p) {
        String[] cols = { "STT", "Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh", "Trạng thái" };
        modelKhachHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblKhachHang = setupTable(modelKhachHang);
        sorter = new TableRowSorter<>(modelKhachHang);
        tblKhachHang.setRowSorter(sorter);
        tblKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblKhachHang.getSelectedRow());
            }
        });
        JScrollPane scr = new JScrollPane(tblKhachHang);
        scr.setBorder(createTitledBorder("Danh sách khách hàng"));
        p.add(scr, BorderLayout.CENTER);
    }

    private void doToForm(int row) {
        if (row < 0) return;
        txtMaKH.setText(tblKhachHang.getValueAt(row, 1).toString());
        txtTenKH.setText(tblKhachHang.getValueAt(row, 2).toString());
        String gt = tblKhachHang.getValueAt(row, 3).toString();
        cboGioiTinh.setSelectedItem(gt);
        txtSDT.setText(tblKhachHang.getValueAt(row, 4).toString());
        String ngaySinhStr = tblKhachHang.getValueAt(row, 5).toString();
        try {
            if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                dateNgaySinh.setDate(java.sql.Date.valueOf(LocalDate.parse(ngaySinhStr, dtf)));
            }
        } catch (Exception e) {
            dateNgaySinh.setDate(null);
        }
        String trangThai = tblKhachHang.getValueAt(row, 6).toString();
        cboTrangThai.setSelectedItem(trangThai.equals("Hoạt động") ? "Hoạt động" : "Ngưng");
        btnSua.setEnabled(true);
        btnThem.setEnabled(false);
    }

    private void loadDataLenBang() {
        listKH = kh_dao.layTatCaKhachHang();
        modelKhachHang.setRowCount(0);
        int stt = 1;
        for (KhachHang kh : listKH) {
            modelKhachHang.addRow(new Object[] {
                    stt++,
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.isGioiTinh() ? "Nam" : "Nữ",
                    kh.getSoDienThoai(),
                    kh.getNgaySinh() != null ? kh.getNgaySinh().format(dtf) : "",
                    kh.getTrangThaiText()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            // Implementation
        } else if (o.equals(btnSua)) {
            // Implementation
        } else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
        }
    }

    private void lamMoiForm() {
        txtMaKH.setText("");
        PlaceholderSupport.addPlaceholder(txtMaKH, kh_dao.phatSinhMaKhachHangTiepTheo());
        txtTenKH.setText("");
        PlaceholderSupport.addPlaceholder(txtTenKH, "Nhập tên khách hàng");
        txtSDT.setText("");
        PlaceholderSupport.addPlaceholder(txtSDT, "Nhập số điện thoại");
        dateNgaySinh.setDate(null);
        cboGioiTinh.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        tblKhachHang.clearSelection();
        btnSua.setEnabled(false);
        btnThem.setEnabled(true);
    }

    private void refreshFilters() {
        // Implementation
    }

    @Override
    public void insertUpdate(DocumentEvent e) { refreshFilters(); }
    @Override
    public void removeUpdate(DocumentEvent e) { refreshFilters(); }
    @Override
    public void changedUpdate(DocumentEvent e) { refreshFilters(); }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    private void thietLapPhimTat() {
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Khách Hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhachHang_NV_GUI());
            frame.setVisible(true);
        });
    }
}
