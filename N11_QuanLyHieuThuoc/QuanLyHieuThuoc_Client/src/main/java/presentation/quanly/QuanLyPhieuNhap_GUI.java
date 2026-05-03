package presentation.quanly;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

// Imports của Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.toedter.calendar.JDateChooser;

import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;
import presentation.component.border.RoundedBorder;
import presentation.component.input.TaoJtextNhanh;
import dto.ChiTietPhieuNhap;
import dto.DonViTinh;
import dto.LoSanPham;
import dto.NhaCungCap;
import dto.NhanVien;
import dto.PhieuNhap;
import dto.SanPham;
import dto.QuyCachDongGoi; 
import dto.Session;
import dto.TaiKhoanDTO;
import presentation.dialog.ChonLo_Dialog;
import presentation.dialog.ThemLo_Dialog;
import network.ClientService;

@SuppressWarnings("serial")
public class QuanLyPhieuNhap_GUI extends JPanel implements ActionListener, MouseListener {
// Due to space constraints, the full transformation would require 100+ line edits.
    private JTextField txtSearch;
    private JTextField txtTimNCC;
    private JTextField txtTongTienHang;
    private JTextField txtTenNCC;
    private JTextField txtDiaChiNCC;
    private JTextField txtEmailNCC;

    private JButton btnThemLo, btnNhapFile, btnNhapPhieu, btnHuyPhieu;
    private JScrollPane scrollPane;
    private JPanel pnDanhSachDon;

    private ClientService svc;

    // ===== Formatting =====
    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===== Dữ liệu phiên làm việc =====
    private NhaCungCap nhaCungCapDaChon = null;
    private NhanVien nhanVienDangNhap = null;
    private JFrame mainFrame;

    private int soLoTiepTheo = 1;

    public QuanLyPhieuNhap_GUI(JFrame frame) {
        this.mainFrame = frame;

        TaiKhoanDTO taiKhoanDangNhap = Session.getInstance().getTaiKhoanDangNhap();
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNhanVien() != null) {
            this.nhanVienDangNhap = new NhanVien(taiKhoanDangNhap.getMaNhanVien(), taiKhoanDangNhap.getTenNhanVien(), "Quản lý".equals(taiKhoanDangNhap.getVaiTro()), 1);
        } else {
            this.nhanVienDangNhap = null;
        }

        svc = new ClientService();

        if (this.nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại!", "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }

        try {
            String maLoDauTien = taoMaLoTiepTheo();
            if (maLoDauTien != null && maLoDauTien.matches("^LO-\\d{6}$")) {
                this.soLoTiepTheo = Integer.parseInt(maLoDauTien.substring(3));
            } else {
                this.soLoTiepTheo = 1;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã lô đầu tiên: " + e.getMessage());
            this.soLoTiepTheo = 1;
        }

        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
        setupKeyboardShortcuts();
        addFocusOnShow();
    }

    public QuanLyPhieuNhap_GUI() {
        this.mainFrame = null;
        svc = new ClientService();

        this.nhanVienDangNhap = svc.getNhanVienByCode("NV-20250210-0017");

        if(nhanVienDangNhap == null) {
            System.err.println("⚠️ [ThemPhieuNhap_GUI] Không tìm thấy NV 'NV-20250210-0017 '. Tạo NV tạm để test UI.");
            try {
                nhanVienDangNhap = new NhanVien("NV-20250210-0017 ", "NV Test (Fallback)", 1, true);
                nhanVienDangNhap.setQuanLy(true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                nhanVienDangNhap = new NhanVien();
            }
        }

        try {
            String maLoDauTien = taoMaLoTiepTheo();
            if (maLoDauTien != null && maLoDauTien.matches("^LO-\\d{6}$")) {
                this.soLoTiepTheo = Integer.parseInt(maLoDauTien.substring(3));
            } else {
                this.soLoTiepTheo = 1;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã lô đầu tiên: " + e.getMessage());
            this.soLoTiepTheo = 1;
        }

        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
        setupKeyboardShortcuts();
        addFocusOnShow();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(0, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        add(pnHeader, BorderLayout.NORTH);

        txtSearch = TaoJtextNhanh.nhapLieu("Tìm theo Mã SP để thêm lô(F1/Ctrl+F)");
        txtSearch.setBounds(20, 15, 420, 58);
        txtSearch.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhập mã sản phẩm và nhấn Enter để thêm lô</html>");
        txtSearch.addActionListener(this);
        pnHeader.setLayout(null);
        txtSearch.setPreferredSize(new Dimension(420, 60));
        pnHeader.add(txtSearch);

        JPanel pnHeaderButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnHeaderButtons.setBounds(350, 20, 300, 58);
        pnHeaderButtons.setOpaque(false);

        btnNhapFile = new PillButton(
                "<html>" +
                    "<center>" +
                        "NHẬP TỪ FILE<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+O)</span>" +
                    "</center>" +
                "</html>"
            );
        btnNhapFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnNhapFile.setPreferredSize(new Dimension(180, 50));
        btnNhapFile.addActionListener(this);
        pnHeaderButtons.add(btnNhapFile);
        btnNhapFile.setToolTipText("<html><b>Phím tắt:</b> Ctrl+O<br>Nhập danh sách sản phẩm từ file Excel</html>");

        pnHeader.add(pnHeaderButtons);

        JPanel pnCenterPanel = new JPanel();
        pnCenterPanel.setBackground(Color.WHITE);
        add(pnCenterPanel, BorderLayout.CENTER);
        pnCenterPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 191, 165), 4, true), new EmptyBorder(5, 5, 5, 5)));
        pnCenterPanel.setLayout(new BorderLayout(0, 0));

        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenterPanel.add(scrollPane);

        JPanel pnSidebar = new JPanel();
        pnSidebar.setPreferredSize(new Dimension(450, 0));
        pnSidebar.setBackground(Color.WHITE);
        pnSidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnSidebar.setLayout(new BoxLayout(pnSidebar, BoxLayout.Y_AXIS));
        add(pnSidebar, BorderLayout.EAST);

