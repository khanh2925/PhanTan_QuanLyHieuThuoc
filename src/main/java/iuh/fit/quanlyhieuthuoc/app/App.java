/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Class App chứa main để chạy trương trình
 */
package iuh.fit.quanlyhieuthuoc.app;

import javax.swing.SwingUtilities;

import iuh.fit.quanlyhieuthuoc.presentation.common.Loading_GUI;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Loading_GUI();
		});
	};
}
