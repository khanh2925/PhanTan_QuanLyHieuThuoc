package presentation;

/**
 * @author Thanh Kha
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Màn hình chính gồm Menu dọc bên trái và card layout chuyển màn hình
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dto.TaiKhoanDTO;
import entity.Session;
import presentation.dialog.ThongTinCaNhan_Dialog;
import presentation.nhanvien.BanHang_GUI;
import presentation.nhanvien.HuyHangNhanVien_GUI;
import presentation.nhanvien.ThongKeNhanVien_GUI;
import presentation.nhanvien.TongQuanNV_GUI;
import presentation.nhanvien.TraHangNhanVien_GUI;
import presentation.quanly.BangGia_GUI;
import presentation.quanly.DonViTinh_QL_GUI;
import presentation.quanly.KhachHang_NV_GUI;
import presentation.quanly.KhuyenMai_GUI;
import presentation.quanly.NhaCungCap_GUI;
import presentation.quanly.NhanVien_QL_GUI;
import presentation.quanly.QLTraHang_GUI;
import presentation.quanly.QL_HuyHang_GUI;
import presentation.quanly.QuanLySanPham_GUI;
import presentation.quanly.QuanLyPhieuNhap_GUI;
import presentation.quanly.ThongKeDoanhThu_GUI;
import presentation.quanly.ThongKeSanPham_GUI;
import presentation.tracuu.TraCuuBangGia_GUI;
import presentation.tracuu.TraCuuDonHang_GUI;
import presentation.tracuu.TraCuuDonTraHang_GUI;
import presentation.tracuu.TraCuuDonViTinh_GUI;
import presentation.tracuu.TraCuuKhachHang_GUI;
import presentation.tracuu.TraCuuKhuyenMai_GUI;
import presentation.tracuu.TraCuuNhaCungCap_GUI;
import presentation.tracuu.TraCuuNhanVien_GUI;
import presentation.tracuu.TraCuuPhieuHuy_GUI;
import presentation.tracuu.TraCuuPhieuNhap_GUI;
import presentation.tracuu.TraCuuSanPham_GUI;
import presentation.tracuu.TraCuuLoSanPham_GUI;
import presentation.trogiup.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.*;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private final Set<String> loadedCards = new HashSet<>();
	private final Map<String, java.util.function.Supplier<JPanel>> cardSuppliers = new HashMap<>();

	private final JPanel cardPanel = new JPanel(new CardLayout());
	private final Map<String, JButton> menuContainers = new LinkedHashMap<>();
	// Lưu container submenu theo key của nút cha
	private final Map<String, JPanel> submenuContainers = new LinkedHashMap<>();
	// Tham chiếu tới panel menu chính để chèn submenu ngay dưới nút cha
	private JPanel menuPanel;

	private int MENU_WIDTH = 250;
	private int MENU_BUTTON_HEIGHT = 60;
	private int LOGO_WIDTH = 100;
	private int MENU_ICON_WIDTH = 33;

	private TaiKhoanDTO nvDangNhap;
	private JLabel lblUserTop;

	// Lưu key của menu đang active để restore sau khi đóng dialog
	private String currentActiveKey;

	// Reference đến Dashboard để có thể refresh khi cần
	private presentation.quanly.TongQuanQuanLy_GUI dashboardQL;
	private presentation.nhanvien.TongQuanNV_GUI dashboardNV;

	public Main(TaiKhoanDTO nv) {
		this.nvDangNhap = nv;
		setTitle("Hiệu thuốc Hòa An - Hệ thống quản lý");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1920, 1080);
		setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH); // Tự
													// động
													// maximize
													// khi
													// chạy
		buildUI();
		hienThongTinNhanVien();
	}

	public Main() {
		this(null);
	}

	// Helper để đăng ký card lazy
	private void registerCard(String key, java.util.function.Supplier<JPanel> supplier) {
		cardSuppliers.put(key, supplier);
	}

	private void buildUI() { // ⬅️ DI CHUYỂN NỘI DUNG từ constructor mặc định vào đây
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(MENU_WIDTH);
		split.setDividerSize(0);
		split.setLeftComponent(createMenu());
		split.setRightComponent(cardPanel);

		add(split);

		boolean isQL = nvDangNhap != null && "Quản lý".equals(nvDangNhap.getVaiTro());

		// Luôn tạo dashboard trước để hiện ngay
		if (isQL) {
			dashboardQL = new presentation.quanly.TongQuanQuanLy_GUI(this);
			// Add trực tiếp dashboard vì nó là default view
			cardPanel.add(dashboardQL, "tongquan");
			loadedCards.add("tongquan");

			// Các màn hình khác: Lazy load
			registerCard("nhaphang", () -> new QuanLyPhieuNhap_GUI());
			registerCard("xuathuy", () -> new QL_HuyHang_GUI());
			registerCard("trahang", () -> new QLTraHang_GUI());
			registerCard("nhacungcap", () -> new NhaCungCap_GUI());
			registerCard("khachhang", () -> new KhachHang_NV_GUI());
			registerCard("khuyenmai", () -> new KhuyenMai_GUI());
			registerCard("nhanvien", () -> new NhanVien_QL_GUI());
			registerCard("huongdan", () -> new HuongDan_GUI(true)); // true = QL

			showCard("tongquan");
		} else {
			dashboardNV = new TongQuanNV_GUI();
			cardPanel.add(dashboardNV, "tongquan");
			loadedCards.add("tongquan");

			registerCard("banhang", () -> new BanHang_GUI());
			registerCard("trahang", () -> new TraHangNhanVien_GUI());
			registerCard("xuathuy", () -> new HuyHangNhanVien_GUI());
			registerCard("khachhang", () -> new KhachHang_NV_GUI());
			registerCard("thongke", () -> new ThongKeNhanVien_GUI(nvDangNhap != null ? nvDangNhap.getMaNhanVien() : ""));
			registerCard("huongdan", () -> new HuongDan_GUI(false)); // false = NV

			// Default view cho NV là Bán hàng -> Phải load ngay hoặc lazy trong showCard
			// showCard sẽ tự handle load nếu chưa có
			showCard("banhang");
		}
	}

	private JPanel createMenu() {
		// Panel chính bên trái, chia thành 2 phần: cuộn và cố định
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(new Color(199, 234, 239));

		// ===== Header: Logo cố định trên cùng =====
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(199, 234, 239));
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		try {
			ImageIcon iconLogo = new ImageIcon(getClass().getResource("/resources/images/Logo.png"));
			Image scaled = iconLogo.getImage().getScaledInstance(LOGO_WIDTH, LOGO_WIDTH, Image.SCALE_SMOOTH);
			JLabel logo = new JLabel(new ImageIcon(scaled));
			logo.setAlignmentX(Component.LEFT_ALIGNMENT);
			headerPanel.add(logo);
		} catch (Exception ex) {
			System.err.println("⚠️ Không tìm thấy Logo: " + ex.getMessage());
		}

		// Panel chứa các nút menu (cuộn được)
		JPanel menuScrollContent = new JPanel();
		menuScrollContent.setLayout(new BoxLayout(menuScrollContent, BoxLayout.Y_AXIS));
		menuScrollContent.setBackground(new Color(199, 234, 239));
		this.menuPanel = menuScrollContent;

		// Thêm menu button
		boolean isQL = nvDangNhap != null && "Quản lý".equals(nvDangNhap.getVaiTro());
		if (isQL) {
			addMenuButton(menuScrollContent, "Tổng quan", "tongquan", "/resources/images/icon_tong_quan.png");
			addMenuButton(menuScrollContent, "Thống kê - Báo cáo", "thongke", "/resources/images/icon_thong_ke.png");
			addSubmenuButton("thongke", "thongkedoanhthu", "Thống kê doanh thu", "/resources/images/icon_thong_ke.png",
					() -> new ThongKeDoanhThu_GUI());
			addSubmenuButton("thongke", "thongkesanpham", "Thống kê sản phẩm", "/resources/images/icon_san_pham.png",
					() -> new ThongKeSanPham_GUI());
			addSubmenuButton("thongke", "thongkenhanvien", "Thống kê nhân viên", "/resources/images/icon_nhan_vien.png",
					() -> new presentation.quanly.ThongKeNhanVien_QL_GUI());

			addMenuButton(menuScrollContent, "Tra cứu", "tracuu", "/resources/images/icon_tra_cuu.png");
			addSubmenuButton("tracuu", "tracuusanpham", "Sản phẩm", "/resources/images/icon_san_pham.png",
					() -> new TraCuuSanPham_GUI());
			addSubmenuButton("tracuu", "tracuudonhang", "Đơn hàng", "/resources/images/icon_don_hang.png",
					() -> new TraCuuDonHang_GUI());
			addSubmenuButton("tracuu", "tracuudontrahang", "Đơn trả hàng", "/resources/images/icon_tra_hang.png",
					() -> new TraCuuDonTraHang_GUI());
			addSubmenuButton("tracuu", "tracuudonhuyhang", "Đơn huỷ hàng", "/resources/images/icon_xuat_huy.png",
					() -> new TraCuuPhieuHuy_GUI());
			addSubmenuButton("tracuu", "tracuudonnhaphang", "Đơn nhập hàng", "/resources/images/icon_nhap_hang.png",
					() -> new TraCuuPhieuNhap_GUI());
			addSubmenuButton("tracuu", "tracuunhanvien", "Nhân viên", "/resources/images/icon_nhan_vien.png",
					() -> new TraCuuNhanVien_GUI());
			addSubmenuButton("tracuu", "tracuukhachhang", "Khách hàng", "/resources/images/icon_khach_hang.png",
					() -> new TraCuuKhachHang_GUI());
			addSubmenuButton("tracuu", "tracuunhacungcap", "Nhà cung cấp", "/resources/images/icon_nha_cung_cap.png",
					() -> new TraCuuNhaCungCap_GUI());
			addSubmenuButton("tracuu", "tracuukhuyenmai", "Khuyến mãi", "/resources/images/icon_khuyen_mai.png",
					() -> new TraCuuKhuyenMai_GUI());
			addSubmenuButton("tracuu", "tracuudonvitinh", "Đơn vị tính", "/resources/images/icon_don_vi_tinh.png",
					() -> new TraCuuDonViTinh_GUI());
			addSubmenuButton("tracuu", "tracuubanggia", "Bảng giá", "/resources/images/icon_bang_gia.png",
					() -> new TraCuuBangGia_GUI());
			addSubmenuButton("tracuu", "tracuulo", "Lô sản phẩm", "/resources/images/icon_san_pham.png",
					() -> new TraCuuLoSanPham_GUI());

			addMenuButton(menuScrollContent, "Nhập hàng", "nhaphang", "/resources/images/icon_nhap_hang.png");
			addMenuButton(menuScrollContent, "Quản lý xuất huỷ", "xuathuy", "/resources/images/icon_xuat_huy.png");
			addMenuButton(menuScrollContent, "Quản lý trả hàng", "trahang", "/resources/images/icon_tra_hang.png");

			addMenuButton(menuScrollContent, "Quản lý sản phẩm", "sanpham", "/resources/images/icon_san_pham.png");
			addSubmenuButton("sanpham", "danhsachsanpham", "Danh sách sản phẩm", "/resources/images/icon_danh_sach.png",
					() -> new QuanLySanPham_GUI());
			addSubmenuButton("sanpham", "donvitinh", "Đơn vị tính", "/resources/images/icon_don_vi_tinh.png",
					() -> new DonViTinh_QL_GUI());
			addSubmenuButton("sanpham", "banggia", "Bảng giá", "/resources/images/icon_bang_gia.png",
					() -> new BangGia_GUI());

			addMenuButton(menuScrollContent, "Quản lý nhà cung cấp", "nhacungcap",
					"/resources/images/icon_nha_cung_cap.png");
			addMenuButton(menuScrollContent, "Quản lý khách hàng", "khachhang",
					"/resources/images/icon_khach_hang.png");
			addMenuButton(menuScrollContent, "Quản lý khuyến mãi", "khuyenmai",
					"/resources/images/icon_khuyen_mai.png");
			addMenuButton(menuScrollContent, "Quản lý nhân viên", "nhanvien", "/resources/images/icon_nhan_vien.png");
			addMenuButton(menuScrollContent, "Hướng dẫn", "huongdan", "/resources/images/icon_huong_dan.png");
			addGioiThieuButton(menuScrollContent);

			menuScrollContent.add(Box.createVerticalGlue());
		} else {
			addMenuButton(menuScrollContent, "Tổng quan", "tongquan", "/resources/images/icon_tong_quan.png");
			addMenuButton(menuScrollContent, "Bán hàng", "banhang", "/resources/images/icon_ban_hang.png");
			addMenuButton(menuScrollContent, "Trả hàng", "trahang", "/resources/images/icon_tra_hang.png");
			addMenuButton(menuScrollContent, "Xuất huỷ", "xuathuy", "/resources/images/icon_xuat_huy.png");

			addMenuButton(menuScrollContent, "Tra cứu", "tracuu", "/resources/images/icon_tra_cuu.png");
			addSubmenuButton("tracuu", "tracuudonhang", "Đơn hàng", "/resources/images/icon_don_hang.png",
					() -> new TraCuuDonHang_GUI());
			addSubmenuButton("tracuu", "tracuudontrahang", "Đơn trả hàng", "/resources/images/icon_tra_hang.png",
					() -> new TraCuuDonTraHang_GUI());
			addSubmenuButton("tracuu", "tracuudonhuyhang", "Đơn huỷ hàng", "/resources/images/icon_xuat_huy.png",
					() -> new TraCuuPhieuHuy_GUI());
			addSubmenuButton("tracuu", "tracuusanpham", "Sản phẩm", "/resources/images/icon_san_pham.png",
					() -> new TraCuuSanPham_GUI());
			addSubmenuButton("tracuu", "tracuukhuyenmai", "Khuyến mãi", "/resources/images/icon_khuyen_mai.png",
					() -> new TraCuuKhuyenMai_GUI());
			addSubmenuButton("tracuu", "tracuukhachhang", "Khách hàng", "/resources/images/icon_khach_hang.png",
					() -> new TraCuuKhachHang_GUI());
			addSubmenuButton("tracuu", "tracuulo", "Lô sản phẩm", "/resources/images/icon_san_pham.png",
					() -> new TraCuuLoSanPham_GUI());

			addMenuButton(menuScrollContent, "Quản lý khách hàng", "khachhang",
					"/resources/images/icon_khach_hang.png");
			addMenuButton(menuScrollContent, "Thống kê cá nhân", "thongke", "/resources/images/icon_thong_ke.png");
			addMenuButton(menuScrollContent, "Hướng dẫn", "huongdan", "/resources/images/icon_huong_dan.png");
			addGioiThieuButton(menuScrollContent);

			menuScrollContent.add(Box.createVerticalGlue());
		}

		// ScrollPane cho phần menu chính (cuộn mượt và hiện đại)
		JScrollPane scrollPane = new JScrollPane(menuScrollContent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(new Color(199, 234, 239));

		// Tùy chỉnh scrollbar hiện đại
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.getVerticalScrollBar().setOpaque(false);
		scrollPane.setOpaque(false);

		// ==== Tùy biến giao diện thanh cuộn ====
		scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			private final Dimension d = new Dimension();

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(d);
				button.setMinimumSize(d);
				button.setMaximumSize(d);
				return button;
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
				// Ẩn phần nền track để thanh cuộn "nổi" hơn
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setComposite(AlphaComposite.SrcOver.derive(0f)); // hoàn toàn trong suốt
				g2.dispose();
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
				if (!c.isEnabled())
					return;
				Graphics2D g2 = (Graphics2D) g.create();

				// Màu thumb: xám trong suốt + bo tròn + hiệu ứng hover
				Color base = new Color(80, 80, 80, 80);
				Color hover = new Color(80, 80, 80, 130);

				if (isThumbRollover()) {
					g2.setColor(hover);
				} else {
					g2.setColor(base);
				}

				g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
				g2.dispose();
			}

			@Override
			protected Dimension getMinimumThumbSize() {
				return new Dimension(8, 40); // mảnh hơn mặc định
			}

			@Override
			protected Dimension getMaximumThumbSize() {
				return new Dimension(8, 9999);
			}
		});

		// Panel dưới cùng (không cuộn)
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setBackground(new Color(199, 234, 239));
		bottomPanel.setBorder(new EmptyBorder(10, 8, 10, 8));

		lblUserTop = new JLabel("Chưa đăng nhập");
		lblUserTop.setFont(new Font("SansSerif", Font.BOLD, 14));
		lblUserTop.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblUserTop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblUserTop.setToolTipText("Click để xem/chỉnh sửa thông tin cá nhân");
		lblUserTop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				moThongTinCaNhan();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblUserTop.setText(
						"<html><u>" + nvDangNhap.getMaNhanVien() + " - " + nvDangNhap.getTenNhanVien() + "</u></html>");  // TaiKhoanDTO
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hienThongTinNhanVien();
			}
		});
		bottomPanel.add(lblUserTop);
		bottomPanel.add(Box.createVerticalStrut(8));

		JButton btnLogout = new JButton("Đăng xuất");
		btnLogout.setFocusPainted(false);
		btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
		btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnLogout.setBackground(new Color(199, 234, 239)); // Màu
															// nền
															// mặc
															// định
															// giống
															// sidebar
		btnLogout.setBorder(null);
		btnLogout.setOpaque(true); // Quan trọng: đảm bảo hiển thị background đúng
		btnLogout.setContentAreaFilled(true); // Cho phép vẽ nền
		ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/resources/images/icon_dang_xuat.png"));
		Image scaledLogout = logoutIcon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH,
				Image.SCALE_SMOOTH);
		btnLogout.setIcon(new ImageIcon(scaledLogout));
		btnLogout.addActionListener(e -> onLogout());

		// Thêm hiệu ứng hover để tránh lỗi hiển thị ảnh khi di chuột
		btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btnLogout.setBackground(new Color(180, 220, 225)); // Màu
																	// hover
																	// sáng
																	// hơn
				btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btnLogout.setBackground(new Color(199, 234, 239)); // Màu
																	// gốc
				btnLogout.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		bottomPanel.add(btnLogout);

		mainPanel.add(headerPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		// Giữ nguyên kích thước
		mainPanel.setPreferredSize(new Dimension(MENU_WIDTH, getHeight()));
		return mainPanel;
	}

	private void addMenuButton(JPanel menu, String text, String key, String iconPath) {
		JButton btn = new JButton(text);
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setBackground(new Color(0, 0, 0, 0));
		btn.setBorder(null);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		// Thêm icon nhỏ phía trước
		// ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
		ImageIcon icon = null;
		if (iconPath == null || iconPath.trim().isEmpty()) {
			System.err.println("⚠️ [Main] Icon path is null or empty for menu: " + text);
		} else {
			URL url = getClass().getResource(iconPath);
			if (url == null) {
				System.err.println("❌ [Main] Icon not found for menu: " + text + " | Path: " + iconPath);
				try {
					String base = getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
					System.err.println("🔍 [Main] Base classpath: " + base);
				} catch (Exception ex) {
					System.err.println("⚠️ [Main] Cannot determine base classpath: " + ex.getMessage());
				}
			} else {
				icon = new ImageIcon(url);
				System.out.println("✅ [Main] Loaded icon: " + iconPath + " for menu: " + text);
			}
		}
		// Scale kích thước icon
		Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH, Image.SCALE_SMOOTH);
		btn.setIcon(new ImageIcon(scaledIcon));
		btn.addActionListener(e -> showCard(key));

		if ("logout".equals(key)) {
			btn.addActionListener(e -> onLogout()); // gọi hàm xử lý đăng xuất
		} else {
			btn.addActionListener(e -> showCard(key));
			menuContainers.put(key, btn); // chỉ lưu các nút có card
		}
		menu.add(btn);
		menuContainers.put(key, btn);
	}

	private void onLogout() {
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất không?", "Đăng xuất",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			dispose();
			new DangNhap_GUI().setVisible(true);
		}
	}

	/**
	 * Thêm nút Giới thiệu - khi click sẽ tô màu, hiện dialog, rồi restore màu menu
	 * trước đó
	 */
	private void addGioiThieuButton(JPanel menu) {
		JButton btn = new JButton("Giới thiệu");
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setBackground(new Color(0, 0, 0, 0));
		btn.setBorder(null);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));

		// Thêm icon
		ImageIcon icon = null;
		String iconPath = "/resources/images/icon_gioi_thieu.png";
		java.net.URL url = getClass().getResource(iconPath);
		if (url != null) {
			icon = new ImageIcon(url);
			Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH, Image.SCALE_SMOOTH);
			btn.setIcon(new ImageIcon(scaledIcon));
		}

		btn.addActionListener(e -> {
			// Lưu menu đang active để restore sau
			String previousActiveKey = currentActiveKey;

			// Tô màu nút Giới thiệu
			menuContainers.forEach((k, b) -> {
				b.setBackground(new Color(0, 0, 0, 0));
				b.setForeground(Color.BLACK);
				b.setOpaque(false);
			});
			btn.setBackground(new Color(0x1E9086));
			btn.setForeground(Color.WHITE);
			btn.setOpaque(true);

			// Ẩn tất cả submenu
			submenuContainers.forEach((k2, p) -> p.setVisible(false));

			// Hiện dialog Giới thiệu
			GioiThieu_GUI.moGioiThieu();

			// Sau khi đóng dialog, reset màu nút Giới thiệu và restore menu trước đó
			btn.setBackground(new Color(0, 0, 0, 0));
			btn.setForeground(Color.BLACK);
			btn.setOpaque(false);

			// Restore màu cho menu trước đó
			if (previousActiveKey != null && menuContainers.containsKey(previousActiveKey)) {
				showCard(previousActiveKey);
			}
		});

		menu.add(btn);
		menuContainers.put("gioithieu", btn);
	}

	private void showCard(String key) {
		// LAZY LOAD: Nếu chưa load thì load ngay
		if (!loadedCards.contains(key) && cardSuppliers.containsKey(key)) {
			JPanel p = cardSuppliers.get(key).get();
			// Nếu supplier trả về null (ví dụ: giới thiệu mở dialog riêng), không add vào
			// cardPanel
			if (p == null) {
				return;
			}
			cardPanel.add(p, key);
			loadedCards.add(key);
		}

		// Refresh dashboard nếu đang chuyển đến tổng quan
		if ("tongquan".equals(key)) {
			if (dashboardQL != null) {
				dashboardQL.refreshDashboard();
			}
			if (dashboardNV != null) {

				dashboardNV.refreshDashboard();
			}
		}

		((CardLayout) cardPanel.getLayout()).show(cardPanel, key);

		// Reset màu tất cả
		menuContainers.forEach((k, b) -> {
			b.setBackground(new Color(0, 0, 0, 0));
			b.setForeground(Color.BLACK);
			b.setOpaque(false);
		});

		// Tô màu cho button đang active
		JButton activeBtn = menuContainers.get(key);
		if (activeBtn == null)
			return;

		// Kiểm tra xem có phải là submenu
		boolean isSubmenu = submenuContainers.values().stream()
				.anyMatch(panel -> Arrays.asList(panel.getComponents()).contains(activeBtn));

		if (isSubmenu) {
			// submenu active
			activeBtn.setBackground(new Color(0x0E736A));
			activeBtn.setForeground(Color.WHITE);
			activeBtn.setOpaque(true);

			// tô màu cha + chỉ giữ mở submenu của cha, ẩn các submenu khác
			final String[] parentKeyHolder = { null };
			submenuContainers.forEach((parentKey, panel) -> {
				if (Arrays.asList(panel.getComponents()).contains(activeBtn)) {
					JButton parentBtn = menuContainers.get(parentKey);
					if (parentBtn != null) {
						parentBtn.setBackground(new Color(0x1E9086));
						parentBtn.setForeground(Color.WHITE);
						parentBtn.setOpaque(true);
					}
					parentKeyHolder[0] = parentKey;
				}
			});
			// Ẩn mọi submenu KHÁC, chỉ giữ mở của cha hiện tại
			final String keepKey = parentKeyHolder[0];
			submenuContainers.forEach((k2, p) -> p.setVisible(k2.equals(keepKey)));
		} else {
			// Là menu cha — tô màu
			activeBtn.setBackground(new Color(0x1E9086));
			activeBtn.setForeground(Color.WHITE);
			activeBtn.setOpaque(true);

			JPanel sub = submenuContainers.get(key);
			if (sub != null && sub.getComponentCount() > 0 && sub.getComponent(0) instanceof JButton) {
				// Menu cha có submenu - tô màu submenu đầu tiên
				JButton firstSub = (JButton) sub.getComponent(0);
				firstSub.setBackground(new Color(0x0E736A));
				firstSub.setForeground(Color.WHITE);
				firstSub.setOpaque(true);

				// Hiển thị card đầu tiên của submenu
				String firstKey = menuContainers.entrySet().stream().filter(entry -> entry.getValue() == firstSub)
						.map(Map.Entry::getKey).findFirst().orElse(null);
				if (firstKey != null && !loadedCards.contains(firstKey) && cardSuppliers.containsKey(firstKey)) {
					JPanel p = cardSuppliers.get(firstKey).get();
					if (p != null) {
						cardPanel.add(p, firstKey);
						loadedCards.add(firstKey);
					}
				}
				if (firstKey != null) {
					((CardLayout) cardPanel.getLayout()).show(cardPanel, firstKey);
					currentActiveKey = firstKey;
				}

				// Khi click sang menu cha khác: Ẩn toàn bộ submenu KHÔNG thuộc menu này
				submenuContainers.forEach((k2, p) -> {
					if (!k2.equals(key))
						p.setVisible(false);
				});
			} else {
				// Menu cha không có submenu
				submenuContainers.forEach((k2, p) -> p.setVisible(false));
			}
		}
		currentActiveKey = key;
	}

	/**
	 * Method public để chuyển card từ bên ngoài (ví dụ: từ Dashboard)
	 * 
	 * @param key Tên card cần chuyển đến
	 */
	public void chuyenDenCard(String key) {
		showCard(key);
	}

	public JPanel createSubmenu(String parentKey) {
		if (menuPanel == null) {
			throw new IllegalStateException("menuPanel chưa được khởi tạo. Hãy gán this.menuPanel trong createMenu().");
		}
		if (!menuContainers.containsKey(parentKey)) {
			throw new IllegalArgumentException("parentKey không tồn tại trong menu: " + parentKey);
		}
		// Nếu đã có thì trả về luôn
		if (submenuContainers.containsKey(parentKey)) {
			return submenuContainers.get(parentKey);
		}

		JPanel sub = new JPanel();
		sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
		sub.setOpaque(false);
		// thụt vào 1 chút
		sub.setBorder(new EmptyBorder(0, 16, 8, 0));
		sub.setVisible(false); // đóng mặc định

		submenuContainers.put(parentKey, sub);

		// Chèn ngay sau nút cha trong menuPanel
		JButton parentBtn = menuContainers.get(parentKey);
		int insertIdx = -1;
		for (int i = 0; i < menuPanel.getComponentCount(); i++) {
			if (menuPanel.getComponent(i) == parentBtn) {
				insertIdx = i + 1;
				break;
			}
		}
		if (insertIdx >= 0)
			menuPanel.add(sub, insertIdx);
		else
			menuPanel.add(sub); // fallback

		// Đảm bảo nút cha bấm sẽ toggle đóng/mở khối submenu
		ensureParentToggle(parentKey);

		menuPanel.revalidate();
		menuPanel.repaint();
		return sub;
	}

	/*
	 * Thêm 1 nút submenu vào dưới nút cha, đồng thời đăng ký 1 card mới để show.
	 */
	public void addSubmenuButton(String parentKey, String subKey, String text, String iconPath,
			java.util.function.Supplier<JPanel> contentSupplier) {
		JPanel subContainer = submenuContainers.get(parentKey);
		if (subContainer == null) {
			subContainer = createSubmenu(parentKey);
		}
		if (contentSupplier != null) {
			// Đăng ký card nội dung lazy
			registerCard(subKey, contentSupplier);
		}

		JButton btn = new JButton(text);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		// kích thước "nhỏ" hơn button chính 1 chút
		btn.setPreferredSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		// thụt vào sâu hơn các nút cha
		btn.setBorder(new EmptyBorder(4, 24, 4, 8));

		// Thêm icon nhỏ phía trước (có kiểm tra null và log)
		ImageIcon icon = null;
		if (iconPath == null || iconPath.isBlank()) {
			System.err.println("iconPath bị null hoặc rỗng khi tạo icon!");
		} else {
			URL url = getClass().getResource(iconPath);

			if (url == null) {
				System.err.println("Không tìm thấy resource icon tại: " + iconPath);
			} else {
				icon = new ImageIcon(url);
				// Scale kích thước icon
				Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH - 7, MENU_ICON_WIDTH - 7,
						Image.SCALE_SMOOTH);
				icon = new ImageIcon(scaledIcon);
				btn.setIcon(new ImageIcon(scaledIcon));
			}
		}

		btn.addActionListener(e -> showCard(subKey));

		subContainer.add(btn);
		menuContainers.put(subKey, btn);

		menuPanel.revalidate();
		menuPanel.repaint();
	}

	/*
	 * Gắn toggle đóng/mở container submenu khi bấm nút cha.
	 */
	private void ensureParentToggle(String parentKey) {
		JButton parent = menuContainers.get(parentKey);
		if (parent == null)
			return;
		// Gắn cờ boolean để biết nút này đã gắn hành vi toggle submenu, nếu đã là true
		// thì thoát ngay
		// Đảm bảo chỉ gắn listener 1 lần
		if (Boolean.TRUE.equals(parent.getClientProperty("submenuBound")))
			return;
		parent.putClientProperty("submenuBound", Boolean.TRUE);

		parent.addActionListener(e -> {
			JPanel sub = submenuContainers.get(parentKey);
			if (sub != null) {
				boolean wasVisible = sub.isVisible();
				sub.setVisible(!wasVisible);
				menuPanel.revalidate();
				menuPanel.repaint();
				// Khi submenu mới mở, tự show card đầu tiên
				if (!wasVisible && sub.getComponentCount() > 0 && sub.getComponent(0) instanceof JButton) {
					JButton firstSub = (JButton) sub.getComponent(0);
					// Lấy key card từ map
					String firstKey = menuContainers.entrySet().stream().filter(entry -> entry.getValue() == firstSub)
							.map(Map.Entry::getKey).findFirst().orElse(null);
					if (firstKey != null) {
						showCard(firstKey);
					}
				}
			}
		});
	}

	private void hienThongTinNhanVien() {
		if (lblUserTop == null)
			return;
		if (nvDangNhap == null) {
			lblUserTop.setText("Chưa đăng nhập");
			return;
		}
		lblUserTop.setText(nvDangNhap.getMaNhanVien() + " - " + nvDangNhap.getTenNhanVien());
	}

	/**
	 * Overload: Thêm nút submenu với hành động tùy chỉnh thay vì JPanel
	 */
	public void addSubmenuButton(String parentKey, String subKey, String text, String iconPath, Runnable action) {
		JPanel subContainer = submenuContainers.get(parentKey);
		if (subContainer == null) {
			subContainer = createSubmenu(parentKey);
		}

		JButton btn = new JButton(text);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setPreferredSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setBorder(new EmptyBorder(4, 24, 4, 8));

		// Thêm icon
		ImageIcon icon = null;
		if (iconPath != null && !iconPath.isBlank()) {
			URL url = getClass().getResource(iconPath);
			if (url != null) {
				icon = new ImageIcon(url);
				Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH - 7, MENU_ICON_WIDTH - 7,
						Image.SCALE_SMOOTH);
				btn.setIcon(new ImageIcon(scaledIcon));
			}
		}

		// Gắn action tùy chỉnh
		btn.addActionListener(e -> {
			if (action != null) {
				action.run();
			}
		});

		subContainer.add(btn);
		menuContainers.put(subKey, btn);

		menuPanel.revalidate();
		menuPanel.repaint();
	}

	/**
	 * Mở dialog thông tin cá nhân để xem/chỉnh sửa thông tin nhân viên đang đăng
	 * nhập
	 */
	private void moThongTinCaNhan() {
		if (nvDangNhap == null) {
			JOptionPane.showMessageDialog(this, "Chưa đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ThongTinCaNhan_Dialog dialog = new ThongTinCaNhan_Dialog(this, () -> {
			TaiKhoanDTO updated = Session.getInstance().getTaiKhoanDangNhap();
			if (updated != null) nvDangNhap = updated;
			hienThongTinNhanVien();
		});
		dialog.setVisible(true);
	}
}
