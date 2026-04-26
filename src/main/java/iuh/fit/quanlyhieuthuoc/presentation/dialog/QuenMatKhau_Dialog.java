package iuh.fit.quanlyhieuthuoc.presentation.dialog;

import iuh.fit.quanlyhieuthuoc.infrastructure.persistence.TaiKhoanRepositoryImpl;
import iuh.fit.quanlyhieuthuoc.presentation.component.button.PillButton; 
import iuh.fit.quanlyhieuthuoc.presentation.component.border.RoundedBorder;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@SuppressWarnings("serial")
public class QuenMatKhau_Dialog extends JDialog {

    private JTextField txtMaNV, txtTenNV, txtSDT;
    private JDateChooser txtNgaySinh;
    private JPasswordField txtMatKhauMoi, txtXacNhanMK;
    private TaiKhoanRepositoryImpl tkDao;
    
    // Màu chủ đạo
    private final Color PRIMARY_COLOR = new Color(0, 96, 100); 
    private final Color ACCENT_COLOR = new Color(0, 150, 136); 
    private final Color BACKGROUND_COLOR = new Color(224, 247, 250); 
    private final Color TEXT_COLOR = new Color(66, 66, 66);

    public QuenMatKhau_Dialog(JFrame parent) {
        super(parent, "Khôi phục mật khẩu", true);
        tkDao = new TaiKhoanRepositoryImpl();
        setSize(550, 720); 
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        
        initUI();
    }

