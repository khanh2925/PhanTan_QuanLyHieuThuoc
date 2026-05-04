package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.*;
// import java.time.LocalDate; // Không cần thiết
// import java.util.List; // Không cần thiết

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import presentation.component.panel.ImagePanel;
import presentation.component.button.PillButton;
import presentation.component.border.RoundedBorder;
import presentation.dialog.QuenMatKhau_Dialog;
import dto.TaiKhoanDTO;
import entity.Session;
import network.ClientService;

@SuppressWarnings("serial")
public class DangNhap_GUI extends JFrame {

	private JTextField txtTaiKhoan;
	private JPasswordField txtMatKhau;

	// Network based authentication (will call server)

	public DangNhap_GUI() {
		// Thiết lập màn hình hiển thị toàn bộ
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		initialize();
		setVisible(true); // Hiển thị khung sau khi khởi tạo
	}

	private void initialize() {
		setTitle("Đăng nhập");
		// setSize(1920, 1080); // Đã dùng setExtendedState
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel pnMain = new JPanel(new BorderLayout());
		add(pnMain, BorderLayout.CENTER);

		pnMain.add(createLeftPanel(), BorderLayout.WEST);
		pnMain.add(createLoginFormPanel(), BorderLayout.CENTER);
	}

	private JPanel createLeftPanel() {
		JPanel pnLeft = new JPanel(new BorderLayout());
		pnLeft.setPreferredSize(new Dimension(1256, 1080));
		pnLeft.setBackground(new Color(0xB2EBF2));

		ImagePanel pnlCenterBackground = new ImagePanel(
				new ImageIcon(getClass().getResource("/resources/images/Login.png")).getImage());
		pnLeft.add(pnlCenterBackground, BorderLayout.CENTER);

		return pnLeft;
	}