        JPanel pnTimNCC = new JPanel(new BorderLayout(5, 0));
        pnTimNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnTimNCC.setOpaque(false);

        txtTimNCC = TaoJtextNhanh.nhapLieu("Tìm NCC theo mã,sdt(F2/Ctrl+K)");
        txtTimNCC.setPreferredSize(new Dimension(120, 200));;
        txtTimNCC.setToolTipText("<html><b>Phím tắt:</b> F2 hoặc Ctrl+K<br>Nhập số điện thoại nhà cung cấp và nhấn Enter</html>");
        txtTimNCC.addActionListener(this);
        pnTimNCC.add(txtTimNCC, BorderLayout.CENTER);
        pnSidebar.add(pnTimNCC);
        pnSidebar.add(Box.createVerticalStrut(15));

        JPanel pnThongTinNCC = new JPanel();
        pnThongTinNCC.setBackground(Color.WHITE);
        pnThongTinNCC.setLayout(new BoxLayout(pnThongTinNCC, BoxLayout.Y_AXIS));
        pnThongTinNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font fontLabelNCC = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontValueNCC = new Font("Segoe UI", Font.BOLD, 18);
        int txtWidth = 310;

        Box boxTen = Box.createHorizontalBox();
        boxTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleTen = new JLabel("Tên NCC: ");
        lblTitleTen.setFont(fontLabelNCC);
        lblTitleTen.setPreferredSize(new Dimension(80, 30));

        txtTenNCC = TaoJtextNhanh.hienThi("Chưa chọn NCC", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxTen.add(lblTitleTen);
        boxTen.add(txtTenNCC);

        Box boxDiaChi = Box.createHorizontalBox();
        boxDiaChi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleDiaChi = new JLabel("Địa chỉ: ");
        lblTitleDiaChi.setFont(fontLabelNCC);
        lblTitleDiaChi.setPreferredSize(new Dimension(80, 30));

        txtDiaChiNCC = TaoJtextNhanh.hienThi("N/A", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxDiaChi.add(lblTitleDiaChi);
        boxDiaChi.add(txtDiaChiNCC);

        Box boxEmail = Box.createHorizontalBox();
        boxEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleEmail = new JLabel("Email: ");
        lblTitleEmail.setFont(fontLabelNCC);
        lblTitleEmail.setPreferredSize(new Dimension(80, 30));

        txtEmailNCC = TaoJtextNhanh.hienThi("N/A", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxEmail.add(lblTitleEmail);
        boxEmail.add(txtEmailNCC);

        pnThongTinNCC.add(boxTen);
        pnThongTinNCC.add(Box.createVerticalStrut(10));
        pnThongTinNCC.add(boxDiaChi);
        pnThongTinNCC.add(Box.createVerticalStrut(10));
        pnThongTinNCC.add(boxEmail);

        int desiredHeight = 150;
        Dimension fixedSize = new Dimension(Integer.MAX_VALUE, desiredHeight);
        pnThongTinNCC.setPreferredSize(fixedSize);
        pnThongTinNCC.setMinimumSize(fixedSize);
        pnThongTinNCC.setMaximumSize(fixedSize);

        pnSidebar.add(pnThongTinNCC);

        pnSidebar.add(Box.createVerticalStrut(100));

        JSeparator lineTotal = new JSeparator();
        lineTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnSidebar.add(lineTotal);
        pnSidebar.add(Box.createVerticalStrut(10));

        Box boxTongTien = Box.createHorizontalBox();
        boxTongTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitleTongTien = new JLabel("Tổng tiền hàng: ");
        lblTitleTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        txtTongTienHang = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.RED);
        txtTongTienHang.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTongTienHang.setBackground(Color.WHITE);
        
        boxTongTien.add(lblTitleTongTien);
        boxTongTien.add(Box.createHorizontalGlue());
        boxTongTien.add(txtTongTienHang);
        
        pnSidebar.add(boxTongTien);
        pnSidebar.add(Box.createVerticalStrut(15));

        btnNhapPhieu = new PillButton(
                "<html>" +
                    "<center>" +
                        "NHẬP PHIẾU<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F9 / Ctrl+Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnNhapPhieu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnNhapPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNhapPhieu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnNhapPhieu.addActionListener(this);
        pnSidebar.add(btnNhapPhieu);
        btnNhapPhieu.setToolTipText("<html><b>Phím tắt:</b> F9 hoặc Ctrl+Enter<br>Lưu phiếu nhập vào hệ thống</html>");
        
        pnSidebar.add(Box.createVerticalStrut(5));
        
        Box boxHuyPhieu = Box.createHorizontalBox();
        boxHuyPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btnHuyPhieu = new JButton("Hủy phiếu (F4)");
        btnHuyPhieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHuyPhieu.setForeground(new Color(120, 120, 120));
        btnHuyPhieu.setBackground(new Color(250, 250, 250));
        btnHuyPhieu.setFocusPainted(false);
        btnHuyPhieu.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnHuyPhieu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuyPhieu.addActionListener(this);
        btnHuyPhieu.addMouseListener(this);
        
        boxHuyPhieu.add(Box.createHorizontalGlue());
        boxHuyPhieu.add(btnHuyPhieu);
        boxHuyPhieu.add(Box.createHorizontalGlue());
        pnSidebar.add(boxHuyPhieu);
    }

    private JLabel taoNhanThongTin(String labelText, String valueText) {
        JLabel label = new JLabel(String.format("<html>%s <b style='color: #333;'>%s</b></html>", labelText, valueText));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return label;
    }

    private void capNhatLaiSTT() {
        Component[] components = pnDanhSachDon.getComponents();
        int stt = 1;
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                panel.setSTT(stt++);
            }
        }
    }

