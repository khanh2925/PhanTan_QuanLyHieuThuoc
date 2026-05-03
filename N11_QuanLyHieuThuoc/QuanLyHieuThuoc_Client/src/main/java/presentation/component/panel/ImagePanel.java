package presentation.component.panel;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image image;

    public ImagePanel(Image image) {
        this.image = image;
        setOpaque(false);
    }

    public Image getImage() {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
