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
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.HinhThucKM;
import entity.SanPham;
import network.ClientService;
import presentation.dialog.DialogChonSanPhamApDung;

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
	private final List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
	private final ClientService svc = new ClientService();
	private final DecimalFormat dfNumber = new DecimalFormat("#,###");
	private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);

	public KhuyenMai_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
		setupKeyboardShortcuts();
		addFocusOnShow();
		lamMoiForm();
		loadDataKhuyenMai();
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
		pnHeader.add(txtTimKiem);
		btnTimKiem = new PillButton("TÌM KIẾM");
		btnTimKiem.setBounds(540, 22, 130, 50);
		btnTimKiem.setFont(FONT_BOLD);
		btnTimKiem.addActionListener(this);
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
		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	private void taoFormNhapLieu(JPanel p) {
		int xStart = 50, yStart = 30, hText = 35, wLbl = 120, wTxt = 320, gap = 25;
		int xCol2 = xStart + wLbl + wTxt + 120;
		p.add(createLabel("Mã KM:", xStart, yStart));
		txtMaKM = createTextField(xStart + wLbl, yStart, wTxt); txtMaKM.setEditable(false); p.add(txtMaKM);
		p.add(createLabel("Tên KM:", xCol2, yStart));
		txtTenKM = createTextField(xCol2 + wLbl, yStart, wTxt); PlaceholderSupport.addPlaceholder(txtTenKM, "Nhập tên khuyến mãi"); p.add(txtTenKM);
		yStart += hText + gap;
		p.add(createLabel("Ngày BĐ:", xStart, yStart));
		dateNgayBD = new JDateChooser(); dateNgayBD.setDateFormatString("dd/MM/yyyy"); dateNgayBD.setBounds(xStart + wLbl, yStart, wTxt, hText); dateNgayBD.setDate(new Date()); p.add(dateNgayBD);
		p.add(createLabel("Ngày KT:", xCol2, yStart));
		dateNgayKT = new JDateChooser(); dateNgayKT.setDateFormatString("dd/MM/yyyy"); dateNgayKT.setBounds(xCol2 + wLbl, yStart, wTxt, hText); dateNgayKT.setDate(new Date()); p.add(dateNgayKT);
		yStart += hText + gap;
		p.add(createLabel("Loại KM:", xStart, yStart));
		cboLoaiKM = new JComboBox<>(new String[]{"Theo hóa đơn", "Theo sản phẩm"}); cboLoaiKM.setBounds(xStart + wLbl, yStart, wTxt, hText); p.add(cboLoaiKM);
		p.add(createLabel("Hình thức:", xCol2, yStart));
		cboHinhThuc = new JComboBox<>(new String[]{HinhThucKM.GIAM_GIA_PHAN_TRAM.getMoTa(), HinhThucKM.GIAM_GIA_TIEN.getMoTa()}); cboHinhThuc.setBounds(xCol2 + wLbl, yStart, wTxt, hText); p.add(cboHinhThuc);
		yStart += hText + gap;
		p.add(createLabel("Giá trị:", xStart, yStart)); txtGiaTri = createTextField(xStart + wLbl, yStart, wTxt); PlaceholderSupport.addPlaceholder(txtGiaTri, "Nhập giá trị"); p.add(txtGiaTri);
		p.add(createLabel("Điều kiện:", xCol2, yStart)); txtDieuKien = createTextField(xCol2 + wLbl, yStart, wTxt); PlaceholderSupport.addPlaceholder(txtDieuKien, "Nhập điều kiện"); p.add(txtDieuKien);
		yStart += hText + gap;
		p.add(createLabel("Số lượng:", xStart, yStart)); txtSoLuong = createTextField(xStart + wLbl, yStart, wTxt); PlaceholderSupport.addPlaceholder(txtSoLuong, "Nhập số lượng"); p.add(txtSoLuong);
		p.add(createLabel("Trạng thái:", xCol2, yStart)); cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Ngưng hoạt động"}); cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText); p.add(cboTrangThai);
	}

	private void taoPanelNutBam(JPanel p) {
		p.setPreferredSize(new Dimension(200, 0)); p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); p.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(); gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
		btnThem = new PillButton("TẠO KM"); btnThem.setFont(FONT_BOLD); btnThem.addActionListener(this); gbc.gridy = 0; p.add(btnThem, gbc);
		btnSua = new PillButton("CẬP NHẬT"); btnSua.setFont(FONT_BOLD); btnSua.addActionListener(this); btnSua.setEnabled(false); gbc.gridy = 1; p.add(btnSua, gbc);
		btnLamMoi = new PillButton("LÀM MỚI"); btnLamMoi.setFont(FONT_BOLD); btnLamMoi.addActionListener(this); gbc.gridy = 2; p.add(btnLamMoi, gbc);
		capNhatNutSP();
	}

	private void taoBangDanhSach(JPanel p) {
		String[] cols = {"STT", "Mã KM", "Tên KM", "Hình thức", "Giá trị", "Bắt đầu", "Kết thúc", "Loại", "Trạng thái"};
		modelKhuyenMai = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
		tblKhuyenMai = setupTable(modelKhuyenMai);
		tblKhuyenMai.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { doToForm(tblKhuyenMai.getSelectedRow()); capNhatTrangThaiNut(); } });
		p.add(new JScrollPane(tblKhuyenMai), BorderLayout.CENTER);
	}

	private void taoBangSanPhamApDung(JPanel p) {
		JPanel pnTool = new JPanel(new FlowLayout(FlowLayout.LEFT)); pnTool.setBackground(Color.WHITE);
		btnChonSP = new PillButton("CHỌN SP"); btnChonSP.setFont(FONT_BOLD); btnChonSP.addActionListener(this);
		btnXoaSP = new PillButton("XÓA SP"); btnXoaSP.setFont(FONT_BOLD); btnXoaSP.addActionListener(this);
		pnTool.add(btnChonSP); pnTool.add(btnXoaSP); p.add(pnTool, BorderLayout.NORTH);
		String[] cols = {"Mã SP", "Tên sản phẩm", "Đơn vị", "Giá gốc", "Giá KM"};
		modelSanPhamApDung = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
		tblSanPhamApDung = setupTable(modelSanPhamApDung);
		p.add(new JScrollPane(tblSanPhamApDung), BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		try {
			if (o == btnThem) themKhuyenMai(); else if (o == btnSua) suaKhuyenMai(); else if (o == btnLamMoi) lamMoiForm(); else if (o == btnChonSP) chonSanPhamApDung(); else if (o == btnXoaSP) xoaSanPhamApDung(); else if (o == btnTimKiem) xuLyTimKiem();
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
	}

	private void setupKeyboardShortcuts() {
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();
		inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
		actionMap.put("focusTimKiem", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { txtTimKiem.requestFocus(); txtTimKiem.selectAll(); }
		});
		inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
		actionMap.put("lamMoi", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { lamMoiForm(); loadDataKhuyenMai(); }
		});
		inputMap.put(KeyStroke.getKeyStroke("control N"), "themKM");
		actionMap.put("themKM", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { try { themKhuyenMai(); } catch (Exception ex) { JOptionPane.showMessageDialog(KhuyenMai_GUI.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
		});
		inputMap.put(KeyStroke.getKeyStroke("control U"), "suaKM");
		actionMap.put("suaKM", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { try { suaKhuyenMai(); } catch (Exception ex) { JOptionPane.showMessageDialog(KhuyenMai_GUI.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); } }
		});
		inputMap.put(KeyStroke.getKeyStroke("F7"), "chonSP");
		actionMap.put("chonSP", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { chonSanPhamApDung(); }
		});
		inputMap.put(KeyStroke.getKeyStroke("F8"), "xoaSP");
		actionMap.put("xoaSP", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) { xoaSanPhamApDung(); }
		});
	}
	private void addFocusOnShow() {
		addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
				SwingUtilities.invokeLater(() -> { txtTimKiem.requestFocusInWindow(); txtTimKiem.selectAll(); });
			}
		});
	}

	private void loadDataKhuyenMai() {
		dsKhuyenMai.clear();
		try {
			for (Object o : svc.getAllKhuyenMai()) if (o instanceof KhuyenMai km) dsKhuyenMai.add(km);
		} catch (Exception ignored) {}
		modelKhuyenMai.setRowCount(0); int stt = 1;
		for (KhuyenMai km : dsKhuyenMai) addKmRow(stt++, km);
	}

	private void loadSanPhamApDung(String maKM) {
		modelSanPhamApDung.setRowCount(0);
		if (maKM == null || maKM.isBlank()) return;
		try {
			for (Object o : svc.getChiTietKhuyenMaiByMaKM(maKM)) {
				if (!(o instanceof ChiTietKhuyenMaiSanPham ct)) continue;
				SanPham sp = ct.getSanPham();
				if (sp != null && sp.getMaSanPham() != null) {
					Object full = svc.getProductByCode(sp.getMaSanPham());
					if (full instanceof SanPham fullSp) sp = fullSp;
				}
				String ten = sp != null ? sp.getTenSanPham() : "";
				String dv = sp != null ? String.valueOf(sp.getKeBanSanPham()) : "";
				double giaGoc = sp != null ? sp.getGiaBan() : 0;
				double giaKm = tinhGiaKhuyenMai(giaGoc);
				modelSanPhamApDung.addRow(new Object[]{ sp != null ? sp.getMaSanPham() : "", ten, dv, dfNumber.format(giaGoc), dfNumber.format(giaKm) });
			}
		} catch (Exception ignored) {}
	}

	private void addKmRow(int stt, KhuyenMai km) {
		modelKhuyenMai.addRow(new Object[]{stt, km.getMaKM(), km.getTenKM(), km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : "", dfNumber.format(km.getGiaTri()), km.getNgayBatDau() != null ? km.getNgayBatDau().format(dfDate) : "", km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(dfDate) : "", km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm", km.isDangHoatDong() ? "Đang hoạt động" : "Ngưng hoạt động"});
	}

	private void xuLyTimKiem() { String kw = txtTimKiem.getText().trim().toLowerCase(); modelKhuyenMai.setRowCount(0); int stt = 1; for (KhuyenMai km : dsKhuyenMai) if (kw.isEmpty() || km.getMaKM().toLowerCase().contains(kw) || km.getTenKM().toLowerCase().contains(kw)) addKmRow(stt++, km); }

	private void doToForm(int row) {
		if (row < 0) return;
		txtMaKM.setText(String.valueOf(modelKhuyenMai.getValueAt(row, 1)));
		txtTenKM.setText(String.valueOf(modelKhuyenMai.getValueAt(row, 2)));
		txtGiaTri.setText(String.valueOf(modelKhuyenMai.getValueAt(row, 4)).replace(",", ""));
		String bd = String.valueOf(modelKhuyenMai.getValueAt(row, 5));
		String kt = String.valueOf(modelKhuyenMai.getValueAt(row, 6));
		dateNgayBD.setDate(parseDate(bd));
		dateNgayKT.setDate(parseDate(kt));
		cboLoaiKM.setSelectedIndex(String.valueOf(modelKhuyenMai.getValueAt(row, 7)).contains("hóa đơn") ? 0 : 1);
		capNhatNutSP();
		cboTrangThai.setSelectedIndex(String.valueOf(modelKhuyenMai.getValueAt(row, 8)).contains("Đang") ? 0 : 1);
		loadSanPhamApDung(txtMaKM.getText().trim());
		btnSua.setEnabled(true);
		capNhatNutSP();
	}

	private void lamMoiForm() {
		try { txtMaKM.setText(svc.taoMaKhuyenMai()); } catch (Exception ex) { txtMaKM.setText("KM-001"); }
		txtTenKM.setText(""); txtGiaTri.setText(""); txtDieuKien.setText(""); txtSoLuong.setText(""); dateNgayBD.setDate(new Date()); dateNgayKT.setDate(new Date()); cboLoaiKM.setSelectedIndex(0); cboHinhThuc.setSelectedIndex(0); cboTrangThai.setSelectedIndex(0); tblKhuyenMai.clearSelection(); modelSanPhamApDung.setRowCount(0); btnSua.setEnabled(false); btnThem.setEnabled(true); capNhatNutSP();
	}

	private void capNhatTrangThaiNut() { boolean chon = tblKhuyenMai.getSelectedRow() != -1; btnThem.setEnabled(!chon); btnSua.setEnabled(chon); capNhatNutSP(); }

	private void capNhatNutSP() {
		boolean laTheoSanPham = cboLoaiKM != null && cboLoaiKM.getSelectedIndex() == 1;
		boolean coKM = txtMaKM != null && !txtMaKM.getText().trim().isEmpty();
		boolean enable = coKM && laTheoSanPham;
		if (btnChonSP != null) btnChonSP.setEnabled(enable);
		if (btnXoaSP != null) btnXoaSP.setEnabled(enable && tblSanPhamApDung != null && tblSanPhamApDung.getSelectedRow() != -1);
	}
	private JLabel createLabel(String text, int x, int y) { JLabel lbl = new JLabel(text); lbl.setFont(FONT_TEXT); lbl.setBounds(x, y, 100, 35); return lbl; }
	private JTextField createTextField(int x, int y, int w) { JTextField txt = new JTextField(); txt.setFont(FONT_TEXT); txt.setBounds(x, y, w, 35); return txt; }
	private JTable setupTable(DefaultTableModel model) { JTable table = new JTable(model); table.setFont(FONT_TEXT); table.setRowHeight(35); table.setSelectionBackground(new Color(0xC8E6C9)); table.setSelectionForeground(Color.BLACK); table.getTableHeader().setFont(FONT_BOLD); table.getTableHeader().setBackground(new Color(33, 150, 243)); table.getTableHeader().setForeground(Color.WHITE); return table; }

	private boolean validData() {
		if (txtTenKM.getText().trim().isEmpty()) return false;
		if (txtGiaTri.getText().trim().isEmpty()) return false;
		return true;
	}
	private LocalDate dateFromChooser(JDateChooser chooser) { Date d = chooser.getDate(); return d == null ? LocalDate.now() : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); }
	private Date parseDate(String s) { try { return Date.from(LocalDate.parse(s, dfDate).atStartOfDay(ZoneId.systemDefault()).toInstant()); } catch (Exception ex) { return new Date(); } }
	private double tinhGiaKhuyenMai(double giaGoc) {
		try {
			double giaTri = Double.parseDouble(txtGiaTri.getText().trim().replace(",", ""));
			return cboHinhThuc.getSelectedIndex() == 0 ? Math.max(0, giaGoc - (giaGoc * giaTri / 100.0)) : Math.max(0, giaGoc - giaTri);
		} catch (Exception ex) {
			return giaGoc;
		}
	}

	private KhuyenMai toEntityFromForm(String ma) {
		KhuyenMai km = new KhuyenMai(); km.setMaKM(ma); km.setTenKM(txtTenKM.getText().trim()); km.setNgayBatDau(dateFromChooser(dateNgayBD)); km.setNgayKetThuc(dateFromChooser(dateNgayKT)); km.setKhuyenMaiHoaDon(cboLoaiKM.getSelectedIndex() == 0); km.setHinhThuc(cboHinhThuc.getSelectedIndex() == 0 ? HinhThucKM.GIAM_GIA_PHAN_TRAM : HinhThucKM.GIAM_GIA_TIEN); km.setGiaTri(Double.parseDouble(txtGiaTri.getText().trim().replace(",", ""))); km.setDieuKienApDungHoaDon(txtDieuKien.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDieuKien.getText().trim().replace(",", ""))); km.setSoLuongKhuyenMai(txtSoLuong.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtSoLuong.getText().trim())); km.setTrangThai(cboTrangThai.getSelectedIndex() == 0); return km;
	}

	private void themKhuyenMai() throws Exception { if (!validData()) return; String ma = svc.taoMaKhuyenMai(); KhuyenMai km = toEntityFromForm(ma); if (svc.createKhuyenMai(km)) { loadDataKhuyenMai(); lamMoiForm(); } }
	private void suaKhuyenMai() throws Exception { int row = tblKhuyenMai.getSelectedRow(); if (row < 0) return; KhuyenMai km = toEntityFromForm(String.valueOf(modelKhuyenMai.getValueAt(row, 1))); if (svc.updateKhuyenMai(km)) { loadDataKhuyenMai(); lamMoiForm(); } }
	private void chonSanPhamApDung() {
		String maKM = txtMaKM.getText().trim();
		if (maKM.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc tạo khuyến mãi trước!");
			return;
		}

		DialogChonSanPhamApDung dialog = new DialogChonSanPhamApDung((JFrame) SwingUtilities.getWindowAncestor(this));
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);

		DefaultTableModel modelDaChon = dialog.getModelDaChon();
		if (modelDaChon == null || modelDaChon.getRowCount() == 0) {
			loadSanPhamApDung(maKM);
			return;
		}

		int daThem = 0;
		int thatBai = 0;
		StringBuilder loi = new StringBuilder();
		for (int i = 0; i < modelDaChon.getRowCount(); i++) {
			String maSP = String.valueOf(modelDaChon.getValueAt(i, 0)).trim();
			if (maSP.isEmpty()) {
				continue;
			}
			boolean ok = svc.createChiTietKhuyenMai(maKM, maSP);
			if (ok) {
				daThem++;
			} else {
				thatBai++;
				loi.append("- ").append(maSP).append("\n");
			}
		}

		loadSanPhamApDung(maKM);
		if (daThem > 0 && thatBai == 0) {
			JOptionPane.showMessageDialog(this, "Đã thêm sản phẩm vào khuyến mãi thành công!");
		} else if (daThem > 0) {
			JOptionPane.showMessageDialog(this,
					"Đã thêm thành công " + daThem + " sản phẩm, nhưng có " + thatBai + " sản phẩm thất bại:\n\n" + loi,
					"Cảnh báo", JOptionPane.WARNING_MESSAGE);
		} else if (thatBai > 0) {
			JOptionPane.showMessageDialog(this,
					"Không thêm được sản phẩm nào vào khuyến mãi.\n\nLỗi:\n" + loi,
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void xoaSanPhamApDung() {
		int row = tblSanPhamApDung.getSelectedRow();
		if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa khỏi khuyến mãi!"); return; }
		String maKM = txtMaKM.getText().trim();
		String maSP = String.valueOf(modelSanPhamApDung.getValueAt(row, 0));
		try {
			if (svc.deleteChiTietKhuyenMai(maKM, maSP)) {
				modelSanPhamApDung.removeRow(row);
			} else {
				JOptionPane.showMessageDialog(this, "Xóa sản phẩm khỏi khuyến mãi thất bại!");
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) { SwingUtilities.invokeLater(() -> { JFrame frame = new JFrame("Quản Lý Khuyến Mãi"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setSize(1500, 850); frame.setLocationRelativeTo(null); frame.setContentPane(new KhuyenMai_GUI()); frame.setVisible(true); }); }
}
