package presentation.common;

import network.ClientSocket;
import network.CommandType;
import network.Request;
import network.Response;
import presentation.DangNhap_GUI;
import presentation.component.panel.ImagePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * Màn hình Loading khi khởi động ứng dụng
 * Hiển thị tiến trình khởi tạo và kết nối database
 * 
 * @author N11_QuanLyHieuThuoc
 * @version 2.0
 */
public class Loading_GUI extends JWindow {

    private static final long serialVersionUID = 1L;
    
    // Components
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblPercentage;
    
    // Cờ để kiểm soát việc đóng cửa sổ
    private boolean loadingComplete = false;

    public Loading_GUI() {
        buildUI();
        startLoading();
    }

    /**
     * Xây dựng giao diện Loading
     */
    private void buildUI() {
        setSize(850, 610);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        // Image Panel (Nền)
        URL bgImageUrl = getClass().getResource("/resources/images/Loading.png");
        ImagePanel imagePanel; 

        if (bgImageUrl != null) {
            ImageIcon bgImageIcon = new ImageIcon(bgImageUrl);
            imagePanel = new ImagePanel(bgImageIcon.getImage());
        } else {
            // Fallback nếu không tìm thấy ảnh
            imagePanel = new ImagePanel(null);
            imagePanel.setLayout(new GridBagLayout());
            imagePanel.setBackground(new Color(0xE3F2F5));
            imagePanel.setOpaque(true);
            imagePanel.add(new JLabel("Không tìm thấy ảnh nền..."));
            System.err.println("⚠️ Không tìm thấy ảnh nền: /resources/images/Loading.png");
        }
        
        imagePanel.setLayout(new BorderLayout());
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        
        // Panel tiến trình
        JPanel progressPanel = createProgressPanel();
        imagePanel.add(progressPanel, BorderLayout.SOUTH);
    }

    /**
     * Tạo panel chứa thanh tiến trình và label trạng thái
     */
    private JPanel createProgressPanel() {
        JPanel progressPanel = new JPanel(new BorderLayout(0, 8));
        progressPanel.setOpaque(false); 
        progressPanel.setPreferredSize(new Dimension(0, 85));
        progressPanel.setBorder(new EmptyBorder(10, 30, 20, 30));
        
        // Panel chứa label trạng thái và phần trăm
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        
        // Label trạng thái
        lblStatus = new JLabel("Đang khởi tạo ứng dụng...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblStatus.setForeground(new Color(33, 33, 33));
        statusPanel.add(lblStatus, BorderLayout.WEST);
        
        // Label phần trăm
        lblPercentage = new JLabel("0%");
        lblPercentage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPercentage.setForeground(new Color(0, 150, 136));
        statusPanel.add(lblPercentage, BorderLayout.EAST);
        
        progressPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Thanh tiến trình
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false); // Tắt text trên thanh, dùng label riêng
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setForeground(new Color(0, 150, 136)); 
        progressBar.setBackground(new Color(230, 230, 230));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        return progressPanel;
    }