	@SuppressWarnings("deprecation")
	private JPanel createLoginFormPanel() {
		JPanel pnFormDangNhap = new JPanel(null);
		pnFormDangNhap.setBackground(new Color(0xE0F7FA));

		ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/images/Logo.png"));
		Image logoImage = logoIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
		JLabel lblLogo = new JLabel(new ImageIcon(logoImage));
		lblLogo.setBounds(190, 30, 250, 250);
		pnFormDangNhap.add(lblLogo);

		JLabel lblTieuDeForm = new JLabel("Chào mừng đến với Hòa An");
		lblTieuDeForm.setHorizontalAlignment(SwingConstants.CENTER);
		lblTieuDeForm.setFont(new Font("Arial", Font.BOLD, 36));
		lblTieuDeForm.setForeground(new Color(0x006064));
		lblTieuDeForm.setBounds(39, 290, 570, 61);
		pnFormDangNhap.add(lblTieuDeForm);

		int inputWidth = 532;
		int inputHeight = 50;

		JLabel lblTaiKhoan = new JLabel("Tài khoản");
		lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 24));
		lblTaiKhoan.setBounds(50, 399, 129, 30);
		pnFormDangNhap.add(lblTaiKhoan);

		txtTaiKhoan = new JTextField();
		txtTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 20));
		txtTaiKhoan.setBounds(50, 439, inputWidth, inputHeight);
		txtTaiKhoan.setOpaque(false);
		txtTaiKhoan.setBorder(new RoundedBorder(20));
		txtTaiKhoan.setMargin(new Insets(5, 15, 5, 15));
		pnFormDangNhap.add(txtTaiKhoan);
		addPlaceholder(txtTaiKhoan, "Nhập tài khoản của bạn");
		txtTaiKhoan.addActionListener(e -> xuLyDangNhap());

		JLabel lblMatKhau = new JLabel("Mật khẩu");
		lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 24));
		lblMatKhau.setBounds(50, 518, 100, 30);
		pnFormDangNhap.add(lblMatKhau);

		JPanel pnMatKhau = new JPanel(null);
		pnMatKhau.setBorder(UIManager.getBorder("PasswordField.border"));
		pnMatKhau.setBounds(50, 558, inputWidth, inputHeight);
		pnMatKhau.setOpaque(false);
		pnMatKhau.setBorder(new RoundedBorder(20));
		pnFormDangNhap.add(pnMatKhau);

		// === Ô nhập mật khẩu ===
		txtMatKhau = new JPasswordField();
		txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 20));
		// NOTE: Vị trí của JPasswordField phải được căn chỉnh thủ công
		// Đã căn lại vị trí, nhưng để trong JLayeredPane hoặc null layout phức tạp
		// Tạm thời dùng vị trí này để tránh xung đột với placeholder
		txtMatKhau.setBounds(50, 558, inputWidth, inputHeight); // Dùng vị trí và kích thước của pnMatKhau
		txtMatKhau.setOpaque(false);
		txtMatKhau.setBorder(new RoundedBorder(20)); // Cần có border trùng với pnMatKhau để hiệu ứng nhìn đồng nhất

		txtMatKhau.setMargin(new Insets(5, 15, 5, 45));
		pnFormDangNhap.add(txtMatKhau);
		addPlaceholder(txtMatKhau, "Nhập mật khẩu của bạn");
		txtMatKhau.addActionListener(e -> xuLyDangNhap());

		// === 1. Khởi tạo Icon ===
		ImageIcon iconOpen = new ImageIcon(
				new ImageIcon(getClass().getResource("/resources/images/eye_open.png")).getImage()
						.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ImageIcon iconClose = new ImageIcon(
				new ImageIcon(getClass().getResource("/resources/images/eye_close.png")).getImage()
						.getScaledInstance(25, 25, Image.SCALE_SMOOTH));

		// === 2. Tạo nút toggle và cấu hình giao diện ===
		JButton btnTogglePassword = new JButton(iconOpen);
		btnTogglePassword.setBounds(50 + inputWidth - 45, 558 + 5, 30, 40);
		btnTogglePassword.setFocusPainted(false);
		btnTogglePassword.setBorderPainted(false);
		btnTogglePassword.setContentAreaFilled(false);
		btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnTogglePassword.setFocusable(false); // Quan trọng: Không cho nút chiếm focus khi click

		pnFormDangNhap.add(btnTogglePassword);

		// 🔥 CỰC KỲ QUAN TRỌNG: Đưa nút lên lớp trên cùng (Layer 0) để chắc chắn nhận
		// được click
		pnFormDangNhap.setComponentZOrder(btnTogglePassword, 0);

		// Đảm bảo trạng thái ban đầu của txtMatKhau (nếu đã có text thì che đi)
		if (!txtMatKhau.getText().equals("Nhập mật khẩu của bạn")) {
			txtMatKhau.setEchoChar('●');
		}

		// === 3. Xử lý sự kiện click (Logic mới: Không dùng biến phụ) ===
		btnTogglePassword.addActionListener(e -> {
			// Debug: In ra console để biết chắc chắn nút đã được bấm
			System.out.println("Sự kiện click mắt đã chạy!");

			String currentPass = new String(txtMatKhau.getPassword());
			// Nếu là placeholder hoặc rỗng thì bỏ qua
			if (currentPass.equals("Nhập mật khẩu của bạn") || currentPass.isEmpty()) {
				return;
			}

			// Kiểm tra trực tiếp trạng thái của ô mật khẩu thay vì dùng biến isHidden
			if (txtMatKhau.getEchoChar() != (char) 0) {
				// Đang có ký tự che (ẩn) -> Chuyển sang HIỆN
				txtMatKhau.setEchoChar((char) 0);
				btnTogglePassword.setIcon(iconClose);
			} else {
				// Đang không che (hiện) -> Chuyển sang ẨN
				txtMatKhau.setEchoChar('●');
				btnTogglePassword.setIcon(iconOpen);
			}
		});

		JButton btnDangNhap = new PillButton("ĐĂNG NHẬP");
		btnDangNhap.setFont(new Font("Arial", Font.BOLD, 18));
		btnDangNhap.setForeground(Color.WHITE);
		btnDangNhap.setBounds(50, 669, inputWidth, 50);
		btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pnFormDangNhap.add(btnDangNhap);

		JButton btnQuenMK = new JButton("Quên mật khẩu?");
		btnQuenMK.setFont(new Font("Arial", Font.ITALIC, 16));
		btnQuenMK.setForeground(new Color(0xD32F2F));
		btnQuenMK.setBounds(403, 732, 179, 30);
		btnQuenMK.setContentAreaFilled(false);
		btnQuenMK.setBorderPainted(false);
		btnQuenMK.setFocusPainted(false);
		btnQuenMK.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 💡 THAY THẾ LOGIC CŨ BẰNG HÀM XỬ LÝ ĐĂNG NHẬP
		btnDangNhap.addActionListener(e -> xuLyDangNhap());

		// === Thay thế toàn bộ đoạn xử lý btnQuenMK cũ bằng đoạn này ===

		// Thêm sự kiện Hover chuột cho đẹp (giữ lại hiệu ứng cũ)
		btnQuenMK.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnQuenMK.setForeground(new Color(0xB71C1C));
				btnQuenMK.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 16));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnQuenMK.setForeground(new Color(0xD32F2F));
				btnQuenMK.setFont(new Font("Arial", Font.ITALIC, 16));
			}
		});

		// Thêm sự kiện Click để mở Dialog Quên Mật Khẩu
		btnQuenMK.addActionListener(e -> {
			// Mở Dialog QuenMatKhau, truyền 'this' làm cha để dialog hiện ở giữa cửa sổ
			// đăng nhập
			new QuenMatKhau_Dialog(this).setVisible(true);
		});

		pnFormDangNhap.add(btnQuenMK);

		// === Nút test nhanh: Đăng nhập nhanh (Quản lý / Nhân viên) ===
		// JButton btnQuickQL = new JButton("QL test");
		// btnQuickQL.setToolTipText("Đăng nhập nhanh: NV-20250210-0017 / 123456aA@");
		// btnQuickQL.setFont(new Font("Arial", Font.PLAIN, 12));
		// btnQuickQL.setBounds(50, 724, 120, 30);
		// btnQuickQL.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// btnQuickQL.addActionListener(e -> {
		// txtTaiKhoan.setText("NV-20250210-0017");
		// txtMatKhau.setText("123456aA@");
		// // đảm bảo echo char
		// txtMatKhau.setEchoChar('●');
		// xuLyDangNhap();
		// });
		// pnFormDangNhap.add(btnQuickQL);
		//
		// JButton btnQuickNV = new JButton("NV test");
		// btnQuickNV.setToolTipText("Đăng nhập nhanh: NV-20250415-0018 / 123456aA@");
		// btnQuickNV.setFont(new Font("Arial", Font.PLAIN, 12));
		// btnQuickNV.setBounds(190, 724, 120, 30);
		// btnQuickNV.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// btnQuickNV.addActionListener(e -> {
		// txtTaiKhoan.setText("NV-20250415-0018");
		// txtMatKhau.setText("123456aA@");
		// txtMatKhau.setEchoChar('●');
		// xuLyDangNhap();
		// });
		// pnFormDangNhap.add(btnQuickNV);

		return pnFormDangNhap;
	}

	/**
	 * 💡 HÀM XỬ LÝ SỰ KIỆN ĐĂNG NHẬP (Dùng Dao và Session)
	 */
	private void xuLyDangNhap() {
		String tenDangNhap = txtTaiKhoan.getText().trim();
		// Chuyển JPasswordField thành String an toàn
		String matKhau = new String(txtMatKhau.getPassword()).trim();

		// Lấy placeholder
		String placeholderTK = "Nhập tài khoản của bạn";
		String placeholderMK = "Nhập mật khẩu của bạn";

		// 1. Kiểm tra rỗng (hoặc còn placeholder)
		if (tenDangNhap.isEmpty() || tenDangNhap.equals(placeholderTK) || matKhau.isEmpty()
				|| matKhau.equals(placeholderMK)) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập và Mật khẩu hợp lệ.", "Lỗi đăng nhập",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 2. Gọi Server qua Socket để xác thực
		try {
		    ClientService svc = new ClientService();
		    TaiKhoanDTO taiKhoan = svc.login(tenDangNhap, matKhau);
		    if (taiKhoan != null) {
			Session.getInstance().setTaiKhoanDangNhap(taiKhoan);
			boolean isQuanLy = "Quản lý".equals(taiKhoan.getVaiTro());
			JOptionPane.showMessageDialog(this,
				"Đăng nhập thành công!\nXin chào " + taiKhoan.getTenNhanVien() + " ("
					+ (isQuanLy ? "Quản lý" : "Nhân viên") + ")",
				"Thành công", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
			new Main(taiKhoan).setVisible(true);
			return;
		    } else {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc Mật khẩu không đúng.", "Lỗi đăng nhập",
				JOptionPane.ERROR_MESSAGE);
			txtMatKhau.setText("");
			addPlaceholder(txtMatKhau, placeholderMK);
			return;
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		    JOptionPane.showMessageDialog(this, "Không thể kết nối đến Server: " + ex.getMessage(), "Lỗi",
			    JOptionPane.ERROR_MESSAGE);
		    return;
		}
	}

	private void addPlaceholder(JTextField field, String placeholder) {
		field.setText(placeholder);
		field.setForeground(Color.GRAY);

		if (field instanceof JPasswordField) {
			((JPasswordField) field).setEchoChar((char) 0);
		}

		field.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(Color.BLACK);
					if (field instanceof JPasswordField) {
						((JPasswordField) field).setEchoChar('●');
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().isEmpty()) {
					field.setForeground(Color.GRAY);
					field.setText(placeholder);
					if (field instanceof JPasswordField) {
						((JPasswordField) field).setEchoChar((char) 0);
					}
				}
			}
		});
	}

}
