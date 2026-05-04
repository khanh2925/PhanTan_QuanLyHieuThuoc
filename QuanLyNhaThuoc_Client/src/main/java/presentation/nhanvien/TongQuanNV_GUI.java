package presentation.nhanvien;

/**
 * Mô tả: Dashboard tổng quan cho nghiệp vụ quản lý hiệu thuốc
 * - 6 KPI Cards (2 hàng x 3 cột):
 *   + Lợi nhuận của ngày hiện tại
 *   + Số đơn hàng trong ngày hiện tại
 *   + Số SP đã bán hiện tại
 *   + Số lô sản phẩm sắt hết hạn
 *   + Số Phiếu hủy đã tạo
 *   + Số Phiếu trả đã tạo
 * - Biểu đồ đường hiển thị số lô SP tới hạn cần hủy
 * - Tích hợp DAO để lấy dữ liệu thực từ database
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Map;

import presentation.component.border.RoundedBorder;
import presentation.component.chart.BieuDoCotJFreeChart;
import presentation.component.chart.DuLieuBieuDoCot;
import entity.LoaiSanPham;
import entity.Session;
import network.ClientService;

public class TongQuanNV_GUI extends JPanel implements ActionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1461265675080059356L;
	private JPanel pnCenter;
	private JPanel pnHeader;

	// KPIs Top Row (2 hàng x 3 cột = 5 thẻ + 1 ô trống)
	private JTextField txtSoDon;
	private JTextField txtTongTienDaBan;
	private JTextField txtLoSPDaHetHan;
	private JTextField txtLoSPSapHetHan;
	private JTextField txtSoPhieuHuyDaTao;
	private JTextField txtSoPhieuTraDaTao;

	// Panel chứa Biểu đồ
	private JPanel pnChartArea;
	private BieuDoCotJFreeChart bieuDoCot;

	private ClientService svc;

	// Main GUI reference để chuyển card
	@SuppressWarnings("unused")
	private presentation.Main mainGUI;

	private Color mauChu = new Color(0, 51, 102);
	private String maNV = Session.getInstance().getTaiKhoanDangNhap() != null
			? Session.getInstance().getTaiKhoanDangNhap().getMaNhanVien()
			: "";
	// Formatter
	private DecimalFormat formatter = new DecimalFormat("#,###");

	/**
	 * Constructor với Main reference để hỗ trợ chuyển tab
	 * 
	 * @param mainGUI Tham chiếu đến Main
	 */
	public TongQuanNV_GUI(presentation.Main mainGUI) {
		this.mainGUI = mainGUI;
		this.setPreferredSize(new Dimension(1537, 850));

		svc = new ClientService();

		initialize();
		// Load dữ liệu thật từ database
		loadRealData();
		loadChartLoaiHSD();
	}

	/**
	 * Constructor mặc định (dùng cho test)
	 */
	public TongQuanNV_GUI() {
		this(null);
	}

	
	private void initialize() {
		setLayout(new BorderLayout());

		// ===== HEADER (NORTH) =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 80));
		pnHeader.setBackground(new Color(227, 242, 245));
		add(pnHeader, BorderLayout.NORTH);
		pnHeader.setLayout(new BorderLayout(0, 0));

		JLabel lblTongQuan = new JLabel("Tổng Quan");
		lblTongQuan.setHorizontalAlignment(SwingConstants.CENTER);
		lblTongQuan.setForeground(new Color(0, 51, 102));
		lblTongQuan.setFont(new Font("Segoe UI", Font.BOLD, 36));
		pnHeader.add(lblTongQuan);
		
		pnCenter = new JPanel();
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		pnCenter.setBackground(Color.WHITE);
		add(pnCenter, BorderLayout.CENTER);
		pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.Y_AXIS));
		
		createKPIpn();
		createChart();
	}

	// --- KHU VỰC KPI ---
	private void createKPIpn() {

		JPanel pnlKPIs = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 hàng, 3 cột
		pnlKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
		pnlKPIs.setOpaque(false);

		// Hàng 1
		// Thẻ 1: số hóa đơn hôm nay
		txtSoDon = new JTextField();
		pnlKPIs.add(taoTheKPI("Số đơn hôm nay", mauChu, txtSoDon, null, null));

		// Thẻ 2: số tiền bán sp hôm nay
		txtTongTienDaBan = new JTextField();
		pnlKPIs.add(taoTheKPI("Tổng tiền hóa đơn", mauChu, txtTongTienDaBan, null, null));

		// Thẻ 3: Số phiếu trả đã tạo hôm nay
		txtSoPhieuTraDaTao = new JTextField();
		pnlKPIs.add(taoTheKPI("Số phiếu trả đã tạo hôm nay", mauChu, txtSoPhieuTraDaTao, null, null));
		

		// Hàng 2
		// Thẻ 4: Lô SP sắp hết hạn
		txtLoSPSapHetHan = new JTextField();
		pnlKPIs.add(taoTheKPI("Lô SP sắp hết hạn", mauChu, txtLoSPSapHetHan, null, null));
		
		// Thẻ 5: Lô SP đã hết hạn
		txtLoSPDaHetHan = new JTextField();
		pnlKPIs.add(taoTheKPI("Số Lô SP hết hạn(vẫn còn hàng)", mauChu, txtLoSPDaHetHan, null, null));

		
		// Thẻ 6: Đơn hủy cần duyệt (có thể click) - Border đỏ
		txtSoPhieuHuyDaTao = new JTextField();
		pnlKPIs.add(taoTheKPI("Số phiếu hủy đã tạo hôm nay", mauChu, txtSoPhieuHuyDaTao, null, null));

		
		

		pnCenter.add(pnlKPIs);
		pnCenter.add(Box.createVerticalStrut(40));
	}
	
	/**
	 * Phương thức helper tạo thẻ KPI - Chức năng: chỉ thiết lập giao diện cho
	 * JTextField đã được tạo.
	 */
	private JPanel taoTheKPI(String title, Color mauChu, JTextField textField, String actionType, Color mauBorder) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(227, 242, 245));
		panel.setBorder(new RoundedBorder(15));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setPreferredSize(new Dimension(100, 100));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblTitle.setForeground(mauChu);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblTitle.setBorder(new EmptyBorder(10, 10, 0, 10));

		// Setup JTextField đã được truyền vào
		textField.setColumns(10);
		textField.setOpaque(false);
		textField.setBorder(null);
		textField.setForeground(mauChu);
		textField.setFont(new Font("Segoe UI", Font.BOLD, 40));
		textField.setEditable(false);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		textField.setBorder(new EmptyBorder(5, 10, 10, 10));

		panel.add(lblTitle);
		panel.add(textField);

		return panel;
	}
	
	// ===== Biểu đồ =====
	private void createChart() {		


		pnChartArea = new JPanel();
		pnChartArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 51, 102)),
				"Biểu đồ số lượng lô SP gần hết hạn", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 20), mauChu));
		pnChartArea.setBackground(Color.WHITE);
		pnChartArea.setLayout(new BorderLayout());
		pnChartArea.setPreferredSize(new Dimension(0, 500));
		pnChartArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

		bieuDoCot = new BieuDoCotJFreeChart();
		bieuDoCot.setTieuDeBieuDo("Số lô gần hết hạn sử dụng");
		bieuDoCot.setTieuDeTrucX("Loại sản phẩm");
		bieuDoCot.setTieuDeTrucY("Số lô");
		
		pnChartArea.removeAll();
		pnChartArea.setLayout(new BorderLayout());
		pnChartArea.add(bieuDoCot, BorderLayout.CENTER);
		pnCenter.add(pnChartArea);
		pnCenter.add(Box.createVerticalStrut(10));

		pnChartArea.revalidate();
		pnChartArea.repaint();
	}

	// biểu đố hiện thị số lô tới hạn cần hủy theo loaisanpham
	private void loadChartLoaiHSD() {
		bieuDoCot.xoaToanBoDuLieu();

		Map<LoaiSanPham, Integer> data = null;
		try {
			java.util.Map<?, ?> raw = svc.getExpiredLotCountByCategory();
			if (raw != null && !raw.isEmpty()) {
				data = new java.util.LinkedHashMap<>();
				for (java.util.Map.Entry<?, ?> entry : raw.entrySet()) {
					if (entry.getKey() instanceof LoaiSanPham && entry.getValue() instanceof Number) {
						data.put((LoaiSanPham) entry.getKey(), ((Number) entry.getValue()).intValue());
					}
				}
			}
		} catch (Exception ex) {
			// ignore and fallback
		}
		if (data == null || data.isEmpty()) {
			try {
				Object raw = svc.getExpiredLotCountByCategory();
				if (raw instanceof Map<?, ?> map && !map.isEmpty()) {
					data = new java.util.LinkedHashMap<>();
					for (Map.Entry<?, ?> entry : map.entrySet()) {
						if (entry.getKey() instanceof LoaiSanPham && entry.getValue() instanceof Number) {
							data.put((LoaiSanPham) entry.getKey(), ((Number) entry.getValue()).intValue());
						}
					}
				}
			} catch (Exception ignored) {
			}
		}

		if (data == null || data.isEmpty()) {
			bieuDoCot.setDaiTrucY(0, 5);
			return;
		}

		int max = 0;

		for (var entry : data.entrySet()) {
			LoaiSanPham loai = entry.getKey();
			int soLo = entry.getValue();
			max = Math.max(max, soLo);

			Color mau;
			if (soLo >= 10)
				mau = new Color(220, 53, 69);
			else if (soLo >= 5)
				mau = new Color(255, 193, 7);
			else
				mau = new Color(25, 135, 84);

			bieuDoCot.themDuLieu(new DuLieuBieuDoCot(loai.getTenLoai(), "Cần hủy theo HSD", soLo, mau));
		}

		bieuDoCot.setDaiTrucY(0, max + 3);
	}
	
	private int getIntField(Object obj, String field) {
		try {
			if (obj == null) return 0;
			java.lang.reflect.Field f = obj.getClass().getDeclaredField(field);
			f.setAccessible(true);
			Object v = f.get(obj);
			return v instanceof Number n ? n.intValue() : 0;
		} catch (Exception ex) {
			return 0;
		}
	}

	private double getDoubleField(Object obj, String field) {
		try {
			if (obj == null) return 0d;
			java.lang.reflect.Field f = obj.getClass().getDeclaredField(field);
			f.setAccessible(true);
			Object v = f.get(obj);
			return v instanceof Number n ? n.doubleValue() : 0d;
		} catch (Exception ex) {
			return 0d;
		}
	}
	
	/**
	 * Load dữ liệu thật từ database
	 */
	private void loadRealData() {

		try {
			Object tk = svc.getThongKeHoaDonHomNayCuaNhanVien(maNV);
			int soHoaDon = getIntField(tk, "soHoaDon");
			double tongTienBanHang = getDoubleField(tk, "tongTien");
			txtSoDon.setText(String.valueOf(soHoaDon));
			txtTongTienDaBan.setText(formatter.format(tongTienBanHang) + " ₫");

			// 3. số phiếu trả đã tạo hôm nay
			int soPhieuTra = 0;
			try {
				soPhieuTra = svc.demSoPhieuTraHomNayCuaNhanVien(maNV);
			} catch (Exception ex) {
				soPhieuTra = 0;
			}
			txtSoPhieuTraDaTao.setText(String.valueOf(soPhieuTra));

			// 4. số lô SP đã hết hạn
			int soLoHetHan = 0;
			try {
				java.util.List<?> lots = svc.getLotsExpired();
				if (lots != null) soLoHetHan = lots.size();
			} catch (Exception ex) {
				// ignore
			}
			txtLoSPDaHetHan.setText(String.valueOf(soLoHetHan));

			// 5. số lô sp gần hết hạn
			int soLoToiHanSD = 0;
			try {
				java.util.List<?> lots = svc.getLotsExpiring();
				soLoToiHanSD = lots != null ? lots.size() : 0;
			} catch (Exception ex) {
				soLoToiHanSD = 0;
			}
			txtLoSPSapHetHan.setText(String.valueOf(soLoToiHanSD));

			// 6. số phiếu hủy đã tạo hôm nay
			int soPhieuHuy = 0;
			try {
				soPhieuHuy = svc.demSoPhieuHuyHomNayCuaNhanVien(maNV);
			} catch (Exception ex) {
				soPhieuHuy = 0;
			}
			txtSoPhieuHuyDaTao.setText(String.valueOf(soPhieuHuy));

			

		} catch (Exception e) {
			System.err.println("❌ Lỗi load dữ liệu dashboard: " + e.getMessage());
			e.printStackTrace();
		}
	}



	/**
	 * Refresh lại toàn bộ dữ liệu dashboard Gọi method này khi cần cập nhật dữ liệu
	 * (VD: sau khi thêm hóa đơn mới)
	 */
	public void refreshDashboard() {

		// Load lại dữ liệu mới
		loadRealData();
		loadChartLoaiHSD();
	}

	// --- MAIN VÀ INTERFACE METHODS ---
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Dashboard Tổng Quan NV");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TongQuanNV_GUI());
			frame.setVisible(true);
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}