    /**
     * Bắt đầu quá trình loading
     */
    private void startLoading() {
        SwingWorker<Boolean, LoadingProgress> worker = new SwingWorker<Boolean, LoadingProgress>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Bước 1: Khởi tạo cấu hình (0-10%)
                    publish(new LoadingProgress(5, "Đang khởi tạo ứng dụng..."));
                    Thread.sleep(300);
                    
                    // Bước 2: Kết nối tới Server qua Socket và yêu cầu dữ liệu khởi tạo
                    publish(new LoadingProgress(10, "Kết nối đến Server..."));
                    try (ClientSocket cs = new ClientSocket("localhost", 9090, 5000)) {
                        publish(new LoadingProgress(12, "Yêu cầu danh sách nhân viên..."));
                        Response rNv = cs.sendRequest(new Request(CommandType.NHANVIEN_LAY_TAT_CA, null));
                        int countNV = 0;
                        if (rNv != null && rNv.isSuccess() && rNv.getData() instanceof List) {
                            countNV = ((List<?>) rNv.getData()).size();
                        }
                        publish(new LoadingProgress(28, "✓ Đã tải " + countNV + " nhân viên"));
                        Thread.sleep(100);

                        publish(new LoadingProgress(30, "Yêu cầu danh sách khách hàng..."));
                        Response rKh = cs.sendRequest(new Request(CommandType.KHACHHANG_LAY_TAT_CA, null));
                        int countKH = 0;
                        if (rKh != null && rKh.isSuccess() && rKh.getData() instanceof List) {
                            countKH = ((List<?>) rKh.getData()).size();
                        }
                        publish(new LoadingProgress(50, "✓ Đã tải " + countKH + " khách hàng"));
                        Thread.sleep(100);

                        publish(new LoadingProgress(52, "Yêu cầu danh sách nhà cung cấp..."));
                        // If server supports a command for suppliers, replace below. Fallback to 0.
                        int countNCC = 0;
                        publish(new LoadingProgress(65, "✓ Đã tải " + countNCC + " nhà cung cấp"));
                        Thread.sleep(100);

                        publish(new LoadingProgress(67, "Yêu cầu danh sách sản phẩm..."));
                        Response rSp = cs.sendRequest(new Request(CommandType.SANPHAM_LAY_TAT_CA, null));
                        int countSP = 0;
                        if (rSp != null && rSp.isSuccess() && rSp.getData() instanceof List) {
                            countSP = ((List<?>) rSp.getData()).size();
                        }
                        publish(new LoadingProgress(95, "✓ Đã tải " + countSP + " sản phẩm"));
                        Thread.sleep(200);
                    }
                    
                    // Note: Data loading moved to server via socket above.
                    
                    // Bước 8: Hoàn tất (95-100%)
                    publish(new LoadingProgress(97, "Đang hoàn tất khởi động..."));
                    Thread.sleep(300);
                    publish(new LoadingProgress(100, "Hoàn tất! Chào mừng bạn đến với hệ thống."));
                    
                    return true;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    publish(new LoadingProgress(0, "❌ Lỗi: " + e.getMessage()));
                    return false;
                }
            }

            @Override
            protected void process(List<LoadingProgress> chunks) {
                LoadingProgress lastProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(lastProgress.getValue());
                lblStatus.setText(lastProgress.getMessage());
                lblPercentage.setText(lastProgress.getValue() + "%");
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    loadingComplete = true;
                    
                    if (success) {
                        // Đợi một chút để người dùng thấy 100%
                        Thread.sleep(500);
                        
                        // Đóng loading và mở màn hình đăng nhập
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            new DangNhap_GUI().setVisible(true);
                        });
                    } else {
                        // Nếu có lỗi, hiển thị thông báo và đóng ứng dụng
                        SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(
                                Loading_GUI.this,
                                "Không thể khởi tạo ứng dụng!\nVui lòng kiểm tra kết nối database.",
                                "Lỗi khởi tạo",
                                javax.swing.JOptionPane.ERROR_MESSAGE
                            );
                            System.exit(0);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        setVisible(true);
    }

    /**
     * Inner class để lưu trữ thông tin tiến trình
     */
    private static class LoadingProgress {
        private final int value;
        private final String message;
        
        public LoadingProgress(int value, String message) {
            this.value = value;
            this.message = message;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getMessage() {
            return message;
        }
    }

    /**
     * Kiểm tra xem loading đã hoàn tất chưa
     */
    public boolean isLoadingComplete() {
        return loadingComplete;
    }

    /**
     * Main method để test Loading GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel
//                javax.swing.UIManager.setLookAndFeel(
//                    javax.swing.UIManager.getSystemLookAndFeelClassName()
//                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Loading_GUI();
        });
    }
}