    private void initUI() {
        // 1. Panel chính
        JPanel pnMain = new JPanel(null);
        pnMain.setBackground(BACKGROUND_COLOR);
        add(pnMain, BorderLayout.CENTER);

        // 2. Header Panel
        JPanel pnHeader = new JPanel();
        pnHeader.setBounds(0, 0, 550, 80);
        pnHeader.setBackground(PRIMARY_COLOR);
        pnHeader.setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("KHÔI PHỤC MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnHeader.add(lblTitle, BorderLayout.CENTER);
        
        JLabel lblSubTitle = new JLabel("Nhập thông tin xác thực để đổi mật khẩu mới", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.ITALIC, 13));
        lblSubTitle.setForeground(new Color(178, 235, 242));
        lblSubTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnHeader.add(lblSubTitle, BorderLayout.SOUTH);

        pnMain.add(pnHeader);

        // 3. Container trắng chứa form
        JPanel pnForm = new JPanel(null);
        pnForm.setBounds(25, 100, 485, 560);
        pnForm.setBackground(Color.WHITE);
        pnForm.setBorder(new RoundedBorder(20)); 
        pnMain.add(pnForm);

        // --- CÁC COMPONENT TRONG FORM ---
        int xLabel = 40, xText = 40;
        int widthComp = 405;
        int heightText = 40;
        int yStart = 30;
        int gap = 75; 

        // 1. Mã Nhân Viên
        addStyledLabel(pnForm, "Mã nhân viên", xLabel, yStart);
        txtMaNV = createStyledTextField(pnForm, xText, yStart + 25, widthComp, heightText);

        // 2. Tên Nhân Viên
        addStyledLabel(pnForm, "Họ và tên", xLabel, yStart + gap);
        txtTenNV = createStyledTextField(pnForm, xText, yStart + gap + 25, widthComp, heightText);

        // 3. Số Điện Thoại & Ngày sinh
        addStyledLabel(pnForm, "Số điện thoại", xLabel, yStart + gap * 2);
        txtSDT = createStyledTextField(pnForm, xText, yStart + gap * 2 + 25, 190, heightText);

        addStyledLabel(pnForm, "Ngày sinh", 255, yStart + gap * 2);
        
        // Tạo JDateChooser
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setBounds(255, yStart + gap * 2 + 25, 190, heightText);
        txtNgaySinh.setDateFormatString("yyyy-MM-dd");
        txtNgaySinh.setFont(new Font("Arial", Font.PLAIN, 16));
        JTextField dateEditor = (JTextField) txtNgaySinh.getDateEditor().getUiComponent();
        dateEditor.setFont(new Font("Arial", Font.PLAIN, 16));
        dateEditor.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        txtNgaySinh.setBorder(new RoundedBorder(10));
        pnForm.add(txtNgaySinh);

        // Đường kẻ phân cách
        JSeparator sep = new JSeparator();
        sep.setBounds(40, yStart + gap * 3 + 15, 405, 2);
        sep.setForeground(new Color(200, 200, 200));
        pnForm.add(sep);

        // === KHỞI TẠO ICON ===
        // Lưu ý: Đường dẫn ảnh phải chính xác
        ImageIcon iconOpen = new ImageIcon(new ImageIcon(getClass().getResource("/resources/images/eye_open.png")).getImage()
                .getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon iconClose = new ImageIcon(new ImageIcon(getClass().getResource("/resources/images/eye_close.png")).getImage()
                .getScaledInstance(25, 25, Image.SCALE_SMOOTH));

        // 4. Mật Khẩu Mới
        addStyledLabel(pnForm, "Mật khẩu mới", xLabel, yStart + gap * 3 + 30);
        txtMatKhauMoi = createStyledPasswordField(pnForm, xText, yStart + gap * 3 + 30 + 25, widthComp, heightText);
        setupPasswordToggle(pnForm, txtMatKhauMoi, iconOpen, iconClose);

        // 5. Xác Nhận Mật Khẩu
        addStyledLabel(pnForm, "Xác nhận mật khẩu", xLabel, yStart + gap * 4 + 30);
        txtXacNhanMK = createStyledPasswordField(pnForm, xText, yStart + gap * 4 + 30 + 25, widthComp, heightText);
        setupPasswordToggle(pnForm, txtXacNhanMK, iconOpen, iconClose);

        // Nút Xác Nhận
        JButton btnXacNhan = new PillButton("XÁC NHẬN ĐỔI MẬT KHẨU");
        btnXacNhan.setBounds(40, 480, 405, 50);
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 16));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setBackground(PRIMARY_COLOR); 
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnXacNhan.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnXacNhan.setBackground(ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                btnXacNhan.setBackground(PRIMARY_COLOR);
            }
        });
        
        btnXacNhan.addActionListener(e -> xuLyDoiMatKhau());
        pnForm.add(btnXacNhan);
    }

    // --- HELPER METHODS ---

    private void addStyledLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);
        lbl.setBounds(x, y, 300, 20);
        panel.add(lbl);
    }

    // Tạo TextField thường (Tên, Mã, SĐT)
    private JTextField createStyledTextField(JPanel panel, int x, int y, int w, int h) {
        JTextField txt = new JTextField();
        txt.setBounds(x, y, w, h);
        txt.setFont(new Font("Arial", Font.PLAIN, 16));
        txt.setForeground(Color.BLACK);
        // Padding phải là 10 (bình thường)
        txt.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10), 
                new EmptyBorder(0, 10, 0, 10) 
        ));
        addFocusEffect(txt);
        panel.add(txt);
        return txt;
    }

    // Tạo PasswordField (Có chừa chỗ cho nút mắt)
    private JPasswordField createStyledPasswordField(JPanel panel, int x, int y, int w, int h) {
        JPasswordField txt = new JPasswordField();
        txt.setBounds(x, y, w, h);
        txt.setFont(new Font("Arial", Font.PLAIN, 16));
        txt.setForeground(Color.BLACK);
        
        // 🔥 QUAN TRỌNG: Padding phải là 45 để chừa chỗ cho nút mắt
        txt.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10), 
                new EmptyBorder(0, 10, 0, 45) 
        ));
        
        addFocusEffect(txt);
        panel.add(txt);
        return txt;
    }

    // Hiệu ứng Focus đổi màu nền
    private void addFocusEffect(JTextField comp) {
        comp.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                comp.setBackground(new Color(245, 253, 255)); 
            }
            @Override
            public void focusLost(FocusEvent e) {
                comp.setBackground(Color.WHITE);
            }
        });
    }

