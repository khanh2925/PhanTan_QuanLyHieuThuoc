package presentation.trogiup;

/**
 * @author Quốc Khánh cute
 * @version 2.0
 * @since Dec 18, 2025
 *
 * Mô tả: PDF Viewer nhúng trực tiếp vào GUI - sử dụng Apache PDFBox
 * 
 * CÁCH CÀI ĐẶT:
 * 1. Download Apache PDFBox từ: https://pdfbox.apache.org/download.html
 * 2. Cần 2 JAR files:
 *    - pdfbox-3.0.x.jar (hoặc 2.0.x)
 *    - fontbox-3.0.x.jar
 * 3. Copy vào thư mục lib/ của project
 * 4. Add to Build Path trong IDE
 */

import org.apache.pdfbox.Loader; // PDFBox 3.0.x
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // Thêm import này
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Apache PDFBox imports - CẦN CÀI ĐẶT THƯ VIỆN

public class HuongDan_GUI extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5775617548894401089L;
	private PDDocument pdfDocument;
    private PDFRenderer pdfRenderer;
    private int currentPage = 0;
    private int totalPages = 0;
    private float zoomLevel = 1.0f;
    private boolean isQuanLy = false;
    
    private JLabel imageLabel;
    private JLabel pageLabel;
    private JButton btnPrev, btnNext, btnZoomIn, btnZoomOut;
    private JScrollPane scrollPane;

    public HuongDan_GUI(boolean isQuanLy) {
        this.isQuanLy = isQuanLy;
        initComponents();
        loadPDF();
    }
    
    public HuongDan_GUI() {
        this(false); // Mặc định là nhân viên
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TOOLBAR (NORTH) =====
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        toolbar.setBackground(new Color(240, 240, 240));
        
        btnPrev = createToolbarButton("◀ Trang trước", new Color(100, 181, 246));
        btnNext = createToolbarButton("Trang sau ▶", new Color(100, 181, 246));
        btnZoomOut = createToolbarButton("🔍-", new Color(255, 152, 0));
        btnZoomIn = createToolbarButton("🔍+", new Color(255, 152, 0));
        
        pageLabel = new JLabel("Trang 0/0");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pageLabel.setForeground(new Color(33, 150, 243));
        
        toolbar.add(btnPrev);
        toolbar.add(pageLabel);
        toolbar.add(btnNext);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(btnZoomOut);
        toolbar.add(btnZoomIn);
        
        add(toolbar, BorderLayout.NORTH);

        // ===== PDF VIEWER (CENTER) =====
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBackground(Color.DARK_GRAY);
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        scrollPane.setPreferredSize(new Dimension(900, 700));
        
        add(scrollPane, BorderLayout.CENTER);

        // ===== EVENT LISTENERS =====
        btnPrev.addActionListener(e -> previousPage());
        btnNext.addActionListener(e -> nextPage());
        btnZoomIn.addActionListener(e -> zoomIn());
        btnZoomOut.addActionListener(e -> zoomOut());
        
        // Keyboard shortcuts
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "prevPage");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "nextPage");
        inputMap.put(KeyStroke.getKeyStroke("PLUS"), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke("EQUALS"), "zoomIn"); // + key without shift
        inputMap.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
        
        actionMap.put("prevPage", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { previousPage(); }
        });
        actionMap.put("nextPage", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { nextPage(); }
        });
        actionMap.put("zoomIn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoomIn(); }
        });
        actionMap.put("zoomOut", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoomOut(); }
        });
        scrollPane.getVerticalScrollBar().setUnitIncrement(40); 
    }
    
    private JButton createToolbarButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        
        // Hover effect
        Color hoverColor = bgColor.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    
    /**
     * Load PDF file và hiển thị trang đầu tiên (tự động fit vào panel)
     */
    private void loadPDF() {
        try {
            // Xác định tên file dựa vào vai trò
            String fileName = isQuanLy ? "HuongDanSuDung_QuanLy.pdf" : "HuongDanSuDung_NhanVien.pdf";
            
            // Tìm file PDF
            File pdfFile = new File("src\\resources\\hdsn\\" + fileName);
            if (!pdfFile.exists()) {
                pdfFile = new File("resources\\hdsn\\" + fileName);
            }
            if (!pdfFile.exists()) {
                pdfFile = new File(fileName);
            }

            if (!pdfFile.exists()) {
                showError("Không tìm thấy file " + fileName + "!\n" +
                         "Vui lòng đặt file trong:\n" +
                         "- src\\resources\\hdsn\\\n" +
                         "- resources\\hdsn\\\n" +
                         "- Thư mục gốc");
                return;
            }

            // Load PDF document (PDFBox 3.0.x API)
            pdfDocument = Loader.loadPDF(pdfFile);
            pdfRenderer = new PDFRenderer(pdfDocument);
            totalPages = pdfDocument.getNumberOfPages();
            currentPage = 0;
            
            // Tự động tính zoom để fit vào panel
            calculateAutoFitZoom();
            
            // Hiển thị trang đầu tiên
            renderPage();
            updatePageLabel();
            updateButtons();
            
        } catch (Exception e) {
            showError("Không thể load file PDF!\n" +
                     "Lỗi: " + e.getMessage() + "\n\n" +
                     "Vui lòng kiểm tra:\n" +
                     "1. Đã cài đặt Apache PDFBox library chưa\n" +
                     "2. File PDF có hợp lệ không\n" +
                     "3. File có bị corrupt không");
            e.printStackTrace();
        }
    }
    
    /**
     * Tính toán zoom level để PDF vừa khít với panel (mặc định zoom to hơn 1 chút)
     */
    @SuppressWarnings("unused")
	private void calculateAutoFitZoom() {
        try {
            if (pdfDocument == null || pdfDocument.getNumberOfPages() == 0) return;
            
            // Lấy kích thước trang PDF đầu tiên (in points: 1 point = 1/72 inch)
            org.apache.pdfbox.pdmodel.PDPage firstPage = pdfDocument.getPage(0);
            float pageWidth = firstPage.getMediaBox().getWidth();
            float pageHeight = firstPage.getMediaBox().getHeight();
            
            // Lấy kích thước viewport của scrollPane (trừ scrollbar)
            int viewportWidth = scrollPane.getViewport().getWidth() - 20; // Trừ margin
            int viewportHeight = scrollPane.getViewport().getHeight() - 20;
            
            // Nếu viewport chưa có kích thước (lần đầu init), dùng preferred size
            if (viewportWidth <= 20) viewportWidth = 880;
            if (viewportHeight <= 20) viewportHeight = 680;
            
            // Tính zoom để fit width (ưu tiên fit chiều rộng)
            float zoomByWidth = (float) viewportWidth / pageWidth;
            
            // Tăng zoom thêm 1.0f (4 lần zoom in) + rộng thêm 10%
            zoomByWidth = zoomByWidth + 1.0f; // +4 lần zoom
            zoomByWidth = zoomByWidth * 1.11f; // +10% rộng hơn
            
            // Giới hạn zoom trong khoảng 0.5 - 3.0 để tránh quá nhỏ/lớn
            zoomLevel = Math.max(0.5f, Math.min(3.0f, zoomByWidth));
            
        } catch (Exception e) {
            zoomLevel = 2.0f; // Fallback về 200% nếu có lỗi
            e.printStackTrace();
        }
    }
    
    /**
     * Render trang PDF hiện tại thành image và hiển thị
     */
    private void renderPage() {
        if (pdfRenderer == null) return;
        
        try {
            // Render page với DPI tùy theo zoom level
            // DPI = 72 * zoomLevel (72 DPI là standard, 150-300 cho chất lượng cao)
            float dpi = 72f * zoomLevel;
            BufferedImage image = pdfRenderer.renderImageWithDPI(currentPage, dpi);
            
            // Hiển thị image
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
            
            // Scroll về đầu trang
            scrollPane.getVerticalScrollBar().setValue(0);
            scrollPane.getHorizontalScrollBar().setValue(0);
            
        } catch (IOException e) {
            showError("Không thể render trang PDF!\nLỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Chuyển sang trang trước
     */
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage();
            updatePageLabel();
            updateButtons();
        }
    }
    
    /**
     * Chuyển sang trang sau
     */
    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderPage();
            updatePageLabel();
            updateButtons();
        }
    }
    
    /**
     * Phóng to (zoom in)
     */
    private void zoomIn() {
        if (zoomLevel < 3.0f) {
            zoomLevel += 0.25f;
            renderPage();
        }
    }
    
    /**
     * Thu nhỏ (zoom out)
     */
    private void zoomOut() {
        if (zoomLevel > 0.5f) {
            zoomLevel -= 0.25f;
            renderPage();
        }
    }
    
    /**
     * Cập nhật label hiển thị số trang
     */
    private void updatePageLabel() {
        pageLabel.setText(String.format("Trang %d/%d (%.0f%%)", 
                                       currentPage + 1, 
                                       totalPages,
                                       zoomLevel * 100));
    }
    
    /**
     * Cập nhật trạng thái buttons
     */
    private void updateButtons() {
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
        btnZoomOut.setEnabled(zoomLevel > 0.5f);
        btnZoomIn.setEnabled(zoomLevel < 3.0f);
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        
        // Hiển thị placeholder nếu không load được PDF
        imageLabel.setText("<html><center><h1>❌ Không thể hiển thị PDF</h1>" +
                          "<p>" + message.replace("\n", "<br>") + "</p></center></html>");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        imageLabel.setForeground(Color.RED);
    }
    
    /**
     * Cleanup khi đóng panel
     */
    public void cleanup() {
        try {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
