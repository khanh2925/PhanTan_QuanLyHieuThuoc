package presentation.quanly;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import presentation.component.button.PillButton;
import presentation.component.input.PlaceholderSupport;
import presentation.component.border.RoundedBorder;
import dao.iml.ChiTietKhuyenMaiSanPhamDaoImpl;
import dao.iml.KhuyenMaiDaoImpl;
import dao.iml.SanPhamDaoImpl;
import dao.iml.QuyCachDongGoiDaoImpl;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.QuyCachDongGoi;
import entity.SanPham;
import entity.HinhThucKM;
import presentation.dialog.DialogChonSanPhamApDung;

/**
  * GUI Quản lý khuyến mãi - Dùng Entity + DAO thật
  */
@SuppressWarnings("serial")
public class KhuyenMai_GUI extends JPanel implements ActionListener {

	private JPanel pnHeader, pnCenter;
	private JSplitPane splitPane;

	private JTextField txtMaKM, txtTenKM, txtGiaTri, txtDieuKien, txtSoLuong;
	private JDateChooser dateNgayBD, dateNgayKT;
	private JComboBox<String> cboLoaiKM, cboHinhThuc, cboTrangThai;

	private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnChonSP, btnXoaSP;
	private JTextField txtTimKiem;

	private JTable tblKhuyenMai, tblSanPhamApDung;
	private DefaultTableModel modelKhuyenMai, modelSanPhamApDung;

	private KhuyenMaiDaoImpl kmDAO = new KhuyenMaiDaoImpl();
	private ChiTietKhuyenMaiSanPhamDaoImpl ctkmDAO = new ChiTietKhuyenMaiSanPhamDaoImpl();
	private SanPhamDaoImpl spDAO = new SanPhamDaoImpl();
	private QuyCachDongGoiDaoImpl qcDAO = new QuyCachDongGoiDaoImpl();
	private List<KhuyenMai> dsKhuyenMai = new ArrayList<>();