/**
     * 💡 Hàm tạo nút mắt toggle ẩn/hiện mật khẩu (Đã Fix lỗi mất icon)
     */
    private void setupPasswordToggle(JPanel panel, JPasswordField txtPass, ImageIcon iconOpen, ImageIcon iconClose) {
        JButton btnToggle = new JButton(iconOpen);
        
        // Căn vị trí: Nằm bên phải, thụt vào 35px so với mép phải của ô nhập
        int size = 25;
        // Tính toán Y để nút nằm giữa ô input theo chiều dọc
        int yPos = txtPass.getY() + (txtPass.getHeight() - size) / 2;
        
        btnToggle.setBounds(txtPass.getX() + txtPass.getWidth() - 35, yPos, size, size);
        
        // Style nút trong suốt
        btnToggle.setFocusPainted(false);
        btnToggle.setBorderPainted(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.setFocusable(false); 

        // Xử lý sự kiện click (Hiện/Ẩn)
        btnToggle.addActionListener(e -> {
            if (txtPass.getEchoChar() != (char) 0) {
                txtPass.setEchoChar((char) 0); // Hiện
                btnToggle.setIcon(iconClose);
            } else {
                txtPass.setEchoChar('●'); // Ẩn
                btnToggle.setIcon(iconOpen);
            }
            // Focus lại vào ô mật khẩu để gõ tiếp được ngay
            txtPass.requestFocusInWindow();
        });

        panel.add(btnToggle);
        // Đưa nút lên lớp trên cùng (Layer 0)
        panel.setComponentZOrder(btnToggle, 0);

        // --- PHẦN QUAN TRỌNG NHẤT ĐỂ SỬA LỖI ---
        
        // Tạo một hành động vẽ lại nút mắt an toàn
        Runnable repaintButton = () -> {
            // invokeLater: Chờ ô mật khẩu vẽ xong mới vẽ nút mắt đè lên
            SwingUtilities.invokeLater(() -> {
                btnToggle.repaint();
                // Đảm bảo Z-Order luôn đúng mỗi khi vẽ lại
                panel.setComponentZOrder(btnToggle, 0); 
            });
        };

        // 1. Bắt sự kiện gõ phím
        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                repaintButton.run();
            }
        });

        // 2. Bắt sự kiện Focus (Click vào ô)
        txtPass.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaintButton.run();
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaintButton.run();
            }
        });
        
        // 3. Bắt sự kiện thay đổi nội dung (Copy/Paste chuột phải)
        txtPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { repaintButton.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { repaintButton.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { repaintButton.run(); }
        });
    }
    // --- LOGIC XỬ LÝ ---
    private void xuLyDoiMatKhau() {
        String maNV = txtMaNV.getText().trim();
        String tenNV = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        Date date = txtNgaySinh.getDate();
        
        String mkMoi = new String(txtMatKhauMoi.getPassword()).trim();
        String mkXacNhan = new String(txtXacNhanMK.getPassword()).trim();

        if (maNV.isEmpty() || tenNV.isEmpty() || date == null || sdt.isEmpty() || mkMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngaySinh = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (!mkMoi.equals(mkXacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi xác nhận", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!mkMoi.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")) {
             JOptionPane.showMessageDialog(this, 
                 "Mật khẩu mới phải có ít nhất:\n- 8 ký tự\n- 1 chữ hoa\n- 1 chữ thường\n- 1 số", 
                 "Mật khẩu yếu", JOptionPane.WARNING_MESSAGE);
             return;
        }

        String maTaiKhoan = tkDao.timTaiKhoanQuenMK(maNV, tenNV, sdt, ngaySinh);
        
        if (maTaiKhoan == null) {
            JOptionPane.showMessageDialog(this, "Thông tin xác thực không chính xác!\nVui lòng kiểm tra lại.", "Xác thực thất bại", JOptionPane.ERROR_MESSAGE);
        } else {
            boolean ketQua = tkDao.capNhatMatKhau(maTaiKhoan, mkMoi);
            if (ketQua) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Đã có lỗi xảy ra khi cập nhật mật khẩu.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
