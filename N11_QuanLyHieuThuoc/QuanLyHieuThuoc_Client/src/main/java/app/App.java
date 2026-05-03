/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Class App chứa main để chạy trương trình
 */
package app;

import presentation.common.Loading_GUI;

import javax.swing.*;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Loading_GUI();
		});
	};
}