	private DecimalFormat dfNumber = new DecimalFormat("#,###");
	private DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);

	public KhuyenMai_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
		setupKeyboardShortcuts();
		addFocusOnShow();
		loadDataKhuyenMai();
		lamMoiForm();
		capNhatTrangThaiNut();
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
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm KM theo mã, tên chương trình (F1 / Ctrl+F)");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtTimKiem.setBounds(25, 17, 500, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		txtTimKiem.addActionListener(e -> xuLyTimKiem());
		pnHeader.add(txtTimKiem);

		btnTimKiem = new PillButton("<html>" + "<center>" + "TÌM KIẾM<br>" + "<span style='font-size:10px; color:#888888;'>(Enter)</span>" + "</center>" + "</html>");
		btnTimKiem.setBounds(540, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên khuyến mãi</html>");
		btnTimKiem.addActionListener(e -> xuLyTimKiem());
		pnHeader.add(btnTimKiem);
	}

	private void taoPhanCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel pnTopWrapper = new JPanel(new BorderLayout());
		pnTopWrapper.setBackground(Color.WHITE);
		pnTopWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Thông tin khuyến mãi", TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY));

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
		tabbedPane.addTab("Danh sách khuyến mãi", pnTab1);

		JPanel pnTab2 = new JPanel(new BorderLayout());
		pnTab2.setBackground(Color.WHITE);
		taoBangSanPhamApDung(pnTab2);
		tabbedPane.addTab("Sản phẩm áp dụng", pnTab2);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
		splitPane.setDividerLocation(380);
		splitPane.setResizeWeight(0.0);

		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	private void taoFormNhapLieu(JPanel p) {
		int xStart = 50, yStart = 30;
		int hText = 35, wLbl = 120, wTxt = 320, gap = 25;
		int xCol2 = xStart + wLbl + wTxt + 120;

		p.add(createLabel("Mã KM:", xStart, yStart));
		txtMaKM = createTextField(xStart + wLbl, yStart, wTxt);
		txtMaKM.setEditable(false);
		p.add(txtMaKM);

		p.add(createLabel("Tên KM:", xCol2, yStart));
		txtTenKM = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtTenKM, "Nhập tên khuyến mãi");
		p.add(txtTenKM);

		yStart += hText + gap;
		p.add(createLabel("Ngày BĐ:", xStart, yStart));
		dateNgayBD = new JDateChooser();
		dateNgayBD.setDateFormatString("dd/MM/yyyy");
		dateNgayBD.setFont(FONT_TEXT);
		dateNgayBD.setBounds(xStart + wLbl, yStart, wTxt, hText);
		dateNgayBD.setDate(new Date());
		p.add(dateNgayBD);

		p.add(createLabel("Ngày KT:", xCol2, yStart));
		dateNgayKT = new JDateChooser();
		dateNgayKT.setDateFormatString("dd/MM/yyyy");
		dateNgayKT.setFont(FONT_TEXT);
		dateNgayKT.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		dateNgayKT.setDate(new Date());
		p.add(dateNgayKT);

		yStart += hText + gap;
		p.add(createLabel("Loại KM:", xStart, yStart));
		cboLoaiKM = new JComboBox<>(new String[] { "Theo hóa đơn", "Theo sản phẩm" });
		cboLoaiKM.setBounds(xStart + wLbl, yStart, wTxt, hText);
		cboLoaiKM.setFont(FONT_TEXT);
		p.add(cboLoaiKM);

		p.add(createLabel("Hình thức:", xCol2, yStart));
		cboHinhThuc = new JComboBox<>(new String[] { HinhThucKM.GIAM_GIA_PHAN_TRAM.getMoTa(), HinhThucKM.GIAM_GIA_TIEN.getMoTa() });
		cboHinhThuc.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboHinhThuc.setFont(FONT_TEXT);
		p.add(cboHinhThuc);

		yStart += hText + gap;
		p.add(createLabel("Giá trị:", xStart, yStart));
		txtGiaTri = createTextField(xStart + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtGiaTri, "Nhập giá trị");
		p.add(txtGiaTri);

		p.add(createLabel("Điều kiện:", xCol2, yStart));
		txtDieuKien = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtDieuKien, "Nhập điều kiện");
		p.add(txtDieuKien);

		yStart += hText + gap;
		p.add(createLabel("Số lượng:", xStart, yStart));
		txtSoLuong = createTextField(xStart + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtSoLuong, "Nhập số lượng");
		p.add(txtSoLuong);

		p.add(createLabel("Trạng thái:", xCol2, yStart));
		cboTrangThai = new JComboBox<>(new String[] { "Đang hoạt động", "Ngưng hoạt động" });
		cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
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

		int btnW = 140;
		int btnH = 45;

		btnThem = new PillButton("<html>" + "<center>" + "TẠO KM<br>" + "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" + "</center>" + "</html>");
		btnThem.setFont(FONT_BOLD);
		btnThem.setPreferredSize(new Dimension(btnW, btnH));
		btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Tạo khuyến mãi mới</html>");
		btnThem.addActionListener(this);
		gbc.gridy = 0;
		p.add(btnThem, gbc);

		btnSua = new PillButton("<html>" + "<center>" + "CẬP NHẬT<br>" + "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" + "</center>" + "</html>");
		btnSua.setFont(FONT_BOLD);
		btnSua.setPreferredSize(new Dimension(btnW, btnH));
		btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật khuyến mãi đang chọn</html>");
		btnSua.addActionListener(this);
		gbc.gridy = 1;
		p.add(btnSua, gbc);

		btnLamMoi = new PillButton("<html>" + "<center>" + "LÀM MỚI<br>" + "<span style='font-size:10px; color:#888888;'>(F5)</span>" + "</center>" + "</html>");
		btnLamMoi.setFont(FONT_BOLD);
		btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới form nhập liệu</html>");
		btnLamMoi.addActionListener(this);
		gbc.gridy = 2;
		p.add(btnLamMoi, gbc);
	}

	private void taoBangDanhSach(JPanel p) {
		String[] cols = { "STT", "Mã KM", "Tên KM", "Hình thức", "Giá trị", "Bắt đầu", "Kết thúc", "Loại", "Trạng thái" };
		modelKhuyenMai = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblKhuyenMai = setupTable(modelKhuyenMai);
		tblKhuyenMai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doToForm(tblKhuyenMai.getSelectedRow());
				capNhatTrangThaiNut();
			}
		});
		p.add(new JScrollPane(tblKhuyenMai), BorderLayout.CENTER);
	}

	private void taoBangSanPhamApDung(JPanel p) {
		JPanel pnTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnTool.setBackground(Color.WHITE);

		btnChonSP = new PillButton("<html>" + "<center>" + "CHỌN SP<br>" + "<span style='font-size:10px; color:#888888;'>(F7)</span>" + "</center>" + "</html>");
		btnChonSP.setFont(FONT_BOLD);
		btnChonSP.setPreferredSize(new Dimension(140, 45));
		btnChonSP.setToolTipText("<html><b>Phím tắt:</b> F7<br>Mở danh sách chọn sản phẩm áp dụng</html>");
		btnChonSP.addActionListener(this);

		btnXoaSP = new PillButton("<html>" + "<center>" + "XÓA SP<br>" + "<span style='font-size:10px; color:#888888;'>(F8)</span>" + "</center>" + "</html>");
		btnXoaSP.setFont(FONT_BOLD);
		btnXoaSP.setPreferredSize(new Dimension(140, 45));
		btnXoaSP.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xóa sản phẩm đã chọn khỏi khuyến mãi</html>");
		btnXoaSP.addActionListener(this);

		pnTool.add(btnChonSP);
		pnTool.add(btnXoaSP);
		p.add(pnTool, BorderLayout.NORTH);

		String[] cols = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Giá gốc", "Giá KM" };
		modelSanPhamApDung = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblSanPhamApDung = setupTable(modelSanPhamApDung);

		p.add(new JScrollPane(tblSanPhamApDung), BorderLayout.CENTER);
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
		} else if (o.equals(btnChonSP)) {
			// Implementation
		} else if (o.equals(btnXoaSP)) {
			// Implementation
		}
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

	private void loadDataKhuyenMai() {
		dsKhuyenMai = kmDAO.layTatCaKhuyenMai();
		modelKhuyenMai.setRowCount(0);
		int stt = 1;
		for (KhuyenMai km : dsKhuyenMai) {
			modelKhuyenMai.addRow(new Object[] { stt++, km.getMaKM(), km.getTenKM(),
					km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : "", dfNumber.format(km.getGiaTri()),
					dfDate.format(km.getNgayBatDau()), dfDate.format(km.getNgayKetThuc()),
					km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm", km.isDangHoatDong() ? "Đang hoạt động" : "Ngưng hoạt động" });
		}
	}

	private void xuLyTimKiem() {
		String kw = txtTimKiem.getText().trim().toLowerCase();
		modelKhuyenMai.setRowCount(0);
		int stt = 1;
		for (KhuyenMai km : dsKhuyenMai) {
			if (kw.isEmpty() || km.getMaKM().toLowerCase().contains(kw) || km.getTenKM().toLowerCase().contains(kw)) {
				modelKhuyenMai.addRow(new Object[] { stt++, km.getMaKM(), km.getTenKM(),
						km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : "", dfNumber.format(km.getGiaTri()),
						dfDate.format(km.getNgayBatDau()), dfDate.format(km.getNgayKetThuc()),
						km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm", km.isDangHoatDong() ? "Đang hoạt động" : "Ngưng hoạt động" });
			}
		}
	}

	private void doToForm(int row) {
		// Implementation
	}

	private void lamMoiForm() {
		String newMa = kmDAO.taoMaKhuyenMai();
		txtMaKM.setText(newMa);
		txtMaKM.setEditable(false);

		txtTenKM.setText("");
		PlaceholderSupport.addPlaceholder(txtTenKM, "Nhập tên khuyến mãi");
		txtGiaTri.setText("");
		PlaceholderSupport.addPlaceholder(txtGiaTri, "Nhập giá trị");

		txtDieuKien.setText("");
		PlaceholderSupport.addPlaceholder(txtDieuKien, "Nhập điều kiện");

		txtSoLuong.setText("");
		PlaceholderSupport.addPlaceholder(txtSoLuong, "Nhập số lượng");

		dateNgayBD.setDate(new Date());
		dateNgayKT.setDate(new Date());

		cboLoaiKM.setSelectedIndex(0);
		cboHinhThuc.setSelectedIndex(0);
		cboTrangThai.setSelectedIndex(0);
		tblKhuyenMai.clearSelection();
		modelSanPhamApDung.setRowCount(0);
		loadDataKhuyenMai();
		capNhatTrangThaiNut();
		txtTimKiem.requestFocus();
	}

	private void capNhatTrangThaiNut() {
		int row = tblKhuyenMai.getSelectedRow();
		boolean coDongDuocChon = (row != -1);
		btnThem.setEnabled(!coDongDuocChon);
		btnSua.setEnabled(coDongDuocChon);
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
		table.getTableHeader().setBackground(new Color(33, 150, 243));
		table.getTableHeader().setForeground(Color.WHITE);
		return table;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản Lý Khuyến Mãi");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1500, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new KhuyenMai_GUI());
			frame.setVisible(true);
		});
	}
}