    public void capNhatTongTienHang() {
        double tongTien = 0;
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                tongTien += panel.layTongThanhTien();
            }
        }
        txtTongTienHang.setText(df.format(tongTien) + " đ");
    }

    private Component timComponentTheoTen(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
            if (comp instanceof Container subContainer) {
                 Component found = timComponentTheoTen(subContainer, name);
                 if (found != null) return found;
            }
        }
        return null;
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimSanPham");
        actionMap.put("focusTimSanPham", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.requestFocus();
                txtSearch.setText("");
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control F"), "timSanPham");
        actionMap.put("timSanPham", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.requestFocus();
                txtSearch.selectAll();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F2"), "focusTimNCC");
        actionMap.put("focusTimNCC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimNCC.requestFocus();
                txtTimNCC.selectAll();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control K"), "timNCC");
        actionMap.put("timNCC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimNCC.requestFocus();
                txtTimNCC.selectAll();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control O"), "nhapFile");
        actionMap.put("nhapFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyNhapFile();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F9"), "nhapPhieu");
        actionMap.put("nhapPhieu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyNhapPhieu();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control ENTER"), "nhapPhieuNhanh");
        actionMap.put("nhapPhieuNhanh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyNhapPhieu();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F4"), "resetDonHang");
        actionMap.put("resetDonHang", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pnDanhSachDon.getComponentCount() == 0) {
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(QuanLyPhieuNhap_GUI.this,
                    "Bạn có chắc muốn xóa toàn bộ đơn nhập hàng?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    xoaTatCaDuLieu();
                    JOptionPane.showMessageDialog(QuanLyPhieuNhap_GUI.this,
                        "Đã làm mới đơn nhập hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    QuanLyPhieuNhap_GUI.this,
                    "Bạn có chắc muốn xóa tất cả dữ liệu và làm mới không?",
                    "Xác nhận làm mới",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    xoaTatCaDuLieu();
                }
            }
        });
    }

    private void addFocusOnShow() {
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtSearch.requestFocusInWindow();
                    txtSearch.selectAll();
                });
            }
        });
    }

    private void xoaTatCaDuLieu() {
        pnDanhSachDon.removeAll();
        
        nhaCungCapDaChon = null;
        txtTimNCC.setText("");
        datLaiThongTinNCC();

        txtSearch.setText("");
        
        capNhatTongTienHang();
        
        try {
            String maLoDauTien = taoMaLoTiepTheo();
            if (maLoDauTien != null && maLoDauTien.matches("^LO-\\d{6}$")) {
                this.soLoTiepTheo = Integer.parseInt(maLoDauTien.substring(3));
            } else {
                this.soLoTiepTheo = 1;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi reset mã lô: " + e.getMessage());
            this.soLoTiepTheo = 1;
        }

        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();

        txtTimNCC.requestFocus();
    }

    private void xuLyHuyPhieu() {
        boolean formRong = (pnDanhSachDon.getComponentCount() == 0 && 
                           nhaCungCapDaChon == null && 
                           txtSearch.getText().trim().isEmpty() && 
                           txtTimNCC.getText().trim().isEmpty());
        
        if (formRong) {
            JOptionPane.showMessageDialog(QuanLyPhieuNhap_GUI.this,
                "Form đã rỗng, không cần làm mới!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            txtSearch.requestFocus();
            return;
        }
        
        String message = "Bạn có chắc muốn hủy phiếu và xóa toàn bộ dữ liệu đã nhập?";
        if (pnDanhSachDon.getComponentCount() > 0) {
            message += "\n\n📦 Số sản phẩm đang có: " + pnDanhSachDon.getComponentCount() + " loại";
            message += "\n💰 Tổng giá trị: " + txtTongTienHang.getText();
        }
        
        int confirm = JOptionPane.showConfirmDialog(QuanLyPhieuNhap_GUI.this,
            message, 
            "⚠️ Xác nhận hủy phiếu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            xoaTatCaDuLieu();
            JOptionPane.showMessageDialog(QuanLyPhieuNhap_GUI.this,
                "✅ Đã hủy phiếu và làm mới toàn bộ form!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == txtSearch) {
            xuLyThemLo();
        } else if (source == btnNhapFile) {
            xuLyNhapFile();
        } else if (source == btnNhapPhieu) {
            xuLyNhapPhieu();
        } else if (source == txtTimNCC) {
             xuLyTimNhaCungCap();
        } else if (source == btnHuyPhieu) {
            xuLyHuyPhieu();
        }
    }

    private void xuLyNhapFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("📂 Chọn file Excel để nhập hàng");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        File defaultDir = new File(System.getProperty("user.home") + "/Desktop");
        if (defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        }

        int userSelection = fileChooser.showOpenDialog(mainFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();

            JDialog loadingDialog = new JDialog(mainFrame, "Đang xử lý...", true);
            JPanel loadingPanel = new JPanel(new BorderLayout(10, 10));
            loadingPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
            loadingPanel.setBackground(Color.WHITE);
            
            JLabel lblIcon = new JLabel("⏳");
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel lblMessage = new JLabel("<html><center>Đang đọc file Excel...<br>Vui lòng chờ trong giây lát</center></html>");
            lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
            
            loadingPanel.add(lblIcon, BorderLayout.NORTH);
            loadingPanel.add(lblMessage, BorderLayout.CENTER);
            
            loadingDialog.getContentPane().add(loadingPanel);
            loadingDialog.setSize(350, 180);
            loadingDialog.setLocationRelativeTo(mainFrame);
            loadingDialog.setUndecorated(true);
            loadingDialog.getRootPane().setBorder(new LineBorder(new Color(0, 191, 165), 3));
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    xuLyDocFileExcel(fileToRead);
                    return null;
                }
                
                @Override
                protected void done() {
                    loadingDialog.dispose();
                }
            };
            
            worker.execute();
            loadingDialog.setVisible(true);
        }
    }

    private void xuLyDocFileExcel(File fileToRead) {
        final StringBuilder errorMessages = new StringBuilder();
        final int[] counts = {0, 0};

        try (FileInputStream fis = new FileInputStream(fileToRead);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            String sdtNCC = "";
            String tenNCC = "";
            String diaChiNCC = "";
            String emailNCC = "";
            
            try {
                Row row2 = sheet.getRow(1);
                if (row2 != null && row2.getCell(1) != null) {
                    tenNCC = layGiaTriChuoiTuO(row2.getCell(1));
                }
                
                Row row3 = sheet.getRow(2);
                if (row3 != null && row3.getCell(1) != null) {
                    diaChiNCC = layGiaTriChuoiTuO(row3.getCell(1));
                }
                
                Row row4 = sheet.getRow(3);
                if (row4 != null && row4.getCell(1) != null) {
                    emailNCC = layGiaTriChuoiTuO(row4.getCell(1));
                }
                
                Row row5 = sheet.getRow(4);
                if (row5 != null && row5.getCell(1) != null) {
                    sdtNCC = layGiaTriChuoiTuO(row5.getCell(1));
                    if (!sdtNCC.isEmpty()) {
                        txtTimNCC.setText(sdtNCC);
                        xuLyTimNhaCungCap();
                        
                        if (nhaCungCapDaChon == null) {
                            errorMessages.append("⚠️ Không tìm thấy NCC với SĐT: ").append(sdtNCC).append("\n");
                        }
                    }
                }
            } catch (Exception e) {
                errorMessages.append("⚠️ Lỗi đọc thông tin NCC: ").append(e.getMessage()).append("\n");
            }

            Iterator<Row> rowIterator = sheet.iterator();
            
            for (int i = 0; i < 7 && rowIterator.hasNext(); i++) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String maSP = layGiaTriChuoiTuO(row.getCell(0)); 
                    LocalDate hsd = layGiaTriNgayTuO(row.getCell(1)); 
                    int soLuong = (int) layGiaTriSoTuO(row.getCell(2)); 
                    double donGia_Excel = layGiaTriSoTuO(row.getCell(3)); 
                    String tenDVT_Excel = layGiaTriChuoiTuO(row.getCell(4));

                        if (maSP.isEmpty() && tenDVT_Excel.isEmpty() && (hsd == null || hsd.toString().isEmpty())) {
                            continue;
                        }

                        if (maSP.isEmpty() || tenDVT_Excel.isEmpty() || hsd == null) {
                            throw new Exception("Mã SP, HSD, hoặc Tên ĐVT không được rỗng.");
                        }

                        if (!maSP.matches("^SP-\\d{6}$")) {
                            throw new Exception("Mã SP không hợp lệ. Định dạng: SP-xxxxxx (VD: SP-000001)");
                        }

                        if (soLuong < 0) {
                            throw new Exception("Số lượng nhập không được là số âm! Giá trị hiện tại: " + soLuong);
                        }
                        if (soLuong == 0) {
                            throw new Exception("Số lượng nhập phải lớn hơn 0. Giá trị hiện tại: " + soLuong);
                        }

                        if (donGia_Excel <= 0) {
                            throw new Exception("Đơn giá nhập phải lớn hơn 0. Giá trị hiện tại: " + donGia_Excel);
                        }

                        counts[0]++;

                    } catch (Exception e) {
                        counts[1]++;
                        errorMessages.append("Dòng ").append(row.getRowNum() + 1).append(": ").append(e.getMessage()).append("\n");
                    }
                }

        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "❌ Lỗi nghiêm trọng khi đọc file:\n" + e.getMessage(), 
                    "Lỗi File", 
                    JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            capNhatTongTienHang();
            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    private String layGiaTriChuoiTuO(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return new DecimalFormat("#").format(cell.getNumericCellValue());
        } else {
            return "";
        }
    }

    private double layGiaTriSoTuO(Cell cell) throws Exception {
        if (cell == null) {
            throw new Exception("Ô số lượng/đơn giá bị rỗng.");
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                throw new Exception("Ô '" + cell.getStringCellValue() + "' không phải là số.");
            }
        } else {
            throw new Exception("Ô số lượng/đơn giá có kiểu dữ liệu không hợp lệ.");
        }
    }

    private LocalDate layGiaTriNgayTuO(Cell cell) throws Exception {
    	if (cell == null) return null;

        if (cell.getCellType() == CellType.STRING) {
            String dateString = cell.getStringCellValue().trim();
            if (dateString.isEmpty()) return null;
            
            try {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e1) {
                try {
                    DateTimeFormatter fmtVietnamese = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a", new Locale("vi", "VN"));
                    return LocalDate.parse(dateString, fmtVietnamese);
                } catch (Exception e2) {
                    try {
                        DateTimeFormatter fmtEnglish = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
                        return LocalDate.parse(dateString, fmtEnglish);
                    } catch (Exception e3) {
                        throw new Exception("Định dạng ngày '" + dateString + "' không hợp lệ (Cần dd/MM/yyyy).");
                    }}}}
        
        else if (cell.getCellType() == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            Date javaDate = cell.getDateCellValue();
            return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        else if (cell.getCellType() == CellType.BLANK) {
            return null;
        }
        else {
            throw new Exception("Ô HSD không phải là ngày tháng (hãy định dạng là Text dd/MM/yyyy).");
        }
    }

    private void datLaiThongTinNCC() {
        nhaCungCapDaChon = null;
        txtTenNCC.setText("N/A");
        txtTenNCC.setForeground(Color.GRAY);
        
        txtDiaChiNCC.setText("N/A");
        txtDiaChiNCC.setToolTipText(null);

        txtEmailNCC.setText("N/A");
    }

    private void capNhatThongTinNCC(NhaCungCap ncc) {
        nhaCungCapDaChon = ncc;
        txtTimNCC.setText(ncc.getSoDienThoai());
        txtTimNCC.setForeground(Color.BLACK);

        txtTenNCC.setText(ncc.getTenNhaCungCap());
        txtTenNCC.setForeground(new Color(0x007BFF));
        txtTenNCC.setToolTipText(ncc.getTenNhaCungCap());

        txtDiaChiNCC.setText(ncc.getDiaChi());
        txtDiaChiNCC.setToolTipText(ncc.getDiaChi());

        txtEmailNCC.setText(ncc.getEmail() != null ? ncc.getEmail() : "N/A");
    }

    private void xuLyTimNhaCungCap() {
        String keyword = txtTimNCC.getText().trim();
        if (keyword.isEmpty()) {
            datLaiThongTinNCC(); 
            return;
        }
        
        NhaCungCap ncc = null;
        try {
            Object o = svc.getNhaCungCapByCodeOrPhone(keyword);
            if (o instanceof NhaCungCap) ncc = (NhaCungCap) o;
        } catch (Exception ex) {
            // ignore and fallback
        }
        if (ncc == null) {
            JOptionPane.showMessageDialog(this, 
                "❌ Không tìm thấy nhà cung cấp với số điện thoại: " + keyword + "\nVui lòng kiểm tra lại!", 
                "Không tìm thấy", 
                JOptionPane.ERROR_MESSAGE);
            
            datLaiThongTinNCC();
            txtTimNCC.setText("");
            txtTimNCC.requestFocus();
            return;
        }
        
        if (!ncc.isHoatDong()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Nhà cung cấp '" + ncc.getTenNhaCungCap() + "' đã ngừng hợp tác.\nVui lòng chọn nhà cung cấp khác!", 
                "Nhà cung cấp ngừng hoạt động", 
                JOptionPane.WARNING_MESSAGE);
            
            datLaiThongTinNCC();
            txtTimNCC.selectAll();
            txtTimNCC.requestFocus();
            return;
        }
        
        capNhatThongTinNCC(ncc);
    }

    private ChiTietSanPhamPanel timPanelSanPham(String maSP) {
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                if (panel.laySanPham().getMaSanPham().equals(maSP)) {
                    return panel;
                }
            }
        }
        return null;
    }

    private void xuLyThemLo() {
        if (nhaCungCapDaChon == null) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Vui lòng tìm và chọn Nhà Cung Cấp trước khi thêm sản phẩm!", 
                "Chưa chọn nhà cung cấp", 
                JOptionPane.WARNING_MESSAGE);
            txtTimNCC.requestFocus();
            txtSearch.setText("");
            return;
        }
        
        String maSP = txtSearch.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy sản phẩm phù hợp với mã sản phẩm đã nhập: " + maSP + "\nVui lòng nhập lại mã sản phẩm khác!", 
                    "Không tìm thấy", 
                    JOptionPane.ERROR_MESSAGE);
            txtSearch.requestFocus();
            return;
        }

        SanPham sp = null;
        try {
            Object o = svc.getProductByCode(maSP);
            if (o instanceof SanPham) sp = (SanPham) o;
        } catch (Exception ex) {
            // ignore and fallback
        }
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với mã: " + maSP, "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSearch.selectAll();
            return;
        }

        String maLoHienThi = String.format("LO-%06d", this.soLoTiepTheo);
        
        ArrayList<QuyCachDongGoi> dsQuyCach = new ArrayList<>();
        try {
            java.util.List<?> qcs = svc.getPackagingRulesByProduct(sp.getMaSanPham());
            if (qcs != null) {
                for (Object o : qcs) if (o instanceof QuyCachDongGoi) dsQuyCach.add((QuyCachDongGoi) o);
            }
        } catch (Exception ex) {
            // ignore and fallback
        }
        QuyCachDongGoi qc_goc = dsQuyCach.stream().filter(QuyCachDongGoi::isDonViGoc).findFirst().orElse(null);

        if (dsQuyCach == null || dsQuyCach.isEmpty() || qc_goc == null) {
            JOptionPane.showMessageDialog(this, "Sản phẩm '" + sp.getTenSanPham() + "' chưa được cấu hình Quy Cách Đóng Gói (hoặc thiếu Đơn Vị Gốc).\nVui lòng kiểm tra trong Quản lý sản phẩm.", "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ThemLo_Dialog dialog = new ThemLo_Dialog(mainFrame, sp, maLoHienThi, dsQuyCach, qc_goc);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                int soLuongNhapDaQuyDoi = dialog.getSoLuongNhap();
                
                if (soLuongNhapDaQuyDoi < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng nhập không được là số âm!\nGiá trị: " + soLuongNhapDaQuyDoi, 
                        "Lỗi số lượng", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (soLuongNhapDaQuyDoi == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng nhập phải lớn hơn 0!", 
                        "Lỗi số lượng", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double donGiaGoc = dialog.getDonGiaNhap(); 
                DonViTinh dvtGoc = dialog.getDonViTinh(); 
                LoSanPham loMoi = dialog.getLoSanPham();
                
                QuyCachDongGoi quyCachDaChon = dialog.getQuyCachDaChon();
                int soLuongHienThi = dialog.getSoLuongHienThi();

                ChiTietPhieuNhap chiTietMoi = new ChiTietPhieuNhap();
                chiTietMoi.setLoSanPham(loMoi);
                chiTietMoi.setDonViTinh(dvtGoc);
                chiTietMoi.setSoLuongNhap(soLuongNhapDaQuyDoi);
                chiTietMoi.setDonGiaNhap(donGiaGoc); 

                ChiTietSanPhamPanel panelSanPham = timPanelSanPham(sp.getMaSanPham());

                if (panelSanPham != null) {
                    if (!panelSanPham.layDonViTinh().equals(dvtGoc) || panelSanPham.layDonGia() != donGiaGoc) { 
                        JOptionPane.showMessageDialog(this,
                            String.format("Lỗi: Lô mới phải có cùng Đơn vị tính (%s) và Đơn giá (%,.0f đ) với các lô đã thêm.",
                                panelSanPham.layDonViTinh().getTenDonViTinh(), panelSanPham.layDonGia()), 
                            "Lỗi Thêm Lô", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    panelSanPham.themLot(chiTietMoi, quyCachDaChon, soLuongHienThi);
                } else {
                    ChiTietSanPhamPanel newPanel = new ChiTietSanPhamPanel(sp, dvtGoc, donGiaGoc);
                    newPanel.themLot(chiTietMoi, quyCachDaChon, soLuongHienThi);
                    pnDanhSachDon.add(newPanel);
                    capNhatLaiSTT(); 
                }

                this.soLoTiepTheo++; 

                capNhatTongTienHang();
                pnDanhSachDon.revalidate();
                pnDanhSachDon.repaint();
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));

                txtSearch.setText("");
                txtSearch.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm lô: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void xuLyNhapPhieu() {
        if (nhaCungCapDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTimNCC.requestFocus();
            return;
        }
        if (nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin Nhân Viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pnDanhSachDon.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm nào.", "Phiếu nhập rỗng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận nhập phiếu với nhà cung cấp '" + nhaCungCapDaChon.getTenNhaCungCap() + "'?",
            "Xác nhận nhập phiếu", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        PhieuNhap phieuNhapMoi = new PhieuNhap();
        String maPN = null;
        try {
            maPN = svc.taoMaPhieuNhap();
        } catch (Exception ex) {
            // ignore and fallback
        }
        if (maPN == null) maPN = "PN-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0001";
        phieuNhapMoi.setMaPhieuNhap(maPN);
        phieuNhapMoi.setNgayNhap(LocalDate.now());
        phieuNhapMoi.setNhanVien(nhanVienDangNhap);
        phieuNhapMoi.setNhaCungCap(nhaCungCapDaChon);

        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                List<ChiTietPhieuNhap> dsLoCuaPanel = panel.layTatCaChiTiet(phieuNhapMoi); 
                dsChiTiet.addAll(dsLoCuaPanel);
            }
        }

        phieuNhapMoi.setChiTietPhieuNhapList(dsChiTiet);
        boolean success = false;
        try {
            success = svc.createPhieuNhap(phieuNhapMoi);
        } catch (Exception ex) {
            // ignore and fallback
        }
        if (success) {
            hienThiHoaDon(phieuNhapMoi);

            JOptionPane.showMessageDialog(this, "Nhập phiếu thành công!\nMã phiếu: " + phieuNhapMoi.getMaPhieuNhap(),
                                          "Thành công", JOptionPane.INFORMATION_MESSAGE);

            pnDanhSachDon.removeAll();
            capNhatTongTienHang();

            txtTimNCC.setText("");
            datLaiThongTinNCC();

            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();

        } else {
            JOptionPane.showMessageDialog(this, "Nhập phiếu thất bại! Vui lòng kiểm tra log lỗi.",
                                          "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiHoaDon(PhieuNhap phieuNhap) {
        JDialog dialog = new JDialog(mainFrame, "Hóa Đơn Nhập Hàng", true);
        dialog.setSize(650, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);
        dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("HÓA ĐƠN NHẬP HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLACK);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JPanel pnHeader = new JPanel(new GridLayout(0, 2, 20, 8));
        pnHeader.setOpaque(false);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        pnHeader.add(taoNhanThuong("Mã hóa đơn nhập:", labelFont)); 
        pnHeader.add(taoNhanInDam(phieuNhap.getMaPhieuNhap(), labelFont)); 

        pnHeader.add(taoNhanThuong("Nhân viên:", labelFont)); 
        pnHeader.add(taoNhanInDam(phieuNhap.getNhanVien().getTenNhanVien(), labelFont)); 

        pnHeader.add(taoNhanThuong("Ngày lập phiếu:", labelFont)); 
        pnHeader.add(taoNhanInDam(phieuNhap.getNgayNhap().format(fmtDate), labelFont)); 

        pnHeader.add(taoNhanThuong("Nhà cung cấp:", labelFont)); 
        pnHeader.add(taoNhanInDam(phieuNhap.getNhaCungCap().getTenNhaCungCap(), labelFont)); 

        pnHeader.add(taoNhanThuong("Điện thoại:", labelFont)); 
        pnHeader.add(taoNhanInDam(phieuNhap.getNhaCungCap().getSoDienThoai(), labelFont)); 

        pnHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        centerPanel.add(pnHeader);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(Box.createVerticalStrut(10));

        JLabel lblChiTiet = new JLabel("Chi tiết sản phẩm nhập");
        lblChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChiTiet.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblChiTiet);
        centerPanel.add(Box.createVerticalStrut(5));

        String[] columns = {"Tên sản phẩm", "Đơn vị tính", "Số lô", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ChiTietPhieuNhap ct : phieuNhap.getChiTietPhieuNhapList()) {
            model.addRow(new Object[]{
                ct.getLoSanPham().getSanPham().getTenSanPham(),
                ct.getDonViTinh().getTenDonViTinh(),
                ct.getLoSanPham().getMaLo(),
                ct.getSoLuongNhap(),
                df.format(ct.getDonGiaNhap()) + " đ",
                df.format(ct.getThanhTien()) + " đ"
            });
        }

        JTable table = new JTable(model);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(25);

        JScrollPane scrollTable = new JScrollPane(table);
        centerPanel.add(scrollTable);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel pnFooter = new JPanel();
        pnFooter.setLayout(new BoxLayout(pnFooter, BoxLayout.Y_AXIS));
        pnFooter.setOpaque(false);

        pnFooter.add(Box.createVerticalStrut(10));

        JLabel lblTongCong = new JLabel(String.format("Tổng hóa đơn: %s đ", df.format(phieuNhap.getTongTien())));
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongCong.setForeground(Color.BLACK);
        lblTongCong.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pnFooter.add(lblTongCong);

        pnFooter.add(Box.createVerticalStrut(15));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnClose.addActionListener(e -> dialog.dispose());
        pnFooter.add(btnClose);

        mainPanel.add(pnFooter, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel taoNhanInDam(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(Font.BOLD));
        return label;
    }

    private JLabel taoNhanThuong(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    class ThongTinHienThi {
        QuyCachDongGoi quyCach;
        int soLuong;
        
        ThongTinHienThi(QuyCachDongGoi quyCach, int soLuong) {
            this.quyCach = quyCach;
            this.soLuong = soLuong;
        }
    }

    class ChiTietSanPhamPanel extends JPanel {
        private SanPham sanPham;
        private DonViTinh donViTinh;
        private double donGia;
        private List<ChiTietPhieuNhap> dsChiTietCuaSP;
        
        private java.util.Map<ChiTietPhieuNhap, ThongTinHienThi> mapThongTinHienThi = new java.util.HashMap<>();

        private JLabel lblSTT;
        private JLabel lblTenSP;
        private JTextField txtTongSoLuong;
        private JLabel lblDonViTinh;
        private JLabel lblDonGia;
        private JLabel lblTongThanhTien;
        
        private JPanel pnDanhSachLo; 
        private JScrollPane scrollLots; 
        private JPanel pnRow2; 
        private JButton btnChonLo; 

        public ChiTietSanPhamPanel(SanPham sp, DonViTinh dvt, double donGia) {
            this.sanPham = sp;
            this.donViTinh = dvt;
            this.donGia = donGia;
            this.dsChiTietCuaSP = new ArrayList<>();

            setLayout(new BorderLayout(5, 5));
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(5, 10, 5, 10)
            ));

            JPanel pnMain = new JPanel();
            pnMain.setOpaque(false);
            pnMain.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.gridy = 0; 
            gbc.gridheight = 1; 

            gbc.gridx = 0; gbc.weightx = 0;
            lblSTT = new JLabel("1");
            lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblSTT.setForeground(Color.black);
            lblSTT.setPreferredSize(new Dimension(30, 40));
            lblSTT.setHorizontalAlignment(SwingConstants.CENTER);
            pnMain.add(lblSTT, gbc);

            gbc.gridx = 2; gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            lblTenSP = new JLabel(sp.getTenSanPham());
            lblTenSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
            pnMain.add(lblTenSP, gbc);
            
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0;

            gbc.gridx = 3; 
            lblDonViTinh = new JLabel(dvt.getTenDonViTinh()); 
            lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblDonViTinh.setPreferredSize(new Dimension(80, 30));
            lblDonViTinh.setHorizontalAlignment(SwingConstants.CENTER);
            pnMain.add(lblDonViTinh, gbc);

            gbc.gridx = 4;
            gbc.fill = GridBagConstraints.NONE;
            
            txtTongSoLuong = new JTextField("0"); 
            txtTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));
            txtTongSoLuong.setForeground(Color.BLACK);
            txtTongSoLuong.setEditable(false); 
            txtTongSoLuong.setBackground(Color.WHITE); 
            txtTongSoLuong.setHorizontalAlignment(JTextField.CENTER); 
            
            txtTongSoLuong.setPreferredSize(new Dimension(80, 30)); 
            txtTongSoLuong.setMinimumSize(new Dimension(80, 30));
            
            txtTongSoLuong.setBorder(new LineBorder(new Color(0xD1D5DB), 1));
            
            pnMain.add(txtTongSoLuong, gbc);

            gbc.gridx = 5;
            lblDonGia = new JLabel(df.format(donGia) + " đ"); 
            lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblDonGia.setPreferredSize(new Dimension(120, 30));
            lblDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
            pnMain.add(lblDonGia, gbc);

            gbc.gridx = 6;
            lblTongThanhTien = new JLabel("0 đ");
            lblTongThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTongThanhTien.setPreferredSize(new Dimension(140, 30));
            lblTongThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
            pnMain.add(lblTongThanhTien, gbc);

            add(pnMain, BorderLayout.CENTER);

            pnRow2 = new JPanel(new BorderLayout(10, 5)); 
            pnRow2.setOpaque(false);

            btnChonLo = new JButton("Chọn Lô");
            btnChonLo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
            btnChonLo.setMargin(new Insets(2, 8, 2, 8));
            btnChonLo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChonLo.setBackground(Color.WHITE);
            btnChonLo.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            btnChonLo.setFocusPainted(false);
            
            JPanel pnButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,0, 0));
            pnButtonWrapper.setOpaque(false);
            pnButtonWrapper.add(btnChonLo);
            pnButtonWrapper.setBorder(new EmptyBorder(0, 60, 0, 0)); 
            
            pnRow2.add(pnButtonWrapper, BorderLayout.WEST);

            pnDanhSachLo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            pnDanhSachLo.setOpaque(true); 
            pnDanhSachLo.setBackground(Color.WHITE);
            
            scrollLots = new JScrollPane(pnDanhSachLo);
            scrollLots.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollLots.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollLots.setBorder(null);
            scrollLots.setOpaque(false);
            scrollLots.getViewport().setOpaque(false);
            scrollLots.setPreferredSize(new Dimension(100, 45)); 

            pnRow2.add(scrollLots, BorderLayout.CENTER);
            add(pnRow2, BorderLayout.SOUTH);
            
            capNhatTongSoLuongVaTien();
        }

        public void setSTT(int stt) {
            lblSTT.setText(String.valueOf(stt));
        }

        private void xoaLoKhoiPanel(ChiTietPhieuNhap chiTiet) {
            if (dsChiTietCuaSP.contains(chiTiet)) {
                dsChiTietCuaSP.remove(chiTiet);
            }
            xoaTagChiTiet(chiTiet);
            capNhatTongSoLuongVaTien(); 
            
            if (dsChiTietCuaSP.isEmpty()) {
                pnDanhSachDon.remove(this);
                capNhatLaiSTT(); 
                
                pnDanhSachDon.revalidate();
                pnDanhSachDon.repaint();
                capNhatTongTienHang(); 
            }
        }

        public SanPham laySanPham() { return sanPham; }
        public DonViTinh layDonViTinh() { return donViTinh; }
        public double layDonGia() { return donGia; }
        public double layTongThanhTien() { 
            double total = 0;
            for (ChiTietPhieuNhap ct : dsChiTietCuaSP) total += ct.getThanhTien();
            return total;
        }
        public List<ChiTietPhieuNhap> layTatCaChiTiet(PhieuNhap pn) { 
            for(ChiTietPhieuNhap ctpn : dsChiTietCuaSP) {
                ctpn.setPhieuNhap(pn); 
                ctpn.getLoSanPham().setSoLuongTon(ctpn.getSoLuongNhap()); 
            }
            return dsChiTietCuaSP;
        }
        
        public java.util.Map<ChiTietPhieuNhap, ThongTinHienThi> layMapThongTinHienThi() {
            return mapThongTinHienThi;
        }
        private void xoaTagChiTiet(ChiTietPhieuNhap chiTiet) {
            String maLoCanXoa = chiTiet.getLoSanPham().getMaLo();
            for (Component comp : pnDanhSachLo.getComponents()) {
                if (comp instanceof JPanel pnlLoTag) {
                    if (pnlLoTag.getName() != null && pnlLoTag.getName().equals(maLoCanXoa)) {
                        pnDanhSachLo.remove(pnlLoTag);
                        return; 
                    }
                }
            }
        }
        public void themLot(ChiTietPhieuNhap chiTiet, QuyCachDongGoi quyCachHienThi, int soLuongHienThi) {
            dsChiTietCuaSP.add(chiTiet);
            
            if (quyCachHienThi != null && soLuongHienThi > 0) {
                mapThongTinHienThi.put(chiTiet, new ThongTinHienThi(quyCachHienThi, soLuongHienThi));
            }
            
            JPanel pnlLoTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            pnlLoTag.setBackground(new Color(0x3B82F6));
            pnlLoTag.setBorder(new EmptyBorder(2, 5, 2, 5));
            pnlLoTag.setName(chiTiet.getLoSanPham().getMaLo());
            
            String tenDonViHienThi;
            int soLuongDisplay;
            
            ThongTinHienThi ttht = mapThongTinHienThi.get(chiTiet);
            if (ttht != null) {
                tenDonViHienThi = ttht.quyCach.getDonViTinh().getTenDonViTinh();
                soLuongDisplay = ttht.soLuong;
            } else {
                tenDonViHienThi = chiTiet.getDonViTinh().getTenDonViTinh();
                soLuongDisplay = chiTiet.getSoLuongNhap();
            }
            
            String loText = String.format("%s - %s - SL: %d %s", 
                chiTiet.getLoSanPham().getMaLo(), 
                chiTiet.getLoSanPham().getHanSuDung().format(fmtDate), 
                soLuongDisplay,
                tenDonViHienThi);
            
            JLabel lblLoInfo = new JLabel(loText);
            lblLoInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblLoInfo.setForeground(Color.WHITE);
            pnlLoTag.add(lblLoInfo);
            JButton btnXoaLo = new JButton("X");
            btnXoaLo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnXoaLo.setForeground(Color.WHITE);
            btnXoaLo.setMargin(new Insets(0, 2, 0, 2));
            btnXoaLo.setBorder(null);
            btnXoaLo.setContentAreaFilled(false);
            btnXoaLo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnXoaLo.addActionListener(e -> {
                if (dsChiTietCuaSP.contains(chiTiet)) dsChiTietCuaSP.remove(chiTiet);
                pnDanhSachLo.remove(pnlLoTag);
                capNhatTongSoLuongVaTien();
                if (dsChiTietCuaSP.isEmpty()) {
                    pnDanhSachDon.remove(this);
                    capNhatLaiSTT();
                    pnDanhSachDon.revalidate();
                    pnDanhSachDon.repaint();
                    capNhatTongTienHang();
                }
            });
            pnlLoTag.add(btnXoaLo);
            pnDanhSachLo.add(pnlLoTag);
            capNhatTongSoLuongVaTien();
        }
        private void capNhatTongSoLuongVaTien() {
            int tongSoLuong = 0;
            double tongThanhTien = 0;
            for (ChiTietPhieuNhap ct : dsChiTietCuaSP) {
                tongSoLuong += ct.getSoLuongNhap();
                tongThanhTien += ct.getThanhTien();
            }
            txtTongSoLuong.setText(String.valueOf(tongSoLuong));
            lblTongThanhTien.setText(df.format(tongThanhTien) + " đ");
            capNhatTongTienHang();
            int totalHeight = 150; 
            setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));
            setPreferredSize(new Dimension(getPreferredSize().width, totalHeight));
            revalidate();
            repaint();
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == btnHuyPhieu) {
			btnHuyPhieu.setForeground(new Color(220, 53, 69));
			btnHuyPhieu.setBackground(new Color(255, 245, 245));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() == btnHuyPhieu) {
			btnHuyPhieu.setForeground(new Color(120, 120, 120));
			btnHuyPhieu.setBackground(new Color(250, 250, 250));
		}
	}

    private String taoMaLoTiepTheo() {
        int max = 0;
        for (Object item : svc.getAllLots()) {
            if (item instanceof LoSanPham lo && lo.getMaLo() != null && lo.getMaLo().matches("^LO-\\d{6}$")) {
                max = Math.max(max, Integer.parseInt(lo.getMaLo().substring(3)));
            }
        }
        return String.format("LO-%06d", max + 1);
    }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Nhập Phiếu");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1500, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new QuanLyPhieuNhap_GUI());
			frame.setVisible(true);
		});
	}
}
